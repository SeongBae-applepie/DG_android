package com.example.bottam_ex.ui.reserpassword;


import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bottam_ex.R;
import com.example.bottam_ex.data.model.CheckEmailResponse;
import com.example.bottam_ex.data.network.ApiService;
import com.example.bottam_ex.data.network.RetrofitClient;
import com.example.bottam_ex.ui.findEmail.FindEmailActivity;
import com.example.bottam_ex.ui.singup.SingupActivity;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailInput, codeInput, newPasswordInput;
    private Button sendCodeButton, verifyCodeButton, resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailInput = findViewById(R.id.editTextResetEmail);
        codeInput = findViewById(R.id.editTextVerificationCode);
        newPasswordInput = findViewById(R.id.editTextNewPassword);

        sendCodeButton = findViewById(R.id.buttonSendCode);
        verifyCodeButton = findViewById(R.id.buttonVerifyCode);
        resetPasswordButton = findViewById(R.id.buttonResetPassword);

        sendCodeButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else {

                Map<String, String> body = new HashMap<>();
                body.put("email", email);

                ApiService api = RetrofitClient.getInstance().create(ApiService.class);
                api.checkEmail(body).enqueue(new Callback<CheckEmailResponse>() {
                    @Override
                    public void onResponse(Call<CheckEmailResponse> call, Response<CheckEmailResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isDuplicate) {
                                //이메일 존재 인증번호 전송
                                sendVerificationEmail(email);
                            } else {
                                Toast.makeText(ResetPasswordActivity.this, "아이디가 없습니다. 회원가입을 진행해 주세요.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckEmailResponse> call, Throwable t) {
                        Toast.makeText(ResetPasswordActivity.this, "중복 확인 실패", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(this, "이메일로 인증 코드를 전송했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        verifyCodeButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String code = codeInput.getText().toString().trim();

            if (email.isEmpty() || code.isEmpty()) {
                Toast.makeText(this, "이메일과 인증 코드를 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> body = new HashMap<>();
            body.put("email", email);
            body.put("code", code);

            ApiService api = RetrofitClient.getInstance().create(ApiService.class);
            api.verifyCode(body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d("VerifyCode", "POST 성공: code=" + response.code());
                        Toast.makeText(ResetPasswordActivity.this, "인증이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("VerifyCode", "POST 실패: code=" + response.code());
                        Toast.makeText(ResetPasswordActivity.this, "인증 코드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(ResetPasswordActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
                }
            });
        });

        resetPasswordButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();

            if (email.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else {
                // 서버에 비밀번호 변경 요청
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("email", email);
                requestBody.put("newPassword", newPassword);

                ApiService api = RetrofitClient.getInstance().create(ApiService.class);
                api.resetPassword(requestBody).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this, "변경 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "변경 실패", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ResetPasswordActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void sendVerificationEmail(String email) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.sendVerification(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("sendVerification", "POST 성공: code=" + response.code());
                    Toast.makeText(ResetPasswordActivity.this, "이메일로 인증 코드가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("sendVerification", "POST 실패: code=" + response.code());
                    Toast.makeText(ResetPasswordActivity.this, "인증 메일 발송 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "서버 오류로 인증 메일 발송 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
