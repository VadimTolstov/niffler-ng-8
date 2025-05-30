package guru.qa.niffler.service.imp;

import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.core.CookieName;
import guru.qa.niffler.api.core.ThreadSafeCookiesStore;
import guru.qa.niffler.api.imp.AuthApiClient;
import guru.qa.niffler.api.imp.UserApiClient;
import guru.qa.niffler.ex.ApiException;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class UserApiService implements UsersClient {

    private final AuthApiClient authApiClient;
    private final UserApiClient userApiClient;

    public UserApiService() {
        this.authApiClient = new AuthApiClient();
        this.userApiClient = new UserApiClient();
    }

    @Override
    @Step("Создание нового пользователя с именем: {username}")
    public @Nonnull UserJson createUser(@Nonnull String username, @Nonnull String password) {
        authApiClient.requestRegisterForm();
        authApiClient.register(
                ThreadSafeCookiesStore.INSTANCE.cookieValue(CookieName.XSRF_TOKEN.getValue()),
                username,
                password,
                password
        );

        long maxWaitTime = 5000L;
        Stopwatch sw = Stopwatch.createStarted();
        while (sw.elapsed(TimeUnit.MILLISECONDS) < maxWaitTime) {
            try {
                UserJson userJson = userApiClient.currentUser(username);
                if (userJson != null || userJson.id() != null) {
                    return userJson; // Пользователь найден, возвращаем
                } else {
                    Thread.sleep(100); // Ожидаем перед следующей проверкой
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Ошибка при выполнении запроса на получение пользователя или ожидании", e);
            }
        }
        // Если пользователь не найден за отведенное время
        throw new AssertionError("Пользователь не был найден в системе после истечения времени ожидания");
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

    private @Nonnull UserJson createRandomUser() {
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