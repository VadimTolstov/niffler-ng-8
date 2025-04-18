package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {

    public AuthUserEntity create(AuthUserEntity user);

    public Optional<AuthUserEntity> findById(UUID id);

    public Optional<AuthUserEntity> findUserByName(String name);

    List<AuthUserEntity> findAll();

    public AuthUserEntity update(AuthUserEntity user);

    public void delete(AuthUserEntity user);
}
