package com.saltechdigital.pizzeria.database.repository;

import androidx.lifecycle.LiveData;

import com.saltechdigital.pizzeria.database.dao.ClientDao;
import com.saltechdigital.pizzeria.database.dao.CommandeDao;
import com.saltechdigital.pizzeria.models.Client;
import com.saltechdigital.pizzeria.models.Commande;

import java.util.List;

public class ClientDataRepository {

    private final ClientDao clientDao;

    public ClientDataRepository(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    public LiveData<List<Client>> getClients() {
        return this.clientDao.getClients();
    }

    public LiveData<Client> getClient(int id) {
        return this.clientDao.getCient(id);
    }

    public Client getBruteCient(int id) {
        return this.clientDao.bruteGetCient(id);
    }

    public long insertClient(Client client) {
        return this.clientDao.insertClient(client);
    }

    public int updateClient(Client client) {
        return this.clientDao.updateClient(client);
    }

    public int deleteClient(Client client) {
        return this.clientDao.deleteClient(client);
    }

    public int deleteClients() {
        return this.clientDao.deleteClients();
    }

    public LiveData<Integer> taille() {
        return this.clientDao.taille();
    }
}
