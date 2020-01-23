package com.saltechdigital.pizzeria.injections;

import android.content.Context;

import com.saltechdigital.pizzeria.database.PizzeriaDatabase;
import com.saltechdigital.pizzeria.database.repository.CategorieDataRepository;
import com.saltechdigital.pizzeria.database.repository.PlatDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    public static CategorieDataRepository provideCategorieDataSource(Context context) {
        PizzeriaDatabase database = PizzeriaDatabase.getInstance(context);
        return new CategorieDataRepository(database.categorieDao());
    }

    public static PlatDataRepository providePlatDataSource(Context context) {
        PizzeriaDatabase database = PizzeriaDatabase.getInstance(context);
        return new PlatDataRepository(database.platDao());
    }

    public static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory provideModelFactory(Context context){
        CategorieDataRepository categorieDataRepository = provideCategorieDataSource(context);
        PlatDataRepository platDataRepository = providePlatDataSource(context);
        Executor executor = provideExecutor();

        return new ViewModelFactory(categorieDataRepository,platDataRepository,executor);
    }
}
