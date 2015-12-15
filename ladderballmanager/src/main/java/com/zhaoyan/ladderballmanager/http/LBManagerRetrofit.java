package com.zhaoyan.ladderballmanager.http;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.zhaoyan.ladderballmanager.BuildConfig;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;


public class LBManagerRetrofit {

    final LBManagerApi service;

    LBManagerRetrofit() {
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);

        //给网络请求添加Log，以便Debug，TAG为 “Okhttp”
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        client.interceptors().add(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://zhaoyanladderball.duapp.com/app/")//retroift要求baseurl总是以“/”结尾
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(LBManagerApi.class);
    }


    public LBManagerApi getService() {
        return service;
    }
}
