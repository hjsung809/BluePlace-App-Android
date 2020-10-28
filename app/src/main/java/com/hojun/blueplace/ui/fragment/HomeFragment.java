package com.hojun.blueplace.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hojun.blueplace.InfectReportActivity;
import com.hojun.blueplace.R;
import com.hojun.blueplace.SharedViewModel;
import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.database.UserData;
import com.hojun.blueplace.database.UserDataDao;

import java.util.List;
//import androidx.lifecycle.ViewModelProviders;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private SharedViewModel sharedViewModel;
    private LocalDatabase localDatabase;
    private UserDataDao userDataDao;

    private TextView detectedPlaceCount;
    private TextView closeUserDetectedPlaceCount;
    private TextView cliqueDetectedPlaceCount;
    private TextView regionDetectedPlaceCount;

    private Switch locationRecordSwitch;
    private Switch highAccuracySwitch;

    private Button infectButton;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        localDatabase = LocalDatabase.getInstance(getContext());
        userDataDao = localDatabase.userDataDao();

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        detectedPlaceCount = root.findViewById(R.id.detectedPlaceCount);
        closeUserDetectedPlaceCount = root.findViewById(R.id.closeUserDetectedPlaceCount);
        cliqueDetectedPlaceCount = root.findViewById(R.id.cliqueDetectedPlaceCount);
        regionDetectedPlaceCount = root.findViewById(R.id.regionDetectedPlaceCount);
        locationRecordSwitch = root.findViewById(R.id.locationRecordSwitch);
        highAccuracySwitch = root.findViewById(R.id.highAccuracySwitch);
        infectButton = root.findViewById(R.id.infectButton);


        // 감염자 보고.
        infectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "감염 보고 실행", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), InfectReportActivity.class);
                startActivity(intent);
            }
        });


        // 변경 사항 DB 반영.
        locationRecordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                highAccuracySwitch.setEnabled(isChecked);
                reflectUserDataToDB("location-record", "" + isChecked);
                if (isChecked) {
                    // 권한 확인 및 요청.
                    boolean permission =  ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;
                    Toast.makeText(getContext(), "위치 권한:" + permission, Toast.LENGTH_SHORT).show();
                    if (!permission) {
                        ActivityCompat.requestPermissions(getActivity(), new String[] {
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                },0
                        );
                    }

                    // 위치 로깅 시작 부분.
                }
            }
        });
        highAccuracySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reflectUserDataToDB("accurate-location", "" + isChecked);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        // DB의 변경사항을 스위치에 반영.
        sharedViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<List<UserData>>() {
            @Override
            public void onChanged(List<UserData> userData) {
                for(UserData ud: userData) {
                    Log.d(TAG, ud.id + "," + ud.value);
                    switch (ud.id) {
                        case "location-record":
                            locationRecordSwitch.setChecked(ud.value.equals("true"));
                            break;
                        case "accurate-location":
                            highAccuracySwitch.setChecked(ud.value.equals("true"));
                            break;
                    }
                }
            }
        });
    }

    private void reflectUserDataToDB(final String id, final String value) {
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    UserData userData = userDataDao.getValue(id);

                    if (userData == null) {
                        userData = new UserData();
                        userData.id = id;
                        userData.value = value;
                        userDataDao.insert(userData);
                    } else {
                        userData.id = id;
                        userData.value = value;
                        userDataDao.update(userData);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                return null;
            }
        };
        asyncTask.execute();
        return;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(),"ACCESS_FINE_LOCATION is not granted.",Toast.LENGTH_SHORT).show();
            locationRecordSwitch.setChecked(false);
            return;
        }

        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(),"ACCESS_BACKGROUND_LOCATION is not granted.",Toast.LENGTH_SHORT).show();
            locationRecordSwitch.setChecked(false);
            return;
        }
    }
}
