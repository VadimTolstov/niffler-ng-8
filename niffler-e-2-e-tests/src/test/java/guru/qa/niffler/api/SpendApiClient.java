package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;
import okhttp3.OkHttpClient;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    @Step("Send POST [internal/spends/add] to niffler-spend")
    public @Nonnull SpendJson addSpend(@Nonnull SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.addSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_CREATED, response.code());
        return Objects.requireNonNull(response.body(), "Ответ API вернул null POST [internal/spends/add]");
    }

    @Step("Send GET [internal/spends/{id}] to niffler-spend")
    public @Nonnull SpendJson getSpend(@Nonnull String id,
                                       @Nonnull String username) {
        final Response<SpendJson> response;
        try {
            response = spendApi.getSpend(id, username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
        return Objects.requireNonNull(response.body(), "Ответ API вернул null GET [internal/spends/{id}]");
    }

    @Step("Send GET [internal/spends/all] to niffler-spend")
    public @Nonnull List<SpendJson> getAllSpends(@Nonnull String username,
                                                 @Nullable CurrencyValues filterCurrency,
                                                 @Nullable Date from,
                                                 @Nullable Data to) {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi.getAllSpends(username, filterCurrency, from, to)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }

    @Step("Send PATCH [internal/spends/edit] to niffler-spend")
    public @Nonnull SpendJson editSpend(@Nonnull SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.editSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
        return Objects.requireNonNull(response.body(), "Ответ API вернул null PATCH [internal/spends/edit]");
    }

    @Step("Send DELETE [internal/spends/remove] to niffler-spend")
    public void deleteSpends(@Nonnull String username,
                             @Nonnull List<String> ids) {
        final Response<Void> response;
        try {
            response = spendApi.deleteSpends(username, ids)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_ACCEPTED, response.code());
    }

    @Step("Send GET [/internal/categories/all] to niffler-spend")
    public @Nonnull List<CategoryJson> getAllCategories(@Nonnull String username,
                                                        boolean excludeArchived) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.getAllCategories(username, excludeArchived)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }

    @Step("Send POST [/internal/categories/add")
    public @Nonnull CategoryJson addCategory(@Nonnull CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
        return Objects.requireNonNull(response.body(), "Ответ API вернул null POST [/internal/categories/add]");
    }

    @Step("Send PATCH [/internal/categories/update")
    public @Nonnull CategoryJson updateCategory(@Nonnull CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_OK, response.code());
        return Objects.requireNonNull(response.body(), "Ответ API вернул null PATCH [/internal/categories/update]");
    }
}
