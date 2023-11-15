package com.datamation.swdsfa.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rashmi.
 */

public class objPicture {

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("image_name")
    @Expose
    private String image_name;

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
