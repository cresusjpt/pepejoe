package com.saltechdigital.pizzeria.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saltechdigital.pizzeria.R;
import com.saltechdigital.pizzeria.models.Notifications;
import com.saltechdigital.pizzeria.tasks.PizzaApi;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        //setAnimation(holder.mItemView);
    }

    private void setAnimation(View toAnimate) {
        Animation animation = AnimationUtils.loadAnimation(toAnimate.getContext(), android.R.anim.fade_in);
        animation.setDuration(1000);
        toAnimate.startAnimation(animation);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.notif_view) LinearLayout mItemView;
        @BindView(R.id.notif_icon) ImageView notifIcon;
        @BindView(R.id.notif_title) TextView notifTitle;
        @BindView(R.id.notif_date) TextView notifDate;
        @BindView(R.id.notif_desc) TextView notifDesc;

        private Notifications current;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
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
                    .load(PizzaApi.BASEENDPOINT + PizzaApi.RESFOLDER + notifications.getIcon())
                    .error(R.mipmap.ic_launcherer_round)
                    .apply(RequestOptions.circleCropTransform())
                    .thumbnail(0.1f)
                    .into(notifIcon);
        }
    }
}
