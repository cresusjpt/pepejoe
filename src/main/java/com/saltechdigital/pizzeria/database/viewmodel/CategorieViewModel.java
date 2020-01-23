package com.saltechdigital.pizzeria.database.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.saltechdigital.pizzeria.database.repository.CategorieDataRepository;
import com.saltechdigital.pizzeria.models.Categorie;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.tasks.PizzaApiService;

import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CategorieViewModel extends ViewModel {

    private final CategorieDataRepository categorieDataSource;
    private final Executor executor;

    private Context context;

    @Nullable
    private LiveData<Categorie> currentCategorie;
    private LiveData<List<Categorie>> listCategories;

    private DisposableSingleObserver<List<Categorie>> getAllCategories() {
        return new DisposableSingleObserver<List<Categorie>>() {
            @Override
            public void onSuccess(List<Categorie> value) {
                for (Categorie categorie : value) {
                    Log.d("JEANPAUL", "onSuccess: ");
                    createCategorie(categorie);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("JEANPAUL", "categorie view model getAllCategorie error: ");
            }
        };
    }

    public CategorieViewModel(CategorieDataRepository categorieDataSource, Executor executor) {
        this.categorieDataSource = categorieDataSource;
        this.executor = executor;
    }

    public void init(int id) {
        if (this.currentCategorie != null) {
            return;
        }
        currentCategorie = categorieDataSource.getCategorie(id);
    }

    public void init(Context context) {
        this.context = context;
        if (this.listCategories != null) {
            Log.d("JEANPAUL", "init: ");
            return;
        } else {
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            PizzaApi pizzaApi = PizzaApiService.createDeliverApi(context);
            compositeDisposable.add(
                    pizzaApi.getCategories()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(getAllCategories())
            );
        }
        listCategories = categorieDataSource.getCategories();
    }

    public LiveData<List<Categorie>> getListCategories() {
        return this.listCategories;
    }

    public LiveData<Categorie> getCategorie(int id) {
        return this.currentCategorie;
    }

    public void createCategorie(Categorie categorie) {
        executor.execute(() -> categorieDataSource.insertCategorie(categorie));
    }

    public void updateCategorie(Categorie categorie) {
        executor.execute(() -> categorieDataSource.updateCategorie(categorie));
    }

    public void deleteCategorie(Categorie categorie) {
        executor.execute(() -> categorieDataSource.deleteCategorie(categorie));
    }

    public int deleteCategories() {
        return categorieDataSource.deleteCategories();
    }

    public int taille() {
        return categorieDataSource.taille();
    }
}
