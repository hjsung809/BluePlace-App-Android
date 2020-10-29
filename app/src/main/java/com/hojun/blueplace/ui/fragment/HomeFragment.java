package com.hojun.blueplace.ui.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

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
import com.hojun.blueplace.location.LocationRecordWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;
//import androidx.lifecycle.ViewModelProviders;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private final int REQUEST_PERMISSION_FINE_LOCATION = 1;
    private final int REQUEST_PERMISSION_COARSE_LOCATION = 2;
    private final int REQUEST_PERMISSION_BACKGROUND_LOCATION = 3;


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
        sharedViewModel.syncToDataBase(localDatabase);
        userDataDao = localDatabase.userDataDao();

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        detectedPlaceCount = root.findViewById(R.id.detectedPlaceCount);
        closeUserDetectedPlaceCount = root.findViewById(R.id.closeUserDetectedPlaceCount);
        cliqueDetectedPlaceCount = root.findViewById(R.id.cliqueDetectedPlaceCount);
        regionDetectedPlaceCount = root.findViewById(R.id.regionDetectedPlaceCount);
        locationRecordSwitch = root.findViewById(R.id.locationRecordSwitch);
        highAccuracySwitch = root.findViewById(R.id.highAccuracySwitch);
        infectButton = root.findViewById(R.id.infectButton);


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

        // 감염자 보고.
        infectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "감염 보고 실행", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), InfectReportActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 변경 사항 DB 반영.
        locationRecordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                highAccuracySwitch.setEnabled(isChecked);

                if (isChecked) {
                    // 위치 기록이 켜질 때.
                    // 권한 확인 및 요청. 권한 요청 성공시, 혹은 이미 권한 보유시, 위치 레코딩 시작.
//                    showFineLocationPermission();
                    checkPermissionAndRecordStart();
                } else {
                    // 위치 기록이 꺼질 때.
                    reflectUserDataToDB("location-record", "" + false);
                    stopLocationRecord();
                }
            }
        });

        highAccuracySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reflectUserDataToDB("accurate-location", "" + isChecked);
            }
        });
    }


    private void checkPermissionAndRecordStart() {
        boolean permissionCheck = ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        permissionCheck = permissionCheck && ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        permissionCheck = permissionCheck && ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (permissionCheck) {
            // 모든 권한을 가졌을 때.
            reflectUserDataToDB("location-record", "" + true);
            startLocationRecord();
        } else {
            reflectUserDataToDB("location-record", "" + false);
            if (ContextCompat.checkSelfPermission(
                    getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getPermission("위치 권한 획득(높은 정확도)", "위치 기록을 위해 권한이 필요합니다.", REQUEST_PERMISSION_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (ContextCompat.checkSelfPermission(
                    getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getPermission("권한 획득(낮은 정확도)", "위치 기록을 위해 권한이 필요합니다.", REQUEST_PERMISSION_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (ContextCompat.checkSelfPermission(
                    getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getPermission("권한 획득(백그라운드)", "앱이 실행중이 아닐 때, 위치기록을 위해 권한이 필요합니다.", REQUEST_PERMISSION_BACKGROUND_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
        }
    }

    private void getPermission(String title,
                               String message,
                               final int permissionRequestCode,
                               final String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            requestPermission(permissionRequestCode, permission);
                        }
                    });
            builder.create().show();
        } else {
            requestPermission(permissionRequestCode, permission);
        }
    }


//    private void showFineLocationPermission() {
//        boolean permissionCheck = ContextCompat.checkSelfPermission(
//                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//
//        permissionCheck = permissionCheck && ContextCompat.checkSelfPermission(
//                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
////
////        permissionCheck = permissionCheck && ContextCompat.checkSelfPermission(
////                getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
//
//        if (!permissionCheck) {
//            Toast.makeText(getContext(), "위치 권한을 획득하여야 합니다.", Toast.LENGTH_SHORT).show();
//
//            // 권한이 없을 때, 일단 끔.
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                showExplanation("위치 권한이 필요합니다.", "위치 기록을 위해 필수적인 권한입니다.", REQUEST_PERMISSION_FINE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
//            } else {
//                requestPermission(REQUEST_PERMISSION_FINE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
//            }
//            reflectUserDataToDB("location-record", "" + false);
//        } else {
////            Toast.makeText(getContext(), "위치 기록을 시작합니다.", Toast.LENGTH_SHORT).show();
//            reflectUserDataToDB("location-record", "" + true);
//            startLocationRecord();
//        }
//    }

//
//    private void showExplanation(String title,
//                                 String message,
//                                 final int permissionRequestCode,
//                                 final String... permissions) {
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle(title)
//                .setMessage(message)
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        requestPermission(permissionRequestCode, permissions);
//                    }
//                });
//        builder.create().show();
//    }

    private void requestPermission(int permissionRequestCode, String... permissions) {
        ActivityCompat.requestPermissions(getActivity(),
                permissions, permissionRequestCode);
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {

        boolean permissionCheck = ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        permissionCheck = permissionCheck && ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        permissionCheck = permissionCheck && ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (permissionCheck) {
            Toast.makeText(getContext(), "모든 권한이 획득 되었습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "일부 권한이 획득 되지 못했습니다.", Toast.LENGTH_SHORT).show();
        }

//
//        switch (requestCode) {
//            case REQUEST_PERMISSION_FINE_LOCATION:
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // 권한 획득 성공시.
//                    Toast.makeText(getContext(), "위치 권한이 획득 되었습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case REQUEST_PERMISSION_BACKGROUND_LOCATION:
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // 권한 획득 성공시.
//                    Toast.makeText(getContext(), "백그라운드 위치 권한이 획득 되었습니다. ", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "백그라운드 위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
    }


    private void startLocationRecord() {
        Constraints constraints = new Constraints.Builder().build();
        PeriodicWorkRequest geoRecordRequest = new PeriodicWorkRequest.Builder(LocationRecordWorker.class,15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(getContext()).enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.KEEP,geoRecordRequest);
        Toast.makeText(getContext(), "위치 기록을 시작합니다.", Toast.LENGTH_SHORT).show();
    }

    private void stopLocationRecord() {
        WorkManager.getInstance(getContext()).cancelUniqueWork("Location");
        Toast.makeText(getContext(), "위치 기록을 중지합니다.", Toast.LENGTH_SHORT).show();
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
}
