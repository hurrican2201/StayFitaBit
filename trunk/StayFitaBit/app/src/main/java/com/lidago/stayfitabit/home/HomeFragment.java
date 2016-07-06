package com.lidago.stayfitabit.home;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lidago.stayfitabit.Args;
import com.lidago.stayfitabit.R;
import com.lidago.stayfitabit.Time;
import com.lidago.stayfitabit.charts.PushUpsValueFormatter;
import com.lidago.stayfitabit.charts.RunValueFormatter;
import com.lidago.stayfitabit.firebase.FirebaseClient;
import com.lidago.stayfitabit.history.HistoryModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 09.05.2016.
 */
public class HomeFragment extends Fragment {

    private final boolean RUN_STATE = true;
    private final boolean PUSHUP_STATE = false;

    private TextView mDistanceTextView;
    private TextView mDurationTextView;
    private TextView mPushUpsTextView;
    private TextView mCaloriesTextView;
    private ValueAnimator mAnimator;
    private PieChart mPieChart;
    private PieData runData;
    private PieData pushUpsData;
    private boolean mState = RUN_STATE;

    public static HomeFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(Args.TOOLBAR_TITLE, title);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        setupUserInterface();
        setupToolbar();
        setAnimation();
    }

    private void setupUserInterface() {
        mDistanceTextView = (TextView) getView().findViewById(R.id.home_distance_textView);
        mDurationTextView = (TextView) getView().findViewById(R.id.home_duration_textView);
        mPushUpsTextView = (TextView) getView().findViewById(R.id.home_pushups_textView);
        mCaloriesTextView = (TextView) getView().findViewById(R.id.home_calories_textView);
    }

    private void setupToolbar() {
        String title = getArguments().getString(Args.TOOLBAR_TITLE);
        // We have a Toolbar in place so we don't need to care about the NPE warning
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private void setAnimation() {
        Time duration = Time.UnitConverter.ConvertMillisToTime(FirebaseClient.getInstance().getTotalDuration());
        PropertyValuesHolder pushupsHolder = PropertyValuesHolder.ofInt(Args.PUSHUPS, 0, FirebaseClient.getInstance().getTotalPushUps());
        PropertyValuesHolder distanceHolder = PropertyValuesHolder.ofFloat(Args.DISTANCE, 0.0f, FirebaseClient.getInstance().getTotalDistance());
        PropertyValuesHolder durationHoursHolder = PropertyValuesHolder.ofInt(Args.DURATION_HOURS, 0,(int) duration.getHours());
        PropertyValuesHolder durationMinutesHolder = PropertyValuesHolder.ofInt(Args.DURATION_MINUTES, 0,(int) duration.getMinutes());
        PropertyValuesHolder caloriesHolder = PropertyValuesHolder.ofInt(Args.CALORIES, 0, FirebaseClient.getInstance().getTotalCalories());

        mAnimator = ValueAnimator.ofPropertyValuesHolder(pushupsHolder, distanceHolder, durationHoursHolder, durationMinutesHolder, caloriesHolder);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.setDuration(2500);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDistanceTextView.setText(String.format("%.2f", mAnimator.getAnimatedValue(Args.DISTANCE)));
                mDurationTextView.setText(String.format("%d:%02d", mAnimator.getAnimatedValue(Args.DURATION_HOURS), mAnimator.getAnimatedValue(Args.DURATION_MINUTES)));
                mPushUpsTextView.setText(String.format("%d", mAnimator.getAnimatedValue(Args.PUSHUPS)));
                mCaloriesTextView.setText(String.format("%d", mAnimator.getAnimatedValue(Args.CALORIES)));
            }
        });
        mAnimator.start();

        mPieChart = (PieChart) getView().findViewById(R.id.home_pieChart);
        mPieChart.clear();
        Legend legend = mPieChart.getLegend();
        legend.setEnabled(false);
        List<Entry> runEntries = new ArrayList<>();
        List<String> runLabels = new ArrayList<>();
        List<Entry> pushUpsEntries = new ArrayList<>();
        List<String> pushUpsLabels = new ArrayList<>();
        runEntries.clear();
        runLabels.clear();
        pushUpsEntries.clear();
        pushUpsLabels.clear();
        int i = 0;
        int h = 0;
        SimpleDateFormat dataFormat = new SimpleDateFormat("dd.MM.yy");
        List<HistoryModel> runItems = FirebaseClient.getInstance().getRunItems();
        List<HistoryModel> pushUpItems = FirebaseClient.getInstance().getPushUpItems();
        Collections.sort(runItems);
        Collections.sort(pushUpItems);

        if (runItems.size() < 10) {
            for(HistoryModel runItem:runItems) {
                runEntries.add(new Entry(runItem.getValue(), i));
                runLabels.add(dataFormat.format(runItem.getDate()));
                i++;
            }
        }
        else {
            for(int count = 0; count < 10; count++) {
                HistoryModel runItem = runItems.get(count);
                runEntries.add(new Entry(runItem.getValue(), i));
                runLabels.add(dataFormat.format(runItem.getDate()));
                i++;
            }
        }
        if (pushUpItems.size() < 10) {
            for(HistoryModel pushUpItem:pushUpItems) {
                pushUpsEntries.add(new Entry(pushUpItem.getValue(), h));
                pushUpsLabels.add(dataFormat.format(pushUpItem.getDate()));
                h++;
            }
        }
        else {
            for(int count = 0; count < 10; count++) {
                HistoryModel pushUpItem = pushUpItems.get(count);
                pushUpsEntries.add(new Entry(pushUpItem.getValue(), h));
                pushUpsLabels.add(dataFormat.format(pushUpItem.getDate()));
                h++;
            }
        }

        PieDataSet runDataset = new PieDataSet(runEntries, "");
        runDataset.setColors(ColorTemplate.VORDIPLOM_COLORS);
        runDataset.setValueTextSize(15);
        runDataset.setValueFormatter(new RunValueFormatter());

        PieDataSet pushUpsDataset = new PieDataSet(pushUpsEntries, "");
        pushUpsDataset.setColors(ColorTemplate.VORDIPLOM_COLORS);
        pushUpsDataset.setValueTextSize(15);
        pushUpsDataset.setValueFormatter(new PushUpsValueFormatter());

        runData = new PieData(runLabels, runDataset);
        pushUpsData = new PieData(pushUpsLabels, pushUpsDataset);
        mPieChart.setData(runData);
        mPieChart.setCenterTextSize(16);
        mPieChart.setDescription("");
        mPieChart.setCenterText(getString(R.string.run));

        mPieChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent motionEvent) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent motionEvent) {
            }

            @Override
            public void onChartSingleTapped(MotionEvent motionEvent) {
                if(mState == RUN_STATE) {
                    mPieChart.clear();
                    mPieChart.setData(pushUpsData);
                    mPieChart.setCenterText(getString(R.string.pushups));
                    mPieChart.invalidate();
                    mState = PUSHUP_STATE;
                }
                else if(mState == PUSHUP_STATE) {
                    mPieChart.clear();
                    mPieChart.setData(runData);
                    mPieChart.setCenterText(getString(R.string.run));
                    mPieChart.invalidate();
                    mState = RUN_STATE;
                }
            }

            @Override
            public void onChartFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

            }

            @Override
            public void onChartScale(MotionEvent motionEvent, float v, float v1) {

            }

            @Override
            public void onChartTranslate(MotionEvent motionEvent, float v, float v1) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mAnimator.end();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAnimator.end();
    }
}
