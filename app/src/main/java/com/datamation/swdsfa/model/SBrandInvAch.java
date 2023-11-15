package com.datamation.swdsfa.model;

import org.json.JSONException;
import org.json.JSONObject;

public class SBrandInvAch {

    private String FSBRAND_INV_ACHIEVEMENT;
    private String FSBRAND_INV_SBRANDCODE;
    private String FSBRAND_INV_TXNDATE;


    public String getFSBRAND_INV_ACHIEVEMENT() {
        return FSBRAND_INV_ACHIEVEMENT;
    }

    public void setFSBRAND_INV_ACHIEVEMENT(String FSBRAND_INV_ACHIEVEMENT) {
        this.FSBRAND_INV_ACHIEVEMENT = FSBRAND_INV_ACHIEVEMENT;
    }

    public String getFSBRAND_INV_SBRANDCODE() {
        return FSBRAND_INV_SBRANDCODE;
    }

    public void setFSBRAND_INV_SBRANDCODE(String FSBRAND_INV_SBRANDCODE) {
        this.FSBRAND_INV_SBRANDCODE = FSBRAND_INV_SBRANDCODE;
    }

    public String getFSBRAND_INV_TXNDATE() {
        return FSBRAND_INV_TXNDATE;
    }

    public void setFSBRAND_INV_TXNDATE(String FSBRAND_INV_TXNDATE) {
        this.FSBRAND_INV_TXNDATE = FSBRAND_INV_TXNDATE;
    }

    public static SBrandInvAch parseInvAchieve(JSONObject instance) throws JSONException {

        if (instance != null) {
            SBrandInvAch bank = new SBrandInvAch();

            bank.setFSBRAND_INV_ACHIEVEMENT(instance.getString("Achievement"));
            bank.setFSBRAND_INV_SBRANDCODE(instance.getString("SBrandCode"));
            bank.setFSBRAND_INV_TXNDATE(instance.getString("txnDate"));


            return bank;
        }

        return null;
    }
}
