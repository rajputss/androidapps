package edu.gatech.seclass.tccart.model;

import java.util.Random;

/**
 * Created by shawnrajput on 3/19/16.
 */
public class Customer {

    private String  szEmailID;
    private String  szFirstName;
    private String  szLastName;
    private String  szQrCode;
    private boolean	bVipStatus;
    private boolean	rewardStatus;

    /* Default Constructor */
    public Customer() {
    }

    /* Initializing instance variables */
    public Customer(String szEmailID, String szFirstName, String szLastName,
                    String szQrCode, boolean bVipStatus, boolean rewardStatus) {
        this.szEmailID = szEmailID;
        this.szFirstName = szFirstName;
        this.szLastName = szLastName;
        this.szQrCode = szQrCode;
        this.bVipStatus = bVipStatus;
        this.rewardStatus = rewardStatus;
    }

    public Customer(String szEmailID, String szFirstName, String szLastName,
                    boolean bVipStatus, boolean rewardStatus) {
        this.szEmailID = szEmailID;
        this.szFirstName = szFirstName;
        this.szLastName = szLastName;
        this.bVipStatus = bVipStatus;
        this.rewardStatus = rewardStatus;
    }

    public Customer(String szEmailID, String szFirstName, String szLastName, String szQrCode) {
        this.szEmailID = szEmailID;
        this.szFirstName = szFirstName;
        this.szLastName = szLastName;
        this.szQrCode = szQrCode;
    }

    public String getSzEmailID() {
        return szEmailID;
    }

    public void setSzEmailID(String szEmailID) {
        this.szEmailID = szEmailID;
    }

    public String getSzFirstName() {
        return szFirstName;
    }

    public void setSzFirstName(String szFirstName) {
        this.szFirstName = szFirstName;
    }

    public String getSzLastName() {
        return szLastName;
    }

    public void setSzLastName(String szLastName) {
        this.szLastName = szLastName;
    }

    public String getSzQrCode() {

        szQrCode = getHexDecimalId();
        return szQrCode;
    }

    public void setSzQrCode(String szQrCode) {
        this.szQrCode = szQrCode;
    }

    public boolean isbVipStatus() {
        return bVipStatus;
    }

    public void setbVipStatus(boolean bVipStatus) {
        this.bVipStatus = bVipStatus;
    }

    public boolean isRewardStatus() {
        return rewardStatus;
    }

    public void setRewardStatus(boolean rewardStatus) {
        this.rewardStatus = rewardStatus;
    }
    public double getBalanceCashDiscount() {
        return 0.0;
    }

    public double getYTDPurchase() {
        return 0.0;
    }

    public String getHexDecimalId() {
        Random rand = new Random();
        long myRandomNumber = (long)(rand.nextDouble() * 10000000000L);
        String result = Long.toHexString(myRandomNumber); // Random hex number in result

        return result;
    }
}
