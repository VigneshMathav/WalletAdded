package com.vk.uplogictask.model;

public class AmountDetails {

    public String userId;
    public String method;
    public String description;
    public String date;
    public double amount;

    public AmountDetails() {
    }

    public void uploadAmountDetails(String userId, String method, String description, String date, double amount) {
        this.userId = userId;
        this.method = method;
        this.description = description;
        this.date = date;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
