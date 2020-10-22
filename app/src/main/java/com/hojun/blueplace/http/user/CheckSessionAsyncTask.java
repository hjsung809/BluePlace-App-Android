package com.hojun.blueplace.http.user;

import android.os.AsyncTask;
import android.util.Log;

import com.hojun.blueplace.MainActivity;
import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.database.UserData;
import com.hojun.blueplace.database.UserDataDao;
import com.hojun.blueplace.http.HttpProgressInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class CheckSessionAsyncTask extends AsyncTask<Void, Integer, Void> {
    private static String TAG = "CheckSessionAsyncTask";
    private int httpResult;
    private String httpMessage;
    private HttpProgressInterface httpProgressInterface;
    private LocalDatabase localDatabase;
    private UserDataDao userDataDao;

    public CheckSessionAsyncTask(LocalDatabase localDatabase,HttpProgressInterface httpProgressInterface) {
        super();
        this.localDatabase = localDatabase;
        if(localDatabase != null) {
            this.userDataDao = localDatabase.userDataDao();
        }
        this.httpProgressInterface = httpProgressInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (httpProgressInterface != null) {
            httpProgressInterface.onPreExecute();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (httpProgressInterface != null) {
            httpProgressInterface.onPostExecute(httpResult, httpMessage);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (httpProgressInterface != null) {
            httpProgressInterface.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        Log.d(TAG, "started.");
        try {
            // ------- 로그인 -----------
            // 저장되어 있는 세션 로드
            UserData BPSID = userDataDao.getValue("BPSID");
            if (BPSID == null) {
                httpResult = 400;
                httpMessage = "저장된 세션이 없습니다.";
                return null;
            }

            // 로그
            URL url = new URL("http://" + MainActivity.serverAddr + "/api/users/session");
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);                // 읽기모드 지정
            urlConnection.setRequestMethod("POST"); // 통신방식
            urlConnection.setRequestProperty("content-type", "application/json");

            if (BPSID != null) {
                Log.d(TAG, "previous BPSID is " + BPSID.value);
                urlConnection.setRequestProperty("Cookie", "BPSID=" + BPSID.value);
            } else {
                Log.d(TAG, "previous BPSID is null");
            }
            OutputStream os = urlConnection.getOutputStream();
//            String jsonBodyString = "{ \"userEmail\" : \"" + userEmail + "\", \"userPassword\" : \"" + userPassword + "\" }";
//            os.write(jsonBodyString.getBytes("utf-8"));
//            os.flush();
            os.close();

            httpResult = urlConnection.getResponseCode();
            if(httpResult != HttpURLConnection.HTTP_OK){
                Log.d(TAG,"Response Code is not OK. " + urlConnection.getResponseCode());
                return null;
            }

            // body의 내용.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
            String line;
            String body = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null){
                body += line;
            }
            Log.d(TAG, body);
            httpMessage = body;
        } catch (Exception e) {
            Log.d(TAG, e.toString());
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
