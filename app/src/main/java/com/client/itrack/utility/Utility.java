package com.client.itrack.utility;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.client.itrack.activities.CategoryContainer;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by NITISH on 3/28/2016.
 */
public class Utility {

    private static boolean isTesting = false ;

    public static boolean isConnectingToInternet(Context ctx){
        ConnectivityManager connectivity = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static void sendReport(final Context context,final String moduleName , final String error, final String input,final String output)
    {
        isTesting =  true  ;
        if(isTesting){
//            AlertDialog.Builder builder  = new AlertDialog.Builder(context) ;
//            builder.setMessage(error);
//            builder.setPositiveButton("Send Report", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    String[] to  ={"atul@askonlinesolutions.com"};
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.setData(Uri.parse("mailto:"));
//                    intent.setType("*/*");
//                    intent.putExtra(Intent.EXTRA_EMAIL, to);
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "Error Report");
//                    intent.putExtra(Intent.EXTRA_TEXT,"Method : \n\n\n"+moduleName +
//                            "\n\n\nRequest :\n\n\n"+input+
//                            " \n\n\nResponse :\n\n\n"+output+
//                            " \n\n\nError :\n\n\n"+error) ;
//                    if (intent.resolveActivity(context.getPackageManager()) != null) {
//                        context.startActivity(Intent.createChooser(intent, "Send mail"));
//                    }
//                }
//            });
//            builder.create() ;
//            builder.show() ;
            //    sendLog(context,moduleName,error,input,output);
        }

    }

    private static void sendLog(final Context context,final String moduleName ,final String error, final String input,final String output)
    {
        if(context==null) return  ;
        isTesting =  true  ;
        if(isTesting){
            String message  = "<b>Context Name : </b><br/>"+(context.getClass().getName())+
                    " <br/<br/><b>Method : </b><br/>"+moduleName +
                    " <br/<br/><b>Request : </b><br/>"+input+
                    " <br/<br/><b>Response : </b><br/>"+output+
                    " <br/<br/><b>Status : </b><br/>"+error ;
            String url = Constants.BASE_URL + "track";
            HashMap<String, String> hm = new HashMap<>();
            hm.put("to", "atul@askonlinesolutions.com");
            hm.put("message",message);
            hm.put("subject","Log Report -"+ Calendar.getInstance().getTime().toString());

            HttpPostRequest.doPost((Activity) context, url, Utils.newGson().toJson(hm), new HttpRequestCallback() {
                @Override
                public void response(String errorMessage, String responseData) {
                    Log.d("I-Track_log","Success") ;
                }
                @Override
                public void onError(String errorMessage) {
                    Log.d("I-Track_log","Fail "+errorMessage) ;
                }
            });
        }

    }

}
