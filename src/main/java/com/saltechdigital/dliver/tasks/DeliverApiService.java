package com.saltechdigital.dliver.tasks;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saltechdigital.dliver.models.Client;
import com.saltechdigital.dliver.models.ClientDeserializer;
import com.saltechdigital.dliver.models.User;
import com.saltechdigital.dliver.models.UserDeserializer;
import com.saltechdigital.dliver.storage.SessionManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeliverApiService {

    public static DeliverApi createDeliverApi(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final String token = new SessionManager(context).getToken();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(Client.class, new ClientDeserializer())
                .registerTypeAdapter(User.class, new UserDeserializer())
                //.registerTypeAdapter(Address.class,new AddressDeserializer())
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();

                    Request.Builder builder = request.newBuilder().header("Authorization", "Bearer " + token);
                    Request newRequest = builder.build();

                    return chain.proceed(newRequest);
                })
                .addInterceptor(interceptor)
                .build();

        //okHttpClient.interceptors().add(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DeliverApi.ENDPOINT)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(DeliverApi.class);
    }
}
