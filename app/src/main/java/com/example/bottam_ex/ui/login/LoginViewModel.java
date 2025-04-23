package com.example.bottam_ex.ui.login;

import androidx.lifecycle.ViewModel;

import com.example.bottam_ex.data.model.LoginRequest;
import com.example.bottam_ex.data.model.LoginResponse;
import com.example.bottam_ex.data.network.ApiService;
import com.example.bottam_ex.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    public interface LoginCallback {
        void onResult(String token, String error);
    }

    public void login(String email, String password, LoginCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.onResult(null, "이메일과 비밀번호를 모두 입력해주세요.");
            return;
        }

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.login(new LoginRequest(email, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(response.body().getToken(), null);
                } else {
                    callback.onResult(null, "로그인 실패: 이메일 또는 비밀번호가 틀립니다.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onResult(null, "서버 오류: " + t.getMessage());
            }
        });
    }
}