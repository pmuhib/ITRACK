package com.client.itrack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;


public class NewsDetail extends AppCompatActivity {

    AppGlobal appGlobal = AppGlobal.getInstance();
    Toolbar toolbar;
    ImageView btn_nav;
    String newsid;
    TextView mTitle;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsdetail);
        setUpToolbar();
        setupBottomBar();
        Intent intent=getIntent();
        newsid =intent.getStringExtra("newsid");
        position =intent.getIntExtra("position",0);

        if(Utils.isNetworkConnected(this,false)) {
            showClientDetail(newsid);
        }
    }

    private void showClientDetail(String companyid) {


        Utils.showLoadingPopup(NewsDetail.this);

        String url = Constants.BASE_URL+"newslistbyid";

        final HashMap<String,String> hm= new HashMap<>();
        hm.put("news_id",companyid);

        HttpPostRequest.doPost(NewsDetail.this, url, Utils.newGson().toJson(hm), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("news_list");

                      for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobject = jsonArray.getJSONObject(j);

                          ((TextView)findViewById(R.id.mdate)).setText("Date : "+jobject.getString("date"));
                          // ((TextView)findViewById(R.id.postedby)).setText(jsonArray.getString("company_phone"));
                        //  ((TextView)findViewById(R.id.phoneno)).setText("NEWS 0"+(position+1));
                          ((TextView)findViewById(R.id.phoneno)).setText(jobject.getString("name").trim());
                          ((TextView)findViewById(R.id.desctext)).setText(Html.fromHtml(jobject.getString("des")));
                          Picasso.with(NewsDetail.this).load(Constants.NEWS_THUMBS_IMG_BASE_URL+jobject.getString("image")).placeholder(R.drawable.circledefault).error(R.drawable.circledefault).into(((ImageView) findViewById(R.id.imageicon)));

                      }
                    } else {
                        Toast.makeText(NewsDetail.this, jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Utility.sendReport(NewsDetail.this,"newslistbyid",e.getMessage(),Utils.newGson().toJson(hm),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }
    private void setupBottomBar() {


    }
    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolNewsBar);
          mTitle = (TextView) toolbar.findViewById(R.id.txt_heading);
        mTitle.setText("News Details");
        btn_nav=(ImageView) toolbar.findViewById(R.id.btn_navigation);
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               finish();
            }
        });
        ImageView client_detail_edit = (ImageView) toolbar.findViewById(R.id.client_detail_edit);
        ImageView client_detail_more_option = (ImageView) toolbar.findViewById(R.id.client_detail_more_option);
        client_detail_edit.setVisibility(View.GONE);
        client_detail_more_option.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
    }




}
