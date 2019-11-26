package api;

import models.PrefDetails;
import models.Result;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @FormUrlEncoded

    @POST("register")

    Call<Result>createUser(
            @Field("id") int id,
            @Field("name") String name,
            @Field("email")String email,
            @Field("contact") String contact,
            @Field("password") String password

    );

    @GET("display_lawyers")
    Call<String> getString();

    //    @POST("login")
//    Call<User>Login(@Field("email") String email, @Field("password") String password );
    @FormUrlEncoded
    @POST("add_case_details")
    Call<PrefDetails>addDetails(@Field("client_email") String client_email,
                                @Field("case_type") String case_type,
                                @Field("description") String description,
                                @Field("location") String location,
                                @Field("other") String other);
}


