package com.datamation.swdsfa.model;

public class Target {
    String date;
    String brandName;
    String brandCode;
    double targetAmt;
    double achieveAmt;
    double precentage;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public double getPrecentage() {
        return precentage;
    }

    public void setPrecentage(double precentage) {
        this.precentage = precentage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTargetAmt() {
        return targetAmt;
    }

    public void setTargetAmt(double targetAmt) {
        this.targetAmt = targetAmt;
    }

    public double getAchieveAmt() {
        return achieveAmt;
    }

    public void setAchieveAmt(double achieveAmt) {
        this.achieveAmt = achieveAmt;
    }
}
