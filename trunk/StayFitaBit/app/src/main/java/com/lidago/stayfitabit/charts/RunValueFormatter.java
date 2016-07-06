package com.lidago.stayfitabit.charts;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created on 28.06.2016.
 */
public class RunValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;

    public RunValueFormatter() {
        mFormat = new DecimalFormat("########0.00");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value/1000);
    }
}
