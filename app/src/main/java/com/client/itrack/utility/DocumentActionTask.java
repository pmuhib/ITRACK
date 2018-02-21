package com.client.itrack.utility;


import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 5/28/2016.
 */

public class DocumentActionTask extends AsyncTask<String,Void,byte[]>{

    byte[] fileBytes = null ;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected byte[] doInBackground(String... params) {
        if(fileBytes==null)
        {
            try {
                URL  url = new URL(params[0]) ;
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return fileBytes;
    }
}
