package com.jinxco.carebabe.notifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jinxco.carebabe.MainActivity;
import com.jinxco.carebabe.R;
import com.jinxco.carebabe.helpers.Globals;
import com.jinxco.carebabe.helpers.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by tenhsor on 13/12/14.
 * class that handles messages received by GcmBroadcastReceiver
 */
public class GcmMessageHandler extends IntentService {

    /**
     * type: used for get the message type received from the server (its configured on the server to)
     * handler & mes: used for debugging
     */
    String type, mes;
    private Handler handler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    //functions executed when the broadcastreceiver sends the intent to this service
    @Override
    protected void onHandleIntent(Intent intent) {
        //receives all the parameters sent with the intent by the broadcastreceiver
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        //if the message has no error (its type MESSAGE_TYPE_MESSAGE) then we get the type of the message we received
        //and handle the messaged based on that
        if(messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)){

            handleNotification(extras);
        }
        Log.i("GCM", "Received : (" + messageType + ")  " + type);

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG).show();
            }
        });

    }

    //if the message received is a move type, then we send a broadcast message called CAREBABE_MOVE_RECEIVED
    private void handleNotification(Bundle extras){
        JSONObject data = new JSONObject();
        try {
            data.put("message", extras.getString("message"));
            int date = Integer.parseInt( extras.getString("date"));
            data.put("date", date);
            createNotification(extras.getString("message"));
        }catch (JSONException e){
            this.mes = "There was a problem with the message received from the server";
            showToast();
            e.printStackTrace();
        }

        Helpers.sendBroadCast(getBaseContext(), Globals.NOTIFICATION_BROADCAST, "data_notif", data );
    }

    private void createNotification(String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setSmallIcon(R.drawable.ic_stat_care_babe_icon);
        builder.setContentTitle("Baby Action!");
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setTicker("There is a new baby action!");
        builder.setWhen(System.currentTimeMillis());

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        builder.setLargeIcon(largeIcon);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(Globals.NOTIFICATION_ID, builder.build());

    }

}
