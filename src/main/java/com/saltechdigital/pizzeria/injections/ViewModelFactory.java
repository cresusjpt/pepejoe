package com.saltechdigital.pizzeria.injections;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.saltechdigital.pizzeria.database.repository.CategorieDataRepository;
import com.saltechdigital.pizzeria.database.repository.ClientDataRepository;
import com.saltechdigital.pizzeria.database.repository.CommandeDataRepository;
import com.saltechdigital.pizzeria.database.repository.DetailCommandeDataRepository;
import com.saltechdigital.pizzeria.database.repository.PlatDataRepository;
import com.saltechdigital.pizzeria.database.viewmodel.CategorieViewModel;
import com.saltechdigital.pizzeria.database.viewmodel.ClientViewModel;
import com.saltechdigital.pizzeria.database.viewmodel.CommandeViewModel;
import com.saltechdigital.pizzeria.database.viewmodel.DetailCommandeViewModel;
import com.saltechdigital.pizzeria.database.viewmodel.PlatViewModel;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final CategorieDataRepository categorieDataSource;
    private final PlatDataRepository platDataRepository;
    private final CommandeDataRepository commandeDataRepository;
    private final DetailCommandeDataRepository detailCommandeDataRepository;
    private final ClientDataRepository clientDataRepository;
    private final Executor executor;

    public ViewModelFactory(CategorieDataRepository categorieDataSource, PlatDataRepository platDataRepository, CommandeDataRepository commandeDataRepository, DetailCommandeDataRepository detailCommandeDataRepository , ClientDataRepository clientDataRepository, Executor executor) {
        this.categorieDataSource = categorieDataSource;
        this.platDataRepository = platDataRepository;
        this.commandeDataRepository = commandeDataRepository;
        this.detailCommandeDataRepository = detailCommandeDataRepository;
        this.clientDataRepository = clientDataRepository;
        this.executor = executor;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategorieViewModel.class)) {
            return (T) new CategorieViewModel(categorieDataSource, executor);
        } else if (modelClass.isAssignableFrom(PlatViewModel.class)) {
            return (T) new PlatViewModel(platDataRepository, executor);
        } else if (modelClass.isAssignableFrom(CommandeViewModel.class)) {
            return (T) new CommandeViewModel(commandeDataRepository, executor);
        } else if (modelClass.isAssignableFrom(DetailCommandeViewModel.class)) {
            return (T) new DetailCommandeViewModel(detailCommandeDataRepository, executor);
        } else if (modelClass.isAssignableFrom(ClientViewModel.class)) {
            return (T) new ClientViewModel(clientDataRepository, executor);
        }
        throw new IllegalArgumentException("View Model Inconnue");
    }
}
