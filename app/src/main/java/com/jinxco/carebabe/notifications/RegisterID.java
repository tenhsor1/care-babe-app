package com.jinxco.carebabe.notifications;

import android.app.Activity;
import android.os.AsyncTask;

import com.jinxco.carebabe.connections.UserRegisterTask;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jinxco.carebabe.helpers.Globals;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tenhsor on 22/09/14.
 * Class that register the device as a new listener for Google Cloud Messages
 */
public class RegisterID extends AsyncTask<String, Void, String> {
    private GoogleCloudMessaging gcm; //allows to connect to the gcmServer
    private Activity activity; //activity used for show messages
    private UserRegisterTask mRegisterTask; //used to send the register_id to the server
    String regId, token; //regId: register_id given by gcm. token: token used to connect to the rest Server
    public RegisterID(Activity act) {
        super();
        this.activity = act;
        mRegisterTask = null;
    }

    //we execute a petition for get a register_id for this device and this app
    @Override
    protected String doInBackground(String... params) {
        String msg;
        token = params[0];
        try {
            //if already exist a gcmHandler then we use it
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(activity);
            }
            // request a register_id
            regId = gcm.register(Globals.PROJECT_API_ID);
            msg = "Device registered, registration ID=" + regId;

        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();

        }
        return msg;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        //after receiving the register_id we send the register_id to the REST Server, so we can keep a record of that on the server
        mRegisterTask= new UserRegisterTask(this.activity);
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("register_id",regId));
        mRegisterTask.execute(params);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

}
