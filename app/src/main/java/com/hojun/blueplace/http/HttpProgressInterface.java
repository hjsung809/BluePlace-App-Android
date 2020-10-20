package com.hojun.blueplace.http;

public interface HttpProgressInterface {
    void onPreExecute();
    void onPostExecute(Integer httpResult, String Message);
    void onProgressUpdate(Integer progress);
}
