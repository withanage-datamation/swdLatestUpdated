package com.datamation.swdsfa.model;

import org.json.JSONException;
import org.json.JSONObject;

public class DiscValDeb {

    private String FDISCVALDEB_DEBCODE;
    private String FDISCVALDEB_REFNO;

    public String getFDISCVALDEB_DEBCODE() {
        return FDISCVALDEB_DEBCODE;
    }

    public void setFDISCVALDEB_DEBCODE(String FDISCVALDEB_DEBCODE) {
        this.FDISCVALDEB_DEBCODE = FDISCVALDEB_DEBCODE;
    }

    public String getFDISCVALDEB_REFNO() {
        return FDISCVALDEB_REFNO;
    }

    public void setFDISCVALDEB_REFNO(String FDISCVALDEB_REFNO) {
        this.FDISCVALDEB_REFNO = FDISCVALDEB_REFNO;
    }

    public static DiscValDeb parseDiscValDeb(JSONObject instance) throws JSONException {

        if (instance != null) {
            DiscValDeb discValDeb = new DiscValDeb();

            discValDeb.setFDISCVALDEB_DEBCODE(instance.getString("DebCode").trim());
            discValDeb.setFDISCVALDEB_REFNO(instance.getString("RefNo").trim());

            return discValDeb;
        }

        return null;
    }
}
