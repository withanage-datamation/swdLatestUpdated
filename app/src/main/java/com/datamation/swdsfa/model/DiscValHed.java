package com.datamation.swdsfa.model;

import org.json.JSONException;
import org.json.JSONObject;

public class DiscValHed {

    private String FDISCVALHED_DISC_DESC;
    private String FDISCVALHED_DISC_TYPE;
    private String FDISCVALHED_PAY_TYPE;
    private String FDISCVALHED_PRIORITY;
    private String FDISCVALHED_REFNO;
    private String FDISCVALHED_REMARKS;
    private String FDISCVALHED_TXNDATE;
    private String FDISCVALHED_VDATEF;
    private String FDISCVALHED_VDATET;

    public String getFDISCVALHED_DISC_DESC() {
        return FDISCVALHED_DISC_DESC;
    }

    public void setFDISCVALHED_DISC_DESC(String FDISCVALHED_DISC_DESC) {
        this.FDISCVALHED_DISC_DESC = FDISCVALHED_DISC_DESC;
    }

    public String getFDISCVALHED_DISC_TYPE() {
        return FDISCVALHED_DISC_TYPE;
    }

    public void setFDISCVALHED_DISC_TYPE(String FDISCVALHED_DISC_TYPE) {
        this.FDISCVALHED_DISC_TYPE = FDISCVALHED_DISC_TYPE;
    }

    public String getFDISCVALHED_PAY_TYPE() {
        return FDISCVALHED_PAY_TYPE;
    }

    public void setFDISCVALHED_PAY_TYPE(String FDISCVALHED_PAY_TYPE) {
        this.FDISCVALHED_PAY_TYPE = FDISCVALHED_PAY_TYPE;
    }

    public String getFDISCVALHED_PRIORITY() {
        return FDISCVALHED_PRIORITY;
    }

    public void setFDISCVALHED_PRIORITY(String FDISCVALHED_PRIORITY) {
        this.FDISCVALHED_PRIORITY = FDISCVALHED_PRIORITY;
    }

    public String getFDISCVALHED_REFNO() {
        return FDISCVALHED_REFNO;
    }

    public void setFDISCVALHED_REFNO(String FDISCVALHED_REFNO) {
        this.FDISCVALHED_REFNO = FDISCVALHED_REFNO;
    }

    public String getFDISCVALHED_REMARKS() {
        return FDISCVALHED_REMARKS;
    }

    public void setFDISCVALHED_REMARKS(String FDISCVALHED_REMARKS) {
        this.FDISCVALHED_REMARKS = FDISCVALHED_REMARKS;
    }

    public String getFDISCVALHED_TXNDATE() {
        return FDISCVALHED_TXNDATE;
    }

    public void setFDISCVALHED_TXNDATE(String FDISCVALHED_TXNDATE) {
        this.FDISCVALHED_TXNDATE = FDISCVALHED_TXNDATE;
    }

    public String getFDISCVALHED_VDATEF() {
        return FDISCVALHED_VDATEF;
    }

    public void setFDISCVALHED_VDATEF(String FDISCVALHED_VDATEF) {
        this.FDISCVALHED_VDATEF = FDISCVALHED_VDATEF;
    }

    public String getFDISCVALHED_VDATET() {
        return FDISCVALHED_VDATET;
    }

    public void setFDISCVALHED_VDATET(String FDISCVALHED_VDATET) {
        this.FDISCVALHED_VDATET = FDISCVALHED_VDATET;
    }

    public static DiscValHed parseDiscValHed(JSONObject instance) throws JSONException {

        if (instance != null) {
            DiscValHed discValHed = new DiscValHed();

            discValHed.setFDISCVALHED_DISC_DESC(instance.getString("DiscDesc").trim());
            discValHed.setFDISCVALHED_DISC_TYPE(instance.getString("DiscType").trim());
            discValHed.setFDISCVALHED_PAY_TYPE(instance.getString("PayType").trim());
            discValHed.setFDISCVALHED_PRIORITY(instance.getString("Priority").trim());
            discValHed.setFDISCVALHED_REFNO(instance.getString("RefNo").trim());
            discValHed.setFDISCVALHED_REMARKS(instance.getString("Remarks").trim());
            discValHed.setFDISCVALHED_TXNDATE(instance.getString("TxnDate").trim());
            discValHed.setFDISCVALHED_VDATEF(instance.getString("Vdatef").trim());
            discValHed.setFDISCVALHED_VDATET(instance.getString("Vdatet").trim());


            return discValHed;
        }

        return null;
    }
}
