package com.lidago.stayfitabit.charts;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.lidago.stayfitabit.Time;

import java.text.DecimalFormat;

/**
 * Created on 28.06.2016.
 */
public class PaceYAxisValueFormatter implements YAxisValueFormatter {

    public PaceYAxisValueFormatter () {
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        Time pace = Time.UnitConverter.ConvertMillisToPace((long)value);
        return String.format("%d:%02d", pace.getMinutes(), pace.getSeconds());
    }
}
