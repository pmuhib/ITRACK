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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.activities.CategoryDetailContainer;

import com.client.itrack.activities.EmployeeDetail;
import com.client.itrack.R;

import com.client.itrack.adapters.EmployeeAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.ClickListener;
import com.client.itrack.listener.HttpRequestCallback;

import com.client.itrack.model.EmployeeModel;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DividerItemDecoration;
import com.client.itrack.utility.DrawerConst;
import com.client.itrack.utility.RecyclerTouchListener;
import com.client.itrack.utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class EmployeelistFragments extends Fragment {

    RecyclerView recyclerView;
    RelativeLayout employeeSearchContainer ;
    ArrayList<EmployeeModel> allClientCompEmployeeList;
    ImageView addicon , ivSearchEmployee ;
    EditText etEmployeeName  ;
    TextView tvEmployeeSearch ;

    String companyName;
    private String companyLogo,companyType;
    private View view;
    private Context context;
    private String companyId;
    boolean isSearching= false ;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context ;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.employeelist, container, false);

        /**** Client Company Details ****/
        companyName=getArguments().getString("CompanyName");
        companyType=getArguments().getString("CompanyType");
        companyLogo=getArguments().getString("CompanyLogo");
        companyId=getArguments().getString("CompanyId");
        Picasso.with(context).load(companyLogo).placeholder(R.drawable.circledefault).error(R.drawable.circledefault).into(((ImageView) view.findViewById(R.id.companyLogo)));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolBarEmployeeList = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((ImageView)toolBarEmployeeList.findViewById(R.id.btn_navigation)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish();
            }
        });

        ((TextView) toolBarEmployeeList.findViewById(R.id.txt_heading)).setGravity(Gravity.CENTER);

        allClientCompEmployeeList = new ArrayList<>();

        recyclerView = (RecyclerView)view.findViewById(R.id.listClientEmployees);

        addicon=(ImageView) view.findViewById(R.id.addicon);

        addicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CategoryDetailContainer.class);
                intent.putExtra("catpos", DrawerConst.ADD_EMPLOYEE );
                intent.putExtra("CompanyId", companyId!=null?companyId:"");
                intent.putExtra("CompanyName",companyName);
                intent.putExtra("CompanyType",companyType);
                startActivity(intent);

            }
        });


        employeeSearchContainer =  (RelativeLayout)view.findViewById(R.id.employeeSearchContainer);
        employeeSearchContainer.setVisibility(View.GONE);

        etEmployeeName = (EditText)employeeSearchContainer.findViewById(R.id.etEmployeeName);
        tvEmployeeSearch =   (TextView)employeeSearchContainer.findViewById(R.id.tvEmployeeSearch);
        tvEmployeeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String employeeName =  etEmployeeName.getText().toString().trim() ;
                if(!employeeName.isEmpty())
                {
                    loadEmployeeListByEmployeeName(employeeName);
                }

            }
        });


        ivSearchEmployee = (ImageView) view.findViewById(R.id.ivSearchEmployee);
        ivSearchEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int isSearchOpen =  employeeSearchContainer.getVisibility();
                if(isSearchOpen==8)
                {
                    employeeSearchContainer.setVisibility(View.VISIBLE);
                }
                else
                {
                    employeeSearchContainer.setVisibility(View.GONE);
                }

            }
        });



        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent= new Intent(getActivity(), EmployeeDetail.class);
                intent.putExtra("compEmpid",allClientCompEmployeeList.get(position).companyId);
                intent.putExtra("CompanyName",companyName);
                intent.putExtra("CompanyType",companyType);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void loadEmployeeListByEmployeeName(final String employeeName) {
        isSearching =  true  ;
        allClientCompEmployeeList.clear();
        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL+"employeelistbyclientid";
        HashMap<String,String> data= new HashMap<>();
        data.put("client_id",companyId);
        HttpPostRequest.doPost(getActivity(), url,Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    allClientCompEmployeeList.clear();
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("employee_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            EmployeeModel cmodel = new EmployeeModel();
                            cmodel.companyId = jobject.getString("user_id");
                            cmodel.companyName = jobject.getString("f_name")+" "+jobject.getString("l_name");
                            cmodel.email = jobject.getString("email");
                            cmodel.companyLogoName=jobject.getString("emp_img");
                            cmodel.phoneNumber="+"+jobject.getString("code_phone_no")+"-"+jobject.getString("phone_no");
                            if(cmodel.companyName.toLowerCase().contains(employeeName.toLowerCase())) {
                                allClientCompEmployeeList.add(cmodel);
                            }
                        }
                        EmployeeAdapter adapter = new EmployeeAdapter(allClientCompEmployeeList,getActivity());

                        recyclerView.setAdapter(adapter);

                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        // recyclerView.setAdapter(drawerAdapter);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(dividerItemDecoration);
                        adapter.notifyDataSetChanged();

                    } else {
                        EmployeeAdapter adapter = new EmployeeAdapter(allClientCompEmployeeList,getActivity());
                        recyclerView.setAdapter(adapter);
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

    private void loadEventData() {
        isSearching = false  ;
        allClientCompEmployeeList.clear();
        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL+"employeelist";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("employee_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            EmployeeModel cmodel = new EmployeeModel();
                            cmodel.companyId = jobject.getString("user_id");
                            cmodel.companyName = jobject.getString("f_name")+" "+jobject.getString("l_name");
                            cmodel.email = jobject.getString("email");
                            // cmodel.companyLogoName=jobject.getString("logo_thumb");
                            cmodel.phoneNumber=jobject.getString("phone_no");
                            allClientCompEmployeeList.add(cmodel);

                        }

                        EmployeeAdapter adapter = new EmployeeAdapter(allClientCompEmployeeList,getActivity());

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

    private void loadEmployeeByClientId() {
        isSearching = false  ;

        allClientCompEmployeeList.clear();
        Utils.showLoadingPopup(getActivity());
        String url = Constants.BASE_URL+"employeelistbyclientid";
        HashMap<String,String> data= new HashMap<>();
        data.put("client_id",companyId);
        HttpPostRequest.doPost(getActivity(), url,Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);

                    Boolean status = jobj.getBoolean("status");
                    allClientCompEmployeeList.clear();
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("employee_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            EmployeeModel cmodel = new EmployeeModel();
                            cmodel.companyId = jobject.getString("user_id");
                            cmodel.companyName = jobject.getString("f_name")+" "+jobject.getString("l_name");
                            cmodel.email = jobject.getString("email");
                            cmodel.companyLogoName=jobject.getString("emp_img");
                            cmodel.phoneNumber="+"+jobject.getString("code_phone_no")+"-"+jobject.getString("phone_no");
                            allClientCompEmployeeList.add(cmodel);
                        }

                        EmployeeAdapter adapter = new EmployeeAdapter(allClientCompEmployeeList,getActivity());

                        recyclerView.setAdapter(adapter);

                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        // recyclerView.setAdapter(drawerAdapter);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(dividerItemDecoration);
                        adapter.notifyDataSetChanged();

                    } else {
                        String msg = jobj.getString("msg");
                        EmployeeAdapter adapter = new EmployeeAdapter(allClientCompEmployeeList,getActivity());
                        recyclerView.setAdapter(adapter);
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
        if(companyId.length()==0){
            if(!isSearching)
            loadEventData();
            else
            {
                String employeeName =  etEmployeeName.getText().toString().trim() ;
                if(!employeeName.isEmpty())
                {
                    loadEmployeeListByEmployeeName(employeeName);
                }
            }
        }else{
            if(!isSearching)
            loadEmployeeByClientId();
            else
            {
                String employeeName =  etEmployeeName.getText().toString().trim() ;
                if(!employeeName.isEmpty())
                {
                    loadEmployeeListByEmployeeName(employeeName);
                }
            }
        }
    }



}
