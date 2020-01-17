package com.saltechdigital.pizzeria.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saltechdigital.pizzeria.R;
import com.saltechdigital.pizzeria.models.PaymentStatus;

import java.text.MessageFormat;
import java.util.List;

public class PaymentStatusAdapter extends RecyclerView.Adapter<PaymentStatusAdapter.ViewHolder> {

    private Context context;
    private List<PaymentStatus> paymentStatuses;

    public PaymentStatusAdapter(Context ctx, List<PaymentStatus> statusList) {
        this.context = ctx;
        this.paymentStatuses = statusList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_pay, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentStatus paymentStatus = paymentStatuses.get(position);
        holder.itemView.setBackgroundResource(R.drawable.state_list);
        holder.view.setBackgroundResource(R.drawable.state_list);
        setAnimation(holder.itemView);
        holder.display(paymentStatus);
    }

    private void setAnimation(View toAnimate) {
        Animation animation = AnimationUtils.loadAnimation(toAnimate.getContext(), android.R.anim.fade_in);
        animation.setDuration(1000);
        toAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return paymentStatuses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        String status;
        private PaymentStatus current;
        private CardView view;
        private ImageView imageView;
        private TextView balanceTittle, balanceDate, balanceDescription, balanceAmount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view);
            imageView = itemView.findViewById(R.id.balance);
            balanceTittle = itemView.findViewById(R.id.balance_title);
            balanceDate = itemView.findViewById(R.id.balance_date);
            balanceDescription = itemView.findViewById(R.id.balance_desc);
            balanceAmount = itemView.findViewById(R.id.balance_amount);

            itemView.setOnClickListener(v -> dialog());
        }

        void dialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            String method = current.getPayMethod();
            String txref = current.getTxRef();
            String payRef = current.getPayRef();
            String date = current.getDate();
            String phone = current.getPhone();
            String message = MessageFormat.format("{0}\n{1}\n{2}\n{3}\n{4}\n{5}", payRef, method, txref, phone, status, date);

            builder.setMessage(message);
            builder.setPositiveButton(R.string.positive_button, null);
            builder.create().show();
        }

        void display(PaymentStatus paymentStatus) {
            current = paymentStatus;

            balanceTittle.setText(MessageFormat.format("{0} {1}", context.getString(R.string.payments), paymentStatus.getTxRef()));
            balanceDate.setText(paymentStatus.getDate());
            balanceDescription.setText(MessageFormat.format("{0} {1}", context.getString(R.string.payment_reference), paymentStatus.getPayRef()));
            balanceAmount.setText(paymentStatus.getAmount());

            //Statut color
            /*
            0 = réussi
            2 = En cours
            4 = Expiré
            6 = Annulé
             */
            if (paymentStatus.isStatus() == 0) {
                status = context.getString(R.string.payment_status_success);
                imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen));
            } else if (paymentStatus.isStatus() == 2) {
                status = context.getString(R.string.payment_status_pending);
                imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorYellow));
            } else if (paymentStatus.isStatus() == 4) {
                status = context.getString(R.string.payment_status_expire);
                imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
            } else if (paymentStatus.isStatus() == 6) {
                status = context.getString(R.string.payment_status_abort);
                imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey));
            }

            Glide.with(context)
                    .load(R.drawable.ic_pay)
                    .apply(RequestOptions.circleCropTransform())
                    .thumbnail(0.1f)
                    .into(imageView);
        }
    }
}
