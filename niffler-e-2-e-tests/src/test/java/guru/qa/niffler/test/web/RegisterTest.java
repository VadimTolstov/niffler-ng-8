package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.RegisterPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
public class RegisterTest {

    @Test
    @DisplayName("Регистрация нового пользователя")
    void successRegistration() {
        String name = RandomDataUtils.randomName();
        String password = RandomDataUtils.randomPassword();

        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .registrationNewUser(name, password)
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Регистрация пользователя с логином и паролем из одного символа")
    void validationLoginPassword() {
        String errorName = "Allowed username length should be from 3 to 50 characters";
        String errorPassword = "Allowed password length should be from 3 to 12 characters";
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .setUsername("A")
                .setPassword("A")
                .setPasswordSubmit("A")
                .submitRegistration()
                .checkError(errorName, errorPassword, errorPassword);
    }

    @Test
    @DisplayName("Регистрация пользователя с зарегистрированным логином")
    void registeringAnExistingUser() {
        String username = "books";
        String password = "12345";

        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkError("Username `" + username + "` already exists");
    }

    @Test
    @DisplayName("Регистрация пользователя с невалидными данными в поле Submit password")
    void registeringUserWithInvalidDataInTheSubmitPasswordField() {
        String username = RandomDataUtils.randomName();
        String password = RandomDataUtils.randomPassword();
        String errorPassword = "Passwords should be equal";

        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit("password")
                .submitRegistration()
                .checkError(errorPassword);
    }

    @Test
    @DisplayName("Отключение маскирование у полей Password и Submit password")
    void disablingMaskingForPasswordAndSubmitPasswordFields() {
        String username = RandomDataUtils.randomName();
        String password = RandomDataUtils.randomPassword();

        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .checkPasswordAndSubmitPassword();
    }

    @Test
    @DisplayName("Переход на страницу авторизации")
    void mainPageShouldBeDisplayedAfterSuccessLogin() {

        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .nextLoginPage()
                .checkThatPageLoaded();
    }

}
