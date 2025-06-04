package guru.qa.niffler.test.api;

import guru.qa.niffler.api.imp.UserApiClient;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.imp.UserApiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

@Order(Integer.MAX_VALUE)
public class LastTest {

    @User
    @Test
    void lastTest(UserJson user) {
        List<UserJson> userList = new UserApiClient().allUsers(user.username(), null);
        Assertions.assertFalse(userList.isEmpty(), "Список пользователей должен быть не пустым");
    }
}
