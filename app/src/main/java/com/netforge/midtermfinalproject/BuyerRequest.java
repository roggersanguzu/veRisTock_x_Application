package com.netforge.midtermfinalproject;

public class BuyerRequest {
    private String buyerUID;
    private double weight;
    private int age;
    private String sex;
    private String location;
    private String buyerPhone;
    private String buyerEmail;

    // Required empty constructor for Firestore
    public BuyerRequest() {}

    // Constructor with parameters
    public BuyerRequest(String buyerUID, double weight, int age, String sex, String location, String buyerPhone, String buyerEmail) {
        this.buyerUID = buyerUID;
        this.weight = weight;
        this.age = age;
        this.sex = sex;
        this.location = location;
        this.buyerPhone = buyerPhone;
        this.buyerEmail = buyerEmail;
    }

    // Getters and setters
    public String getBuyerUID() {
        return buyerUID;
    }

    public void setBuyerUID(String buyerUID) {
        this.buyerUID = buyerUID;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }
}