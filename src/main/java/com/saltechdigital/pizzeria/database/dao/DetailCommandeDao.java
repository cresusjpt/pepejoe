package com.saltechdigital.pizzeria.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.saltechdigital.pizzeria.models.Commande;
import com.saltechdigital.pizzeria.models.DetailCommande;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface DetailCommandeDao {

    @Query("SELECT * from detailcommande where idCommande = :idCommande")
    LiveData<List<DetailCommande>> getAll(int idCommande);

    @Query("SELECT * from detailcommande where idCommande = :id")
    LiveData<DetailCommande> get(int id);

    @Query("Select count(*) from detailcommande")
    LiveData<Integer> taille();

    //@Insert(onConflict = REPLACE)
    @Insert
    long insert(DetailCommande detailCommande);

    @Update
    int update(DetailCommande detailCommande);

    @Delete
    int delete(DetailCommande detailCommande);

    @Query("Delete from detailcommande")
    int deletes();
}
