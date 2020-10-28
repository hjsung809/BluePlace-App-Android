package com.hojun.blueplace;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.http.HttpProgressInterface;
import com.hojun.blueplace.http.user.CheckSessionAsyncTask;

public class SplashActivity extends AppCompatActivity {
    public final static int LOGIN_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getPermission();

        CheckSessionAsyncTask checkSessionAsyncTask = new CheckSessionAsyncTask(LocalDatabase.getInstance(this), new HttpProgressInterface() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(Integer httpResult, String Message) {
                if (httpResult >= 400 && httpResult < 500) {
                    Toast.makeText(getApplicationContext(),"세션이 유효하지 않습니다. 로그인 시도합니다.",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST);
                }
                else if (httpResult >= 200 && httpResult < 300){
                    Toast.makeText(getApplicationContext(),"유효한 세션이 존재합니다.",Toast.LENGTH_LONG).show();
                    startActivity((new Intent(getApplicationContext(), MainActivity.class)));
                    finish();
                }
            }

            @Override
            public void onProgressUpdate(Integer progress) {

            }
        });
        checkSessionAsyncTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_REQUEST) {
            Toast.makeText(this, "login request response." + resultCode, Toast.LENGTH_SHORT).show();
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "login RESULT_OK", Toast.LENGTH_SHORT).show();
                startActivity((new Intent(this, MainActivity.class)));
                finish();
            } else if (requestCode == RESULT_CANCELED){
                Toast.makeText(this, "login RESULT_CANCELED", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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