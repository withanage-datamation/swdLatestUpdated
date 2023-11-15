package com.datamation.swdsfa.model;

import org.json.JSONException;
import org.json.JSONObject;

public class DiscValDet {

    private String FDISCVALDET_DIS_PER;
    private String FDISCVALDET_DIS_AMT;
    private String FDISCVALDET_REFNO;
    private String FDISCVALDET_SEQNO;
    private String FDISCVALDET_VDATEF;
    private String FDISCVALDET_VDATET;

    public String getFDISCVALDET_DIS_PER() {
        return FDISCVALDET_DIS_PER;
    }

    public void setFDISCVALDET_DIS_PER(String FDISCVALDET_DIS_PER) {
        this.FDISCVALDET_DIS_PER = FDISCVALDET_DIS_PER;
    }

    public String getFDISCVALDET_DIS_AMT() {
        return FDISCVALDET_DIS_AMT;
    }

    public void setFDISCVALDET_DIS_AMT(String FDISCVALDET_DIS_AMT) {
        this.FDISCVALDET_DIS_AMT = FDISCVALDET_DIS_AMT;
    }

    public String getFDISCVALDET_REFNO() {
        return FDISCVALDET_REFNO;
    }

    public void setFDISCVALDET_REFNO(String FDISCVALDET_REFNO) {
        this.FDISCVALDET_REFNO = FDISCVALDET_REFNO;
    }

    public String getFDISCVALDET_SEQNO() {
        return FDISCVALDET_SEQNO;
    }

    public void setFDISCVALDET_SEQNO(String FDISCVALDET_SEQNO) {
        this.FDISCVALDET_SEQNO = FDISCVALDET_SEQNO;
    }

    public String getFDISCVALDET_VDATEF() {
        return FDISCVALDET_VDATEF;
    }

    public void setFDISCVALDET_VDATEF(String FDISCVALDET_VDATEF) {
        this.FDISCVALDET_VDATEF = FDISCVALDET_VDATEF;
    }

    public String getFDISCVALDET_VDATET() {
        return FDISCVALDET_VDATET;
    }

    public void setFDISCVALDET_VDATET(String FDISCVALDET_VDATET) {
        this.FDISCVALDET_VDATET = FDISCVALDET_VDATET;
    }

    public static DiscValDet parseDiscValDet(JSONObject instance) throws JSONException {

        if (instance != null) {
            DiscValDet discValDet = new DiscValDet();

            discValDet.setFDISCVALDET_DIS_PER(instance.getString("disPer").trim());
            discValDet.setFDISCVALDET_DIS_AMT(instance.getString("disamt").trim());
            discValDet.setFDISCVALDET_REFNO(instance.getString("RefNo").trim());
            discValDet.setFDISCVALDET_SEQNO(instance.getString("seqno").trim());
            discValDet.setFDISCVALDET_VDATEF(instance.getString("Valuef").trim());
            discValDet.setFDISCVALDET_VDATET(instance.getString("Valuet").trim());


            return discValDet;
        }

        return null;
    }
}
