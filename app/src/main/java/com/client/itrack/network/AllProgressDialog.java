package com.client.itrack.network;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by NITISH on 3/28/2016.
 */
public class AllProgressDialog {

    static ProgressDialog pdialog;
    public static void showProgressDialog(Activity activity){

         pdialog= new ProgressDialog(activity);
        pdialog.setMessage("Please wait");
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();

    }

    public static void cancelProgressdialog(){
       // if(pdialog != null)
        pdialog.cancel();
    }
}
