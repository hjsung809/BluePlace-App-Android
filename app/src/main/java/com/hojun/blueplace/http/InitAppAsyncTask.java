package com.hojun.blueplace.http;

import android.os.AsyncTask;
import android.util.Log;

import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.database.UserData;
import com.hojun.blueplace.database.UserDataDao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InitAppAsyncTask extends AsyncTask<Void, Void, Void> {
    private static String TAG = "InitAppAsyncTask";
    private LocalDatabase localDatabase;
    private UserDataDao userDataDao;

    public InitAppAsyncTask(LocalDatabase localDatabase) {
        super();
        this.localDatabase = localDatabase;
        this.userDataDao = localDatabase.userDataDao();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;

        try {
            // ------- 로그인 -----------
            // 저장되어 있는 세션 로드
            UserData BPSID = userDataDao.getValue("BPSID");

            // 로그
            URL url = new URL("http://virustracker.iptime.org:3000/users/login");
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("POST"); // 통신방식
            urlConnection.setRequestProperty("content-type", "application/json");
            if (BPSID != null) {
                urlConnection.setRequestProperty("Cookie", "BPSID=" + BPSID.value);
            }
            urlConnection.setDoOutput(true);
            OutputStream os = urlConnection.getOutputStream();

            String jsonBodyString = " { \"userEmail\" : \"\", \"userPassword\" : \"\" }";
            os.write(jsonBodyString.getBytes("utf-8"));
//            os.flush();
            os.close();


            urlConnection.setDoInput(true);                // 읽기모드 지정
            if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                Log.d(TAG,"Response Code is not OK. " + urlConnection.getResponseCode());
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
            String line;
            String page = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null){
                page += line;
            }
            Log.d(TAG, page);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 연결 해제.
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }

        return null;
    }
}
