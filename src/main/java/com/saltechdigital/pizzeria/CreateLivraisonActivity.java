package com.saltechdigital.pizzeria;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.pizzeria.ZXingScanner.IntentIntegrator;
import com.saltechdigital.pizzeria.ZXingScanner.IntentResult;
import com.saltechdigital.pizzeria.models.Livraison;
import com.saltechdigital.pizzeria.models.Process;
import com.saltechdigital.pizzeria.models.Repere;
import com.saltechdigital.pizzeria.models.User;
import com.saltechdigital.pizzeria.storage.SessionManager;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.tasks.PizzaApiService;
import com.saltechdigital.pizzeria.utils.Config;
import com.saltechdigital.pizzeria.utils.Utils;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.saltechdigital.pizzeria.adapter.PendingOrderAdapter.LIVRAISON_ID;

public class CreateLivraisonActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, RadioGroup.OnCheckedChangeListener {

    public static final int PIX_REQUEST_CODE = 3;
    public static final int IMAGE_REQUEST_CODE = 4;
    public static final int LOCATION_REQUEST_CODE = 5;
    public static final int FIND_LOCATION_REQUEST_CODE1 = 6;
    public static final int FIND_LOCATION_REQUEST_CODE2 = 7;

    public static int afterRequestCode = 0;
    String colisName;
    String descriptionColis;
    String valeurColis;
    String chargmentAdress;
    String destinationAdress;
    String expeditName;
    String expeditAdress;
    String destinataireName;
    String destinataireAdress;
    private Context context;
    private double price;
    private int idLivraison;
    private TextInputEditText mColisName;
    private TextInputEditText mColisDesc;
    private TextInputEditText mColisValeur;
    private TextInputEditText mChargmentAddress;
    private TextInputEditText mDestinAddress;
    private TextInputEditText mExpeditName;
    private TextInputEditText mExpeditAddress;
    private TextInputEditText mDestinName;
    private TextInputEditText mDestinataireAddress;

    private TextInputLayout expeditPhoneLayout;
    private TextInputLayout destinPhoneLayout;

    private ArrayList<String> images = new ArrayList<>();
    private Options options;

    private RadioGroup radioGroup;
    private RadioButton radioYes;
    private RadioButton radioNo;

    private LinearLayout linearHide;
    private RelativeLayout principaleView;
    private ScrollView scrollView;

    private AppCompatSpinner poidsSpinner;
    private ProgressBar progressBar;

    private IntlPhoneInput mExpeditPhoneNumber;
    private IntlPhoneInput mDestinPhoneNumber;

    private ImageButton mColisCodeBarre;
    private ImageButton mColisAddPhoto;
    private ImageButton mChargmentLoc;
    private ImageButton mDestinLoc;

    private LatLng chargmentLatLng;
    private LatLng destinationtLatLng;

    private PizzaApi pizzaApi;

    private int repereChargement;
    private int repereDechargement;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private double price(float distance) {
        double distanceInKm = distance / 1000;
        Log.d(Config.TAG, "price: distance in km" + distanceInKm);
        double returnable;
        if (distanceInKm <= 10) {
            returnable = distanceInKm * 100;
        } else if (distanceInKm > 10 && distanceInKm <= 40) {
            returnable = distanceInKm * 85;
        } else {
            returnable = distanceInKm * 70;
        }
        return returnable;
    }

