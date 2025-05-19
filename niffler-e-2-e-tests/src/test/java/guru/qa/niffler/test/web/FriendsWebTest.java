package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebTest {
    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);


    @User(incomeInvitations = 1)
    @Test
//добавления друга
    void acceptingFriendRequest(UserJson user) {
        driver.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .getHeaderComponent()
                .toFriendsPage()
                .checkIncomeFriendship(user.testData().incomeInvitations().getFirst().username());
    }


    @User(friends = 1)
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        driver.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .getHeaderComponent()
                .toFriendsPage()
                .checkFriend(user.testData().friends().getFirst().username());
    }

    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        driver.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .getHeaderComponent()
                .toFriendsPage()
                .checkEmptyListFriends();
    }

    @User(incomeInvitations = 1)
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        driver.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .getHeaderComponent()
                .toFriendsPage()
                .checkIncomeFriendship(user.testData().incomeInvitations().getFirst().username());
    }

    @User(outcomeInvitations = 1)
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        driver.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .getHeaderComponent()
                .toAllPeoplesPage()
                .checkStatusWaiting(user.testData().outcomeInvitations().getFirst().username());
    }
}