package com.example.bottam_ex.main;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import com.example.bottam_ex.R;
import com.example.bottam_ex.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bottam_ex.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            // Redirect to login activity if not logged in
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Prevent user from returning to MainActivity without logging in
            return;
        }



        // (The rest of MainActivity code will not be executed until login is successful)
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}