package guru.qa.niffler.service.imp;

import guru.qa.niffler.api.imp.AuthApiClient;
import guru.qa.niffler.api.imp.UserApiClient;
import guru.qa.niffler.ex.ApiException;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;

import javax.annotation.Nonnull;

public class UserApiService implements UsersClient {

    private final AuthApiClient authApiClient;
    private final UserApiClient userApiClient;

    public UserApiService() {
        this.authApiClient = new AuthApiClient();
        this.userApiClient = new UserApiClient();
    }

    @Override
    public UserJson createUser(String username, String password) {
        return new UsersDbClient().createUser(username, password);
    }

    @Override
    public void createIncomeInvitations(@Nonnull UserJson targetUser, int count) {
        validateUserExists(targetUser.username());
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                UserJson addressee = createRandomUser();
                userApiClient.sendInvitation(addressee.username(), targetUser.username());
                targetUser.testData().incomeInvitations().add(addressee);
            }
        }
    }


    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        validateUserExists(targetUser.username());
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                UserJson addressee = createRandomUser();
                userApiClient.sendInvitation(targetUser.username(), addressee.username());
                targetUser.testData().outcomeInvitations().add(addressee);
            }
        }
    }

    @Override
    public void createFriends(UserJson targetUser, int count) {
        validateUserExists(targetUser.username());
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                UserJson addressee = createRandomUser();
                userApiClient.sendInvitation(targetUser.username(), addressee.username());
                userApiClient.acceptInvitation(addressee.username(), targetUser.username());
                targetUser.testData().friends().add(addressee);
            }
        }
    }

    private UserJson createRandomUser() {
        String username = RandomDataUtils.randomUsername();
        return createUser(username, "12345");
    }

    private void validateUserExists(String username) {
        UserJson user = userApiClient.currentUser(username);
        if (user == null || user.username() == null) {
            throw new ApiException("User not found: " + username);
        }
    }
}