package com.saltechdigital.dliver.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.saltechdigital.dliver.R;
import com.saltechdigital.dliver.TimeLineActivityJ;
import com.saltechdigital.dliver.models.Livraison;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PendingOrderAdapter extends RecyclerView.Adapter<PendingOrderAdapter.ViewHolder> {
    public static final String LIVRAISON_ID = "LIVRAISON_ID";

    private List<Livraison> livraisonList;
    private Context context;

    public PendingOrderAdapter(Context ctx, List<Livraison> livraisons) {
        livraisonList = livraisons;
        this.context = ctx;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_pending_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Livraison livraison = livraisonList.get(position);
        holder.display(livraison);
        setAnimation(holder.itemView);
    }

    private void setAnimation(View toAnimate) {
        Animation animation = AnimationUtils.loadAnimation(toAnimate.getContext(), android.R.anim.fade_in);
        animation.setDuration(1000);
        toAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return livraisonList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Livraison current;

        private TextView pendingTitle;
        private TextView pendingNumber;
        private TextView pendingTotal;
        private TextView pendingStatus;

        private Button pendingTrack;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            pendingTitle = itemView.findViewById(R.id.po_title);
            pendingNumber = itemView.findViewById(R.id.po_number);
            pendingTotal = itemView.findViewById(R.id.po_total);
            pendingStatus = itemView.findViewById(R.id.po_status);

            pendingTrack = itemView.findViewById(R.id.po_track);
            pendingTrack.setOnClickListener(v -> {
                Intent intent = new Intent(context, TimeLineActivityJ.class);
                intent.putExtra(LIVRAISON_ID, current.getId());
                context.startActivity(intent);
            });
        }

        private void display(Livraison livraison) {
            current = livraison;

            pendingTitle.setText(livraison.getNomExpediteur());
            pendingNumber.setText(context.getString(R.string.order_number, String.valueOf(livraison.getId())));
            pendingTotal.setText(context.getString(R.string.order_total_fees, String.valueOf(livraison.getCoutHt())));
            //TODO get order status from the database
            pendingStatus.setText("" + livraison.getCodeStatLivraison());
        }
    }
}
