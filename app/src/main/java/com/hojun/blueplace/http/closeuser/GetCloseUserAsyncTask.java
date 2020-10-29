package com.hojun.blueplace.http.closeuser;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.hojun.blueplace.MainActivity;
import com.hojun.blueplace.database.CloseUser;
import com.hojun.blueplace.database.CloseUserDao;
import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.database.UserData;
import com.hojun.blueplace.database.UserDataDao;
import com.hojun.blueplace.http.HttpProgressInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class GetCloseUserAsyncTask extends AsyncTask<Void, Integer, Void> {
    private static String TAG = "CloseUserAsyncTask";
    private int httpResult;
    private String httpMessage;
    private HttpProgressInterface httpProgressInterface;
    private LocalDatabase localDatabase;
    private UserDataDao userDataDao;
    private CloseUserDao closeUserDao;

    public GetCloseUserAsyncTask(LocalDatabase localDatabase, HttpProgressInterface httpProgressInterface) {
        super();
        this.localDatabase = localDatabase;
        if(localDatabase != null) {
            this.userDataDao = localDatabase.userDataDao();
            this.closeUserDao = localDatabase.closeUserDao();
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
            URL url = new URL("http://" + MainActivity.serverAddr + "/api/closeusers/");
            urlConnection = (HttpURLConnection)url.openConnection();
//            urlConnection.setDoOutput(true);           // 읽기모드 지정
            urlConnection.setRequestMethod("GET"); // 통신방식
            urlConnection.setRequestProperty("content-type", "application/json");

            if (BPSID != null) {
                Log.d(TAG, "previous BPSID is " + BPSID.value);
                urlConnection.setRequestProperty("Cookie", "BPSID=" + BPSID.value);
            } else {
                Log.d(TAG, "previous BPSID is null");
            }
//            OutputStream os = urlConnection.getOutputStream();
//            String jsonBodyString = "{ \"userEmail\" : \"" + userEmail + "\", \"userPassword\" : \"" + userPassword + "\" }";
//            os.write(jsonBodyString.getBytes("utf-8"));
//            os.flush();
//            os.close();

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
            JSONObject jUser = new JSONObject(body);

            JSONArray relatedUsers = jUser.getJSONArray("RelatedUser");

            for (int i = 0; i < relatedUsers.length(); i++) {
                JSONObject relatedUser = relatedUsers.getJSONObject(i);
                Integer id = relatedUser.getInt("Id");
                String email = relatedUser.getString("userEmail");
                String phoneNumber = relatedUser.getString("userPhoneNumber");
                Log.d(TAG, id + email + phoneNumber);
                CloseUser closeUser = new CloseUser();
                closeUser.id = id;
                closeUser.email = email;
                closeUser.phoneNumber = phoneNumber;
                try {
                    closeUserDao.insert(closeUser);
                } catch (Exception e) {
                    closeUserDao.update(closeUser);
                }
            }

//            test
//            CloseUser closeUserRe =  closeUserDao.getValue( "2");
//            Log.d("test", closeUserRe.id + closeUserRe.email + closeUserRe.phoneNumber);
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
