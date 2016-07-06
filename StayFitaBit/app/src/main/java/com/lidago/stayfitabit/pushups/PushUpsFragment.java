package com.lidago.stayfitabit.pushups;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.firebase.client.Firebase;
import com.lidago.stayfitabit.Args;
import com.lidago.stayfitabit.R;
import com.lidago.stayfitabit.firebase.FirebaseClient;
import com.lidago.stayfitabit.firebase.PushUps;

/**
 * Created on 09.05.2016.
 */
public class PushUpsFragment extends Fragment {

    private final int WORKOUT_SELECT_REQUEST = 54;
    private final int PROGRESS_MAX = 6;
    private final String SELECTED_COLOR = "#FF5722";
    private final String ARROW_COLOR = "#BDBDBD";

    private SharedPreferences mPrefs;
    private PushUps mPushUps;
    private ProgressBar mTrainingProgressBar;
    private TextView mProgressTextView;
    private TextView mCompletedTextView;
    private TextView mToDoTextView;
    private TextView mSetsTextView;
    private TextView mWorkoutTextView;
    private TextView mSet1TextView;
    private TextView mSet2TextView;
    private TextView mSet3TextView;
    private TextView mSet4TextView;
    private TextView mSet5TextView;
    private ImageView mArrow1ImageView;
    private ImageView mArrow2ImageView;
    private ImageView mArrow3ImageView;
    private ImageView mArrow4ImageView;
    private FloatingActionButton mFAB;
    private ImageButton mOptionsButton;
    private LinearLayout mInfoBar;
    private LinearLayout mTrainingBar;
    private LinearLayout mMainLayout;
    private CardView mCardView;
    private TextSwitcher mTextSwitcher;
    private TextView mTextView;
    private ToneGenerator mToneGenerator;
    private boolean mIsRunning = false;
    private boolean mIsPaused = false;
    private Thread refreshThread;
    private PushUpsWorkout mWorkout;
    private WorkoutPlan mWorkoutPlan;
    private int mState = 0;
    private int mSelectedWorkout = 1;
    private int mDay = 3;
    private int mPauseTime;

    public static PushUpsFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(Args.TOOLBAR_TITLE, title);
        PushUpsFragment fragment = new PushUpsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        return inflater.inflate(R.layout.fragment_pushups, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Firebase.setAndroidContext(getActivity());
        init();
    }

    private void init() {
        getPreferences();
        setupUserInterface();
        setupToolbar();
        setListeners();
        loadAnimations();
        setFactory();
        updateUserInterface();
    }

    private void getPreferences() {
        mPrefs = getActivity().getSharedPreferences(Args.PUSHUPS_PREFERENCES, Context.MODE_PRIVATE);
        mSelectedWorkout = mPrefs.getInt(Args.SELECTED_WORKOUT, 1);
        mDay = mPrefs.getInt(Args.SELECTED_DAY, 1);
    }

    private void setupUserInterface() {
        mWorkoutPlan = WorkoutPlan.getInstance();
        mProgressTextView = (TextView) getView().findViewById(R.id.pushups_progress_textView);
        mCompletedTextView = (TextView) getView().findViewById(R.id.pushups_completed_textView);
        mSetsTextView = (TextView) getView().findViewById(R.id.pushups_sets_textView);
        mWorkoutTextView = (TextView) getView().findViewById(R.id.pushups_workout_textView);
        mToDoTextView = (TextView) getView().findViewById(R.id.pushups_todo_textView);
        mSet1TextView = (TextView) getView().findViewById(R.id.pushups_set1_textView);
        mSet2TextView = (TextView) getView().findViewById(R.id.pushups_set2_textView);
        mSet3TextView = (TextView) getView().findViewById(R.id.pushups_set3_textView);
        mSet4TextView = (TextView) getView().findViewById(R.id.pushups_set4_textView);
        mSet5TextView = (TextView) getView().findViewById(R.id.pushups_set5_textView);
        mArrow1ImageView = (ImageView) getView().findViewById(R.id.pushups_arrow1_imageView);
        mArrow2ImageView = (ImageView) getView().findViewById(R.id.pushups_arrow2_imageView);
        mArrow3ImageView = (ImageView) getView().findViewById(R.id.pushups_arrow3_imageView);
        mArrow4ImageView = (ImageView) getView().findViewById(R.id.pushups_arrow4_imageView);
        mFAB = (FloatingActionButton) getView().findViewById(R.id.pushups_fab);
        mOptionsButton = (ImageButton) getView().findViewById(R.id.pushups_options_button);
        mInfoBar = (LinearLayout) getView().findViewById(R.id.pushups_infobar);
        mTrainingBar = (LinearLayout) getView().findViewById(R.id.pushups_trainingbar);
        mTextSwitcher = (TextSwitcher) getView().findViewById(R.id.pushups_number_textSwitcher);
        mTextView = (TextView) getView().findViewById(R.id.pushups_textView);
        mCardView = (CardView) getView().findViewById(R.id.pushups_cardView);
        mMainLayout = (LinearLayout) getView().findViewById(R.id.pushups_main_layout);
        mTrainingProgressBar = (ProgressBar) getView().findViewById(R.id.pushups_progressBar);
        mTrainingProgressBar.setMax(PROGRESS_MAX);
        mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    }

