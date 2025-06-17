package guru.qa.niffler.test.api;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.imp.UserApiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OAuthTest {
    private final UserApiService userApiService = new UserApiService();


    @Test
    @ApiLogin(username = "Adica", password = "12345")
    void oauthTest(@Token String token) {
        System.out.println("Получили токен = " + token);
        Assertions.assertNotNull(token);
    }

    @Test
    @User(friends = 1)
    @ApiLogin
    void oauthTest2(@Token String token, UserJson user) {
        System.out.println("Получили токен = " + token);
          System.out.println("Получили user =================================== " + user );
        Assertions.assertNotNull(token);
    }

    @Test
    @ApiLogin(username = "Adica", password = "12345")
    void oauthTest3(@Token String token) {
        System.out.println("Получили токен = " + token);
        Assertions.assertNotNull(token);
    }
}
