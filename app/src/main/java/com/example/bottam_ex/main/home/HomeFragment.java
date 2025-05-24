package com.example.bottam_ex.main.home;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bottam_ex.R;
import com.example.bottam_ex.data.model.AlertItem;
import com.example.bottam_ex.main.home.GridUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HomeFragment extends Fragment {

    private Spinner citySpinner;
    private TextView temperatureText;
    private TextView locationAddress;
    private ImageView weatherIcon;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private final String SERVICE_KEY = "EqQiRcmZFDmLxN%2F6iv7vItXFhaBtYpkGgXmlVq035yI9nnklfXtPbj3G2Rc28lBp9dwbMmdz6EMEwMMDnnq6Og%3D%3D"; // 공공데이터포털 인증키

    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private final long REFRESH_INTERVAL_MS = 60 * 60 * 1000;

    private static final String PREF_KEY_HISTORY = "search_history";
    private static final int MAX_HISTORY_SIZE = 5;

    private SharedPreferences preferences;

    private RecyclerView alertRecyclerView;
    private AlertAdapter alertAdapter;

    private final List<AlertItem> alertList = new ArrayList<>();

    private final HashMap<String, double[]> locationCoordinates = new HashMap<String, double[]>() {{
        put("강남구", new double[]{37.4979, 127.0276});
        put("서초구", new double[]{37.4836, 127.0326});
        put("송파구", new double[]{37.5145, 127.1056});
        put("강동구", new double[]{37.5301, 127.1238});
        put("마포구", new double[]{37.5638, 126.9084});
        put("서대문구", new double[]{37.5792, 126.9368});
        put("은평구", new double[]{37.6027, 126.9291});
        put("용산구", new double[]{37.5323, 126.9906});
    }};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        citySpinner = view.findViewById(R.id.city_spinner);
        temperatureText = view.findViewById(R.id.temperature);
        weatherIcon = view.findViewById(R.id.weather_icon);
        locationAddress = view.findViewById(R.id.location_address);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        preferences = requireContext().getSharedPreferences("weather_prefs", 0);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }
        loadSearchHistory();

        alertRecyclerView = view.findViewById(R.id.alert_recycler_view);
        alertRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        alertAdapter = new AlertAdapter(alertList);
        alertRecyclerView.setAdapter(alertAdapter);

        fetchWeatherAlerts(() -> alertAdapter.notifyDataSetChanged());

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = parent.getItemAtPosition(position).toString();
                preferences.edit().putString("favorite_city", selectedCity).apply();
                saveSearchHistory(selectedCity);
                fetchWeatherFromLocationName(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        startAutoRefresh();
        return view;
    }

    private void fetchWeatherAlerts(Runnable onComplete) {
        new Thread(() -> {
            try {
                String urlStr = "https://apihub.kma.go.kr/api/typ01/url/wrn_now_data.php"
                        + "?fe=f&tm=&disp=0&help=1"
                        + "&authKey=hrQs0zeMSfq0LNM3jLn6RA"
                        + "&returnType=JSON";

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                rd.close();

                Log.d("WeatherAlertAPI", "응답 결과: " + result.toString());

                JSONObject root = new JSONObject(result.toString());
                if (root.has("data")) {
                    JSONArray items = root.getJSONArray("data");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject obj = items.getJSONObject(i);

                        String region = obj.optString("REG_KO", "지역미상");
                        String wrn = obj.optString("WRN", "특보");
                        String lvl = obj.optString("LVL", "주의보");


                        String title = region + " " + wrn + " (" + lvl + ")";
                        alertList.add(new AlertItem(lvl, title, "weather"));
                    }
                }

                requireActivity().runOnUiThread(onComplete);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                fetchAddressFromLatLng(lat, lon); // 주소 추출 추가
                fetchWeather(lat, lon);

            } else {
                Toast.makeText(requireContext(), "위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAddressFromLatLng(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.KOREA);
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locality = address.getLocality();
                String subLocality = address.getSubLocality();
                String adminArea = address.getAdminArea();
                locationAddress.setText(adminArea + " " + locality + " " + subLocality);
                setNearbyAreas(locality);
            } else {
                locationAddress.setText("주소 정보 없음");
            }
        } catch (Exception e) {
            e.printStackTrace();
            locationAddress.setText("주소 조회 실패");
        }
    }


    private void fetchWeatherFromLocationName(String city) {
        double lat = 37.5665, lon = 126.9780;
        if (locationCoordinates.containsKey(city)) {
            double[] coords = locationCoordinates.get(city);
            lat = coords[0];
            lon = coords[1];
        }
        fetchWeather(lat, lon);
    }

    private void fetchWeather(double lat, double lon) {
        GridUtil.LatXLngY grid = GridUtil.convertGRID(lat, lon);
        String baseDate = new SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(new Date());
        String baseTime = getBaseTime();

        String urlStr = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"
                + "?serviceKey=" + SERVICE_KEY + "&numOfRows=10&pageNo=1&dataType=JSON"
                + "&base_date=" + baseDate + "&base_time=" + baseTime
                + "&nx=" + grid.x + "&ny=" + grid.y;

        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) result.append(line);

                JSONObject json = new JSONObject(result.toString());
                JSONArray items = json.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

                String skyValue = "", ptyValue = "", temperatureValue = "";
                for (int i = 0; i < items.length(); i++) {
                    JSONObject obj = items.getJSONObject(i);
                    String category = obj.getString("category");
                    String value = obj.getString("obsrValue");
                    switch (category) {
                        case "T1H":
                            temperatureValue = value;
                            break;
                        case "SKY":
                            skyValue = value;
                            break;
                        case "PTY":
                            ptyValue = value;
                            break;
                    }
                }

                String finalTemp = temperatureValue;
                String finalSky = skyValue;
                String finalPty = ptyValue;

                requireActivity().runOnUiThread(() -> {
                    temperatureText.setText(finalTemp + "℃");
                    updateWeatherIcon(finalSky, finalPty);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private String getBaseTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (minute < 45) hour -= 1;
        if (hour < 0) hour = 23;
        return String.format(Locale.KOREA, "%02d00", hour);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateWeatherIcon(String sky, String pty) {
        if (!pty.equals("0")) {
            switch (pty) {
                case "1":
                case "2":
                    weatherIcon.setImageResource(R.drawable.ic_rain);
                    return;
                case "3":
                    weatherIcon.setImageResource(R.drawable.ic_snow);
                    return;
                case "4":
                    weatherIcon.setImageResource(R.drawable.ic_thunder);
                    return;
            }
        }

        switch (sky) {
            case "1":
                weatherIcon.setImageResource(R.drawable.ic_sunny);
                break;
            case "4":
                weatherIcon.setImageResource(R.drawable.ic_cloud);
                break;
            default:
                weatherIcon.setImageResource(R.drawable.ic_sunny);
                break;
        }
    }
    private void startAutoRefresh() {
        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String savedCity = preferences.getString("favorite_city", null);
                if (savedCity != null) {
                    fetchWeatherFromLocationName(savedCity);
                } else {
                    getCurrentLocation();
                }
                refreshHandler.postDelayed(this, REFRESH_INTERVAL_MS);
            }
        }, REFRESH_INTERVAL_MS);
    }

    private void setNearbyAreas(String currentLocality) {
        ArrayList<String> areaList = new ArrayList<>();

        if (currentLocality == null || currentLocality.isEmpty()) {
            currentLocality = "서울";
        }

        switch (currentLocality) {
            case "강남구":
                areaList.add("강남구"); areaList.add("서초구"); areaList.add("송파구"); areaList.add("강동구"); break;
            case "마포구":
                areaList.add("마포구"); areaList.add("서대문구"); areaList.add("은평구"); areaList.add("용산구"); break;
            default:
                areaList.add(currentLocality);
                areaList.add("서울"); areaList.add("부산"); areaList.add("대전");
                break;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, areaList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);
    }

    private void saveSearchHistory(String city) {
        Set<String> historySet = preferences.getStringSet(PREF_KEY_HISTORY, new LinkedHashSet<>());
        LinkedHashSet<String> updated = new LinkedHashSet<>(historySet);
        updated.remove(city); // 중복 제거
        updated.add(city); // 최근 항목으로 추가

        // 최근 검색 최대 5개 제한
        while (updated.size() > MAX_HISTORY_SIZE) {
            Iterator<String> it = updated.iterator();
            it.next();
            it.remove();
        }

        preferences.edit().putStringSet(PREF_KEY_HISTORY, updated).apply();
    }

    private void loadSearchHistory() {
        Set<String> history = preferences.getStringSet(PREF_KEY_HISTORY, null);
        if (history != null && !history.isEmpty()) {
            ArrayList<String> historyList = new ArrayList<>(history);
            Collections.reverse(historyList); // 최신순 정렬

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, historyList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            citySpinner.setAdapter(adapter);
        }
    }
}