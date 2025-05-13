package guru.qa.niffler.service.imp;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Override
    public @Nonnull UserJson createUser(@Nonnull String username, @Nonnull String password) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUserEntity = authUserEntity(username, password);

                    //создаем нового пользователя и получаем его id
                    authUserEntity.setId(authUserRepository.create(authUserEntity).getId());

                    return UserJson.fromEntity(
                            userdataUserRepository.create(
                                    userEntity(username)
                            ), FriendshipStatus.PENDING
                    );
                }
        ), "Transaction result cannot be null");
    }

    @Override
    public void createIncomeInvitations(@Nonnull UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = RandomDataUtils.randomUsername();
                            AuthUserEntity authUserEntity = authUserEntity(username, "12345");
                            authUserRepository.create(authUserEntity);
                            UserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.sendInvitation(targetEntity, adressee);
                            return null;
                        }
                );
            }
        }
    }

    @Override
    public void createOutcomeInvitations(@Nonnull UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = RandomDataUtils.randomUsername();
                            AuthUserEntity authUserEntity = authUserEntity(username, "12345");
                            authUserRepository.create(authUserEntity);
                            UserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.sendInvitation(adressee, targetEntity);
                            return null;
                        }
                );
            }
        }
    }

    @Override
    public void createFriends(@Nonnull UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = RandomDataUtils.randomUsername();
                            AuthUserEntity authUserEntity = authUserEntity(username, "12345");
                            authUserRepository.create(authUserEntity);
                            UserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.addFriend(targetEntity, adressee);
                            return null;
                        }
                );
            }
        }
    }

    private @Nonnull UserEntity userEntity(@Nonnull String username) {
        return new UserEntity()
                .setUsername(username)
                .setCurrency(CurrencyValues.RUB);
    }

    private @Nonnull AuthUserEntity authUserEntity(@Nonnull String username, @Nonnull String password) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(username);
        authUserEntity.setPassword(pe.encode(password));
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);
        authUserEntity.setAuthorities(
                Arrays.stream(Authority.values())
                        .map(value -> {
                                    AuthorityEntity ae = new AuthorityEntity();
                                    ae.setUser(authUserEntity);
                                    ae.setAuthority(value);
                                    return ae;
                                }
                        ).toList()
        );
        return authUserEntity;
    }

}
