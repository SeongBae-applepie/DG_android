package com.example.bottam_ex.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeedDetailModel {

    @SerializedName("service")
    public Service service;

    public static class Service {

        @SerializedName("weedsKorName")
        public String weedsKorName;

        @SerializedName("weedsKorNameEngInscrptn")
        public String weedsKorNameEngInscrptn;

        @SerializedName("weedsScientificName")
        public String weedsScientificName;

        @SerializedName("weedsScientificOtherName")
        public String weedsScientificOtherName;

        @SerializedName("weedsFamily")
        public String weedsFamily;

        @SerializedName("weedsLifeForm")
        public String weedsLifeForm;

        @SerializedName("weedsHabitat")
        public String weedsHabitat;

        @SerializedName("weedsInflowTime")
        public String weedsInflowTime;

        @SerializedName("weedsEcology")
        public String weedsEcology;

        @SerializedName("weedsShape")
        public String weedsShape;

        @SerializedName("literature")
        public String literature;

        @SerializedName("consider")
        public String consider;

        @SerializedName("imageList")
        public List<ImageItem> imageList;

        public static class ImageItem {
            @SerializedName("image")
            public String image;

            @SerializedName("imageTitle")
            public String imageTitle;
        }
    }
}