    private void setupToolbar() {
        String title = getArguments().getString(Args.TOOLBAR_TITLE);
        // We have a Toolbar in place so we don't need to care about the NPE warning
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private void setListeners() {
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        mOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWorkoutPlan();
            }
        });
    }

    private void loadAnimations() {
        Animation in = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);
        mTextSwitcher.setInAnimation(in);
        mTextSwitcher.setOutAnimation(out);
    }

    private void setFactory() {
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getActivity());
                textView.setTextSize(200);
                textView.setGravity(Gravity.CENTER);
                return textView;
            }
        });
    }

    private void showDialog() {
        if(!mIsRunning) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.start_workout))
                    .setTitle(getString(R.string.pushups))
                    .setIcon(R.drawable.ic_pushup);
            builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startPushups();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.finish_workout))
                    .setTitle(getString(R.string.pushups))
                    .setIcon(R.drawable.ic_pushup);
            builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    stopPushups();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void startPushups() {
        switchUserInterface();
        mPushUps = new PushUps(FirebaseClient.getInstance().getUid());
        mPushUps.startPushUpActivity(mWorkout);
    }

    private void stopPushups() {
        mState++;
        updateTrainingBar();
        switchUserInterface();
        mPushUps.endPushUpActivity();
        FirebaseClient.getInstance().saveToFirebase(mPushUps);
        if(isWorkoutCompleted() && mDay != 6) {
            SharedPreferences.Editor editor = mPrefs.edit();
            mDay++;
            editor.putInt(Args.SELECTED_WORKOUT, mSelectedWorkout);
            editor.putInt(Args.SELECTED_DAY, mDay);
            editor.apply();
            updateUserInterface();
        }
        mState = 0;
        mTextSwitcher.setText("");
        mPushUps = null;
    }

    private void switchUserInterface() {
        if(!mIsRunning) {
            mInfoBar.setVisibility(View.GONE);
            mCardView.setVisibility(View.GONE);
            mSet1TextView.setText(Integer.toString(mWorkout.getSet1()));
            mSet2TextView.setText(Integer.toString(mWorkout.getSet2()));
            mSet3TextView.setText(Integer.toString(mWorkout.getSet3()));
            mSet4TextView.setText(Integer.toString(mWorkout.getSet4()));
            mSet5TextView.setText(Integer.toString(mWorkout.getSet5()));
            mTrainingBar.setVisibility(View.VISIBLE);
            mTextSwitcher.setVisibility(View.VISIBLE);
            mTextSwitcher.setText(Integer.toString(mWorkout.getSet1()));
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(getString(R.string.remaining));
            updateTrainingBar();
            mMainLayout.setClickable(true);
            mMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doPushup();
                }
            });
            mIsRunning = true;
        }
        else {
            mInfoBar.setVisibility(View.VISIBLE);
            mCardView.setVisibility(View.VISIBLE);
            mTrainingBar.setVisibility(View.GONE);
            mTextSwitcher.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);
            mMainLayout.setOnClickListener(null);
            mMainLayout.setClickable(false);
            mIsRunning = false;
        }
    }

    private void doPushup() {
        int mod = mState % 2;
        int set = mState / 2;
        if(mod == 0) {
            int max = 0;
            switch(set) {
                case 0:
                    max = mWorkout.getSet1();
                    break;
                case 1:
                    max = mWorkout.getSet1() + mWorkout.getSet2();
                    break;
                case 2:
                    max = mWorkout.getSet1() + mWorkout.getSet2() + mWorkout.getSet3();
                    break;
                case 3:
                    max = mWorkout.getSet1() + mWorkout.getSet2() + mWorkout.getSet3() + mWorkout.getSet4();
                    break;
                case 4:
                    max = mWorkout.getSet1() + mWorkout.getSet2() + mWorkout.getSet3() + mWorkout.getSet4() + mWorkout.getSet5();
                    break;
            }
            if(max != 0) {
                mPushUps.increaseValue();
                mToneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 1000);
                if((mPushUps.getValue() < max)) {
                    mTextSwitcher.setText(Integer.toString(max - mPushUps.getValue()));
                }
                else if(mPushUps.getValue() >= max && mState == 8) {
                    mTextSwitcher.setText(Integer.toString(mPushUps.getValue()- max + mWorkout.getSet5()));
                    mTextView.setText(getString(R.string.finished_workout));
                }
                else {
                    mState++;
                    updateTrainingBar();
                    mIsPaused = true;
                    startPauseTimer();
                    mTextView.setText(getString(R.string.pause));
                }
            }
        }
        if(mod == 1) {
            mIsPaused = false;
            mState++;
            set = mState/2;
            switch (set) {
                case 1:
                    mTextSwitcher.setText(Integer.toString(mWorkout.getSet2()));
                    break;
                case 2:
                    mTextSwitcher.setText(Integer.toString(mWorkout.getSet3()));
                    break;
                case 3:
                    mTextSwitcher.setText(Integer.toString(mWorkout.getSet4()));
                    break;
                case 4:
                    mTextSwitcher.setText(Integer.toString(mWorkout.getSet5()));
                    break;
            }
            mTextView.setText(getString(R.string.remaining));
            updateTrainingBar();
        }
    }

    private void updateTrainingBar() {
        switch (mState) {
            case 0:
                mSet1TextView.setBackgroundColor(Color.parseColor(SELECTED_COLOR));
                mSet1TextView.setTextColor(Color.WHITE);
                break;

            case 1:
                mSet1TextView.setBackgroundColor(Color.TRANSPARENT);
                mSet1TextView.setTextColor(Color.BLACK);
                mArrow1ImageView.setColorFilter(Color.parseColor(SELECTED_COLOR));
                break;

            case 2:
                mArrow1ImageView.setColorFilter(Color.parseColor(ARROW_COLOR));
                mSet2TextView.setBackgroundColor(Color.parseColor(SELECTED_COLOR));
                mSet2TextView.setTextColor(Color.WHITE);
                break;

            case 3:
                mSet2TextView.setBackgroundColor(Color.TRANSPARENT);
                mSet2TextView.setTextColor(Color.BLACK);
                mArrow2ImageView.setColorFilter(Color.parseColor(SELECTED_COLOR));
                break;

            case 4:
                mArrow2ImageView.setColorFilter(Color.parseColor(ARROW_COLOR));
                mSet3TextView.setBackgroundColor(Color.parseColor(SELECTED_COLOR));
                mSet3TextView.setTextColor(Color.WHITE);
                break;

            case 5:
                mSet3TextView.setBackgroundColor(Color.TRANSPARENT);
                mSet3TextView.setTextColor(Color.BLACK);
                mArrow3ImageView.setColorFilter(Color.parseColor(SELECTED_COLOR));
                break;

            case 6:
                mArrow3ImageView.setColorFilter(Color.parseColor(ARROW_COLOR));
                mSet4TextView.setBackgroundColor(Color.parseColor(SELECTED_COLOR));
                mSet4TextView.setTextColor(Color.WHITE);
                break;

            case 7:
                mSet4TextView.setBackgroundColor(Color.TRANSPARENT);
                mSet4TextView.setTextColor(Color.BLACK);
                mArrow4ImageView.setColorFilter(Color.parseColor(SELECTED_COLOR));
                break;

            case 8:
                mArrow4ImageView.setColorFilter(Color.parseColor(ARROW_COLOR));
                mSet5TextView.setBackgroundColor(Color.parseColor(SELECTED_COLOR));
                mSet5TextView.setTextColor(Color.WHITE);
                break;
            default:
                mSet5TextView.setBackgroundColor(Color.TRANSPARENT);
                mSet5TextView.setTextColor(Color.BLACK);
                break;
        }
    }

    private void startPauseTimer() {
        mPauseTime = 59;
        refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsPaused) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mPauseTime > 0 && mIsPaused) {
                                mTextSwitcher.setText(Integer.toString(mPauseTime));
                            }
                            else {
                                mIsPaused = false;
                            }
                            mPauseTime--;
                        }
                    });
                }
            }
        });
        refreshThread.start();
    }

    private void selectWorkoutPlan() {
        Intent intent = new Intent(getActivity(), WorkoutPlanActivity.class);
        intent.putExtra(Args.SELECTED_WORKOUT, mSelectedWorkout);
        startActivityForResult(intent, WORKOUT_SELECT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == WORKOUT_SELECT_REQUEST) {
            if(resultCode == getActivity().RESULT_OK) {
                if(mSelectedWorkout != data.getIntExtra(Args.SELECTED_WORKOUT, 1)) {
                    mSelectedWorkout = data.getIntExtra(Args.SELECTED_WORKOUT, 1);
                    mDay = 1;
                    updateUserInterface();
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putInt(Args.SELECTED_WORKOUT, mSelectedWorkout);
                    editor.putInt(Args.SELECTED_DAY, mDay);
                    editor.apply();
                }
            }
        }
    }

    private void updateUserInterface() {
        mWorkout = getCurrentWorkout(mSelectedWorkout, mDay);
        mToDoTextView.setText(Integer.toString(getToDoPushUpsCount(mSelectedWorkout, mDay)));
        mCompletedTextView.setText(Integer.toString(getCompletedPushUpsCount(mSelectedWorkout, mDay)));
        mProgressTextView.setText(mDay-1+" / "+PROGRESS_MAX);
        mTrainingProgressBar.setProgress(mDay-1);
        mSetsTextView.setText(mWorkout.getSet1()+" - "+mWorkout.getSet2()+" - "+mWorkout.getSet3()+" - "+mWorkout.getSet4()+" - "+mWorkout.getSet5());
        mWorkoutTextView.setText(Integer.toString(getCurrentWorkoutPushupsCount(mSelectedWorkout, mDay)));
    }

    private int getCompletedPushUpsCount(int selectedWorkout, int day) {
        int count = 0;
        for (PushUpsWorkout workout: mWorkoutPlan.getWorkoutPlan()) {
            if(workout.getLevel() == selectedWorkout)
                if(workout.getDay() < day)
                    count = count + workout.getSet1() + workout.getSet2() + workout.getSet3() + workout.getSet4() + workout.getSet5();
        }
        return count;
    }

    private int getToDoPushUpsCount(int selectedWorkout, int day) {
        int count = 0;
        for (PushUpsWorkout workout: mWorkoutPlan.getWorkoutPlan()) {
            if(workout.getLevel() == selectedWorkout)
                count = count + workout.getSet1() + workout.getSet2() + workout.getSet3() + workout.getSet4() + workout.getSet5();
        }
        count = count - getCompletedPushUpsCount(selectedWorkout, day);
        return count;
    }

    private int getCurrentWorkoutPushupsCount(int selectedWorkout, int day) {
        int count = 0;
        for (PushUpsWorkout workout: mWorkoutPlan.getWorkoutPlan()) {
            if(workout.getLevel() == selectedWorkout)
                if(workout.getDay() == day)
                    count = count + workout.getSet1() + workout.getSet2() + workout.getSet3() + workout.getSet4() + workout.getSet5();
        }
        return count;
    }

    private PushUpsWorkout getCurrentWorkout(int selectedWorkout, int day) {
        PushUpsWorkout workout = null;
        for (PushUpsWorkout work: mWorkoutPlan.getWorkoutPlan()) {
            if(work.getLevel() == selectedWorkout)
                if (work.getDay() == day)
                    workout = work;
        }
        return workout;
    }

    private boolean isWorkoutCompleted() {
        if(getCurrentWorkoutPushupsCount(mSelectedWorkout, mDay) <= mPushUps.getValue()) {
            return true;
        }
        else {
            return false;
        }
    }
}
