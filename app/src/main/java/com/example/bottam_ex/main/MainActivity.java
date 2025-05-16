package com.example.bottam_ex.main;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottam_ex.R;
import com.example.bottam_ex.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bottam_ex.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.bottomNavigationView;


        // NavController 연결
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navView.setElevation(0f);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_searchplent,
                R.id.nav_myplent,
                R.id.nav_setting
        ).build();

        NavigationUI.setupWithNavController(navView, navController);


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}