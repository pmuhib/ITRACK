package com.client.itrack.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.fragments.contact.ContactCallBackFragment;
import com.client.itrack.fragments.contact.ContactEmailFragment;
import com.client.itrack.fragments.contact.ContactMapFragment;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utils;

import org.json.JSONObject;

import java.util.HashMap;

public class ContactUs extends AppCompatActivity {

    AppGlobal appGlobal = AppGlobal.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        setupTopBar();
        setupGUI();
        setDefaultFragmentView();
    }

    private void setDefaultFragmentView() {
        Fragment fragment = null;
        Class fragmentClass  = ContactMapFragment.class ;
            try { fragment = (Fragment) fragmentClass.newInstance(); }
            catch (InstantiationException e) { e.printStackTrace();}
            catch (IllegalAccessException e) { e.printStackTrace();}
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    private void setupGUI() {
        TextView tvContactMap = (TextView)findViewById(R.id.tvContactMap);
        TextView tvContactCallBack = (TextView)findViewById(R.id.tvContactCallBack);
        TextView tvContactEmail = (TextView)findViewById(R.id.tvContactEmail);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = null;
                Class fragmentClass = null ;
                switch(view.getId())
                {
                    case R.id.tvContactMap :
                        fragmentClass = ContactMapFragment.class ;
                        break ;
                    case R.id.tvContactCallBack :
                        if(!appGlobal.userId.isEmpty()) sendCallBack() ;
                        else fragmentClass = ContactCallBackFragment.class;
                        break;

                    case R.id.tvContactEmail :
                        fragmentClass = ContactEmailFragment.class ;
                        break ;
                }
                if (fragmentClass!=null) {
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                } Utils.showLoadingPopup(ContactUs.this);
                String url = Constants.BASE_URL + "call_request";
                HashMap<String, String> data = new HashMap<>();
                data.put("user_id", appGlobal.userId);
                HttpPostRequest.doPost(ContactUs.this, url, Utils.newGson().toJson(data), new HttpRequestCallback() {
                    @Override
                    public void response(String errorMessage, String responseData) {
                        Utils.hideLoadingPopup();
                        try {
                            JSONObject jobj = new JSONObject(responseData);
                            Boolean status = jobj.getBoolean("status");
                            if (status) Toast.makeText(ContactUs.this, jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                            else Toast.makeText(ContactUs.this, jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
            }

            private void sendCallBack() {

//                AlertDialog.Builder builder = new AlertDialog.Builder(ContactUs.this);
//                builder.setMessage("CallBack Send by User");
//                builder.create();
//                builder.show() ;
            }
        };

        tvContactMap.setOnClickListener(listener);
        tvContactCallBack.setOnClickListener(listener);
        tvContactEmail.setOnClickListener(listener);

    }



    private void setupTopBar() {
        Toolbar  contactToolBar  =  (Toolbar)findViewById(R.id.toolbar_contact_us);
        TextView txt_heading  = (TextView) contactToolBar.findViewById(R.id.txt_heading);
        ImageView client_detail_edit  = (ImageView) contactToolBar.findViewById(R.id.client_detail_edit);
        ImageView client_detail_more_option  = (ImageView) contactToolBar.findViewById(R.id.client_detail_more_option);
        ImageView btn_navigation  = (ImageView) contactToolBar.findViewById(R.id.btn_navigation);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        };
        btn_navigation.setOnClickListener(listener);
        client_detail_edit.setVisibility(View.GONE);
        client_detail_more_option.setVisibility(View.GONE);
        txt_heading.setText("Contact Us");
    }
}
