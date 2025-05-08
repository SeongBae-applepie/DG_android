package com.example.bottam_ex.data.network;


import com.example.bottam_ex.main.dashboard.UnifiedSearchInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ApiResponse {
    @SerializedName("service")
    public ServiceWrapper service;
    public Map<String, List<UnifiedSearchInfo>> items;

    public static class ServiceWrapper {
        @SerializedName("list")
        public List<UnifiedSearchInfo> list;
    }
}