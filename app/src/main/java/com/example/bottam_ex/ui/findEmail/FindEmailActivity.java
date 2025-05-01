package com.example.bottam_ex.ui.findEmail;
// FindEmailActivity.java

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bottam_ex.R;
import com.example.bottam_ex.data.model.CheckEmailResponse;
import com.example.bottam_ex.data.network.ApiService;
import com.example.bottam_ex.data.network.RetrofitClient;
import com.example.bottam_ex.ui.singup.SingupActivity;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindEmailActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button checkEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_email);

        emailInput = findViewById(R.id.editTextFindEmail);
        checkEmailButton = findViewById(R.id.buttonCheckEmail);

        checkEmailButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
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
                            Toast.makeText(FindEmailActivity.this, "아이디가 존재 합니다.", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(FindEmailActivity.this, "아이디가 없습니다. 회원가입을 진행해 주세요.", Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                @Override
                public void onFailure(Call<CheckEmailResponse> call, Throwable t) {
                    Toast.makeText(FindEmailActivity.this, "중복 확인 실패", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}