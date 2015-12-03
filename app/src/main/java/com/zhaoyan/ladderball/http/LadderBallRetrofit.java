package com.zhaoyan.ladderball.http;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;


public class LadderBallRetrofit {

    final LadderBallApi ladderBallService;

    LadderBallRetrofit() {
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                // Do anything with response here
                return response;
            }
        });


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
