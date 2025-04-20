package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.dao.SpendDbClient;
import guru.qa.niffler.service.dao.UsersDbClient;
import guru.qa.niffler.service.dao.UsersDbExperimentalClient;
import guru.qa.niffler.service.repository.UsersDbRepositoryClient;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class JdbcTest {

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpendJdbc(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-1",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx",
                        "null3"
                )
        );

        System.out.println(spend);
    }

    @Test
    void xaTxTest() {
        UsersDbRepositoryClient usersDbClient = new UsersDbRepositoryClient();
        UserJson user = usersDbClient.creatUserJdbc(
                new UserJson(
                        null,
                        "valentin-12",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void xaTxaTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson user = usersDbClient.creatUserJdbc(
                new UserJson(
                        null,
                        "valentin-5",
                        "valentin-5",
                        "valentin-5",
                        "valentin-5",
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void springJdbcTest() {
        UserJson user = new UsersDbExperimentalClient().createUserSpringJdbcTransaction(
                new UserJson(
                        null,
                        "valentin-4",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void createUserSpringJdbcTransactionTest() {
        UserJson user = new UsersDbExperimentalClient().createUserSpringJdbcTransaction(
                new UserJson(
                        null,
                        null,
                        "valentin-10",
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void createUserSpringJdbcWithoutTransactionTest() {
        UserJson user = new UsersDbExperimentalClient().createUserSpringJdbc(
                new UserJson(
                        null,
                        "valentin-10",
                        null,
                        null,
                        null,
                        null,
                        "valentin-10",
                        "valentin-10"
                )
        );
        System.out.println(user);
    }

    @Test
    void createUserJdbcTransactionTest() {
        UserJson user = new UsersDbExperimentalClient().createUserJdbcTransaction(
                new UserJson(
                        null,
                        "valentin-10",
                        null,
                        null,
                        "valentin-10",
                        null,
                        "valentin-10",
                        "valentin-10"
                )
        );
        System.out.println(user);
    }

    @Test
    void createUserJdbcWithoutTransactionTest() {
        UserJson user = new UsersDbExperimentalClient().createUserJdbc(
                new UserJson(
                        null,
                        "valentin-10",
                        "valentin-10",
                        null,
                        "valentin-10",
                        null,
                        "valentin-10",
                        "valentin-10"
                )
        );
        System.out.println(user);
    }
}
