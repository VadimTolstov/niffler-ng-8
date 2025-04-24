package guru.qa.niffler.service.repository;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.jdbc.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.jdbc.UserdataUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.spring.UserdataUserRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class UsersDbRepositoryClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepositoryJdbc = new AuthUserRepositoryJdbc();
    private final UserdataUserRepository userdataUserRepositoryJdbc = new UserdataUserRepositoryJdbc();

    private final AuthUserRepository authUserRepositorySpringJdbc = new AuthUserRepositorySpringJdbc();
    private final UserdataUserRepository userdataUserRepositorySpringJdbc = new UserdataUserRepositorySpringJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserJson creatUserJdbc(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
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
                                                ae.setUserId(authUserEntity);
                                                ae.setAuthority(value);
                                                return ae;
                                            }
                                    ).toList()
                    );

                    //создаем нового пользователя и получаем его id
                    authUserEntity.setId(authUserRepositoryJdbc.create(authUserEntity).getId());

                    return UserJson.fromEntity(
                            userdataUserRepositoryJdbc.create(UserEntity.fromJson(user)
                            )
                    );
                }
        );
    }

    public void addIncomeInvitationJdbc(UserJson requester, UserJson addressee) {
        xaTransactionTemplate.execute(() -> {
            userdataUserRepositoryJdbc.addIncomeInvitation(UserEntity.fromJson(requester), UserEntity.fromJson(addressee));
            return null;
        });
    }

    public void addOutcomeInvitationJdbc(UserJson requester, UserJson addressee) {
        xaTransactionTemplate.execute(() -> {
            userdataUserRepositoryJdbc.addOutcomeInvitation(UserEntity.fromJson(requester), UserEntity.fromJson(addressee));
            return null;
        });
    }

    public void addFriendJdbc(UserJson requester, UserJson addressee) {
        xaTransactionTemplate.execute(() -> {
            userdataUserRepositoryJdbc.addFriend(UserEntity.fromJson(requester), UserEntity.fromJson(addressee));
            return null;
        });
    }

    public UserJson creatUserSpringJdbc(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
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
                                                ae.setUserId(authUserEntity);
                                                ae.setAuthority(value);
                                                return ae;
                                            }
                                    ).toList()
                    );

                    //создаем нового пользователя и получаем его id
                    authUserEntity.setId(authUserRepositorySpringJdbc.create(authUserEntity).getId());

                    return UserJson.fromEntity(
                            userdataUserRepositorySpringJdbc.create(UserEntity.fromJson(user)
                            )
                    );
                }
        );
    }

    public void addIncomeInvitationSpringJdbc(UserJson requester, UserJson addressee) {
        xaTransactionTemplate.execute(() -> {
            userdataUserRepositorySpringJdbc.addIncomeInvitation(UserEntity.fromJson(requester), UserEntity.fromJson(addressee));
            return null;
        });
    }

    public void addOutcomeInvitationSpringJdbc(UserJson requester, UserJson addressee) {
        xaTransactionTemplate.execute(() -> {
            userdataUserRepositorySpringJdbc.addOutcomeInvitation(UserEntity.fromJson(requester), UserEntity.fromJson(addressee));
            return null;
        });
    }

    public void addFriendSpringJdbc(UserJson requester, UserJson addressee) {
        xaTransactionTemplate.execute(() -> {
            userdataUserRepositorySpringJdbc.addFriend(UserEntity.fromJson(requester), UserEntity.fromJson(addressee));
            return null;
        });
    }
}
