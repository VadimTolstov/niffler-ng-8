package guru.qa.niffler.test.fake.api;

import guru.qa.niffler.api.impl.UserApiClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
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
