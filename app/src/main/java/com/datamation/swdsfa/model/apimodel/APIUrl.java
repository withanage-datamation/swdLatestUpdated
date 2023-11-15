package com.datamation.swdsfa.model.apimodel;

import android.content.Context;

import com.datamation.swdsfa.helpers.SharedPref;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rashmi 2020-04-08
 */

public class APIUrl {

    public static final String ROOT_URL = "http://123.231.116.171:8071/swadeshiImg/";

    /********
     * URLS
     *******/
    /**
     * Get Retrofit Instance
     */
//    private static Retrofit getRetrofitInstance() {
//        return new Retrofit.Builder()
//                .baseUrl(ROOT_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//    }

    private final String LOG_TAG = APIUrl.class.getSimpleName();
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

        //pref = SharedPref.getInstance(contextt);
      //  String domain = pref.getBaseURL();
        //Log.d("baseURL>>>>>>>>>", domain);
        //baseURL = domain + contextt.getResources().getString(R.string.connection_string);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create());
        builder.client(httpClient.build());

        if(retrofit == null){
            retrofit = builder.build();
        }
        return retrofit;
    }

}

