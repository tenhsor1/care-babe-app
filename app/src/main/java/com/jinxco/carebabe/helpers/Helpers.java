package com.jinxco.carebabe.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tenhsor on 13/12/14.
 * General functions that help to acomplish certain tasks
 */
public class Helpers {
    public static void dialNumber(Activity act, String number){
        String uriNumber = "tel:" + number;
        Intent i = new Intent(Intent.ACTION_DIAL, null);
        i.setData(Uri.parse(uriNumber));
        act.startActivity(i);
    }

    public static void openLink(Activity act, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        act.startActivity(i);
    }

    //receives a unix_timestamp number and convert it in a date format
    public static String getDateFormat(String format, int unixDate){
        Date time= new Date((long)unixDate*1000);

        DateFormat df = new SimpleDateFormat(format);
        return df.format(time);
    }

    //receive a number of seconds and return a minute format 0:00
    public static String getMinutesFormat(int seconds){
        String mins, secs;

        int minutes = seconds / 60;
        mins = Integer.toString(minutes);


        int sec = seconds % 60;
        if(sec < 10){
            secs = "0" + Integer.toString(sec);
        }else{
            secs = Integer.toString(sec);
        }

        return mins + ':' + secs;
    }

    //send a broadcast
    public static void sendBroadCast(Context context, String action) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(action);
        context.sendBroadcast(broadcastIntent);
    }

    //send a broadcast with a jsonObject converted to string called -nameJSON-
    public static void sendBroadCast(Context context, String action, String nameJSON, JSONObject data) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(action);
        broadcastIntent.putExtra(nameJSON, data.toString());
        context.sendBroadcast(broadcastIntent);
    }


    public static String getPhoneNumberFormat (String number) {

        try{
            String formattedNumber = String.format("(%s) %s-%s",
                number.substring(1, 4),
                number.substring(4, 7),
                number.substring(7, 11));
            return formattedNumber;
        }catch (StringIndexOutOfBoundsException e){
            return number;
        }
    }

}
