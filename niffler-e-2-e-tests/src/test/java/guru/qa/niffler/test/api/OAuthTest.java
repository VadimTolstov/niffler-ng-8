package guru.qa.niffler.test.api;

import guru.qa.niffler.service.imp.UserApiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OAuthTest {
    private final UserApiService userApiService = new UserApiService();


    @Test
    void oauthTest() {
        final String token = userApiService.singIn("Adica", "12345");
        System.out.println("Получили токен = " + token);
        Assertions.assertNotNull(token);
    }

    @Test
    void oauthTest2() {
        final String token = userApiService.singIn("raymundo.bartell", "12345");
        System.out.println("Получили токен = " + token);
        Assertions.assertNotNull(token);
    }

    @Test
    void oauthTest3() {
        final String token = userApiService.singIn("johnie.parker", "12345");
        System.out.println("Получили токен = " + token);
        Assertions.assertNotNull(token);
    }
}
