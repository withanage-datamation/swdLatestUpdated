package com.datamation.swdsfa.api;

import android.content.Context;
import android.util.Log;


import com.datamation.swdsfa.R;
import com.datamation.swdsfa.helpers.SharedPref;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rashmi on 7/15/2019.
 */

public class ApiCllient {
//    private final String LOG_TAG = ApiCllient.class.getSimpleName();
//    private static String baseURL;
//    private static SharedPref pref;
//    private static Retrofit retrofit = null;
//
//
//    public static Retrofit getClient(Context contextt) {
//        pref = SharedPref.getInstance(contextt);
//        String domain = pref.getBaseURL();
//        Log.d("baseURL>>>>>>>>>", domain);
//        //baseURL = domain;
//        baseURL = domain + contextt.getResources().getString(R.string.connection_string);
//
////        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
////                .connectTimeout(20, TimeUnit.SECONDS)
////                .readTimeout(30, TimeUnit.SECONDS)
////                .writeTimeout(30, TimeUnit.SECONDS);
//
//        if (retrofit==null) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(baseURL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//        }
//        return retrofit;
//    }
//}
//////////////////////////////////////////////
private final String LOG_TAG = ApiCllient.class.getSimpleName();
    private static String baseURL;
    private static SharedPref pref;
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context contextt) {

        //add timouts 2020-03-19 becz sockettimeoutexception by rashmi
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                // .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(50, TimeUnit.SECONDS)
                .readTimeout(80, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        pref = SharedPref.getInstance(contextt);
        String domain = pref.getBaseURL();
        Log.d("baseURL>>>>>>>>>", domain);
        baseURL = domain + contextt.getResources().getString(R.string.connection_string);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create());
        builder.client(httpClient.build());

        if(retrofit == null){
            retrofit = builder.build();
        }
        return retrofit;
    }
}
