package com.saltechdigital.dliver;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.dliver.utils.Config;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.saltechdigital.dliver.CheckAuthActivity.PHONENUMBER;

public class VerificationActivity extends AppCompatActivity {

    private Button btVerification;
    private EditText verificationCode;
    private ProgressBar progressBar;
    private TextView verificationError;

    private String phoneNumber;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken token;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            token = forceResendingToken;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verificationCode.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.w("JEANPAUL", "onVerificationFailed", e);
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(VerificationActivity.this, R.string.firebase_database_url, Toast.LENGTH_SHORT).show();
                Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(Config.TAG, "Firebase error: ",e);
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(VerificationActivity.this, "Le quota des messages est atteint", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("VERIFICATION_ID", verificationId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        if (savedInstanceState != null && savedInstanceState.getString("VERIFICATION_ID") != null) {
            verificationId = savedInstanceState.getString("VERIFICATION_ID");
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        verificationCode = findViewById(R.id.verification_code);
        btVerification = findViewById(R.id.btn_verification);
        progressBar = findViewById(R.id.progressbar);
        verificationError = findViewById(R.id.verification_error);

        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();

        phoneNumber = getIntent().getStringExtra(PHONENUMBER);
        sendVerificationCode(phoneNumber);

        btVerification.setOnClickListener(view -> {
            String code = verificationCode.getText().toString().trim();

            if (code.isEmpty() || code.length() < 6) {
                verificationCode.setError(getString(R.string.enter_valid_code));
                verificationError.setVisibility(View.VISIBLE);
                verificationCode.requestFocus();
                return;
            }
            verifyCode(code);
        });

        Fonty.setFonts(this);
    }

    private void sendVerificationCode(String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60L,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        Intent intent = new Intent(VerificationActivity.this, InscriptionActivity.class);
                        intent.putExtra(PHONENUMBER, phoneNumber);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(Config.TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(this, R.string.enter_valid_code, Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(VerificationActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}