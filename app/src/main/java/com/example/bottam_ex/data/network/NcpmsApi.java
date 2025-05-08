package com.example.bottam_ex.data.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NcpmsApi {
    @GET("npmsAPI/service")
    Call<ApiResponse> searchUnified(
            @Query("apiKey") String apiKey,
            @Query("serviceCode") String serviceCode,
            @Query("serviceType") String serviceType,
            @Query("searchName") String searchName,
            @Query("displayCount") int displayCount,
            @Query("startPoint") int startPoint
    );
}
