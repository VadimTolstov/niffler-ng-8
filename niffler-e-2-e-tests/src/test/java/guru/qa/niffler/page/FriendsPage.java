package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.*;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {
    public static final String URL = CFG.frontUrl() + "people/friends";
    private final SelenideElement friendsTab = $("a[aria-selected='true'][href='/people/friends']");
    private final ElementsCollection friendsList = $$(".MuiTableRow-root");
    private final SelenideElement emptyListFriends = $("#simple-tabpanel-friends:not(:has(tr))");

    @Step("Проверяем, что загрузилась страница Друзей")
    @Override

    public FriendsPage checkThatPageLoaded() {
        friendsTab.shouldHave(Condition.visible);
        return this;
    }

    @Step("Проверить отображение списка друзей: {users}")
    public FriendsPage checkFriends(String... users) {
        for (String user : users) {
            friendsList.shouldHave(CollectionCondition.anyMatch(
                    "Элемент с текстом '" + user + "' не найден",
                    el -> el.getText().contains(user)
            ));
        }
        return this;
    }

    @Step("Проверяем отображение входящей заявки на дружбу от пользователя: {username}")
    public FriendsPage checkIncomeFriendship(String username) {
        $x(".//tr[.//p[text()='%s'] and .//button[text()='Accept'] and .//button[text()='Decline']]"
                .formatted(username)).shouldHave(Condition.visible);
        return this;
    }

    @Step("Проверяем отображение пользователя: {username} в списке друзей")
    public FriendsPage checkFriend(String username) {
        $x(".//tr[.//p[text()='%s'] and .//button[text()='Unfriend']]"
                .formatted(username)).shouldHave(Condition.visible);
        return this;
    }

    @Step("Проверяем, что у пользователя нет друзей")
    public FriendsPage emptyListFriends() {
        emptyListFriends.shouldHave(Condition.visible);
        return this;
    }
}
