package com.lidago.stayfitabit.firebase;

import java.sql.Timestamp;

import java.util.List;

/**
 * Created on 09.06.2016.
 */
public class Running {

    private String uid;
    private int value;
    private Timestamp startTime;
    private Timestamp endTime;
    private List<TrackingLocation> locationList;

    public Running() {
        // empty constructor required for Firebase
    }

    public Running(String uid) {
        this.uid = uid;
        value = 0;
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

    public List<TrackingLocation> getLocationList() {
        return locationList;
    }

    public void startRunActivity() {
        startTime = new Timestamp(System.currentTimeMillis());
    }

    public void endRunActivity(int value, List<TrackingLocation> locationList) {
        endTime = new Timestamp(System.currentTimeMillis());
        this.value = value;
        this.locationList = locationList;
    }
}
