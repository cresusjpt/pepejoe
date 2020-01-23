package com.saltechdigital.pizzeria;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.pizzeria.adapter.NotificationAdapter;
import com.saltechdigital.pizzeria.models.Notifications;
import com.saltechdigital.pizzeria.storage.SessionManager;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.tasks.PizzaApiService;
import com.saltechdigital.pizzeria.utils.ConnectionState;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class NotificationActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView notifRv;
    private RelativeLayout relativeLayout;

    private ProgressDialog progressDialog;
    private Context context;
    private NotificationAdapter adapter;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PizzaApi pizzaApi;

    private DisposableSingleObserver<List<Notifications>> getNotifications() {
        return new DisposableSingleObserver<List<Notifications>>() {
            @Override
            public void onSuccess(List<Notifications> value) {
                adapter = new NotificationAdapter(context, value);
                notifRv.setAdapter(adapter);
                progressDialog(false);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                progressDialog(false);
                Snackbar.make(relativeLayout, R.string.connection_error, Snackbar.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        context = this;
        pizzaApi = PizzaApiService.createDeliverApi(context);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        inflateViews();
        progressDialog(true);
        databind();

        Fonty.setFonts(this);
    }

    private void inflateViews() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::databind);
        notifRv = findViewById(R.id.notif_rv);
        notifRv.setLayoutManager(new LinearLayoutManager(context));
        notifRv.setItemAnimator(new DefaultItemAnimator());
        relativeLayout = findViewById(R.id.notif_rl);
    }

    private void progressDialog(boolean show) {
        if (show) {
            progressDialog = ProgressDialog.show(context, null, getString(R.string.loading), true, true);
        } else {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private void databind() {
        if (ConnectionState.isConnected(context)) {
            compositeDisposable.add(
                    pizzaApi.getNotifByClient(new SessionManager(context).getClientID())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(getNotifications())
            );
        } else {
            Snackbar.make(relativeLayout, R.string.not_connected, Snackbar.LENGTH_SHORT).show();
            progressDialog(false);
        }

    }
}
