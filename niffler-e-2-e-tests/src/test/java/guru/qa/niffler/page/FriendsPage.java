package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.PeopleTable;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;
import lombok.Getter;
import retrofit2.http.GET;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.*;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {

    public static final String URL = CFG.frontUrl() + "people/friends";

    SearchField searchField = new SearchField();
    PeopleTable peopleTable = new PeopleTable();

    private final SelenideElement friendsTab = $("a[aria-selected='true'][href='/people/friends']");
    private final SelenideElement emptyListFriends = $("#simple-tabpanel-friends:not(:has(tr))");

    @Nonnull
    public PeopleTable getPeopleTable() {
        return peopleTable;
    }

    @Step("Проверяем, что загрузилась страница Друзей")
    @Override
    public FriendsPage checkThatPageLoaded() {
        friendsTab.shouldHave(Condition.visible);
        return this;
    }

    @Step("Проверить отображение списка друзей: {users}")
    public FriendsPage checkFriends(String... users) {
        for (String user : users) {
            searchField.search(user);
            peopleTable.checkPeople(user);
        }
        return this;
    }

    @Step("Проверяем отображение входящей заявки на дружбу от пользователя: {username}")
    public FriendsPage checkIncomeFriendship(String username) {
        peopleTable.checkIncomeFriendship(username);
        return this;
    }

    @Step("Проверяем отображение пользователя: {username} в списке друзей")
    public FriendsPage checkFriend(String username) {
        peopleTable.checkFriend(username);
        return this;
    }

    @Step("Проверяем, что у пользователя нет друзей")
    public FriendsPage checkEmptyListFriends() {
        emptyListFriends.shouldHave(Condition.visible);
        return this;
    }
}
