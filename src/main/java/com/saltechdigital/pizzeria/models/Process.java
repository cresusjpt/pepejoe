package com.saltechdigital.pizzeria.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Process implements Parcelable {

    public static final Creator<Process> CREATOR = new Creator<Process>() {
        @Override
        public Process createFromParcel(Parcel in) {
            return new Process(in);
        }

        @Override
        public Process[] newArray(int size) {
            return new Process[size];
        }
    };
    @SerializedName("id_process")
    private int id;
    @SerializedName("id_livraison")
    private int idLivraison;
    @SerializedName("ordre")
    private int orderNum;
    @SerializedName("tag")
    private String tag;
    @SerializedName("action")
    private int action;
    @SerializedName("etat")
    private int etat;

    public Process() {

    }

    private Process(Parcel in) {
        id = in.readInt();
        idLivraison = in.readInt();
        orderNum = in.readInt();
        tag = in.readString();
        action = in.readInt();
        etat = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLivraison() {
        return idLivraison;
    }

    public void setIdLivraison(int idLivraison) {
        this.idLivraison = idLivraison;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(idLivraison);
        dest.writeInt(orderNum);
        dest.writeString(tag);
        dest.writeInt(action);
        dest.writeInt(etat);
    }
}
