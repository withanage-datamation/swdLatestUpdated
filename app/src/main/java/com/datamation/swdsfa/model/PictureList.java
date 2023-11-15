package com.datamation.swdsfa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Rashmi
 */

public class PictureList {

    @SerializedName("data")
    @Expose
    private ArrayList<objPicture> images = new ArrayList<>();

    public ArrayList<objPicture> getImages() {
        return images;
    }

    public void setImages(ArrayList<objPicture> images) {
        this.images = images;
    }
}
