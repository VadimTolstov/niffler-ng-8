package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.ex.DataAccessException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class AuthUserDaoJdbc implements AuthUserDao {

    private final Connection connection;

    public AuthUserDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public @Nonnull AuthUserEntity creat(@Nonnull AuthUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());

            ps.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new DataAccessException("Can't find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при создании пользователя", e);
        }
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findUserById(@Nonnull UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity aue = new AuthUserEntity(
                            rs.getObject("id", UUID.class),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getBoolean("enabled"),
                            rs.getBoolean("account_non_expired"),
                            rs.getBoolean("account_non_locked"),
                            rs.getBoolean("credentials_non_expired")
                    );
                    return Optional.ofNullable(aue);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске пользователя по id = " + id, e);
        }
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findUserByName(@Nonnull String username) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity aue = new AuthUserEntity(
                            rs.getObject("id", UUID.class),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getBoolean("enabled"),
                            rs.getBoolean("account_non_expired"),
                            rs.getBoolean("account_non_locked"),
                            rs.getBoolean("credentials_non_expired")
                    );
                    return Optional.ofNullable(aue);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске пользователя по username = " + username, e);
        }
    }

    @Override
    public @Nonnull AuthUserEntity update(@Nonnull AuthUserEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При обновлении Пользователя  id не должен быть null");
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE \"user\" SET username = ?, password = ?, enabled = ?, account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ?" +
                        "WHERE id = ?"
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            ps.setObject(7, user.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Пользователь с id " + user.getId() + " не найден");
            }
            return user;
        } catch (SQLException e) {
            log.error("Ошибка при обновлении пользователя с id {}", user.getId(), e);
            throw new DataAccessException("Ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public void delete(@Nonnull AuthUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM \"user\" where id = ?"
        )) {
            ps.setObject(1, user.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Пользователь с id " + user.getId() + " не найдена");
            }
        } catch (SQLException e) {
            log.error("Ошибка при удалении пользователя с id {}", user.getId(), e);
            throw new DataAccessException("Ошибка при удалении пользователя с id " + user.getId(), e);
        }
    }
}