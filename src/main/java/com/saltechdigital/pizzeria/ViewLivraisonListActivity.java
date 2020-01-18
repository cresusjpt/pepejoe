package com.saltechdigital.pizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.pizzeria.adapter.PizzaAdapter;
import com.saltechdigital.pizzeria.adapter.ServicesAdapter;
import com.saltechdigital.pizzeria.utils.Config;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewLivraisonListActivity extends AppCompatActivity {

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.view) CoordinatorLayout view;

    @BindView(R.id.view_list) RecyclerView viewRecycleView;
    @BindView(R.id.tag_text) TextView tagText;
    @BindView(R.id.visible_text) TextView emptyText;
    @BindView(R.id.tag_content) CardView cardView;

    private String serviceTag;

    List<String> stringList = new ArrayList<>();

    private PizzaAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_livraison_list);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        inflateViews();
        final Intent intent = getIntent();
        if (intent.hasExtra(Config.TAG)) {
            serviceTag = intent.getStringExtra(Config.TAG);
        } else {
            cardView.setVisibility(View.GONE);
        }

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

    private void inflateViews() {

        stringList.add("La Jingle bells");
        stringList.add("Chaudière");
        stringList.add("Charcutaille");
        stringList.add("Paysous");
        stringList.add("Pepe Burger");
        stringList.add("La Chargée");
        stringList.add("St Jacques");
        stringList.add("Duo Saumons");
        stringList.add("Délice de Foie Gras");
        stringList.add("Montagnarde");
        stringList.add("4 Fromages");
        stringList.add("Merguez");
        stringList.add("Royale");
        stringList.add("Dauphine");
        stringList.add("Végétarienne");
        stringList.add("Poulette");

        adapter = new PizzaAdapter(this, stringList);
        viewRecycleView.setLayoutManager(new GridLayoutManager(this, 2));
        viewRecycleView.setAdapter(adapter);
        /*
        TODO
        check later if recycler adapter is empty for showing or not the textview
         */
        emptyText.setVisibility(View.GONE);
    }
}
