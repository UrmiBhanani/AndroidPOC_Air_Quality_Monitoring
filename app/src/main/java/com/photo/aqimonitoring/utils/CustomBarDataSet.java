package com.photo.aqimonitoring.utils;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class CustomBarDataSet extends BarDataSet {
    public CustomBarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public int getColor(int index) {
        if(getEntryForIndex(index).getX() <= 50)
            return mColors.get(0);
        else if(getEntryForIndex(index).getX() <= 100)
            return mColors.get(1);
        else if(getEntryForIndex(index).getX() <= 200)
            return mColors.get(2);
        else if(getEntryForIndex(index).getX() <= 300)
            return mColors.get(3);
        else if(getEntryForIndex(index).getX() <= 400)
            return mColors.get(4);
        else
            return mColors.get(5);
    }
}
