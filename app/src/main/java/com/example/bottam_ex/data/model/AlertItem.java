package com.example.bottam_ex.data.model;

public class AlertItem {
    private final String type;     // "경보", "주의보"
    private final String title;    // 예: "서울 호우경보 (경보)"
    private final String source;   // "weather" 등

    public AlertItem(String type, String title, String source) {
        this.type = type;
        this.title = title;
        this.source = source;
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getSource() { return source; }
}