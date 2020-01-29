package com.saltechdigital.pizzeria.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saltechdigital.pizzeria.R;
import com.saltechdigital.pizzeria.database.viewmodel.CommandeViewModel;
import com.saltechdigital.pizzeria.injections.Injection;
import com.saltechdigital.pizzeria.injections.ViewModelFactory;
import com.saltechdigital.pizzeria.models.Commande;
import com.saltechdigital.pizzeria.models.Plat;
import com.saltechdigital.pizzeria.storage.SessionManager;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.utils.Config;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlatAdapter extends RecyclerView.Adapter<PlatAdapter.ViewHolder> {

    private Context context;
    private FragmentActivity activity;
    private List<Plat> platList;
    private PlatCalback callback;

    private CommandeViewModel commandeViewModel;
    private Commande commande;
    private String tag;

    public PlatAdapter(FragmentActivity activity, Context context, List<Plat> plats, PlatCalback callback, String tag) {
        this.context = context;
        this.activity = activity;
        this.tag = tag;
        platList = plats;
        configViewModel();
        this.callback = callback;
        this.notifyDataSetChanged();
        getCreateCommande();
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

    private void configViewModel() {
        ViewModelFactory factory = Injection.provideModelFactory(context);
        this.commandeViewModel = ViewModelProviders.of(activity, factory).get(CommandeViewModel.class);
    }

    private void getCreateCommande() {
        this.commandeViewModel.getStatutCommande(Config.STATUT_COMMANDE_CREE).observe(activity, this::commandeObserver);
        if (commande != null)
            Log.d(Config.TAG, "getCreateCommande  : id=" + commande.getIdCommande());
    }

    private void commandeObserver(Commande commande) {
        this.commande = commande;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.platview)
        ImageView platImage;

        @BindView(R.id.item_view)
        CardView mItemView;
        @BindView(R.id.cardv)
        CardView cardv;
        //@BindView(R.id.deliver_type) TextView deliverType;
        @BindView(R.id.plat_name)
        TextView platName;
        @BindView(R.id.linear_spinner)
        LinearLayout linearSpinner;
        @BindView(R.id.epaisseur)
        Spinner epaisseur;
        @BindView(R.id.taille)
        Spinner taille;
        @BindView(R.id.order_button)
        Button orderButton;

        private String epaisseurText;
        private String tailleText;
        private int values;

        private Plat current;

        /*
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                //Get the selected value
                int selectedValue = selectedValues.getInt(position, -1);
                Log.d("demo", "selectedValues = " + selectedValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
});

         */

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            Resources res = context.getResources();
            final TypedArray selectedValues = res.obtainTypedArray(R.array.taille_values);

            orderButton.setOnClickListener(v -> databind());

            epaisseur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    epaisseurText = epaisseur.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            taille.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    values = selectedValues.getInt(position, -1);
                    tailleText = taille.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        void display(final Plat plat) {
            current = plat;
            platName.setText(plat.getNomPlat());
            String photo = PizzaApi.WEBPLATENDPOINT + plat.getPhoto();
            if (!tag.equals("pizzas")) {
                linearSpinner.setVisibility(View.GONE);
            } else {

            }
            Glide.with(context)
                    .load(photo)
                    //.apply(RequestOptions.circleCropTransform())
                    .thumbnail(0.1f)
                    .into(platImage);
        }

        private void databind() {
            if (commande == null) {
                LocalDateTime localDateTime = LocalDateTime.now();
                LocalDate date = localDateTime.toLocalDate();
                LocalTime time = localDateTime.toLocalTime();

                String finalDate = date.toString() + " " + time.toString("HH:mm:ss");
                commande = new Commande();
                commande.setIdClient(new SessionManager(context).getClientID());
                commande.setStatutCommande(Config.STATUT_COMMANDE_CREE);
                commande.setDateCommande(finalDate);

                commandeViewModel.brutCreateCommande(commande);
                getCreateCommande();
            } else {
                Log.d(Config.TAG, "data: " + commande.toString());
            }
            callback.onPlatSuccess(commande, current,epaisseurText,tailleText,values);
        }
    }

    public interface PlatCalback {
        void onPlatSuccess(Commande commande, Plat plat, String epaisseur, String taille,int values);

        void onPlatError();
    }
}
