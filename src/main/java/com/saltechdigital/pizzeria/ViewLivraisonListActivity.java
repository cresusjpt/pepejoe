package com.saltechdigital.pizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.saltechdigital.pizzeria.adapter.PlatAdapter;
import com.saltechdigital.pizzeria.database.viewmodel.PlatViewModel;
import com.saltechdigital.pizzeria.injections.Injection;
import com.saltechdigital.pizzeria.injections.ViewModelFactory;
import com.saltechdigital.pizzeria.models.Plat;
import com.saltechdigital.pizzeria.models.Services;
import com.saltechdigital.pizzeria.utils.Config;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewLivraisonListActivity extends AppCompatActivity {

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

    private Services service;
    private String serviceTag;

    List<String> stringList = new ArrayList<>();

    private PlatViewModel platViewModel;

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

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);



        if (serviceTag.equals("pharmacie")) {
            tagText.setText(R.string.pharmacy_tag);
        }
        setTitle(serviceTag);
        fab.setOnClickListener(v -> Snackbar.make(view, R.string.special_demande, Snackbar.LENGTH_LONG)
                .setAction(R.string.rb_yes, view -> {
                    Intent intentStart = new Intent(ViewLivraisonListActivity.this, CreateLivraisonActivity.class);
                    intent.putExtra(Config.TAG, serviceTag);
                    startActivity(intentStart);
                }).show());

        Fonty.setFonts(this);
    }

    private void inflateViews(List<Plat> plats) {
        emptyText.setVisibility(View.GONE);
        if (plats.isEmpty()){
            emptyText.setVisibility(View.VISIBLE);
        }

        if (!service.getTag().equals("menus") && !service.getTag().equals("magic")) {
            Log.d("JEANPAUL", "inflateViews: ");
            adapter = new PlatAdapter(this, plats);
            viewRecycleView.setLayoutManager(new GridLayoutManager(this, 2));
            viewRecycleView.setAdapter(adapter);
        }
    }

    private void configViewModel(Plat plat) {
        ViewModelFactory factory = Injection.provideModelFactory(this);
        this.platViewModel = ViewModelProviders.of(this, factory).get(PlatViewModel.class);
        this.platViewModel.init(this, plat);
    }

    private void getAllPlat() {
        this.platViewModel.getListPlats().observe(this, this::inflateViews);
    }
}
