package com.photo.aqimonitoring.ui.main;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.photo.aqimonitoring.R;
import com.photo.aqimonitoring.model.AQIResponseModel;
import com.photo.aqimonitoring.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

public class AQIListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private LinkedHashMap<String, AQIResponseModel> items;
    OnItemClickListener onItemClickListener;

    public AQIListRecyclerViewAdapter(Context context, LinkedHashMap<String, AQIResponseModel> items) {
        this.context = context;
        this.items = items;
    }

    class AQIListRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView mTv_CityName;
        TextView mTv_CurrentAQI;
        TextView mTv_LastUpdated;
        ConstraintLayout rowLayout;
        View itemView;

        private AQIListRecyclerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            rowLayout = itemView.findViewById(R.id.rowLayout);
            mTv_CityName = itemView.findViewById(R.id.tv_city);
            mTv_CurrentAQI = itemView.findViewById(R.id.tv_cur_aqi);
            mTv_LastUpdated = itemView.findViewById(R.id.tv_last_updt);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.aqi_list_item, parent, false);
        return new AQIListRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AQIResponseModel model = new ArrayList<>(items.entrySet()).get(position).getValue();

        AQIListRecyclerViewHolder listHolder = (AQIListRecyclerViewHolder) holder;
        listHolder.mTv_CityName.setText(model.getCity());
        String aqiValue = String.format(Locale.getDefault(), "%.2f", model.getAqi());
        listHolder.mTv_CurrentAQI.setText(aqiValue);
        listHolder.mTv_CurrentAQI.setTextColor(ContextCompat.getColor(context, Utils.getAQIColorBG(model.getAqi())));
        String updatedTime = Utils.getTimeText(model.getTime());
        listHolder.mTv_LastUpdated.setText(updatedTime);

        LayerDrawable layerDrawable = (LayerDrawable) listHolder.rowLayout.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable
                .findDrawableByLayerId(R.id.card);
        gradientDrawable.setColor(ContextCompat.getColor(context, Utils.getAQIColorBG(model.getAqi())));

        listHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Give some time to the ripple to finish the effect
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickEvent(model);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickEvent(AQIResponseModel mData);
    }
}