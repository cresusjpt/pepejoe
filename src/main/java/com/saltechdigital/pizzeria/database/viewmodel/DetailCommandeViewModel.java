package com.saltechdigital.pizzeria.database.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.saltechdigital.pizzeria.database.repository.CommandeDataRepository;
import com.saltechdigital.pizzeria.database.repository.DetailCommandeDataRepository;
import com.saltechdigital.pizzeria.models.Client;
import com.saltechdigital.pizzeria.models.Commande;
import com.saltechdigital.pizzeria.models.DetailCommande;

import java.util.List;
import java.util.concurrent.Executor;

public class DetailCommandeViewModel extends ViewModel {

    private final DetailCommandeDataRepository detailcommandeDataSource;
    private final Executor executor;

    private Context context;

    @Nullable
    private LiveData<DetailCommande> currentDetailCommande;
    private LiveData<List<DetailCommande>> listDetailCommade;

    /*private DisposableSingleObserver<List<Plat>> getPlatByCategorie() {
        return new DisposableSingleObserver<List<Plat>>() {
            @Override
            public void onSuccess(List<Plat> value) {
                for (Plat plat : value) {
                    createPlat(plat);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("JEANPAUL", "plat view model getPlatByCategorie error: ");
            }
        };
    }*/

    public DetailCommandeViewModel(DetailCommandeDataRepository detailcommandeDataSource, Executor executor) {
        this.detailcommandeDataSource = detailcommandeDataSource;
        this.executor = executor;
    }

    public void init(int id) {
        if (this.currentDetailCommande != null) {
            return;
        }
        currentDetailCommande = detailcommandeDataSource.get(id);
    }

    public void init(Context context,@NonNull Commande commande) {
        this.context = context;
        if (this.listDetailCommade != null) {
            Log.d("JEANPAUL", "init: ");
            return;
        }
        listDetailCommade = detailcommandeDataSource.getAll(commande.getIdCommande());
    }

    public LiveData<List<DetailCommande>> getAll() {
        return this.listDetailCommade;
    }

    public LiveData<DetailCommande> get(int id) {
        return this.currentDetailCommande;
    }

    public void insert(DetailCommande detailCommande) {
        executor.execute(() -> {
            detailcommandeDataSource.insert(detailCommande);
        });
    }

    public void update(DetailCommande detailCommande) {
        executor.execute(() -> {
            detailcommandeDataSource.update(detailCommande);
        });
    }

    public void delete(DetailCommande detailCommande) {
        executor.execute(() -> {
            detailcommandeDataSource.delete(detailCommande);
        });
    }

    public void deleteAll() {
        executor.execute(detailcommandeDataSource::deleteAll);
    }

    public LiveData<Integer> taille() {
        return detailcommandeDataSource.taille();
    }
}