    private DisposableSingleObserver<Repere> addRepere() {
        Log.d(Config.TAG, "addRepere: method");
        return new DisposableSingleObserver<Repere>() {
            @Override
            public void onSuccess(Repere value) {
                if (value.getLibRepere().equals(Config.LIB_REPERE_CHARGEMENT)) {//on a ajouté le repere de collecte
                    Log.d(Config.TAG, "onSuccess: chargement repere add");
                    repereChargement = value.getId();
                    //point de dechargement
                    //on ajoute le repere de livraison
                    Repere repere = new Repere();
                    repere.setLibRepere(Config.LIB_REPERE_DECHARGEMENT);
                    repere.setLatitude(destinationtLatLng.latitude);
                    repere.setLongitude(destinationtLatLng.longitude);
                    compositeDisposable.add(
                            pizzaApi.addRepere(repere)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribeWith(addRepere())
                    );
                } else if (value.getLibRepere().equals(Config.LIB_REPERE_DECHARGEMENT)) {//on a ajouté le repere de livraison
                    Log.d(Config.TAG, "onSuccess: dechargement repere add");
                    repereDechargement = value.getId();

                    Livraison livraison = new Livraison();
                    livraison.setCodeStatLivraison(1);//create
                    livraison.setRepereCharg(repereChargement);
                    livraison.setRepereDecharg(repereDechargement);
                    livraison.setIdClient(new SessionManager(context).getClientID());
                    //TODO
                    livraison.setIdDistance(1);
                    livraison.setCoutHt((float) price);
                    float tva = (livraison.getCoutHt() * 18) / 100;
                    livraison.setMontantTva(tva);
                    livraison.setCoutTTC(livraison.getCoutHt() + livraison.getMontantTva());
                    Date d = new Date();
                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String s = f.format(d);
                    livraison.setDateDemandeLivraison(s);
                    if (radioGroup.getCheckedRadioButtonId() == R.id.rb_no) {
                        livraison.setNomExpediteur(expeditName);
                        livraison.setAdresseExpediteur(expeditAdress);
                        livraison.setTelExpediteur(mExpeditPhoneNumber.getNumber());
                    } else {
                        SessionManager ss = new SessionManager(context);
                        //TODO Ne plus faire un appel réseau pour récupérer les adresses
                        livraison.setNomExpediteur(ss.getUsername());
                        livraison.setAdresseExpediteur("Une adresse de test pour voir");
                        livraison.setTelExpediteur(ss.getUserPhone1());
                    }
                    livraison.setNomDestinataire(destinataireName);
                    livraison.setAdresseDestinataire(destinataireAdress);
                    livraison.setTelDestinataire(mDestinPhoneNumber.getNumber());
                    Random random = new Random();
                    int validate = random.nextInt((99999 - 10000) + 1) + 10000;
                    livraison.setValidate("" + validate);
                    compositeDisposable.add(
                            pizzaApi.createLivraison(livraison)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribeWith(createLivraisonObserver())
                    );
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(Config.TAG, "onError: repere error", e);
                showProgress(false);
                Snackbar.make(principaleView, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
            }
        };
    }

    private DisposableSingleObserver<Process> addProcess() {
        return new DisposableSingleObserver<Process>() {
            @Override
            public void onSuccess(Process value) {
                Log.d(Config.TAG, "onSuccess: process" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(Config.TAG, "onError: process", e);
            }
        };
    }

    private DisposableSingleObserver<Livraison> createLivraisonObserver() {
        return new DisposableSingleObserver<Livraison>() {
            @Override
            public void onSuccess(Livraison value) {
                showProgress(false);
                idLivraison = value.getId();
                if (compositeDisposable.isDisposed()) {
                    compositeDisposable = new CompositeDisposable();
                }
                for (int i = 0; i < Config.orderProcess.length; i++) {
                    String processName = Config.orderProcess[i];
                    int processAction = Config.orderProcessAction[i];

                    Process process = new Process();
                    process.setIdLivraison(idLivraison);
                    process.setOrderNum(i + 1);
                    process.setTag(processName);
                    process.setAction(processAction);
                    process.setEtat(0);
                    Log.d(Config.TAG, "process: adding" + process.getTag());
                    compositeDisposable.add(
                            pizzaApi.addProcess(process)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(addProcess())
                    );
                }

                if (images.size() > 0) {
                    for (String image : images) {
                        File file = new File(image);

                        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                        RequestBody fileName = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), "" + value.getId());

                        Call<User> fileUpload = pizzaApi.uploadLivraisonResource(fileToUpload, fileName, id);
                        fileUpload.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                                if (response.isSuccessful()) {
                                    Log.d(Config.TAG, "onResponse: ");
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
                                Log.d(Config.TAG, "onFailure: error", t);
                            }
                        });
                    }
                }

                Snackbar.make(principaleView, R.string.order_add, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.rb_yes, v -> {
                            Intent intent = new Intent(context, TimeLineActivityJ.class);
                            intent.putExtra(LIVRAISON_ID, value.getId());
                            principaleView.getContext().startActivity(intent);
                        })
                        .show();
            }

            @Override
            public void onError(Throwable e) {
                showProgress(false);
                Log.d(Config.TAG, "onError: Livraison adding", e);
                Snackbar.make(principaleView, R.string.connection_error, Snackbar.LENGTH_SHORT)
                        .show();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_livraison);
        context = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_close_24);

