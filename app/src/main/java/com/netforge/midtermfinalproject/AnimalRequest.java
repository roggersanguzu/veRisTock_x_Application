package com.netforge.midtermfinalproject;

public class AnimalRequest {
    private String requestId;
    private String weight;
    private String age;
    private String sex;
    private String location;
    private String buyerPhone;
    private String buyerEmail;

    // Required empty constructor for Firebase
    public AnimalRequest() {}

    // Constructor with parameters
    public AnimalRequest(String requestId, String weight, String age, String sex, String location, String buyerPhone, String buyerEmail) {
        this.requestId = requestId;
        this.weight = weight;
        this.age = age;
        this.sex = sex;
        this.location = location;
        this.buyerPhone = buyerPhone;
        this.buyerEmail = buyerEmail;
    }

    // Getters and setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
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