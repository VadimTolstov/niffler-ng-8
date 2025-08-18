package guru.qa.niffler.test.soap;

import guru.qa.jaxb.userdata.*;
import guru.qa.niffler.api.impl.UserSoapClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.rest.UserJson;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

@SoapTest
public class SoapUsersTest {

    private final UserSoapClient userSoapClient = new UserSoapClient();


    @User
    @Test
    void currentUserTest(UserJson user) throws IOException {
        CurrentUserRequest request = new CurrentUserRequest();
        request.setUsername(user.username());
        UserResponse response = userSoapClient.currentUser(request);
        Assertions.assertEquals(
                user.username(),
                response.getUser().getUsername()
        );
    }

    @User(friends = 4)
    @Test
    void friendsPageTest(UserJson user) throws IOException {
        FriendsPageRequest request = new FriendsPageRequest();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(1);
        pageInfo.setSize(2);
        request.setUsername(user.username());
        request.setPageInfo(pageInfo);
        UsersResponse response = userSoapClient.friendsPage(request);
        Assertions.assertEquals(2, response.getUser().size());
    }

    @User(friends = 4)
    @Test
    void friendGetNamePageTest(UserJson user) throws IOException {
        final String friendName = user.testData().friends().getFirst().username();

        FriendsPageRequest request = new FriendsPageRequest();

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(0);
        pageInfo.setSize(10);
        request.setUsername(user.username());
        request.setPageInfo(pageInfo);
        request.setSearchQuery(friendName);

        UsersResponse response = userSoapClient.friendsPage(request);

        List<guru.qa.jaxb.userdata.User> userList = response.getUser().stream().toList();
        SoftAssertions.assertSoftly(softly -> {
            org.assertj.core.api.Assertions.assertThat(userList).hasSize(1);
            org.assertj.core.api.Assertions.assertThat(friendName.equals(userList.getFirst().getUsername()));
        });
    }

    @User(friends = 4)
    @Test
    void friendGetNameTest(UserJson user) throws IOException {
        final String friendName = user.testData().friends().getFirst().username();

        FriendsRequest request = new FriendsRequest();

        request.setUsername(user.username());
        request.setSearchQuery(friendName);

        UsersResponse response = userSoapClient.friends(request);

        List<guru.qa.jaxb.userdata.User> userList = response.getUser().stream().toList();
        SoftAssertions.assertSoftly(softly -> {
            org.assertj.core.api.Assertions.assertThat(userList).hasSize(1);
            org.assertj.core.api.Assertions.assertThat(friendName.equals(userList.getFirst().getUsername()));
        });
    }

    @User(friends = 1)
    @Test
    void removingFriendTest(UserJson user) throws IOException {
        final String friendName = user.testData().friends().getFirst().username();

        RemoveFriendRequest removingFriend = new RemoveFriendRequest();
        removingFriend.setUsername(user.username());
        removingFriend.setFriendToBeRemoved(friendName);
        userSoapClient.removeFriend(removingFriend);

        FriendsRequest request = new FriendsRequest();
        request.setUsername(user.username());

        UsersResponse response = userSoapClient.friends(request);

        Assertions.assertEquals(0, response.getUser().size());

    }

    @Test
    @User(incomeInvitations = 1)
    void shouldAcceptFriendRequest(UserJson user) throws IOException {
        String senderUsername = user.testData().incomeInvitations().getFirst().username();

        AcceptInvitationRequest acceptInvitationRequest = new AcceptInvitationRequest();
        acceptInvitationRequest.setUsername(user.username());
        acceptInvitationRequest.setFriendToBeAdded(senderUsername);

        UserResponse response = userSoapClient.acceptInvitation(acceptInvitationRequest);

        org.assertj.core.api.Assertions.assertThat(response.getUser().getFriendshipStatus()).isEqualTo(FriendshipStatus.FRIEND);
    }

    @Test
    @User(incomeInvitations = 1)
    void shouldDeclineFriendRequest(UserJson user) throws IOException {
        String senderUsername = user.testData().incomeInvitations().getFirst().username();

        DeclineInvitationRequest declineInvitationRequest = new DeclineInvitationRequest();
        declineInvitationRequest.setUsername(user.username());
        declineInvitationRequest.setInvitationToBeDeclined(senderUsername);

        UserResponse response = userSoapClient.declineInvitation(declineInvitationRequest);

        org.assertj.core.api.Assertions.assertThat(response.getUser().getFriendshipStatus()).isEqualTo(FriendshipStatus.VOID);
    }
}
