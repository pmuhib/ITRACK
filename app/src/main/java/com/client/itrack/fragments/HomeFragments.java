package com.client.itrack.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.activities.CategoryContainer;
import com.client.itrack.activities.DFTMainActivity;
import com.client.itrack.activities.SocialMediaActivity;
import com.client.itrack.activities.UsefulLinksActivity;
import com.client.itrack.R;
import com.client.itrack.activities.YTDActivity;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DrawerConst;
import com.client.itrack.utility.SharedPreferenceStore;
import com.client.itrack.utility.Utility;
import com.client.itrack.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeFragments extends Fragment {

    View view;
    LinearLayout quoteLayout ;
    LinearLayout dftLayout ;
    LinearLayout usefulLinksLayout ;
    LinearLayout socialMediaLayout ;
    LinearLayout dsrLayout ;
    LinearLayout toolsLayout ;
    LinearLayout businessForumLayout;
    LinearLayout layoutLatestNews;
    ImageView ytd;
    AppGlobal appGlobal =  AppGlobal.getInstance();
    private String userType ,userId ,userCompId ,ytdpermiss; // UserId --> Logged in user (Client Employee Id) & UserCompId --> Logged in User company Id

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view  =  inflater.inflate(R.layout.home, container, false);
        userType = appGlobal.userType ;
        userId = appGlobal.userId ;
        userCompId = appGlobal.userCompId ;
        ytdpermiss= SharedPreferenceStore.getValue(getActivity(),"ytdpres","");
        ytd= (ImageView) view.findViewById(R.id.img_ytdimg);
        setupView();
        return view;
    }

    private void setupView() {
        // Dashboard Options Icon
        quoteLayout=(LinearLayout)view.findViewById(R.id.quotelayout);
        dftLayout=(LinearLayout)view.findViewById(R.id.dftLayout);
        usefulLinksLayout=(LinearLayout) view.findViewById(R.id.usefulLinksLayout);
        socialMediaLayout=(LinearLayout) view.findViewById(R.id.socialMediaLayout);
        if(userType.equals("employee")){
          ytd.setImageDrawable(getResources().getDrawable(R.drawable.ytd));
        }
        dsrLayout = (LinearLayout) view.findViewById(R.id.dsrlayout);
        toolsLayout = (LinearLayout) view.findViewById(R.id.toolslayout);
        businessForumLayout = (LinearLayout) view.findViewById(R.id.businessForumLayout);

        layoutLatestNews = (LinearLayout) view.findViewById(R.id.layoutLatestNews);

        //Client Icon
        usefulLinksLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), CategoryContainer.class);
//                intent.putExtra("catpos", DrawerConst.CLIENTS_LIST);
//                startActivity(intent);
                //showComingSoonPopup();
                
                Intent intent = new Intent(getActivity(), UsefulLinksActivity.class);
                startActivity(intent);
            }
        });


        //DSR Icon
        dsrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appGlobal.checkIsUserAuthenticated(getActivity())){
                    Intent intent = new Intent(getActivity(), CategoryContainer.class);
                    intent.putExtra("catpos", DrawerConst.DSR_LIST);
                    if(!userType.equals("employee")){
                        intent.putExtra("CompanyId", userCompId);
                        intent.putExtra("CompanyName","");
                        intent.putExtra("CompanyLogo","");
                    }
                    startActivity(intent);
                }
            }
        });
        // Quote Icon
        quoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryContainer.class);
                intent.putExtra("catpos", DrawerConst.QUOTE_LIST);
                if(!userType.equals("employee")){
                    intent.putExtra("CompanyId", userCompId);
                }
                startActivity(intent);

            }
        });

        //Message Icon // Current 'Daily Financial Transactions' Icon
        dftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DFTMainActivity.class);
                startActivity(intent);

            }
        });

        // Tools Icon
        toolsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComingSoonPopup();
            }
        });

        // Profile Icon
        socialMediaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(appGlobal.checkIsUserAuthenticated(getActivity())) {
//                   /* Intent intent = new Intent(getActivity(), UserProfileDetail.class);
//                    startActivity(intent);*/
//                }
               // showComingSoonPopup();
               /* Intent intent = new Intent(getActivity(), SocialMediaActivity.class);
                startActivity(intent);*/
               if(userType.equals("employee")) {
                   if (ytdpermiss.equals("1")) {
                       startActivity(new Intent(getActivity(), YTDActivity.class));
                   }
                   else
                   {
                       Toast.makeText(getActivity(),"You don't have a permission",Toast.LENGTH_LONG).show();
                   }
               }
               else
               {
                   Intent intent = new Intent(getActivity(), SocialMediaActivity.class);
                   startActivity(intent);
               }

            }
        });

        businessForumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    startActivity(new Intent(getActivity(),YTDActivity.class));
//                Intent intent = new Intent(getActivity(), CategoryContainer.class);
//                intent.putExtra("catpos", DrawerConst.NEWS_LIST);
//                startActivity(intent);
                showComingSoonPopup();
            }
        });

        setupGUISetting();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void showComingSoonPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View viewPop =  LayoutInflater.from(getActivity()).inflate(R.layout.coming_soon,null) ;
        builder.setView(viewPop);
        builder.create();
        builder.show() ;
    }

    private void setupGUISetting() {
        toolsLayout.setVisibility(View.VISIBLE);
        dsrLayout.setVisibility(View.VISIBLE);
        quoteLayout.setVisibility(View.VISIBLE);
        businessForumLayout.setVisibility(View.VISIBLE);
        socialMediaLayout.setVisibility(View.VISIBLE);
        switch(userType)
        {

            case Constants.ADMIN_EMP_TYPE :
                dftLayout.setVisibility(View.VISIBLE);
                usefulLinksLayout.setVisibility(View.GONE);
                break ;
            default:
                dftLayout.setVisibility(View.GONE);
                usefulLinksLayout.setVisibility(View.VISIBLE);
                break ;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Utils.isNetworkConnected(getActivity(),false)) loadLatestNews();
    }


    private void loadLatestNews() {
        String url = Constants.BASE_URL+"newslist";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    StringBuilder newsCollection  = new StringBuilder();
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("news_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            //newsId  = jobject.getString("news_id");
                            String newsTitle = jobject.getString("name");
                            // String newsDesc  = jobject.getString("des");
                            newsCollection.append("* "+newsTitle+"   ");
                        }
                        final TextView tvNewsAll = (TextView)layoutLatestNews.findViewById(R.id.tvNewsAll);
                        tvNewsAll.setText(newsCollection.toString()) ;
                        tvNewsAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(),CategoryContainer.class);
                                intent.putExtra("catpos", DrawerConst.NEWS_LIST);
                                startActivity(intent);
                            }
                        });

                    }
                    Utility.sendReport(getActivity(),"newslist","Success","",responseData);
                } catch (Exception e) {
                    Utility.sendReport(getActivity(),"newslist",e.getMessage(),"",responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Utility.sendReport(getActivity(),"newslist","Error","",errorMessage);
            }
        });
    }
}
