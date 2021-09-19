package com.photo.aqimonitoring.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AQIResponseModel {
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("aqi")
    @Expose
    private Double aqi;

    private HashMap<String, AQIResponseModel> aqiHashMap = new HashMap<>();
    private long time;
    private HashMap<String, List<AQIResponseModel>> timeHistory = new HashMap<>();

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getAqi() {
        return aqi;
    }

    public void setAqi(Double aqi) {
        this.aqi = aqi;
    }

    public HashMap<String, AQIResponseModel> getAqiHashMap() {
        return aqiHashMap;
    }

    public void setAqiHashMap(HashMap<String, AQIResponseModel> aqiHashMap) {
        this.aqiHashMap = aqiHashMap;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public HashMap<String, List<AQIResponseModel>> getTimeHistory() {
        return timeHistory;
    }

    public void setTimeHistory(HashMap<String, List<AQIResponseModel>> timeHistory) {
        this.timeHistory = timeHistory;
    }
}
