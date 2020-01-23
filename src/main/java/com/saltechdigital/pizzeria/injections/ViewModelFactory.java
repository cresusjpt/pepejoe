package com.saltechdigital.pizzeria.injections;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.saltechdigital.pizzeria.database.repository.CategorieDataRepository;
import com.saltechdigital.pizzeria.database.repository.PlatDataRepository;
import com.saltechdigital.pizzeria.database.viewmodel.CategorieViewModel;
import com.saltechdigital.pizzeria.database.viewmodel.PlatViewModel;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final CategorieDataRepository categorieDataSource;
    private final PlatDataRepository platDataRepository;
    private final Executor executor;

    public ViewModelFactory(CategorieDataRepository categorieDataSource, PlatDataRepository platDataRepository, Executor executor) {
        this.categorieDataSource = categorieDataSource;
        this.platDataRepository = platDataRepository;
        this.executor = executor;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategorieViewModel.class)){
            return (T) new CategorieViewModel(categorieDataSource,executor);
        }else if (modelClass.isAssignableFrom(PlatViewModel.class)){
            return (T) new PlatViewModel(platDataRepository,executor);
        }
        throw new IllegalArgumentException("View Model Inconnue");
    }
}
