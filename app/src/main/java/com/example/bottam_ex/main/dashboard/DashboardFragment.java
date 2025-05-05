package com.example.bottam_ex.main.dashboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.bottam_ex.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class DashboardFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_REQ_CODE = 1001;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // 1) 위치 서비스 초기화
        fusedLocationClient = LocationServices
                .getFusedLocationProviderClient(requireContext());

        // 2) 권한 확인 & 요청 또는 바로 지도 로딩
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청
            requestPermissions(
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    LOCATION_REQ_CODE
            );
        } else {
            loadPredictionMap(root);
        }

        return root;
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQ_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            View root = getView();
            if (root != null) {
                loadPredictionMap(root);
            }
        }
    }

    // WebView에 예측지도 로드
    private void loadPredictionMap(View root) {
        // 1) 다시 한 번 위치 권한 체크
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) return;

            double lat = location.getLatitude();
            double lng = location.getLongitude();

            WebView webView = root.findViewById(R.id.webview_map);
            WebSettings ws = webView.getSettings();

            // ★ 필수 설정 추가
            ws.setJavaScriptEnabled(true);
            ws.setDomStorageEnabled(true);  // DOMStorage 허용
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                // HTTP 스크립트 로딩 허용
            }

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                    Log.e("DashboardFragment", "WebView error: " + err.getDescription());
                }
            });

            // 2) HTML 생성 (setCoordinateZoom 인자도 분리)
            String html = "<!DOCTYPE html><html><head>"
                    + "<meta charset='UTF-8'><title>병해충 예측지도</title>"
                    // 절대 URL 사용하므로 baseUrl이 동작하게끔 고정
                    + "<script src='http://ncpms.rda.go.kr/npmsAPI/api/openapiFore.jsp'></script>"
                    + "<script>"
                    + "npmsJ(document).ready(function(){"
                    + "  setNpmsOpenApiKey('2025d70aa9a8d35aba68e74d477c42500c74');"
                    + "  setNpmsOpenApiServiceCode('SVC31');"
                    + "  setNpmsOpenApiProxyUrl('https://aidoctorgreen.com/openapiFore_ajax_callback.jsp');"
                    + "  setNpmsOpenAPIWidth(1000);"
                    + "  setCoordinateZoom('" + lat + "', '" + lng + "', 15);"   // 위도·경도 분리
                    + "  setCropList(['FC010101']);"
                    + "  setMoveMatAt(true);"
                    + "  actionMapInfo('mapContainer');"
                    + "});"
                    + "</script></head><body>"
                    + "<div id='mapContainer' style='width:100%; height:100%;'></div>"
                    + "</body></html>";

            // 3) baseUrl을 꼭 지정해서 HTTP 스크립트가 실행될 수 있도록
            webView.loadDataWithBaseURL(
                    "http://ncpms.rda.go.kr",  // 중요: 스크립트 로드 도메인과 동일
                    html,
                    "text/html",
                    "UTF-8",
                    null
            );
        });
    }
}