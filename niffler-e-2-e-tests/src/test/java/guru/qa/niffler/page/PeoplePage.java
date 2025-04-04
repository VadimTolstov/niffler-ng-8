package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.*;

@ParametersAreNonnullByDefault
public class PeoplePage extends BasePage<PeoplePage> {
    public static final String URL = CFG.frontUrl() + "people/all";
    private final SelenideElement friendsTab = $("a[aria-selected='true'][href='/people/all']");
    private final ElementsCollection allPeopleList = $$("tr.MuiTableRow-root");

    @Step("Проверяем, что загрузилась страница Всех пользователей")
    @Override
    public PeoplePage checkThatPageLoaded() {
        friendsTab.shouldHave(Condition.visible);
        return this;
    }

    @Step("Проверяем отображение пользователя: {users}")
    public PeoplePage checkPeople(String... users) {
        for (String user : users) {
            allPeopleList.filterBy(Condition.text(user));
        }
        return this;
    }

    @Step("Проверяем у пользователя: {username} статус в Waiting... таблице")
    public PeoplePage checkStatusWaiting(String username) {
        $x(".//tr[.//p[text()='%s'] and .//span[contains(text(), 'Waiting...')]]"
                .formatted(username)).shouldHave(Condition.visible);
        return this;
    }

    @Step("Проверяем у пользователя: {username} отображение кнопки Add friend в таблице")
    public PeoplePage checkButtonAddFriend(String username) {
        $x(".//tr[.//p[text()='%s'] and .//button[contains(., 'Add friend')]]"
                .formatted(username)).shouldHave(Condition.visible);
        return this;
    }
}