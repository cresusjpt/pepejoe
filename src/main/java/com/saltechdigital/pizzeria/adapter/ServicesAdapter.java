package com.saltechdigital.pizzeria.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.saltechdigital.pizzeria.CreateLivraisonActivity;
import com.saltechdigital.pizzeria.R;
import com.saltechdigital.pizzeria.ViewLivraisonListActivity;
import com.saltechdigital.pizzeria.models.Services;
import com.saltechdigital.pizzeria.utils.Config;
import com.synnapps.carouselview.CarouselView;

import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ViewHolder> {

    private Context context;
    private List<Services> servicesList;

    public ServicesAdapter(Context context, List<Services> services) {
        this.context = context;
        servicesList = services;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_principale, parent, false);
        return new ServicesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Services services = servicesList.get(position);
        holder.carouselView.setBackgroundResource(R.drawable.state_list);
        holder.deliverType.setBackgroundResource(R.drawable.state_list);
        holder.cardv.setBackgroundResource(R.drawable.state_list);
        holder.display(services);
        setAnimation(holder.mItemView);
    }

    private void setAnimation(View toAnimate) {
        Animation animation = AnimationUtils.loadAnimation(toAnimate.getContext(), android.R.anim.fade_in);
        animation.setDuration(1000);
        toAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mItemView;
        private CardView cardv;
        private CarouselView carouselView;
        private TextView deliverType;

        private Services current;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView.findViewById(R.id.item_view);
            carouselView = itemView.findViewById(R.id.carouselView);
            deliverType = itemView.findViewById(R.id.deliver_type);
            cardv = itemView.findViewById(R.id.cardv);
            deliverType.setOnClickListener(v -> serviceClick());

            carouselView.setOnClickListener(v -> serviceClick());
            carouselView.setImageClickListener(position -> serviceClick());
        }

        void serviceClick() {
            Intent intent;
            if (current.getTag().equals("magic")) {
                intent = new Intent(context, CreateLivraisonActivity.class);
            } else {
                intent = new Intent(context, ViewLivraisonListActivity.class);
            }
            intent.putExtra(Config.TAG, current.getTag());
            context.startActivity(intent);
        }

        void display(final Services services) {
            current = services;
            final int[] list = services.getImages();
            carouselView.setPageCount(services.getImages().length);
            carouselView.setImageListener((position, imageView) -> Glide.with(context)
                    .load(ContextCompat.getDrawable(context, list[position]))
                    .skipMemoryCache(true)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .into(imageView));
            deliverType.setText(services.getName());
        }
    }
}
