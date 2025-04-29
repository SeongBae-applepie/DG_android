package com.example.bottam_ex.ui.singup;

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

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingupActivity extends AppCompatActivity {

    private EditText editEmail, editPassword, editVerificationCode;
    private Button signupButton, checkEmailButton, confirmCodeButton;
    private boolean isEmailChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        signupButton = findViewById(R.id.buttonSignup);
        checkEmailButton = findViewById(R.id.buttonCheckEmail);
        editVerificationCode = findViewById(R.id.editTextVerificationCode);
        confirmCodeButton = findViewById(R.id.buttonConfirmCode);

        confirmCodeButton.setEnabled(false);

        signupButton.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isEmailChecked) {
                Toast.makeText(this, "이메일 인증을 완료해주세요", Toast.LENGTH_SHORT).show();
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

        checkEmailButton.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> body = new HashMap<>();
            body.put("email", email);

            ApiService api = RetrofitClient.getInstance().create(ApiService.class);
            api.checkEmail(body).enqueue(new Callback<CheckEmailResponse>() {
                @Override
                public void onResponse(Call<CheckEmailResponse> call, Response<CheckEmailResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isDuplicate) {
                            Toast.makeText(SingupActivity.this, "이미 등록된 이메일입니다.", Toast.LENGTH_SHORT).show();
                            isEmailChecked = false;
                            confirmCodeButton.setEnabled(false);
                        } else {
                            Toast.makeText(SingupActivity.this, "사용 가능한 이메일입니다. 인증 메일을 전송합니다.", Toast.LENGTH_SHORT).show();
                            isEmailChecked = true;
                            confirmCodeButton.setEnabled(true);
                            sendVerificationEmail(email);
                        }
                    }
                }

                @Override
                public void onFailure(Call<CheckEmailResponse> call, Throwable t) {
                    Toast.makeText(SingupActivity.this, "중복 확인 실패", Toast.LENGTH_SHORT).show();
                    isEmailChecked = false;
                    confirmCodeButton.setEnabled(false);
                }
            });
        });

        confirmCodeButton.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String code = editVerificationCode.getText().toString().trim();

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
                        Toast.makeText(SingupActivity.this, "인증이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                        isEmailChecked = true; // 인증 완료로 상태 갱신
                    } else {
                        Log.d("VerifyCode", "POST 실패: code=" + response.code());
                        Toast.makeText(SingupActivity.this, "인증 코드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        isEmailChecked = false;
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(SingupActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
                    isEmailChecked = false;
                }
            });
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
                    Toast.makeText(SingupActivity.this, "이메일로 인증 코드가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("sendVerification", "POST 실패: code=" + response.code());
                    Toast.makeText(SingupActivity.this, "인증 메일 발송 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SingupActivity.this, "서버 오류로 인증 메일 발송 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
