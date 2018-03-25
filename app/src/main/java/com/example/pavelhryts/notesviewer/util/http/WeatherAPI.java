package com.example.pavelhryts.notesviewer.util.http;

import com.example.pavelhryts.notesviewer.model.weather.WeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by Pavel.Hryts on 25.03.2018.
 */

public interface WeatherAPI {
    @GET("/data/2.5/weather?units=metric")
    Call<WeatherModel> getData(@Header("x-api-key")String key, @Query("lat")double lat, @Query("lon")double lon);
}
