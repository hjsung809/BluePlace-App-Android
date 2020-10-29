package com.hojun.blueplace.location;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.database.RawLocation;
import com.hojun.blueplace.database.RawLocationDao;
import com.hojun.blueplace.database.UserPlace;
import com.hojun.blueplace.database.UserPlaceDao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LocationToPlaceAsyncTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "LocationToPlaceTask";
    private static final int START_NUMBER_OF_POINT = 3;
    private static final int RAW_LOCATION_EXPIRE_TIME = 1000 * 60 * 60 * 24 *3;
    private static final int START_MIN_GAP_OF_POINT_METER = 50;
    private static final int SEPARATE_GAP_OF_POINT_METER = 30;
    private static final int SEPARATE_GAP_OF_POINT_TIME = 1000 * 60 * 60 * 24 *1;

    LocalDatabase localDatabase;
    RawLocationDao rawLocationDao;
    UserPlaceDao userPlaceDao;


    LocationToPlaceInterface locationToPlaceInterface;

    boolean isSuccess;
    String message;

    private static Location locationA;
    private static Location locationB;

    public LocationToPlaceAsyncTask(LocalDatabase localDatabase, LocationToPlaceInterface locationToPlaceInterface) {
        this.localDatabase = localDatabase;

        if(this.localDatabase != null) {
            rawLocationDao = this.localDatabase.rawLocationDao();
            userPlaceDao = this.localDatabase.userPlaceDao();
        }

        this.locationToPlaceInterface = locationToPlaceInterface;
        isSuccess = false;

        locationA = new Location("A");
        locationB = new Location("B");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (localDatabase == null) {
            message = "LocationToPlaceAsyncTask was failed. (local db is null)";
            return null;
        }

        Log.d(TAG, "LocationToPlace started.");
        try {
            // 3일 이상된 데이터를 지우고 시작.
            RawLocation rawLocation = rawLocationDao.getRecent();
            rawLocationDao.deleteByTime(rawLocation.utc_time - RAW_LOCATION_EXPIRE_TIME);
            List<RawLocation> rawLocations = rawLocationDao.getAllForPlace();
            Log.d(TAG, "Expired locations are deleted.");
            Log.d(TAG, "rawLocations size is " + rawLocations.size());

            // 기존 장소를 지움.
            userPlaceDao.deleteAll();
            Log.d(TAG, "all User Place are deleted.");

            // 시작 플래그
            boolean isStart = false;
            // 큐 처럼 사용.
            LinkedList<RawLocation> startLocations = new LinkedList<>();
            LatLng startLatLng = null; // 시작 위치.
            long startTime = 0; // 시작 시간.
            int visitCount = 0; // 측정된 점 갯수
            double maxDistance = 0; // 가장 먼 점.


            // 만들어진 장소
            List<UserPlace> userPlaces = new ArrayList<>();
            UserPlace place;
            RawLocation current = null;

            for (int i = 0; i < rawLocations.size(); i++) {
                current = rawLocations.get(i);

                if (i > 0) {
                    // 같은 측정일 때, 넘김.
                    RawLocation former = rawLocations.get(i - 1);
                    if(current.measurement_time == former.measurement_time){
                        Log.d(TAG, i + "th rawLocation is passed.");
                        continue;
                    }
                }

                // 장소화가 시작 되었을때
                if (isStart) {
                    double distance = calDistance(current, startLatLng);
                    // 30m 이내에 있거나, 하루 이하 지났을 때.
                    if (distance <= SEPARATE_GAP_OF_POINT_METER &&
                            current.measurement_time - startTime <= SEPARATE_GAP_OF_POINT_TIME) {
                        // 가까울 때 포함시키기.
                        visitCount ++;
                        maxDistance = Math.max(maxDistance, distance);
                    } else {
                        place = new UserPlace();
                        place.latitude = startLatLng.latitude;
                        place.longitude = startLatLng.longitude;
                        place.first_visit_time = startTime;
                        place.last_visit_time = current.utc_time;
                        place.visit_count = visitCount;
                        place.size = maxDistance;
                        userPlaces.add(place);

                        isStart = false;
                        visitCount = 0;
                        maxDistance = 0;
                        startLocations.clear();
                        startLocations.offer(rawLocations.get(i));
                        Log.d(TAG, i + "th point. location created.");
                    }
                } else {
                    // 3개 유지.
                    startLocations.offer(rawLocations.get(i));
                    if (startLocations.size() > START_NUMBER_OF_POINT) {
                        startLocations.poll();
                    }

                    // 3개의 점이 뭉쳐있으면 시작.
                    if (isStartPoint(startLocations)) {
                        startLatLng = calStartLatLng(startLocations);
                        startTime = startLocations.get(0).utc_time - 1000 * 60 * 3; // 5분 전으로 셋팅
                        isStart = true;
                        Log.d(TAG, i + "th point. startPoint is set.");
                    } else {
                        Log.d(TAG, i + "th point. startPoint is not set.");
                    }
                }
            }

            if (isStart && current != null) {
                place = new UserPlace();
                place.latitude = startLatLng.latitude;
                place.longitude = startLatLng.longitude;
                place.first_visit_time = startTime;
                place.last_visit_time = current.utc_time;
                place.visit_count = visitCount;
                place.size = maxDistance;
                userPlaces.add(place);
            }

            Log.d(TAG, "User Place is generated.");
            Log.d(TAG, "User Place length is " + userPlaces.size());
            UserPlace[] userPlacesTmp = new UserPlace[userPlaces.size()];
            for(int i = 0; i < userPlaces.size(); i++) {
                userPlacesTmp[i] = userPlaces.get(i);
            }
            userPlaceDao.insertAll(userPlacesTmp);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        isSuccess = true;
        message = "LocationToPlaceAsyncTask was successfully completed.";
        return null;
    }

    private LatLng calStartLatLng(LinkedList<RawLocation> startLocations) {
        double latitude = 0;
        double longitude = 0;

        for(int i = 0; i < startLocations.size(); i++) {
            latitude += startLocations.get(i).latitude;
            longitude += startLocations.get(i).longitude;
        }

        return new LatLng(latitude/startLocations.size(), longitude/startLocations.size());
    }

    private boolean isStartPoint(LinkedList<RawLocation> startLocations) {
        if (startLocations.size() < START_NUMBER_OF_POINT) {
            return false;
        }

        for(int i = 0; i < startLocations.size(); i++) {
            for(int j = i + 1; j < startLocations.size(); j++) {
                if (calDistance(startLocations.get(i), startLocations.get(j)) > START_MIN_GAP_OF_POINT_METER){
                    return false;
                }
            }
        }
        return true;
    }


    private double calDistance(RawLocation rawLocationA, RawLocation rawLocationB){
        //널 값 방지.
        if(rawLocationA.longitude == 0 || rawLocationB.longitude == 0
                || rawLocationA == null || rawLocationB == null){
            return 1000000;
        }

        locationA.setLatitude(rawLocationA.latitude);
        locationA.setLongitude(rawLocationA.longitude);
        locationB.setLatitude(rawLocationB.latitude);
        locationB.setLongitude(rawLocationB.longitude);
        return locationA.distanceTo(locationB);
    }

    private double calDistance(RawLocation rawLocationA, LatLng latLngB){
        //널 값 방지.
        if(rawLocationA.longitude == 0 || latLngB.longitude == 0 || latLngB == null){
            return 1000000;
        }

        locationA.setLatitude(rawLocationA.latitude);
        locationA.setLongitude(rawLocationA.longitude);
        locationB.setLatitude(latLngB.latitude);
        locationB.setLongitude(latLngB.longitude);
        return locationA.distanceTo(locationB);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        locationToPlaceInterface.preExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (isSuccess) {
            locationToPlaceInterface.success(message);
        } else {
            locationToPlaceInterface.fail(message);
        }
    }

    public interface LocationToPlaceInterface {
        void preExecute();
        void success(String message);
        void fail(String message);
    }
}
