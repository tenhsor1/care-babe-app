package com.jinxco.carebabe.connections;

import android.app.Activity;
import android.util.Log;
import android.widget.Switch;

import com.jinxco.carebabe.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tenhsor on 13/12/14.
 */
public class ButtonStateTask extends HttpTask {
    private Switch soundSwitch, lightSwitch;
    public ButtonStateTask(Activity act) {
        super(act);
    }
    @Override
    protected void jsonParseResponse(JSONObject response) {
        Log.i("response_button_state", response.toString());
        soundSwitch = (Switch) activity.findViewById(R.id.soundSwitch);
        lightSwitch = (Switch) activity.findViewById(R.id.lightSwitch);

        try{
            if(response.has("light")){
                int state = response.getInt("light");
                lightSwitch.setChecked(state > 0);
            }

            if(response.has("sound")){
                int state = response.getInt("sound");
                soundSwitch.setChecked(state > 0);
            }
        }catch (JSONException e){

        }

        //Toast.makeText(this.activity, response.toString(), Toast.LENGTH_LONG).show();
    }
}
