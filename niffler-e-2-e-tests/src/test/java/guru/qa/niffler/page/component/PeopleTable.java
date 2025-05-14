package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class PeopleTable {

    private final SelenideElement self = $("#root header");
    private final SelenideElement peopleTable = self.$("div[role='tabpanel']");
    private final ElementsCollection allPeopleList = peopleTable.$$("tr.MuiTableRow-root");

    @Step("Проверяем отображение пользователя: {user}")
    public void checkPeople(String user) {
        allPeopleList.filterBy(Condition.text(user));
    }

    @Step("Проверяем у пользователя: {username} статус в Waiting... таблице")
    public void checkStatusWaiting(String username) {
        $x(".//tr[.//p[text()='%s'] and .//span[contains(text(), 'Waiting...')]]"
                .formatted(username)).shouldHave(Condition.visible);
    }

    @Step("Проверяем у пользователя: {username} отображение кнопки Add friend в таблице")
    public void checkButtonAddFriend(String username) {
        $x(".//tr[.//p[text()='%s'] and .//button[contains(., 'Add friend')]]"
                .formatted(username)).shouldHave(Condition.visible);
    }

    @Step("Проверяем отображение пользователя: {username} в списке друзей")
    public void checkFriend(String username) {
        $x(".//tr[.//p[text()='%s'] and .//button[text()='Unfriend']]"
                .formatted(username)).shouldHave(Condition.visible);
    }

    @Step("Проверяем отображение входящей заявки на дружбу от пользователя: {username}")
    public void checkIncomeFriendship(String username) {
        $x(".//tr[.//p[text()='%s'] and .//button[text()='Accept'] and .//button[text()='Decline']]"
                .formatted(username)).shouldHave(Condition.visible);
    }

}
