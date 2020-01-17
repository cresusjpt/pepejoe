package com.saltechdigital.pizzeria;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.pizzeria.storage.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import pub.devrel.easypermissions.EasyPermissions;

public class IntroActivity extends AppIntro implements EasyPermissions.PermissionCallbacks {

    public static final int requestCode = 26;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SliderPage sliderPageFirst = new SliderPage();
        sliderPageFirst.setTitle("PepeJoe");
        sliderPageFirst.setDescription("Le moyen sur, fiable et efficace de se faire livrer rapidement");
        sliderPageFirst.setImageDrawable(R.drawable.intro_deliver1);
        sliderPageFirst.setBgColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        addSlide(AppIntroFragment.newInstance(sliderPageFirst));

        SliderPage sliderPageSecond = new SliderPage();
        sliderPageSecond.setTitle("");
        sliderPageSecond.setDescription("Plus besoin de vous déplacer pour faire vos courses, nous nous en chargeons pour vous");
        sliderPageSecond.setImageDrawable(R.drawable.intro_deliver2);
        sliderPageSecond.setBgColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        addSlide(AppIntroFragment.newInstance(sliderPageSecond));

        SliderPage sliderPageThird = new SliderPage();
        sliderPageThird.setTitle("");
        sliderPageThird.setDescription("Suivez vos livreurs et vos colis en temps réel partout où vous êtes");
        sliderPageThird.setImageDrawable(R.drawable.intro_deliver3);
        sliderPageThird.setBgColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        addSlide(AppIntro2Fragment.newInstance(sliderPageThird));

        setFlowAnimation();

        Fonty.setFonts(this);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        askForPermissions();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        askForPermissions();
    }

    private void askForPermissions() {
        String[] perms = {
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.VIBRATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.ACCOUNT_MANAGER,
        };

        new SessionManager(IntroActivity.this).firstInstallation();
        if (EasyPermissions.hasPermissions(this, perms)) {
            startActivity(new Intent(getApplicationContext(), CheckAuthActivity.class));
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.all_permissions),
                    requestCode, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        startActivity(new Intent(getApplicationContext(), CheckAuthActivity.class));
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        finish();
    }
}
