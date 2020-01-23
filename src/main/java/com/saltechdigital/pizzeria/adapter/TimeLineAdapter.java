package com.saltechdigital.pizzeria.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saltechdigital.pizzeria.DeliverLocationActivity;
import com.saltechdigital.pizzeria.R;
import com.saltechdigital.pizzeria.TrackLocationActivity;
import com.saltechdigital.pizzeria.ValidLivraisonActivity;
import com.saltechdigital.pizzeria.WebviewInAppActivity;
import com.saltechdigital.pizzeria.models.Livraison;
import com.saltechdigital.pizzeria.models.Payment;
import com.saltechdigital.pizzeria.models.Process;
import com.saltechdigital.pizzeria.storage.SessionManager;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.tasks.PizzaApiService;
import com.saltechdigital.pizzeria.utils.Config;

import java.io.UnsupportedEncodingException;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    public static final String LIVRAISON = "livraison";
    public static final String PROCESS = "process";

    private Livraison livraison;
    private List<Process> processes;
    private Context context;
    private CompositeDisposable compositeDisposable;
    private PizzaApi pizzaApi;
    private List<Payment> paymentList;

    public TimeLineAdapter(Context ctx, List<Process> processList, Livraison livraison) {
        this.livraison = livraison;
        processes = processList;
        this.context = ctx;
        pizzaApi = PizzaApiService.createDeliverApi(context);
        notifyDataSetChanged();
        databind();
    }

    private DisposableSingleObserver<List<Payment>> getPayment() {
        return new DisposableSingleObserver<List<Payment>>() {
            @Override
            public void onSuccess(List<Payment> value) {
                paymentList = value;
            }

            @Override
            public void onError(Throwable e) {
                databind();
            }
        };
    }

    private void databind() {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(
                pizzaApi.getPayment(new SessionManager(context).getClientID())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(getPayment())
        );
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_track_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Process process = processes.get(position);
        holder.display(process);
        setAnimation(holder.itemView);
    }

    private void setAnimation(View toAnimate) {
        Animation animation = AnimationUtils.loadAnimation(toAnimate.getContext(), android.R.anim.fade_in);
        animation.setDuration(1000);
        toAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return processes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Process current;

        private TextView trackStatus;
        private TextView trackDescription;

        private Button trackAction;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackStatus = itemView.findViewById(R.id.track_status);
            trackDescription = itemView.findViewById(R.id.track_description);

            trackAction = itemView.findViewById(R.id.track_bt_action);
            trackAction.setOnClickListener(v -> {
                switch (current.getTag()) {
                    case "Payed":
                        payment();
                        break;
                    case "Deliver":
                        startActivity(DeliverLocationActivity.class);
                        break;
                    case "Delivery":
                        startActivity(TrackLocationActivity.class);
                        break;
                    case "Delivered":
                        startActivity(ValidLivraisonActivity.class);
                        break;
                }
            });
        }

        void payment() {
            if (paymentList != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.payments);
                builder.setNegativeButton(R.string.negative_button, null);
                CharSequence[] items = new CharSequence[paymentList.size()];
                for (int i = 0; i < paymentList.size(); i++) {
                    Payment payment = paymentList.get(i);
                    if (payment.getType().equals("Flooz") || payment.getType().equals("TMoney")) {
                        items[i] = payment.getPhoneNumber() + " " + payment.getType();
                    } else {
                        items[i] = payment.getCardNumber() + " " + payment.getType();
                    }
                }

                builder.setItems(items, (dialog, which) -> {
                    Payment payment = paymentList.get(which);
                    if (payment.getType().equals("Flooz") || payment.getType().equals("TMoney")) {
                        String url;
                        try {
                            url = PizzaApi.urlRessource(PizzaApi.PAYGATE_ENDPOINT, Config.PAYGATE_API_KEY, context, livraison, PizzaApi.PAYGATE_PAYMENT_URL);
                            Intent intent = new Intent(context, WebviewInAppActivity.class);
                            intent.putExtra(Config.INTENT_EXTRA_URL, url);
                            context.startActivity(intent);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.card_unimplemented_yet), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
            } else {
                Toast.makeText(context, R.string.none_paymentway, Toast.LENGTH_SHORT).show();
            }
        }

        private void startActivity(Class cls) {
            Intent intent = new Intent(context, cls);
            intent.putExtra(PROCESS, current);
            intent.putExtra(LIVRAISON, livraison);
            context.startActivity(intent);
        }

        private void display(Process process) {
            current = process;
            for (int i = 0; i < Config.orderProcess.length; i++) {
                if (process.getTag().equals(Config.orderProcess[i])) {
                    trackStatus.setText(context.getString(Config.orderProcessString[i]));
                    trackDescription.setText(context.getString(Config.orderProcessDescription[i]));
                    trackAction.setText(context.getString(Config.orderProcessActionString[i]));
                }
            }
            trackAction.setTag(process.getTag());
            if (process.getAction() == 1) {
                trackDescription.setVisibility(View.GONE);
                trackAction.setVisibility(View.VISIBLE);
            } else {
                trackDescription.setVisibility(View.VISIBLE);
                trackAction.setVisibility(View.GONE);
            }
        }
    }
}