        inflateViews();
        options = Options.init()
                .setRequestCode(PIX_REQUEST_CODE)                                     //Request code for activity results
                .setCount(10)                                                         //Number of images to restict selection count
                //.setFrontfacing(true)                                               //Front Facing camera on start
                .setImageQuality(ImageQuality.HIGH)                                   //Image Quality
                .setImageResolution(1024, 800)                          //Custom Resolution
                .setPreSelectedUrls(images)                                           //Pre selected Image Urls
                .setScreenOrientation(Options.SCREEN_ORIENTATION_REVERSE_PORTRAIT)   //Orientation
                .setPath("/dliver/images");

        String[] poids = {"Moins de 1Kg", "1Kg à 5Kg", "5Kg à 10Kg"};

        SpinnerAdapter adapter = new ArrayAdapter<>(this, R.layout.custom_simple_spinner_item, poids);
        ((ArrayAdapter) adapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        poidsSpinner.setAdapter(adapter);
        ((ArrayAdapter) adapter).setNotifyOnChange(true);

        mColisCodeBarre.setOnClickListener(this);
        mColisAddPhoto.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        mChargmentLoc.setOnClickListener(this);
        mDestinLoc.setOnClickListener(this);
        poidsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        pizzaApi = PizzaApiService.createDeliverApi(this);
        Fonty.setFonts(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.abort_changes);
        builder.setIcon(R.drawable.warning);
        builder.setMessage(R.string.abort_changes_text);
        builder.setPositiveButton(R.string.rb_yes, (dialogInterface, i) -> {
            finish(); // close this activity as oppose to navigating up
        });
        builder.setNegativeButton(R.string.rb_no, null);
        builder.show();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_livraison, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_livraison) {
            attemptSaveLivraison();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private float[] computedDistance(float[] results) {
        Location.distanceBetween(chargmentLatLng.latitude, chargmentLatLng.longitude, destinationtLatLng.latitude, destinationtLatLng.longitude, results);
        return results;
    }

    private void attemptSaveLivraison() {
        mColisName.setError(null);
        mColisDesc.setError(null);
        mColisValeur.setError(null);
        mChargmentAddress.setError(null);
        mDestinAddress.setError(null);
        mExpeditName.setError(null);
        mExpeditAddress.setError(null);
        mDestinName.setError(null);
        mDestinataireAddress.setError(null);

        colisName = mColisName.getEditableText().toString().trim();
        descriptionColis = mColisDesc.getEditableText().toString().trim();
        valeurColis = mColisValeur.getEditableText().toString().trim();
        chargmentAdress = mChargmentAddress.getEditableText().toString().trim();
        destinationAdress = mDestinAddress.getEditableText().toString().trim();
        expeditName = mExpeditName.getEditableText().toString().trim();
        expeditAdress = mExpeditAddress.getEditableText().toString().trim();
        destinataireName = mDestinName.getEditableText().toString().trim();
        destinataireAdress = mDestinataireAddress.getEditableText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (!mDestinPhoneNumber.isValid() || TextUtils.isEmpty(mDestinPhoneNumber.getNumber())) {
            focusView = destinPhoneLayout;
            cancel = true;
            destinPhoneLayout.setError(getString(R.string.error_field_required));
        }

        if (TextUtils.isEmpty(destinataireAdress)) {
            focusView = mDestinataireAddress;
            cancel = true;
            mDestinataireAddress.setError(getString(R.string.error_field_required));
        }

        if (TextUtils.isEmpty(destinataireName)) {
            focusView = mDestinName;
            cancel = true;
            mDestinName.setError(getString(R.string.error_field_required));
        }

        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_no) {

            if (!mExpeditPhoneNumber.isValid() || TextUtils.isEmpty(mExpeditPhoneNumber.getNumber())) {
                focusView = expeditPhoneLayout;
                cancel = true;
                expeditPhoneLayout.setError(getString(R.string.error_field_required));
            }

            if (TextUtils.isEmpty(expeditAdress)) {
                focusView = mExpeditAddress;
                cancel = true;
                mExpeditAddress.setError(getString(R.string.error_field_required));
            }

            if (TextUtils.isEmpty(expeditName)) {
                focusView = mExpeditName;
                cancel = true;
                mExpeditName.setError(getString(R.string.error_field_required));
            }
        }

        //the same localisation isn't given
        if (destinationtLatLng != null && chargmentLatLng != null && !destinationtLatLng.equals(chargmentLatLng)) {
            if (TextUtils.isEmpty(destinationAdress)) {
                cancel = true;
                focusView = mDestinAddress;
                mDestinAddress.setError(getString(R.string.error_field_required));
            }

            if (TextUtils.isEmpty(chargmentAdress)) {
                cancel = true;
                focusView = mExpeditAddress;
                mExpeditAddress.setError(getString(R.string.error_field_required));
            }
        } else { //both localisation are equals
            cancel = true;
            focusView = mDestinAddress;
            mDestinAddress.setError(getString(R.string.location_same));
            mExpeditAddress.setError(getString(R.string.location_same));
        }

        //valeur validation
        if (TextUtils.isEmpty(valeurColis)) {
            cancel = true;
            focusView = mColisValeur;
            mColisValeur.setError(getString(R.string.error_field_required));
        }


        //colis name validation
        if (TextUtils.isEmpty(colisName)) {
            cancel = true;
            focusView = mColisName;
            mColisName.setError(getString(R.string.error_field_required));
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            sendLivraison();
        }
    }

