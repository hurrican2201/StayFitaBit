package com.lidago.stayfitabit.charts;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.lidago.stayfitabit.Time;

/**
 * Created on 28.06.2016.
 */
public class PaceValueFormatter implements ValueFormatter {

    public PaceValueFormatter() {
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        Time pace = Time.UnitConverter.ConvertMillisToPace((long)value);
        return String.format("%d:%02d", pace.getMinutes(), pace.getSeconds());
    }
}
