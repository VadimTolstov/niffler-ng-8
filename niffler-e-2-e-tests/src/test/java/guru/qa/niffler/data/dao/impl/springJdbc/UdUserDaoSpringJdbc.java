package guru.qa.niffler.data.dao.impl.springJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.ex.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.dao.impl.springJdbc.utils.DaoUtils.getGeneratedId;

public class UdUserDaoSpringJdbc implements UdUserDao {
    private final static Config CFG = Config.getInstance();

    @Override
    public @Nonnull UserEntity createUser(@Nonnull UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            return ps;
        }, kh);

        final UUID generatedKey = getGeneratedId(kh, "id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(@Nonnull UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        UdUserEntityRowMapper.instance,
                        id
                )
        );
    }


    @Override
    public @Nonnull Optional<UserEntity> findByUsername(@Nonnull String username) {
        List<UserEntity> result = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl())).query(
                "SELECT * FROM \"user\" WHERE username = ?",
                UdUserEntityRowMapper.instance,
                username
        );
        return result.isEmpty() ? Optional.empty() : Optional.ofNullable(result.getFirst());
    }

    @Override
    public @Nonnull List<UserEntity> findAll() {
        return new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl())).query(
                "SELECT * FROM \"user\"",
                UdUserEntityRowMapper.instance
        );
    }

    @Override
    public @Nonnull UserEntity update(@Nonnull UserEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При обновлении данных в таблице user в UserEntity id не должен быть null");
        }
        int updated = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl())).update(
                "UPDATE \"user\" SET username = ?, currency = ?, firstname = ?, " +
                        "surname = ?, photo = ?, photo_small = ?, full_name = ? " +
                        "WHERE id = ?",
                user.getUsername(),
                user.getCurrency().name(),
                user.getFirstname(),
                user.getSurname(),
                user.getPhoto(),
                user.getPhotoSmall(),
                user.getFullname(),
                user.getId()
        );
        if (updated == 0) {
            throw new DataAccessException("При обновлении данных в таблице user данные с id " + user.getId() + " не найдена для обновления");
        }
        return user;
    }

    @Override
    public void delete(@Nonnull UserEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При удалении данных в таблице user в UserEntity id не должен быть null");
        }
        int deleted = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl())).update(
                "DELETE FROM \"user\" WHERE id = ?",
                user.getId()
        );
        if (deleted == 0) {
            throw new DataAccessException("При удалении данных в таблице user данные c id " + user.getId() + " не найдена для удаления");
        }
    }
}