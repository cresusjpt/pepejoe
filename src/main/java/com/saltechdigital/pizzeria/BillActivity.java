package com.saltechdigital.pizzeria;

import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.pizzeria.adapter.PaymentStatusAdapter;
import com.saltechdigital.pizzeria.models.PaymentStatus;
import com.saltechdigital.pizzeria.storage.SessionManager;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.tasks.PizzaApiService;
import com.saltechdigital.pizzeria.utils.ConnectionState;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class BillActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PizzaApi pizzaApi;

    private RelativeLayout relativeLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Context context;
    private SessionManager sessionManager;
    private PaymentStatusAdapter adapter;

    private DisposableSingleObserver<List<PaymentStatus>> getPaymentStatusByClient() {
        return new DisposableSingleObserver<List<PaymentStatus>>() {
            @Override
            public void onSuccess(List<PaymentStatus> value) {
                adapter = new PaymentStatusAdapter(context, value);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(adapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        pizzaApi = PizzaApiService.createDeliverApi(context);
        sessionManager = new SessionManager(context);

        Fonty.setFonts(this);
        inflateviews();

        swipeRefreshLayout.setOnRefreshListener(this::databind);
        databind();
    }

    private void databind() {
        if (ConnectionState.isConnected(context)) {
            compositeDisposable.add(
                    pizzaApi.getPaymentStatusByClient(sessionManager.getClientID())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(getPaymentStatusByClient())
            );
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(relativeLayout, R.string.not_connected, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    private void inflateviews() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        recyclerView = findViewById(R.id.recyclerView);
        relativeLayout = findViewById(R.id.view);
    }
}
