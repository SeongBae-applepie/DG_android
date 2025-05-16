package com.example.bottam_ex.main.dashboard.frgmnet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bottam_ex.R;
import com.example.bottam_ex.data.model.PathogenDetailModel;
import com.example.bottam_ex.data.network.DetailApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PathogenDetailFragment extends Fragment {

    private static final String ARG_DETAIL_URL = "detailUrl";
    private String detailUrl;

    private TextView cropNameText, sickNameText, virusNameText, gensText, spcsText, groupText,
            authorText, relateText, charText, qrantText, remark1Text, remark2Text;

    public static PathogenDetailFragment newInstance(String detailUrl) {
        PathogenDetailFragment fragment = new PathogenDetailFragment();
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
        View view = inflater.inflate(R.layout.fragment_pathogen_detail, container, false);

        cropNameText = view.findViewById(R.id.cropNameText);
        sickNameText = view.findViewById(R.id.sickNameText);
        virusNameText = view.findViewById(R.id.virusNameText);
        gensText = view.findViewById(R.id.virusGensText);
        spcsText = view.findViewById(R.id.virusSpcsText);
        groupText = view.findViewById(R.id.virusGroupText);
        authorText = view.findViewById(R.id.virusAuthorText);
        relateText = view.findViewById(R.id.virusRelateText);
        charText = view.findViewById(R.id.virusCharText);
        qrantText = view.findViewById(R.id.qrantText);
        remark1Text = view.findViewById(R.id.remark1Text);
        remark2Text = view.findViewById(R.id.remark2Text);

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
        api.getPathogenDetail(fullUrl).enqueue(new Callback<PathogenDetailModel>() {
            @Override
            public void onResponse(Call<PathogenDetailModel> call, Response<PathogenDetailModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PathogenDetailModel.Service s = response.body().service;

                    cropNameText.setText("작물명: " + s.cropName);
                    sickNameText.setText("병명: " + s.sickNameKor);
                    virusNameText.setText("병원체명: " + s.virusName);
                    gensText.setText("속명: " + s.virusGens);
                    spcsText.setText("종명: " + s.virusSpcs);
                    groupText.setText("분류군: " + s.virusGroup);
                    authorText.setText("명명자: " + s.virusAuthor);
                    relateText.setText("관련성: " + s.virusRelate);
                    charText.setText("특징: " + s.virusCharacteristic);
                    qrantText.setText("검역정보: " + s.qrantInfo);
                    remark1Text.setText("비고1: " + s.remark1);
                    remark2Text.setText("비고2: " + s.remark2);
                }
            }

            @Override
            public void onFailure(Call<PathogenDetailModel> call, Throwable t) {
                cropNameText.setText("불러오기 실패: " + t.getMessage());
            }
        });
    }
}