package com.client.itrack.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.client.itrack.R;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.SharedPreferenceStore;

public class SplashActivity extends Activity {

    Context ctx;
    AppGlobal appGlobal  = AppGlobal.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gotoLoginScreen();
    }

    private void gotoLoginScreen() {

        Handler handler= new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                /**
                 * *******************
                 * Check Login or Not
                 * *******************
                 **/

                String userid =  SharedPreferenceStore.getValue(getApplicationContext(), "Userid", "");
                String userCompId =  SharedPreferenceStore.getValue(getApplicationContext(), "client_comp_id", "");
                String loginType =  SharedPreferenceStore.getValue(getApplicationContext(), "Type","");
                SharedPreferenceStore.getValue(getApplicationContext(), "LoadingPoint", "");
                String country_code = SharedPreferenceStore.getValue(getApplicationContext(), "country_code","AE"); // May Put Here default
                String currency_code = SharedPreferenceStore.getValue(getApplicationContext(),"currency_code","AED"); // May put here Default
                appGlobal.userType = loginType ;
                appGlobal.userId = userid;
                appGlobal.userCompId = userCompId;
                appGlobal.currency_code =  currency_code ;
                appGlobal.country_code = country_code ;

                /*******************/
                if(userid.isEmpty()) {
                    Intent intent = new Intent(SplashActivity.this, HomeContainer.class); // LoginContainer.class
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), HomeContainer.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 2000);
    }
}
