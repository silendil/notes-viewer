
package com.example.pavelhryts.notesviewer.model.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {

    @SerializedName("temp")
    @Expose
    private Double temp;
    @SerializedName("pressure")
    @Expose
    private Integer pressure;
    @SerializedName("humidity")
    @Expose
    private Integer humidity;
    @SerializedName("temp_min")
    @Expose
    private Double tempMin;
    @SerializedName("temp_max")
    @Expose
    private Double tempMax;

    public Integer getIntTemp() {
        return temp.intValue();
    }

    public void setTemp(Integer temp) {
        this.temp = (double)temp;
    }

    public Integer getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getIntTempMin() {
        return tempMin.intValue();
    }

    public void setTempMin(Integer tempMin) {
        this.tempMin = (double)tempMin;
    }

    public Integer getTempMax() {
        return tempMax.intValue();
    }

    public void setTempMax(Integer tempMax) {
        this.tempMax = (double)tempMax;
    }

}
