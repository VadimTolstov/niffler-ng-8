package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayV2Api;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import io.qameta.allure.Step;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class GatewayV2ApiClient extends RestClient {

    private final GatewayV2Api gatewayApi;

    public GatewayV2ApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayV2Api.class);
    }

    @Step("Get all friends & income invitations using /api/v2/friends/all endpoint")
    @Nonnull
    public RestResponsePage<UserJson> allFriends(String bearerToken,
                                                 @Nullable Integer page,
                                                 @Nullable Integer size,
                                                 @Nullable String searchQuery) {

        return executeAndVerify(
                gatewayApi.allFriends(
                        "Bearer " + bearerToken,
                        page,
                        size,
                        searchQuery
                )
        );
    }

    @Step("send /api/v2/spends/all GET request to niffler-gateway")
    public RestResponsePage<SpendJson> allSpends(@Nonnull String bearerToken,
                                                 @Nullable Integer page,
                                                 @Nullable DataFilterValues filterPeriod,
                                                 @Nullable CurrencyValues filterCurrency,
                                                 @Nullable String searchQuery) {
        return executeAndVerify(
                gatewayApi.allSpends(
                        "Bearer " + bearerToken,
                        page,
                        filterPeriod,
                        filterCurrency,
                        searchQuery
                )
        );
    }

    @Step("send /api/v2/users/all GET request to niffler-gateway")
    public RestResponsePage<UserJson> allUsers(@Nonnull String bearerToken,
                                               @Nullable String searchQuery,
                                               @Nullable Integer page,
                                               @Nullable Integer size,
                                               @Nullable String sort) {
        return executeAndVerify(
                gatewayApi.allUsers(
                        "Bearer " + bearerToken,
                        searchQuery,
                        page,
                        size,
                        sort
                )
        );
    }


    private <T> RestResponsePage<T> executeAndVerify(Call<RestResponsePage<T>> call) {
        final Response<RestResponsePage<T>> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertEquals(HttpStatus.SC_OK, response.code());
        return requireNonNull(response.body());
    }
}
