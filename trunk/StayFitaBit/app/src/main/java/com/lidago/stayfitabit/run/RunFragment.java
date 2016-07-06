package com.lidago.stayfitabit.run;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lidago.stayfitabit.Args;
import com.lidago.stayfitabit.MainActivity;
import com.lidago.stayfitabit.R;
import com.lidago.stayfitabit.Time;
import com.lidago.stayfitabit.firebase.FirebaseClient;
import com.lidago.stayfitabit.firebase.Running;
import com.lidago.stayfitabit.firebase.TrackingLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created on 09.05.2016.
 */
public class RunFragment extends Fragment implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private final int LOCATION_PERMISSION_REQUEST = 74;
    private final float cameraZoom = 16;

    private MapView mMapView;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private PolylineOptions mLineOptions;
    private Polyline mLine;
    private TextView mDurationTextView;
    private TextView mDistanceTextView;
    private TextView mPaceTextView;
    private FloatingActionButton mFAB;
    private LatLng mCurrent;
    private LatLng mPrevious;
    private List<TrackingLocation> mLocationList;
    private Running mRun;
    private Thread refreshThread;
    private boolean mIsRunning = false;
    private long millis = 0;

    public static RunFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(Args.TOOLBAR_TITLE, title);
        RunFragment fragment = new RunFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        return inflater.inflate(R.layout.fragment_run, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Firebase.setAndroidContext(getActivity());
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        setupUserInterface(savedInstanceState);
        setupToolbar();
        setListeners();
        setupLocation();
    }

    private void setupUserInterface(Bundle savedInstanceState) {
        mDistanceTextView = (TextView) getView().findViewById(R.id.run_distance_textView);
        mDurationTextView = (TextView) getView().findViewById(R.id.run_duration_textView);
        mPaceTextView = (TextView) getView().findViewById(R.id.run_pace_textView);
        mFAB = (FloatingActionButton) getView().findViewById(R.id.run_fab);
        mMapView = (MapView) getView().findViewById(R.id.run_mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    private void setupToolbar() {
        String title = getArguments().getString(Args.TOOLBAR_TITLE);
        // We have a Toolbar in place so we don't need to care about the NPE warning
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        setHasOptionsMenu(true);
    }

    private void setListeners() {
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void setupLocation() {
        mLocationList = new ArrayList<TrackingLocation>();
        mLineOptions = new PolylineOptions().color(Color.BLUE).width(12);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    private void showDialog() {
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (!mIsRunning) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.start_workout))
                        .setTitle(getString(R.string.run))
                        .setIcon(R.drawable.ic_directions_run);
                builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startRun();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.finish_workout))
                        .setTitle(getString(R.string.run))
                        .setIcon(R.drawable.ic_directions_run);
                builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        stopRun();
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
        else {
            showGPSDisabledAlertToUser();
        }
    }

    private void startRun() {
        MainActivity.disableNavigationDrawer();
        mIsRunning = true;
        mRun = new Running(FirebaseClient.getInstance().getUid());
        mRun.startRunActivity();
        startLocationUpdates();
        initThread();
    }

    private void stopRun() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }
        mRun.endRunActivity(getDistance(mLocationList), mLocationList);
        mIsRunning = false;
        removeLocationUpdates();
        FirebaseClient.getInstance().saveToFirebase(mRun);
        mRun = null;
        mLocationList.clear();
        clearUserInterface();
        MainActivity.enableNavigationDrawer();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }
        LocationRequest request = LocationRequest.create();
        request.setInterval(0);
        request.setSmallestDisplacement(5);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
    }

    private void removeLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void initThread() {
        refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsRunning) {
                    millis = System.currentTimeMillis() - mRun.getStartTime().getTime();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mIsRunning) {
                                    long time = millis;
                                    if (getDistance(mLocationList) > 0) {
                                        long pace = (long) (millis / (getDistance(mLocationList) / (double) 1000));
                                        Time paceTime = Time.UnitConverter.ConvertMillisToPace(pace);
                                        mPaceTextView.setText(String.format(Locale.GERMAN, "%02d:%02d", paceTime.getMinutes(), paceTime.getSeconds()));
                                    }
                                    Time duration = Time.UnitConverter.ConvertMillisToTime(time);
                                    double distance = (double) getDistance(mLocationList) / (double) 1000;
                                    mDistanceTextView.setText(String.format(Locale.GERMAN, "%.2f", distance));
                                    mDurationTextView.setText(String.format(Locale.GERMAN, "%02d:%02d:%02d", duration.getHours(), duration.getMinutes(), duration.getSeconds()));
                                }
                            }
                        });
                    }
                }
            }
        });
        refreshThread.start();
    }

    private void updateUserInterface(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(mCurrent, cameraZoom)), 1000, null);
        if (mPrevious != null) {
            List<LatLng> points = mLine.getPoints();
            points.add(new LatLng(location.getLatitude(),location.getLongitude()));
            mLine.setPoints(points);
        }
    }

    private void clearUserInterface() {
        mMap.clear();
        mDistanceTextView.setText(String.format(Locale.GERMAN, "%.2f", 0.00));
        mPaceTextView.setText(String.format(Locale.GERMAN, "%02d:%02d", 0, 0));
        mDurationTextView.setText(String.format(Locale.GERMAN, "%02d:%02d:%02d", 0, 0, 0));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_run_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.run_menu_stretching:
                Intent intent = new Intent(getActivity(), StretchingActivity.class);
                startActivity(intent);
                break;
            case R.id.run_menu_map_standard:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                item.setChecked(true);
                break;
            case R.id.run_menu_map_satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                item.setChecked(true);
                break;
            case R.id.run_menu_map_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                item.setChecked(true);
                break;
            case R.id.run_menu_map_terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                item.setChecked(true);
                break;
        }
        return true;
    }

    private int getDistance(List<TrackingLocation> locationList) {
        double distance = 0;
        LatLng previous = null;
        for (TrackingLocation location : locationList) {
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            if (previous != null) {
                distance += locationToMeters(current, previous);
            }
            previous = current;
        }
        return (int) distance;
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

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getString(R.string.gps_disabled))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.enable_gps),
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (mLastLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), cameraZoom)));
        }
        mLine = mMap.addPolyline(mLineOptions);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), cameraZoom)));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrent = new LatLng(location.getLatitude(), location.getLongitude());
        mLocationList.add(new TrackingLocation(location.getLatitude(), location.getLongitude()));
        updateUserInterface(location);
        mPrevious = mCurrent;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }
}
