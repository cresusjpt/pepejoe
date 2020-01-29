package com.saltechdigital.pizzeria.database.repository;

import androidx.lifecycle.LiveData;

import com.saltechdigital.pizzeria.database.dao.CategorieDao;
import com.saltechdigital.pizzeria.models.Categorie;

import java.util.List;

public class CategorieDataRepository {

    private final CategorieDao categorieDao;

    public CategorieDataRepository(CategorieDao categorieDao) {
        this.categorieDao = categorieDao;
    }

    public LiveData<List<Categorie>> getCategories(){
        return this.categorieDao.getCategories();
    }

    public LiveData<Categorie> getCategorie(int id){
        return this.categorieDao.getCategorie(id);
    }

    public long insertCategorie(Categorie categorie){
        return this.categorieDao.insertCategorie(categorie);
    }

    public int updateCategorie(Categorie categorie){
        return this.categorieDao.updateCategorie(categorie);
    }

    public int deleteCategorie(Categorie categorie){
        return this.categorieDao.deleteCategorie(categorie);
    }

    public int deleteCategories(){
        return this.categorieDao.deleteCategories();
    }

    public LiveData<Integer> taille(){
        return this.categorieDao.taille();
    }
}
