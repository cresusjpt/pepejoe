package com.saltechdigital.dliver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.saltechdigital.dliver.R;
import com.saltechdigital.dliver.models.Payment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private Context context;
    private List<Payment> paymentList;

    public PaymentAdapter(Context context, List<Payment> payments) {
        this.context = context;
        paymentList = payments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_payment_way, parent, false);
        return new PaymentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment payment = paymentList.get(position);
        //set background click resource
        holder.display(payment);

        setAnimation(holder.mItemView);
    }

    private void setAnimation(View toAnimate) {
        Animation animation = AnimationUtils.loadAnimation(toAnimate.getContext(), android.R.anim.fade_in);
        animation.setDuration(1000);
        toAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mItemView;

        private Payment current;

        private TextView name;
        private TextView number;
        private TextView type;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
            type = itemView.findViewById(R.id.type);
            mItemView = itemView.findViewById(R.id.item_view);

            mItemView.setOnClickListener(v -> {

            });
        }

        void display(Payment payment) {
            current = payment;
            type.setText(payment.getType());

            if (payment.getType().equals("Carte")) {
                name.setText(payment.getCardOwner());
                number.setText(payment.getCardNumber());
            } else {
                name.setText(payment.getMobileName());
                number.setText(payment.getPhoneNumber());
            }
        }
    }
}
