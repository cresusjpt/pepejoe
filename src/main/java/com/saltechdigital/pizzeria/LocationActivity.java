package com.saltechdigital.pizzeria;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.pizzeria.utils.Config;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.saltechdigital.pizzeria.utils.Config.DEFAULT_ZOOM;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    public static final int FINE_LOCATION_PERMISSION = 7;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private GoogleMap mMap;
    private LatLng position;
    private LatLng lome = new LatLng(6.1256082, 1.22327);
    private CameraPosition cameraPosition;
    //the entry points to the Places API
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastLocation;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private PlacesClient placesClient;

    private RelativeLayout locationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            lastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), DEFAULT_ZOOM));
            }
        }
        setContentView(R.layout.activity_location);
        locationView = findViewById(R.id.location_view);

        // Construct a GeoDataClient.
        geoDataClient = com.google.android.gms.location.places.Places.getGeoDataClient(this);

        // Construct a PlaceDetectionClient.
        placeDetectionClient = com.google.android.gms.location.places.Places.getPlaceDetectionClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_close_24);*/

        Button btGetLocation = findViewById(R.id.btn_get_location);
        btGetLocation.setOnClickListener(view -> {
            if (position != null) {
                Intent result = new Intent();
                result.putExtra("latitude", position.latitude);
                result.putExtra("longitude", position.longitude);
                if (lastLocation != null) {
                    result.putExtra("altitude", lastLocation.getAltitude());
                    result.putExtra("accuracy", (int) lastLocation.getAccuracy());
                }
                setResult(RESULT_OK, result);
                finish();
            } else {
                Snackbar.make(locationView, R.string.pick_location, Snackbar.LENGTH_SHORT);
            }
        });

        autocompletePlaceSearch();

        Fonty.setFonts(this);
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void autocompletePlaceSearch() {
        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                addMarker(place);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(Config.TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setTrafficEnabled(true);

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_infor,
                        findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        locationProcess();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    lastLocation = task.getResult();
                    if (lastLocation != null) {
                        position = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(lastLocation.getLatitude(),
                                        lastLocation.getLongitude()), DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    } else
                        mMap.animateCamera(CameraUpdateFactory
                                .newLatLngZoom(lome, DEFAULT_ZOOM));
                } else {
                    Log.d(Config.TAG, "Current location is null. Using defaults.");
                    Log.e(Config.TAG, "Exception: %s", task.getException());
                    mMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(lome, DEFAULT_ZOOM));
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();
            @SuppressWarnings("MissingPermission") final Task<FindCurrentPlaceResponse> responseTask = placesClient.findCurrentPlace(request);
            responseTask
                    .addOnSuccessListener(placeResponse -> {
                        List<com.google.android.libraries.places.api.model.PlaceLikelihood> placeLikelihoodList = placeResponse.getPlaceLikelihoods();

                        // Set the count, handling cases where less than 5 entries are returned.
                        int count;
                        if (placeLikelihoodList.size() < M_MAX_ENTRIES) {
                            count = placeLikelihoodList.size();
                        } else {
                            count = M_MAX_ENTRIES;
                        }

                        int i = 0;
                        mLikelyPlaceNames = new String[count];
                        mLikelyPlaceAddresses = new String[count];
                        mLikelyPlaceAttributions = new String[count];
                        mLikelyPlaceLatLngs = new LatLng[count];

                        for (com.google.android.libraries.places.api.model.PlaceLikelihood placeLikelihood : placeLikelihoodList) {
                            // Build a list of likely places to show the user.
                            mLikelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                            mLikelyPlaceAddresses[i] = placeLikelihood.getPlace()
                                    .getAddress();
                            mLikelyPlaceAttributions[i] = null/*Objects.requireNonNull(placeLikelihood.getPlace().getAttributions()).get(0)*/;
                            mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                            i++;
                            if (i > (count - 1)) {
                                break;
                            }
                        }

                        // Show a dialog offering the user the list of likely places, and add a
                        // marker at the selected place.
                        openPlacesDialog();
                    })
                    .addOnFailureListener(e -> {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.e("JEANPAUL", "Place not found: " + apiException.getStatusCode());
                        }
                        Log.e("JEANPAUL", "Exception: %s", e);
                    });
        } else {
            // The user has not granted permission.
            Log.i("JEANPAUL", "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(lome)
                    .snippet(getString(R.string.default_info_snippet)));

            EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale), FINE_LOCATION_PERMISSION, perms);
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            // The "which" argument contains the position of the selected item.
            LatLng markerLatLng = mLikelyPlaceLatLngs[which];
            String markerSnippet = mLikelyPlaceAddresses[which];
            if (mLikelyPlaceAttributions[which] != null) {
                markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.

            mMap.addMarker(new MarkerOptions()
                    .title(mLikelyPlaceNames[which])
                    .position(markerLatLng)
                    .snippet(markerSnippet));

            // Position the map's camera at the location of the marker.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                    DEFAULT_ZOOM));
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == FINE_LOCATION_PERMISSION) {
            locationProcess();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void locationProcess() {
        String[] perms = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        if (!EasyPermissions.hasPermissions(this, perms)) {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            EasyPermissions.requestPermissions(LocationActivity.this, getString(R.string.permission_rationale), FINE_LOCATION_PERMISSION, perms);
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            mMap.setOnMyLocationClickListener(this::addMarker);

            mMap.setOnMapClickListener(this::addMarker);
        }
    }

    private void addMarker(Place place) {
        mMap.clear();
        position = place.getLatLng();
        mMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(place.getLatLng())).draggable(true).title(place.getName())).setDraggable(true);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

        Log.i(Config.TAG, "Place: " + place.getName() + ", " + place.getId());
    }

    private void addMarker(Location location) {
        mMap.clear();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        position = latLng;
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        Log.i(Config.TAG, "Location latitude: " + location.getLatitude() + ", Longitude" + location.getLongitude());
    }

    private void addMarker(LatLng latLng) {
        mMap.clear();
        position = latLng;
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        Log.i(Config.TAG, "Latitude: " + latLng.latitude + ", " + latLng.longitude);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.location, menu);

        /*MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) searchItem.getActionView();
        //searchView.setQueryHint("Rechercher");

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                *//*   if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }*//*
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                return false;
            }
        });*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                return true;
            case R.id.action_settings:
                showCurrentPlace();
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
