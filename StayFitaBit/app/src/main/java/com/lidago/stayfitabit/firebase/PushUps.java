package com.lidago.stayfitabit.firebase;

import com.lidago.stayfitabit.pushups.PushUpsWorkout;

import java.sql.Timestamp;

/**
 * Created on 09.06.2016.
 */
public class PushUps {

    private String uid;
    private int value;
    private Timestamp startTime;
    private Timestamp endTime;
    private PushUpsWorkout workout;

    public PushUps() {
        // empty constructor required for Firebase
    }

    public PushUps(String uid) {
        this.uid = uid;
        value = 0;
    }

    public PushUps(String uid, int value, Timestamp startTime, Timestamp endTime, PushUpsWorkout workout) {
        this.uid = uid;
        this.value = value;
        this.startTime = startTime;
        this.endTime = endTime;
        this.workout = workout;
    }

    public String getUid() {
        return uid;
    }

    public int getValue() {
        return value;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public PushUpsWorkout getWorkout() { return workout; }

    public void startPushUpActivity(PushUpsWorkout workout) {
        startTime = new Timestamp(System.currentTimeMillis());
        this.workout = workout;
    }

    public void increaseValue() {
        value = value + 1;
    }

    public void endPushUpActivity() {
        endTime = new Timestamp(System.currentTimeMillis());
    }
}
