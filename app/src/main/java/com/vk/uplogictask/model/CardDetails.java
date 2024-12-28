package com.vk.uplogictask.model;

public class CardDetails {

    public String userId;
    public String cardHolder;
    public String cardCVV;
    public String cardNumber;
    public String cardExpireDate;

    public CardDetails() {
    }

    public void uploadCardDetails(String userId, String cardNumber, String cardExpireDate, String cardHolder, String cardCVV) {
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.cardExpireDate = cardExpireDate;
        this.cardHolder = cardHolder;
        this.cardCVV = cardCVV;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getCardCVV() {
        return cardCVV;
    }

    public void setCardCVV(String cardCVV) {
        this.cardCVV = cardCVV;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpireDate() {
        return cardExpireDate;
    }

    public void setCardExpireDate(String cardExpireDate) {
        this.cardExpireDate = cardExpireDate;
    }
}
