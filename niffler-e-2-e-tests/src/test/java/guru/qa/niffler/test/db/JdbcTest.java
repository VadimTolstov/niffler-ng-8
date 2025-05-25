package guru.qa.niffler.test.db;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.imp.UsersDbClient;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class JdbcTest {

    static UsersDbClient usersDbClient = new UsersDbClient();

    @ValueSource(strings = {
            "valentin-235",
            "valentin-236",
            "valentin-237"
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
