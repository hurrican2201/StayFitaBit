package com.lidago.stayfitabit.history;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.model.LatLng;
import com.lidago.stayfitabit.ActivityType;
import com.lidago.stayfitabit.R;
import com.lidago.stayfitabit.charts.PaceValueFormatter;
import com.lidago.stayfitabit.charts.PaceYAxisValueFormatter;
import com.lidago.stayfitabit.charts.PushUpsValueFormatter;
import com.lidago.stayfitabit.charts.RunValueFormatter;
import com.lidago.stayfitabit.firebase.TrackingLocation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created on 01.06.2016.
 */
public class HistoryViewAdapter extends RecyclerView.Adapter<HistoryViewAdapter.HistoryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(HistoryModel item, int position);
    }

    private LayoutInflater mInflater;
    private OnItemClickListener mListener;
    private List<HistoryModel> mHistoryItems;

    public HistoryViewAdapter(Context context, List<HistoryModel> items, OnItemClickListener listener) {
        mInflater = LayoutInflater.from(context);
        mHistoryItems = items;
        mListener = listener;
    }

    public void setHistoryItems(List<HistoryModel> historyItems) {
        this.mHistoryItems = historyItems;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryViewHolder(mInflater.inflate(R.layout.history_recycler_view, parent, false));
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        holder.bind(mHistoryItems.get(position), mListener, position);
    }

    public int getItemCount() {
        return mHistoryItems.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mDetailView;
        private ImageView mRunImageView;
        private ImageView mPushUpsImageView;
        private TextView mDateTextView;
        private TextView mDurationTextView;
        private TextView mValueTextView;
        private CardView cardView;
        private LineChart mLineChart;
        private int titleHeight;
        private int detailHeight;

        public HistoryViewHolder(View view) {
            super(view);
            mDetailView = (LinearLayout) view.findViewById(R.id.history_detailView);
            mDetailView.setVisibility(View.GONE);
            mRunImageView = (ImageView) view.findViewById(R.id.history_run_imageView);
            mPushUpsImageView = (ImageView) view.findViewById(R.id.history_pushUps_imageView);
            mDateTextView = (TextView) view.findViewById(R.id.recyclerview_date_textView);
            mDurationTextView = (TextView) view.findViewById(R.id.recyclerview_duration_textView);
            mValueTextView = (TextView) view.findViewById(R.id.recyclerview_value_textView);
            cardView = (CardView) view.findViewById(R.id.cardView);
            cardView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    cardView.getViewTreeObserver().removeOnPreDrawListener(this);
                    titleHeight = cardView.getHeight();
                    detailHeight = cardView.getWidth();
                    ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                    layoutParams.height = titleHeight;
                    cardView.setLayoutParams(layoutParams);
                    return true;
                }
            });
            mLineChart = (LineChart) view.findViewById(R.id.history_lineChart);
        }

        public void bind(final HistoryModel item, OnItemClickListener listener, final int position) {
            if(item.getType() == ActivityType.RUN) {
                mLineChart.clear();
                mRunImageView.setVisibility(View.VISIBLE);
                mPushUpsImageView.setVisibility(View.GONE);
                mValueTextView.setText(String.format("%.2f km", (item.getValue()/1000)));
                List<Entry> runEntries = new ArrayList<>();
                List<String> runLabels = new ArrayList<>();
                runEntries.clear();
                runLabels.clear();

                int i = 0;
                float distance = 0;
                LatLng current;
                LatLng previous = null;
                long firstTime = 0;
                long lastTime = 0;

                if(item.getLocationList() != null && item.getValue() > 1000) {
                    for (TrackingLocation location : item.getLocationList()) {
                        current = new LatLng(location.getLatitude(), location.getLongitude());
                        if (previous != null) {
                            distance += locationToMeters(current, previous);
                        } else {
                            firstTime = location.getTimestamp().getTime();
                        }
                        previous = current;

                        if (distance > 997) {
                            lastTime = location.getTimestamp().getTime();
                            float value = (lastTime - firstTime);
                            runEntries.add(new Entry(value, i));
                            runLabels.add(Integer.toString(i + 1));
                            distance = 0;
                            i++;
                            firstTime = lastTime;
                        }
                    }
                    LineDataSet runDataset = new LineDataSet(runEntries, mInflater.getContext().getString(R.string.pace));
                    runDataset.setDrawFilled(true);
                    runDataset.setValueFormatter(new PaceValueFormatter());
                    LineData runData = null;
                    runData = new LineData(runLabels, runDataset);
                    mLineChart.getAxisLeft().setValueFormatter(new PaceYAxisValueFormatter());
                    mLineChart.getAxisRight().setValueFormatter(new PaceYAxisValueFormatter());
                    mLineChart.setData(runData);
                    mLineChart.setDescription(mInflater.getContext().getString(R.string.run));
                    runDataset.setColors(ColorTemplate.COLORFUL_COLORS);
                }
                else {
                    LineDataSet runDataset = new LineDataSet(runEntries, mInflater.getContext().getString(R.string.pace));
                    runDataset.setDrawFilled(true);
                    LineData runData = null;
                    mLineChart.setData(null);
                    mLineChart.setDescription(mInflater.getContext().getString(R.string.run));
                    runDataset.setColors(ColorTemplate.COLORFUL_COLORS);
                }
            }
            else {
                mLineChart.clear();
                mPushUpsImageView.setVisibility(View.VISIBLE);
                mRunImageView.setVisibility(View.GONE);
                mValueTextView.setText(String.format("%d", (int)item.getValue()));
                List<Entry> pushUpsEntries = new ArrayList<>();
                List<String> pushUpsLabels = new ArrayList<>();
                pushUpsEntries.clear();
                pushUpsLabels.clear();

                int previousValues = 0;
                pushUpsEntries.add(new Entry(item.getWorkout().getSet1(), 0));
                pushUpsLabels.add(Integer.toString(1));
                pushUpsEntries.add(new Entry(item.getWorkout().getSet2(), 1));
                pushUpsLabels.add(Integer.toString(2));
                pushUpsEntries.add(new Entry(item.getWorkout().getSet3(), 2));
                pushUpsLabels.add(Integer.toString(3));
                pushUpsEntries.add(new Entry(item.getWorkout().getSet4(), 3));
                pushUpsLabels.add(Integer.toString(4));
                previousValues = item.getWorkout().getSet1()+item.getWorkout().getSet2()+item.getWorkout().getSet3()+item.getWorkout().getSet4();
                pushUpsEntries.add(new Entry(item.getValue()-previousValues, 4));
                pushUpsLabels.add(Integer.toString(5));


                if(item.getValue()-previousValues>0)
                {
                    LineDataSet pushUpsDataset = new LineDataSet(pushUpsEntries, mInflater.getContext().getString(R.string.count));
                    pushUpsDataset.setDrawFilled(true);
                    LineData pushUpsData = null;
                    pushUpsData = new LineData(pushUpsLabels, pushUpsDataset);
                    pushUpsDataset.setValueFormatter(new PushUpsValueFormatter());
                    mLineChart.setData(pushUpsData);
                    mLineChart.setDescription(mInflater.getContext().getString(R.string.pushups));
                    pushUpsDataset.setColors(ColorTemplate.COLORFUL_COLORS);
                }
            }
            SimpleDateFormat dataFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            mDateTextView.setText(dataFormat.format(item.getDate()));
            long hours = TimeUnit.MILLISECONDS.toHours(item.getDuration());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(item.getDuration() - TimeUnit.HOURS.toMillis(hours));
            long seconds = TimeUnit.MILLISECONDS.toSeconds(item.getDuration() - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));
            mDurationTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleCardViewHeight(detailHeight);
                    mListener.onItemClick(item, position);
                }
            });
        }

        private void toggleCardViewHeight(int height) {

            if (cardView.getHeight() == titleHeight) {
                expandView(height);
                mDetailView.setVisibility(View.VISIBLE);
            } else {
                collapseView();
                mDetailView.setVisibility(View.GONE);
            }
        }

        public void collapseView() {

            ValueAnimator anim = ValueAnimator.ofInt(cardView.getMeasuredHeightAndState(),
                    titleHeight);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                    layoutParams.height = val;
                    cardView.setLayoutParams(layoutParams);
                }
            });
            anim.start();
        }

        public void expandView(int height) {

            ValueAnimator anim = ValueAnimator.ofInt(cardView.getMeasuredHeightAndState(),
                    height);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                    layoutParams.height = val;
                    cardView.setLayoutParams(layoutParams);
                }
            });
            anim.start();
        }

        private int locationToMeters(LatLng A, LatLng B){
            double pk = (180.f/Math.PI);

            double a1 = A.latitude / pk;
            double a2 = A.longitude / pk;

            double b1 = B.latitude / pk;
            double b2 = B.longitude / pk;

            double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
            double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
            double t3 = Math.sin(a1)*Math.sin(b1);
            double tt = Math.acos(t1 + t2 + t3);
            int result = (int)(6366000*tt);

            return result;
        }
    }
}
