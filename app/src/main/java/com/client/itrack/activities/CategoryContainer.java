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
import com.client.itrack.fragments.client.ClientlistFragments;
import com.client.itrack.fragments.message.ComposeMessageFragment;
import com.client.itrack.fragments.dsr.DSRListFragment;
import com.client.itrack.fragments.client.EmployeelistFragments;
import com.client.itrack.fragments.EventListFragments;
import com.client.itrack.fragments.message.MessageListFragment;
import com.client.itrack.fragments.NewslistFragments;
import com.client.itrack.fragments.quotes.QuoteListFragment;
import com.client.itrack.fragments.quotes.SubmitQuoteRequestFragment;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.DrawerConst;

public class CategoryContainer extends AppCompatActivity {

    Toolbar toolbar;
    ImageView btn_nav;
    TextView mTitle;
    AppGlobal appGlobal = AppGlobal.getInstance();
    private String companyLogo,companyid,companyName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorycontainer);
        setUpToolbar();
        Intent intent = getIntent();
        int catpos = intent.getIntExtra("catpos", 0);
        companyid = intent.getStringExtra("CompanyId");
        companyName = intent.getStringExtra("CompanyName");
        companyLogo = intent.getStringExtra("CompanyLogo");
        try {
            switch (catpos) {
                case DrawerConst.CLIENTS_LIST: {
                    //View Clients List
                    mTitle.setText(R.string.viewclientlist);
                    ClientlistFragments registerFragments = new ClientlistFragments();
                    Bundle bundle = new Bundle();
                    if (companyid == null) {
                        bundle.putCharSequence("CompanyId", "");
                    } else if (companyid != null) {
                        bundle.putCharSequence("CompanyId", companyid);
                    }
                    bundle.putCharSequence("CompanyName", companyName);
                    bundle.putCharSequence("CompanyLogo", companyLogo);
                    registerFragments.setArguments(bundle);
                    replaceFragment(R.id.categorycontainer, registerFragments);
                }
                break;

                case DrawerConst.DSR_LIST: {
                    // View DSR List
                    mTitle.setText(R.string.viewdsrlist);
                    DSRListFragment registerFragments = new DSRListFragment();
                    Bundle bundle = new Bundle();
                    if (companyid != null) {
                        bundle.putCharSequence("client_id", companyid);
                    }
                    registerFragments.setArguments(bundle);
                    replaceFragment(R.id.categorycontainer, registerFragments);
                }
                break;
                case DrawerConst.QUOTE_LIST: {
                    // View Quotes List
                    if(!appGlobal.userId.isEmpty()) {
                        mTitle.setText(R.string.viewquotelist);
                        QuoteListFragment registerFragments = new QuoteListFragment();
                        Bundle bundle = new Bundle();
                        if (companyid != null && !companyid.trim().isEmpty()) {
                            bundle.putCharSequence("client_id", companyid.trim());
                        } else {
                            bundle.putCharSequence("client_id", "");
                        }
                        registerFragments.setArguments(bundle);
                        replaceFragment(R.id.categorycontainer, registerFragments);

                    }
                    else
                    {
                        mTitle.setText(R.string.quote_req_msg);
                        SubmitQuoteRequestFragment registerFragments =  new SubmitQuoteRequestFragment();
                        replaceFragment(R.id.categorycontainer, registerFragments);
                    }
                }

                break;
                case 3 : {
                    mTitle.setText(R.string.events_update);
                    EventListFragments registerFragments = new EventListFragments();
                    replaceFragment(R.id.categorycontainer, registerFragments);
                }
                break ;

                case DrawerConst.MSG_LIST : {
                    mTitle.setText(R.string.viewmessages);
                    MessageListFragment registerFragments = new MessageListFragment();
                    replaceFragment(R.id.categorycontainer, registerFragments);
                }

                break ;
                case DrawerConst.COMPOSE_MSG : {
                    mTitle.setText(R.string.composemessage);
                    ComposeMessageFragment registerFragments = new ComposeMessageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("senderId",intent.getStringExtra("senderId"));
                    bundle.putCharSequence("senderEmpId",intent.getStringExtra("senderEmpId"));
                    bundle.putCharSequence("receiverEmpId",intent.getStringExtra("receiverEmpId"));
                    bundle.putCharSequence("senderType",intent.getStringExtra("senderType"));
                    bundle.putCharSequence("receiverId",intent.getStringExtra("receiverId"));
                    bundle.putCharSequence("msgSubject",intent.getStringExtra("msgSubject"));
                    bundle.putCharSequence("receiverType", intent.getStringExtra("receiverType"));
                    registerFragments.setArguments(bundle);
                    replaceFragment(R.id.categorycontainer, registerFragments);
                }

                break ;

                case DrawerConst.NEWS_LIST : {
                    // view News updates
                    mTitle.setText(R.string.newsupdate);
                    NewslistFragments registerFragments = new NewslistFragments();
                    replaceFragment(R.id.categorycontainer, registerFragments);
                }
                break;
                case 6 :
                    break ;
                default: {
                    // View Client companies Employees List
                    EmployeelistFragments registerFragments = new EmployeelistFragments();
                    Bundle bundle = new Bundle();
                    mTitle.setText(companyName.trim());
                    if (companyid == null) {
                        bundle.putCharSequence("CompanyId", "");
                    } else if (companyid != null) {
                        bundle.putCharSequence("CompanyId", companyid);
                    }
                    bundle.putCharSequence("CompanyType", intent.getStringExtra("CompanyType"));
                    bundle.putCharSequence("CompanyName", companyName);
                    bundle.putCharSequence("CompanyLogo", companyLogo);
                    registerFragments.setArguments(bundle);
                    replaceFragment(R.id.categorycontainer, registerFragments);
                }

                break;

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void replaceFragment(int oldFragmentId, Fragment registerFragments) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(oldFragmentId, registerFragments);
        // fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) ((Toolbar) findViewById(R.id.toolbar)).findViewById(R.id.txt_heading);
        btn_nav = (ImageView) toolbar.findViewById(R.id.btn_navigation);
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFragmentManager().popBackStack();
            }
        });
        setSupportActionBar(toolbar);

    }


}
