package com.saltechdigital.pizzeria.injections;

import android.content.Context;

import androidx.room.Room;

import com.saltechdigital.pizzeria.database.PizzeriaDatabase;
import com.saltechdigital.pizzeria.database.repository.CategorieDataRepository;
import com.saltechdigital.pizzeria.database.repository.ClientDataRepository;
import com.saltechdigital.pizzeria.database.repository.CommandeDataRepository;
import com.saltechdigital.pizzeria.database.repository.DetailCommandeDataRepository;
import com.saltechdigital.pizzeria.database.repository.PlatDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    private static PizzeriaDatabase databaseOperation(Context context){
        PizzeriaDatabase database = Room.databaseBuilder(context,PizzeriaDatabase.class,"pizzeria.db").allowMainThreadQueries().build();

        //PizzeriaDatabase database = PizzeriaDatabase.getInstance(context);
        return database;
    }

    public static CategorieDataRepository provideCategorieDataSource(Context context) {

        PizzeriaDatabase database = databaseOperation(context);

        return new CategorieDataRepository(database.categorieDao());
    }

    public static PlatDataRepository providePlatDataSource(Context context) {
        PizzeriaDatabase database = databaseOperation(context);
        return new PlatDataRepository(database.platDao());
    }

    private static ClientDataRepository provideClientDataSource(Context context) {
        PizzeriaDatabase database = databaseOperation(context);
        return new ClientDataRepository(database.clientDao());
    }

    private static CommandeDataRepository provideCommandeDataSource(Context context) {
        PizzeriaDatabase database = databaseOperation(context);
        return new CommandeDataRepository(database.commandeDao());
    }

    private static DetailCommandeDataRepository provideDetailCommandeDataSource(Context context) {
        PizzeriaDatabase database = databaseOperation(context);
        return new DetailCommandeDataRepository(database.detailCommandeDao());
    }

    public static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory provideModelFactory(Context context){
        CategorieDataRepository categorieDataRepository = provideCategorieDataSource(context);
        PlatDataRepository platDataRepository = providePlatDataSource(context);
        CommandeDataRepository commandeDataRepository = provideCommandeDataSource(context);
        DetailCommandeDataRepository detailCommandeDataRepository = provideDetailCommandeDataSource(context);
        ClientDataRepository clientDataRepository = provideClientDataSource(context);
        Executor executor = provideExecutor();

        return new ViewModelFactory(categorieDataRepository,platDataRepository,commandeDataRepository,detailCommandeDataRepository,clientDataRepository, executor);
    }

}
