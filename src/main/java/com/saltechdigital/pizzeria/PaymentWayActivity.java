package com.saltechdigital.pizzeria;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.pizzeria.adapter.PaymentAdapter;
import com.saltechdigital.pizzeria.models.Payment;
import com.saltechdigital.pizzeria.storage.SessionManager;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.tasks.PizzaApiService;
import com.saltechdigital.pizzeria.utils.Config;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class PaymentWayActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addWay;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView addPaymentRv;
    private AppCompatSpinner spinner;

    private TextInputEditText mMName;
    private TextInputEditText mMPhone;
    private TextInputEditText bankCardNumber;
    private TextInputEditText bankCardCvc;
    private TextInputEditText bankCardExpire;
    private TextInputEditText bankCardOwner;
    private TextView spinnerLabel;

    private LinearLayout bankCard;
    private LinearLayout mobileMoney;
    private ProgressBar progressBar;

    private PaymentAdapter adapter;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PizzaApi pizzaApi;

    private ProgressDialog progressDialog;

    private Context context;

    private DisposableSingleObserver<List<Payment>> getPayment() {
        return new DisposableSingleObserver<List<Payment>>() {
            @Override
            public void onSuccess(List<Payment> value) {
                showProgress(false);
                swipeRefreshLayout.setRefreshing(false);
                adapter = new PaymentAdapter(context, value);
                addPaymentRv.setAdapter(adapter);
            }

            @Override
            public void onError(Throwable e) {
                showProgress(false);
                swipeRefreshLayout.setRefreshing(false);
                Log.d(Config.TAG, "onError: ", e);
            }
        };
    }

    private DisposableSingleObserver<Payment> addPayment() {
        return new DisposableSingleObserver<Payment>() {
            @Override
            public void onSuccess(Payment value) {
                showProgressDialog(false);
                dataBind(false);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                showProgressDialog(false);
                Log.d(Config.TAG, "onError: ", e);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_way);
        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        inflateViews();
        addPaymentRv.setLayoutManager(new LinearLayoutManager(this));
        addPaymentRv.setAdapter(adapter);
        pizzaApi = PizzaApiService.createDeliverApi(this);

        dataBind(true);
        addWay.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(() -> dataBind(false));

        Fonty.setFonts(this);
    }

    private void inflateViews() {
        addWay = findViewById(R.id.bt_add_pay_way);
        swipeRefreshLayout = findViewById(R.id.swipe_way);
        addPaymentRv = findViewById(R.id.rv_payment_way);
        progressBar = findViewById(R.id.way_progress);
        progressDialog = new ProgressDialog(this);
    }

    private void inflateDialogViews(View view) {
        bankCard = view.findViewById(R.id.bank_card);
        mobileMoney = view.findViewById(R.id.mobile_money);

        mMName = view.findViewById(R.id.mobile_money_name);
        mMPhone = view.findViewById(R.id.mobile_money_phone);
        spinner = view.findViewById(R.id.spinner);
        spinnerLabel = view.findViewById(R.id.spinner_label);
        bankCardNumber = view.findViewById(R.id.bank_card_number);
        bankCardCvc = view.findViewById(R.id.bank_card_cvc);
        bankCardExpire = view.findViewById(R.id.bank_card_expire);
        bankCardOwner = view.findViewById(R.id.bank_card_owner);
        spinnerLabel.setVisibility(View.GONE);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        swipeRefreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        swipeRefreshLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                swipeRefreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void dataBind(boolean show) {
        if (show) {
            showProgress(true);
        }
        compositeDisposable.add(
                pizzaApi.getPayment(new SessionManager(this).getClientID())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(getPayment())
        );
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_add_pay_way:
                addPaymentWay();
                break;
        }
    }

    private void addPaymentWay() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.add_payment_way, null, false);
        inflateDialogViews(view);

        String[] paymentWay = getResources().getStringArray(R.array.payment_way);

        SpinnerAdapter adapter = new ArrayAdapter<>(this, R.layout.custom_simple_spinner_item, paymentWay);
        ((ArrayAdapter) adapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mobileMoney.setVisibility(View.GONE);
                    bankCard.setVisibility(View.VISIBLE);
                } else {
                    mobileMoney.setVisibility(View.VISIBLE);
                    bankCard.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setNegativeButton(R.string.negative_button, null);
        builder.setPositiveButton(R.string.positive_button, (dialog, which) -> attemptAddPayment());
        builder.show();
    }

    private void attemptAddPayment() {
        int spinnerItem = spinner.getSelectedItemPosition();
        String[] spinnerValue = getResources().getStringArray(R.array.payment_way_value);
        String[] spinnerString = getResources().getStringArray(R.array.payment_way);

        String selectedSpinner = spinnerValue[spinnerItem];
        String type = spinnerString[spinnerItem];
        if (selectedSpinner.equals("0")) {
            String bcNumber = bankCardNumber.getEditableText().toString().trim();
            String bcCvc = bankCardCvc.getEditableText().toString().trim();
            String bcExpire = bankCardExpire.getEditableText().toString().trim();
            String bcOwner = bankCardOwner.getEditableText().toString().trim();

            View focusView = null;
            boolean cancel = false;

            if (TextUtils.isEmpty(bcOwner)) {
                cancel = true;
                focusView = bankCardOwner;
                bankCardOwner.setError(getString(R.string.error_field_required));
            }

            if (TextUtils.isEmpty(bcNumber)) {
                cancel = true;
                focusView = bankCardNumber;
                bankCardNumber.setError(getString(R.string.error_field_required));
            }

            if (TextUtils.isEmpty(bcCvc)) {
                cancel = true;
                focusView = bankCardCvc;
                bankCardCvc.setError(getString(R.string.error_field_required));
            }

            if (TextUtils.isEmpty(bcExpire)) {
                cancel = true;
                focusView = bankCardExpire;
                bankCardExpire.setError(getString(R.string.error_field_required));
            }

            if (cancel) {
                focusView.requestFocus();
            } else {
                Payment payment = new Payment();
                payment.setClienID(new SessionManager(context).getClientID());
                payment.setType("Carte");
                payment.setCardNumber(bcNumber);
                payment.setCardOwner(bcOwner);
                payment.setCvc(Integer.valueOf(bcCvc));
                payment.setExpire(bcExpire);
                showProgressDialog(true);
                compositeDisposable.add(
                        pizzaApi.addPayment(payment)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribeWith(addPayment())
                );
            }


        } else {
            String mbName = mMName.getEditableText().toString().trim();
            String mbPhone = mMPhone.getEditableText().toString().trim();

            View focusView = null;
            boolean cancel = false;


            if (TextUtils.isEmpty(mbName)) {
                focusView = mMName;
                mMName.setError(getString(R.string.error_field_required));
                cancel = true;
            }

            if (TextUtils.isEmpty(mbPhone)) {
                focusView = mMPhone;
                mMPhone.setError(getString(R.string.error_field_required));
                cancel = true;
            }

            if (cancel) {
                focusView.requestFocus();
            } else {
                Payment payment = new Payment();

                payment.setType(type);
                payment.setClienID(new SessionManager(context).getClientID());
                payment.setMobileName(mbName);
                payment.setPhoneNumber(mbPhone);
                showProgressDialog(true);
                compositeDisposable.add(
                        pizzaApi.addPayment(payment)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribeWith(addPayment())
                );
            }
        }
    }

    private void showProgressDialog(boolean show) {
        if (show) {
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        } else {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
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
        compositeDisposable = new CompositeDisposable();
        dataBind(true);
        super.onResume();
    }
}
