package com.saltechdigital.pizzeria.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Services implements Parcelable {

    private int id;

    private String name;

    private int[] images;

    private String tag;

    public Services(){}

    protected Services(Parcel in) {
        id = in.readInt();
        name = in.readString();
        images = in.createIntArray();
        tag = in.readString();
    }

    public static final Creator<Services> CREATOR = new Creator<Services>() {
        @Override
        public Services createFromParcel(Parcel in) {
            return new Services(in);
        }

        @Override
        public Services[] newArray(int size) {
            return new Services[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getImages() {
        return images;
    }

    public void setImages(int[] images) {
        this.images = images;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeIntArray(images);
        dest.writeString(tag);
    }
}
