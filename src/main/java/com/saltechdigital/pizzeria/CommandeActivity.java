package com.saltechdigital.pizzeria;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.marcinorlowski.fonty.Fonty;
import com.rowland.cartcounter.view.CartCounterActionView;
import com.saltechdigital.pizzeria.adapter.CommandeAdapter;
import com.saltechdigital.pizzeria.database.viewmodel.CommandeViewModel;
import com.saltechdigital.pizzeria.database.viewmodel.DetailCommandeViewModel;
import com.saltechdigital.pizzeria.injections.Injection;
import com.saltechdigital.pizzeria.injections.ViewModelFactory;
import com.saltechdigital.pizzeria.models.Commande;
import com.saltechdigital.pizzeria.models.DetailCommande;
import com.saltechdigital.pizzeria.utils.Config;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommandeActivity extends AppCompatActivity {

    @BindView(R.id.detcom_view)
    LinearLayout view;
    @BindView(R.id.rv_basket)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_valid)
    Button button;

    private Commande commande;

    private CommandeAdapter adapter;

    private CartCounterActionView actionView;

    private DetailCommandeViewModel detailCommandeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commande);
        Intent intentSent = getIntent();
        if (intentSent.hasExtra(ViewLivraisonListActivity.COMMANDE_PARCELABLE) && intentSent.getParcelableExtra(ViewLivraisonListActivity.COMMANDE_PARCELABLE) != null){
            commande = intentSent.getParcelableExtra(ViewLivraisonListActivity.COMMANDE_PARCELABLE);
            Log.d("JEANPAUL", "onCreate: "+commande.getDateCommande());
            configViewModel();
        }
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Fonty.setFonts(this);
    }

    private void configViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideModelFactory(this);
        this.detailCommandeViewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailCommandeViewModel.class);
        this.detailCommandeViewModel.init(this,commande);
        this.detailCommandeViewModel.getAll().observe(this,this::getAllCommande );
    }

    private void getAllCommande(List<DetailCommande> detailCommandes){
        adapter = new CommandeAdapter(this,this,commande,detailCommandes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principale, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_addcart);
        actionView = (CartCounterActionView) item.getActionView();
        actionView.setItemData(menu, item);
        actionView.setCount(0);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(SettingsActivity.class);
            return true;
        }
        if (id == R.id.action_addcart) {
            actionView.setCount(actionView.getCount() + 1);
        }

        return super.onOptionsItemSelected(item);
    }

    private void startActivity(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
