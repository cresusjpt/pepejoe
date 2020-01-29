package com.saltechdigital.pizzeria.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.saltechdigital.pizzeria.database.dao.CategorieDao;
import com.saltechdigital.pizzeria.database.dao.ClientDao;
import com.saltechdigital.pizzeria.database.dao.CommandeDao;
import com.saltechdigital.pizzeria.database.dao.PlatDao;
import com.saltechdigital.pizzeria.database.dao.DetailCommandeDao;
import com.saltechdigital.pizzeria.models.Categorie;
import com.saltechdigital.pizzeria.models.Client;
import com.saltechdigital.pizzeria.models.Commande;
import com.saltechdigital.pizzeria.models.DetailCommande;
import com.saltechdigital.pizzeria.models.Plat;

@Database(entities = {Categorie.class, Plat.class, Commande.class, Client.class, DetailCommande.class}, version = 1, exportSchema = false)
public abstract class PizzeriaDatabase extends RoomDatabase {

    private static volatile PizzeriaDatabase INSTANCE;

    public abstract CategorieDao categorieDao();
    public abstract PlatDao platDao();
    public abstract CommandeDao commandeDao();
    public abstract ClientDao clientDao();
    public abstract DetailCommandeDao detailCommandeDao();

    public static PizzeriaDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PizzeriaDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PizzeriaDatabase.class, "pizzeria.db")
                            //.addCallback(prepopulated())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static Callback prepopulated() {
        return new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }
        };
    }
}
