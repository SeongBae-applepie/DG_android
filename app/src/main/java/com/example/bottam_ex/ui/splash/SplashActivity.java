package com.example.bottam_ex.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bottam_ex.data.model.TokenResponse;
import com.example.bottam_ex.data.model.User;
import com.example.bottam_ex.data.network.ApiService;
import com.example.bottam_ex.data.network.RetrofitClient;
import com.example.bottam_ex.main.MainActivity;
import com.example.bottam_ex.ui.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", null);
        String refreshToken = prefs.getString("refresh_token", null);

        Log.d("Splash", "Stored accessToken: " + accessToken);
        Log.d("Splash", "Stored refreshToken: " + refreshToken);

        if (accessToken != null) {
            checkAccessToken(accessToken, refreshToken);
        } else {
            goToLogin();
        }
    }

    private void checkAccessToken(String token, String refreshToken) {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getProfile("Bearer " + token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("Splash", "Access token profile check response: " + response.code());
                if (response.isSuccessful()) {
                    goToMain();
                } else if (response.code() == 401 && refreshToken != null) {
                    Log.d("Splash", "Access token expired. Attempting refresh...");
                    attemptRefreshToken(refreshToken);
                } else {
                    goToLogin();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Splash", "Access token check failed", t);
                goToLogin();
            }
        });
    }

    private void attemptRefreshToken(String refreshToken) {
        Log.d("Splash", "Sending refresh token to server...");

        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.refresh(body).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                Log.d("Splash", "Refresh token response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    String newAccessToken = response.body().accessToken;
                    prefs.edit().putString("access_token", newAccessToken).apply();
                    Log.d("Splash", "New access token saved: " + newAccessToken);
                    goToMain();
                } else {
                    Log.d("Splash", "Refresh failed or token invalid.");
                    goToLogin();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.e("Splash", "Refresh token request failed", t);
                goToLogin();
            }
        });
    }

    private void goToMain() {
        Log.d("Splash", "Navigating to MainActivity");
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void goToLogin() {
        Log.d("Splash", "Navigating to LoginActivity");
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }
}