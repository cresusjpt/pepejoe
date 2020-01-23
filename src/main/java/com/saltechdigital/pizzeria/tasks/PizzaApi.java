package com.saltechdigital.pizzeria.tasks;

import android.content.Context;

import com.saltechdigital.pizzeria.R;
import com.saltechdigital.pizzeria.models.Address;
import com.saltechdigital.pizzeria.models.Categorie;
import com.saltechdigital.pizzeria.models.Client;
import com.saltechdigital.pizzeria.models.Livraison;
import com.saltechdigital.pizzeria.models.Notifications;
import com.saltechdigital.pizzeria.models.Payment;
import com.saltechdigital.pizzeria.models.PaymentStatus;
import com.saltechdigital.pizzeria.models.Plat;
import com.saltechdigital.pizzeria.models.Process;
import com.saltechdigital.pizzeria.models.Repere;
import com.saltechdigital.pizzeria.models.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface PizzaApi {

    //String BASEENDPOINT = "http://192.168.1.2:8090/deliver/web/";
    String BASEENDPOINT = "http://10.0.2.2/deliver/web/";
    String RESFOLDER = "resfolder/";
    //String BASEENDPOINT = "http://dliver.alwaysdata.net/web/";
    String WEBENDPOINT = String.format("%sprofile_picture/", BASEENDPOINT);
    String WEBPLATENDPOINT = String.format("%splat_picture/", BASEENDPOINT);
    String ENDPOINT = String.format("%sapi/", BASEENDPOINT);
    String PAYGATE_ENDPOINT = "https://paygateglobal.com/";
    String PAYGATE_PAYMENT_URL = ENDPOINT + "/app/payment-status";

    static String urlRessource(String endpoint, String key, Context context, Livraison livraison, String url) throws UnsupportedEncodingException {
        return endpoint + "v1/page?token=" + URLEncoder.encode(key, "UTF-8") + "&amount=" + URLEncoder.encode("" + livraison.getCoutTTC(), "UTF-8") + "&description=" + URLEncoder.encode(context.getString(R.string.payment_description), "UTF-8") + "&identifier=" + URLEncoder.encode("" + livraison.getId(), "UTF-8") + "&url=" + URLEncoder.encode(url, "UTF-8");
    }

    @GET("/repos/{owner}/{repo}/issues")
    Single<List<Client>> getIssues(@Path("owner") String owner, @Path("repo") String repository);

    @POST
    Single<ResponseBody> postComment(@Url String url, @Body Client issue);

    @POST("app/user-exist")
    Single<Client> phoneExist(@Body Client client);

    @POST("app/register")
    Single<User> register(@Body User user);

    @POST("app/login")
    Single<User> login(@Body User user);

    @Multipart
    @POST("app/addphoto")
    Single<User> addPhoto(@Part MultipartBody.Part file, @Part("name") RequestBody name, @Part("id") RequestBody id);

    @Multipart
    @POST("app/add")
    Call<User> uploadProfilePhoto(@Part MultipartBody.Part file, @Part("name") RequestBody name, @Part("id") RequestBody id);

    @POST("reperes")
    Single<Repere> addRepere(@Body Repere repere);

    @GET("repere/livraison")
    Single<List<Repere>> getRepereLivraison(@Query("id") int id);

    @POST("livraisons")
    Single<Livraison> createLivraison(@Body Livraison livraison);

    @GET("livraison/pending")
    Single<List<Livraison>> getPendingLivraison(@Query("id_client") int id);

    @GET("livraisons/{id}")
    Single<Livraison> getLivraison(@Path("id") int id);

    @GET("adresse/client")
    Single<List<Address>> getAddress(@Query("id") int id);

    @POST("paymentways")
    Single<Payment> addPayment(@Body Payment payment);

    @GET("paymentway/client")
    Single<List<Payment>> getPayment(@Query("id") int id);

    @POST("processes")
    Single<Process> addProcess(@Body Process process);

    @GET("process/livraison")
    Single<List<Process>> getProcessByLivraison(@Query("id") int id);

    @GET("notification/client")
    Single<List<Notifications>> getNotifByClient(@Query("id_client") int id);

    @POST("notifications")
    Single<Notifications> addNotification(@Body Notifications notifications);

    @GET("categories")
    Single<List<Categorie>> getCategories();

    @GET("plat/by-categorie")
    Single<List<Plat>> getPlatByCategorie(@Query("id") int idCategorie);

    @GET("payment-status/client")
    Single<List<PaymentStatus>> getPaymentStatusByClient(@Query("client") int id);

    @GET("v1/page")
    Single<Completable> makePayment(@Query("token") String token, @Query("amount") String amount, @Query("description") String description, @Query("identifier") String identifier, @Query("url") String url);

    @Multipart
    @POST("resource/livraison-photo")
    Call<User> uploadLivraisonResource(@Part MultipartBody.Part file, @Part("name") RequestBody name, @Part("id") RequestBody id);
}
