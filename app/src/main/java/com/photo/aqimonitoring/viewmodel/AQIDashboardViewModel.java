package com.photo.aqimonitoring.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.photo.aqimonitoring.model.AQIResponseModel;
import com.photo.aqimonitoring.repository.AQIDashboardRepository;

import java.util.LinkedHashMap;

/**
 * ViewModel for the AQI list screen.
 */
public class AQIDashboardViewModel extends ViewModel {
    private final AQIDashboardRepository aqiDashboardRepository;
    private final MutableLiveData<AQIResponseModel> selectedItem = new MutableLiveData<AQIResponseModel>();

    public AQIDashboardViewModel(AQIDashboardRepository aqiDashboardRepository) {
        this.aqiDashboardRepository = aqiDashboardRepository;
    }

    public LiveData<Boolean> connectToServer(){
        return aqiDashboardRepository.connectToServer();
    }

    public LiveData<LinkedHashMap<String, AQIResponseModel>> callAPI(LinkedHashMap<String, AQIResponseModel> aqiList){
        return aqiDashboardRepository.callAPI(aqiList);
    }

    public void dispose(){
        aqiDashboardRepository.dispose();
    }

    public void selectItem(AQIResponseModel item) {
        selectedItem.setValue(item);
    }
    public MutableLiveData<AQIResponseModel> getSelectedItem() {
        return selectedItem;
    }

    /**
     * ViewModel provider factory to instantiate AQIDashboardViewModel.
     * Required given AQIDashboardViewModel has a non-empty constructor
     */
    public static class AQIDashboardViewModelFactory implements ViewModelProvider.Factory {

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AQIDashboardViewModel.class)) {
                return (T) new AQIDashboardViewModel(new AQIDashboardRepository());
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }
}