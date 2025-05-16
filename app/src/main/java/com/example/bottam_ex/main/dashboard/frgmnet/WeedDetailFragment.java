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
import com.example.bottam_ex.data.model.WeedDetailModel;
import com.example.bottam_ex.data.network.DetailApi;
import com.example.bottam_ex.main.dashboard.adapter.DiseaseImageAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class WeedDetailFragment extends Fragment {

    private static final String ARG_DETAIL_URL = "detailUrl";
    private String detailUrl;

    private TextView nameText, sciNameText, familyText, habitatText, lifeFormText,
            ecologyText, shapeText, literatureText, noImageText;
    private RecyclerView imageRecycler;

    public static WeedDetailFragment newInstance(String detailUrl) {
        WeedDetailFragment fragment = new WeedDetailFragment();
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
        View view = inflater.inflate(R.layout.fragment_weed_detail, container, false);

        nameText = view.findViewById(R.id.weedNameText);
        sciNameText = view.findViewById(R.id.sciNameText);
        familyText = view.findViewById(R.id.familyText);
        habitatText = view.findViewById(R.id.habitatText);
        lifeFormText = view.findViewById(R.id.lifeFormText);
        ecologyText = view.findViewById(R.id.ecologyText);
        shapeText = view.findViewById(R.id.shapeText);
        literatureText = view.findViewById(R.id.literatureText);
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
        api.getWeedDetail(fullUrl).enqueue(new Callback<WeedDetailModel>() {
            @Override
            public void onResponse(Call<WeedDetailModel> call, Response<WeedDetailModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeedDetailModel.Service s = response.body().service;

                    nameText.setText("잡초명: " + s.weedsKorName);
                    sciNameText.setText("학명: " + s.weedsScientificName);
                    familyText.setText("과명: " + s.weedsFamily);
                    habitatText.setText("서식지: " + s.weedsHabitat);
                    lifeFormText.setText("생육형: " + s.weedsLifeForm);
                    ecologyText.setText("생태 정보: " + s.weedsEcology);
                    shapeText.setText("형태 정보: " + s.weedsShape);
                    literatureText.setText("문헌: " + s.literature);

                    List<String> imageUrls = new ArrayList<>();
                    List<String> imageTitles = new ArrayList<>();
                    if (s.imageList != null) {
                        for (WeedDetailModel.Service.ImageItem item : s.imageList) {
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
            public void onFailure(Call<WeedDetailModel> call, Throwable t) {
                nameText.setText("불러오기 실패: " + t.getMessage());
            }
        });
    }
}
