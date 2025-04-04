package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.Type.*;

@WebTest
public class FriendsWebTest {

    @Test
    void friendShouldBePresentInFriendsTable(@UsersQueueExtension.UserType(WITH_FRIEND) UsersQueueExtension.StaticUser user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.password())
                .getHeaderComponent()
                .toFriendsPage()
                .checkFriend(user.friend());
    }

    @Test
    void friendsTableShouldBeEmptyForNewUser(@UsersQueueExtension.UserType(EMPTY) UsersQueueExtension.StaticUser user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.password())
                .getHeaderComponent()
                .toFriendsPage()
                .checkEmptyListFriends();
    }

    @Test
    void incomeInvitationBePresentInFriendsTable(@UsersQueueExtension.UserType(WITH_INCOME_REQUEST) UsersQueueExtension.StaticUser user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.password())
                .getHeaderComponent()
                .toFriendsPage()
                .checkIncomeFriendship(user.income());
    }

    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UsersQueueExtension.UserType(WITH_OUTCOME_REQUEST) UsersQueueExtension.StaticUser user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .doLogin(new MainPage(), user.username(), user.password())
                .getHeaderComponent()
                .toAllPeoplesPage()
                .checkStatusWaiting(user.outcome());
    }

}
