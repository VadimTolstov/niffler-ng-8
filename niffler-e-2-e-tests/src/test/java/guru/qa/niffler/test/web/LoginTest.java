package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

public class LoginTest {



    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver chromeDriver = new SelenideDriver(SelenideUtils.chromeConfig);

    @DisplayName("Авторизация пользователя")
    @Test
    void userAuthorization() {
        browserExtension.drivers().add(chromeDriver);

        final String username = "duck";
        final String password = "12345";

        chromeDriver.open(LoginPage.URL);
        new LoginPage(chromeDriver).checkThatPageLoaded()
                .doLogin(new MainPage(), username, password)
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Авторизация незарегистрированного пользователя")
    void authorizationOfAnUnregisteredUser() {
        SelenideDriver firefox = new SelenideDriver(SelenideUtils.firefoxConfig);

        browserExtension.drivers().addAll(List.of(chromeDriver, firefox));

        final String username = RandomDataUtils.randomUsername();
        final String password = RandomDataUtils.randomPassword();

        firefox.open(LoginPage.URL);
        chromeDriver.open(LoginPage.URL);
        new LoginPage(chromeDriver).doLogin(new LoginPage(chromeDriver), username, password)
                .checkError("Неверные учетные данные пользователя");
    }

    @Test
    @DisplayName("Авторизация пользователя c невалидным паролем")
    void userAuthorizationWithInvalidPassword() {
        browserExtension.drivers().add(chromeDriver);

        final String username = "duck";
        final String password = RandomDataUtils.randomPassword();

        chromeDriver.open(LoginPage.URL);
        new LoginPage(chromeDriver).doLogin(new LoginPage(chromeDriver), username, password)
                .checkError("Неверные учетные данные пользователя");
    }

    @Test
    @DisplayName("Переход на страницу регистрации")
    void goingToTheRegistrationPage() {
        browserExtension.drivers().add(chromeDriver);

        chromeDriver.open(LoginPage.URL);
        new LoginPage(chromeDriver).nextOpenRegisterPage()
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Отключение маскирование у Password")
    void disablingPasswordMasking() {
        browserExtension.drivers().add(chromeDriver);

        chromeDriver.open(LoginPage.URL);
        new LoginPage(chromeDriver).passwordInput(RandomDataUtils.randomPassword())
                .checkPassword();
    }
}
