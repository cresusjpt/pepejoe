package com.saltechdigital.pizzeria.database.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.saltechdigital.pizzeria.database.repository.ClientDataRepository;
import com.saltechdigital.pizzeria.database.repository.CommandeDataRepository;
import com.saltechdigital.pizzeria.models.Client;
import com.saltechdigital.pizzeria.models.Commande;

import java.util.List;
import java.util.concurrent.Executor;

public class ClientViewModel extends ViewModel {

    private final ClientDataRepository clientDataSource;
    private final Executor executor;

    private Context context;

    @Nullable
    private LiveData<Client> currentClient;
    private LiveData<List<Client>> listClient;

    public ClientViewModel(ClientDataRepository clientDataSource, Executor executor) {
        this.clientDataSource = clientDataSource;
        this.executor = executor;
    }

    public void init(int id) {
        if (this.currentClient != null) {
            return;
        }
        currentClient = clientDataSource.getClient(id);
    }

    public void init(Context context,@NonNull Client client) {
        this.context = context;
        if (this.listClient != null) {
            Log.d("JEANPAUL", "init: ");
            return;
        }
        listClient = clientDataSource.getClients();
    }

    public LiveData<List<Client>> getListClient() {
        return this.listClient;
    }

    public LiveData<Client> getCurrentClient(int id) {
        return this.currentClient;
    }

    public Client getBruteClient(int idClient){
        return this.clientDataSource.getBruteCient(idClient);
    }

    public void createClient(Client client) {
        executor.execute(() -> {
            clientDataSource.insertClient(client);
        });
    }

    public void brutCreateClient(Client client){
        this.clientDataSource.insertClient(client);
    }

    public void updateClient(Client client) {
        executor.execute(() -> {
            clientDataSource.updateClient(client);
        });
    }

    public void deleteClient(Client client) {
        executor.execute(() -> {
            clientDataSource.deleteClient(client);
        });
    }

    public int deleteClient() {
        executor.execute(clientDataSource::deleteClients);
        return 1;
    }

    public LiveData<Integer> taille() {
        return clientDataSource.taille();
    }
}
