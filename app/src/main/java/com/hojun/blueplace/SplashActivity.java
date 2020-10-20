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

import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.http.HttpProgressInterface;
import com.hojun.blueplace.http.InitAppAsyncTask;
import com.hojun.blueplace.http.user.LoginAsyncTask;

public class SplashActivity extends AppCompatActivity {
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        getPermission();

//        InitAppAsyncTask initAppAsyncTask = new InitAppAsyncTask(null, new HttpProgressInterface() {
//            @Override
//            public void onPreExecute() {
//                progressBar.setProgress(0);
//                Toast.makeText(getApplicationContext(),"init pre excute",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPostExecute(Integer httpResult, String Message) {
//                progressBar.setProgress(100);
//                Toast.makeText(getApplicationContext(),"init post excute",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onProgressUpdate(Integer progress) {
//                progressBar.setProgress(progress);
//                Toast.makeText(getApplicationContext(),"init",Toast.LENGTH_SHORT).show();
//            }
//        });
//        initAppAsyncTask.execute();

        LoginAsyncTask loginAsyncTask = new LoginAsyncTask("hjsung809@naver.com", "123123", LocalDatabase.getInstance(this),
                new HttpProgressInterface() {
                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onPostExecute(Integer httpResult, String Message) {
                        Toast.makeText(getApplicationContext(),"result : " + httpResult + " ,message : " + Message,Toast.LENGTH_LONG).show();
                        finishSplash();
                    }

                    @Override
                    public void onProgressUpdate(Integer progress) {

                    }
                }
        );
        loginAsyncTask.execute();

//        AsyncTask<Void,Void,Void> asyncTask = new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                try {
//                    for(int i = 2; i <= 100; i += 2) {
//                        progressBar.setProgress(i);
//                        Thread.sleep(20);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                finishSplash();
//                return null;
//            }
//        };
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