    private void sendLivraison() {
        showProgress(true);
        float[] computeDistance = new float[2];
        computeDistance = computedDistance(computeDistance);
        Log.d(Config.TAG, "onSuccess: compute distance" + computeDistance[0]);
        price = price(computeDistance[0]);
        price = Utils.roundDouble(price, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.transaction_price);
        builder.setMessage(getString(R.string.transaction_message, String.valueOf(price)));
        builder.setPositiveButton(R.string.rb_yes, (dialogInterface, i) -> {
            //point de chargement
            //on ajoute le point de collecte
            Repere repere = new Repere();
            repere.setLibRepere(Config.LIB_REPERE_CHARGEMENT);
            repere.setLatitude(chargmentLatLng.latitude);
            repere.setLongitude(chargmentLatLng.longitude);
            Log.d(Config.TAG, "sendLivraison: calling addRepere disposable");
            compositeDisposable.add(
                    pizzaApi.addRepere(repere)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribeWith(addRepere())
            );
            showProgress(true);
        });
        showProgress(false);
        builder.setNegativeButton(R.string.rb_no, (dialog, which) -> showProgress(false));
        builder.show();
    }

    private void inflateViews() {
        mColisName = findViewById(R.id.mcolis_name);
        mColisDesc = findViewById(R.id.colis_desc);
        mColisValeur = findViewById(R.id.mcolis_valeur);
        mChargmentAddress = findViewById(R.id.ad_chargement);
        mDestinAddress = findViewById(R.id.ad_destin);
        mExpeditName = findViewById(R.id.mexpedit_name);
        mExpeditAddress = findViewById(R.id.mexpedit_address);
        mDestinName = findViewById(R.id.mdestin_name);
        mDestinataireAddress = findViewById(R.id.destinateur_adresse);

        mColisCodeBarre = findViewById(R.id.colis_code_barre);
        mColisAddPhoto = findViewById(R.id.colis_add_photo);
        mChargmentLoc = findViewById(R.id.chargment_loc);
        mDestinLoc = findViewById(R.id.loc_destin);

        radioGroup = findViewById(R.id.rg_expedit);
        radioYes = findViewById(R.id.rb_yes);
        radioNo = findViewById(R.id.rb_no);

        linearHide = findViewById(R.id.linear_hide);
        scrollView = findViewById(R.id.scrollView);
        principaleView = findViewById(R.id.create_view);

        mExpeditPhoneNumber = findViewById(R.id.expedit_phone_number);
        mDestinPhoneNumber = findViewById(R.id.destin_phone_number);

        poidsSpinner = findViewById(R.id.spinner);

        expeditPhoneLayout = findViewById(R.id.expedit_phone);
        destinPhoneLayout = findViewById(R.id.destin_phone);
        progressBar = findViewById(R.id.create_livraison_progress);
    }

