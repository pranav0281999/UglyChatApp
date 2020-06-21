package com.example.uglychatapp.network;

import android.os.AsyncTask;
import android.util.Log;

import com.example.uglychatapp.MainApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserRestCalls {
    private static final String TAG = "UserRestCalls";

    public static class GetAllUsers extends AsyncTask<Void, Void, String> {
        GetAllUsersCallback mCallback;

        public GetAllUsers(GetAllUsersCallback mCallback) {
            this.mCallback = mCallback;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public String doInBackground(Void... params) {
            try {
                URL url = new URL("http://" + MainApplication.serverNode + ":" + MainApplication.serverNodePort + "/users/allUsers");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json;charset=UTF-8");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                if (urlConnection.getInputStream() != null) {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    return sb.toString();
                }
            } catch (Exception e) {
                Log.v(TAG, e.toString());
            }

            return null;
        }

        public void onPostExecute(String result) {
            try {
                JSONObject resultObject = new JSONObject(result);
                JSONArray userList = resultObject.getJSONArray("userList");

                mCallback.onTaskComplete(true, userList);
            } catch (Exception e) {
                mCallback.onTaskComplete(false, null);
            }
        }
    }

    public abstract static class GetAllUsersCallback {
        public abstract void onTaskComplete(boolean success, JSONArray result);
    }

}
