package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.AuthUserDaoJdbc;
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

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();

    @Override
    public @Nonnull AuthUserEntity create(@Nonnull AuthUserEntity user) {
        AuthUserEntity createUser = authUserDao.create(user);
        for (AuthorityEntity authority : createUser.getAuthorities()) {
            authAuthorityDao.create(authority);
        }
        return createUser;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        for (AuthorityEntity authority : user.getAuthorities()) {
            authAuthorityDao.update(authority);
        }
        return authUserDao.update(user);
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(@Nonnull UUID id) {
        List<AuthorityEntity> byListAe = authAuthorityDao.findByUserId(id);
        Optional<AuthUserEntity> authUser = authUserDao.findById(id);
        AuthUserEntity user;
        if (authUser.isPresent()) {
            user = authUser.get();
            user.setAuthorities(byListAe);
            return Optional.ofNullable(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        AuthUserEntity user;
        Optional<AuthUserEntity> authUser = authUserDao.findUserByName(username);
        if (authUser.isPresent()) {
            user = authUser.get();
            user.setAuthorities(authAuthorityDao.findByUserId(user.getId()));
            return Optional.ofNullable(user);
        }
        return Optional.empty();
    }

    @Override
    public void remove(@Nonnull AuthUserEntity user) {
        for (AuthorityEntity authority : user.getAuthorities()) {
            authAuthorityDao.delete(authority);
        }
        authUserDao.delete(user);
    }
}