package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.rest.UserJson;
import io.qameta.allure.Step;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class GatewayApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayApi.class);
    }

    @Step("Get all friends & income invitations using /api/friends/all endpoint")
    @Nonnull
    public List<UserJson> allFriends(String bearerToken,
                                     @Nullable String searchQuery) {
        return executeAndGetBody(
                gatewayApi.allFriends(bearerToken, searchQuery)
        );
    }

    @Step("send /api/friends/remove DELETE request to niffler-gateway")
    public void removeFriend(@Nonnull String bearerToken, @Nullable String targetUsername) {
        executeAndVerifySuccess(
                gatewayApi.removeFriend("Bearer " + bearerToken, targetUsername)
        );
    }

    @Step("send /api/invitations/send POST request to niffler-gateway")
    public UserJson sendInvitation(@Nonnull String bearerToken, @Nonnull FriendJson friend) {
        return executeAndGetBody(
                gatewayApi.sendInvitation("Bearer " + bearerToken, friend)
        );
    }

    @Step("send /api/invitations/accept POST request to niffler-gateway")
    public UserJson acceptInvitation(@Nonnull String bearerToken, @Nonnull FriendJson friend) {
        return executeAndGetBody(
                gatewayApi.acceptInvitation("Bearer " + bearerToken, friend)
        );
    }

    @Step("send /api/invitations/decline POST request to niffler-gateway")
    public UserJson declineInvitation(@Nonnull String bearerToken, @Nonnull FriendJson friend) {
        return executeAndGetBody(
                gatewayApi.declineInvitation("Bearer " + bearerToken, friend)
        );
    }

    @Step("send /api/users/all GET request to niffler-gateway")
    public @Nonnull List<UserJson> allUsers(@Nonnull String bearerToken, @Nullable String searchQuery) {
        Response<List<UserJson>> response = executeCall(
                gatewayApi.allUsers("Bearer " + bearerToken, searchQuery)
        );
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }

    private <T> T executeAndGetBody(Call<T> call) {
        Response<T> response = executeCall(call);
        return requireNonNull(response.body());
    }

    private void executeAndVerifySuccess(Call<Void> call) {
        executeCall(call);
    }

    private <T> Response<T> executeCall(Call<T> call) {
        try {
            Response<T> response = call.execute();
            assertEquals(HttpStatus.SC_OK, response.code());
            return response;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}