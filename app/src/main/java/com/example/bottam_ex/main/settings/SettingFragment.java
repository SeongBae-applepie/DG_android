package com.example.bottam_ex.main.settings;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bottam_ex.R;
import com.example.bottam_ex.ui.login.LoginActivity;

public class SettingFragment extends Fragment {

    private SettingViewModel settingsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        settingsViewModel = new ViewModelProvider(this).get(SettingViewModel.class);

        return root;
    }

    private void logout() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        prefs.edit().remove("jwt_token").apply();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}