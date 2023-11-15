package com.datamation.swdsfa.model.apimodel;

import com.datamation.swdsfa.model.PictureList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Rashmi
 */

public interface ApiService {

    @GET("index.php")
    Call<PictureList> downlodPicture();

}
