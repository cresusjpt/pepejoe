package com.saltechdigital.pizzeria.storage;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.saltechdigital.pizzeria.R;
import com.saltechdigital.pizzeria.utils.Config;

import java.io.IOException;

public class AccountManagerUtils {

    private Context context;
    private AccountManager accountManager;

    public AccountManagerUtils(Context context) {
        this.context = context;
        accountManager = AccountManager.get(this.context);
    }

    public static boolean hasAccount(Context ctx) {
        AccountManager accountManager = AccountManager.get(ctx);
        Account[] accounts = accountManager.getAccountsByType(ctx.getString(R.string.account_type));

        return accounts.length > 0;

    }

    private boolean validateAccount() {
        AccountManagerCallback<Bundle> callback = future -> {
            Log.e(Config.TAG, "run: ");
            try {
                Bundle b = future.getResult();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.account_type));
        if (accounts.length <= 0) {
            return false;
        } else {
            String loginNameVal = accounts[0].name;
            String loginPswdVal = accountManager.getPassword(accounts[0]);
            return true;
        }
    }
}
