package com.example.bottam_ex.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bottam_ex.main.MainActivity;
import com.example.bottam_ex.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        if (token != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}