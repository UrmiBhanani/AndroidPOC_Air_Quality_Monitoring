package com.photo.aqimonitoring.utils;

import android.content.Context;

import com.photo.aqimonitoring.R;

public class Utils {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeText(long time){
        long now = System.currentTimeMillis();
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    /**
     * Return color based on AQI value
     * @param aqi
     * @return
     */
    public static int getAQIColorBG(Double aqi){
        if (aqi <= 50){
            return R.color.aqi_good;
        } else if(aqi<=100){
            return R.color.aqi_satisfactory;
        }else if(aqi<=200){
            return R.color.aqi_moderate;
        }else if(aqi<=300){
            return R.color.aqi_poor;
        }else if(aqi<=400){
            return R.color.aqi_vpoor;
        }else if(aqi<=500){
            return R.color.aqi_sever;
        }
        return R.color.aqi_sever;
    }

    /**
     * Return AQI Status text based on AQI value
     * @param aqi
     * @return
     */
    public static String getAQIStatus(Context context, Double aqi){
        if (aqi <= 50){
            return context.getResources().getString(R.string.status_good);
        } else if(aqi<=100){
            return context.getResources().getString(R.string.status_satisfactory);
        }else if(aqi<=200){
            return context.getResources().getString(R.string.status_moderate);
        }else if(aqi<=300){
            return context.getResources().getString(R.string.status_poor);
        }else if(aqi<=400){
            return context.getResources().getString(R.string.status_vpoor);
        }else if(aqi<=500){
            return context.getResources().getString(R.string.status_sever);
        }
        return context.getResources().getString(R.string.status_sever);
    }
}
