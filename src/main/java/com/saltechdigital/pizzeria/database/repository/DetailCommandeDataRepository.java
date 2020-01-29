package com.saltechdigital.pizzeria.database.repository;

import androidx.lifecycle.LiveData;

import com.saltechdigital.pizzeria.database.dao.DetailCommandeDao;
import com.saltechdigital.pizzeria.models.DetailCommande;

import java.util.List;

public class DetailCommandeDataRepository {

    private final DetailCommandeDao detailCommandeDao;

    public DetailCommandeDataRepository(DetailCommandeDao detailCommandeDao) {
        this.detailCommandeDao = detailCommandeDao;
    }

    public LiveData<List<DetailCommande>> getAll(int idClient) {
        return this.detailCommandeDao.getAll(idClient);
    }

    public LiveData<DetailCommande> get(int id) {
        return this.detailCommandeDao.get(id);
    }

    public long insert(DetailCommande detailCommande) {
        return this.detailCommandeDao.insert(detailCommande);
    }

    public int update(DetailCommande detailCommande) {
        return this.detailCommandeDao.update(detailCommande);
    }

    public int delete(DetailCommande detailCommande) {
        return this.detailCommandeDao.delete(detailCommande);
    }

    public int deleteAll() {
        return this.detailCommandeDao.deletes();
    }

    public LiveData<Integer> taille() {
        return this.detailCommandeDao.taille();
    }
}
