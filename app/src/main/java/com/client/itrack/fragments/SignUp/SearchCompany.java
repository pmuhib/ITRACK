package com.client.itrack.fragments.SignUp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.adapters.ClientAdapter;
import com.client.itrack.adapters.CompanySearchAdapter;

import com.client.itrack.fragments.client.AddClientFragments;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.ClickListener;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.ClientModel;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DividerItemDecoration;
import com.client.itrack.utility.RecyclerTouchListener;
import com.client.itrack.utility.Utils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sony on 03-03-2017.
 */

public class SearchCompany extends Fragment {
    RecyclerView recyclerView;
    ArrayList<ClientModel> allclientlist;
    TextView txt_compsearch,selectionmsg,add_comp;
    EditText et_compname;
    View view;
    LinearLayout bottompart;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.searchcompany,container,false);
        txt_compsearch= (TextView) view.findViewById(R.id.txt_compSearch);
        selectionmsg= (TextView) view.findViewById(R.id.txt_selection);
        add_comp= (TextView) view.findViewById(R.id.txt_addComp);
        add_comp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            SignUpAddClient fragments=new SignUpAddClient();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer,fragments).addToBackStack(null).commit();
            }
        });
        et_compname= (EditText) view.findViewById(R.id.et_compName);
        bottompart= (LinearLayout) view.findViewById(R.id.bottomview);
        txt_compsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager manager= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(txt_compsearch.getWindowToken(),0);
                if (Utils.isNetworkConnected(getActivity(),false)) {
                    if (et_compname.getText().toString().length() == 0)  {
                        Toast.makeText(getActivity(), "Please enter Company Name", Toast.LENGTH_SHORT).show();
                        allclientlist.clear();
                        selectionmsg.setVisibility(View.GONE);
                        bottompart.setVisibility(View.GONE);
                        return;
                    }
                    loadSearchData();
                } else {
                    Toast.makeText(getActivity(), "Internet not connected", Toast.LENGTH_SHORT).show();
                    selectionmsg.setVisibility(View.GONE);
                }

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((TextView)getActivity().findViewById(R.id.txt_heading)).setText("Search");
        ((ImageView)getActivity().findViewById(R.id.btn_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount()>0)
                {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        allclientlist = new ArrayList<>();
        recyclerView = (RecyclerView)getView().findViewById(R.id.clientItemList);
        ClientAdapter adapter = new ClientAdapter(allclientlist,getActivity());

        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // recyclerView.setAdapter(drawerAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter.notifyDataSetChanged();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                AddCompany registerFragments = new AddCompany();
                Bundle bundle=new Bundle();
                bundle.putSerializable("company_name",allclientlist.get(position).companyName);
                bundle.putSerializable("company_email",allclientlist.get(position).email);
                bundle.putSerializable("client_id",allclientlist.get(position).companyId);
                bundle.putSerializable("CompanyType",allclientlist.get(position).company_type);
                registerFragments.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.framecontainer, registerFragments).addToBackStack(null).commit();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void loadSearchData() {
        String CompanyName = et_compname.getText().toString().trim();
        Utils.showLoadingPopup(getActivity());
        allclientlist.clear();
        String url = Constants.BASE_URL+"clientsearch";
        JsonObject jsonObject = new JsonObject();
        HashMap<String, String> data = new HashMap<>();


        if(CompanyName.length()>0){
            data.put("comp_name",CompanyName);
        }
        HttpPostRequest.doPost(getActivity(), url,Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        selectionmsg.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = jobj.getJSONArray("client_details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            ClientModel cmodel = new ClientModel();
                            cmodel.companyId = jobject.getString("comp_id");
                            cmodel.companyName = jobject.getString("company_name");
                            cmodel.address=jobject.getString("company_add1");
                            cmodel.companyCode = jobject.getString("comp_code");
                            cmodel.email = jobject.getString("company_email");
                            cmodel.companyLogoName=jobject.getString("logo_thumb");
                            cmodel.company_type =  jobject.getString("company_type");
                            cmodel.phoneNumber="+"+jobject.getString("code_phone_no")+"-"+jobject.getString("company_phone");
                            cmodel.domain_name=jobject.getString("company_domain_name");

                            allclientlist.add(cmodel);
                            bottompart.setVisibility(View.VISIBLE);
                        }

                        CompanySearchAdapter adapter = new CompanySearchAdapter(allclientlist,getActivity());
                        recyclerView.setAdapter(adapter);
                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        // recyclerView.setAdapter(drawerAdapter);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(dividerItemDecoration);
                        adapter.notifyDataSetChanged();

                    } else {
//                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                        selectionmsg.setVisibility(View.GONE);
                        allclientlist.clear();
                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        builder.setTitle("Message");
                        builder.setMessage("Seems your Company is Not Registered with us.Please add your Company");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Add Your Company", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                              SignUpAddClient fragments=new SignUpAddClient();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framecontainer,fragments).addToBackStack(null).commit();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.create();
                        builder.show();

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

}
