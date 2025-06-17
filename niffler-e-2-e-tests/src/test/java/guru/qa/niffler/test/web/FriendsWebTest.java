package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.PeoplePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebTest {

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
//добавления друга
    void acceptingFriendRequest(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkIncomeFriendship(user.testData().incomeInvitations().getFirst().username());
    }


    @User(friends = 1)
    @ApiLogin
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkFriend(user.testData().friends().getFirst().username());
    }

    @User
    @ApiLogin
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkEmptyListFriends();
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    @DisplayName("Принять входящие предложение дружбы")
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkIncomeFriendship(user.testData().incomeInvitations().getFirst().username())
                .getPeopleTable()
                .acceptFriendship();

        new FriendsPage().checkAlert(
                "Invitation of %s accepted".formatted(
                        user.testData()
                                .incomeInvitations()
                                .getFirst()
                                .username()
                )
        );
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    @DisplayName("Отклонить входящие предложение дружбы")
    void rejectingFriendRequest(UserJson user) {
        final String incomeInvitationsUserName = user.testData().incomeInvitations().getFirst().username();
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkIncomeFriendship(incomeInvitationsUserName)
                .getPeopleTable()
                .declineFriendInvitationFromUser(incomeInvitationsUserName);
        new FriendsPage().checkAlert("Invitation of %s is declined".formatted(incomeInvitationsUserName));
    }

    @User(outcomeInvitations = 1)
    @ApiLogin
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(PeoplePage.URL, PeoplePage.class)
                .checkStatusWaiting(user.testData().outcomeInvitations().getFirst().username());
    }
}