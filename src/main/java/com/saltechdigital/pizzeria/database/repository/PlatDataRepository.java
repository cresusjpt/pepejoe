package com.saltechdigital.pizzeria.database.repository;

import androidx.lifecycle.LiveData;

import com.saltechdigital.pizzeria.database.dao.CategorieDao;
import com.saltechdigital.pizzeria.database.dao.PlatDao;
import com.saltechdigital.pizzeria.models.Categorie;
import com.saltechdigital.pizzeria.models.Plat;

import java.util.List;

public class PlatDataRepository {

    private final PlatDao platDao;

    public PlatDataRepository(PlatDao platDao) {
        this.platDao = platDao;
    }

    public LiveData<List<Plat>> getPlates(int idCategorie) {
        return this.platDao.getPlates(idCategorie);
    }

    public LiveData<Plat> getPlat(int id) {
        return this.platDao.getPlate(id);
    }

    public long insertPlate(Plat plat) {
        return this.platDao.insertPlate(plat);
    }

    public int updatePlate(Plat plat) {
        return this.platDao.updatPlate(plat);
    }

    public int deletePlate(Plat plat) {
        return this.platDao.deletePlate(plat);
    }

    public int deletePlates() {
        return this.platDao.deletePlates();
    }

    public LiveData<Integer> taille() {
        return this.platDao.taille();
    }
}
