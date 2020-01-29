package com.saltechdigital.pizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.marcinorlowski.fonty.Fonty;
import com.rowland.cartcounter.view.CartCounterActionView;
import com.saltechdigital.pizzeria.adapter.PlatAdapter;
import com.saltechdigital.pizzeria.database.viewmodel.CommandeViewModel;
import com.saltechdigital.pizzeria.database.viewmodel.DetailCommandeViewModel;
import com.saltechdigital.pizzeria.database.viewmodel.PlatViewModel;
import com.saltechdigital.pizzeria.injections.Injection;
import com.saltechdigital.pizzeria.injections.ViewModelFactory;
import com.saltechdigital.pizzeria.models.Commande;
import com.saltechdigital.pizzeria.models.DetailCommande;
import com.saltechdigital.pizzeria.models.Plat;
import com.saltechdigital.pizzeria.models.Services;
import com.saltechdigital.pizzeria.utils.Config;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewLivraisonListActivity extends AppCompatActivity implements PlatAdapter.PlatCalback {

    public static final String COMMANDE_PARCELABLE = "COMMANDE_PARCELABLE";

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.view)
    CoordinatorLayout view;

    @BindView(R.id.view_list)
    RecyclerView viewRecycleView;
    @BindView(R.id.tag_text)
    TextView tagText;
    @BindView(R.id.visible_text)
    TextView emptyText;
    @BindView(R.id.tag_content)
    CardView cardView;

    private CartCounterActionView actionView;

    private Services service;
    private String serviceTag;

    private Commande commande;

    private PlatViewModel platViewModel;
    private DetailCommandeViewModel detailCommandeViewModel;
    private CommandeViewModel commandeViewModel;

    private PlatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_livraison_list);
        ButterKnife.bind(this);

        final Intent intent = getIntent();
        if (intent.hasExtra(Config.TAG)) {
            service = intent.getParcelableExtra(Config.TAG);
            serviceTag = service.getTag();

            Plat plat = new Plat();
            plat.setIdCategorie(service.getId());
            configViewModel(plat);
        } else {
            cardView.setVisibility(View.GONE);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getAllPlat();
        getCreateCommande();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle(serviceTag);
        fab.setOnClickListener(v -> Snackbar.make(view, R.string.special_demande, Snackbar.LENGTH_LONG)
                .setAction(R.string.rb_yes, view -> {
                    Intent intentStart = new Intent(ViewLivraisonListActivity.this, CreateLivraisonActivity.class);
                    intent.putExtra(Config.TAG, serviceTag);
                    startActivity(intentStart);
                }).show());

        Fonty.setFonts(this);
    }

    private void configViewModel(Plat plat) {
        ViewModelFactory factory = Injection.provideModelFactory(this);
        this.platViewModel = ViewModelProviders.of(this, factory).get(PlatViewModel.class);
        this.detailCommandeViewModel = ViewModelProviders.of(this, factory).get(DetailCommandeViewModel.class);
        this.commandeViewModel = ViewModelProviders.of(this, factory).get(CommandeViewModel.class);
        this.platViewModel.init(this, plat);
    }

    private void inflateViews(List<Plat> plats) {
        emptyText.setVisibility(View.GONE);
        if (plats.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        }

        if (!service.getTag().equals("menus") && !service.getTag().equals("magic")) {
            Log.d("JEANPAUL", "inflateViews: ");
            adapter = new PlatAdapter(this, this, plats, this, service.getTag());
            viewRecycleView.setLayoutManager(new GridLayoutManager(this, 2));
            viewRecycleView.setAdapter(adapter);
        }
    }

    private void getAllPlat() {
        this.platViewModel.getListPlats().observe(this, this::inflateViews);
    }

    private void getCreateCommande() {
        this.commandeViewModel.getStatutCommande(Config.STATUT_COMMANDE_CREE).observe(this, this::commandeObserver);
    }

    private void commandeObserver(Commande commande) {
        this.commande = commande;
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
        getTaille();
        return super.onPrepareOptionsMenu(menu);
    }

    private void getTaille() {
        this.detailCommandeViewModel.taille().observe(this, this::refreshCart);
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
            Intent intent = new Intent(this, CommandeActivity.class);
            intent.putExtra(COMMANDE_PARCELABLE, commande);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void startActivity(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    @Override
    public void onPlatSuccess(Commande cmde, Plat plat,String epaisseur,String taille,int values) {
        this.commande = cmde;
        DetailCommande detailCommande = new DetailCommande();
        detailCommande.setIdCommande(commande.getIdCommande());
        detailCommande.setIdPlat(plat.getIdPlat());
        detailCommande.setInfos(epaisseur);
        switch (values) {
            case 4:
                detailCommande.setPrixDetail(plat.getPrixPetite());
                break;
            case 5:
                detailCommande.setPrixDetail(plat.getPrixMoyen());
                break;
            case 6:
                detailCommande.setPrixDetail(plat.getPrix_geante());
                break;
        }
        if (!service.getTag().equals("menus")){
            detailCommande.setIsMenu(0);
        }else {
            detailCommande.setIsMenu(1);
        }

        this.detailCommandeViewModel.insert(detailCommande);
        getTaille();
    }

    @Override
    public void onPlatError() {

    }

    private void refreshCart(Integer size) {
        actionView.setCount(size);
    }
}
