package guru.qa.niffler.api;

import retrofit2.Call;
import retrofit2.http.*;

public interface AuthApi {

    @POST("/register")
    @FormUrlEncoded
    Call<Void> register(@Field("_csrf") String _csrf,
                        @Field("username") String username,
                        @Field("password") String password,
                        @Field("passwordSubmit") String passwordSubmit);

    @GET("/register")
    Call<Void> requestRegisterForm();
}
