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
import com.example.bottam_ex.data.model.PestInsectDetailModel;
import com.example.bottam_ex.data.network.DetailApi;
import com.example.bottam_ex.main.dashboard.adapter.DiseaseImageAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class PestInsectDetailFragment extends Fragment {

    private static final String ARG_DETAIL_URL = "detailUrl";
    private String detailUrl;

    private TextView cropNameText, speciesKorText, sciNameText, orderText, familyText,
            genusText, damageText, preventText, ecologyText, stleText, noImageText;
    private RecyclerView imageRecycler;

    public static PestInsectDetailFragment newInstance(String detailUrl) {
        PestInsectDetailFragment fragment = new PestInsectDetailFragment();
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
        View view = inflater.inflate(R.layout.fragment_insect_detail, container, false);

        cropNameText = view.findViewById(R.id.cropNameText);
        speciesKorText = view.findViewById(R.id.speciesKorText);
        sciNameText = view.findViewById(R.id.sciNameText);
        orderText = view.findViewById(R.id.insectOrderText);
        familyText = view.findViewById(R.id.insectFamilyText);
        genusText = view.findViewById(R.id.insectGenusText);
        damageText = view.findViewById(R.id.damageText);
        preventText = view.findViewById(R.id.preventText);
        ecologyText = view.findViewById(R.id.ecologyText);
        stleText = view.findViewById(R.id.stleText);
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
        api.getpestInsectDetail(fullUrl).enqueue(new Callback<PestInsectDetailModel>() {
            @Override
            public void onResponse(Call<PestInsectDetailModel> call, Response<PestInsectDetailModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PestInsectDetailModel.Service s = response.body().service;

                    cropNameText.setText("작물명: " + s.cropName);
                    speciesKorText.setText("해충명: " + s.insectSpeciesKor);
                    sciNameText.setText("학명: " + s.insectGenus + " " + s.insectSpecies);
                    orderText.setText("목: " + s.insectOrder);
                    familyText.setText("과: " + s.insectFamily);
                    genusText.setText("속: " + s.insectGenus);
                    damageText.setText("피해 내용: " + s.damageInfo);
                    preventText.setText("방제 방법: " + s.preventMethod);
                    ecologyText.setText("생태 정보: " + s.ecologyInfo);
                    stleText.setText("형태 정보: " + s.stleInfo);

                    List<String> imageUrls = new ArrayList<>();
                    List<String> imageTitles = new ArrayList<>();

                    if (s.imageList != null) {
                        for (PestInsectDetailModel.Service.ImageItem item : s.imageList) {
                            imageUrls.add(item.image);
                            imageTitles.add(item.imageTitle);
                        }
                    }

                    if (s.spcsPhotoData != null) {
                        for (PestInsectDetailModel.Service.PhotoItem item : s.spcsPhotoData) {
                            imageUrls.add(item.image);
                            imageTitles.add(item.photoSubject + " (" + item.stageName + ")");
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
            public void onFailure(Call<PestInsectDetailModel> call, Throwable t) {
                cropNameText.setText("불러오기 실패: " + t.getMessage());
            }
        });
    }
}
