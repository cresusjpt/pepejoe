package com.saltechdigital.pizzeria.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.saltechdigital.pizzeria.models.Client;
import com.saltechdigital.pizzeria.models.Commande;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ClientDao {

    @Query("SELECT * from client")
    LiveData<List<Client>> getClients();

    @Query("SELECT * from client where idClient = :id")
    LiveData<Client> getCient(int id);

    @Query("SELECT * from client where idClient = :id")
    Client bruteGetCient(int id);

    @Query("Select count(*) from client")
    LiveData<Integer> taille();

    @Insert(onConflict = REPLACE)
    long insertClient(Client client);

    @Update
    int updateClient(Client client);

    @Delete
    int deleteClient(Client client);

    @Query("Delete from client")
    int deleteClients();
}
