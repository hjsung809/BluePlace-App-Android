package com.hojun.blueplace.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.app.ActivityCompat;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.common.util.concurrent.ListenableFuture;
import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.database.RawLocation;
import com.hojun.blueplace.database.RawLocationDao;


import java.util.List;


public class LocationRecordWorker extends ListenableWorker {
    private static final String TAG = "ListenableWorker";
    private static final int REPEAT = 4;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private LocalDatabase localDatabase;
    private RawLocationDao rawLocationDao;


    public LocationRecordWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        localDatabase = LocalDatabase.getInstance(getApplicationContext());
        if (localDatabase != null) {
            rawLocationDao = localDatabase.rawLocationDao();
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        Log.d(TAG, "startWork() invoked.");

        locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(600000)
                .setFastestInterval(10000)
                .setNumUpdates(REPEAT);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);


        return CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<Result>() {
            @Nullable
            @Override
            public Object attachCompleter(@NonNull final CallbackToFutureAdapter.Completer<Result> completer) throws Exception {
                Log.d(TAG, "getFuture() invoked.");
                locationCallback = new LocationCallback() {
                    int invoked = 0;

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        Log.d(TAG, "onLocationResult() invoked. invoked : " + ++invoked);
                        List<Location> locationList = locationResult.getLocations();
                        Log.d(TAG, "locationList size : " + locationList.size());


                        if (locationList.size() > 0) {
                            for (int i = 0; i < locationList.size(); i++) {
                                final Location location = locationList.get(i);
                                Log.d(TAG, "location : \n" + location);

                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            RawLocation rawLocation = translateLocation(location);
                                            rawLocationDao.insert(rawLocation);
                                        } catch (Exception e) {
                                            Log.d(TAG, e.toString());
                                        }
                                    }
                                }.start();
                            }
                        }
                        if (invoked == REPEAT) {
                            Log.d(TAG,"4번 위치 기록 완료됨.");
                            completer.set(Result.success());
                        }
                    }
                };

                try {
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                } catch (SecurityException e) {
                    completer.set(Result.failure());
                    Log.d(TAG, e.toString());
                }
                Log.d(TAG,"10분간 위치기록 시작.");
                return locationCallback;
            }
        });
    }

    private RawLocation translateLocation(Location location) {
        RawLocation rawLocation = new RawLocation();
        rawLocation.latitude = location.getLatitude();
        rawLocation.longitude = location.getLongitude();
        rawLocation.altitude = location.getAltitude();
        rawLocation.accuracy = location.getAccuracy();
        rawLocation.bearing = location.getBearing();
        rawLocation.utc_time = location.getTime();
        rawLocation.measurement_time = System.currentTimeMillis();
        return rawLocation;
    }
}
