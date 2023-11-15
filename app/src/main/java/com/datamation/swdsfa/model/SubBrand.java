package com.datamation.swdsfa.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class SubBrand {

    @SerializedName("BrandCode")
    private String FSUBRAND_BRANDCODE;
    @SerializedName("SBrandName")
    private String FSUBRAND_NAME;
    @SerializedName("SBrandCode")
    private String FSUBRAND_SUBCODE;

    public String getFSUBRAND_BRANDCODE() {
        return FSUBRAND_BRANDCODE;
    }

    public void setFSUBRAND_BRANDCODE(String FSUBRAND_BRANDCODE) {
        this.FSUBRAND_BRANDCODE = FSUBRAND_BRANDCODE;
    }

    public String getFSUBRAND_NAME() {
        return FSUBRAND_NAME;
    }

    public void setFSUBRAND_NAME(String FSUBRAND_NAME) {
        this.FSUBRAND_NAME = FSUBRAND_NAME;
    }

    public String getFSUBRAND_SUBCODE() {
        return FSUBRAND_SUBCODE;
    }

    public void setFSUBRAND_SUBCODE(String FSUBRAND_SUBCODE) {
        this.FSUBRAND_SUBCODE = FSUBRAND_SUBCODE;
    }

    public static SubBrand parseSubBrand(JSONObject instance) throws JSONException{

        if(instance != null){

            SubBrand subBrand =  new SubBrand();

            subBrand.setFSUBRAND_NAME(instance.getString("SBrandName"));
            subBrand.setFSUBRAND_SUBCODE(instance.getString("SBrandCode"));

            return subBrand;

        }
        return null;
    }
}
