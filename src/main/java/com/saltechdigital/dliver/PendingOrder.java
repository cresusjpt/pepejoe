package com.saltechdigital.dliver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.dliver.adapter.PendingOrderAdapter;
import com.saltechdigital.dliver.models.Livraison;
import com.saltechdigital.dliver.storage.SessionManager;
import com.saltechdigital.dliver.tasks.DeliverApi;
import com.saltechdigital.dliver.tasks.DeliverApiService;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class PendingOrder extends Fragment {
    private RecyclerView pendingRecycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DeliverApi deliverApi;
    private Context context;
    private ProgressDialog progressDialog;

    private PendingOrderAdapter adapter;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private DisposableSingleObserver<List<Livraison>> getPendingLivraisons() {
        return new DisposableSingleObserver<List<Livraison>>() {
            @Override
            public void onSuccess(List<Livraison> value) {
                progressDialog(false);
                adapter = new PendingOrderAdapter(context, value);
                pendingRecycler.setLayoutManager(new LinearLayoutManager(context));
                pendingRecycler.setAdapter(adapter);
                pendingRecycler.setItemAnimator(new DefaultItemAnimator());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                progressDialog(false);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fonty.setFonts(container);
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.pending_order, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        deliverApi = DeliverApiService.createDeliverApi(getContext());
        inflateViews(Objects.requireNonNull(getActivity()));
        databind();
    }

    private void inflateViews(Activity activity) {
        pendingRecycler = activity.findViewById(R.id.pending_rv);
        swipeRefreshLayout = activity.findViewById(R.id.swipe_pending_order);
        swipeRefreshLayout.setOnRefreshListener(this::databind);
    }

    private void progressDialog(boolean show) {
        if (show) {
            progressDialog = ProgressDialog.show(context, null, getString(R.string.loading), true);
        } else if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void databind() {
        if (compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        progressDialog(true);
        compositeDisposable.add(
                deliverApi.getPendingLivraison(new SessionManager(getContext()).getClientID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(getPendingLivraisons())
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
    }
}
