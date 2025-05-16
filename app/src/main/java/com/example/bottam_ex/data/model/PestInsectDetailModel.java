package com.example.bottam_ex.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PestInsectDetailModel {
    @SerializedName("service")
    public Service service;

    public static class Service {
        @SerializedName("cropName")
        public String cropName;

        @SerializedName("insectSpeciesKor")
        public String insectSpeciesKor;

        @SerializedName("insectGenus")
        public String insectGenus;

        @SerializedName("insectSpecies")
        public String insectSpecies;

        @SerializedName("insectFamily")
        public String insectFamily;

        @SerializedName("insectOrder")
        public String insectOrder;

        @SerializedName("insectAuthor")
        public String insectAuthor;

        @SerializedName("damageInfo")
        public String damageInfo;

        @SerializedName("preventMethod")
        public String preventMethod;

        @SerializedName("biologyPrvnbeMth")
        public String biologyPrvnbeMth;

        @SerializedName("chemicalPrvnbeMth")
        public String chemicalPrvnbeMth;

        @SerializedName("stleInfo")
        public String stleInfo;

        @SerializedName("ecologyInfo")
        public String ecologyInfo;

        @SerializedName("qrantInfo")
        public String qrantInfo;

        @SerializedName("distrbInfo")
        public String distrbInfo;

        @SerializedName("spcsCode")
        public String spcsCode;

        @SerializedName("enemyInsectList")
        public List<EnemyInsect> enemyInsectList;

        @SerializedName("imageList")
        public List<ImageItem> imageList;

        @SerializedName("spcsPhotoData")
        public List<PhotoItem> spcsPhotoData;

        public static class EnemyInsect {
            @SerializedName("enemyInsectName")
            public String enemyInsectName;

            @SerializedName("controlEffect")
            public String controlEffect;
        }

        public static class ImageItem {
            @SerializedName("image")
            public String image;

            @SerializedName("imageTitle")
            public String imageTitle;
        }

        public static class PhotoItem {
            @SerializedName("image")
            public String image;

            @SerializedName("photoSj")
            public String photoSubject;

            @SerializedName("priyClNm")
            public String stageName;
        }
    }
}