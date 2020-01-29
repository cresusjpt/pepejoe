package com.saltechdigital.pizzeria.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saltechdigital.pizzeria.R;
import com.saltechdigital.pizzeria.database.viewmodel.ClientViewModel;
import com.saltechdigital.pizzeria.database.viewmodel.CommandeViewModel;
import com.saltechdigital.pizzeria.injections.Injection;
import com.saltechdigital.pizzeria.injections.ViewModelFactory;
import com.saltechdigital.pizzeria.models.Client;
import com.saltechdigital.pizzeria.models.Commande;
import com.saltechdigital.pizzeria.models.DetailCommande;
import com.saltechdigital.pizzeria.storage.SessionManager;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.utils.Config;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @version 2.0
 * @author Jeanpaul Tossou on 10/03/2019.
 */

public class CommandeAdapter extends RecyclerView.Adapter<CommandeAdapter.ViewHolder> {

    private Context context;
    private Commande commande;
    private List<DetailCommande> detailCommandeList;
    private ClientViewModel clientViewModel;
    private FragmentActivity activity;

    private Client client;

    public CommandeAdapter(FragmentActivity activity, Context context, Commande commande, List<DetailCommande> detailCommandes) {
        this.context = context;
        detailCommandeList = detailCommandes;
        this.commande = commande;
        this.activity = activity;
        configViewModel();
    }

    @Override
    public int getItemCount() {
        return detailCommandeList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_commande, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailCommande commande = detailCommandeList.get(position);
        //set background click ressoure
        holder.display(commande);
        setAnimation(holder.mItemView);
    }

    private void setAnimation(View toAnimate) {
        Animation animation = AnimationUtils.loadAnimation(toAnimate.getContext(), android.R.anim.fade_in);
        animation.setDuration(1000);
        toAnimate.startAnimation(animation);
    }

    private void configViewModel() {
        ViewModelFactory factory = Injection.provideModelFactory(context);
        this.clientViewModel = ViewModelProviders.of(activity,factory).get(ClientViewModel.class);
        this.clientViewModel.init(new SessionManager(context).getClientID());
        client = this.clientViewModel.getBruteClient(new SessionManager(context).getClientID());
        //getClient();
    }

    private void getClient(){
        this.clientViewModel.getCurrentClient(new SessionManager(context).getClientID()).observe(activity,this::bind);
    }

    private void bind(Client client){
        this.client = client;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.com_view) LinearLayout mItemView;
        @BindView(R.id.com_icon) ImageView notifIcon;
        @BindView(R.id.plat_cate) TextView notifTitle;
        @BindView(R.id.comm_date) TextView date;
        @BindView(R.id.plat_desc) TextView desc;

        private DetailCommande current;

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

        void display(DetailCommande detailCommande) {
            current = detailCommande;
            if (client != null && commande != null){
                notifTitle.setText(client.getNomClient());
                desc.setText(client.getEmailClient() );
                date.setText(commande.getDateCommande() );
            }
            Glide.with(context)
                    .load(client.getPhoto())
                    .error(R.mipmap.ic_launcherer_round)
                    .apply(RequestOptions.circleCropTransform())
                    .thumbnail(0.1f)
                    .into(notifIcon);
        }
    }

    public interface CommandeCallback{
        void onSuccess();
        void onError();
    }
}
