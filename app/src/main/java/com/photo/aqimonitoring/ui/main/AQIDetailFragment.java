package com.photo.aqimonitoring.ui.main;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.photo.aqimonitoring.R;
import com.photo.aqimonitoring.model.AQIResponseModel;
import com.photo.aqimonitoring.utils.CustomBarDataSet;
import com.photo.aqimonitoring.utils.Utils;
import com.photo.aqimonitoring.viewmodel.AQIDashboardViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Detail screen to display selected city's last updated Air Quality Index value and history in chart form.
 */
public class AQIDetailFragment extends Fragment {

    private AQIDashboardViewModel mViewModel;
    public static final String TAG = "AQIDetailsFragment";
    BarChart chart;
    TextView mTv_Title;
    TextView mTv_CurrentCount;
    TextView mTv_AQI_Status;
    LinearLayout mCardLayout;

    public static AQIDetailFragment newInstance() {
        return new AQIDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.aqi_details, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity(), new AQIDashboardViewModel.AQIDashboardViewModelFactory()).get(AQIDashboardViewModel.class);

        init(view);
    }

    /**
     * Initialization
     * @param view
     */
    private void init(View view) {
        mTv_Title = view.findViewById(R.id.tv_title);
        mTv_CurrentCount = view.findViewById(R.id.tv_current_aqi);
        chart = view.findViewById(R.id.chart);
        mCardLayout = view.findViewById(R.id.detailCardLayout);
        mTv_AQI_Status = view.findViewById(R.id.tv_aqi_status);

        initChartView(view);
        fetchAQIData();
    }

    // Fetch data from the shared viewmodel
    private void fetchAQIData() {
        mViewModel.getSelectedItem().observe(getViewLifecycleOwner(), this::setData);
    }

    // Set data on UI
    private void setData(AQIResponseModel responseModel) {
        mTv_Title.setText(getString(R.string.detail_title, responseModel.getCity()));
        String aqiValue = String.format(Locale.getDefault(), "%.2f", responseModel.getAqi());
        mTv_CurrentCount.setText(aqiValue);
        mTv_CurrentCount.setTextColor(ContextCompat.getColor(requireActivity(), Utils.getAQIColorBG(responseModel.getAqi())));
        mTv_AQI_Status.setText(Utils.getAQIStatus(requireActivity(), responseModel.getAqi()));
        mTv_AQI_Status.setTextColor(ContextCompat.getColor(requireActivity(), Utils.getAQIColorBG(responseModel.getAqi())));

        LayerDrawable layerDrawable = (LayerDrawable) mCardLayout.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable
                .findDrawableByLayerId(R.id.card);
        gradientDrawable.setColor(ContextCompat.getColor(requireActivity(), Utils.getAQIColorBG(responseModel.getAqi())));

        setChartData(responseModel);
    }

    /**
     * Initialize chart
     * @param view
     */
    private void initChartView(View view) {
        chart = view.findViewById(R.id.chart);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(30f);
        xAxis.setLabelCount(7);

        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                long millis = TimeUnit.MINUTES.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });


        ValueFormatter custom = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return super.getFormattedValue(value);
            }
        };
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

    }

    private void setChartData(AQIResponseModel aqiResponseModel) {

        ArrayList<BarEntry> values = new ArrayList<>();
        ArrayList<AQIResponseModel> history = (ArrayList<AQIResponseModel>) aqiResponseModel.getTimeHistory().get(aqiResponseModel.getCity());

        long currentTime = 0;
        for (int i = 0; i < history.size(); i++) {
            currentTime = history.get(i).getTime() - history.get(0).getTime();
            float val = TimeUnit.MILLISECONDS.toSeconds(currentTime);
            values.add(new BarEntry(history.get(i).getAqi().floatValue(), val));
        }

        CustomBarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (CustomBarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new CustomBarDataSet(values, "");

            set1.setDrawIcons(false);

            int[] colors = new int[]{ContextCompat.getColor(getActivity(), R.color.aqi_good),
                    ContextCompat.getColor(getActivity(), R.color.aqi_satisfactory),
                    ContextCompat.getColor(getActivity(), R.color.aqi_moderate),
                    ContextCompat.getColor(getActivity(), R.color.aqi_poor),
                    ContextCompat.getColor(getActivity(), R.color.aqi_vpoor),
                    ContextCompat.getColor(getActivity(), R.color.aqi_sever)};
            set1.setColors(colors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.dispose();
    }

}