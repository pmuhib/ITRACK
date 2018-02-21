package com.client.itrack.http;

import android.content.Context;
import android.os.AsyncTask;

import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utils;

import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by OWNER on 2015/7/11.
 */
public class HttpGetRequest extends AsyncTask<String, String, String> {
    HttpRequestCallback callback;
    String url;
    CacheType cacheType;
    Context activity;
    Timer timer;
    boolean responseDone;

    HttpGetRequest() {
        startTimeoutTimer();
    }

    public static void doGet(Context context, String url, HttpRequestCallback callback) {
        HttpGetRequest.doGet(context, url, CacheType.NO_CACHE, callback);
    }

    public static void doGet(Context context, String url, HttpRequestCallback callback, CacheType cacheType) {
        HttpGetRequest.doGet(context, url, cacheType, callback);
    }

    public static void doGet(Context context, String url, CacheType cacheType, HttpRequestCallback callback) {
        boolean isNetworkConnected = Utils.isNetworkConnected(context);
        if (isNetworkConnected) {
            HttpGetRequest req = new HttpGetRequest();
            req.url = url;
            req.activity = context;
            req.callback = callback;
            req.cacheType = cacheType;
            req.execute(url);
        }
    }

    private void startTimeoutTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cancelNow();
                Utils.printLog("Timer Cancelled.......");
            }
        }, Constants.HTTP_CONNECTION_TIMEOUT);
    }

    @Override
    protected String doInBackground(String[] params) {
        try {
            String url = params[0].toString();
            String response = HttpRequest.get(url).body();
            return response;
        } catch (Exception e) {
        }
        return "";
    }

    protected void cancelNow() {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                timer = null;
//                onPostExecute("");
//            }
//        });
    }

    @Override
    protected void onPostExecute(String data) {
        if (responseDone)
            return;
        responseDone = true;

        if (callback != null) {
            callback.response("Result", data);
            return;
        }

        cancelTimer();

    }

    private void cancelTimer() {
        if (timer != null)
            timer.cancel();
        else
            cancel(true);
        timer = null;
    }

}

