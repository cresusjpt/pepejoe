package com.saltechdigital.pizzeria.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.saltechdigital.pizzeria.models.Commande;
import com.saltechdigital.pizzeria.models.Plat;
import com.saltechdigital.pizzeria.utils.Config;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CommandeDao {

    @Query("SELECT * from commande where idClient = :idClient")
    LiveData<List<Commande>> getCommandes(int idClient);

    @Query("SELECT * from commande where idCommande = :id")
    LiveData<Commande> getCommande(int id);

    @Query("SELECT * from commande where statutCommande = :statut order by idCommande limit 1")
    LiveData<Commande> getStatutCommande(String statut);

    @Query("Select count(*) from commande")
    LiveData<Integer> taille();

    @Insert(onConflict = REPLACE)
    long insertCommande(Commande commande);

    @Update
    int updateCommande(Commande commande);

    @Delete
    int deleteCommande(Commande commande);

    @Query("Delete from commande")
    int deleteCommandes();
}
