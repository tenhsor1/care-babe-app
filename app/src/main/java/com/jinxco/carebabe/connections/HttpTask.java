package com.jinxco.carebabe.connections;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;


import com.jinxco.carebabe.helpers.Globals;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Abstract class that need to be implemented to generate an AsyncTask to connect to the main server, and send some POST variables
 */
public abstract class HttpTask extends AsyncTask<ArrayList<NameValuePair>, Void, JSONObject> {
    protected boolean showDialog = false;
    protected String messageDialog = "";
    protected Activity activity;
    private ProgressDialog dialog;

    //call the parent constructor and create the config for the dialog
    public HttpTask(Activity act, boolean showDialog, String messageDialog){

        super();
        this.activity = act;
        dialog = new ProgressDialog(act);
        this.showDialog = showDialog;
        this.messageDialog = messageDialog;
    }
    //call the parent constructor and start a task without progressDialog
    public HttpTask(Activity act){
        super();
        this.activity = act;
        dialog = new ProgressDialog(act);
    }

    @Override
    //if showDialog is true, whe show the dialog before the execution of the asynctask
    protected void onPreExecute() {
        if(showDialog) {
            dialog.setMessage(messageDialog);
            dialog.show();
        }
    }

    //function that executes a petition to the REST Server with the indicated params
    @Override
    protected JSONObject doInBackground(ArrayList<NameValuePair>... params) {
        // we get the params passed with HttpTask.execute(Arraylist... params)
        ArrayList<NameValuePair> nvPairs = params[0];
        try {
            try {
                UrlEncodedFormEntity u = new UrlEncodedFormEntity(nvPairs);
                //we try to connect to the server especified at Globals.MAIN_URL and send it the params
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Globals.MAIN_URL);
                httppost.setEntity(u);
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                //get the responseCode and with it, we check that everything is correct, if not, we send a message
                Integer responseCode = response.getStatusLine().getStatusCode();
                switch (responseCode) {
                    case 200:
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            String responseBody = EntityUtils.toString(entity);
                            return new JSONObject(responseBody);
                        } else {
                            return new JSONObject("{'error':'empty_response','message':'The response from server is correct, but empty'}");
                        }
                    default:
                        return new JSONObject("{'error':'" + responseCode.toString() + "','message':'error on server, contact the admin'}");
                }
            } catch (UnsupportedEncodingException e) {
                return new JSONObject("{'error':'1','message':'1" + e.getMessage() + "'}");
            } catch (ClientProtocolException e) {
                return new JSONObject("{'error':'2','message':'2" + e.getMessage() + "'}");
            } catch (IOException e) {
                return new JSONObject("{'error':'3','message':'There was a problem with the connection to the server, check your internet connection'}");
            }
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    //if the dialog is shown, hide it, then execute the parsing of the response (need to be implemented by child classes)
    protected void onPostExecute(final JSONObject response) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        jsonParseResponse(response);
    }

    @Override
    //if the asynctask is cancelled, hide the dialog if shown
    protected void onCancelled() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        /*mAuthTask = null;
        showProgress(false);
        */
    }
    //custome function to parse the response from the server (need to be implemented on child classes)
    protected abstract void jsonParseResponse(JSONObject response);
}