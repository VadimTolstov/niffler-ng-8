package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthAuthorityRepository {

    void create(AuthorityEntity... authority);

    Optional<AuthorityEntity> findById(UUID id);

    List<AuthorityEntity> findAll();

    AuthorityEntity update(AuthorityEntity user);

    void delete(AuthorityEntity user);
}
