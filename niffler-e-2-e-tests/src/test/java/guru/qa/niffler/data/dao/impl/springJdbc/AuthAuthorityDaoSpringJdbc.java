package guru.qa.niffler.data.dao.impl.springJdbc;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthorityEntityRowMapper;
import guru.qa.niffler.ex.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

    private final DataSource dataSource;

    public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void create(@Nonnull AuthorityEntity... authority) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUserId().getId());
                        ps.setString(2, authority[i].getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
    }

    @Override
    public @Nonnull Optional<AuthorityEntity> findById(@Nonnull UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<AuthorityEntity> result = jdbcTemplate.query(
                "SELECT * FROM \"authority\" WHERE id = ?",
                AuthorityEntityRowMapper.instance,
                id
        );
        return result.isEmpty() ? Optional.empty() : Optional.ofNullable(result.getFirst());
    }

    @Override
    public @Nonnull List<AuthorityEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
                "SELECT * FROM \"authority\"",
                AuthorityEntityRowMapper.instance
        );
    }

    @Override
    public @Nonnull AuthorityEntity update(@Nonnull AuthorityEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При обновлении данных в таблице authority в AuthorityEntity id не должен быть null");
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int updated = jdbcTemplate.update(
                "UPDATE \"authority\" SET user_id = ?, authority = ? WHERE id = ?",
                user.getUserId().getId(),
                user.getAuthority().name(),
                user.getId()
        );
        if (updated == 0) {
            throw new DataAccessException("В таблице authority данные  по  id " + user.getId() + " не найдена для обновления");
        }
        return user;
    }

    @Override
    public void delete(@Nonnull AuthorityEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При удалении данных в таблице authority в AuthorityEntity id не должен быть null");
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int deleted = jdbcTemplate.update(
                "DELETE FROM \"authority\" WHERE id = ?",
                user.getId()
        );
        if (deleted == 0) {
            throw new DataAccessException("В таблице authority данные по id " + user.getId() + " не найдена для удаления");
        }
    }
}