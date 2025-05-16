package com.example.bottam_ex.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OtherInsectDetailModel {

    @SerializedName("service")
    public Service service;

    public static class Service {

        @SerializedName("insectGenus")
        public String insectGenus;

        @SerializedName("authYear")
        public String authYear;

        @SerializedName("fullInsectSpecies")
        public String fullInsectSpecies;

        @SerializedName("insectEngAlias")
        public String insectEngAlias;

        @SerializedName("insectAuthor")
        public String insectAuthor;

        @SerializedName("insectSpeciesKor")
        public String insectSpeciesKor;

        @SerializedName("insectFamily")
        public String insectFamily;

        @SerializedName("insectOrder")
        public String insectOrder;

        @SerializedName("insectSpecies")
        public String insectSpecies;

        @SerializedName("wrongedCrop")
        public String wrongedCrop;

        @SerializedName("distributeInfo")
        public String distributeInfo;

        @SerializedName("shapeInfo")
        public String shapeInfo;

        @SerializedName("ecologyInfo")
        public String ecologyInfo;

        @SerializedName("quarantineInfo")
        public String quarantineInfo;

        @SerializedName("cropDanageList")
        public List<CropDamage> cropDanageList;

        @SerializedName("imageList")
        public List<ImageItem> imageList;

        public static class CropDamage {
            @SerializedName("cropName")
            public String cropName;

            @SerializedName("damageInfo")
            public String damageInfo;

            @SerializedName("preventionMethod")
            public String preventionMethod;

            @SerializedName("hlsctInsectKey")
            public String hlsctInsectKey;
        }

        public static class ImageItem {
            @SerializedName("image")
            public String image;

            @SerializedName("imageTitle")
            public String imageTitle;
        }
    }
}