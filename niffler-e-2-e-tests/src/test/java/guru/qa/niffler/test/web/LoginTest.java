package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginTest {

    @DisplayName("Авторизация пользователя")
    @Test
    void userAuthorization() {
        final String username = "duck";
        final String password = "12345";

        Selenide.open(LoginPage.URL, LoginPage.class)
                .checkThatPageLoaded()
                .doLogin(new MainPage(), username, password)
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Авторизация незарегистрированного пользователя")
    void authorizationOfAnUnregisteredUser() {
        final String username = RandomDataUtils.randomUsername();
        final String password = RandomDataUtils.randomPassword();

        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new LoginPage(), username, password)
                .checkError("Неверные учетные данные пользователя");
    }

    @Test
    @DisplayName("Авторизация пользователя c невалидным паролем")
    void userAuthorizationWithInvalidPassword() {
        final String username = "duck";
        final String password = RandomDataUtils.randomPassword();

        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new LoginPage(), username, password)
                .checkError("Неверные учетные данные пользователя");
    }

    @Test
    @DisplayName("Переход на страницу регистрации")
    void goingToTheRegistrationPage() {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .nextOpenRegisterPage()
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Отключение маскирование у Password")
    void disablingPasswordMasking() {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .passwordInput(RandomDataUtils.randomPassword())
                .checkPassword();
    }
}
