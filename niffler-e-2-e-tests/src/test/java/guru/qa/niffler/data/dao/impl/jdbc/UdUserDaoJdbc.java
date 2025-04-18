package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import guru.qa.niffler.ex.DataAccessException;
import guru.qa.niffler.model.CurrencyValues;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@Slf4j
public class UdUserDaoJdbc implements UdUserDao {
    private final static Config CFG = Config.getInstance();

    @Override
    public @Nonnull UserEntity createUser(@Nonnull UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)"
                , Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при создании пользователя", e);
        }
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(@Nonnull UUID id) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity ue = new UserEntity(
                            rs.getObject("id", UUID.class),
                            rs.getString("username"),
                            CurrencyValues.valueOf(rs.getString("currency")),
                            rs.getString("firstname"),
                            rs.getString("surname"),
                            rs.getString("full_name"),
                            rs.getBytes("photo"),
                            rs.getBytes("photo_small")
                    );
                    return Optional.ofNullable(ue);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске пользователя по id = " + id, e);
        }
    }

    @Override
    public @Nonnull Optional<UserEntity> findByUsername(@Nonnull String username) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setObject(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity ue = new UserEntity(
                            rs.getObject("id", UUID.class),
                            rs.getString("username"),
                            CurrencyValues.valueOf(rs.getString("currency")),
                            rs.getString("firstname"),
                            rs.getString("surname"),
                            rs.getString("full_name"),
                            rs.getBytes("photo"),
                            rs.getBytes("photo_small")
                    );
                    return Optional.ofNullable(ue);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске пользователя по username = " + username, e);
        }
    }

    @Override
    public @Nonnull List<UserEntity> findAll() {
        List<UserEntity> userEntityList = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\""
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    userEntityList.add(UdUserEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
            }
            return userEntityList;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при получении данных с таблицы user ", e);
        }
    }

    @Override
    public @Nonnull UserEntity update(@Nonnull UserEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При обновлении User в UserEntity id не должен быть null");
        }
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" SET username = ?, currency = ?, firstname = ?, " +
                        "surname = ?, photo = ?, photo_small = ?, full_name = ? " +
                        "WHERE id = ?"
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            ps.setObject(8, user.getId());

            // Проверяем количество обновленных строк
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Пользователь с id " + user.getId() + " не найдена");
            }
            return user;
        } catch (SQLException e) {
            log.error("Ошибка при обновлении пользователя с id {}", user.getId(), e);
            throw new DataAccessException("Ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public void delete(@Nonnull UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());

            // Проверяем количество обновленных строк
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
