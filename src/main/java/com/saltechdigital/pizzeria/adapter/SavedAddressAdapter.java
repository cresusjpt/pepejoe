package com.saltechdigital.pizzeria.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.saltechdigital.pizzeria.R;
import com.saltechdigital.pizzeria.models.Address;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Jeanpaul Tossou on 10/03/2019.
 */

public class SavedAddressAdapter extends RecyclerView.Adapter<SavedAddressAdapter.ViewHolder> {

    private Context context;
    private List<Address> addressList;

    public SavedAddressAdapter(Context context, List<Address> addresses) {
        this.context = context;
        addressList = addresses;
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address address = addressList.get(position);
        //set background click ressoure
        holder.display(address);

        setAnimation(holder.mItemView);
    }

    private void setAnimation(View toAnimate) {
        Animation animation = AnimationUtils.loadAnimation(toAnimate.getContext(), android.R.anim.fade_in);
        animation.setDuration(1000);
        toAnimate.startAnimation(animation);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mItemView;
        private TextView addressName;
        private ImageView isDefault;
        private TextView addressContent;
        private Button addressEdit;
        private Button addressDelete;
        private Button addressDefault;

        private Address current;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView.findViewById(R.id.item_address_linear);
            addressName = itemView.findViewById(R.id.address_name);
            isDefault = itemView.findViewById(R.id.is_default);
            addressContent = itemView.findViewById(R.id.address_content);
            addressEdit = itemView.findViewById(R.id.address_edit);
            addressDelete = itemView.findViewById(R.id.address_delete);
            addressDefault = itemView.findViewById(R.id.address_default);


            addressEdit.setOnClickListener(v -> {

            });

            addressDelete.setOnClickListener(v -> showDialog(R.string.delete_address_title, v.getContext().getString(R.string.delete_address_message), current.getId()));

            addressDefault.setOnClickListener(v -> {

            });
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

        void display(Address address) {
            current = address;
            isDefault.setVisibility(address.isDefaut() == 1 ? View.VISIBLE : View.GONE);
            addressDefault.setVisibility(address.isDefaut() == 1 ? View.GONE : View.VISIBLE);
            addressName.setText(address.getLibAddress());
            addressContent.setText("Informations du colis et quelques autres informations pas tres importantes juste saisi ici pour voir le rendu que le text aura dans la vie réel de l'application. apparemment je suis a la troisieme ligne donc je pense que c'est assez représentatif.");
        }
    }
}
