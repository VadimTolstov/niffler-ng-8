package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.UdUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.UserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDaoSpringJdbcDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDaoSpringJdbc = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDao udUserDaoSpringJdbc = new UdUserDaoSpringJdbc();

    private final AuthUserDao authUserDaoJdbc = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDaoJdbc = new AuthAuthorityDaoJdbc();
    private final UdUserDao udUserDaoJdbc = new UdUserDaoJdbc();


    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJdbcUrl())
            )
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserJson creatUserSpringJdbc(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUserEntity = new AuthUserEntity();
                    authUserEntity.setUsername(user.username());
                    authUserEntity.setPassword(pe.encode("12345"));
                    authUserEntity.setEnabled(true);
                    authUserEntity.setAccountNonExpired(true);
                    authUserEntity.setAccountNonLocked(true);
                    authUserEntity.setCredentialsNonExpired(true);

                    //создаем нового пользователя и получаем его id
                    authUserEntity.setId(authUserDaoSpringJdbcDao.creat(authUserEntity).getId());

                    authAuthorityDaoSpringJdbc.create(Arrays.stream(Authority.values())
                            .map(value -> {
                                        AuthorityEntity ae = new AuthorityEntity();
                                        ae.setUserId(authUserEntity);
                                        ae.setAuthority(value);
                                        return ae;
                                    }
                            ).toArray(AuthorityEntity[]::new));

                    return UserJson.fromEntity(
                            udUserDaoSpringJdbc.createUser(UserEntity.fromJson(user)
                            )
                    );
                }
        );
    }

    public UserJson creatUserJdbc(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUserEntity = new AuthUserEntity();
                    authUserEntity.setUsername(user.username());
                    authUserEntity.setPassword(pe.encode("12345"));
                    authUserEntity.setEnabled(true);
                    authUserEntity.setAccountNonExpired(true);
                    authUserEntity.setAccountNonLocked(true);
                    authUserEntity.setCredentialsNonExpired(true);

                    //создаем нового пользователя и получаем его id
                    authUserEntity.setId(authUserDaoJdbc.creat(authUserEntity).getId());

                    authAuthorityDaoJdbc.create(Arrays.stream(Authority.values())
                            .map(value -> {
                                        AuthorityEntity ae = new AuthorityEntity();
                                        ae.setUserId(authUserEntity);
                                        ae.setAuthority(value);
                                        return ae;
                                    }
                            ).toArray(AuthorityEntity[]::new));

                    return UserJson.fromEntity(
                            udUserDaoJdbc.createUser(UserEntity.fromJson(user)
                            )
                    );
                }
        );

    }
}
