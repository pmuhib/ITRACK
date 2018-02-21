package com.client.itrack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.fragments.ForgotFragments;
import com.client.itrack.fragments.LoginFragments;
import com.client.itrack.fragments.SignUp.SelectSignUp;


public class LoginContainer extends AppCompatActivity{
    String goTo ;
    ImageView img_back;
    TextView head;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logincontainer);
       // img_back= (ImageView) findViewById(R.id.btn_back);
      //  head= (TextView) findViewById(R.id.txt_head);
        Intent  intent  =getIntent() ;
        goTo =  intent.getStringExtra("goto");
        if(goTo!=null) {
            switch (goTo) {
                case "forgot-pwd": {
                    FragmentManager fm = getSupportFragmentManager();
                    ForgotFragments forgotFragments = new ForgotFragments();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.framecontainer, forgotFragments);
                    // fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;

                case "sign-up":{
                  //  Toast.makeText(getApplication(),"Hi",Toast.LENGTH_LONG).show();
                    FragmentManager fm = getSupportFragmentManager();
                    SelectSignUp registerFragments=new SelectSignUp();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.framecontainer, registerFragments);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }

                break;
            }
        }
        else {
            addLoginFragments();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(goTo !=null)
        {
            finish();
        }
    }

    // Replace the login handler
    private void addLoginFragments() {

        FragmentManager fm = getSupportFragmentManager();
        //  Login Fragment
        LoginFragments loginFragments = new LoginFragments();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.framecontainer, loginFragments);
        // fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
