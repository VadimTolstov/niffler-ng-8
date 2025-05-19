package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;


@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {
    public static final String URL = CFG.authUrl() + "login";

    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement submitBtn;
    private final SelenideElement nextOpenRegisterPage;
    private final SelenideElement passwordButton;
    private final ElementsCollection errorContainer;

    public LoginPage(SelenideDriver driver) {
    this.usernameInput = driver.$("input[name='username']");
    this.passwordInput = driver.$("input[name='password']");
    this.submitBtn = driver.$("button[type='submit']");
    this.nextOpenRegisterPage = driver.$("a[class='form__register']");
    this.passwordButton = driver.$(".form__password-button");
        this.errorContainer = driver.$$(".form__error");
    }

    public LoginPage() {
        this.usernameInput = $("input[name='username']");
        this.passwordInput = $("input[name='password']");
        this.submitBtn = $("button[type='submit']");
        this.nextOpenRegisterPage = $("a[class='form__register']");
        this.passwordButton = $(".form__password-button");
        this.errorContainer = $$(".form__error");
    }

    @Step("Проверяем, что загрузилась страница авторизации")
    @Override
    public LoginPage checkThatPageLoaded() {
        usernameInput.shouldHave(Condition.visible);
        passwordInput.shouldHave(Condition.visible);
        submitBtn.shouldHave(Condition.visible);
        return this;
    }

    @Step("Авторизуемся пользователем с username: {0}, password: {1}")
    public <T extends BasePage<?>> T doLogin(T expectedPage, String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitBtn.click();
        return expectedPage;
    }

    @Step("Заполняем поле, password: {0}")
    public LoginPage passwordInput(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Проверяем ошибку на странице: {error}")
    public LoginPage checkError(String... errors) {
        errorContainer.shouldHave(textsInAnyOrder(errors));
        return this;
    }

    @Step("Переходим на страницу регистрации")
    public RegisterPage nextOpenRegisterPage() {
        nextOpenRegisterPage.click();
        return new RegisterPage();
    }

    @Step("Показать Password")
    public LoginPage openPassword() {
        passwordButton.click();
        return this;
    }

    @Step("Проверяем, что у полей Password отключено маскирование")
    public LoginPage checkPassword() {
        openPassword();
        passwordButton.shouldHave(cssClass("form__password-button_active"), visible);
        return this;
    }

}
