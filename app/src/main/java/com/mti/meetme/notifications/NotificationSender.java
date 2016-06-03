package com.mti.meetme.notifications;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Corentin on 6/3/2016.
 */
public class NotificationSender extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        String REQUEST_URL = "https://fcm.googleapis.com/fcm/send";

        try{

            // 1. URL
            URL url = new URL(REQUEST_URL);

            // 2. Open connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 3. Specify POST method
            conn.setRequestMethod("POST");

            // 4. Set the headers
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=AIzaSyDXwo-cw_qTrpo_AfMbHeaWoee2PICs9kI");
            conn.setDoOutput(true);

            // 5. Add JSON data into POST request body

            String destinationId = params[0];

            JSONObject parent=new JSONObject();
            JSONObject notif=new JSONObject();
            notif.put("body", params[2]);
            notif.put("title", params[1] + " vous a envoyé un message");
            notif.put("priority", "high");

            parent.put("to", params[0]);
            parent.put("notification", notif);

            OutputStreamWriter wr= new OutputStreamWriter(conn .getOutputStream());
            wr.write(parent.toString());

            // 5.4 Send the request
            wr.flush();

            // 5.5 close
            wr.close();

            // 6. Get the response
            int responseCode = conn.getResponseCode();
            Log.w("NOTIFS", "\nSending 'POST' request to URL : " + url);
            Log.w("NOTIFS", "Response Code : " + responseCode);

            // 7. Print result
            Log.w("NOTIFS", conn.getResponseMessage());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
