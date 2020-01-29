package com.saltechdigital.pizzeria;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lamudi.phonefield.PhoneEditText;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.pizzeria.database.viewmodel.ClientViewModel;
import com.saltechdigital.pizzeria.injections.Injection;
import com.saltechdigital.pizzeria.injections.ViewModelFactory;
import com.saltechdigital.pizzeria.models.Client;
import com.saltechdigital.pizzeria.models.User;
import com.saltechdigital.pizzeria.storage.AccountManagerUtils;
import com.saltechdigital.pizzeria.storage.SessionManager;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.tasks.PizzaApiService;
import com.saltechdigital.pizzeria.utils.ConnectionState;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CheckAuthActivity extends AppCompatActivity {

    public static final String PHONENUMBER = "PHONENUMBER";

    private static final String TAG = "JEANPAUL";
    private String phoneNumber;
    private LinearLayout checkAuthView;
    private ProgressBar progressBar;
    @BindView(R.id.edit_text)
    PhoneEditText phoneEditText;

    private final int REQUEST_CODE_REGISTER = 2020;
    private final int REQUEST_CODE_LOGIN = 2021;
    private ClientViewModel clientViewModel;
    FirebaseUser firebaseUser;

    private PizzaApi pizzaApi;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void createAccount(String email, String password, String authToken) {
        Account account = new Account(email, getString(R.string.account_type));

        AccountManager am = AccountManager.get(this);
        am.addAccountExplicitly(account, password, null);
        am.setAuthToken(account, "full_access", authToken);
    }

    private DisposableSingleObserver<User> loginObserver() {
        return new DisposableSingleObserver<User>() {
            @Override
            public void onSuccess(User value) {
                showProgress(false);
                if (value.isStatut()) {
                    createAccount(value.getEmail(), value.getPassword(), value.getAuthKey());

                    SessionManager ss = new SessionManager(CheckAuthActivity.this);

                    ss.createUserName(value.getNom());
                    ss.createUserEmail(value.getEmail());
                    ss.createToken(value.getAccessToken());
                    ss.createClientInfo(value.getIdClient(), value.getNom(), phoneNumber);

                    Client client = new Client();
                    client.setIdClient(value.getIdClient());
                    client.setEmailClient(value.getEmail());
                    client.setNomClient(value.getNom());
                    client.setPhone1(phoneNumber);
                    client.setPhoto(Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString());

                    //clientViewModel.createClient(client);
                    clientViewModel.brutCreateClient(client);

                    Intent intent = new Intent(CheckAuthActivity.this, PrincipaleActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    String shown = value.getMessage().equals("Connexion echouée") ? getString(R.string.connection_failed) : getString(R.string.connection_error);
                    snack(checkAuthView, shown);
                }
            }

            @Override
            public void onError(Throwable e) {
                showProgress(false);
                Toast.makeText(CheckAuthActivity.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
                Log.w("JEANPAUL", "onError: ", e);
                snack(checkAuthView, getString(R.string.connection_error));
            }
        };
    }

    private void configViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideModelFactory(this);
        this.clientViewModel = ViewModelProviders.of(this, viewModelFactory).get(ClientViewModel.class);
    }

    private DisposableSingleObserver<User> registerObserver() {
        return new DisposableSingleObserver<User>() {
            @Override
            public void onSuccess(User user) {
                showProgress(false);
                Log.d(TAG, "register onSuccess: " + user.toString());
                if (user.isStatut()) {
                    createAccount(user.getEmail(), user.getPassword(), user.getAuthKey());

                    SessionManager ss = new SessionManager(CheckAuthActivity.this);

                    ss.createUserName(user.getNom());
                    ss.createUserEmail(user.getEmail());
                    ss.createToken(user.getAccessToken());
                    ss.createClientInfo(user.getIdClient(), user.getNom(), phoneNumber);

                    Client client = new Client();
                    client.setIdClient(user.getIdClient());
                    client.setEmailClient(user.getEmail());
                    client.setNomClient(user.getNom());
                    client.setPhone1(phoneNumber);
                    client.setPhoto(Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString());

                    //clientViewModel.createClient(client);
                    clientViewModel.brutCreateClient(client);

                    Intent intent = new Intent(CheckAuthActivity.this, PrincipaleActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    String shown = user.getMessage().equals("Les mots de passes ne sont pas identiques") ? getString(R.string.error_notSame_password) : getString(R.string.connection_error);
                    snack(checkAuthView, shown);
                }
            }

            @Override
            public void onError(Throwable e) {
                showProgress(false);
                snack(checkAuthView, getString(R.string.connection_error_bluff));
                Log.w("JEANPAUL", "phoneExist onError: " + e.getMessage(), e);
            }
        };
    }

    private DisposableSingleObserver<Client> phoneExistObserver() {
        return new DisposableSingleObserver<Client>() {
            @Override
            public void onSuccess(Client client) {
                showProgress(false);
                Log.d(TAG, "phoneExist onSuccess: " + client.isStatut());
                if (!client.isStatut()) {
                    //don't hava an account
                    startSignInActivity(REQUEST_CODE_REGISTER);
                } else {
                    //have an account but need to connect again
                    startSignInActivity(REQUEST_CODE_LOGIN);
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(CheckAuthActivity.this, "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
                showProgress(false);
                snack(checkAuthView, getString(R.string.connection_error));
                Log.w(TAG, "phoneExist onError: " + e.getMessage(), e);
            }
        };
    }

    private void attemptLogin(String email) {
        showProgress(true);
        Log.d(TAG, "attemptLogin: ");
        User user = new User();
        user.setUsername(email);
        user.setPassword("aaaaaa");
        if (compositeDisposable.isDisposed() || compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(
                pizzaApi.login(user)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(loginObserver())
        );
    }

    private void attemptRegister(String email, String name) {
        Log.d(TAG, "attemptRegister: ");
        showProgress(true);
        User user = new User();
        user.setEmail(email);
        user.setNom(name);
        user.setRawPassword("aaaaaa");
        user.setPassword("aaaaaa");
        user.setContact(phoneNumber);
        user.setClient(true);
        if (compositeDisposable.isDisposed() || compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        if (ConnectionState.isConnected(CheckAuthActivity.this)) {
            compositeDisposable.add(
                    pizzaApi.register(user)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(registerObserver())
            );
        } else {
            showProgress(false);
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == REQUEST_CODE_REGISTER) {//REGISTER
            if (resultCode == RESULT_OK) { // SUCCESS
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                assert firebaseUser != null;
                String email = firebaseUser.getEmail();
                String name = firebaseUser.getDisplayName();
                Log.d(TAG, "register: " + email + " " + name);
                attemptRegister(email, name);
            } else { // ERRORS
                if (response == null) {
                    Toast.makeText(this, "ANNULE", Toast.LENGTH_SHORT).show();
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "PAS DE CONNEXION INTERNET", Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "UNE ERREUR", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == REQUEST_CODE_LOGIN) {//LOGIN
            if (resultCode == RESULT_OK) { // SUCCESS
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                assert firebaseUser != null;
                String email = firebaseUser.getEmail();
                Log.d(TAG, "login: " + email);
                attemptLogin(email);
            } else { // ERRORS
                if (response == null) {
                    Toast.makeText(this, "ANNULE", Toast.LENGTH_SHORT).show();
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "PAS DE CONNEXION INTERNET", Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "UNE ERREUR", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startSignInActivity(int requestCode) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                //new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
                //new AuthUI.IdpConfig.TwitterBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.AppTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.deliver)
                        .build(),
                requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isFirstInstallation()) {
            startActivity(new Intent(CheckAuthActivity.this, IntroActivity.class));
        }
        if (AccountManagerUtils.hasAccount(this)) {
            Intent intent = new Intent(CheckAuthActivity.this, PrincipaleActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_check_auth);
        checkAuthView = findViewById(R.id.check_auth_view);
        progressBar = findViewById(R.id.check_progress);
        ButterKnife.bind(this);

        //phoneInputLayout.setDefaultCountry("FR");
        phoneEditText.setDefaultCountry("FR");

        //phoneInputLayout.setHint(R.string.phone_hint);
        phoneEditText.setHint(R.string.phone_hint);

        Button btValid = findViewById(R.id.btn_valid);
        btValid.setOnClickListener(this::btClickAction);
        configViewModel();

        pizzaApi = PizzaApiService.createDeliverApi(this);
        Fonty.setFonts(this);
    }

    private void btClickAction(View view) {
        boolean valid = true;

        if (phoneEditText.isValid()) {
            phoneEditText.setError(null);
        } else {
            phoneEditText.setError(getString(R.string.invalid_phone_number));
            valid = false;
        }

        // Return the phone number as follows
        phoneNumber = phoneEditText.getPhoneNumber();
        if (valid && ConnectionState.isConnected(getApplicationContext())) {

            //phoneNumber = "+22892109283";
            //phoneNumber = "+33767299172";
            Client client = new Client();
            client.setPhone1(phoneNumber);
            showProgress(true);
            compositeDisposable.add(
                    pizzaApi.phoneExist(client)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(phoneExistObserver())
            );

        } else if (!ConnectionState.isConnected(getApplicationContext())) {
            snack(view, getString(R.string.not_connected));
        } else {
            snack(view, getString(R.string.invalid_phone_number));
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

    private void snack(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        //.setAction("Action", null).show();
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

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
