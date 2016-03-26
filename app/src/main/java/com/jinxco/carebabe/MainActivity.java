package com.jinxco.carebabe;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.jinxco.carebabe.connections.ButtonStateTask;
import com.jinxco.carebabe.handlers.OnClickActionSwitch;
import com.jinxco.carebabe.helpers.ConnectionDetector;
import com.jinxco.carebabe.helpers.Globals;
import com.jinxco.carebabe.helpers.Helpers;
import com.jinxco.carebabe.notifications.RegisterID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private RegisterID register;
    private int googleServicesActive;
    private IntentFilter filterNotification; //will filter the broadcast messages only for CAREBABE_NOTIFICATION_RECEIVED
    private TextView firstDate, secondDate, thirdDate, firstNotification, secondNotification, thirdNotification;
    private Switch soundSwitch, lightSwitch;
    private ConnectionDetector conn;
    private ButtonStateTask getStateTask;

    //Broadcast focused on reloading the notification box when a new push notification comes.
    private BroadcastReceiver receiverNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String dataString = intent.getStringExtra("data_notif");
            try {

                JSONObject data = new JSONObject(dataString);
                String firstNot = data.getString("message");
                int date1 = data.getInt("date");
                String firstD = Helpers.getDateFormat("h:mma d/M/y", date1);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                String secondD = "";
                int date2 = preferences.getInt("firstDate", 0);
                if(date2 > 0){
                    secondD = Helpers.getDateFormat("h:mma d/M/y", date2);
                }
                String secondNot = preferences.getString("firstNot", "");

                String thirdD = "";
                int date3 = preferences.getInt("secondDate", 0);
                if(preferences.getInt("secondDate", 0) > 0){
                    thirdD = Helpers.getDateFormat("h:mma d/M/y", date3);
                }
                String thirdNot = preferences.getString("secondNot", "");

                firstNotification.setText(firstNot);
                secondNotification.setText(secondNot);
                thirdNotification.setText(thirdNot);

                firstDate.setText(firstD);
                secondDate.setText(secondD);
                thirdDate.setText(thirdD);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("firstDate", date1);
                editor.putInt("secondDate", date2);
                editor.putInt("thirdDate", date3);

                editor.putString("firstNot", firstNot);
                editor.putString("secondNot", secondNot);
                editor.putString("thirdNot", thirdNot);

                editor.commit();

            }catch (JSONException e){
                Toast.makeText(MainActivity.this, "There was a problem receiving a notification", Toast.LENGTH_LONG).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conn = new ConnectionDetector(this);
        register = new RegisterID(this);
        getStateTask = new ButtonStateTask(this);

        googleServicesActive = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(googleServicesActive != ConnectionResult.SUCCESS){
            if(googleServicesActive == ConnectionResult.SERVICE_MISSING ||
                    googleServicesActive == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
                    googleServicesActive == ConnectionResult.SERVICE_DISABLED){
                GooglePlayServicesUtil.getErrorDialog(googleServicesActive, this, 1);
            }else{
                Toast.makeText(this, "It seems that you have a problem with your Google services, " +
                        "this app needs Google Services for notification management, please update.", Toast.LENGTH_LONG).show();
            }
        }

        firstNotification = (TextView) findViewById(R.id.firstNotificationView);
        secondNotification = (TextView) findViewById(R.id.secondNotificationView);
        thirdNotification = (TextView) findViewById(R.id.thirdNotificationView);

        firstDate = (TextView) findViewById(R.id.firstNotificationDateTextView);
        secondDate = (TextView) findViewById(R.id.secondNotificationDateTextView);
        thirdDate = (TextView) findViewById(R.id.thirdNotificationDateTextView);

        soundSwitch = (Switch) findViewById(R.id.soundSwitch);
        lightSwitch = (Switch) findViewById(R.id.lightSwitch);

        soundSwitch.setOnClickListener(new OnClickActionSwitch(this, Globals.ACTION_SOUND));
        lightSwitch.setOnClickListener(new OnClickActionSwitch(this, Globals.ACTION_LIGHT));

        if(conn.isOnline()) {
            register.execute("ABCD1234");
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("get_button_states", "1"));
            getStateTask.execute(params);
        }





    }



    @Override
    protected void onResume(){
        super.onResume();
        filterNotification = new IntentFilter();
        filterNotification.addAction(Globals.NOTIFICATION_BROADCAST);
        registerReceiver(receiverNotification, filterNotification);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String firstD = "";
        int date1 = preferences.getInt("firstDate", 0);
        if(date1 > 0){
            firstD = Helpers.getDateFormat("h:mma d/M/y", date1);
        }
        String firstNot = preferences.getString("firstNot", "");

        String secondD = "";
        int date2 = preferences.getInt("secondDate", 0);
        if(date2 > 0){
            secondD = Helpers.getDateFormat("h:mma d/M/y", date2);
        }
        String secondNot = preferences.getString("secondNot", "");

        String thirdD = "";
        int date3 = preferences.getInt("secondDate", 0);
        if(preferences.getInt("secondDate", 0) > 0){
            thirdD = Helpers.getDateFormat("h:mma d/M/y", date3);
        }
        String thirdNot = preferences.getString("secondNot", "");


        firstNotification.setText(firstNot);
        secondNotification.setText(secondNot);
        thirdNotification.setText(thirdNot);

        firstDate.setText(firstD);
        secondDate.setText(secondD);
        thirdDate.setText(thirdD);

        soundSwitch.setChecked(preferences.getBoolean("soundState", false));
        lightSwitch.setChecked(preferences.getBoolean("lightState", false));
    }

    //on pause it unregister the broadcast receiver
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverNotification);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("soundState", soundSwitch.isChecked());
        editor.putBoolean("lightState", lightSwitch.isChecked());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
