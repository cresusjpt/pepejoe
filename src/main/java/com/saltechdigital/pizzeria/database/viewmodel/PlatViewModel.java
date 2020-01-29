package com.saltechdigital.pizzeria.database.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.saltechdigital.pizzeria.database.repository.PlatDataRepository;
import com.saltechdigital.pizzeria.models.Plat;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.tasks.PizzaApiService;

import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class PlatViewModel extends ViewModel {

    private final PlatDataRepository platDataSource;
    private final Executor executor;

    private Context context;

    @Nullable
    private LiveData<Plat> currentPlat;
    private LiveData<List<Plat>> listPlat;

    private DisposableSingleObserver<List<Plat>> getPlatByCategorie() {
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
    }

    public PlatViewModel(PlatDataRepository platDataSource, Executor executor) {
        this.platDataSource = platDataSource;
        this.executor = executor;
    }

    public void init(int id) {
        if (this.currentPlat != null) {
            return;
        }
        currentPlat = platDataSource.getPlat(id);
    }

    public void init(Context context,@NonNull Plat plat) {
        this.context = context;
        if (this.listPlat != null) {
            Log.d("JEANPAUL", "init: ");
            return;
        } else {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            PizzaApi pizzaApi = PizzaApiService.createDeliverApi(context);
            compositeDisposable.add(
                    pizzaApi.getPlatByCategorie(plat.getIdCategorie())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(getPlatByCategorie())
            );
        }
        listPlat = platDataSource.getPlates(plat.getIdCategorie());
    }

    public LiveData<List<Plat>> getListPlats() {
        return this.listPlat;
    }

    public LiveData<Plat> getPlat(int id) {
        return this.currentPlat;
    }

    public void createPlat(Plat plat) {
        executor.execute(() -> {
            platDataSource.insertPlate(plat);
        });
    }

    public void updatePlat(Plat plat) {
        executor.execute(() -> {
            platDataSource.updatePlate(plat);
        });
    }

    public void deletePlat(Plat plat) {
        executor.execute(() -> {
            platDataSource.deletePlate(plat);
        });
    }

    public void deletePlats() {
        executor.execute(platDataSource::deletePlates);
    }

    public LiveData<Integer> taille() {
        return platDataSource.taille();
    }
}
