package com.example.bottam_ex.main.dashboard.frgmnet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottam_ex.R;
import com.example.bottam_ex.data.model.EnemyInsectDetailModel;
import com.example.bottam_ex.data.network.DetailApi;
import com.example.bottam_ex.main.dashboard.adapter.DiseaseImageAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnemyInsectDetailFragment extends Fragment {

    private static final String ARG_DETAIL_URL = "detailUrl";
    private String detailUrl;

    private TextView nameText, cropText, featureText, lifecycleText, utilizationText, noImageText;
    private RecyclerView imageRecycler;

    public static EnemyInsectDetailFragment newInstance(String detailUrl) {
        EnemyInsectDetailFragment fragment = new EnemyInsectDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DETAIL_URL, detailUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            detailUrl = getArguments().getString(ARG_DETAIL_URL);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enemy_insect_detail, container, false);

        nameText = view.findViewById(R.id.insectNameText);
        cropText = view.findViewById(R.id.cropText);
        featureText = view.findViewById(R.id.featureText);
        lifecycleText = view.findViewById(R.id.lifecycleText);
        utilizationText = view.findViewById(R.id.utilizationText);
        noImageText = view.findViewById(R.id.noImageText);
        imageRecycler = view.findViewById(R.id.imageRecycler);
        imageRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        fetchDetail();
        return view;
    }

    private void fetchDetail() {
        String fullUrl = "http://ncpms.rda.go.kr/npmsAPI/service?" + detailUrl;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ncpms.rda.go.kr/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DetailApi api = retrofit.create(DetailApi.class);
        api.getEnemyInsectDetail(fullUrl).enqueue(new Callback<EnemyInsectDetailModel>() {
            @Override
            public void onResponse(Call<EnemyInsectDetailModel> call, Response<EnemyInsectDetailModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EnemyInsectDetailModel.Service s = response.body().service;

                    nameText.setText("곤충명: " + s.insectSpeciesKor);
                    cropText.setText("관련 작물: " + s.cropName);
                    featureText.setText("특징: " + s.feature);
                    lifecycleText.setText("생활사: " + s.lifeCycle);
                    utilizationText.setText("이용법: " + s.utilizationMethod);

                    List<String> imageUrls = new ArrayList<>();
                    List<String> imageTitles = new ArrayList<>();

                    if (s.ptzoneImg != null) {
                        for (EnemyInsectDetailModel.ImageItem item : s.ptzoneImg) {
                            imageUrls.add(item.image);
                            imageTitles.add(item.imageTitle);
                        }
                    }

                    if (imageUrls.isEmpty()) {
                        noImageText.setVisibility(View.VISIBLE);
                        imageRecycler.setVisibility(View.GONE);
                    } else {
                        noImageText.setVisibility(View.GONE);
                        imageRecycler.setVisibility(View.VISIBLE);
                        imageRecycler.setAdapter(new DiseaseImageAdapter(imageUrls, imageTitles));
                    }
                }
            }

            @Override
            public void onFailure(Call<EnemyInsectDetailModel> call, Throwable t) {
                nameText.setText("불러오기 실패: " + t.getMessage());
            }
        });
    }
}
