package com.saltechdigital.pizzeria;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marcinorlowski.fonty.Fonty;
import com.saltechdigital.pizzeria.models.User;
import com.saltechdigital.pizzeria.storage.SessionManager;
import com.saltechdigital.pizzeria.tasks.PizzaApi;
import com.saltechdigital.pizzeria.tasks.PizzaApiService;
import com.saltechdigital.pizzeria.utils.Config;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private static final int PIX_REQUEST_CODE = 26;
    private static final int REQUEST_PERMISSION_CODE = 27;

    private ImageView profileView;
    private FloatingActionButton fabView;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profilePhone;
    private Button profileNameEdit, profileEmailEdit, profilePhoneEdit, secondPhoneAdd;

    private ArrayList<String> profileImage = new ArrayList<>();
    private Options options;

    private Context context;
    private PizzaApi pizzaApi;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;

        options = Options.init()
                .setRequestCode(PIX_REQUEST_CODE)                                                 //Request code for activity results
                .setCount(1)                                                         //Number of images to restict selection count
                //.setFrontfacing(true)                                                //Front Facing camera on start
                .setImageQuality(ImageQuality.HIGH)                                  //Image Quality
                .setImageResolution(1024, 800)                                       //Custom Resolution
                .setPreSelectedUrls(profileImage)                                     //Pre selected Image Urls
                .setScreenOrientation(Options.SCREEN_ORIENTATION_REVERSE_PORTRAIT)   //Orientaion
                .setPath("/dliver/images");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        inflateViews();
        getSessionManager();
        pizzaApi = PizzaApiService.createDeliverApi(this);

        fabView.setOnClickListener(v -> {
            String[] perms = {
                    Manifest.permission.CAMERA
            };

            if (EasyPermissions.hasPermissions(v.getContext(), perms)) {
                Pix.start(ProfileActivity.this, options);
            } else {
                EasyPermissions.requestPermissions(ProfileActivity.this, getString(R.string.permission_rationale), REQUEST_PERMISSION_CODE, perms);
            }
        });
        userPhoto();

        Fonty.setFonts(this);
    }

    private void setOnClickListener() {
        profilePhoneEdit.setOnClickListener(this);
        profileEmailEdit.setOnClickListener(this);
        profileNameEdit.setOnClickListener(this);
    }

    private void userPhoto() {
        String photo = new SessionManager(context).getUserPhoto();
        Glide.with(ProfileActivity.this)
                .load(PizzaApi.WEBENDPOINT + photo)
                //.load(PizzaApi.WEBENDPOINT + "pp_Jean-Paul_TOSSOU.jpg")
                .apply(RequestOptions.circleCropTransform())
                .thumbnail(0.1f)
                .into(profileView);
    }

    private void progressDialog(boolean show) {
        if (show) {
            progressDialog = ProgressDialog.show(this, null, getString(R.string.loading), true, false);
        } else {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PIX_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            progressDialog(true);
            ArrayList<String> returnValue = intent.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            profileImage = returnValue;
            File file = new File(returnValue.get(0));

            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
            RequestBody fileName = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            RequestBody id = RequestBody.create(MediaType.parse("text/plain"), "" + new SessionManager(ProfileActivity.this).getClientID());

            Glide.with(ProfileActivity.this)
                    .load(file)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileView);

            Call<User> fileUpload = pizzaApi.uploadProfilePhoto(fileToUpload, fileName, id);
            fileUpload.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                    if (response.isSuccessful()) {
                        User user = response.body();
                        assert user != null;
                        String photo = user.getUserPhoto();
                        /*photo = photo.substring(1,user.getUserPhoto().length()-1);*/
                        Log.d(Config.TAG, "onResponse: add profile photo" + user.getUserPhoto());
                        new SessionManager(context).createPhoto(photo);
                        progressDialog(false);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                    progressDialog(false);
                    Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show();
                    Log.d(Config.TAG, "onFailure: error", t);
                }
            });
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

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void inflateViews() {
        profileView = findViewById(R.id.profile_view);
        fabView = findViewById(R.id.fab_view);
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        profilePhone = findViewById(R.id.profile_phone);
        profileNameEdit = findViewById(R.id.profile_name_bt);
        profileEmailEdit = findViewById(R.id.profile_email_bt);
        profilePhoneEdit = findViewById(R.id.profile_phone_bt);
        secondPhoneAdd = findViewById(R.id.bt_add_second_phone);
    }

    private void getSessionManager() {
        SessionManager ss = new SessionManager(this);
        profileName.setText(ss.getUsername());
        profileEmail.setText(ss.getUserMail());
        profilePhone.setText(ss.getUserPhone1());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_add_second_phone:
                break;
            default:
                clickAction(id);
                break;
        }
    }

    public void clickAction(int id) {
    }
}
