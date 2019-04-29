package com.saltechdigital.dliver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.dliver.adapter.PendingOrderAdapter;
import com.saltechdigital.dliver.adapter.TimeLineAdapter;
import com.saltechdigital.dliver.models.Livraison;
import com.saltechdigital.dliver.models.Process;
import com.saltechdigital.dliver.tasks.DeliverApi;
import com.saltechdigital.dliver.tasks.DeliverApiService;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration;
import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;
import xyz.sangcomz.stickytimelineview.model.SectionInfo;

public class TimeLineActivityJ extends AppCompatActivity {

    private Drawable green, red;

    private Intent intent;
    private int idLivraison;
    private Livraison livraison;
    private ProgressDialog progressDialog;
    private Context context;
    private TimeLineAdapter adapter;

    private TimeLineRecyclerView recyclerView;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private DeliverApi deliverApi;

    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<Process> processList) {
        return new RecyclerSectionItemDecoration.SectionCallback() {

            @Override
            public boolean isSection(int position) {
                return true;
            }

            @Override
            public SectionInfo getSectionHeader(int position) {
                Process process = processList.get(position);
                Drawable dot;

                switch (process.getEtat()) {
                    case 0:
                        dot = red;
                        break;
                    case 1:
                        dot = green;
                        break;
                    default:
                        dot = red;
                        break;
                }
                return new SectionInfo(getString(R.string.order_track_stade, "" + (position + 1)), "", dot);
            }
        };
    }

    private DisposableSingleObserver<List<Process>> getProcesses() {
        return new DisposableSingleObserver<List<Process>>() {
            @Override
            public void onSuccess(List<Process> value) {
                progressDialog(false);
                recyclerView.addItemDecoration(getSectionCallback(value));
                adapter = new TimeLineAdapter(context, value, livraison);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(Throwable e) {
                progressDialog(false);
            }
        };
    }


    private DisposableSingleObserver<Livraison> getLivraison() {
        return new DisposableSingleObserver<Livraison>() {
            @Override
            public void onSuccess(Livraison value) {
                livraison = value;
                if (compositeDisposable.isDisposed()) {
                    compositeDisposable = new CompositeDisposable();
                }
                compositeDisposable.add(
                        deliverApi.getProcessByLivraison(idLivraison)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(getProcesses())
                );
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void progressDialog(boolean show) {
        if (show) {
            progressDialog = ProgressDialog.show(context, null, getString(R.string.loading), true, true);
        } else {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line_java);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        setTitle(R.string.order_track);

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        context = this;
        deliverApi = DeliverApiService.createDeliverApi(this);
        intent = getIntent();
        idLivraison = intent.getIntExtra(PendingOrderAdapter.LIVRAISON_ID, 0);
        inflateView();
        initDrawable();
        progressDialog(true);
        databind();

        Fonty.setFonts(this);
    }

    private void inflateView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void databind() {
        compositeDisposable.add(
                deliverApi.getLivraison(idLivraison)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(getLivraison())
        );
    }

    private void initDrawable() {
        green = AppCompatResources.getDrawable(this, R.drawable.time_line_green);
        red = AppCompatResources.getDrawable(this, R.drawable.time_line_red);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
    }
}
