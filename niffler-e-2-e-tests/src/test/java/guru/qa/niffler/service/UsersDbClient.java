package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.Databases.XaFunction;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDAOJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.ex.DataAccessException;
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;

import static guru.qa.niffler.data.Databases.xaTransaction;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserJson addUser(@Nonnull UserJson userJson) {
        return xaTransaction(
                new XaFunction<>(
                        connection -> {
                            if (userJson.username() != null) {
                                //проверяем есть ли такой пользователь в таблице
                                Optional<AuthUserEntity> findUserEntity = new AuthUserDaoJdbc(connection).findUserByName(userJson.username());
                                if (findUserEntity.isPresent()) {
                                    throw new DataAccessException("Пользователь c именем =  " + userJson.username() + " уже существует ");
                                }

                                AuthUserEntity authUserEntity = new AuthUserEntity();
                                authUserEntity.setUsername(userJson.username());
                                authUserEntity.setPassword(pe.encode("12345"));
                                authUserEntity.setEnabled(true);
                                authUserEntity.setAccountNonExpired(true);
                                authUserEntity.setAccountNonLocked(true);
                                authUserEntity.setCredentialsNonExpired(true);

                                //создаем нового пользователя и получаем его id
                                authUserEntity.setId(new AuthUserDaoJdbc(connection).creat(authUserEntity).getId());

                                new AuthAuthorityDaoJdbc(connection)
                                        .creat(Arrays.stream(Authority.values())
                                                .map(value -> {
                                                            AuthorityEntity ae = new AuthorityEntity();
                                                            ae.setUserId(authUserEntity);
                                                            ae.setAuthority(value);
                                                            return ae;
                                                        }
                                                ).toArray(AuthorityEntity[]::new));
                            }
                            return null;
                        },
                        CFG.authJdbcUrl()
                ),
                new Databases.XaFunction<>(
                        connection -> {
                            UserEntity userEntity = UserEntity.fromEntity(userJson);
                            userEntity = new UserdataUserDAOJdbc(connection).createUser(userEntity);
                            return UserJson.fromEntity(userEntity);
                        },
                        CFG.userdataJdbcUrl()
                )
        );
    }
}
