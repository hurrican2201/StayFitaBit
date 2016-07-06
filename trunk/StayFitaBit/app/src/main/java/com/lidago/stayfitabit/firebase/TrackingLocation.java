package com.lidago.stayfitabit.firebase;

import java.sql.Timestamp;

/**
 * Created on 09.06.2016.
 */
public class TrackingLocation {
    private double latitude;
    private double longitude;
    private Timestamp timestamp;

    public TrackingLocation() {
        // empty constructor required for Firebase
    }

    public TrackingLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
