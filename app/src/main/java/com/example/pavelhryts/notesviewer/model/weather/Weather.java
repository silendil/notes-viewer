
package com.example.pavelhryts.notesviewer.model.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

import static com.example.pavelhryts.notesviewer.util.Consts.CLEAR_NIGHT;
import static com.example.pavelhryts.notesviewer.util.Consts.WEATHER_CLOUDY;
import static com.example.pavelhryts.notesviewer.util.Consts.WEATHER_DRIZZLE;
import static com.example.pavelhryts.notesviewer.util.Consts.WEATHER_FOGGY;
import static com.example.pavelhryts.notesviewer.util.Consts.WEATHER_RAINY;
import static com.example.pavelhryts.notesviewer.util.Consts.WEATHER_SNOWY;
import static com.example.pavelhryts.notesviewer.util.Consts.WEATHER_SUNNY;
import static com.example.pavelhryts.notesviewer.util.Consts.WEATHER_THUNDER;


public class Weather {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("icon")
    @Expose
    private String icon;

    private Map<String, String> weatherIcons = null;

    public boolean isIconsInited(){
        return weatherIcons != null;
    }

    public void initIcons(Map<String, String> icons){
        weatherIcons = icons;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription(boolean isDay) {
        if(weatherIcons != null){
            if (id == 800){
                if(isDay){
                    return weatherIcons.get(WEATHER_SUNNY);
                }else{
                    return weatherIcons.get(CLEAR_NIGHT);
                }
            }
            int actualId = id / 100;
            switch (actualId){
                case 2:
                    return weatherIcons.get(WEATHER_THUNDER);
                case 3:
                    return weatherIcons.get(WEATHER_DRIZZLE);
                case 5:
                    return weatherIcons.get(WEATHER_RAINY);
                case 6:
                    return weatherIcons.get(WEATHER_SNOWY);
                case 7:
                    return weatherIcons.get(WEATHER_FOGGY);
                case 8:
                    return weatherIcons.get(WEATHER_CLOUDY);
                default:
                    return description;
            }
        }else
            return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
