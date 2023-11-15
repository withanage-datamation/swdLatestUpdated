package com.datamation.swdsfa.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemTarDet {

    private String Itemcode;
    private String RefNo;
    private String TxnDate;
    private String Volume;

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

    public String getVolume() {
        return Volume;
    }

    public void setVolume(String volume) {
        Volume = volume;
    }

    public static ItemTarDet parseItemTarDet(JSONObject instance) throws JSONException {

        if (instance != null) {
            ItemTarDet itemTarDet = new ItemTarDet();

            itemTarDet.setItemcode(instance.getString("Itemcode"));
            itemTarDet.setRefNo(instance.getString("RefNo"));
            itemTarDet.setTxnDate(instance.getString("TxnDate"));
            itemTarDet.setVolume(instance.getString("Volume"));

            return itemTarDet;
        }

        return null;
    }
}
