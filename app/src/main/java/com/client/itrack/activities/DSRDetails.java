package com.client.itrack.activities;


import android.content.Intent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.fragments.dsr.AddDSRCargoDetailFragment;
import com.client.itrack.fragments.dsr.AddDSRGeneralDetailFragment;
import com.client.itrack.fragments.dsr.DSRCargoDetailClientFragment;
import com.client.itrack.fragments.dsr.DSRCargoDetailFragment;
import com.client.itrack.fragments.dsr.DSRGeneralDetailClientFragment;
import com.client.itrack.fragments.dsr.DSRGeneralDetailFragment;

import com.client.itrack.model.DSRCargoDetailModel;
import com.client.itrack.model.DSRGeneralDetailModel;
import com.client.itrack.model.TrailerTypeModel;
import com.client.itrack.model.TruckStatusModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;

import java.util.ArrayList;


public class DSRDetails extends AppCompatActivity {


    AppGlobal appGlobal = AppGlobal.getInstance();
    Toolbar toolbar;
    TextView mTitle;
    ImageView btn_nav;
    public String dsr_id ;
    private String userType ;
    String dsr_ref_num , compName;
    public String action ;
    TextView btn_general_dsr, btn_cargo_dsr;
    public boolean isValidationSuccess ;
    public String msg_fail_validation ;
    public ArrayList<TrailerTypeModel> listTrailerType;
    public ArrayList<TruckStatusModel> listTruckStatus;

    /** DSR Cargo/General Detail Model **/

    public DSRGeneralDetailModel dsrGeneralDetailModel  ;
    public DSRCargoDetailModel dsrCargoDetailModel  ;


