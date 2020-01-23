package com.saltechdigital.pizzeria;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.pizzeria.models.User;
import com.saltechdigital.pizzeria.storage.SessionManager;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.tasks.PizzaApiService;
import com.saltechdigital.pizzeria.utils.Config;
import com.saltechdigital.pizzeria.utils.ConnectionState;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.saltechdigital.pizzeria.CheckAuthActivity.PHONENUMBER;

public class InscriptionActivity extends AppCompatActivity implements View.OnClickListener {

    private String phoneNumber;

    private AutoCompleteTextView mNomPrenom;
    private AutoCompleteTextView mEmail;

    private TextInputEditText mPassword;
    private TextInputEditText mPasswordConfirm;

    private Button socialNetwork;
    private Button btInscription;

    private RelativeLayout mInscriptionView;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    private PizzaApi pizzaApi;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void createAccount(String email, String password, String authToken) {
        Account account = new Account(email, getString(R.string.account_type));

        AccountManager am = AccountManager.get(this);
        am.addAccountExplicitly(account, password, null);
        am.setAuthToken(account, "full_access", authToken);
    }

    private DisposableSingleObserver<User> registerObserver() {
        return new DisposableSingleObserver<User>() {
            @Override
            public void onSuccess(User user) {
                showProgress(false);
                Log.d(Config.TAG, "register onSuccess: " + user.toString());
                if (user.isStatut()) {
                    createAccount(user.getEmail(), user.getPassword(), user.getAuthKey());

                    SessionManager ss = new SessionManager(InscriptionActivity.this);

                    ss.createUserName(user.getNom());
                    ss.createUserEmail(user.getEmail());
                    ss.createToken(user.getAccessToken());
                    ss.createClientInfo(user.getIdClient(), user.getNom(), phoneNumber);

                    Intent intent = new Intent(InscriptionActivity.this, PrincipaleActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    String shown = user.getMessage().equals("Les mots de passes ne sont pas identiques") ? getString(R.string.error_notSame_password) : getString(R.string.connection_error);
                    snack(mInscriptionView, shown);
                }
            }

            @Override
            public void onError(Throwable e) {
                showProgress(false);
                snack(mInscriptionView, getString(R.string.connection_error));
                Log.w("JEANPAUL", "phoneExist onError: " + e.getMessage(), e);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        phoneNumber = getIntent().getStringExtra(PHONENUMBER);
        inflateView();

        btInscription.setOnClickListener(this);
        socialNetwork.setOnClickListener(this);

        pizzaApi = PizzaApiService.createDeliverApi(this);

        Fonty.setFonts(this);
    }

    private void inflateView() {
        mNomPrenom = findViewById(R.id.nom_prenom);
        mEmail = findViewById(R.id.email);

        mPassword = findViewById(R.id.password);
        mPasswordConfirm = findViewById(R.id.confirm_password);

        socialNetwork = findViewById(R.id.social_network);
        btInscription = findViewById(R.id.bt_inscription);

        mInscriptionView = findViewById(R.id.inscription_view);
        progressBar = findViewById(R.id.progress);
        scrollView = findViewById(R.id.scrollView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    private void snack(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        //.setAction("Action", null).show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.bt_inscription:
                attemptLogin();
                break;
            case R.id.social_network:
                //TODO
                break;
        }
    }

    private void attemptLogin() {
        mNomPrenom.setError(null);
        mEmail.setError(null);
        mPasswordConfirm.setError(null);
        mPassword.setError(null);

        String nomPrenom = mNomPrenom.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = Objects.requireNonNull(mPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(mPasswordConfirm.getText()).toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid confirm password and same with password, if the user entered one.
        if (TextUtils.isEmpty(confirmPassword) || !isPasswordSame(password, confirmPassword)) {
            mPassword.setError(getString(R.string.error_notSame_password));
            mPasswordConfirm.setError(getString(R.string.error_notSame_password));
            focusView = mPasswordConfirm;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));

            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));

            focusView = mEmail;
            cancel = true;
        }

        // Check for a valid name, if the user entered one.
        if (TextUtils.isEmpty(nomPrenom)) {
            mNomPrenom.setError(getString(R.string.error_field_required));
            focusView = mNomPrenom;
            cancel = true;
        } else if (!isNameValid(nomPrenom)) {
            mNomPrenom.setError(getString(R.string.error_invalid_name));
            focusView = mNomPrenom;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            showProgress(true);
            User user = new User();
            user.setEmail(email);
            user.setNom(nomPrenom);
            user.setRawPassword(password);
            user.setPassword(confirmPassword);
            user.setContact(phoneNumber);
            user.setClient(true);

            if (ConnectionState.isConnected(InscriptionActivity.this)){
                compositeDisposable.add(
                        pizzaApi.register(user)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(registerObserver())
                );
                Toast.makeText(InscriptionActivity.this, "Inscription en cours. Vous allez être redirigé dans l'application. Sinon veuillez vous reconnecter", Toast.LENGTH_SHORT).show();
            }else {
                showProgress(false);
                Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            }
        }
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
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private boolean isNameValid(String nomPrenom) {
        return nomPrenom.length() > 4;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isPasswordSame(String password, String passwordConfirm) {
        return password.equals(passwordConfirm);
    }
}
