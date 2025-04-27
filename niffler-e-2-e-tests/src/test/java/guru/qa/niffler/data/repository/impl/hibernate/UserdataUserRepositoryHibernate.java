package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.userdataJdbcUrl());

    @Override
    public @Nonnull UserEntity create(@Nonnull UserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(@Nonnull UUID id) {
        return Optional.ofNullable(
                entityManager.find(UserEntity.class, id)
        );
    }

    @Override
    public void addIncomeInvitation(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        entityManager.joinTransaction();
        addressee.addFriends(FriendshipStatus.PENDING, requester);
    }

    @Override
    public void addOutcomeInvitation(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.PENDING, addressee);
    }

    @Override
    public void addFriend(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.ACCEPTED,addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED,requester);
    }

    @Override
    public void delete(@Nonnull UserEntity user) {
        entityManager.joinTransaction();
        entityManager.remove(user);
    }
}