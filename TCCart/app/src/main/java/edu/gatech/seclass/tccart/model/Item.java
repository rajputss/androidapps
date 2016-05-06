package edu.gatech.seclass.tccart.model;

/**
 * Created by shawnrajput on 3/23/16.
 */
public class Item {
    //Application is responsible for input validations, especially to counteract SQL injection attacks.
    private String  szItemID;
    private String  szItemName;
    private String  szItemType;
    private Double  szItemPrice;

    public Item() {
    }

    public Item(String szItemID, String szItemName, String szItemType, Double szItemPrice) {
        this.szItemID = szItemID;
        this.szItemName = szItemName;
        this.szItemType = szItemType;
        this.szItemPrice = szItemPrice;
    }

    public String getSzItemID() {
        return szItemID;
    }

    public void setSzItemID(String szItemID) {
        this.szItemID = szItemID;
    }

    public String getSzItemName() {
        return szItemName;
    }

    public void setSzItemName(String szItemName) {
        this.szItemName = szItemName;
    }

    public String getSzItemType() {
        return szItemType;
    }

    public void setSzItemType(String szItemType) {
        this.szItemType = szItemType;
    }

    public Double getSzItemPrice() {
        return szItemPrice;
    }

    public void setSzItemPrice(Double szItemPrice) {
        this.szItemPrice = szItemPrice;
    }
}
