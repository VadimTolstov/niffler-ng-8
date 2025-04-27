package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.extractor.UserdataUserResultSetExtractor;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.ex.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.dao.impl.springJdbc.utils.DaoUtils.getGeneratedId;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {
    private final static Config CFG = Config.getInstance();

    @Override
    public @Nonnull UserEntity create(@Nonnull UserEntity user) {
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
                jdbcTemplate.query(
                        "SELECT DISTINCT " +
                                " u.*," +
                                " f.requester_id AS requester_id," +
                                " f.addressee_id AS addressee_id," +
                                " f.status AS friendship_status," +
                                " f.created_date AS created_date" +
                                " FROM \"user\" u LEFT JOIN \"friendship\" " +
                                " ON u.id = f.requester_id OR u.id = f.addressee_id " +
                                " WHERE  u.id = ?",
                        UserdataUserResultSetExtractor.INSTANCE,
                        id
                )
        );
    }

    @Override
    public void addIncomeInvitation(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        extractedFriend(requester, addressee, FriendshipStatus.PENDING.name());
    }

    @Override
    public void addOutcomeInvitation(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        addIncomeInvitation(addressee, requester);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        extractedFriend(requester, addressee, FriendshipStatus.ACCEPTED.name());
        extractedFriend(addressee, requester, FriendshipStatus.ACCEPTED.name());
    }

    private void extractedFriend(@Nonnull UserEntity requester, @Nonnull UserEntity addressee, @Nonnull String friendshipStatus) {
        if (requester.getId() == null || addressee.getId() == null) {
            throw new DataAccessException("При добавлении дружбы id не должен быть null ");
        }
        new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl())).update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO friendship (requester_id, addressee_id, status, created_date)" +
                            "VALUES (?,?,?,?)"
            );
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, friendshipStatus);
            ps.setObject(4, new java.sql.Date(System.currentTimeMillis()));
            return ps;
        });
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