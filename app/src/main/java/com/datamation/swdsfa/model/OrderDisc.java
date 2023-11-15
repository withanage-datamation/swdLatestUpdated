package com.datamation.swdsfa.model;

import com.google.gson.annotations.SerializedName;

public class OrderDisc {

  //  @SerializedName("RefNo")
    private String RefNo;
   // @SerializedName("TxnDate")
    private String TxnDate;
   // @SerializedName("RefNo1")
    private String RefNo1;
   // @SerializedName("ItemCode")
    private String ItemCode;
   // @SerializedName("DisAmt")
    private String DisAmt;
  //  @SerializedName("DisPer")
    private String DisPer;

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

    public String getRefNo1() {
        return RefNo1;
    }

    public void setRefNo1(String refNo1) {
        RefNo1 = refNo1;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getDisAmt() {
        return DisAmt;
    }

    public void setDisAmt(String disAmt) {
        DisAmt = disAmt;
    }

    public String getDisPer() {
        return DisPer;
    }

    public void setDisPer(String disPer) {
        DisPer = disPer;
    }


}
