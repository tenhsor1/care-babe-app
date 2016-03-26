package com.jinxco.carebabe.connections;

import android.app.Activity;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by tenhsor on 13/12/14.
 *class used to send the registration id for GCM to the adkins server and save the value on the database
*/
 public class UserRegisterTask extends HttpTask {
    public UserRegisterTask(Activity act) {
        super(act);
    }
    @Override
    protected void jsonParseResponse(JSONObject response) {
        Log.i("response_register", response.toString());
        //Toast.makeText(this.activity, response.toString(), Toast.LENGTH_LONG).show();
    }
}