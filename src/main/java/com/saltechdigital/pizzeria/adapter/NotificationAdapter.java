package com.saltechdigital.pizzeria.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saltechdigital.pizzeria.R;
import com.saltechdigital.pizzeria.models.Notifications;
import com.saltechdigital.pizzeria.tasks.DeliverApi;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Jeanpaul Tossou on 10/03/2019.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<Notifications> notificationsList;

    public NotificationAdapter(Context context, List<Notifications> notifications) {
        this.context = context;
        notificationsList = notifications;
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notifications notifications = notificationsList.get(position);
        //set background click ressoure
        holder.display(notifications);
        setAnimation(holder.mItemView);
    }

    private void setAnimation(View toAnimate) {
        Animation animation = AnimationUtils.loadAnimation(toAnimate.getContext(), android.R.anim.fade_in);
        animation.setDuration(1000);
        toAnimate.startAnimation(animation);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mItemView;
        private ImageView notifIcon;
        private TextView notifTitle;
        private TextView notifDate;
        private TextView notifDesc;

        private Notifications current;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView.findViewById(R.id.notif_view);
            notifIcon = itemView.findViewById(R.id.notif_icon);
            notifTitle = itemView.findViewById(R.id.notif_title);
            notifDate = itemView.findViewById(R.id.notif_date);
            notifDesc = itemView.findViewById(R.id.notif_desc);
        }

        private void showDialog(int title, String message, int addressID) {
            String mTitle = context.getString(title);
            showDialog(mTitle, message, addressID);
        }

        private void showDialog(String title, String message, int addressID) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setNegativeButton(R.string.negative_button, null);
            builder.setPositiveButton(R.string.positive_button, (dialog, which) -> {

            });
            builder.show();
        }

        void display(Notifications notifications) {
            current = notifications;
            notifTitle.setText(current.getTitle());
            notifDate.setText(current.getDate());
            notifDesc.setText(notifications.getDescription());
            Glide.with(context)
                    .load(DeliverApi.BASEENDPOINT + DeliverApi.RESFOLDER + notifications.getIcon())
                    .error(R.mipmap.ic_launcherer_round)
                    .apply(RequestOptions.circleCropTransform())
                    .thumbnail(0.1f)
                    .into(notifIcon);
        }
    }
}
