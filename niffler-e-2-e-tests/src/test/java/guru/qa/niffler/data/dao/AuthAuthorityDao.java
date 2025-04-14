package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthAuthorityDao {

    public List<AuthorityEntity> creat(AuthorityEntity... users);

    public Optional<AuthorityEntity> findUserById(UUID id);

    public AuthorityEntity update(AuthorityEntity user);

    public void delete(AuthorityEntity user);
}
