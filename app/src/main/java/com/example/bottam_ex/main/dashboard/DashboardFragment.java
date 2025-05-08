package com.example.bottam_ex.main.dashboard;

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

        searchButton.setOnClickListener(v -> {
            String query = inputField.getText().toString().trim();

            if (!query.isEmpty()) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputField.getWindowToken(), 0);
                searchUnified(query);
            }
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
}