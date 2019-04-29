package com.saltechdigital.dliver.storage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.saltechdigital.dliver.CheckAuthActivity;
import com.saltechdigital.dliver.LoginActivity;

import java.util.HashMap;

public class SessionManager {

    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String ID_CLIENT = "id_client";
    private static final String CLIENT_NAME = "nom_client";
    private static final String CLIENT_TEL1 = "tel1_client";
    private static final String USER_NAME = "username";
    private static final String USER_EMAIL = "email";
    private static final String USER_AUTHKEY = "auth_key";
    private static final String USER_PHOTO = "photo";
    private static final String USER_TOKEN = "token";
    // Sharedpref file name
    private static final String PREF_NAME = "com.saltechdigital.deliver.APP_PREFERENCES";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_FIRST_LOGIN = "IsFirstInstallation";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String name, String email) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // commit changes
        editor.commit();
    }

    public void createClientInfo(int id, String nom, String tel1) {
        editor.putInt(ID_CLIENT, id);
        editor.putString(CLIENT_NAME, nom);
        editor.putString(CLIENT_TEL1, tel1);

        editor.commit();
    }

    public void createUserName(String name) {
        editor.putString(USER_NAME, name);
        editor.commit();
    }

    public void createUserEmail(String email) {
        editor.putString(USER_EMAIL, email);
        editor.commit();
    }

    public void createAuthKey(String authKey) {
        editor.putString(USER_AUTHKEY, authKey);
        editor.commit();
    }

    public void createPhoto(String photo) {
        editor.putString(USER_PHOTO, photo);
        editor.commit();
    }

    public void createToken(String token) {
        editor.putString(USER_TOKEN, token);
        editor.commit();
    }

    public void firstInstallation() {
        editor.putBoolean(IS_FIRST_LOGIN, true);

        editor.commit();
    }

    public String getUsername() {
        return pref.getString(USER_NAME, "John Doe");
    }

    public String getUserMail() {
        return pref.getString(USER_EMAIL, "useremail.example@dliver.tg");
    }

    public String getUserPhoto() {
        return pref.getString(USER_PHOTO, "anonyme.jpg");
    }

    public String getUserPhone1() {
        return pref.getString(CLIENT_TEL1, "+22892457896");
    }

    public String getAuthKey() {
        return pref.getString(USER_AUTHKEY, "");
    }

    public String getToken() {
        return pref.getString(USER_TOKEN, "");
    }

    public int getClientID() {
        return pref.getInt(ID_CLIENT, 0);
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, CheckAuthActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isFirstInstallation() {
        return pref.getBoolean(IS_FIRST_LOGIN, false);
    }
}
