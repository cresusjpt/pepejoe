package com.saltechdigital.pizzeria.models;

import com.google.gson.annotations.SerializedName;

public class Payment {

    @SerializedName("id_payment")
    private int id;

    @SerializedName("id_client")
    private int clienID;

    @SerializedName("payment_type")
    private String type;

    @SerializedName("mobile_name")
    private String mobileName;

    @SerializedName("card_titulaire")
    private String cardOwner;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("expire")
    private String expire;

    @SerializedName("card_number")
    private String cardNumber;

    @SerializedName("cvc")
    private int cvc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienID() {
        return clienID;
    }

    public void setClienID(int clienID) {
        this.clienID = clienID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMobileName() {
        return mobileName;
    }

    public void setMobileName(String mobileName) {
        this.mobileName = mobileName;
    }

    public String getCardOwner() {
        return cardOwner;
    }

    public void setCardOwner(String cardOwner) {
        this.cardOwner = cardOwner;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getCvc() {
        return cvc;
    }

    public void setCvc(int cvc) {
        this.cvc = cvc;
    }
}
