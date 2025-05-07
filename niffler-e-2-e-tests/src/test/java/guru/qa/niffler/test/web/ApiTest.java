package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.imp.UserApiService;
import org.junit.jupiter.api.Test;

public class ApiTest {

    @Test
    void apiTest() {
        UserApiService userApiService = new UserApiService();

        UserJson user = userApiService.createUser("valentin-231", "12345");
        userApiService.createFriends(user, 1);
    }
}
