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
import com.example.bottam_ex.data.model.OtherInsectDetailModel;
import com.example.bottam_ex.data.network.DetailApi;
import com.example.bottam_ex.main.dashboard.adapter.DiseaseImageAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OtherInsectDetailFragment extends Fragment {

    private static final String ARG_DETAIL_URL = "detailUrl";
    private String detailUrl;

    private TextView cropNameText, insectNameText, sciNameText, shapeText, ecologyText, cropsText, noImageText;
    private RecyclerView imageRecycler;

    public static OtherInsectDetailFragment newInstance(String detailUrl) {
        OtherInsectDetailFragment fragment = new OtherInsectDetailFragment();
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
        View view = inflater.inflate(R.layout.fragment_other_insect_detail, container, false);

        cropNameText = view.findViewById(R.id.cropNameText);
        insectNameText = view.findViewById(R.id.insectNameText);
        sciNameText = view.findViewById(R.id.sciNameText);
        shapeText = view.findViewById(R.id.shapeText);
        ecologyText = view.findViewById(R.id.ecologyText);
        cropsText = view.findViewById(R.id.cropsText);
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
        api.getOtherInsectDetail(fullUrl).enqueue(new Callback<OtherInsectDetailModel>() {
            @Override
            public void onResponse(Call<OtherInsectDetailModel> call, Response<OtherInsectDetailModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OtherInsectDetailModel.Service s = response.body().service;

                    cropNameText.setText("작물명: " + s.wrongedCrop);
                    insectNameText.setText("곤충명: " + s.insectSpeciesKor);
                    sciNameText.setText("학명: " + s.insectGenus + " " + s.insectSpecies);
                    shapeText.setText("형태 정보: " + s.shapeInfo);
                    ecologyText.setText("생태 정보: " + s.ecologyInfo);
                    cropsText.setText("피해 작물: " + s.wrongedCrop);

                    List<String> imageUrls = new ArrayList<>();
                    List<String> imageTitles = new ArrayList<>();
                    if (s.imageList != null) {
                        for (OtherInsectDetailModel.Service.ImageItem item : s.imageList) {
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
            public void onFailure(Call<OtherInsectDetailModel> call, Throwable t) {
                cropNameText.setText("불러오기 실패: " + t.getMessage());
            }
        });
    }
}

