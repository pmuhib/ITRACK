package com.client.itrack.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.fragments.quotes.AddQuoteServicesDetailFragment;
import com.client.itrack.fragments.quotes.QuoteFullDetailClientFragment;
import com.client.itrack.fragments.quotes.QuoteServicesDetailFragment;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;

public class QuoteDetails extends AppCompatActivity {

    String action ;
    Bundle params ;
    AppGlobal appGlobal = AppGlobal.getInstance() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_details);
        Intent intent = getIntent() ;
        action = intent.getStringExtra("action");
        params = intent.getBundleExtra("bundle");
        doAction();
        setUpTopBar();

    }

    private void setUpTopBar() {

        Toolbar quote_tool_bar =  (Toolbar) findViewById(R.id.quote_tool_bar);
        TextView language = (TextView)quote_tool_bar.findViewById(R.id.navigationdot) ;
        ImageView btn_navigation = (ImageView)quote_tool_bar.findViewById(R.id.btn_navigation) ;
        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().popBackStack();
            }
        });

    }

    public void doAction() {
        switch (action) {
            case "view":
                loadQuoteDetailFragment();
                break;
            case "add":
                //mTitle.setText("Add New DSR");
                loadAddOrEditQuoteDetailFragment();
                break;

            case "edit":
                //mTitle.setText("Edit DSR");
                loadAddOrEditQuoteDetailFragment();
                break;

            default:
                break;
        }
    }

    private void loadAddOrEditQuoteDetailFragment() {
        AddQuoteServicesDetailFragment addDetailFragment = new AddQuoteServicesDetailFragment();
        params.putCharSequence("action",action);
        addDetailFragment.setArguments(params);
        replaceFragment(R.id.quote_detail_container,addDetailFragment);
    }

    private void loadQuoteDetailFragment()
    {
        switch (appGlobal.userType)
        {
            case Constants.ADMIN_EMP_TYPE :
                QuoteServicesDetailFragment detailFragment = new QuoteServicesDetailFragment();
                detailFragment.setArguments(params);
                replaceFragment(R.id.quote_detail_container,detailFragment);
                break ;

            case Constants.CLIENT_EMP_TYPE :
                QuoteFullDetailClientFragment detailClientFragment = new QuoteFullDetailClientFragment();
                detailClientFragment.setArguments(params);
                replaceFragment(R.id.quote_detail_container,detailClientFragment);
                break  ;
        }
    }

    /*********************************
     * Fragment Replacement On demand
     * ********************************/

    private void replaceFragment(int oldFragmentId, Fragment registerFragments) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(oldFragmentId, registerFragments);
      //   fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
