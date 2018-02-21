package com.client.itrack.fragments.client;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.activities.CategoryDetailContainer;
import com.client.itrack.activities.ClientDetail;
import com.client.itrack.R;
import com.client.itrack.adapters.ClientAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.ClickListener;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.ClientModel;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DividerItemDecoration;
import com.client.itrack.utility.DrawerConst;
import com.client.itrack.utility.RecyclerTouchListener;
import com.client.itrack.utility.Utils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientlistFragments extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ClientModel> allclientlist;
    ArrayList<ClientModel> allclientlistOriginal = new ArrayList<>();
    ImageView addicon;
    private View view;
    private EditText etSearchClientName, etSearchAccountNumber;
    private TextView tvSearch;
    private ImageView searchIcon;
    private RelativeLayout searchLayout;
    private boolean isSearch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.clientlist, container, false);

        tvSearch = (TextView)view.findViewById(R.id.tvSearch);
        searchLayout = (RelativeLayout) view.findViewById(R.id.search_layout);
        etSearchClientName = (EditText) searchLayout.findViewById(R.id.etSearchClientName);
        etSearchAccountNumber = (EditText) searchLayout.findViewById(R.id.etSearchAccountNumber);
        searchLayout.setVisibility(View.GONE);
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                if (Utils.isNetworkConnected(getActivity(),false)) {
                    if ((etSearchClientName.getText().toString().length() == 0) && (etSearchAccountNumber.getText().toString().length() == 0)) {
                        Toast.makeText(getActivity(), "Please enter client name or account number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    loadSearchData();
                } else {
                    Toast.makeText(getActivity(), "Internet not connected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((ImageView)((Toolbar) getActivity().findViewById(R.id.toolbar)).findViewById(R.id.btn_navigation)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish();
            }
        });

        ((TextView)((Toolbar) getActivity().findViewById(R.id.toolbar)).findViewById(R.id.txt_heading)).setGravity(Gravity.CENTER);

        allclientlist = new ArrayList<>();

        recyclerView = (RecyclerView)getView().findViewById(R.id.clientItemList);

        //Add New Client Company
        addicon=(ImageView) getView().findViewById(R.id.addicon);
        addicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Intent intent = new Intent(getActivity(), CategoryDetailContainer.class);
                    intent.putExtra("catpos", DrawerConst.ADD_CLIENT);
                    startActivity(intent);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        });

        searchIcon=(ImageView) getView().findViewById(R.id.ivSearchIcon);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSearch) {
                    isSearch=true;
                    searchLayout.setVisibility(View.VISIBLE);
                }else{
                    isSearch=false;
                    searchLayout.setVisibility(View.GONE);
                    allclientlist.clear();
                    allclientlist.addAll(allclientlistOriginal);
                    ClientAdapter adapter = new ClientAdapter(allclientlist,getActivity());

                    recyclerView.setAdapter(adapter);
                    DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    // recyclerView.setAdapter(drawerAdapter);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    adapter.notifyDataSetChanged();

                }
            }
        });



        //   recyclerView.addOnItemTouchListener(new Re);

        //Click Event for One of client in Client List
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent= new Intent(getActivity(), ClientDetail.class);
                intent.putExtra("companyid",allclientlist.get(position).companyId);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void loadClientList() {
        allclientlist.clear();
        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL+"clientlist";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("client_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            ClientModel cmodel = new ClientModel();
                            cmodel.companyId = jobject.getString("comp_id");
                            cmodel.companyName = jobject.getString("company_name");
                            cmodel.companyCode = jobject.getString("comp_code");
                            cmodel.email = jobject.getString("company_email");
                            cmodel.companyLogoName=jobject.getString("logo_thumb");
                            cmodel.company_type =  jobject.getString("company_type");
                            cmodel.phoneNumber="+"+jobject.getString("code_phone_no")+"-"+jobject.getString("company_phone");
                            allclientlist.add(cmodel);

                        }
                        allclientlistOriginal.clear();
                        allclientlistOriginal.addAll(allclientlist);
                        ClientAdapter adapter = new ClientAdapter(allclientlist,getActivity());
                        recyclerView.setAdapter(adapter);
                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        // recyclerView.setAdapter(drawerAdapter);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(dividerItemDecoration);
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }


    private void loadSearchData() {
        // {"comp_name":"inf","client_code":"shh"}

        String clientName = etSearchClientName.getText().toString().trim();
        String accountNumber = etSearchAccountNumber.getText().toString().trim();

        allclientlist.clear();
        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL+"clientsearch";
        JsonObject jsonObject = new JsonObject();
        HashMap<String, String> data = new HashMap<>();


        if(clientName.length()>0){
            data.put("comp_name", clientName);
        }
        if(accountNumber.length()>0){
            data.put("client_code", accountNumber);
        }

        HttpPostRequest.doPost(getActivity(), url,Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("client_details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            ClientModel cmodel = new ClientModel();
                            cmodel.companyId = jobject.getString("comp_id");
                            cmodel.companyName = jobject.getString("company_name");
                            cmodel.companyCode = jobject.getString("comp_code");
                            cmodel.email = jobject.getString("company_email");
                            cmodel.companyLogoName=jobject.getString("logo_thumb");
                            cmodel.company_type =  jobject.getString("company_type");
                            cmodel.phoneNumber="+"+jobject.getString("code_phone_no")+"-"+jobject.getString("company_phone");
                            allclientlist.add(cmodel);
                        }
                        ClientAdapter adapter = new ClientAdapter(allclientlist,getActivity());
                        recyclerView.setAdapter(adapter);
                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        // recyclerView.setAdapter(drawerAdapter);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(dividerItemDecoration);
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Utils.isNetworkConnected(getActivity(),false))
            loadClientList();
        else
            Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
    }

    public void hideKeyboard(){
        InputMethodManager imm=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().findViewById(R.id.etSearchClientName).getWindowToken(), 0);
        imm.hideSoftInputFromWindow(getView().findViewById(R.id.etSearchAccountNumber).getWindowToken(), 0);

    }

}
