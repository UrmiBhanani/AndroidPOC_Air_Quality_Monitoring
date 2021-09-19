package com.photo.aqimonitoring.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.photo.aqimonitoring.model.APIInterface;
import com.photo.aqimonitoring.model.AQIResponseModel;
import com.photo.aqimonitoring.model.WSBuilder;
import com.tinder.scarlet.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class AQIDashboardRepository {
    public static final String TAG = "AQIDashboardRepository";
    public APIInterface apiInterface;
    private Disposable disposableConnection;
    private Disposable disposableDataEvent;
    private MutableLiveData<Boolean> isConnected = new MutableLiveData<>();
    private MutableLiveData<LinkedHashMap<String, AQIResponseModel>> response = new MutableLiveData<>();

    public AQIDashboardRepository() {
        WSBuilder wsBuilder = WSBuilder.getInstance();
        apiInterface = wsBuilder.createService(APIInterface.class);
    }

    public MutableLiveData<Boolean> connectToServer() {
        disposableConnection = apiInterface.observeWebSocketEvent()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSubscriber<WebSocket.Event>() {
                    @Override
                    public void onNext(WebSocket.Event event) {
                        Log.d(TAG, "Current socket connection status: " + event);
                            if (event instanceof WebSocket.Event.OnConnectionOpened)
                                isConnected.postValue(true);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        isConnected.postValue(false);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Connection status flowable complete");
                    }
                });
        return isConnected;
    }

    public MutableLiveData<LinkedHashMap<String, AQIResponseModel>> callAPI(LinkedHashMap<String, AQIResponseModel> existingMap) {
        disposableDataEvent = apiInterface.observeAQIChangedResponse()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSubscriber<List<AQIResponseModel>>() {
                    @Override
                    public void onNext(List<AQIResponseModel> responseModel) {
                        // Update model
                        for (AQIResponseModel model : responseModel) {
                            model.setTime(getCurrentTime()); // It wil save latest time
                            // Set logic to store history of all cities
                            ArrayList<AQIResponseModel> history = new ArrayList<>();
                            if (existingMap.containsKey(model.getCity())){
                                ArrayList<AQIResponseModel> aqiList = (ArrayList<AQIResponseModel>) existingMap.get(model.getCity()).getTimeHistory().get(model.getCity());
                                history = new ArrayList<>(aqiList);
                            }
                            history.add(model);
                            HashMap<String, List<AQIResponseModel>> modelHistory = new HashMap<>();
                            modelHistory.put(model.getCity(), history);
                            model.setTimeHistory(modelHistory);
                            existingMap.put(model.getCity(), model);
                        }
                        response.postValue(existingMap);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError---- ");
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "observeAQIChangedResponse  complete---- ");
                    }
                });
        return response;
    }

    public long getCurrentTime(){
        return System.currentTimeMillis();
    }

    public void dispose() {
        if (disposableDataEvent != null) {
            disposableDataEvent.dispose();
        }
        if (disposableConnection != null) {
            disposableConnection.dispose();
        }
    }
}
