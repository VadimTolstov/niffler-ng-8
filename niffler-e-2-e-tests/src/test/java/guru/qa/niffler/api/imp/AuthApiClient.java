package guru.qa.niffler.api.imp;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.ex.ApiException;
import io.qameta.allure.Step;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthApiClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl());
        authApi = retrofit.create(AuthApi.class);
    }

    @Step("Send GET [/register] to niffler-auth")
    public void requestRegisterForm() {
        executeVoid(authApi.requestRegisterForm(), HttpStatus.SC_OK);
    }

    @Step("Send POST [/register] to niffler-auth")
    public void register(@Nonnull String _csrf,
                         @Nonnull String username,
                         @Nonnull String password,
                         @Nonnull String passwordSubmit) {
        executeVoid(
                authApi.register(
                        _csrf,
                        username,
                        password,
                        passwordSubmit
                ), HttpStatus.SC_CREATED
        );
    }

    private void executeVoid(@Nonnull Call<Void> call, int expectedStatusCode) {
        try {
            final Response<Void> response = call.execute();
            assertEquals(expectedStatusCode, response.code());
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }
}
