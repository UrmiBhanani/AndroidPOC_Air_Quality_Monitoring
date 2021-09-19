package com.photo.aqimonitoring.model;

import com.tinder.scarlet.Scarlet;
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter;
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory;
import com.tinder.scarlet.websocket.okhttp.OkHttpClientUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.photo.aqimonitoring.utils.Constants.BASE_URL;

/**
 * Singleton class to get web socket client instance
 */
public class WSBuilder {

    private static WSBuilder instance = null;
    // Used Scarlet library to connect web socket client
    Scarlet scarlet;

    private WSBuilder() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        scarlet = new Scarlet.Builder()
                .webSocketFactory(OkHttpClientUtils.newWebSocketFactory(okHttpClient, BASE_URL))
                .addMessageAdapterFactory(new GsonMessageAdapter.Factory())
                .addStreamAdapterFactory(new RxJava2StreamAdapterFactory())
                .build();
    }

    public static synchronized WSBuilder getInstance() {
        if (instance == null) {
            instance = new WSBuilder();
        }
        return instance;
    }

    public <S> S createService(Class<S> serviceClass) {
        return scarlet.create(serviceClass);
    }
}
