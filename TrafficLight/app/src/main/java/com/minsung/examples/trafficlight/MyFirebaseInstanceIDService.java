package com.minsung.examples.trafficlight;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by minsung on 2018-09-30.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCMTOKEN", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        DataManager dataManager = new DataManager(this);
        String address = dataManager.getBleID();

        UpdateFCMTokenTask updateFCMTokenTask = new UpdateFCMTokenTask();
        updateFCMTokenTask.execute(Server.UPDATEFCMTOKEN_URL,address,refreshedToken);
    }


    class UpdateFCMTokenTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {

            String url = params[0];
            String addr = params[1];
            String token = params[2];


            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("Addr",addr)
                    .add("Token",token)
                    .build();

            //request
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Log.e("updateFCMToken", request.toString());
            try {
                Response response=client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                Log.e("updateFCMToken", e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("updateFCMToken", "onPostExecute: "+s);
        }
    }



}
