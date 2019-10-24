package com.example.inventorymanagementsystem;

public class LendItem {

    private String itemName;
    private String personname;
    private String returnDate;
    private String LendDate;
    private String lendComments;

    public LendItem(String itemName, String personname, String returnDate, String lendDate, String lendComments) {
        this.itemName = itemName;
        this.personname = personname;
        this.returnDate = returnDate;
        LendDate = lendDate;
        this.lendComments = lendComments;
    }

    public String getItemName() {
        return itemName;
    }

    public String getPersonname() {
        return personname;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getLendDate() {
        return LendDate;
    }

    public String getLendComments() {
        return lendComments;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setPersonname(String personname) {
        this.personname = personname;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public void setLendDate(String lendDate) {
        LendDate = lendDate;
    }

    public void setLendComments(String lendComments) {
        this.lendComments = lendComments;
    }
}
