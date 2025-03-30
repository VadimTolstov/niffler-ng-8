package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class RegisterPage extends BasePage<RegisterPage> {

    public static final String URL = CFG.authUrl() + "register";

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitPasswordInput = $("input[name='passwordSubmit']");
    private final SelenideElement passwordButton = $("#passwordBtn");
    private final SelenideElement submitPasswordButton = $("#passwordSubmitBtn");
    private final SelenideElement buttonSubmit = $("button[type='submit']");
    private final SelenideElement loginPage = $(".form_sign-in");
    private final SelenideElement nextLoginPage = $("a[class='form__link']");
    private final ElementsCollection errorContainer = $$(".form__error");

    @Override
    public RegisterPage checkThatPageLoaded() {
        usernameInput.should(Condition.visible);
        passwordInput.should(Condition.visible);
        submitPasswordInput.should(Condition.visible);
        return this;
    }

    @Step("Ввести username: {0}")
    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Ввести password: {0}")
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Ввести submit password: {0}")
    public RegisterPage setPasswordSubmit(String password) {
        submitPasswordInput.setValue(password);
        return this;
    }

    @Step("Нажимаем на  кнопку Sign Up")
    public RegisterPage submitRegistration() {
        buttonSubmit.click();
        return this;
    }

    @Step("Нажимаем на  кнопку Sign in и перегодим на страницу Log in")
    public LoginPage clickSingIn() {
        loginPage.click();
        return new LoginPage();
    }

    @Step("Регистрируем нового пользователя username: {0}, password: {1}")
    public LoginPage registrationNewUser(String username, String password) {
        return setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .clickSingIn();
    }

    @Step("Показать Password")
    public RegisterPage openPassword() {
        passwordButton.click();
        return this;
    }

    @Step("Показать Submit password")
    public RegisterPage openSubmitPassword() {
        submitPasswordButton.click();
        return this;
    }

    @Step("Проверяем, что у полей Password и Submit password отключено маскирование")
    public RegisterPage checkPasswordAndSubmitPassword() {
        openPassword().openSubmitPassword();
        passwordButton.shouldHave(cssClass("form__password-button_active"), visible);
        submitPasswordButton.shouldHave(cssClass("form__password-button_active"), visible);
        return this;
    }

    @Step("Проверяем ошибку на странице: {error}")
    @Nonnull
    public RegisterPage checkError(String... errors) {
        errorContainer.shouldHave(textsInAnyOrder(errors));
        return this;
    }

    @Step("Переходим со страницы регистрации на страницу авторизации по ссылке")
    @Nonnull
    public LoginPage nextLoginPage() {
        nextLoginPage.click();
        return new LoginPage();
    }
}
