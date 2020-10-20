package com.hojun.blueplace;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hojun.blueplace.database.InfectedPlace;
import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.database.RawLocation;
import com.hojun.blueplace.database.UserData;
import com.hojun.blueplace.database.UserPlace;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private static String TAG = "SharedViewModel";
    private LiveData<List<InfectedPlace>> infectedPlaces;
    private LiveData<List<RawLocation>> rawLocations;
    private LiveData<List<UserData>> userData;
    private LiveData<List<UserPlace>> userPlace;

    public SharedViewModel() {
        super();
//        centerOfMap = new MutableLiveData<>();
    }

    public void syncToDataBase(LocalDatabase database) {
        if (database == null) {
            Log.d(TAG, "!! database is null.");
            return;
        }
        infectedPlaces = database.infectedPlaceDao().getAll();
        rawLocations = database.rawLocationDao().getAll();
        userData = database.userDataDao().getAll();
        userPlace = database.userPlaceDao().getAll();
    }

//    public void setCenterOfMap(double latitude,double longitude) {
////        centerOfMap.setValue(new LatLng(latitude,longitude));
//    }
}
