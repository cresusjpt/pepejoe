package com.saltechdigital.dliver;

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
import com.saltechdigital.dliver.adapter.ServicesAdapter;
import com.saltechdigital.dliver.models.Services;
import com.saltechdigital.dliver.storage.SessionManager;
import com.saltechdigital.dliver.tasks.DeliverApi;
import com.saltechdigital.dliver.utils.Config;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PrincipaleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private ServicesAdapter adapter;

    private ImageView profile;
    private TextView profileName;
    private TextView profileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principale);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inflateViews();
        principaleBinder();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        populateView(navigationView);

        Fonty.setFonts(this);
    }

    private void inflateViews() {
        recyclerView = findViewById(R.id.rv_principale);
    }

    private void principaleBinder() {
        List<Services> ourServices = new ArrayList<>();

        Services pharmacy = new Services();
        pharmacy.setName(getString(R.string.pharmacy));
        int[] pharmacyImages = {R.drawable.pharmacie, R.drawable.pharmacie_1/*, R.drawable.pharmacie_2, R.drawable.pharmacie_3, R.drawable.pharmacie_4*/};
        pharmacy.setImages(pharmacyImages);
        pharmacy.setTag("pharmacie");
        ourServices.add(pharmacy);

        Services market = new Services();
        market.setName(getString(R.string.market));
        int[] marketImages = {R.drawable.market, R.drawable.market_1/*, R.drawable.market_2, R.drawable.market_3, R.drawable.market_4*/};
        market.setImages(marketImages);
        market.setTag("marché");
        ourServices.add(market);

        Services repas = new Services();
        repas.setName(getString(R.string.repas));
        int[] repasImages = {R.drawable.food, R.drawable.food_1/*, R.drawable.food_2, R.drawable.food_3, R.drawable.food_4*/};
        repas.setImages(repasImages);
        repas.setTag("repas");
        ourServices.add(repas);

        Services boisson = new Services();
        boisson.setName(getString(R.string.boisson));
        int[] boissonImages = {R.drawable.drink, R.drawable.drink_1/*, R.drawable.drink_2*/};
        boisson.setImages(boissonImages);
        boisson.setTag("boisson");
        ourServices.add(boisson);

        Services coursier = new Services();
        coursier.setName(getString(R.string.coursier));
        int[] coursierImages = {R.drawable.deliver, R.drawable.deliver_1/*, R.drawable.deliver_2, R.drawable.deliver_3*/};
        coursier.setImages(coursierImages);
        coursier.setTag("coursier");
        ourServices.add(coursier);

        Services magic = new Services();
        magic.setName(getString(R.string.autre));
        int[] magicImages = {R.drawable.deliverfinal};
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

    @SuppressWarnings("StatementWithEmptyBody")
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
