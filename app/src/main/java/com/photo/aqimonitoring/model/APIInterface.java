package com.photo.aqimonitoring.model;

import com.tinder.scarlet.WebSocket;
import com.tinder.scarlet.ws.Receive;
import com.tinder.scarlet.ws.Send;

import java.util.List;

import io.reactivex.Flowable;


public interface APIInterface {
    @Receive
    Flowable<WebSocket.Event> observeWebSocketEvent();

    @Send
    void sendSubscribe(String string);

    @Receive
    Flowable<List<AQIResponseModel>> observeAQIChangedResponse();
}
