package com.example.bottam_ex.main.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottam_ex.R;
import com.example.bottam_ex.data.network.ApiResponse;
import com.example.bottam_ex.data.network.ApiClient;
import com.example.bottam_ex.data.network.NcpmsApi;
import com.example.bottam_ex.main.dashboard.UnifiedSearchInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.inputmethod.InputMethodManager;
import android.content.Context;

public class DashboardFragment extends Fragment {

    private List<UnifiedSearchInfo> dataList;
    private UnifiedSearchAdapter adapter;
    private TextView statusText;
    private static final String API_KEY = "2025d70aa9a8d35aba68e74d477c42500c74";

    private int currentPage = 1;
    private int totalCount = 0;
    private boolean isLoading = false;
    private final int PAGE_SIZE = 50;
    private String query = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        EditText inputField = root.findViewById(R.id.inputField);
        Button searchButton = root.findViewById(R.id.searchButton);
        RecyclerView recyclerView = root.findViewById(R.id.resultRecyclerView);
        statusText = root.findViewById(R.id.statusText);
        statusText.setVisibility(View.GONE);

        dataList = new ArrayList<>();
        adapter = new UnifiedSearchAdapter(dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading && manager != null &&
                        manager.findLastCompletelyVisibleItemPosition() == dataList.size() - 1 &&
                        dataList.size() < totalCount) {

                    currentPage++;
                    loadData(query, currentPage);
                }
            }
        });

        searchButton.setOnClickListener(v -> {
            query = inputField.getText().toString().trim();
            if (!query.isEmpty()) {
                currentPage = 1;
                totalCount = 0;
                dataList.clear();
                adapter.notifyDataSetChanged();
                loadData(query, currentPage);
            }
        });

        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("detailUrl", item.detailUrl);
            Log.d("detailUrl", item.detailUrl);
            startActivity(intent);
        });

        return root;
    }

    private void searchUnified(String query) {
        NcpmsApi api = ApiClient.getApi();
        Call<ApiResponse> call = api.searchUnified(API_KEY, "SVC16", "AA003:JSON", query, 10, 1);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UnifiedSearchInfo> results = response.body().service != null
                            ? response.body().service.list : null;

                    dataList.clear();
                    if (results != null && !results.isEmpty()) {
                        dataList.addAll(results);
                        adapter.notifyDataSetChanged();
                        statusText.setVisibility(View.VISIBLE);
                        statusText.setText("총 " + results.size() + "건의 정보가 검색되었습니다.");
                    } else {
                        adapter.notifyDataSetChanged();
                        statusText.setVisibility(View.VISIBLE);
                        statusText.setText("검색 결과가 없습니다.");
                    }
                } else {
                    statusText.setVisibility(View.VISIBLE);
                    statusText.setText("API 응답 실패 또는 잘못된 요청입니다.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                statusText.setVisibility(View.VISIBLE);
                statusText.setText("API 호출 실패: " + t.getMessage());
            }
        });
    }

    private void loadData(String keyword, int page) {
        isLoading = true;
        int startPoint = 1 + (page - 1) * PAGE_SIZE;

        NcpmsApi api = ApiClient.getApi();
        Call<ApiResponse> call = api.searchUnified(
                API_KEY, "SVC16", "AA003:JSON", keyword, PAGE_SIZE, startPoint
        );

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading = false;

                if (response.isSuccessful() && response.body() != null && response.body().service != null) {
                    List<UnifiedSearchInfo> results = response.body().service.list;
                    totalCount = response.body().service.totalCount;
                    if (results != null) {
                        dataList.addAll(results);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading = false;
            }
        });
    }



}