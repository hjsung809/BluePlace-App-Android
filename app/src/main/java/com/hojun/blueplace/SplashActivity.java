package com.hojun.blueplace;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.http.HttpProgressInterface;
import com.hojun.blueplace.http.user.CheckSessionAsyncTask;

public class SplashActivity extends AppCompatActivity {
    public final static int LOGIN_REQUEST = 100;
    private final int REQUEST_PERMISSION_PHONE_STATE=1;

    CheckSessionAsyncTask checkSessionAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 세션 유효성 체크 테스크.
        checkSessionAsyncTask = new CheckSessionAsyncTask(LocalDatabase.getInstance(this), new HttpProgressInterface() {
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

        // 기본 권한 획득 요청. 요청 성공 혹은 이미 권한 보유 시, 메인 엑티비티 실행.
        showPhoneStatePermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 로그인 엑티비티의 결과가 성공일때 실행.
        if (requestCode == LOGIN_REQUEST) {
//            Toast.makeText(this, "로그인 결과 코드:" + resultCode, Toast.LENGTH_SHORT).show();
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                startActivity((new Intent(this, MainActivity.class)));
            } else {
                // 로그인 실패시, 종료.
                Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void finishSplash() {
        startActivity((new Intent(this, MainActivity.class)));
        finish();
    }

    private void showPhoneStatePermission() {
        boolean phoneStatePermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;

        if (!phoneStatePermission) {
            // 권한에 대해 설명해야할때.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                showExplanation("권한이 필요합니다.", "핸드폰의 상태 조회 및 인터넷 접근을 해야합니다",  REQUEST_PERMISSION_PHONE_STATE,
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.INTERNET);
            } else {
                requestPermission(REQUEST_PERMISSION_PHONE_STATE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.INTERNET);
            }
        } else {
            Toast.makeText(this, "권한이 부여되어있습니다.", Toast.LENGTH_SHORT).show();
            checkSessionAsyncTask.execute();
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_PHONE_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한을 획득하였습니다.", Toast.LENGTH_SHORT).show();
                    checkSessionAsyncTask.execute();
                } else {
                    Toast.makeText(this, "권한이 거부되었습니다. 권한을 부여해 주세요.", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final int permissionRequestCode,
                                 final String... permissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permissionRequestCode, permissions);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(int permissionRequestCode, String... permissions) {
        ActivityCompat.requestPermissions(this,
                permissions, permissionRequestCode);
    }
}