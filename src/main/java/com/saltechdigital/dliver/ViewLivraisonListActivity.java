package com.saltechdigital.dliver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.dliver.utils.Config;

public class ViewLivraisonListActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private CoordinatorLayout view;

    private RecyclerView serviceRecycleView;
    private TextView tagText;
    private TextView emptyText;
    private CardView cardView;

    private String serviceTag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_livraison_list);

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
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> Snackbar.make(view, R.string.special_demande, Snackbar.LENGTH_LONG)
                .setAction(R.string.rb_yes, view -> {
                    Intent intentStart = new Intent(ViewLivraisonListActivity.this, CreateLivraisonActivity.class);
                    intent.putExtra(Config.TAG, serviceTag);
                    startActivity(intentStart);
                }).show());

        Fonty.setFonts(this);
    }

    private void inflateViews() {
        view = findViewById(R.id.view);
        tagText = findViewById(R.id.tag_text);
        emptyText = findViewById(R.id.visible_text);
        cardView = findViewById(R.id.tag_content);
        serviceRecycleView = findViewById(R.id.service_list);

        /*
        TODO
        check later if recycler adapter is empty for showing or not the textview
         */
        emptyText.setVisibility(View.GONE);
    }
}
