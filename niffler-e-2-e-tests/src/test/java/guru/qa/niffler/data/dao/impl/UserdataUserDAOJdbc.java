package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.UserdataUserDAO;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.Optional;
import java.util.UUID;

public class UserdataUserDAOJdbc implements UserdataUserDAO {
    @Override
    public UserEntity createUser(UserEntity user) {
        return null;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return null;
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return null;
    }

    @Override
    public void delete(UserEntity user) {

    }
}
