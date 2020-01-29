package com.saltechdigital.pizzeria.database.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.saltechdigital.pizzeria.database.repository.CommandeDataRepository;
import com.saltechdigital.pizzeria.models.Client;
import com.saltechdigital.pizzeria.models.Commande;

import java.util.List;
import java.util.concurrent.Executor;
public class CommandeViewModel extends ViewModel {

    private final CommandeDataRepository commandeDataSource;
    private final Executor executor;

    private Context context;

    @Nullable
    private LiveData<Commande> currentCommande;
    private LiveData<List<Commande>> listCommande;

    public CommandeViewModel(CommandeDataRepository commandeDataSource, Executor executor) {
        this.commandeDataSource = commandeDataSource;
        this.executor = executor;
    }

    public void init(int id) {
        if (this.currentCommande != null) {
            return;
        }
        currentCommande = commandeDataSource.getCommande(id);
    }

    public void init(Context context,@NonNull Client client) {
        this.context = context;
        if (this.listCommande != null) {
            Log.d("JEANPAUL", "init: ");
            return;
        }
        listCommande = commandeDataSource.getCommandes(client.getIdClient());
    }

    public LiveData<List<Commande>> getListCommande() {
        return this.listCommande;
    }

    public LiveData<Commande> getCommande(int id) {
        return this.currentCommande;
    }

    public LiveData<Commande> getStatutCommande(String statut) {
        return this.commandeDataSource.getStatutCommande(statut);
    }

    public void createCommande(Commande commande) {
        executor.execute(() -> commandeDataSource.insertCommande(commande));
    }

    public int brutCreateCommande(Commande commande){
         return this.commandeDataSource.insertCommande(commande);
    }

    public void updateCommande(Commande commande) {
        executor.execute(() -> {
            commandeDataSource.updateCommande(commande);
        });
    }

    public void deleteCommande(Commande commande) {
        executor.execute(() -> {
            commandeDataSource.deleteCommande(commande);
        });
    }

    public void deletePlats() {
        executor.execute(commandeDataSource::deleteCommande);
    }

    public LiveData<Integer> taille() {
        return commandeDataSource.taille();
    }
}
