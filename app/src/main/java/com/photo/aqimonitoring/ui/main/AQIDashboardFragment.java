package com.photo.aqimonitoring.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.aqimonitoring.R;
import com.photo.aqimonitoring.model.AQIResponseModel;
import com.photo.aqimonitoring.viewmodel.AQIDashboardViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

/**
 * Display a list of cities with its Air Quality Index value and updated time.
 */
public class AQIDashboardFragment extends Fragment implements AQIListRecyclerViewAdapter.OnItemClickListener {

    private AQIDashboardViewModel mAQIDashboardViewModel;
    public static final String TAG = "AQIDashboardFragment";
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView recyclerview;
    TextView txtNoRecord;
    ProgressBar loadingProgressBar;
    AQIListRecyclerViewAdapter mAdapter;
    // Store data in map where key set as city and value is updated response model for particular city
    private LinkedHashMap<String, AQIResponseModel> aqiHashMap = new LinkedHashMap<>();

    public static AQIDashboardFragment newInstance() {
        return new AQIDashboardFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.aqi_dashboard, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAQIDashboardViewModel = new ViewModelProvider(requireActivity(), new AQIDashboardViewModel.AQIDashboardViewModelFactory()).get(AQIDashboardViewModel.class);

        init(view);
    }

    /**
     * Initialization
     * @param view
     */
    private void init(View view){
        mLinearLayoutManager = new LinearLayoutManager(requireActivity());
        recyclerview = view.findViewById(R.id.recyclerview);
        txtNoRecord = view.findViewById(R.id.txtNoRecord);
        loadingProgressBar = view.findViewById(R.id.loading);
        loadingProgressBar.setVisibility(View.VISIBLE);

        // Connect to websocket
        connectServer();
    }

    /**
     * Connect to web socket and observe event for connection success or failure
     */
    private void connectServer(){
        mAQIDashboardViewModel.connectToServer().observe(getViewLifecycleOwner(), isConnected -> {
            if (isConnected){
                fetchAQIData();
            } else {
                Toast.makeText(getActivity(), getResources().getText(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch data
    private void fetchAQIData(){
        mAQIDashboardViewModel.callAPI(aqiHashMap).observe(getViewLifecycleOwner(), updatedAQIMap -> {
            setData(updatedAQIMap);
        });
    }

    // Set data to display on UI
    private void setData(LinkedHashMap<String, AQIResponseModel> updatedAQIMap) {
        loadingProgressBar.setVisibility(View.GONE);

        if (updatedAQIMap.isEmpty()) {
            txtNoRecord.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.GONE);
        } else {
            txtNoRecord.setVisibility(View.GONE);
            recyclerview.setVisibility(View.VISIBLE);
        }

        recyclerview.setHasFixedSize(true);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setLayoutManager(mLinearLayoutManager);

        mAdapter = new AQIListRecyclerViewAdapter(requireActivity(), updatedAQIMap);
        recyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Dispose the resource
        mAQIDashboardViewModel.dispose();
    }

    @Override
    public void onItemClickEvent(AQIResponseModel mData) {
        mAQIDashboardViewModel.selectItem(mData);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, AQIDetailFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }
}