package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.ex.DataAccessException;
import guru.qa.niffler.model.Authority;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public @Nonnull List<AuthorityEntity> create(@Nonnull AuthorityEntity... users) {
        List<AuthorityEntity> usersList = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            for (AuthorityEntity user : users) {
                ps.setObject(1, user.getUserId().getId());
                ps.setString(2, user.getAuthority().name());
                ps.addBatch();
                usersList.add(user);
            }
            ps.executeBatch();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                for (int i = 0; i < usersList.size(); i++) {
                    if (rs.next()) {
                        UUID generatedKey = rs.getObject("id", UUID.class);
                        usersList.get(i).setId(generatedKey);
                    } else {
                        throw new DataAccessException("Can't find id in ResultSet");
                    }
                }
            }
            return usersList;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при добавлении прав пользователю", e);
        }
    }

    @Override
    public @Nonnull Optional<AuthorityEntity> findById(@Nonnull UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"authority\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UUID userId = rs.getObject("user_id", UUID.class);
                    Optional<AuthUserEntity> userEntity = new AuthUserDaoJdbc(connection).findById(userId);

                    if (userEntity.isEmpty()) {
                        log.warn("Пользователь с id {} не найден в таблице authority по user_id {}", id, userId);
                        return Optional.empty();
                    }

                    AuthorityEntity ae = new AuthorityEntity(
                            rs.getObject("id", UUID.class),
                            userEntity.get(),
                            Authority.valueOf(rs.getString("authority"))
                    );
                    return Optional.ofNullable(ae);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске данных в таблице authority  по id = " + id, e);
        }
    }

    @Override
    public @Nonnull List<AuthorityEntity> findAll() {
        return List.of();
    }

    @Override
    public @Nonnull AuthorityEntity update(@Nonnull AuthorityEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При данных в таблице authority id не должен быть null");
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE \"authority\" SET user_id = ?, authority = ? WHERE id = ?"
        )) {
            ps.setObject(1, user.getUserId().getId());
            ps.setString(2, user.getAuthority().name());
            ps.setObject(3, user.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Пользователь с id " + user.getId() + " не найдена");
            }

            return user;
        } catch (SQLException e) {
            log.error("Ошибка при обновлении данных в таблице authority с id {}", user.getId(), e);
            throw new DataAccessException("Ошибка при обновлении данных в таблице authority", e);
        }
    }

    @Override
    public void delete(@Nonnull AuthorityEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM \"authority\" WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Запись в таблице authority с id " + user.getId() + " не найдена");
            }
        } catch (SQLException e) {
            log.error("Ошибка при удалении записи с таблице authority с id {}", user.getId(), e);
            throw new DataAccessException("Ошибка при удалении траты с id " + user.getId(), e);
        }
    }
}
