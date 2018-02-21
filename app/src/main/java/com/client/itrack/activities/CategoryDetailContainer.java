package com.client.itrack.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.fragments.client.AddClientFragments;
import com.client.itrack.fragments.client.AddEmployeeFragments;
import com.client.itrack.model.QuoteDetailsModel;
import com.client.itrack.utility.DrawerConst;

/**
 * Created by  on 4/14/2016.
 * ***************************************************************
 * For Add And Edit Client And Employee Detail
 *
 * For Adding and editing DSR Details
 * **************************************************************
 *
 *
 */
public class CategoryDetailContainer extends AppCompatActivity {

    Toolbar toolbar;
    ImageView btn_nav;
    int catpos=0;
    TextView mTitle;
    private String companyid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorydetailcontainer);
        setUpToolbar();
        Intent intent=getIntent();
        catpos=intent.getIntExtra("catpos",0);

        switch(catpos)
        {
            case DrawerConst.ADD_CLIENT :{
                mTitle.setText(R.string.addnewclient);
                AddClientFragments registerFragments = new AddClientFragments();
                replaceFragment(R.id.categorydetailcontainer,registerFragments);
            }
                break ;

            case DrawerConst.ADD_EMPLOYEE : {

                mTitle.setText(R.string.addemployee);
                AddEmployeeFragments registerFragments = new AddEmployeeFragments();
                companyid = intent.getStringExtra("CompanyId");
                if (companyid == null) {
                    companyid = "0";
                } else if (companyid.length() == 0) {
                    companyid = "0";
                }

                String companyName = intent.getStringExtra("CompanyName");
                if (companyName == null) {
                    companyName = "";
                }
                String companyType = intent.getStringExtra("CompanyType");
                if (companyType == null) {
                    companyType = "";
                }
                Bundle args = new Bundle();
                args.putString("CompanyName", companyName);
                args.putString("CompanyEmail", "");
                args.putString("CompanyType", companyType);
                args.putInt("client_id", Integer.parseInt(companyid));
                args.putString("action","add");
                registerFragments.setArguments(args);
                replaceFragment(R.id.categorydetailcontainer, registerFragments);
            }
                break ;

            case 21 :
            mTitle.setText(R.string.addnewdsr);


            break ;

            case 22 :
                break ;

            case DrawerConst.ADD_QUOTE : {
                mTitle.setText(R.string.addnewquote);
                Intent intentQuote = new Intent(getApplicationContext(), QuoteDetails.class);
                intentQuote.putExtra("action", "add");
                Bundle bundle  = new Bundle();
                bundle.putSerializable("details",new QuoteDetailsModel());
                intentQuote.putExtra("bundle",bundle);
                startActivity(intentQuote);
                finish();
            }

                break ;

            case DrawerConst.EDIT_CLIENT : {
                mTitle.setText(R.string.editclient);
                AddClientFragments registerFragments = new AddClientFragments();
                Bundle bundle = new Bundle();
                bundle.putString("companyAddr1", intent.getStringExtra("companyAddr1"));
                bundle.putString("companyAddr2", intent.getStringExtra("companyAddr2"));
                bundle.putString("companyDName", intent.getStringExtra("companyDName"));
                bundle.putString("companyEmail", intent.getStringExtra("companyEmail"));
                bundle.putString("CompanyType", intent.getStringExtra("CompanyType"));
                bundle.putString("companyFax", intent.getStringExtra("companyFax"));
                bundle.putString("companyName", intent.getStringExtra("companyName"));
                bundle.putString("zipcode", intent.getStringExtra("zipcode"));
                bundle.putString("companyPhone", intent.getStringExtra("companyPhone"));
                bundle.putString("countryCodePhone", intent.getStringExtra("countryCodePhone"));
                bundle.putString("compCode", intent.getStringExtra("compCode"));
                bundle.putString("companyid", intent.getStringExtra("companyid"));
                bundle.putString("logo", intent.getStringExtra("logo"));
                bundle.putString("country", intent.getStringExtra("country"));
                bundle.putString("state", intent.getStringExtra("state"));
                bundle.putString("city", intent.getStringExtra("city"));
                registerFragments.setArguments(bundle);
                replaceFragment(R.id.categorydetailcontainer,registerFragments);

            }
            break;
            case DrawerConst.EDIT_EMPLOYEE : {
                mTitle.setText(R.string.editemployee);
                AddEmployeeFragments registerFragments = new AddEmployeeFragments();
                Bundle bundle = new Bundle();

                bundle.putString("employeeId", intent.getStringExtra("employeeId"));
                String clientId  =   intent.getStringExtra("client_id");
                if(clientId==null || clientId.isEmpty())
                {
                    clientId="0";
                }
                bundle.putString("CompanyType", intent.getStringExtra("CompanyType"));
                bundle.putInt("client_id",Integer.parseInt(clientId));
                bundle.putString("CompanyName", intent.getStringExtra("CompanyName"));
                bundle.putString("fName", intent.getStringExtra("fName"));
                bundle.putString("lName", intent.getStringExtra("lName"));
                bundle.putString("email", intent.getStringExtra("email"));
                bundle.putString("phoneNo", intent.getStringExtra("phoneNo"));
                bundle.putString("countryCodePhone", intent.getStringExtra("countryCodePhone"));
                bundle.putString("designation", intent.getStringExtra("designation"));
                bundle.putString("username", intent.getStringExtra("username"));
                bundle.putByteArray("img",intent.getByteArrayExtra("img"));
                bundle.putString("action","edit");

                registerFragments.setArguments(bundle);
                replaceFragment(R.id.categorydetailcontainer,registerFragments);

            }
                break;

        }
    }

    private void replaceFragment(int oldFragmentId, Fragment registerFragments)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(oldFragmentId, registerFragments);
        // fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
          mTitle = (TextView) ((Toolbar) findViewById(R.id.toolbar)).findViewById(R.id.txt_heading);

        btn_nav=(ImageView) toolbar.findViewById(R.id.btn_navigation);
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFragmentManager().popBackStack();
            }
        });
        setSupportActionBar(toolbar);
    }


}
