package com.example.bottam_ex.main.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingViewModel extends ViewModel {

    private final MutableLiveData<String> infoText;

    public SettingViewModel() {
        infoText = new MutableLiveData<>();
        infoText.setValue("설정 화면입니다.");
    }

    public LiveData<String> getInfoText() {
        return infoText;
    }

    // 로그아웃, 설정 저장 등의 추가 기능을 여기에 작성 가능
}