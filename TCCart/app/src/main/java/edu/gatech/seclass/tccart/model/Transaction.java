package edu.gatech.seclass.tccart.model;

/**
 * Created by shawnrajput on 3/19/16.
 */
public class Transaction {
    /* Application is responsible to prevent SQL injection attack by making sure that the email id entered by the user does not contain special characters */
    private String szEmailID;
    private String szDate;
    private double dAmount;
    private double dCashDiscount;
    private double dVipDiscount;

    public Transaction() {
    }

    public Transaction (String szEmailID, String szDate, double dAmount, double dCashDiscount, double dVipDiscount) {
        this.szEmailID = szEmailID;
        this.szDate = szDate;
        this.dAmount = dAmount;
        this.dCashDiscount = dCashDiscount;
        this.dVipDiscount = dVipDiscount;
    }

    public String getSzEmailID() {
        return szEmailID;
    }

    public void setSzEmailID(String szEmailID) {
        this.szEmailID = szEmailID;
    }

    public String getSzDate() {
        return szDate;
    }

    public void setSzDate(String szDate) {
        this.szDate = szDate;
    }

    public double getdAmount() {
        return dAmount;
    }

    public void setdAmount(double dAmount) {
        this.dAmount = dAmount;
    }

    public double getdCashDiscount() {
        return dCashDiscount;
    }

    public void setdCashDiscount(double dCashDiscount) {
        this.dCashDiscount = dCashDiscount;
    }

    public double getdVipDiscount() {
        return dVipDiscount;
    }

    public void setdVipDiscount(double dVipDiscount) {
        this.dVipDiscount = dVipDiscount;
    }
}
