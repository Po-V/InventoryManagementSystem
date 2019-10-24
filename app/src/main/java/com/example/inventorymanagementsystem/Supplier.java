package com.example.inventorymanagementsystem;

public class Supplier {

    private int id;
    private String imagePath;
    private String name;
    private String email;
    private String contact;
    private String address;
    private String comments;

    public Supplier(){

    }

    public Supplier( String imagePath, String name, String email, String contact, String address, String comments) {
        // if(name.trim().equals("")){
        //     name = "None";
        //}

        this.imagePath = imagePath;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.address = address;
        this.comments = comments;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getAddress() {
        return address;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}