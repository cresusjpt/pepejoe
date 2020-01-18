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
import androidx.recyclerview.widget.RecyclerView;

import com.saltechdigital.pizzeria.CreateLivraisonActivity;
import com.saltechdigital.pizzeria.R;
import com.saltechdigital.pizzeria.ViewLivraisonListActivity;
import com.saltechdigital.pizzeria.utils.Config;
import com.synnapps.carouselview.CarouselView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.ViewHolder> {

    private Context context;
    private List<String> servicesList;

    public PizzaAdapter(Context context, List<String> services) {
        this.context = context;
        servicesList = services;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_view_pizza, parent, false);
        return new PizzaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String services = servicesList.get(position);
        /*holder.carouselView.setBackgroundResource(R.drawable.state_list);
        holder.deliverType.setBackgroundResource(R.drawable.state_list);
        holder.cardv.setBackgroundResource(R.drawable.state_list);
        holder.display(services);
        setAnimation(holder.mItemView);*/
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

        /*@BindView(R.id.item_view) CardView mItemView;
        @BindView(R.id.cardv) CardView cardv;
        @BindView(R.id.carouselView) CarouselView carouselView;
        @BindView(R.id.deliver_type) TextView deliverType;*/

        private String current;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            /*deliverType.setOnClickListener(v -> serviceClick());
            carouselView.setOnClickListener(v -> serviceClick());
            carouselView.setImageClickListener(position -> serviceClick());*/
        }

        void serviceClick() {
            Intent intent;
            if (current.equals("magic")) {
                intent = new Intent(context, CreateLivraisonActivity.class);
            } else {
                intent = new Intent(context, ViewLivraisonListActivity.class);
            }
            intent.putExtra(Config.TAG, current);
            context.startActivity(intent);
        }

        void display(final String services) {
            /*current = services;
            /*final int[] list = services.getImages();
            carouselView.setPageCount(services.getImages().length);
            carouselView.setImageListener((position, imageView) -> Glide.with(context)
                    .load(ContextCompat.getDrawable(context, list[position]))
                    .skipMemoryCache(true)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .into(imageView));
            deliverType.setText(services);*/
        }
    }
}
