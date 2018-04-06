package com.example.pavelhryts.notesviewer.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.pavelhryts.notesviewer.App;
import com.example.pavelhryts.notesviewer.R;
import com.example.pavelhryts.notesviewer.model.weather.WeatherModel;
import com.example.pavelhryts.notesviewer.util.handlers.LocListener;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WeatherService extends Service implements LocListener.LocationCallback {

    private IBinder binder = new ServiceBinder();
    private WeatherCallback callback;
    private LocationManager locManager;
    private LocationListener locListener;
    private String city = "";

    private final static String SHARED_NAME = "LIST_FRAGMENT";
    private final static String PERMISSIONS = "PERMISSIONS";
    private final String SEPARATOR = "; ";

    private boolean permissions = false;

    public void registerCallback(WeatherCallback callback) {
        this.callback = callback;
        if(!permissions)
            checkPermissions();
    }

    private void checkPermissions() {
        if (!permissions && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && !permissions) {
            if (callback != null)
                callback.requestPermissions();
        } else {
            permissions = true;
        }
    }

    public void setPermissions(boolean permissions) {
        this.permissions = permissions;
        SharedPreferences sp = getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(PERMISSIONS, this.permissions).apply();
        initLocManager();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sf = getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        permissions = sf.getBoolean(PERMISSIONS, false);
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(permissions)
            initLocManager();
        else
            checkPermissions();
        return binder;
    }

    @SuppressLint("MissingPermission")
    private void initLocManager() {
        if (locListener == null)
            locListener = new LocListener(this);
        if (locManager == null)
            locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (permissions) {
            locManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
                    3000L, 1.0F, locListener);
        }
    }

    public void requestWeatherInformation() {
        if (permissions) {
            if (locManager != null) {
                @SuppressLint("MissingPermission") Location location = locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                updateCity(location);
                updateWeather(location);
            } else
                initLocManager();
        }else
            if(callback != null)
                callback.updateWeatherState(getString(R.string.weather_not_found));
    }

    @Override
    public void updateCity(Location location) {
        if (permissions) {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> list = null;
            try {
                if (location != null)
                    list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list != null && !list.isEmpty()) {
                Address address = list.get(0);
                city = String.format("%s, %s", address.getLocality(), address.getCountryName());
            }
        }
    }

    @Override
    public void updateWeather(Location location) {
        if (location != null) {
            App.getApi().getData(getString(R.string.weather_key), location.getLatitude(), location.getLongitude())
                    .enqueue(new Callback<WeatherModel>() {
                        @Override
                        public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                            String result = getString(R.string.weather_not_found);
                            if (response.isSuccessful()) {
                                result = getWeatherString(response.body());
                            }
                            if (callback != null)
                                callback.updateWeatherState(result);
                        }

                        @Override
                        public void onFailure(Call<WeatherModel> call, Throwable t) {
                            if (callback != null)
                                callback.updateWeatherState(getString(R.string.weather_not_found));
                        }
                    });
        } else if (callback != null)
            callback.updateWeatherState(getString(R.string.weather_not_found));
    }

    private String getWeatherString(WeatherModel model) {
        StringBuilder builder = new StringBuilder();
        if (model != null) {
            if (city.isEmpty())
                city = model.getName();
            builder.append(getString(R.string.location)).append(city).append(SEPARATOR)
                    .append(getString(R.string.weather_string)).append(model.getWeather().get(0).getDescription())
                    .append(SEPARATOR)
                    .append(getString(R.string.temp)).append(model.getMain().getIntTemp()).append("\u2103")
                    .append(SEPARATOR)
                    .append(getString(R.string.press)).append(model.getMain().getPressure())
                    .append(getString(R.string.press_units));
        } else {
            builder.append(getString(R.string.weather_not_found));
        }
        return builder.toString();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        releaseLocManager();
        return super.onUnbind(intent);
    }

    public boolean isPermissions() {
        return permissions;
    }

    private void releaseLocManager() {
        if (locListener != null && locManager != null)
            locManager.removeUpdates(locListener);
    }

    public class ServiceBinder extends Binder {

        public WeatherService getService() {
            return WeatherService.this;
        }
    }

    public interface WeatherCallback {
        void updateWeatherState(String weatherState);

        void requestPermissions();
    }
}
