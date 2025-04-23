package com.example.bottam_ex.ui.singup;

import android.content.Intent;
import android.content.SharedPreferences;

import com.example.bottam_ex.main.MainActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bottam_ex.R;
import com.example.bottam_ex.data.network.ApiService;
import com.example.bottam_ex.data.network.RetrofitClient;


import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingupActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", null);
        if (accessToken != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_singup);

        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        signupButton = findViewById(R.id.buttonSignup);

        signupButton.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("email", email);
            requestBody.put("password", password);

            ApiService api = RetrofitClient.getInstance().create(ApiService.class);
            api.signup(requestBody).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SingupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(SingupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(SingupActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}