    public void scanQRCode(View v) {
        IntentIntegrator integrator = new IntentIntegrator(CreateLivraisonActivity.this);
        integrator.initiateScan();
    }

    private void showDialog(int title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();
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

        scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
        scrollView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.colis_code_barre:
                scanQRCode(view);
                break;
            case R.id.colis_add_photo:
                pickImage();
                break;
            case R.id.chargment_loc:
                pickLocation(FIND_LOCATION_REQUEST_CODE1);
                break;
            case R.id.loc_destin:
                pickLocation(FIND_LOCATION_REQUEST_CODE2);
                break;
        }
    }

    public void pickLocation(int requestCode) {
        afterRequestCode = requestCode;
        String[] perms = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            Intent intent = new Intent(CreateLivraisonActivity.this, LocationActivity.class);
            startActivityForResult(intent, requestCode);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.all_permissions), LOCATION_REQUEST_CODE, perms);
        }
    }

    public void pickImage() {
        String[] perms = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            Pix.start(this, options.setPreSelectedUrls(images));
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.all_permissions), IMAGE_REQUEST_CODE, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        compositeDisposable = new CompositeDisposable();

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == IMAGE_REQUEST_CODE) {
            Pix.start(this, options);
        } else if (requestCode == LOCATION_REQUEST_CODE) {
            Intent intent = new Intent(CreateLivraisonActivity.this, LocationActivity.class);
            if (afterRequestCode == FIND_LOCATION_REQUEST_CODE1) {
                startActivityForResult(intent, FIND_LOCATION_REQUEST_CODE1);
            } else if (afterRequestCode == FIND_LOCATION_REQUEST_CODE2) {
                startActivityForResult(intent, FIND_LOCATION_REQUEST_CODE2);
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        compositeDisposable = new CompositeDisposable();
        if (resultCode == Activity.RESULT_OK && requestCode == PIX_REQUEST_CODE) {
            ArrayList<String> returnValue = intent.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            images.addAll(returnValue);
            mColisDesc.setText(MessageFormat.format("{0}", getString(R.string.photo_added, returnValue.size())));
        } else if (resultCode == Activity.RESULT_OK && requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (result != null) {
                String contents = result.getContents();
                if (contents != null) {
                    mColisName.setText(result.getContents());
                } else {
                    showDialog(R.string.scan_title, getString(R.string.scan_failed));
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == FIND_LOCATION_REQUEST_CODE1) {
            double latitude = intent.getDoubleExtra("latitude", -1995);
            double longitude = intent.getDoubleExtra("longitude", -1995);
            LatLng latLng = new LatLng(latitude, longitude);
            chargmentLatLng = latLng;
            if (longitude != -1995 && latitude != -1995) {
                String locationName = getNameFromLatLng(latLng);
                if (!locationName.equals("No Address returned!") && !locationName.equals("Canont get Address!")) {
                    mChargmentAddress.setText(locationName);
                } else {
                    mChargmentAddress.setText(MessageFormat.format("Lat: {0} Lng: {1}", latitude, longitude));
                }
            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == FIND_LOCATION_REQUEST_CODE2) {
            double latitude = intent.getDoubleExtra("latitude", -1995);
            double longitude = intent.getDoubleExtra("longitude", -1995);
            LatLng latLng = new LatLng(latitude, longitude);
            destinationtLatLng = latLng;
            if (longitude != -1995 && latitude != -1995) {
                String locationName = getNameFromLatLng(latLng);
                if (!locationName.equals("No Address returned!") && !locationName.equals("Canont get Address!")) {
                    mDestinAddress.setText(locationName);
                } else {
                    mDestinAddress.setText(MessageFormat.format("Lat: {0} Lng: {1}", latitude, longitude));
                }
            }
        }

        /*else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(this, "", Toast.LENGTH_SHORT)
                    .show();
        }*/
    }

    private String getNameFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);

                returnedAddress.getAddressLine(0);
                returnedAddress.getLocality();

                return returnedAddress.getAddressLine(0) + ", " +
                        returnedAddress.getLocality() + ", ";
            } else {
                return "No Address returned!";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Canont get Address!";
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.rb_yes:
                linearHide.setVisibility(View.GONE);
                break;
            case R.id.rb_no:
                linearHide.setVisibility(View.VISIBLE);
                break;
        }
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
