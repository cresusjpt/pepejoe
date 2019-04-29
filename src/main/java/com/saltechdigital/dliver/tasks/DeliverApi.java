package com.saltechdigital.dliver.tasks;

import com.saltechdigital.dliver.models.Address;
import com.saltechdigital.dliver.models.Client;
import com.saltechdigital.dliver.models.Livraison;
import com.saltechdigital.dliver.models.Notifications;
import com.saltechdigital.dliver.models.Payment;
import com.saltechdigital.dliver.models.Process;
import com.saltechdigital.dliver.models.Repere;
import com.saltechdigital.dliver.models.User;

import java.util.List;

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

public interface DeliverApi {

    //String BASEENDPOINT = "http://192.168.10.110:8090/deliver/web/";
    String RESFOLDER = "resfolder/";
    String BASEENDPOINT = "http://dliver.alwaysdata.net/web/";
    String WEBENDPOINT = String.format("%sprofile_picture/", BASEENDPOINT);
    String ENDPOINT = String.format("%sapi/", BASEENDPOINT);
    //String ENDPOINT = "http://192.168.1.26:8090/deliver/web/api/";

    @GET("user/repos?per_page=100")
    Single<List<User>> getRepos();

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

    @POST("reperes")
    Single<Repere> addRepere(@Body Repere repere);

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

    @GET("repere/livraison")
    Single<List<Repere>> getRepereLivraison(@Query("id") int id);


    @Multipart
    @POST("app/addphoto")
    Single<User> addPhoto(@Part MultipartBody.Part file, @Part("name") RequestBody name, @Part("id") RequestBody id);

    @Multipart
    @POST("app/add")
    Call<User> uploadProfilePhoto(@Part MultipartBody.Part file, @Part("name") RequestBody name, @Part("id") RequestBody id);

    @Multipart
    @POST("resource/livraison-photo")
    Call<User> uploadLivraisonResource(@Part MultipartBody.Part file, @Part("name") RequestBody name, @Part("id") RequestBody id);
}
