package guru.qa.niffler.service.repository.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.ex.DataAccessException;
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class UsersDbRepositoryHibernateClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public @Nonnull UserJson creatUser(@Nonnull String username, @Nonnull String password) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUserEntity = authUserEntity(username, password);

                    //создаем нового пользователя и получаем его id
                    authUserEntity.setId(authUserRepository.create(authUserEntity).getId());

                    return UserJson.fromEntity(
                            userdataUserRepository.create(userEntity(username)
                            )
                    );
                }
        );
    }

    public void addIncomeInvitation(UserJson targetUser, int count) {
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
                            userdataUserRepository.addIncomeInvitation(targetEntity, adressee);
                            return null;
                        }
                );
            }
        }
    }

    public void addOutcomeInvitation(UserJson targetUser, int count) {
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
                            userdataUserRepository.addOutcomeInvitation(targetEntity, adressee);
                            return null;
                        }
                );
            }
        }
    }

    public void addFriend(UserJson targetUser, int count) {
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
