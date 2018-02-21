package com.client.itrack.http;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utils;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by OWNER on 2015/7/11.
 */
public class HttpPostRequest extends AsyncTask<String, String, String> {
    HttpRequestCallback callback;
    String url;
    Context context;
    HashMap<String, String> hashMap;
    String data;

    public static void doPost(Activity context, String url, HttpRequestCallback callback) {
        HttpPostRequest.doPost(context, url, new HashMap<String, String>(), callback);
    }

    public static void doPost(Activity context, String url, HashMap<String, String> hashMap, HttpRequestCallback callback) {
        if (!Utils.isNetworkConnected(context, false)) {
            callback.onError("No Internet Connected!");
            return;
        }
        HttpPostRequest req = new HttpPostRequest();
        req.url = url;
        req.context = context;
        req.callback = callback;
        req.hashMap = hashMap;
        req.execute(url);
    }

    public static void doPost(Activity context, String url, String data, HttpRequestCallback callback) {
        if (!Utils.isNetworkConnected(context, false)) {
            callback.onError("No Internet Connected!");
            return;
        }
        Utils.printLog("DATA - " + data);
        HttpPostRequest req = new HttpPostRequest();
        req.url = url;
        req.context = context;
        req.callback = callback;
        req.data = data;
        req.execute(url);
    }

    public static void doPostOnRunningThread(Activity context, String url, HashMap<String, String> hashMap, HttpRequestCallback callback) {
        if (!Utils.isNetworkConnected(context, true)) {
            callback.onError("No Internet Connected!");
            return;
        }
        String responseData = HttpRequest.post(url).send(new JSONObject(hashMap).toString()).body();
        callback.response("", responseData);
    }

    @Override
    protected String doInBackground(String[] params) {
        //Log.v("Message By Atul",params[0].toString());
        String url = params[0].toString();
        if (data != null) {
            return HttpRequest.post(url).header(HttpRequest.HEADER_CONTENT_TYPE, HttpRequest.CONTENT_TYPE_JSON).send((data != null ? data : "")).body();
        } else if (hashMap != null)
            return HttpRequest.post(url).form(hashMap).body();
       // Log.v("Message By Atul",HttpRequest.post(url).body());
        return HttpRequest.post(url).body();
    }

    @Override
    protected void onPostExecute(String data) {
        Log.i(Constants.LOG_TAG, data);
        callback.response("Result", data);
    }
}

