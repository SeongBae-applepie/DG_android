package com.example.bottam_ex.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bottam_ex.R;
import com.example.bottam_ex.data.model.LoginRequest;
import com.example.bottam_ex.data.model.LoginResponse;
import com.example.bottam_ex.data.model.User;
import com.example.bottam_ex.data.network.ApiService;
import com.example.bottam_ex.data.network.RetrofitClient;
import com.example.bottam_ex.main.MainActivity;
import com.example.bottam_ex.ui.findEmail.FindEmailActivity;
import com.example.bottam_ex.ui.reserpassword.ResetPasswordActivity;
import com.example.bottam_ex.ui.singup.SingupActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private TextView signupText, findEmailText, resetPasswordText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.editTextEmail);
        passwordField = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        signupText = findViewById(R.id.textViewSignUp);
        findEmailText = findViewById(R.id.textFindEmail);
        resetPasswordText = findViewById(R.id.textResetPassword);

        // 로그인 시도
        loginButton.setOnClickListener(v -> attemptLogin());

        // 회원가입 화면으로 이동
        signupText.setOnClickListener(v -> {
            startActivity(new Intent(this, SingupActivity.class));
        });

        // 아이디(이메일) 찾기 화면으로 이동
        findEmailText.setOnClickListener(v -> {
            startActivity(new Intent(this, FindEmailActivity.class));
        });

        // 비밀번호 재설정 화면으로 이동
        resetPasswordText.setOnClickListener(v -> {
            startActivity(new Intent(this, ResetPasswordActivity.class));
        });
    }

    private void attemptLogin() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);

                    String accessToken = response.body().getAccessToken();
                    String refreshToken = response.body().getRefreshToken();

                    prefs.edit()
                            .putString("access_token", accessToken)
                            .putString("refresh_token", refreshToken)
                            .apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

