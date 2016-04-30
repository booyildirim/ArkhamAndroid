package com.monitise.mea.polata.monitisehackathon;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by polata on 21/04/2016.
 */
public interface RetrofitService {

    @POST("event")
    Call<String> test(@Body Event event);
}
