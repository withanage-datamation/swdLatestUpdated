package com.datamation.swdsfa.model;

import org.json.JSONException;
import org.json.JSONObject;

public class DayTargetD {

    private String Itemcode;
    private String RefNo;
    private String TxnDate;
    private String TargetPercen;
    private String Day;
    private String Volume;

    public String getVolume() {
        return Volume;
    }

    public void setVolume(String volume) {
        Volume = volume;
    }

    public String getItemcode() {
        return Itemcode;
    }

    public void setItemcode(String itemcode) {
        Itemcode = itemcode;
    }

    public String getRefNo() {
        return RefNo;
    }

    public void setRefNo(String refNo) {
        RefNo = refNo;
    }

    public String getTxnDate() {
        return TxnDate;
    }

    public void setTxnDate(String txnDate) {
        TxnDate = txnDate;
    }

    public String getTargetPercen() {
        return TargetPercen;
    }

    public void setTargetPercen(String targetPercen) {
        TargetPercen = targetPercen;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public static DayTargetD parseDayTarDetD(JSONObject instance) throws JSONException {

        if (instance != null) {
            DayTargetD dayTargetD = new DayTargetD();

           dayTargetD.setItemcode(instance.getString("Itemcode"));
           dayTargetD.setRefNo(instance.getString("RefNo"));
           dayTargetD.setTxnDate(instance.getString("TxnDate"));
           dayTargetD.setTargetPercen(instance.getString("TargetPercen"));
           dayTargetD.setDay(instance.getString("Day"));

            return dayTargetD;
        }

        return null;
    }
}
