package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Header extends BaseComponent<Header>{

    public Header() {
        super($("#root header"));
    }

    private final SelenideElement mainPageLink = self.$("a[href*='/main']");
    private final SelenideElement addSpendingBtn = self.$("a[href*='/spending']");
    private final SelenideElement menuBtn = self.$("button");
    private final SelenideElement menu = $("ul[role='menu']");
    private final ElementsCollection menuItems = menu.$$("li");

    @Step("Перейти на страницу профиля")
    public ProfilePage toProfilePage() {
        menuBtn.shouldBe(Condition.visible, Duration.ofSeconds(10)).click();
        menuItems.findBy(Condition.text("Profile")).click();
        return new ProfilePage();
    }

    @Step("Перейти на страницу друзей")
    public FriendsPage toFriendsPage() {
        menuBtn.shouldBe(Condition.visible, Duration.ofSeconds(15)).click();
        menuItems.findBy(Condition.text("Friends")).click();
        return new FriendsPage();
    }

    @Step("Перейти на страницу всех людей")
    public PeoplePage toAllPeoplesPage() {
        menuBtn.shouldBe(Condition.visible, Duration.ofSeconds(10)).click();
        menuItems.findBy(Condition.text("All People")).click();
        return new PeoplePage();
    }

    @Step("Выйти из аккаунта")
    public LoginPage signOut() {
        menuBtn.click();
        menuItems.findBy(Condition.text("Sign out")).click();
        return new LoginPage();
    }

    @Step("Добавить новую трату")
    public EditSpendingPage addSpendingPage() {
        addSpendingBtn.click();
        return new EditSpendingPage();
    }

    @Step("Перейти на главную страницу")
    public MainPage toMainPage() {
        mainPageLink.click();
        return new MainPage();
    }
}
