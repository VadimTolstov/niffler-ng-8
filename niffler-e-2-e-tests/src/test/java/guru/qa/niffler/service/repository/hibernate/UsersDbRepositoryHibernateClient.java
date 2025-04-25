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
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class UsersDbRepositoryHibernateClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public @Nonnull UserJson creatUser(@Nonnull UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUserEntity = authUserEntity(user);

                    //создаем нового пользователя и получаем его id
                    authUserEntity.setId(authUserRepository.create(authUserEntity).getId());

                    return UserJson.fromEntity(
                            userdataUserRepository.create(UserEntity.fromJson(user)
                            )
                    );
                }
        );
    }

    private @Nonnull AuthUserEntity authUserEntity(@Nonnull UserJson user) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(user.username());
        authUserEntity.setPassword(pe.encode("12345"));
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
