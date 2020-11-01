package com.hojun.blueplace.http.infectedplace;

import android.os.AsyncTask;
import android.util.Log;

import com.hojun.blueplace.MainActivity;
import com.hojun.blueplace.database.CloseUser;
import com.hojun.blueplace.database.CloseUserDao;
import com.hojun.blueplace.database.InfectedPlace;
import com.hojun.blueplace.database.InfectedPlaceDao;
import com.hojun.blueplace.database.LocalDatabase;
import com.hojun.blueplace.database.UserData;
import com.hojun.blueplace.database.UserDataDao;
import com.hojun.blueplace.http.HttpProgressInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetInfectedPlaceAsyncTask extends AsyncTask<Void, Integer, Void> {
    private static String TAG = "GetInfectedPlaceAsyncTask";
    private int httpResult;
    private String httpMessage;
    private HttpProgressInterface httpProgressInterface;
    private LocalDatabase localDatabase;
    private UserDataDao userDataDao;
    private InfectedPlaceDao infectedPlaceDao;

    public GetInfectedPlaceAsyncTask( LocalDatabase localDatabase, HttpProgressInterface httpProgressInterface) {
        super();
        this.localDatabase = localDatabase;
        if(localDatabase != null) {
            this.userDataDao = localDatabase.userDataDao();
            this.infectedPlaceDao = localDatabase.infectedPlaceDao();
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
            URL url = new URL("http://" + MainActivity.serverAddr + "/api/infectedplaces/");
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

            JSONArray relatedUsers = jUser.getJSONArray("SelfUser");
            for (int i = 0; i < relatedUsers.length(); i++) {
                JSONObject relatedUser = relatedUsers.getJSONObject(i);
                JSONObject infectedUsers = relatedUser.getJSONObject("InfectedUser");
                JSONArray infectedPlaces = infectedUsers.getJSONArray("InfectedPlaces");

                for (int j = 0; j < infectedPlaces.length(); j++) {
                    JSONObject infectedPlace = infectedPlaces.getJSONObject(j);

                    Integer id = infectedPlace.getInt("Id");
                    String infected_place_name = infectedPlace.getString("infectedPlaceName");
                    String infected_place_name_en = infectedPlace.getString("infectedPlaceNameEn");
                    String adress = infectedPlace.getString("adress");
                    String note = infectedPlace.getString("note");

                    double latitude = infectedPlace.getDouble("latitude");
                    double longitude = infectedPlace.getDouble("longitude");
                    double size = infectedPlace.getDouble("size");
                    int level = infectedPlace.getInt("level");

                    String first_visit_time = infectedPlace.getString("firstVisitTime");
                    String last_visit_time = infectedPlace.getString("lastVisitTime");
                    int visit_count = infectedPlace.getInt("visitCount");

                    InfectedPlace infectedPlaceDTO = new InfectedPlace();
                    infectedPlaceDTO.infected_place_name = infected_place_name;
                    infectedPlaceDTO.infected_place_name_en = infected_place_name_en;
                    infectedPlaceDTO.adress = adress;
                    infectedPlaceDTO.note = note;
                    infectedPlaceDTO.latitude = latitude;
                    infectedPlaceDTO.longitude = longitude;
                    infectedPlaceDTO.size = size;
                    infectedPlaceDTO.level = level;
                    infectedPlaceDTO.first_visit_time = first_visit_time;
                    infectedPlaceDTO.last_visit_time = last_visit_time;
                    infectedPlaceDTO.visit_count = visit_count;


                    try {
                        infectedPlaceDao.insert(infectedPlaceDTO);
                    } catch (Exception e) {
                        infectedPlaceDao.update(infectedPlaceDTO);
                    }
                }
            }
            JSONArray cliques = jUser.getJSONArray("CliqueMember");
            for (int i = 0; i < cliques.length(); i++) {
                JSONObject clique = cliques.getJSONObject(i);
                JSONArray cliqueMembers = clique.getJSONArray("CliqueMember");
                for(int k = 0; k < cliqueMembers.length(); k++) {
                    JSONObject cliqueMember = cliqueMembers.getJSONObject(k);
                    JSONObject infectedUsers = cliqueMember.getJSONObject("InfectedUser");
                    JSONArray infectedPlaces = infectedUsers.getJSONArray("InfectedPlaces");

                    for (int j = 0; j < infectedPlaces.length(); j++) {
                        JSONObject infectedPlace = infectedPlaces.getJSONObject(j);

                        Integer id = infectedPlace.getInt("Id");
                        String infected_place_name = infectedPlace.getString("infectedPlaceName");
                        String infected_place_name_en = infectedPlace.getString("infectedPlaceNameEn");
                        String adress = infectedPlace.getString("adress");
                        String note = infectedPlace.getString("note");

                        double latitude = infectedPlace.getDouble("latitude");
                        double longitude = infectedPlace.getDouble("longitude");
                        double size = infectedPlace.getDouble("size");
                        int level = infectedPlace.getInt("level");

                        String first_visit_time = infectedPlace.getString("firstVisitTime");
                        String last_visit_time = infectedPlace.getString("lastVisitTime");
                        int visit_count = infectedPlace.getInt("visitCount");

                        InfectedPlace infectedPlaceDTO = new InfectedPlace();
                        infectedPlaceDTO.infected_place_name = infected_place_name;
                        infectedPlaceDTO.infected_place_name_en = infected_place_name_en;
                        infectedPlaceDTO.adress = adress;
                        infectedPlaceDTO.note = note;
                        infectedPlaceDTO.latitude = latitude;
                        infectedPlaceDTO.longitude = longitude;
                        infectedPlaceDTO.size = size;
                        infectedPlaceDTO.level = level;
                        infectedPlaceDTO.first_visit_time = first_visit_time;
                        infectedPlaceDTO.last_visit_time = last_visit_time;
                        infectedPlaceDTO.visit_count = visit_count;


                        try {
                            infectedPlaceDao.insert(infectedPlaceDTO);
                        } catch (Exception e) {
                            infectedPlaceDao.update(infectedPlaceDTO);
                        }
                    }
                }
            }
            JSONArray regions = jUser.getJSONArray("Regions");
            for (int i = 0; i < cliques.length(); i++) {
                JSONObject region = regions.getJSONObject(i);
                JSONArray regionUsers = region.getJSONArray("Users");
                for (int k = 0; k < regionUsers.length(); k++) {
                    JSONObject regionUser = regionUsers.getJSONObject(k);
                    JSONObject infectedUsers = regionUser.getJSONObject("InfectedUser");
                    JSONArray infectedPlaces = infectedUsers.getJSONArray("InfectedPlaces");

                    for (int j = 0; j < infectedPlaces.length(); j++) {
                        JSONObject infectedPlace = infectedPlaces.getJSONObject(j);

                        Integer id = infectedPlace.getInt("Id");
                        String infected_place_name = infectedPlace.getString("infectedPlaceName");
                        String infected_place_name_en = infectedPlace.getString("infectedPlaceNameEn");
                        String adress = infectedPlace.getString("adress");
                        String note = infectedPlace.getString("note");

                        double latitude = infectedPlace.getDouble("latitude");
                        double longitude = infectedPlace.getDouble("longitude");
                        double size = infectedPlace.getDouble("size");
                        int level = infectedPlace.getInt("level");

                        String first_visit_time = infectedPlace.getString("firstVisitTime");
                        String last_visit_time = infectedPlace.getString("lastVisitTime");
                        int visit_count = infectedPlace.getInt("visitCount");

                        InfectedPlace infectedPlaceDTO = new InfectedPlace();
                        infectedPlaceDTO.infected_place_name = infected_place_name;
                        infectedPlaceDTO.infected_place_name_en = infected_place_name_en;
                        infectedPlaceDTO.adress = adress;
                        infectedPlaceDTO.note = note;
                        infectedPlaceDTO.latitude = latitude;
                        infectedPlaceDTO.longitude = longitude;
                        infectedPlaceDTO.size = size;
                        infectedPlaceDTO.level = level;
                        infectedPlaceDTO.first_visit_time = first_visit_time;
                        infectedPlaceDTO.last_visit_time = last_visit_time;
                        infectedPlaceDTO.visit_count = visit_count;


                        try {
                            infectedPlaceDao.insert(infectedPlaceDTO);
                        } catch (Exception e) {
                            infectedPlaceDao.update(infectedPlaceDTO);
                        }
                    }
                }
            }
//            test
//            CloseUser closeUserRe =  closeUserDao.getValue( "2");
//
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
