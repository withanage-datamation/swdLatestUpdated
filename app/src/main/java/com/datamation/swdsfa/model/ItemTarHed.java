package com.datamation.swdsfa.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemTarHed {

    private String RepCode;
    private String AddDate;
    private String AddMach;
    private String AddUser;
    private String DealCode;
    private String ManuRef;
    private String Month;
    private String RefNo;
    private String year;
    private String TxnDate;

    public String getRepCode() {
        return RepCode;
    }

    public void setRepCode(String repCode) {
        RepCode = repCode;
    }

    public String getAddDate() {
        return AddDate;
    }

    public void setAddDate(String addDate) {
        AddDate = addDate;
    }

    public String getAddMach() {
        return AddMach;
    }

    public void setAddMach(String addMach) {
        AddMach = addMach;
    }

    public String getAddUser() {
        return AddUser;
    }

    public void setAddUser(String addUser) {
        AddUser = addUser;
    }

    public String getDealCode() {
        return DealCode;
    }

    public void setDealCode(String dealCode) {
        DealCode = dealCode;
    }

    public String getManuRef() {
        return ManuRef;
    }

    public void setManuRef(String manuRef) {
        ManuRef = manuRef;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getRefNo() {
        return RefNo;
    }

    public void setRefNo(String refNo) {
        RefNo = refNo;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTxnDate() {
        return TxnDate;
    }

    public void setTxnDate(String txnDate) {
        TxnDate = txnDate;
    }

    public static ItemTarHed parseItemTarHed(JSONObject instance) throws JSONException {

        if (instance != null) {

            ItemTarHed itemTarHed = new ItemTarHed();

            itemTarHed.setAddDate(instance.getString("AddDate"));
            itemTarHed.setAddMach(instance.getString("AddMach"));
            itemTarHed.setAddUser(instance.getString("AddUser"));
            itemTarHed.setDealCode(instance.getString("DealCode"));
            itemTarHed.setManuRef(instance.getString("ManuRef"));
            itemTarHed.setMonth(instance.getString("Month"));
            itemTarHed.setRefNo(instance.getString("RefNo"));
            itemTarHed.setRepCode(instance.getString("RepCode"));
            itemTarHed.setTxnDate(instance.getString("TxnDate"));
            itemTarHed.setYear(instance.getString("year"));

            return itemTarHed;
        }

        return null;
    }
}
