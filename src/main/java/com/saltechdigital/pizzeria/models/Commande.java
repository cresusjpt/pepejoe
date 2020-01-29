package com.saltechdigital.pizzeria.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(foreignKeys = @ForeignKey(entity = Client.class,
        parentColumns = "idClient",childColumns = "idClient"),indices ={@Index("idClient")})
public class Commande implements Parcelable {

    @SerializedName("id_commande")
    @PrimaryKey(autoGenerate = true)
    private int idCommande;

    private int idClient;

    private String dateCommande;

    private String statutCommande;

    public Commande() {
    }

    protected Commande(Parcel in) {
        idCommande = in.readInt();
        idClient = in.readInt();
        dateCommande = in.readString();
        statutCommande = in.readString();
    }

    public static final Creator<Commande> CREATOR = new Creator<Commande>() {
        @Override
        public Commande createFromParcel(Parcel in) {
            return new Commande(in);
        }

        @Override
        public Commande[] newArray(int size) {
            return new Commande[size];
        }
    };

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(String dateCommande) {
        this.dateCommande = dateCommande;
    }

    public String getStatutCommande() {
        return statutCommande;
    }

    public void setStatutCommande(String statutCommande) {
        this.statutCommande = statutCommande;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idCommande);
        dest.writeInt(idClient);
        dest.writeString(dateCommande);
        dest.writeString(statutCommande);
    }

    @NonNull
    @Override
    public String toString() {
        return "id :"+idCommande+" client :"+idClient+" date"+dateCommande+" statut: "+statutCommande;
    }
}
