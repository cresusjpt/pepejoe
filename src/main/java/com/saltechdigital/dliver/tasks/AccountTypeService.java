package com.saltechdigital.dliver.tasks;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.saltechdigital.dliver.storage.Authenticator;

import androidx.annotation.Nullable;

public class AccountTypeService extends Service {

    private Authenticator authenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        authenticator = new Authenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
