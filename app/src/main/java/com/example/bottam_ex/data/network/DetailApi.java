package com.example.bottam_ex.data.network;

import com.example.bottam_ex.data.model.DiseaseDetailModel;
import com.example.bottam_ex.data.model.EnemyInsectDetailModel;
import com.example.bottam_ex.data.model.OtherInsectDetailModel;
import com.example.bottam_ex.data.model.PathogenDetailModel;
import com.example.bottam_ex.data.model.PestInsectDetailModel;
import com.example.bottam_ex.data.model.WeedDetailModel;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface DetailApi {
    @GET
    Call<JsonObject> getDetail(@Url String fullUrl); // 전체 URL 전달
    @GET
    Call<DiseaseDetailModel> getDiseaseDetail(@Url String url);
    @GET
    Call<PathogenDetailModel> getPathogenDetail(@Url String url);
    @GET
    Call<PestInsectDetailModel> getpestInsectDetail(@Url String url);
    @GET
    Call<OtherInsectDetailModel> getOtherInsectDetail(@Url String url);
    @GET
    Call<EnemyInsectDetailModel> getEnemyInsectDetail(@Url String url);
    @GET
    Call<WeedDetailModel> getWeedDetail(@Url String url);
}
