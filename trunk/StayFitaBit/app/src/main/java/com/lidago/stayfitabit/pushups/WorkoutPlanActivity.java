package com.lidago.stayfitabit.pushups;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.lidago.stayfitabit.Args;
import com.lidago.stayfitabit.R;

import java.util.List;

public class WorkoutPlanActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView mListView;
    private List<PushUpsWorkout> mList;
    private WorkoutPlanAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan);
        init();
    }

    private void init() {
        setupUserInterface();
        setupToolbar();
    }

    private void setupUserInterface() {
        mToolbar = (Toolbar) findViewById(R.id.workout_plan_toolbar);
        mListView = (ListView) findViewById(R.id.workout_plan_listView);
        mList = WorkoutPlan.getInstance().getWorkoutPlan();
        mAdapter = new WorkoutPlanAdapter(this, mList, getIntent().getIntExtra(Args.SELECTED_WORKOUT, 1));
        mListView.setAdapter(mAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        // We have a Toolbar in place so we don't need to care about the NPE warning
        getSupportActionBar().setTitle(getString(R.string.workout_plan));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent data = new Intent();
                data.putExtra(Args.SELECTED_WORKOUT, mAdapter.getSelectedWorkout());
                setResult(RESULT_OK, data);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
