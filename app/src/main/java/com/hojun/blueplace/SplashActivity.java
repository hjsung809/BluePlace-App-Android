package com.hojun.blueplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        getPermission();

        AsyncTask<Void,Void,Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    for(int i = 2; i <= 100; i += 2) {
                        progressBar.setProgress(i);
                        Thread.sleep(20);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finishSplash();
                return null;
            }
        };

//        asyncTask.execute();
    }

    private void finishSplash() {
        startActivity((new Intent(this, MainActivity.class)));
        finish();
    }

    private void getPermission(){
        boolean permission;
        permission =  ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED;

        if (!permission) {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE
                    },0
            );
        } else {
            Toast.makeText(this,"all permission granted",Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
//                == PackageManager.PERMISSION_GRANTED){
//            Toast.makeText(this,"INTERNET granted",Toast.LENGTH_SHORT).show();
//        }
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
//                == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "ACCESS_NETWORK_STATE granted", Toast.LENGTH_SHORT).show();
//        }
//    }
}