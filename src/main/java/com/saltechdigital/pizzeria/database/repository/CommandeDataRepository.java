package com.saltechdigital.pizzeria.database.repository;

import androidx.lifecycle.LiveData;

import com.saltechdigital.pizzeria.database.dao.CommandeDao;
import com.saltechdigital.pizzeria.models.Commande;

import java.util.List;

import javax.xml.transform.sax.TemplatesHandler;

public class CommandeDataRepository {

    private final CommandeDao commandeDao;

    public CommandeDataRepository(CommandeDao commandeDao) {
        this.commandeDao = commandeDao;
    }

    public LiveData<List<Commande>> getCommandes(int idClient) {
        return this.commandeDao.getCommandes(idClient);
    }

    public LiveData<Commande> getCommande(int id) {
        return this.commandeDao.getCommande(id);
    }

    public LiveData<Commande> getStatutCommande(String statut) {
        return this.commandeDao.getStatutCommande(statut);
    }

    public int insertCommande(Commande commande) {
        return (int) this.commandeDao.insertCommande(commande);
    }


    public int updateCommande(Commande commande) {
        return this.commandeDao.updateCommande(commande);
    }

    public int deleteCommande(Commande commande) {
        return this.commandeDao.deleteCommande(commande);
    }

    public int deleteCommande() {
        return this.commandeDao.deleteCommandes();
    }

    public LiveData<Integer> taille() {
        return this.commandeDao.taille();
    }
}
