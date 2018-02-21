package com.client.itrack.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.itrack.R;
import com.client.itrack.adapters.UsefulLinkAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.LinkModel;
import com.client.itrack.model.UsefulLinkModel;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DividerItemDecoration;
import com.client.itrack.utility.Utility;
import com.client.itrack.utility.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UsefulLinksActivity extends AppCompatActivity {

    ArrayList<UsefulLinkModel> listUsefulLinks ;

    UsefulLinkAdapter usefulLinkAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useful_links);

        final RecyclerView recyclerUsefulLinks =(RecyclerView) findViewById(R.id.recyclerUsefulLinks) ;
        usefulLinkAdapter = new UsefulLinkAdapter(listUsefulLinks,UsefulLinksActivity.this);
        recyclerUsefulLinks.setAdapter(usefulLinkAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestGetUsefulLinks() ;

        Toolbar ulToolbar = (Toolbar) findViewById(R.id.toolbarUL);
        final ImageView client_detail_edit  = (ImageView) ulToolbar.findViewById(R.id.client_detail_edit);
        client_detail_edit.setVisibility(View.GONE);
        final ImageView client_detail_more_option  = (ImageView) ulToolbar.findViewById(R.id.client_detail_more_option);
        client_detail_more_option.setVisibility(View.GONE);

        final ImageView btn_navigation  = (ImageView) ulToolbar.findViewById(R.id.btn_navigation);
        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final TextView txt_heading  = (TextView) ulToolbar.findViewById(R.id.txt_heading);
        txt_heading.setText("Useful Links");

    }

    private void requestGetUsefulLinks() {
        listUsefulLinks = new ArrayList<>();
        String url = Constants.BASE_URL+"get_usefull_link";
        Utils.showLoadingPopup(UsefulLinksActivity.this);
        HttpPostRequest.doPost(UsefulLinksActivity.this, url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();

                try {
                    JSONArray usefulLinksJArr  = new JSONArray(responseData);
                    for (int indexUsefulLinks = 0; indexUsefulLinks < usefulLinksJArr.length(); indexUsefulLinks++) {
                        JSONObject usefulLinkJObj = usefulLinksJArr.getJSONObject(indexUsefulLinks) ;
                        String category  = usefulLinkJObj.getString("title") ;

                        JSONArray jUrls = usefulLinkJObj.getJSONArray("url") ;
                        ArrayList<LinkModel> lurls  =  new ArrayList<>();
                        for (int indexJURL = 0; indexJURL < jUrls.length(); indexJURL++) {
                            JSONObject jObjREF  = jUrls.getJSONObject(indexJURL);
                            String url =   jObjREF.getString("url") ;
                            String title =   jObjREF.getString("name") ;
                            LinkModel linkModel = new LinkModel();
                            linkModel.title = title ;
                            linkModel.url = url ;
                            lurls.add(linkModel);
                        }

                        UsefulLinkModel  ulinkModel = new UsefulLinkModel();
                        ulinkModel.category = category ;
                        ulinkModel.urls = lurls ;
                        listUsefulLinks.add(ulinkModel);
                    }

                    usefulLinkAdapter = new UsefulLinkAdapter(listUsefulLinks ,UsefulLinksActivity.this);

                    final RecyclerView recyclerUsefulLinks =(RecyclerView) findViewById(R.id.recyclerUsefulLinks) ;
                    DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL_LIST);
                    recyclerUsefulLinks.setAdapter(usefulLinkAdapter);
                    recyclerUsefulLinks.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerUsefulLinks.setItemAnimator(new DefaultItemAnimator());
                    recyclerUsefulLinks.addItemDecoration(dividerItemDecoration);
                    Utility.sendReport(UsefulLinksActivity.this,"get_usefull_link","Success","",responseData);
                }
                catch (Exception e){
                    Utility.sendReport(UsefulLinksActivity.this,"get_usefull_link",e.getMessage(),"",responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Utils.hideLoadingPopup();
            }
        });

    }
}
