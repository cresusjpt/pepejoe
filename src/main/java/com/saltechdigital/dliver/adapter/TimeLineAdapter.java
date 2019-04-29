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

import com.saltechdigital.dliver.DeliverLocationActivity;
import com.saltechdigital.dliver.R;
import com.saltechdigital.dliver.TrackLocationActivity;
import com.saltechdigital.dliver.ValidLivraisonActivity;
import com.saltechdigital.dliver.models.Livraison;
import com.saltechdigital.dliver.models.Process;
import com.saltechdigital.dliver.utils.Config;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    public static final String LIVRAISON = "livraison";
    public static final String PROCESS = "process";

    private Livraison livraison;
    private List<Process> processes;
    private Context context;

    public TimeLineAdapter(Context ctx, List<Process> processList, Livraison livraison) {
        this.livraison = livraison;
        processes = processList;
        this.context = ctx;
        notifyDataSetChanged();
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
