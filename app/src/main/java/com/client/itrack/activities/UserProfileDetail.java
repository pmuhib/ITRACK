package com.client.itrack.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.fragments.client.EditUserProfileDetailFragment;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.SharedPreferenceStore;
import com.squareup.picasso.Picasso;

/**
 * Created by Atul Kumar Gupta on 4/14/2016.
 */
public class UserProfileDetail extends AppCompatActivity {

    AppGlobal appGlobal = AppGlobal.getInstance();
    Toolbar toolBarUserProfile;
    ImageView btn_nav;

    TextView mTitle;
    String userId , username,phoneNo,phoneCode ,email,designation,type,profileImg,usernameId ;
    Context ctx;

    @Override
    protected void onResume() {
        super.onResume();
        bindDetailToGUI();
    }

    // private  static int MY_PERMISSIONS_REQUEST_READ_CONTACTS=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_detail);
        ctx=this;
        setUpToolbar();

    }


    private void bindDetailToGUI() {
        userId= SharedPreferenceStore.getValue(this,"Userid","");
        username = SharedPreferenceStore.getValue(this,"UserName","");
        phoneNo =  SharedPreferenceStore.getValue(this,"PhoneNo","");
        phoneCode = SharedPreferenceStore.getValue(this,"PhoneCode","0");
        email =  SharedPreferenceStore.getValue(this,"Email","");
        designation  =  SharedPreferenceStore.getValue(this,"Designation","") ;
        type =  SharedPreferenceStore.getValue(this,"Type","") ;
        profileImg =  SharedPreferenceStore.getValue(this, "img","");
        String loadingPointName  = SharedPreferenceStore.getValue(this, "LoadingPointName","");
        usernameId =  SharedPreferenceStore.getValue(this,"usernameId","");

        ((TextView)findViewById(R.id.companyNamee)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.companyhead)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.companyName)).setText(username.trim());
        ((TextView)findViewById(R.id.phoneno)).setText("+"+phoneCode+"-"+phoneNo);
        ((TextView)findViewById(R.id.clientemail)).setText(email);
        ((TextView)findViewById(R.id.clientfax)).setText(designation);
        ((TextView)findViewById(R.id.type)).setText(type);

       // String loadingPointId  = SharedPreferenceStore.getValue(this,"LoadingPoint","");

        //String usernameId  =  SharedPreferenceStore.getValue(this, "usernameId","");
        //String loadingCity =   SharedPreferenceStore.getValue(this, "city","");
        //String loadingCounrty  =  SharedPreferenceStore.getValue(this, "country","");
        TextView loadingPoint  =  (TextView)findViewById(R.id.loadingpoint);
        TextView lblAdminEmployeeLoadingPoint  =  (TextView) findViewById(R.id.lblAdminEmployeeLoadingPoint) ;
        View lastDividerLine =  findViewById(R.id.lastDividerLine);
        loadingPoint.setText(loadingPointName);
        switch (appGlobal.userType)
        {
            case "employee" :
                lblAdminEmployeeLoadingPoint.setVisibility(View.VISIBLE);
                lastDividerLine.setVisibility(View.VISIBLE);
                loadingPoint.setVisibility(View.VISIBLE);
                break ;

            default :
                lblAdminEmployeeLoadingPoint.setVisibility(View.INVISIBLE);
                lastDividerLine.setVisibility(View.GONE);
                loadingPoint.setVisibility(View.INVISIBLE);
                break ;

        }
        Picasso.with(getApplicationContext()).load(Constants.IMAGE_BASE_URL_ADMIN+profileImg).placeholder(R.drawable.circledefault).error(R.drawable.circledefault).into((ImageView) findViewById(R.id.companyLogo));
    }

    private void setUpToolbar() {

        toolBarUserProfile = (Toolbar) findViewById(R.id.toolbar);

        mTitle = (TextView) toolBarUserProfile.findViewById(R.id.txt_heading);
        mTitle.setText("Profile");


        btn_nav=(ImageView) toolBarUserProfile.findViewById(R.id.btn_navigation);
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        /******************************
         * Edit Profile Button
         * ****************************/

        ImageView ivEditUserProfile = (ImageView) toolBarUserProfile.findViewById(R.id.client_detail_edit) ;
        ivEditUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[]  names = username.split(" ");
                Intent intent = new Intent(getApplicationContext(), EditUserProfileDetailFragment.class);
                intent.putExtra("id",userId) ;
                intent.putExtra("uName",username) ;
                intent.putExtra("uNameId",usernameId) ;
                intent.putExtra("uPhone",phoneNo) ;
                intent.putExtra("uPhoneCode",phoneCode) ;
                intent.putExtra("uEmail",email) ;
                intent.putExtra("uDesignation",designation) ;
                intent.putExtra("uType",type) ;
                intent.putExtra("uImage",profileImg) ;
                startActivity(intent);
            }
        });
        ImageView clientMoreOption = (ImageView) toolBarUserProfile.findViewById(R.id.client_detail_more_option);
        clientMoreOption.setVisibility(View.GONE);
        setSupportActionBar(toolBarUserProfile);
    }
}
