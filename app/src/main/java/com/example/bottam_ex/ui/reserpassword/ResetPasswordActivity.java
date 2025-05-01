package com.example.bottam_ex.ui.reserpassword;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bottam_ex.R;

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
                // 서버에 인증 코드 전송 요청
                Toast.makeText(this, "이메일로 인증 코드를 전송했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        verifyCodeButton.setOnClickListener(v -> {
            String code = codeInput.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "인증 코드를 입력해주세요", Toast.LENGTH_SHORT).show();
            } else {
                // 서버에 인증 코드 확인 요청
                Toast.makeText(this, "코드 확인 완료.", Toast.LENGTH_SHORT).show();
            }
        });

        resetPasswordButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String code = codeInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();

            if (email.isEmpty() || code.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else {
                // 서버에 비밀번호 변경 요청
                Toast.makeText(this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
