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
import com.saltechdigital.pizzeria.tasks.DeliverApi;
import com.saltechdigital.pizzeria.tasks.DeliverApiService;
import com.saltechdigital.pizzeria.utils.ConnectionState;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.saltechdigital.pizzeria.CheckAuthActivity.PHONENUMBER;

public class ConnexionActivity extends AppCompatActivity implements View.OnClickListener {

    private ScrollView scrollView;
    private ProgressBar progressBar;
    private AutoCompleteTextView mEmailView;
    private TextInputEditText mPasswordView;
    private Button mConnectButton;
    private RelativeLayout connectionView;

    private DeliverApi deliverApi;

    private String phoneNumber;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private DisposableSingleObserver<User> loginObserver() {
        return new DisposableSingleObserver<User>() {
            @Override
            public void onSuccess(User value) {
                showProgress(false);
                //Log.d("JEANPAUL", "onSuccess: "+value.isStatut());
                //Toast.makeText(ConnexionActivity.this, "success", Toast.LENGTH_SHORT).show();
                if (value.isStatut()) {
                    createAccount(value.getEmail(), value.getPassword(), value.getAuthKey());

                    SessionManager ss = new SessionManager(ConnexionActivity.this);

                    ss.createUserName(value.getNom());
                    ss.createUserEmail(value.getEmail());
                    ss.createToken(value.getAccessToken());
                    ss.createClientInfo(value.getIdClient(), value.getNom(), phoneNumber);

                    Intent intent = new Intent(ConnexionActivity.this, PrincipaleActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    String shown = value.getMessage().equals("Connexion echouée") ? getString(R.string.connection_failed) : getString(R.string.connection_error);
                    snack(connectionView, shown);
                }
            }

            @Override
            public void onError(Throwable e) {
                showProgress(false);
                Toast.makeText(ConnexionActivity.this, "error", Toast.LENGTH_SHORT).show();
                Log.w("JEANPAUL", "onError: ", e);
                snack(connectionView, getString(R.string.connection_error));
            }
        };
    }

    public void createAccount(String email, String password, String authToken) {
        Account account = new Account(email, getString(R.string.account_type));

        AccountManager am = AccountManager.get(this);
        am.addAccountExplicitly(account, password, null);
        am.setAuthToken(account, "full_access", authToken);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        inflateViews();

        phoneNumber = getIntent().getStringExtra(PHONENUMBER);

        mConnectButton.setOnClickListener(this);
        deliverApi = DeliverApiService.createDeliverApi(this);

        Fonty.setFonts(this);
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

    private void inflateViews() {
        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.connect_progress);
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mConnectButton = findViewById(R.id.connect_button);
        connectionView = findViewById(R.id.connection_view);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.connect_button:
                if (ConnectionState.isConnected(this)) {
                    attemptLogin();
                } else {
                    snack(view, getString(R.string.not_connected));
                }
                break;
        }
    }

    private void attemptLogin() {

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getEditableText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            User user = new User();
            user.setUsername(email);
            user.setPassword(password);
            compositeDisposable.add(
                    deliverApi.login(user)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribeWith(loginObserver())
            );
        }

    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void snack(View view, String message) {

        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        //.setAction("Action", null).show();
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
