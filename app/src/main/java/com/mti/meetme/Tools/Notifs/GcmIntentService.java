package com.mti.meetme.Tools.Notifs;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mti.meetme.MapsActivity;
import com.mti.meetme.R;
import com.pubnub.api.Callback;
import com.pubnub.api.PnGcmMessage;
import com.pubnub.api.PnMessage;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thiba_000 on 03/05/2016.
 */
public class GcmIntentService extends IntentService {

    int NOTIFICATION_ID = 1;
    static String  TAG ="Mettme";
    static String  CHANNEL ="channel";
    GoogleCloudMessaging gcm;
    String regId;
    String PROPERTY_REG_ID = "regID";

    public static Callback callback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            Log.i(TAG, "Success on Channel " + CHANNEL + " : " + message);
        }
        @Override
        public void errorCallback(String channel, PubnubError error) {
            Log.i(TAG, "Error On Channel " + CHANNEL + " : " + error);
        }
    };



    private final Pubnub pubnub = new Pubnub(getString(R.string.SubscribeKey) /* replace with your publish key */,
            getString(R.string.PublishKey));

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            sendNotification("Received: " + extras.toString());
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MapsActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("PubNub GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    private void register() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            try {
                regId = getRegistrationId(getApplication());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (regId.isEmpty()) {
                registerInBackground();
            } else {

            }
        } else {
            Log.e(TAG, "No valid Google Play Services APK found.");
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //GooglePlayServicesUtil.getErrorDialog(resultCode, this, "uncool").show();
            } else {
                Log.e(TAG, "This device is not supported.");

            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) throws Exception {
        final SharedPreferences prefs =
                getSharedPreferences(MapsActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }

        return registrationId;
    }

    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplication());
                    }
                    regId = gcm.register(getResources().getString(R.string.SenderID));
                    msg = "Device registered, registration ID: " + regId;

                    sendRegistrationId(regId);

                    storeRegistrationId(getApplication(), regId);
                    Log.i(TAG, msg);
                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.e(TAG, msg);
                }
                return msg;
            }
        }.execute(null, null, null);
    }
    private void sendRegistrationId(String regId) {
        pubnub.enablePushNotificationsOnChannel(
                "your channel name",
                regId);
    }
    private void storeRegistrationId(Context context, String regId) throws Exception {
        final SharedPreferences prefs =
                getSharedPreferences(MapsActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.apply();
    }
    public void sendNotification() {
        PnGcmMessage gcmMessage = new PnGcmMessage();
        JSONObject jso = new JSONObject();
        try {
            jso.put("GCMSays", "hi");
        } catch (JSONException e) { }
        gcmMessage.setData(jso);

        PnMessage message = new PnMessage(
                pubnub,
                "your channel name",
                callback,
                gcmMessage);
        try {
            message.publish();
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    private void unregister() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplication());
                    }

                    // Unregister from GCM
                    gcm.unregister();

                    // Remove Registration ID from memory
                    removeRegistrationId(getApplication());

                    // Disable Push Notification
                    pubnub.disablePushNotificationsOnChannel("your channel name", regId);

                } catch (Exception e) { }
                return null;
            }
        }.execute(null, null, null);
    }

    private void removeRegistrationId(Context context) throws Exception {
        final SharedPreferences prefs =
                getSharedPreferences(MapsActivity.class.getSimpleName(), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.apply();
    }
}