package com.saltechdigital.pizzeria.models;

import com.google.gson.annotations.SerializedName;

public class PaymentStatus {

    @SerializedName("id_pstatus")
    private int id;

    @SerializedName("tx_reference")
    private String txRef;

    private String identifier;

    @SerializedName("payment_reference")
    private String payRef;

    private String amount;

    @SerializedName("datetime")
    private String date;

    @SerializedName("payment_method")
    private String payMethod;

    @SerializedName("phone_number")
    private String phone;

    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTxRef() {
        return txRef;
    }

    public void setTxRef(String txRef) {
        this.txRef = txRef;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPayRef() {
        return payRef;
    }

    public void setPayRef(String payRef) {
        this.payRef = payRef;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int isStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
