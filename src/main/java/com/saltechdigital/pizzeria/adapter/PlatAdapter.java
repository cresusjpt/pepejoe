package com.saltechdigital.pizzeria.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saltechdigital.pizzeria.CreateLivraisonActivity;
import com.saltechdigital.pizzeria.R;
import com.saltechdigital.pizzeria.ViewLivraisonListActivity;
import com.saltechdigital.pizzeria.models.Plat;
import com.saltechdigital.pizzeria.storage.SessionManager;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.utils.Config;
import com.synnapps.carouselview.CarouselView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlatAdapter extends RecyclerView.Adapter<PlatAdapter.ViewHolder> {

    private Context context;
    private List<Plat> platList;

    public PlatAdapter(Context context, List<Plat> plats) {
        this.context = context;
        platList = plats;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_view_plat, parent, false);
        return new PlatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plat plat = platList.get(position);

        holder.mItemView.setBackgroundResource(R.drawable.state_list);
        //holder.deliverType.setBackgroundResource(R.drawable.state_list);
        holder.cardv.setBackgroundResource(R.drawable.state_list);
        holder.display(plat);
        setAnimation(holder.mItemView);
    }

    private void setAnimation(View toAnimate) {
        Animation animation = AnimationUtils.loadAnimation(toAnimate.getContext(), android.R.anim.fade_in);
        animation.setDuration(1000);
        toAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return platList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.platview) ImageView platImage;

        @BindView(R.id.item_view) CardView mItemView;
        @BindView(R.id.cardv) CardView cardv;
        //@BindView(R.id.deliver_type) TextView deliverType;
        @BindView(R.id.plat_name) TextView platName;
        @BindView(R.id.linear_spinner) LinearLayout linearSpinner;
        @BindView(R.id.epaisseur) Spinner epaisseur;
        @BindView(R.id.taille) Spinner taille;
        @BindView(R.id.order_button) Button orderButton;

        private Plat current;

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

        void display(final Plat plat) {
            current = plat;
            platName.setText(plat.getNomPlat());
            String photo = PizzaApi.WEBPLATENDPOINT + plat.getPhoto();
            linearSpinner.setVisibility(View.GONE);
            Glide.with(context)
                    .load(photo)
                    .apply(RequestOptions.circleCropTransform())
                    .thumbnail(0.1f)
                    .into(platImage);

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
