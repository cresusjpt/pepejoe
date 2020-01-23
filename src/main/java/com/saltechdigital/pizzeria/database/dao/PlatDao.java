package com.saltechdigital.pizzeria.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.saltechdigital.pizzeria.models.Categorie;
import com.saltechdigital.pizzeria.models.Plat;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PlatDao {

    @Query("SELECT * from plat where idCategorie = :idCategorie")
    LiveData<List<Plat>> getPlates(int idCategorie);

    @Query("SELECT * from plat where idPlat = :id")
    LiveData<Plat> getPlate(int id);

    @Query("Select count(*) from plat")
    int taille();

    @Insert(onConflict = REPLACE)
    long insertPlate(Plat plat);

    @Update
    int updatPlate(Plat plat);

    @Delete
    int deletePlate(Plat plat);

    @Query("Delete from plat")
    int deletePlates();
}
