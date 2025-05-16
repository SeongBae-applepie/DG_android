package com.example.bottam_ex.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EnemyInsectDetailModel {

    @SerializedName("service")
    public Service service;

    public static class Service {

        @SerializedName("insectSpeciesKor")
        public String insectSpeciesKor;

        @SerializedName("cropName")
        public String cropName;

        @SerializedName("cropCode")
        public String cropCode;

        @SerializedName("etcCrop")
        public String etcCrop;

        @SerializedName("domesticDistribution")
        public String domesticDistribution;

        @SerializedName("outsideDistribution")
        public String outsideDistribution;

        @SerializedName("utilizationMethod")
        public String utilizationMethod;

        @SerializedName("feature")
        public String feature;

        @SerializedName("lifeCycle")
        public String lifeCycle;

        @SerializedName("examinResult")
        public String examinResult;

        @SerializedName("ptzoneImg")
        public List<ImageItem> ptzoneImg;

        @SerializedName("targetVermin")
        public List<TargetVermin> targetVermin;
    }

    public static class ImageItem {
        @SerializedName("image")
        public String image;

        @SerializedName("imageTitle")
        public String imageTitle;

        @SerializedName("priyClNm")
        public String priyClNm;

        @SerializedName("category")
        public String category;
    }

    public static class TargetVermin {
        @SerializedName("targetInsectSpeciesKor")
        public String targetInsectSpeciesKor;

        @SerializedName("targetInsectSpecies")
        public String targetInsectSpecies;

        @SerializedName("targetInsectGenus")
        public String targetInsectGenus;

        @SerializedName("targetInsectFamily")
        public String targetInsectFamily;

        @SerializedName("targetInsectOrder")
        public String targetInsectOrder;

        @SerializedName("targetCropName")
        public String targetCropName;

        @SerializedName("targetVrmnGroup")
        public String targetVrmnGroup;

        @SerializedName("targetImage")
        public String targetImage;

        @SerializedName("targetCropLink")
        public String targetCropLink;

        @SerializedName("targetInsectKey")
        public String targetInsectKey;

        @SerializedName("hlsctKey")
        public String hlsctKey;
    }
}