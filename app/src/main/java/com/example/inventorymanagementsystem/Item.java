package com.example.inventorymanagementsystem;

public class Item {
    private String imagePath;
    private String code;
    private String name;
    private String price;
    private String quantity;
    private String location;
    private String category;
    private String purchaseDate;
    private  String warrantyEnd;
    private String supplName;
    private String comments;

    public Item(){

    }

    public Item( String imagePath, String name, String price, String quantity, String location, String category, String purchaseDate, String warrantyEnd, String supplName, String comments) {
        // if(name.trim().equals("")){
        //     name = "None";
        //}

        this.imagePath = imagePath;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.location = location;
        this.category = category;
        this.purchaseDate = purchaseDate;
        this.warrantyEnd = warrantyEnd;
        this.supplName = supplName;
        this.comments = comments;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getWarrantyEnd() {
        return warrantyEnd;
    }

    public String getSupplName() {
        return supplName;
    }

    public String getComments() {
        return comments;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setWarrantyEnd(String warrantyEnd) {
        this.warrantyEnd = warrantyEnd;
    }

    public void setSupplName(String supplName) {
        this.supplName = supplName;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
