package com.example.bottam_ex.main.dashboard.frgmnet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottam_ex.R;
import com.example.bottam_ex.data.model.DiseaseDetailModel;
import com.example.bottam_ex.data.network.DetailApi;
import com.example.bottam_ex.main.dashboard.adapter.DiseaseImageAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DiseaseDetailFragment extends Fragment {

    private static final String ARG_DETAIL_URL = "detailUrl";
    private String detailUrl;

    private TextView nameText, symptomsText, infectionRouteText, preventionText, noImageText;
    private RecyclerView imageRecycler;

    public static DiseaseDetailFragment newInstance(String detailUrl) {
        DiseaseDetailFragment fragment = new DiseaseDetailFragment();
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
        View view = inflater.inflate(R.layout.fragment_disease_detail, container, false);

        nameText = view.findViewById(R.id.nameText);
        symptomsText = view.findViewById(R.id.symptomsText);
        infectionRouteText = view.findViewById(R.id.infectionRouteText);
        preventionText = view.findViewById(R.id.preventionText);
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
        api.getDiseaseDetail(fullUrl).enqueue(new Callback<DiseaseDetailModel>() {
            @Override
            public void onResponse(Call<DiseaseDetailModel> call, Response<DiseaseDetailModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DiseaseDetailModel.Service s = response.body().service;

                    nameText.setText("병명: " + s.sickNameKor);
                    symptomsText.setText("증상: " + s.symptoms);
                    infectionRouteText.setText("전염경로: " + s.infectionRoute);
                    preventionText.setText("방제: " + s.preventionMethod);

                    List<String> imageUrls = new ArrayList<>();
                    List<String> imageTitles = new ArrayList<>();
                    if (s.imageList != null) {
                        for (DiseaseDetailModel.Service.ImageItem item : s.imageList) {
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
            public void onFailure(Call<DiseaseDetailModel> call, Throwable t) {
                nameText.setText("불러오기 실패: " + t.getMessage());
            }
        });
    }
}