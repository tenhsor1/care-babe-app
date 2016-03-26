package com.jinxco.carebabe.handlers;

import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.jinxco.carebabe.MainActivity;
import com.jinxco.carebabe.connections.ButtonStateTask;
import com.jinxco.carebabe.helpers.ConnectionDetector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by tenhsor on 13/11/14.
 * Class used for handle the toggle deleted button, (show deleted records on lists)
 */
public class OnClickActionSwitch implements View.OnClickListener {

    private MainActivity mainActivity;
    private int action;
    private ButtonStateTask task;
    private ConnectionDetector conn;
    public OnClickActionSwitch(MainActivity mainActivity, int action){
        this.mainActivity = mainActivity;
        this.action = action;
        this.conn = new ConnectionDetector(mainActivity);
    }

    @Override
    public void onClick(View view) {
        if(conn.isOnline()) {
            task = new ButtonStateTask(mainActivity);
            Switch s = (Switch) view;
            String state;
            if(s.isChecked()){
                state = "1";
            }else{
                state = "0";
            }

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("button_action", Integer.toString(action)));
            params.add(new BasicNameValuePair("state", state));
            task.execute(params);
        }else{
            Toast.makeText(mainActivity, "You don't have internet connection", Toast.LENGTH_LONG).show();
        }
    }
}