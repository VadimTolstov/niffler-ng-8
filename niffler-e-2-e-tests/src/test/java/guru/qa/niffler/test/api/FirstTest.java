package guru.qa.niffler.test.api;

import guru.qa.niffler.api.imp.UserApiClient;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

@Order(1)
public class FirstTest {


    @User
    @Test
    void firstUserTest(UserJson user) {
        List<UserJson> userList = new UserApiClient().allUsers(user.username(), null);
        Assertions.assertFalse(userList.isEmpty(), "Список пользователей должен быть пустым");
    }
}
