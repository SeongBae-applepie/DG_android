package com.example.bottam_ex.main.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bottam_ex.R;
import com.example.bottam_ex.ui.login.LoginActivity;

public class SettingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        Button logoutButton = root.findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(v -> logout());

        Switch darkModeSwitch = root.findViewById(R.id.switch_darkmode);

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        darkModeSwitch.setChecked(currentNightMode == Configuration.UI_MODE_NIGHT_YES);

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        return root;
    }

    private void logout() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        prefs.edit()
                .remove("access_token")
                .remove("refresh_token")
                .apply();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void openSettingsDetails() {
        Toast.makeText(getContext(), "설정 상세 보기 클릭", Toast.LENGTH_SHORT).show();
        // 설정 상세 화면으로 이동할 경우 여기서 Intent 추가할 수 있습니다.
    }
}