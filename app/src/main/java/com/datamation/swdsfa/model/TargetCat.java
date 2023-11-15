package com.datamation.swdsfa.model;

import org.json.JSONException;
import org.json.JSONObject;

public class TargetCat {

    private String Incentive1;
    private String Incentive2;
    private String TarCatCode;
    private String TarcatName;
    private float id;

    public float getId() {
        return id;
    }

    public void setId(float id) {
        this.id = id;
    }

    public String getIncentive1() {
        return Incentive1;
    }

    public void setIncentive1(String incentive1) {
        Incentive1 = incentive1;
    }

    public String getIncentive2() {
        return Incentive2;
    }

    public void setIncentive2(String incentive2) {
        Incentive2 = incentive2;
    }

    public String getTarCatCode() {
        return TarCatCode;
    }

    public void setTarCatCode(String tarCatCode) {
        TarCatCode = tarCatCode;
    }

    public String getTarcatName() {
        return TarcatName;
    }

    public void setTarcatName(String tarcatName) {
        TarcatName = tarcatName;
    }

    public static TargetCat parseTargetCat(JSONObject instance) throws JSONException {

        if (instance != null) {
            TargetCat targetCat = new TargetCat();

            targetCat.setIncentive1(instance.getString("incentive1"));
            targetCat.setIncentive2(instance.getString("incentive2"));
            targetCat.setTarCatCode(instance.getString("tarCatCode"));
            targetCat.setTarcatName(instance.getString("tarcatName"));


            return targetCat;
        }

        return null;
    }
}
