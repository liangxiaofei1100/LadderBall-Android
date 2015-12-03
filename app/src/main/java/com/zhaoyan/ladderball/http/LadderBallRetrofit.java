package com.zhaoyan.ladderball.http;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.zhaoyan.ladderball.BuildConfig;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;


public class LadderBallRetrofit {

    final LadderBallApi ladderBallService;

    LadderBallRetrofit() {
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

        ladderBallService = retrofit.create(LadderBallApi.class);
    }


    public LadderBallApi getLadderBallService() {
        return ladderBallService;
    }
}