    public int indexActivatedView = 0; // 0 => General , 1 => Cargo
    private String createEmpId,createEmpType;
    private ImageView clientDetailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsrdetails);
        Intent intent = getIntent();
        dsr_id = intent.getStringExtra("dsr_id");
        dsr_ref_num = intent.getStringExtra("dsr_ref_no");
        action = intent.getStringExtra("action");
        compName = intent.getStringExtra("client_comp_name");
        createEmpId=intent.getStringExtra("createEmpId");
        createEmpType = intent.getStringExtra("createEmpType");
        userType = appGlobal.userType ;
        setUpToolbar();

        btn_general_dsr = (TextView) findViewById(R.id.btn_general_dsr);
        btn_cargo_dsr = (TextView) findViewById(R.id.btn_cargo_dsr);

        indexActivatedView = 0;
        doAction();

        btn_general_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexActivatedView = 0;
                doAction();

            }
        });


        btn_cargo_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexActivatedView = 1;
                doAction();
            }
        });

    }

    // Choose action want to do  Edit , delete , add  , view
    public void doAction() {
        switch (action) {
            case "view":

                loadDetailFragment(dsr_id);
                break;
            case "add":

                mTitle.setText("Add New DSR");
                loadAddDetailFragment(dsr_id);
                break;

            case "edit":
                mTitle.setText("Edit DSR");
                loadEditDetailFragment(dsr_id);
                break;

            default:
                break;

        }
    }

    private void loadAddDetailFragment(String dsr_id) {

        switch (indexActivatedView) {
            case 0: {
                AddDSRGeneralDetailFragment registerFragments = new AddDSRGeneralDetailFragment();
                Bundle bundle = new Bundle();
                // Bundle All General Detail
                bundle.putCharSequence("dsr_id", dsr_id);
                bundle.putCharSequence("dsr_ref_no", dsr_ref_num);
                bundle.putCharSequence("action",action);
                registerFragments.setArguments(bundle);
                replaceFragment(R.id.dsr_detail_container, registerFragments);
                //Toast.makeText(DSRDetails.this, "Working on " + action + " DSR General Fragment", Toast.LENGTH_SHORT).show();
            }
            break;

            case 1: {
                if(isValidationSuccess) {
                    AddDSRCargoDetailFragment registerFragments = new AddDSRCargoDetailFragment();
                    if(dsrCargoDetailModel!=null)
                        registerFragments.listDSRTruckDetails = dsrCargoDetailModel.listDSRTruckDetails;
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("dsr_id", dsr_id);
                    bundle.putCharSequence("dsr_ref_no", dsr_ref_num);
                    bundle.putCharSequence("action", action);
                    registerFragments.setArguments(bundle);
                    replaceFragment(R.id.dsr_detail_container, registerFragments);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please submit General detail.", Toast.LENGTH_SHORT).show();
                }
            }
            break;

        }

    }

    private void loadEditDetailFragment(String dsr_id) {

        switch (indexActivatedView) {
            case 0: {
                AddDSRGeneralDetailFragment registerFragments = new AddDSRGeneralDetailFragment();
                Bundle bundle = new Bundle();
                // Bundle All General Detail
                bundle.putCharSequence("dsr_id", dsr_id);
                bundle.putCharSequence("dsr_ref_no", dsr_ref_num);
                bundle.putCharSequence("action",action);
                registerFragments.setArguments(bundle);
                replaceFragment(R.id.dsr_detail_container, registerFragments);
                //Toast.makeText(DSRDetails.this, "Working on " + action + " DSR General Fragment", Toast.LENGTH_SHORT).show();
            }
            break;

            case 1: {
                if(isValidationSuccess) {
                    AddDSRCargoDetailFragment registerFragments = new AddDSRCargoDetailFragment();
                    if(dsrCargoDetailModel!=null)
                        registerFragments.listDSRTruckDetails = dsrCargoDetailModel.listDSRTruckDetails;
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("dsr_id", dsr_id);
                    bundle.putCharSequence("dsr_ref_no", dsr_ref_num);
                    bundle.putCharSequence("action", action);
                    registerFragments.setArguments(bundle);
                    replaceFragment(R.id.dsr_detail_container, registerFragments);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please submit General detail.", Toast.LENGTH_SHORT).show();
                }
            }
            break;

        }

    }


    //  View DSR Detail Module for both General and Cargo
    private void loadDetailFragment(String dsr_id) {
        switch (indexActivatedView) {
            case 0: {
                mTitle.setText(dsr_ref_num);
                if(userType.equals(Constants.ADMIN_EMP_TYPE)|| createEmpType.equals(Constants.ADMIN_TYPE)) {
                    if(createEmpType.equals(Constants.ADMIN_EMP_TYPE) && createEmpId.equals(appGlobal.userId)){
                        clientDetailEdit.setVisibility(View.VISIBLE);
                    }
                    DSRGeneralDetailFragment registerFragments = new DSRGeneralDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("dsr_id", dsr_id);
                    bundle.putCharSequence("dsr_ref_no", dsr_ref_num);
                    registerFragments.setArguments(bundle);
                    replaceFragment(R.id.dsr_detail_container, registerFragments);
                }
                else
                {
                    DSRGeneralDetailClientFragment registerFragments = new DSRGeneralDetailClientFragment();
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("dsr_id", dsr_id);
                    bundle.putCharSequence("dsr_ref_no", dsr_ref_num);
                    registerFragments.setArguments(bundle);
                    replaceFragment(R.id.dsr_detail_container, registerFragments);
                }
            }
            break;

            case 1: {
                mTitle.setText(dsr_ref_num);
                isValidationSuccess = true ;
                if(userType.equals(Constants.ADMIN_EMP_TYPE)|| createEmpType.equals(Constants.ADMIN_TYPE)) {
                    if(createEmpType.equals(Constants.ADMIN_EMP_TYPE) && createEmpId.equals(appGlobal.userId))
                    {
                        clientDetailEdit.setVisibility(View.VISIBLE);
                    }
                    DSRCargoDetailFragment registerFragments = new DSRCargoDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("dsr_id", dsr_id);
                    bundle.putCharSequence("dsr_ref_no", dsr_ref_num);
                    registerFragments.setArguments(bundle);
                    replaceFragment(R.id.dsr_detail_container, registerFragments);
                }
                else
                {
                    DSRCargoDetailClientFragment registerFragments = new DSRCargoDetailClientFragment();
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("dsr_id", dsr_id);
                    bundle.putCharSequence("dsr_ref_no", dsr_ref_num);
                    registerFragments.setArguments(bundle);
                    replaceFragment(R.id.dsr_detail_container, registerFragments);
                }

            }
            break;
        }
    }

    /*********************************
     * Fragment Replacement On demand
     * ********************************/

    private void replaceFragment(int oldFragmentId, Fragment registerFragments) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(oldFragmentId, registerFragments);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    /**********************************
     *   Setup ToolBar For DSR Section
     * ********************************/

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.dsr_toolbar);
        mTitle = (TextView) (toolbar.findViewById(R.id.txt_heading));

        ImageView clientMoreOption = (ImageView) toolbar.findViewById(R.id.client_detail_more_option);
        clientMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view);
            }
        });
        clientMoreOption.setVisibility(View.GONE);

        clientDetailEdit = (ImageView) toolbar.findViewById(R.id.client_detail_edit);
        clientDetailEdit.setVisibility(View.GONE);
        clientDetailEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "edit" ;
                doAction();
            }
        });
        btn_nav = (ImageView) toolbar.findViewById(R.id.btn_navigation);
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
               // onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
    }

    /**********************************
     *   Show Menu bar for DSR Section
     * ********************************/

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.action_quotes:

                        break;

                    case R.id.action_dsr:
                        Intent intent = new Intent(getApplicationContext(), CategoryContainer.class);
                        intent.putExtra("catpos", 20);
                        startActivity(intent);
                        break;

                    case R.id.action_delete:
                        break;
                    default:
                        break;


                }
                return false;
            }
        });
        popup.inflate(R.menu.dsr_menu);
        popup.show();
    }
}
