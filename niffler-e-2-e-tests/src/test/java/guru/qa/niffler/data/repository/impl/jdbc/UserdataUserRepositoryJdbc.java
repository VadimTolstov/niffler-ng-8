package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.UdUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.ex.DataAccessException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static guru.qa.niffler.data.tpl.Connections.holder;

@Slf4j
public class UserdataUserRepositoryJdbc implements UserdataUserRepository {
    private final UdUserDao udUserDao = new UdUserDaoJdbc();

    @Override
    public @Nonnull UserEntity create(@Nonnull UserEntity user) {
        return udUserDao.createUser(user);
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(@Nonnull UUID id) {
        return udUserDao.findById(id);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return udUserDao.findByUsername(username);
    }

    @Override
    public UserEntity update(UserEntity user) {
        return udUserDao.update(user);
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        udUserDao.sendInvitation(requester, addressee);
    }


    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        udUserDao.addFriend(requester, addressee);
    }

    @Override
    public void remove(@Nonnull UserEntity user) {
        udUserDao.delete(user);
    }
}
