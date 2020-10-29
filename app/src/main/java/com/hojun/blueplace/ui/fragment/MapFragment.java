package com.hojun.blueplace.ui.fragment;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.hojun.blueplace.R;
import com.hojun.blueplace.SharedViewModel;
import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.database.RawLocation;
import com.hojun.blueplace.database.UserPlace;
import com.hojun.blueplace.location.LocationToPlaceAsyncTask;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener
{
    private static final String TAG = "MapFragment";
    private SharedViewModel sharedViewModel;
    GoogleMap gMap = null;
    List<LatLng> rawLocationLatLngs;
    List<UserPlace> userPlaces;

    LocationToPlaceAsyncTask locationToPlaceAsyncTask;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.syncToDataBase(LocalDatabase.getInstance(getContext()));

        View root = inflater.inflate(R.layout.fragment_map, container, false);
        rawLocationLatLngs = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sharedViewModel.getRawLocations().observe(getViewLifecycleOwner(), new Observer<List<RawLocation>>() {
            @Override
            public void onChanged(List<RawLocation> rawLocations) {
                rawLocationLatLngs.clear();
                if (rawLocations != null) {
                    for(RawLocation rawLocation: rawLocations) {
//                    Log.d(TAG, rawLocation.id + ","  +rawLocation.latitude + "," + rawLocation.longitude);
                        rawLocationLatLngs.add(new LatLng(rawLocation.latitude, rawLocation.longitude));
                    }
                } else {
                    Log.d(TAG, "rawLocations is null in observer.");
                }
                reDrawMap();
            }
        });

        sharedViewModel.getUserPlace().observe(getViewLifecycleOwner(), new Observer<List<UserPlace>>() {
            @Override
            public void onChanged(List<UserPlace> userPlaces) {
                MapFragment.this.userPlaces = userPlaces;

                if (userPlaces != null) {
                    for (UserPlace userPlace : userPlaces) {
                        Log.d(TAG, "userPlaces: " + userPlace.id + ","  +userPlace.latitude + "," + userPlace.longitude);
                    }
                } else {
                    Log.d(TAG, "userPlaces is null in observer.");
                }
                reDrawMap();
            }
        });

        locationToPlaceAsyncTask = new LocationToPlaceAsyncTask(LocalDatabase.getInstance(getContext()),
                new LocationToPlaceAsyncTask.LocationToPlaceInterface() {
            @Override
            public void preExecute() {
                Log.d(TAG, "locationToPlaceAsyncTask executing..");
            }

            @Override
            public void success(String message) {
                Log.d(TAG, "locationToPlaceAsyncTask success..!");
                Log.d(TAG, message);
            }

            @Override
            public void fail(String message) {

            }
        });
        locationToPlaceAsyncTask.execute();
        return root;
    }

    private void reDrawMap() {
        if (gMap != null) {
            gMap.clear();

            if (rawLocationLatLngs != null) {
                for(LatLng rawLocationLatLng: rawLocationLatLngs) {
                    gMap.addCircle(new CircleOptions()
                            .center(rawLocationLatLng)
                            .radius(5)
                            .fillColor(Color.GREEN)
                            .zIndex(100)
                    );
                }
            } else {
                Log.d(TAG, "rawLocations is null in reDrawMap");
            }

            if (userPlaces != null) {
                for(UserPlace userPlace : userPlaces) {
                    gMap.addCircle(new CircleOptions()
                            .center(new LatLng(userPlace.latitude, userPlace.longitude))
                            .radius(userPlace.size)
                            .fillColor(Color.BLUE)
                            .zIndex(99)
                    );
                }
            } else {
                Log.d(TAG, "userPlaces is null in reDrawMap");
            }
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(googleMap != null){
            gMap = googleMap;
            try {
                gMap.setMyLocationEnabled(true);
                gMap.setOnMyLocationClickListener(this);
                gMap.setOnMyLocationButtonClickListener(this);

                gMap.getUiSettings().setZoomControlsEnabled(true);
            } catch (SecurityException e) {
                Toast.makeText(getContext(), "위치 권한이 없습니다",Toast.LENGTH_SHORT).show();
            }
        }
    }
}