package com.saltechdigital.dliver;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.dliver.models.Client;
import com.saltechdigital.dliver.storage.AccountManagerUtils;
import com.saltechdigital.dliver.storage.SessionManager;
import com.saltechdigital.dliver.tasks.DeliverApi;
import com.saltechdigital.dliver.tasks.DeliverApiService;
import com.saltechdigital.dliver.utils.ConnectionState;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CheckAuthActivity extends AppCompatActivity {

    public static final String PHONENUMBER = "PHONENUMBER";

    private static final String TAG = "JEANPAUL";
    private IntlPhoneInput phoneNumberInput;
    private String phoneNumber;
    private LinearLayout checkAuthView;
    private ProgressBar progressBar;

    private DeliverApi deliverApi;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private DisposableSingleObserver<Client> phoneExistObserver() {
        return new DisposableSingleObserver<Client>() {
            @Override
            public void onSuccess(Client client) {
                showProgress(false);
                Log.d(TAG, "phoneExist onSuccess: " + client.isStatut());
                if (!client.isStatut()) {
                    Intent intent = new Intent(CheckAuthActivity.this, VerificationActivity.class);
                    intent.putExtra(PHONENUMBER, phoneNumber);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CheckAuthActivity.this, ConnexionActivity.class);
                    intent.putExtra(PHONENUMBER, phoneNumber);
                    startActivity(intent);
                }
            }

            @Override
            public void onError(Throwable e) {
                showProgress(false);
                snack(checkAuthView, getString(R.string.connection_error));
                Log.w(TAG, "phoneExist onError: " + e.getMessage(), e);
            }
        };
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

        phoneNumberInput = findViewById(R.id.phone_number);
        phoneNumberInput.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Touche OK sélectionné. a revoir ultérieurement
                btClickAction(view);
                return true;
            }
            return false;
        });

        Button btValid = findViewById(R.id.btn_valid);
        btValid.setOnClickListener(this::btClickAction);

        deliverApi = DeliverApiService.createDeliverApi(this);
        Fonty.setFonts(this);
    }

    private void btClickAction(View view) {
        if (phoneNumberInput.isValid() && ConnectionState.isConnected(getApplicationContext())) {
            phoneNumber = phoneNumberInput.getNumber();
            Client client = new Client();
            client.setPhone1(phoneNumber);
            showProgress(true);
            compositeDisposable.add(
                    deliverApi.phoneExist(client)
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

    /*@Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(IntroActivity.this,InscriptionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }*/
}
