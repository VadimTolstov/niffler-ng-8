package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.PeopleTable;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.*;

@ParametersAreNonnullByDefault
public class PeoplePage extends BasePage<PeoplePage> {
    SearchField searchField = new SearchField();
    PeopleTable peopleTable = new PeopleTable();

    public static final String URL = CFG.frontUrl() + "people/all";
    private final SelenideElement friendsTab = $("a[aria-selected='true'][href='/people/all']");

    @Step("Проверяем, что загрузилась страница Всех пользователей")
    @Override
    public PeoplePage checkThatPageLoaded() {
        friendsTab.shouldHave(Condition.visible);
        return this;
    }

    @Step("Проверяем отображение пользователя: {users}")
    public PeoplePage checkPeople(String... users) {
        for (String user : users) {
            searchField.search(user);
            peopleTable.checkPeople(user);
        }
        return this;
    }

    @Step("Проверяем у пользователя: {username} статус в Waiting... таблице")
    public PeoplePage checkStatusWaiting(String username) {
        searchField.search(username);
        peopleTable.checkStatusWaiting(username);
        return this;
    }

    @Step("Проверяем у пользователя: {username} отображение кнопки Add friend в таблице")
    public PeoplePage checkButtonAddFriend(String username) {
        searchField.search(username);
        peopleTable.checkButtonAddFriend(username);
        return this;
    }
}