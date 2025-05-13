package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.Type.*;

@WebTest
public class FriendsWebTest {

    @User(incomeInvitations = 1)
    @Test
    void acceptingFriendRequest(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .getHeaderComponent()
                .toFriendsPage()
                .checkIncomeFriendship(user.testData().incomeInvitations().getFirst().username());
    }





    @User(friends = 1)
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .getHeaderComponent()
                .toFriendsPage()
                .checkFriend(user.testData().friends().getFirst().username());
    }

    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .getHeaderComponent()
                .toFriendsPage()
                .checkEmptyListFriends();
    }

    @User(incomeInvitations = 1)
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.testData().password())
                .getHeaderComponent()
                .toFriendsPage()
                .checkIncomeFriendship(user.testData().incomeInvitations().getFirst().username());
    }

    @User(outcomeInvitations = 1)
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(),  user.testData().password())
                .getHeaderComponent()
                .toAllPeoplesPage()
                .checkStatusWaiting(user.testData().outcomeInvitations().getFirst().username());
    }
}