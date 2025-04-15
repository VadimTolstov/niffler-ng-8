package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthAuthorityDao {

    public List<AuthorityEntity> create(AuthorityEntity... authority);

    public Optional<AuthorityEntity> findById(UUID id);

    List<AuthorityEntity> findAll();

    public AuthorityEntity update(AuthorityEntity user);

    public void delete(AuthorityEntity user);
}
