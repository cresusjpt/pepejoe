package com.saltechdigital.pizzeria;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.pizzeria.adapter.PizzaAdapter;
import com.saltechdigital.pizzeria.adapter.ServicesAdapter;
import com.saltechdigital.pizzeria.models.Services;
import com.saltechdigital.pizzeria.storage.SessionManager;
import com.saltechdigital.pizzeria.tasks.DeliverApi;
import com.saltechdigital.pizzeria.utils.Config;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrincipaleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.rv_principale) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private ServicesAdapter adapter;

    private ImageView profile;
    private TextView profileName;
    private TextView profileEmail;

    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principale);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        principaleBinder();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        populateView(navigationView);

        Fonty.setFonts(this);
    }

    private void principaleBinder() {
        List<Services> ourServices = new ArrayList<>();

        Services pizza = new Services();
        pizza.setName(getString(R.string.pizzas));
        int[] pizzaImages = {R.drawable.pizza, R.drawable.pizza_1/*, R.drawable.pharmacie_2, R.drawable.pharmacie_3, R.drawable.pharmacie_4*/};
        pizza.setImages(pizzaImages);
        pizza.setTag("pizzas");
        ourServices.add(pizza);

        Services burger = new Services();
        burger.setName(getString(R.string.burger));
        int[] burgerImages = {R.drawable.burger, R.drawable.burger_1/*, R.drawable.market_2, R.drawable.market_3, R.drawable.market_4*/};
        burger.setImages(burgerImages);
        burger.setTag("burger");
        ourServices.add(burger);

        Services verdures = new Services();
        verdures.setName(getString(R.string.verdure));
        int[] verdureImages = {R.drawable.food, R.drawable.salad/*, R.drawable.food_2, R.drawable.food_3, R.drawable.food_4*/};
        verdures.setImages(verdureImages);
        verdures.setTag("verdure");
        ourServices.add(verdures);

        Services amuseBouches = new Services();
        amuseBouches.setName(getString(R.string.amusebouche));
        int[] amuseImages = {R.drawable.ambouche, R.drawable.ambouche_1/*, R.drawable.drink_2*/};
        amuseBouches.setImages(amuseImages);
        amuseBouches.setTag("amusebouche");
        ourServices.add(amuseBouches);

        Services gourmandises = new Services();
        gourmandises.setName(getString(R.string.gourmandise));
        int[] gourmandiseImages = {R.drawable.gourmandise, R.drawable.gourmandise, R.drawable.gourmandise_2/*, R.drawable.deliver_3*/};
        gourmandises.setImages(gourmandiseImages);
        gourmandises.setTag("gourmandise");
        ourServices.add(gourmandises);

        Services desalterants = new Services();
        desalterants.setName(getString(R.string.desalterant));
        int[] desalterantImages = {R.drawable.boisson, R.drawable.boisson_1/*, R.drawable.deliver_2, R.drawable.deliver_3*/};
        desalterants.setImages(desalterantImages);
        desalterants.setTag("desalterant");
        ourServices.add(desalterants);

        Services menus = new Services();
        menus.setName(getString(R.string.menus));
        int[] menusImages = {R.drawable.menu, /*R.drawable.deliver_1, R.drawable.deliver_2, R.drawable.deliver_3*/};
        menus.setImages(menusImages);
        menus.setTag("menus");
        ourServices.add(menus);

        Services magic = new Services();
        magic.setName(getString(R.string.autre));
        int[] magicImages = {R.drawable.pepejoe};
        magic.setImages(magicImages);
        magic.setTag("magic");
        ourServices.add(magic);

        adapter = new ServicesAdapter(this, ourServices);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    private void populateView(NavigationView navigationView) {
        SessionManager sessionManager = new SessionManager(this);
        View view;
        view = navigationView.getHeaderView(0);
        if (view == null) {
            view = navigationView.inflateHeaderView(R.layout.nav_header_principale);
        }

        profile = view.findViewById(R.id.imageView);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);

        profileName.setText(sessionManager.getUsername());
        profileEmail.setText(sessionManager.getUserMail());

        String photo = new SessionManager(this).getUserPhoto();
        photo = DeliverApi.WEBENDPOINT + photo;
        Glide.with(PrincipaleActivity.this)
                //.load(DeliverApi.WEBENDPOINT + "pp_Jean-Paul_TOSSOU.jpg")
                .load(photo)
                .apply(RequestOptions.circleCropTransform())
                .thumbnail(0.1f)
                .into(profile);
        Log.d(Config.TAG, "populateView: " + DeliverApi.WEBENDPOINT + photo);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principale, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(SettingsActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_order:
                startActivity(OrderActivity.class);
                break;
            case R.id.nav_address:
                startActivity(SavedAddressActivity.class);
                break;
            case R.id.nav_notification:
                startActivity(NotificationActivity.class);
                break;
            case R.id.nav_payment:
                startActivity(BillActivity.class);
                break;
            case R.id.nav_wallet:
                startActivity(PaymentWayActivity.class);
                break;
            case R.id.nav_account:
                startActivity(ProfileActivity.class);
                break;
            case R.id.nav_setting:
                startActivity(SettingsActivity.class);
                break;
            case R.id.nav_about:
                startActivity(AboutActivity.class);
                break;
            case R.id.nav_logout:
                AccountManager accountManager = AccountManager.get(PrincipaleActivity.this);
                Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
                for (Account ac : accounts) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        accountManager.removeAccountExplicitly(ac);
                    } else {
                        accountManager.removeAccount(ac, null, null);
                    }
                }
                logOut();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startActivity(Class cls) {
        Intent intent = new Intent(PrincipaleActivity.this, cls);
        startActivity(intent);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(PrincipaleActivity.this, CheckAuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
