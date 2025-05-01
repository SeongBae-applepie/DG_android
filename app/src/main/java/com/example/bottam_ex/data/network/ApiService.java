package com.example.bottam_ex.data.network;

import com.example.bottam_ex.data.model.CheckEmailResponse;
import com.example.bottam_ex.data.model.LoginRequest;
import com.example.bottam_ex.data.model.LoginResponse;
import com.example.bottam_ex.data.model.TokenResponse;
import com.example.bottam_ex.data.model.User;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/api/signup")
    Call<ResponseBody> signup(@Body Map<String, String> body);


    @POST("/api/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("/api/refresh")
    Call<TokenResponse> refresh(@Body Map<String, String> body);

    @GET("/api/profile")
    Call<User> getProfile(@Header("Authorization") String token);

    @POST("/api/check-email")
    Call<CheckEmailResponse> checkEmail(@Body Map<String, String> body);

    @POST("/api/m/send-verification")
    Call<ResponseBody> sendVerification(@Body Map<String, String> body);

    @POST("/api/m/verify-code")
    Call<ResponseBody> verifyCode(@Body Map<String, String> body);
}

