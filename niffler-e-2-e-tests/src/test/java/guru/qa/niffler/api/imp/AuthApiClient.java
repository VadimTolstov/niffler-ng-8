package guru.qa.niffler.api.imp;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.ex.ApiException;
import io.qameta.allure.Step;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthApiClient {

    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi;

    public AuthApiClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CFG.authUrl())
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        this.authApi = retrofit.create(AuthApi.class);
    }

    @Step("Send POST [/register] to niffler-auth")
    public void register(@Nonnull String _csrf,
                         @Nonnull String username,
                         @Nonnull String password,
                         @Nonnull String passwordSubmit) {
        try {
            final Response<Void> response = authApi.register(
                    _csrf,
                    username,
                    password,
                    passwordSubmit
            ).execute();

            assertEquals(HttpStatus.SC_OK, response.code());
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }
}
