package com.saltechdigital.pizzeria.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.saltechdigital.pizzeria.models.Categorie;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CategorieDao {

    @Query("SELECT * from categorie")
    LiveData<List<Categorie>> getCategories();

    @Query("SELECT * from categorie where idCategorie = :id")
    LiveData<Categorie> getCategorie(int id);

    @Query("Select count(*) from categorie")
    int taille();

    @Insert(onConflict = REPLACE)
    long insertCategorie(Categorie categorie);

    @Update
    int updateCategorie(Categorie categorie);

    @Delete
    int deleteCategorie(Categorie categorie);

    @Query("Delete from categorie")
    int deleteCategories();
}
