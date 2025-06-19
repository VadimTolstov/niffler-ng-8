package guru.qa.niffler.test.rest;

import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayApiClient;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@RestTest
public class FriendsV2RestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    private final GatewayV2ApiClient gatewayV2ApiClient = new GatewayV2ApiClient();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @User(friends = 1, incomeInvitations = 2)
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedFromGateway(@Token String bearerToken) {
        final Page<UserJson> responseBody = gatewayV2ApiClient.allFriends(
                bearerToken,
                0,
                10,
                null
        );
        Assertions.assertEquals(3, responseBody.getContent().size());
    }

    @User(friends = 1)
    @ApiLogin
    @Test
    void friendShouldBeDeleted(@Token String token) {
        final Page<UserJson> friendsBefore = gatewayV2ApiClient.allFriends(
                token,
                0,
                10,
                null
        );

        Assertions.assertEquals(1, friendsBefore.getContent().size());
        final List<UserJson> friendToDelete = friendsBefore.stream().toList();

        gatewayApiClient.removeFriend(token, friendToDelete.get(0).username());

        final PageImpl<UserJson> friendsAfter = gatewayV2ApiClient.allFriends(
                token,
                0,
                10,
                null
        );
        Assertions.assertTrue(friendsAfter.isEmpty());
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void invitationShouldBeAccepted(@Token String token) {
        final PageImpl<UserJson> invitationsBefore = gatewayV2ApiClient.allFriends(
                token,
                null,
                null,
                null
        );

        Assertions.assertEquals(1, invitationsBefore.getContent().size());
        final List<UserJson> invitation = invitationsBefore.stream().toList();

        gatewayApiClient.acceptInvitation(token, new FriendJson(invitation.get(0).username()));

        final PageImpl<UserJson> friendsAfter = gatewayV2ApiClient.allFriends(
                token,
                null,
                null,
                null
        );
        Assertions.assertEquals(1, friendsAfter.getContent().size());
        final List<UserJson> friends = friendsAfter.stream().toList();
        Assertions.assertEquals(FriendshipStatus.FRIEND, friends.getFirst().friendshipStatus());
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void invitationShouldBeDeclined(@Token String token) {
        final PageImpl<UserJson> invitationsBefore = gatewayV2ApiClient.allFriends(
                token,
                null,
                null,
                null
        );
        Assertions.assertEquals(1, invitationsBefore.getContent().size());
        final List<UserJson> invitation = invitationsBefore.stream().toList();

        gatewayApiClient.declineInvitation(token, new FriendJson(invitation.get(0).username()));

        final PageImpl<UserJson> invitationsAfter = gatewayV2ApiClient.allFriends(
                token,
                null,
                null,
                null
        );
        Assertions.assertTrue(invitationsAfter.isEmpty());
    }

    @User(outcomeInvitations = 1)
    @ApiLogin
    @Test
    void outgoingInvitationShouldBeCreated(UserJson user, @Token String token) {
        final String outcomeInvitationUsername = user.testData().outcomeInvitationsUsernames()[0];
        final FriendJson newFriend = new FriendJson(outcomeInvitationUsername);

        final List<UserJson> outgoingInvitations = gatewayV2ApiClient.allUsers(
                        token,
                        null,
                        null,
                        null,
                        null
                ).stream()
                .filter(u -> u.friendshipStatus() == FriendshipStatus.INVITE_SENT)
                .toList();

        Assertions.assertEquals(1, outgoingInvitations.size());
        Assertions.assertEquals(newFriend.username(), outgoingInvitations.getFirst().username());
    }
}
