package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.imp.UserApiService;
import guru.qa.niffler.service.imp.UsersDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class JdbcTest {

    static UsersDbClient usersDbClient = new UsersDbClient();

    @ValueSource(strings = {
            "valentin-222",
            "valentin-223",
            "valentin-224"
    })

    @ParameterizedTest
    void hibernateTest(String username) {
        UserJson user = usersDbClient.createUser(
                username,
                "12345"
        );
        usersDbClient.createIncomeInvitations(user, 1);
    }
}
