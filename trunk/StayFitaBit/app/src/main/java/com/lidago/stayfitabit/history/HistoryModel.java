package com.lidago.stayfitabit.history;

import com.lidago.stayfitabit.ActivityType;
import com.lidago.stayfitabit.firebase.TrackingLocation;
import com.lidago.stayfitabit.pushups.PushUpsWorkout;

import java.util.Date;
import java.util.List;

/**
 * Created on 01.06.2016.
 */
public class HistoryModel implements Comparable<HistoryModel> {
    private ActivityType type;
    private Date date;
    private int value;
    private long duration;
    private PushUpsWorkout workout;
    private List<TrackingLocation> locationList;

    public HistoryModel(ActivityType type, Date date, int value, long duration, PushUpsWorkout workout) {
        this.type = type;
        this.date = date;
        this.value = value;
        this.duration = duration;
        this.workout = workout;
        this.locationList = null;
    }

    public HistoryModel(ActivityType type, Date date, int value, long duration, List<TrackingLocation> locationList) {
        this.type = type;
        this.date = date;
        this.value = value;
        this.duration = duration;
        this.locationList = locationList;
        this.workout = null;
    }

    public ActivityType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public float getValue() {
        return value;
    }

    public long getDuration() {
        return duration;
    }

    public PushUpsWorkout getWorkout() {
        return workout;
    }

    public List<TrackingLocation> getLocationList() {
        return locationList;
    }

    @Override
    public int compareTo(HistoryModel another) {
        if(this.getDate().getTime() <= another.getDate().getTime()) {
            return 1;
        }
        else
            return -1;
    }
}
