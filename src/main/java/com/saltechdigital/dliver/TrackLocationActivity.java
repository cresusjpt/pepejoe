package com.saltechdigital.dliver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.saltechdigital.dliver.adapter.TimeLineAdapter;
import com.saltechdigital.dliver.models.Livraison;
import com.saltechdigital.dliver.models.Process;
import com.saltechdigital.dliver.models.Repere;
import com.saltechdigital.dliver.tasks.DeliverApi;
import com.saltechdigital.dliver.tasks.DeliverApiService;
import com.saltechdigital.dliver.utils.Config;
import com.saltechdigital.dliver.utils.ConnectionState;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class TrackLocationActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    private Context context;

    private Intent intent;
    private Process process;
    private Livraison livraison;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private DeliverApi deliverApi;
    private List<Polyline> polylines;
    private LatLng chargeLlng = null, dechargeLlng = null;

    private DisposableSingleObserver<List<Repere>> getRepereByLIvraison() {
        return new DisposableSingleObserver<List<Repere>>() {
            @Override
            public void onSuccess(List<Repere> value) {
                Toast.makeText(context, "get repere", Toast.LENGTH_SHORT).show();
                Repere charge, decharge;
                for (Repere rep : value) {
                    if (rep.getLibRepere().equals(Config.LIB_REPERE_CHARGEMENT)) {
                        charge = rep;
                        chargeLlng = new LatLng(charge.getLatitude(), charge.getLongitude());
                        Toast.makeText(context, "get repere" + chargeLlng.latitude, Toast.LENGTH_SHORT).show();
                    } else if (rep.getLibRepere().equals(Config.LIB_REPERE_DECHARGEMENT)) {
                        decharge = rep;
                        dechargeLlng = new LatLng(decharge.getLatitude(), decharge.getLongitude());
                        Toast.makeText(context, "get repere" + dechargeLlng.latitude, Toast.LENGTH_SHORT).show();
                    }
                }
                Routing routing = new Routing.Builder()
                        .travelMode(Routing.TravelMode.DRIVING)
                        .withListener(TrackLocationActivity.this)
                        .key(getString(R.string.google_maps_key))
                        .waypoints(chargeLlng, dechargeLlng)
                        .build();

                routing.execute();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
            }
        };
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_location);
        intent = getIntent();
        context = this;
        deliverApi = DeliverApiService.createDeliverApi(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        process = intent.getParcelableExtra(TimeLineAdapter.PROCESS);
        livraison = intent.getParcelableExtra(TimeLineAdapter.LIVRAISON);
        polylines = new ArrayList<>();

        setTitle(R.string.app_name);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ConnectionState.isConnected(context)) {
            compositeDisposable.add(
                    deliverApi.getRepereLivraison(livraison.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(getRepereByLIvraison())
            );
        } else {
            Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.d(Config.TAG, "onRoutingFailure: ", e);
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int i) {
        //progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(chargeLlng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMap.moveCamera(center);


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            //int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(R.color.colorGrey));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(chargeLlng);
        //options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(dechargeLlng);
        //options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        mMap.addMarker(options);
    }

    @Override
    public void onRoutingCancelled() {

    }
}
