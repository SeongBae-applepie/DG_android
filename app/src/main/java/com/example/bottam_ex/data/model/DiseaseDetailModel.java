package com.example.bottam_ex.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DiseaseDetailModel {
    @SerializedName("service")
    public Service service;

    public static class Service {
        @SerializedName("cropName")
        public String cropName;

        @SerializedName("sickNameKor")
        public String sickNameKor;

        @SerializedName("symptoms")
        public String symptoms;

        @SerializedName("preventionMethod")
        public String preventionMethod;

        @SerializedName("chemicalPrvnbeMth")
        public String chemicalPrvnbeMth;

        @SerializedName("biologyPrvnbeMth")
        public String biologyPrvnbeMth;

        @SerializedName("developmentCondition")
        public String developmentCondition;

        @SerializedName("infectionRoute")
        public String infectionRoute;

        @SerializedName("etc")
        public String etc;

        // 병원체 리스트 (ex: Erwinia pyrifoliae)
        @SerializedName("virusList")
        public List<VirusItem> virusList;

        // 병 증상 이미지 리스트
        @SerializedName("imageList")
        public List<ImageItem> imageList;

        public static class VirusItem {
            @SerializedName("virusName")
            public String virusName;

            @SerializedName("sfeNm")
            public String sfeNm;
        }

        public static class ImageItem {
            @SerializedName("image")
            public String image;

            @SerializedName("imageTitle")
            public String imageTitle;

            @SerializedName("iemSpchcknCode")
            public String iemSpchcknCode;

            @SerializedName("iemSpchcknNm")
            public String iemSpchcknNm;
        }
    }
}