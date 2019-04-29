package com.saltechdigital.dliver;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.dliver.adapter.SavedAddressAdapter;
import com.saltechdigital.dliver.models.Address;
import com.saltechdigital.dliver.storage.SessionManager;
import com.saltechdigital.dliver.tasks.DeliverApi;
import com.saltechdigital.dliver.tasks.DeliverApiService;
import com.saltechdigital.dliver.utils.Config;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SavedAddressActivity extends AppCompatActivity {

    private RecyclerView addressRecyclerView;
    private RelativeLayout viewGeneral;
    private Context context;
    private Button btNewAddress;
    private ProgressBar progressBar;

    private SwipeRefreshLayout swipeRefreshLayout;

    private SavedAddressAdapter addressAdapter;

    private DeliverApi deliverApi;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private DisposableSingleObserver<List<Address>> getAddress() {
        return new DisposableSingleObserver<List<Address>>() {
            @Override
            public void onSuccess(List<Address> value) {
                addressAdapter = new SavedAddressAdapter(context, value);
                addressRecyclerView.setAdapter(addressAdapter);
                addressAdapter.notifyDataSetChanged();
                showProgress(false);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                showProgress(false);
                swipeRefreshLayout.setRefreshing(false);
                Snackbar.make(viewGeneral, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                Log.e(Config.TAG, "get Address error", e);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.saved_address_title);
        inflateViews();

        btNewAddress.setOnClickListener(v -> Snackbar.make(viewGeneral, "Implementation en cours", Snackbar.LENGTH_SHORT).show());

        deliverApi = DeliverApiService.createDeliverApi(this);

        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //addressRecyclerView.addItemDecoration(new MarginItemDecoration(5));
        addressRecyclerView.setAdapter(addressAdapter);

        //addressRecyclerView.setItemAnimator(new DefaultItemAnimator());
        databind(true);
        swipeRefreshLayout.setOnRefreshListener(() -> databind(false));

        Fonty.setFonts(this);
    }

    private void inflateViews() {
        context = this;
        addressRecyclerView = findViewById(R.id.rv_sv_address);
        btNewAddress = findViewById(R.id.btn_add_address);
        progressBar = findViewById(R.id.saved_address_progress);
        viewGeneral = findViewById(R.id.rv_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        addressRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        addressRecyclerView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                addressRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.setAlpha(show ? 1 : 0);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    private void databind(boolean show) {
        if (show) {
            showProgress(true);
        }

        compositeDisposable.add(
                deliverApi.getAddress(new SessionManager(this).getClientID())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(getAddress())
        );
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
        compositeDisposable = new CompositeDisposable();
        databind(true);
        super.onResume();
    }
}
