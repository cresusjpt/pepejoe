package com.saltechdigital.dliver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.arsy.maps_library.MapRadar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.saltechdigital.dliver.adapter.TimeLineAdapter;
import com.saltechdigital.dliver.models.Livraison;
import com.saltechdigital.dliver.models.Process;
import com.saltechdigital.dliver.models.Repere;
import com.saltechdigital.dliver.tasks.DeliverApi;
import com.saltechdigital.dliver.tasks.DeliverApiService;
import com.saltechdigital.dliver.utils.Config;
import com.saltechdigital.dliver.utils.ConnectionState;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DeliverLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapRadar mapRadar;
    private ProgressDialog progressDialog;
    private Context context;

    private Livraison livraison;
    private Process process;

    private List<Repere> reperes;

    private DeliverApi deliverApi;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private DisposableSingleObserver<List<Repere>> getRepereLivraison() {
        return new DisposableSingleObserver<List<Repere>>() {
            @Override
            public void onSuccess(List<Repere> value) {
                progressDialog(false);
                reperes = value;

                LatLng latLngChar = null;
                //LatLng latLngDeChar = null;

                for (Repere repere : reperes) {
                    if (repere.getLibRepere().equals(Config.LIB_REPERE_CHARGEMENT)) {
                        latLngChar = new LatLng(repere.getLatitude(), repere.getLongitude());
                    }
                }

                assert latLngChar != null;
                mMap.addMarker(new MarkerOptions().position(latLngChar).title(Config.LIB_REPERE_CHARGEMENT));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngChar, 14.8f));

                mapRadar = new MapRadar(mMap, latLngChar, context);
                mapRadar.withDistance(2000);
                mapRadar.withOuterCircleStrokeColor(0xfccd29);
                mapRadar.withOuterCircleTransparency(0.4f);
                mapRadar.withRadarColors(0x00fccd29, 0xfffccd29);
                mapRadar.startRadarAnimation();
            }

            @Override
            public void onError(Throwable e) {
                progressDialog(false);
                Toast.makeText(DeliverLocationActivity.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void progressDialog(boolean show) {
        if (show) {
            progressDialog = ProgressDialog.show(this, null, getString(R.string.loading), true, true);
        } else {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_location);
        context = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        livraison = intent.getParcelableExtra(TimeLineAdapter.LIVRAISON);
        process = intent.getParcelableExtra(TimeLineAdapter.PROCESS);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void databind() {
        if (ConnectionState.isConnected(context)) {
            progressDialog(true);
            compositeDisposable.add(
                    deliverApi.getRepereLivraison(livraison.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(getRepereLivraison())
            );
        } else {
            Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        context = this;
        deliverApi = DeliverApiService.createDeliverApi(this);
        databind();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if (mapRadar.isAnimationRunning()) {
            mapRadar.stopRadarAnimation();
        }*/

        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        //mapRadar.startRadarAnimation();
    }
}
