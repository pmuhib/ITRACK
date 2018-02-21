package com.client.itrack.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.adapters.ExpandableListAdapter;
import com.client.itrack.fragments.HomeFragments;
import com.client.itrack.model.DrawerModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DrawerConst;
import com.client.itrack.utility.SharedPreferenceStore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeContainer extends AppCompatActivity {

    AppGlobal  appGlobal = AppGlobal.getInstance();
    String userType,userId ;
    Toolbar toolbar;
    ImageView btn_nav;
    DrawerLayout mDrawer;
    ArrayList<DrawerModel> parentDrawarData;
    private Map<String, List<String>> childDrawarData;
    ExpandableListView expdrawarListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homecontainer);
        userType = appGlobal.userType ;
        userId = appGlobal.userId ;
        setUpToolbar();
        addHomeScreenFragments();
        setUpDrawer();
        prepareParentData();
        prepareChildData();
        setupGUI();


        appGlobal.checkIsUserAuthenticated(HomeContainer.this);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    private void setupGUI() {
        //    setUpRecyclerView();
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawer.isDrawerOpen(Gravity.LEFT)) {
                    mDrawer.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawer.openDrawer(Gravity.LEFT);
                }
            }
        });

        expdrawarListView = (ExpandableListView) findViewById(R.id.drawar_list);
        ExpandableListAdapter eadapter = new ExpandableListAdapter(this, parentDrawarData, childDrawarData);

        expdrawarListView.setAdapter(eadapter);
        eadapter.notifyDataSetChanged();

        // Group Click Events
        expdrawarListView.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                switch(userType){
                    case Constants.ADMIN_EMP_TYPE :setupEmployeeDrawerParentElAction(groupPosition);break;
                    default :setupClientDrawerParentElAction(groupPosition);break ;
                }
                return false;
            }
        });

        expdrawarListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                int absPosition = groupPosition*10+childPosition;
                switch(userType){
                    case Constants.ADMIN_EMP_TYPE :setupEmployeeDrawerChildElAction(absPosition);break ;
                    default:setupClientDrawerChildElAction(absPosition);break;
                }
                return false;
            }
        });

    }

    private void setupClientDrawerChildElAction(int absPosition) {

        switch(absPosition){
            case 20 :
                //  View Quotes
                Intent intentQuote = new Intent(getApplicationContext(), CategoryContainer.class);
                intentQuote.putExtra("catpos", DrawerConst.QUOTE_LIST);
                intentQuote.putExtra("CompanyId",appGlobal.userCompId); /*Logged In User Company Id */
                startActivity(intentQuote);
                break ;
            case 21 :
                // Add New Quote
                startViewAtAbsPosition(DrawerConst.ADD_QUOTE, CategoryDetailContainer.class);
                //startViewAtAbsPosition(absPosition);
                break ;

            case 31 :
                //startViewAtAbsPosition(absPosition);
                break ;

            default:
                break;
        }

    }

    private void setupEmployeeDrawerChildElAction(int absPosition) {
        switch(absPosition){
            case 10 :
                // View Clients
                startViewAtAbsPosition(absPosition, CategoryContainer.class);
                break ;
            case 11 :
                // Add New Client
                startViewAtAbsPosition(absPosition, CategoryDetailContainer.class);
                break ;
            case 12 :
                // Add New Employee
                startViewAtAbsPosition(absPosition,CategoryDetailContainer.class);
                break ;
            case 20 :
                // View DSR List
                startViewAtAbsPosition(absPosition, CategoryContainer.class);
                break ;
            case 21 :
                // Add New DSR
                //startViewAtAbsPosition(absPosition);
                break ;
            case 22 :
                //startViewAtAbsPosition(absPosition);
                break ;
            case 30 :
                // View Quote List
                startViewAtAbsPosition(absPosition,  CategoryContainer.class);
            default:
                break;
        }
    }

    private void setupClientDrawerParentElAction(int groupPosition) {
        switch (groupPosition) {
            case 0: {
                /** DASHBOARD **/
                Intent intent = new Intent(getApplicationContext(), HomeContainer.class);
                startActivity(intent);
            }
            break;
            case 1: {
                /** DSR **/
                if(appGlobal.checkIsUserAuthenticated(this)) {
                    Intent intent = new Intent(getApplicationContext(), CategoryContainer.class);
                    intent.putExtra("catpos", DrawerConst.DSR_LIST);
                    intent.putExtra("CompanyId", appGlobal.userCompId);
                    intent.putExtra("CompanyName", "");
                    intent.putExtra("CompanyLogo", "");
                    startActivity(intent);
                }
            }
            break;
            case 2: break;
            // case 3:startViewAtAbsPosition(groupPosition , CategoryContainer.class);  break;
            case 3:break; /** Tools **/
            case 4: if(!userId.isEmpty()) startViewAtAbsPosition(groupPosition , CategoryContainer.class);   /** Messages **/
            else startViewAtAbsPosition(groupPosition+1 , CategoryContainer.class);   /** News **/
                break;
            case 5:  if(!userId.isEmpty()) startViewAtAbsPosition(groupPosition , CategoryContainer.class);   /** News **/
            else {
                //startViewAtAbsPosition(7 , CategoryContainer.class)/** Contact **/;
                Intent intentContact =  new Intent(this,ContactUs.class);
                startActivity(intentContact);
            }
                break;
            case 6:
                if(appGlobal.checkIsUserAuthenticated(this)) {
                    Intent intentContact = new Intent(this, ContactUs.class);
                    startActivity(intentContact);
                }
                break;
            case 7:
                /** USER Profilea **/
                if(appGlobal.checkIsUserAuthenticated(this)) {
                    Intent intent = new Intent(getApplicationContext(), UserProfileDetail.class);
                    startActivity(intent);
                }
                break;
            case 8:
                /** Company Profile **/ // Only Authenticated Client User Can View Profile
                if(appGlobal.checkIsUserAuthenticated(this)) {
                    Intent intent = new Intent(getApplicationContext(), CompanyProfileDetail.class);
                    startActivity(intent);
                }
                break;
            default:logoutAppSession();break; // logOut Session
        }
    }

    private void setupEmployeeDrawerParentElAction(int groupPosition) {
        switch (groupPosition) {
            case 0: {
                Intent intent = new Intent(getApplicationContext(), HomeContainer.class);
                startActivity(intent);
            }
            break;
            case 1:break;
            case 2:break;
            case 3:break;
            case 4:break;
            case 5: startViewAtAbsPosition(DrawerConst.MSG_LIST,CategoryContainer.class);break; // Messages
            case 6: startViewAtAbsPosition(DrawerConst.NEWS_LIST,CategoryContainer.class);break; // News
//            case 7: Intent intentContact = new Intent(this,ContactUs.class); // Contact Us
//                startActivity(intentContact);break;
            case 7: {
                Intent intent = new Intent(getApplicationContext(), UserProfileDetail.class);
                startActivity(intent);
            }
            break;
            default:logoutAppSession();break;
        }
    }

    private void  startViewAtAbsPosition(int absPosition, Class<?> classObj){
        Intent intent = new Intent(getApplicationContext(), classObj);
        intent.putExtra("catpos", absPosition);
        startActivity(intent);
    }

    private void addHomeScreenFragments() {

        FragmentManager fm = getFragmentManager();
        HomeFragments homeFragments = new HomeFragments();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.homecontainer, homeFragments);
        // fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //   mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        btn_nav = (ImageView) toolbar.findViewById(R.id.btn_navigation);
        setSupportActionBar(toolbar);
    }

    public void setUpDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

    }

    private void prepareParentData() {
        String[] titleCard  ;
        int[] drawerCard ;
        int numOfParent;
        if(userType==null) {
            return ;
        }
        switch (userType) {
            case "employee":
                titleCard = getResources().getStringArray(R.array.titleDrawer);
                drawerCard = new int[]{R.drawable.home_drawaer, R.drawable.clients_drawar, R.drawable.dsr_drawar, R.drawable.quotes_drawar, R.drawable.tools_drawar, R.drawable.message_drawar,
                        R.drawable.news_drawar, R.drawable.my_profile_drawar, R.drawable.logout_drawer};
                break;
            default:
                titleCard = getResources().getStringArray(R.array.titleDrawerClient);
                drawerCard = new int[]{R.drawable.home_drawaer, R.drawable.dsr_drawar, R.drawable.quotes_drawar, R.drawable.tools_drawar, R.drawable.message_drawar,
                        R.drawable.news_drawar, R.drawable.contact_drawar,R.drawable.logout_drawer, R.drawable.my_profile_drawar, R.drawable.my_profile_drawar, R.drawable.logout_drawer};

                break;
        }

        parentDrawarData = new ArrayList<>();
        // Check User Is guest User or Client
        if(userId.isEmpty())
        {
            numOfParent  = titleCard.length-3;
        }
        else
        {
            numOfParent  = titleCard.length ;
        }
        for (int i = 0; i < numOfParent ; i++) {
            DrawerModel drawerModel = new DrawerModel();
            drawerModel.itemTitle = titleCard[i];
            drawerModel.drawerCardIcon = drawerCard[i];
            if(userId.isEmpty())
            {
                if(i!=4 )
                    parentDrawarData.add(drawerModel);
            }
            else
            {
                if( i!=7 || userType.equals("employee"))
                    parentDrawarData.add(drawerModel);

            }
        }
    }

    private void prepareChildData() {

        childDrawarData = new HashMap<>();

        for (int indexP = 0; indexP < parentDrawarData.size(); indexP++) {
            List<String> clientData = new ArrayList<String>();

            switch(userType)
            {
                case "employee" :
                    switch (indexP) {

                        case 1:
                            clientData.add("View");
                            clientData.add("Add New Company");
                            clientData.add("Add New Employee");
                            break;
                        case 2:
                            clientData.add("View DSR");
                            //clientData.add("Add New DSR");
                            break;
                        case 3:
                            clientData.add("View Quotes");
                            break;

                        default:
                            break;

                    }
                    break ;

                default :
                    switch (indexP) {
                        case 2:
                            clientData.add("View Quotes");
                            clientData.add("Add New Quote");
                            break;
                        default:
                            break;

                    }
                    break ;
            }
            childDrawarData.put(parentDrawarData.get(indexP).getItemTitle(), clientData);
        }
    }

    private void logoutAppSession() {
        /** Delete From SharedPreferences **/
        SharedPreferenceStore.deleteValue(getApplicationContext(), "Userid");
        SharedPreferenceStore.deleteValue(getApplicationContext(), "UserName");
        SharedPreferenceStore.deleteValue(getApplicationContext(), "PhoneNo");
        SharedPreferenceStore.deleteValue(getApplicationContext(), "PhoneCode");
        SharedPreferenceStore.deleteValue(getApplicationContext(), "Designation");
        SharedPreferenceStore.deleteValue(getApplicationContext(), "Email");
        SharedPreferenceStore.deleteValue(getApplicationContext(), "Type");
        SharedPreferenceStore.deleteValue(getApplicationContext(), "LoadingPoint");
        SharedPreferenceStore.deleteValue(getApplicationContext(), "LoadingPointName");
        SharedPreferenceStore.deleteValue(getApplicationContext(), "city");
        SharedPreferenceStore.deleteValue(getApplicationContext(), "country");
        SharedPreferenceStore.deleteValue(getApplicationContext(), "img");
        SharedPreferenceStore.deleteValue(getApplicationContext(), "client_comp_id");
        SharedPreferenceStore.deleteValue(getApplicationContext(),"ytdpres");
/************ Change 15 July *************/
        String userid =  SharedPreferenceStore.getValue(getApplicationContext(), "Userid", "");
        String userCompId =  SharedPreferenceStore.getValue(getApplicationContext(), "client_comp_id", "");
        String loginType =  SharedPreferenceStore.getValue(getApplicationContext(), "Type","");
        SharedPreferenceStore.getValue(getApplicationContext(), "LoadingPoint", "");
        appGlobal.userType = loginType ;
        appGlobal.userId = userid;
        appGlobal.userCompId = userCompId;

/************ Change 15 July *************/
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null,null,null);


        /** Navigate to Login Screen **/
        Intent intent = new Intent(getApplicationContext(), HomeContainer.class);
        startActivity(intent);
        finish();
    }


}
