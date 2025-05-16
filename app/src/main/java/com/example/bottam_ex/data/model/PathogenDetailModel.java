package com.example.bottam_ex.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PathogenDetailModel {
    @SerializedName("service")
    public Service service;

    public static class Service {
        @SerializedName("cropName")
        public String cropName;

        @SerializedName("sickNameKor")
        public String sickNameKor;

        @SerializedName("virusName")
        public String virusName;

        @SerializedName("virusGens")
        public String virusGens;

        @SerializedName("virusSpcs")
        public String virusSpcs;

        @SerializedName("virusGroup")
        public String virusGroup;

        @SerializedName("virusAuthor")
        public String virusAuthor;

        @SerializedName("virusCharacteristic")
        public String virusCharacteristic;

        @SerializedName("virusRelate")
        public String virusRelate;

        @SerializedName("linkInfo")
        public String linkInfo;

        @SerializedName("qrantInfo")
        public String qrantInfo;

        @SerializedName("literature")
        public String literature;

        @SerializedName("etc")
        public String etc;

        @SerializedName("remark1")
        public String remark1;

        @SerializedName("remark2")
        public String remark2;

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