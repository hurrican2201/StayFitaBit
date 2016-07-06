package com.lidago.stayfitabit.pushups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidago.stayfitabit.R;

import java.util.List;

/**
 * Created on 25.06.2016.
 */
public class WorkoutPlanAdapter extends BaseAdapter {

    private static final int STATE_SECTIONED_CELL = 0;
    private static final int STATE_REGULAR_CELL = 1;

    private List<PushUpsWorkout> mList;
    private Context mContext;
    private int mSelectedPosition;

    public WorkoutPlanAdapter(Context context, List<PushUpsWorkout> list, int selectedWorkout)
    {
        mContext = context;
        mList = list;
        mSelectedPosition = (selectedWorkout-1) * 6;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if(position%6 == 0)
            return STATE_SECTIONED_CELL;
        else
            return STATE_REGULAR_CELL;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        if(getItemViewType(position) == STATE_SECTIONED_CELL)
            return true;
        else
            return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        int rowType = getItemViewType(position);
        boolean needSeparator = false;

        if (row == null)
        {
            row = LayoutInflater.from(mContext).inflate(R.layout.workout_list_view, null, false);
        }

        switch (rowType) {
            case STATE_SECTIONED_CELL:
                needSeparator = true;
                break;

            case STATE_REGULAR_CELL:
                needSeparator = false;
                break;
        }

        PushUpsWorkout workout = mList.get(position);
        RelativeLayout sectionLayout = (RelativeLayout) row.findViewById(R.id.workout_sectionLayout);
        TextView sectionTitle = (TextView) row.findViewById(R.id.workout_section_textView);
        TextView sets = (TextView) row.findViewById(R.id.workout_sets_textView);
        TextView day = (TextView) row.findViewById(R.id.workout_day_textView);
        RadioButton radioButton = (RadioButton) row.findViewById(R.id.workout_radio_button);
        if (needSeparator) {
            sectionTitle.setText(mContext.getString(R.string.level)+" "+workout.getLevel());
            if(position == mSelectedPosition)
                radioButton.setChecked(true);
            else
                radioButton.setChecked(false);
            sectionLayout.setVisibility(View.VISIBLE);
            sectionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedPosition = position;
                    WorkoutPlanAdapter.this.notifyDataSetChanged();
                }
            });
        } else {
            sectionLayout.setVisibility(View.GONE);
        }
        sets.setText(workout.getSet1()+" - "+workout.getSet2()+" - "+workout.getSet3()+" - "+workout.getSet4()+" - "+workout.getSet5());
        day.setText(mContext.getString(R.string.day)+" "+workout.getDay());
        return row;
    }

    public int getSelectedWorkout() {
        if(mSelectedPosition%6 == 0) {
            return (mSelectedPosition / 6) + 1;
        }
        else
            return 0;
    }

}
