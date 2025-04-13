package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {

    public AuthUserEntity creat(AuthUserEntity user);

    public Optional<AuthUserEntity> findUserById(UUID id);

    public Optional<AuthUserEntity> findUserByName(String name);

    public AuthUserEntity update(AuthUserEntity user);

    public void delete(AuthUserEntity user);
}
