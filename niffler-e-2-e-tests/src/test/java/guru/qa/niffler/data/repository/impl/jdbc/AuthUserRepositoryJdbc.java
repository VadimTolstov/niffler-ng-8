package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.ex.DataAccessException;
import guru.qa.niffler.model.Authority;
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
public class AuthUserRepositoryJdbc implements AuthUserRepository {
    private final static Config CFG = Config.getInstance();

    @Override
    public @Nonnull AuthUserEntity create(@Nonnull AuthUserEntity user) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
                        "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)"
             )) {
            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getPassword());
            userPs.setBoolean(3, user.getEnabled());
            userPs.setBoolean(4, user.getAccountNonExpired());
            userPs.setBoolean(5, user.getAccountNonLocked());
            userPs.setBoolean(6, user.getCredentialsNonExpired());

            userPs.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new DataAccessException("Can't find id in ResultSet");
                }
            }
            user.setId(generatedKey);

            for (AuthorityEntity a : user.getAuthorities()) {
                authorityPs.setObject(1, generatedKey);
                authorityPs.setString(2, a.getAuthority().name());
                authorityPs.addBatch();
            }
            authorityPs.executeBatch();
            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при создании пользователя", e);
        }
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(@Nonnull UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" u JOIN authority a ON u.id = a.user_id WHERE u.id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            List<AuthorityEntity> authorityEntities = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity user = null;
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }

                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUserId(user);
                    ae.setId(rs.getObject("a.id", UUID.class));
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorityEntities.add(ae);
                }
                if (user == null) {
                    return Optional.empty();
                }
                user.setAuthorities(authorityEntities);
                return Optional.ofNullable(user);

            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске пользователя по id = " + id, e);
        }
    }

    @Override//todo
    public @Nonnull Optional<AuthUserEntity> findUserByName(@Nonnull String username) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
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
                            rs.getBoolean("credentials_non_expired"),
                            new ArrayList<>()
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

    @Override//todo
    public @Nonnull List<AuthUserEntity> findAll() {
        List<AuthUserEntity> authUserEntityList = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\""
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    authUserEntityList.add(AuthUserEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
            }
            return authUserEntityList;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при получении данных с таблицы user ", e);
        }
    }

    @Override//todo
    public @Nonnull AuthUserEntity update(@Nonnull AuthUserEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При обновлении Пользователя  id не должен быть null");
        }
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
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
        try (PreparedStatement psAuthority = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM authority WHERE user_id = ?");
             PreparedStatement psUser = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "DELETE FROM \"user\" WHERE id = ?"
             )) {
            psAuthority.setObject(1, user.getId());

            int affectedRowsAuthority = psAuthority.executeUpdate();
            if (affectedRowsAuthority == 0) {
                throw new DataAccessException("Пользователь с id " + user.getId() + " не найдена в таблице authority");
            }

            psUser.setObject(1, user.getId());

            int affectedRowsUser = psUser.executeUpdate();
            if (affectedRowsUser == 0) {
                throw new DataAccessException("Пользователь с id " + user.getId() + " не найдена в таблице user");
            }
        } catch (SQLException e) {
            log.error("Ошибка при удалении пользователя с id {}", user.getId(), e);
            throw new DataAccessException("Ошибка при удалении пользователя с id " + user.getId(), e);
        }
    }
}