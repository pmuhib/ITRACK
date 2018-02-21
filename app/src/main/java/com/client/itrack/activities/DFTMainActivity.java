package com.client.itrack.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.client.itrack.R;
import com.client.itrack.fragments.dft.DFTOverviewFragment;
import com.client.itrack.utility.AppGlobal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DFTMainActivity extends AppCompatActivity {

    public String from_date ;
    public String to_date ;
    public  String currency_code  ;
    public boolean isCountrySelected = false  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dftmain);
        Calendar calendar = Calendar.getInstance() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateString  = dateFormat.format( calendar.getTime());
        from_date = dateString ;
        to_date = dateString ;
        currency_code = AppGlobal.getInstance().currency_code ;
        callDefaultView();
    }

    private void callDefaultView() {
        DFTOverviewFragment fragment = new DFTOverviewFragment();
        FragmentManager fm = getSupportFragmentManager() ;
        FragmentTransaction fragmentTransaction = fm.beginTransaction() ;
        fragmentTransaction.replace(R.id.dftContentContainer,fragment).commit();
    }

}
