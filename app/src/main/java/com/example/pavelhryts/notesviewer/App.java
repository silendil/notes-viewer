package com.example.pavelhryts.notesviewer;

import android.app.Application;
import android.util.Log;

import com.example.pavelhryts.notesviewer.util.http.WeatherAPI;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Pavel.Hryts on 25.03.2018.
 */

public class App extends Application {
    private static WeatherAPI weatherAPI;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        retrofit = new Retrofit.Builder().baseUrl("http://api.openweathermap.org").
                addConverterFactory(GsonConverterFactory.create()).build();
        weatherAPI = retrofit.create(WeatherAPI.class);
    }

    public static WeatherAPI getApi(){
        return weatherAPI;
    }
}
