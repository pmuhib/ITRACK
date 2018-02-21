package com.client.itrack.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utility;
import com.client.itrack.utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

public class CompanyProfileDetail extends AppCompatActivity {
    AppGlobal appGlobal = AppGlobal.getInstance();
    TextView mTitle ;
    String userCompId ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientdetail);
        setUpToolbar();
        TextView btn_viewemployee = (TextView) this.findViewById(R.id.btn_viewemployee);
        btn_viewemployee.setVisibility(View.INVISIBLE);
        userCompId = appGlobal.userCompId ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!userCompId.isEmpty())
            showClientDetail(userCompId);
    }

    private void showClientDetail(String companyid) {

        Utils.showLoadingPopup(this);
        String url = Constants.BASE_URL + "clientview";
        final HashMap<String, String> hm = new HashMap<>();
        hm.put("client_id", companyid);

        HttpPostRequest.doPost(CompanyProfileDetail.this, url, Utils.newGson().toJson(hm), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONObject jsonArray = jobj.getJSONObject("client_details");
                        String companyName = jsonArray.getString("company_name");
                        String  companyAddr1 = jsonArray.getString("company_add1");
                        String zipcode = jsonArray.getString("zipcode");
                        String  companyPhone = jsonArray.getString("company_phone");
                        String companyEmail = jsonArray.getString("company_email");
                        String companyFax = jsonArray.getString("company_fax");
                        String companyDName = jsonArray.getString("company_domain_name");
                        String countryCodePhone = jsonArray.getString("code_phone_no");
                        // String compCode = jsonArray.getString("comp_code");
                        mTitle.setText(companyName.trim()); // Company Name
                        String  companyLogo = Constants.IMAGE_BASE_URL + jsonArray.getString("logo_thumb"); // Company Logo
                        //Load Company Logo
                        Picasso.with(CompanyProfileDetail.this).load(companyLogo).placeholder(R.drawable.circledefault).error(R.drawable.circledefault).into(((ImageView) findViewById(R.id.companyLogo)));
                        //Initialize  Company Details
                        ((TextView) findViewById(R.id.companyName)).setText(companyName);
                        ((TextView) findViewById(R.id.phoneno)).setText("+"+countryCodePhone+"-"+companyPhone);
                        ((TextView) findViewById(R.id.clientemail)).setText(companyEmail);
                        ((TextView) findViewById(R.id.clientfax)).setText(companyFax);
                        ((TextView) findViewById(R.id.companywebsite)).setText(companyDName);
                        ((TextView) findViewById(R.id.companyaddress)).setText(companyAddr1+"\n"+ zipcode);
                    } else {
                        Toast.makeText(CompanyProfileDetail.this, jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Utility.sendReport(CompanyProfileDetail.this,"clientview",e.getMessage(),Utils.newGson().toJson(hm),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.txt_heading);
        mTitle.setText("Company Profile");
        ImageView  btn_nav = (ImageView) toolbar.findViewById(R.id.btn_navigation);
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // More option Button
        ImageView clientMoreOption = (ImageView) toolbar.findViewById(R.id.client_detail_more_option);
        // Edit Client Details
        ImageView clientDetailEdit = (ImageView) toolbar.findViewById(R.id.client_detail_edit);


        // Top action Tool bar Settings
        clientDetailEdit.setVisibility(View.GONE);
        clientMoreOption.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
    }
}
