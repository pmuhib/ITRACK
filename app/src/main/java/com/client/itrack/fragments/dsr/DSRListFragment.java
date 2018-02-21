package com.client.itrack.fragments.dsr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.activities.DSRDetails;
import com.client.itrack.R;
import com.client.itrack.adapters.DSRAdapter;
import com.client.itrack.adapters.DSRStatusSpinnerAdapter;
import com.client.itrack.adapters.LocationPointAdapter;
import com.client.itrack.adapters.QuoteStatusSpinnerAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.ClickListener;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.ClientModel;
import com.client.itrack.model.DSRModel;
import com.client.itrack.model.DSRStatusModel;
import com.client.itrack.model.LocationPointModel;
import com.client.itrack.model.TrailerTypeModel;
import com.client.itrack.model.TruckStatusModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DividerItemDecoration;
import com.client.itrack.utility.RecyclerTouchListener;
import com.client.itrack.utility.SharedPreferenceStore;
import com.client.itrack.utility.Utility;
import com.client.itrack.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class DSRListFragment extends Fragment {

    View view;
    RecyclerView dsrRecyclerView;
    ArrayList<DSRModel> dsrArrList;
    Spinner spinnerListStatus;
    LinearLayout dsr_search_form;
    boolean isSearchPopupOpen, isFiltering, isSearching;
    private AppGlobal appGlobal= AppGlobal.getInstance() ;
    String client_id ,userType ;

    DatePickerDialog fromDatePickerDialog ;
    DatePickerDialog toDatePickerDialog ;
    long miliseconds ;


    /**
     * Search Form Field
     **/

    EditText etDSRRefNum, etClientCompName, etFromDate, etToDate, et;
    ImageView ivFromDate, ivToDate,ivAddNewDSR;
    LinearLayout dateContainer ;
    private EditText etTruckDriverName,etVehicleNumber;
    private Spinner spinnerListBalancePaid;
    private AutoCompleteTextView autocompleteTVLoadingPoint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.dsrlistfragment, container, false);
        userType   = appGlobal.userType;
        client_id = getArguments().getString("client_id");

        setupGUI();
        return view;
    }

    String loadingPointId = "" ;
    private void setupGUI() {
        dsr_search_form = (LinearLayout) view.findViewById(R.id.dsr_search_form);
        dsr_search_form.setVisibility(View.GONE);
        isSearchPopupOpen = false;
        /**  DSR Form Fields  **/
        etDSRRefNum = (EditText) dsr_search_form.findViewById(R.id.etDSRRefNum);
        etClientCompName = (EditText) dsr_search_form.findViewById(R.id.etClientCompName);
        etTruckDriverName = (EditText) dsr_search_form.findViewById(R.id.etTruckDriverName);
        etVehicleNumber = (EditText) dsr_search_form.findViewById(R.id.etVehicleNumber);

        autocompleteTVLoadingPoint = (AutoCompleteTextView) view.findViewById(R.id.autocompleteTVLoadingPoint);
        autocompleteTVLoadingPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                LocationPointModel locationPointModel =  (LocationPointModel)((ListView) adapterView).getAdapter().getItem(position);
                loadingPointId = locationPointModel.locId;
            }
        });

        autocompleteTVLoadingPoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("TextChange",s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etFromDate = (EditText) dsr_search_form.findViewById(R.id.etFromDate);
        etToDate = (EditText) dsr_search_form.findViewById(R.id.etToDate);
        ivFromDate = (ImageView) dsr_search_form.findViewById(R.id.ivFromDate);
        ivToDate = (ImageView) dsr_search_form.findViewById(R.id.ivToDate);
        dateContainer = (LinearLayout) dsr_search_form.findViewById(R.id.dateContainer);
        etFromDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    showFromDateDialog();
                }
            }
        });

        etToDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    showFromDateDialog();
                }
            }
        });

        // Select From Date
        ivFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFromDateDialog();
            }
        });
        // Select To Date
        ivToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToDateDialog();
            }
        });

        TextView tvDSRSearch = (TextView) dsr_search_form.findViewById(R.id.tvDSRSearch);
        ImageView ivDSRSearch = (ImageView) view.findViewById(R.id.ivDsrSrchIcn);
        ImageView ivDSRFilterIcn = (ImageView) view.findViewById(R.id.ivDSRFilterIcn);
        ivAddNewDSR  = (ImageView) view.findViewById(R.id.addDsrIcn) ;
        /** DSR Search Button  **/
        tvDSRSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dsrRefNum = etDSRRefNum.getText().toString().trim();
                String clientComp = etClientCompName.getText().toString().trim();
                String fromDate = etFromDate.getText().toString().trim();
                String toDate = etToDate.getText().toString().trim();
                DSRStatusModel statusModel = (DSRStatusModel) spinnerListStatus.getSelectedItem();
                String status = "";
                String balance_status = "";
                String driver_name = etTruckDriverName.getText().toString().trim();
                String vehicle_number = etVehicleNumber.getText().toString().trim();
                String loading_point = loadingPointId;

                if (statusModel != null) {
                    int dsrStatusId = Integer.parseInt(statusModel.dsrStatusID);
                    if (dsrStatusId != 0) {
                        status = statusModel.dsrStatusID;
                    }
                }
                if(spinnerListBalancePaid.getSelectedItemPosition()>0)
                {
                    balance_status = (String)spinnerListBalancePaid.getSelectedItem() ;
                }

                if (!dsrRefNum.isEmpty() || !clientComp.isEmpty() || !fromDate.isEmpty() || !toDate.isEmpty()
                        || !balance_status.isEmpty() || !driver_name.isEmpty() || !loading_point.isEmpty() || !vehicle_number.isEmpty()) {
                    if (Utils.isNetworkConnected(getActivity(),false)) {
                        loadDSRListFiltered(dsrRefNum, clientComp, fromDate, toDate, status,balance_status,driver_name,loading_point,vehicle_number);
                    } else {
                        Toast.makeText(getActivity(), "No Internet Connectivity!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please provide at least one filter value.", Toast.LENGTH_SHORT).show();
                }


            }
        });


        /** DSR Search Popup open Button **/


        ivDSRSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showHideSearchForm();
            }
        });

        /**** Open Filter Popup ****/


        ivDSRFilterIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSearchPopupOpen) {

                    if (isSearching) {

                        dsr_search_form.setVisibility(View.VISIBLE);
                        spinnerListStatus.setVisibility(View.VISIBLE);
                        (dsr_search_form.findViewById(R.id.tvDSRSearch)).setVisibility(View.GONE);
                        etDSRRefNum.setVisibility(View.GONE);
                        etClientCompName.setVisibility(View.GONE);
                        etVehicleNumber.setVisibility(View.GONE);
                        autocompleteTVLoadingPoint.setVisibility(View.GONE);
                        spinnerListBalancePaid.setVisibility(View.GONE);
                        etTruckDriverName.setVisibility(View.GONE);
                        dateContainer.setVisibility(View.GONE);
                        isSearching = false;
                        isFiltering = true;
                    } else {
                        dsr_search_form.setVisibility(View.GONE);
                        isSearchPopupOpen = false;
                        isFiltering = false;
                        isSearching = false;
                    }

                } else {
                    dsr_search_form.animate()
                            .translationX(0)
                            .alpha(1.0f)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    isFiltering = true;
                                    isSearchPopupOpen = true;
                                }
                            });

                    spinnerListStatus.setVisibility(View.VISIBLE);
                    (dsr_search_form.findViewById(R.id.tvDSRSearch)).setVisibility(View.GONE);
                    etDSRRefNum.setVisibility(View.GONE);
                    etClientCompName.setVisibility(View.GONE);
                    etVehicleNumber.setVisibility(View.GONE);
                    spinnerListBalancePaid.setVisibility(View.GONE);
                    autocompleteTVLoadingPoint.setVisibility(View.GONE);
                    etTruckDriverName.setVisibility(View.GONE);
                    dateContainer.setVisibility(View.GONE);
                    dsr_search_form.setVisibility(View.VISIBLE);

                }
            }
        });

        /***** Add New DSR ********/


        ivAddNewDSR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DSRDetails.class);
                intent.putExtra("action", "add");
                startActivity(intent);
            }
        });

    }

    private void showHideSearchForm() {

        if (isSearchPopupOpen) {

            if (isFiltering) {

                dsr_search_form.setVisibility(View.VISIBLE);
                spinnerListStatus.setVisibility(View.GONE);
                (dsr_search_form.findViewById(R.id.tvDSRSearch)).setVisibility(View.VISIBLE);
                etDSRRefNum.setVisibility(View.VISIBLE);
                if(userType.equals("employee")) {
                    etClientCompName.setVisibility(View.VISIBLE);
                    autocompleteTVLoadingPoint.setVisibility(View.VISIBLE);
                    spinnerListBalancePaid.setVisibility(View.VISIBLE);
                    etTruckDriverName.setVisibility(View.VISIBLE);
                    etVehicleNumber.setVisibility(View.VISIBLE);
                }

                dateContainer.setVisibility(View.VISIBLE);

                isFiltering = false;
                isSearching = true;
            } else {
                dsr_search_form.setVisibility(View.GONE);
                isSearchPopupOpen = false;
                isSearching = false;
                isFiltering = false;
            }

        } else {
            dsr_search_form.animate()
                    .translationX(0)
                    .alpha(1.0f)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            isSearching = true;
                            isSearchPopupOpen = true;
                        }
                    });


            spinnerListStatus.setVisibility(View.GONE);
            (dsr_search_form.findViewById(R.id.tvDSRSearch)).setVisibility(View.VISIBLE);
            etDSRRefNum.setVisibility(View.VISIBLE);
            if(userType.equals("employee")) {
                etClientCompName.setVisibility(View.VISIBLE);
                spinnerListBalancePaid.setVisibility(View.VISIBLE);
                autocompleteTVLoadingPoint.setVisibility(View.VISIBLE);
                etTruckDriverName.setVisibility(View.VISIBLE);
                etVehicleNumber.setVisibility(View.VISIBLE);
            }
            dateContainer.setVisibility(View.VISIBLE);

            dsr_search_form.setVisibility(View.VISIBLE);

        }
    }


    private void showFromDateDialog() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        fromDatePickerDialog = new DatePickerDialog(getActivity(),R.style.GLCalenderStyle, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                etFromDate.setText(sdf.format(myCalendar.getTime()));
                miliseconds =  myCalendar.getTime().getTime();
            }
        }, year, month, day);

        fromDatePickerDialog.setTitle("Select Date");
        fromDatePickerDialog.show();

    }

    private void showToDateDialog() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        toDatePickerDialog = new DatePickerDialog(getActivity(),  R.style.GLCalenderStyle,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                etToDate.setText(sdf.format(myCalendar.getTime()));
            }
        }, year, month, day);
        if(fromDatePickerDialog == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please select from date");
            builder.setTitle("Select From Date");
            builder.create();
            builder.show();
            return ;
        }
        toDatePickerDialog.getDatePicker().setMinDate(miliseconds);
        toDatePickerDialog.setTitle("Select Date");
        toDatePickerDialog.show();

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        setupTopBar();

        dsrArrList = new ArrayList<>();
        Bundle bundle = getArguments() ;




        dsrRecyclerView = (RecyclerView) getView().findViewById(R.id.dsrlistview);

        dsrRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), dsrRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), DSRDetails.class);
                intent.putExtra("dsr_id", dsrArrList.get(position).dsrId);
                intent.putExtra("dsr_ref_no", dsrArrList.get(position).dsrRefNumber);
                intent.putExtra("action", "view");
                intent.putExtra("client_comp_name", dsrArrList.get(position).clientCompName);
                intent.putExtra("createEmpId", dsrArrList.get(position).createEmpId);
                intent.putExtra("createEmpType", dsrArrList.get(position).createEmpType);
                startActivity(intent);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        spinnerListBalancePaid = (Spinner) view.findViewById(R.id.spinnerListBalancePaid);
        ArrayList<String> listBalancePaidStatus =   new ArrayList<>();
        listBalancePaidStatus.add("Select paid status") ;
        listBalancePaidStatus.add("Yes") ;
        listBalancePaidStatus.add("NO") ;
        QuoteStatusSpinnerAdapter balancePaidSpinnerAdapter = new QuoteStatusSpinnerAdapter(getActivity(), listBalancePaidStatus);
        spinnerListBalancePaid.setAdapter(balancePaidSpinnerAdapter);
        balancePaidSpinnerAdapter.notifyDataSetChanged();
        spinnerListBalancePaid.setSelection(0);
        /**
         ****************************************
         * Filtering Data on the basis of Status
         ****************************************
         **/

        spinnerListStatus = (Spinner) view.findViewById(R.id.spinnerListStatus);

        spinnerListStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                DSRStatusModel modelDSRStatus = (DSRStatusModel) spinnerListStatus.getSelectedItem();
                if (modelDSRStatus != null) {
                    int statusValue = Integer.parseInt(modelDSRStatus.dsrStatusID);

                    if (statusValue != 0) {

                        if (statusValue > 0) {

                            ArrayList<DSRModel> filteredDsrList = new ArrayList<DSRModel>();
                            for (int indexDsrObj = 0; indexDsrObj < dsrArrList.size(); indexDsrObj++) {
                                DSRModel modelDSR = dsrArrList.get(indexDsrObj);
                               /* int statusVal = Integer.parseInt(modelDSR.dsrStatus);
                                if (statusValue == statusVal) {
                                    filteredDsrList.add(modelDSR);
                                }*/
                                // int statusVal = Integer.parseInt(modelDSR.dsrStatus);
                                if (modelDSR.dsrStatus.equals(modelDSRStatus.dsrStatusID) || modelDSR.dsrStatus.equals(modelDSRStatus.dsrStatusName)) {
                                    filteredDsrList.add(modelDSR);
                                }

                            }
                            DSRAdapter adapter = new DSRAdapter(filteredDsrList, getActivity());
                            dsrRecyclerView.setAdapter(adapter);
                        } else {
                            DSRAdapter adapter = new DSRAdapter(dsrArrList, getActivity());
                            dsrRecyclerView.setAdapter(adapter);
                        }
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setupGUISettings();

    }

    private void setupTopBar() {

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        ImageView ivBack = (ImageView) toolbar.findViewById(R.id.btn_navigation);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        ((TextView) toolbar.findViewById(R.id.txt_heading)).setGravity(Gravity.CENTER);

    }



    private void setupGUISettings() {
        switch (userType)
        {
            case "employee" :
                etClientCompName.setVisibility(View.VISIBLE);
                etVehicleNumber.setVisibility(View.VISIBLE);
                ivAddNewDSR.setVisibility(View.VISIBLE);
                break ;

            default :
                etClientCompName.setVisibility(View.GONE);
                etVehicleNumber.setVisibility(View.GONE);
                ivAddNewDSR.setVisibility(View.GONE);
                break ;

        }
    }

    private void loadDSRListByClientId(String client_id) {
        dsrArrList.clear();
        Utils.showLoadingPopup(getActivity());
        String url = Constants.BASE_URL + "dsrlistbyclientid";
        final HashMap<String, String> data = new HashMap<String, String>();
        data.put("client_id",client_id);
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("general_dsr_info");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            DSRModel dsrModel = new DSRModel();
                            dsrModel.dsrId = jobject.getString("dsr_id");
                            dsrModel.clientCompId = jobject.getString("client_id");
                            dsrModel.clientCompName = jobject.getString("client_id");
                            dsrModel.dsrRefNumber = jobject.getString("dsr_ref_no");
                            dsrModel.dsrCreatedDate = jobject.getString("dsr_date_time").split(" ")[0];
                            dsrModel.dsrStatus = jobject.getString("dsr_status");
                            dsrModel.createEmpId =  jobject.getString("employee_id");
                            String empType =  jobject.getString("type");
                            dsrModel.createEmpType =  empType ;
                            dsrModel.createEmpName =   jobject.getString("empoyee_name");
                            dsrArrList.add(dsrModel);
                        }

                        appGlobal.dsrArrList  = dsrArrList ;

                        setGlobalListCollections(jobj);

                        DSRAdapter adapter = new DSRAdapter(dsrArrList, getActivity());
                        dsrRecyclerView.setAdapter(adapter);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
                        dsrRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        //recyclerView.setAdapter(drawerAdapter);
                        dsrRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        dsrRecyclerView.addItemDecoration(dividerItemDecoration);

                        adapter.notifyDataSetChanged();

                    } else {

                        setGlobalListCollections(jobj);
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Utility.sendReport(getActivity(),"dsrlistbyclientid",e.getMessage(),Utils.newGson().toJson(data),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }


    /**
     * **********************************
     * Load DSR Lists All
     * **********************************
     **/


    private void loadDSRList() {
        dsrArrList.clear();
        Utils.showLoadingPopup(getActivity());
        String url = Constants.BASE_URL + "dsrlist";
        final HashMap<String, String> data = new HashMap<String, String>();
        data.put("emp_id", SharedPreferenceStore.getValue(getActivity(), "Userid", ""));
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("general_dsr_info");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            DSRModel dsrModel = new DSRModel();
                            dsrModel.dsrId = jobject.getString("dsr_id");
                            dsrModel.clientCompId = jobject.getString("client_id");
                            dsrModel.clientCompName = jobject.getString("client_id");
                            dsrModel.dsrRefNumber = jobject.getString("dsr_ref_no");
                            dsrModel.dsrCreatedDate = jobject.getString("dsr_date_time").split(" ")[0];
                            dsrModel.dsrStatus = jobject.getString("dsr_status");
                            dsrModel.createEmpId =  jobject.getString("employee_id");
                            String empType =  jobject.getString("type");
                            dsrModel.createEmpType =  empType ;
                            dsrModel.createEmpName =   jobject.getString("empoyee_name");
                            dsrArrList.add(dsrModel);
                        }

                        appGlobal.dsrArrList  = dsrArrList ;

                        setGlobalListCollections(jobj);

                        DSRAdapter adapter = new DSRAdapter(dsrArrList, getActivity());
                        dsrRecyclerView.setAdapter(adapter);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
                        dsrRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        //recyclerView.setAdapter(drawerAdapter);
                        dsrRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        dsrRecyclerView.addItemDecoration(dividerItemDecoration);

                        adapter.notifyDataSetChanged();

                    } else {

                        setGlobalListCollections(jobj);
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Utility.sendReport(getActivity(),"dsrlist",e.getMessage(),Utils.newGson().toJson(data),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    private void setGlobalListCollections(JSONObject jobj) throws JSONException {

        // DSR Client List Filled
        JSONArray clientJsonArr  = jobj.getJSONArray("client");
        ArrayList<ClientModel> listClients =  new ArrayList<ClientModel>();
        ClientModel clientModel = null ;
        clientModel =new ClientModel();
        clientModel.companyId =  "0" ;
        clientModel.companyName = "Select Client";
        listClients.add(clientModel);

        for (int indexClient = 0; indexClient < clientJsonArr.length(); indexClient++) {
            JSONObject jobject = clientJsonArr.getJSONObject(indexClient);
            clientModel =new ClientModel();
            clientModel.companyId =   jobject.getString("comp_id");
            clientModel.companyName = jobject.getString("company_name");
            clientModel.companyLogoName =  jobject.getString("logo");
            clientModel.email =  jobject.getString("company_email");
            clientModel.phoneNumber=  jobject.getString("company_phone");
            clientModel.companyCode = jobject.getString("comp_code");
            listClients.add(clientModel);
        }

        appGlobal.listClients = listClients ;

        // Trailer Types  List Filled
        JSONArray trailorTypeArr  = jobj.getJSONArray("trailor_type");
        ArrayList<TrailerTypeModel> listTrailerType =  new ArrayList<TrailerTypeModel>();
        TrailerTypeModel modelTrailerType = null ;
        modelTrailerType =new TrailerTypeModel();
        modelTrailerType.trailerId = "0";
        modelTrailerType.trailorType ="Select Trailer Type";
        listTrailerType.add(modelTrailerType);

        for (int indexTrailerType = 0; indexTrailerType < trailorTypeArr.length(); indexTrailerType++) {
            modelTrailerType = new TrailerTypeModel();
            JSONObject trailerTypeObj = trailorTypeArr.getJSONObject(indexTrailerType);
            modelTrailerType.trailerId = trailerTypeObj.getString("trailor_id");
            modelTrailerType.trailorType = trailerTypeObj.getString("trailor_type");
            modelTrailerType.createDate = trailerTypeObj.getString("create_date");
            listTrailerType.add(modelTrailerType);
        }

        appGlobal.listTrailerType = listTrailerType ;



        /****** Fill Trucks Statuses into Truck Status Lists  ****/
        JSONArray truckStatusArr  = jobj.getJSONArray("truck_status");
        ArrayList<TruckStatusModel> listTruckStatus =  new ArrayList<TruckStatusModel>();
        TruckStatusModel modelTruckStatus = null ;
        modelTruckStatus = new TruckStatusModel();
        modelTruckStatus.statusId =  "0" ;
        modelTruckStatus.statusName = "Select Truck Status"  ;
        listTruckStatus.add(modelTruckStatus);
        for (int indexTruckStatus = 0; indexTruckStatus < truckStatusArr.length(); indexTruckStatus++) {
            modelTruckStatus = new TruckStatusModel();
            JSONObject truckStatusObj = truckStatusArr.getJSONObject(indexTruckStatus);
            modelTruckStatus.statusId = truckStatusObj.getString("truck_status_id");
            modelTruckStatus.statusName = truckStatusObj.getString("status_name");
            listTruckStatus.add(modelTruckStatus);
        }

        appGlobal.listTruckStatus = listTruckStatus ;

        // DSR Loading points List
        ArrayList<LocationPointModel> listLoadingPoint =  new ArrayList<LocationPointModel>();
        LocationPointModel locationLPointModel = null ;
//        locationLPointModel = new LocationPointModel();
//        locationLPointModel.locId = "0" ;
//        locationLPointModel.locPoint =  "Select Loading Point" ;
//        locationLPointModel.city = "" ;
//        locationLPointModel.country =  "" ;
//        listLoadingPoint.add(locationLPointModel);

        JSONArray loadingPointJsonArr  = jobj.getJSONArray("loding_point");
        for (int indexLoadingPoint = 0; indexLoadingPoint < loadingPointJsonArr.length(); indexLoadingPoint++) {
            JSONObject jobject = loadingPointJsonArr.getJSONObject(indexLoadingPoint);
            locationLPointModel  = new LocationPointModel();
            locationLPointModel.locId =  jobject.getString("loading_id") ;
            locationLPointModel.locPoint =  jobject.getString("loading_point") ;
            locationLPointModel.city =  jobject.getString("city") ;
            locationLPointModel.country =  jobject.getString("country") ;
            locationLPointModel.created_date =  jobject.getString("create_date") ;
            listLoadingPoint.add(locationLPointModel);
        }

        appGlobal.listLoadingnPoint = listLoadingPoint ;

        // DSR Destination points List
        ArrayList<LocationPointModel> listDestinationPoint =  new ArrayList<LocationPointModel>();
        LocationPointModel locationPointModel = null ;
//        locationPointModel = new LocationPointModel();
//        locationPointModel.locId = "0" ;
//        locationPointModel.locPoint =  "Select Destination Point" ;
//        locationPointModel.city = "" ;
//        locationPointModel.country =  "" ;
//        listDestinationPoint.add(locationPointModel);

        JSONArray destinationPointJsonArr  = jobj.getJSONArray("destination_point");
        for (int indexDestinationPoint = 0; indexDestinationPoint < destinationPointJsonArr.length(); indexDestinationPoint++) {
            JSONObject jobject = destinationPointJsonArr.getJSONObject(indexDestinationPoint);
            locationPointModel  = new LocationPointModel();
            locationPointModel.locId =  jobject.getString("destination_id") ;
            locationPointModel.locPoint =  jobject.getString("destination_point") ;
            locationPointModel.city =  jobject.getString("city") ;
            locationPointModel.country =  jobject.getString("country") ;
            locationPointModel.created_date =  jobject.getString("create_date") ;
            listDestinationPoint.add(locationPointModel);
        }
        appGlobal.listDestinationPoint = listDestinationPoint ;

        //  Truck Details List Vehicle Number All
//
//        JSONArray truckDetailsArr  = jobj.getJSONArray("vichle_detail");
//        ArrayList<VehicleModel> listVehicles= new ArrayList<VehicleModel>();
//        VehicleModel vehicleModel = null ;
//
//
//        for (int indexVehicle = 0; indexVehicle < truckDetailsArr.length(); indexVehicle++) {
//            JSONObject jobject = truckDetailsArr.getJSONObject(indexVehicle);
//            vehicleModel  = new VehicleModel();
//
//            //  vehicleModel.vehicle_id =  jobject.getString("vehicle_id") ;
//            String vehicle_number =  jobject.getString("trailor_number") ;
//            vehicleModel.vehicle_number =  vehicle_number ;
//            vehicleModel.trailer_type =  jobject.getString("trailor_type") ;
//            if(!vehicle_number.trim().isEmpty() && !vehicle_number.equals("-1")) {
//                listVehicles.add(vehicleModel);
//            }
//        }
//        appGlobal.listVehicles = listVehicles ;

        // DSR Status List Filled

        ArrayList<DSRStatusModel> listDSRStatuses = new ArrayList<DSRStatusModel>();
        DSRStatusModel modelDSRStatus = null;
        modelDSRStatus = new DSRStatusModel();
        modelDSRStatus.dsrStatusName = "Select Status";
        modelDSRStatus.dsrStatusID = "0";
        listDSRStatuses.add(modelDSRStatus);

        JSONArray dsrStatusesArr = jobj.getJSONArray("dsr_status_list");

        for (int indexDSRStatus = 0; indexDSRStatus < dsrStatusesArr.length(); indexDSRStatus++) {
            JSONObject jobject = dsrStatusesArr.getJSONObject(indexDSRStatus);
            modelDSRStatus = new DSRStatusModel();
            modelDSRStatus.dsrStatusName = jobject.getString("status_name");
            modelDSRStatus.dsrStatusID = jobject.getString("dsr_status_id");
            listDSRStatuses.add(modelDSRStatus);
        }

        modelDSRStatus = new DSRStatusModel();
        modelDSRStatus.dsrStatusName = "All";
        modelDSRStatus.dsrStatusID = "-1";
        listDSRStatuses.add(modelDSRStatus);
        appGlobal.listDSRStatuses = listDSRStatuses ;

        DSRStatusSpinnerAdapter dsrStatusAdapter = new DSRStatusSpinnerAdapter(getActivity(), listDSRStatuses);
        spinnerListStatus.setAdapter(dsrStatusAdapter);
        dsrStatusAdapter.notifyDataSetChanged();
        spinnerListStatus.setSelection(0);

        LocationPointAdapter loadingPointAdapter = new LocationPointAdapter(getActivity(),listLoadingPoint);
        autocompleteTVLoadingPoint.setAdapter(loadingPointAdapter);
        loadingPointAdapter.notifyDataSetChanged();

    }


    /**
     * *********************************
     * Search DSR List Using Filters
     * *********************************
     **/


    private void loadDSRListFiltered(String dsrRefNum, String clientComp, String fromDate, String toDate, String status,
                                     String balance_status, String driver_name, String loading_point, String vehicle_number) {

        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL + "dsrsearch";

        final HashMap<String, String> data = new HashMap<>();
        if (!dsrRefNum.isEmpty()) {
            data.put("dsr_ref_no", dsrRefNum);
        }
        if (!clientComp.isEmpty()) {
            data.put("compnay_name", clientComp);
        }

        if (!fromDate.isEmpty() && !toDate.isEmpty()) {
            data.put("from_date", fromDate);
            data.put("to_date", toDate);
        }
        if (!status.isEmpty()) {
            data.put("status", status);
        }

        if (!balance_status.isEmpty()) {
            data.put("paid_status", balance_status);
        }
        if (!driver_name.isEmpty()) {
            data.put("driver_name", driver_name);
        }

        if(!loading_point.isEmpty()){
            data.put("loading_point", loading_point);
        }
        if(!vehicle_number.isEmpty()){
            data.put("vehicle_number", vehicle_number);
        }

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {

                        dsrArrList.clear();
                        JSONArray jsonArray = jobj.getJSONArray("dsr_details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            DSRModel dsrModel = new DSRModel();
                            dsrModel.dsrId = jobject.getString("dsr_id");
                            dsrModel.clientCompId = jobject.getString("client_id");
                            dsrModel.clientCompName = jobject.getString("client_id");
                            dsrModel.dsrRefNumber = jobject.getString("dsr_ref_no");
                            dsrModel.dsrCreatedDate = jobject.getString("dsr_date_time").split(" ")[0];
                            dsrModel.dsrStatus = jobject.getString("dsr_status");
                            dsrModel.createEmpId =  jobject.getString("employee_id");
                            String empType =  jobject.getString("type");
                            dsrModel.createEmpType =  empType ;
                            dsrModel.createEmpName =   jobject.getString("empoyee_name");
                            dsrArrList.add(dsrModel);
                        }

                        DSRAdapter adapter = new DSRAdapter(dsrArrList, getActivity());
                        dsrRecyclerView.setAdapter(adapter);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
                        dsrRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        //recyclerView.setAdapter(drawerAdapter);
                        dsrRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        dsrRecyclerView.addItemDecoration(dividerItemDecoration);

                        adapter.notifyDataSetChanged();
                        hideKeyboard();

                    } else {
                        hideKeyboard();
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Utility.sendReport(getActivity(),"dsrsearch",e.getMessage(),Utils.newGson().toJson(data),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }


    /*******************
     * Hide Keyboard
     *******************/

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etDSRRefNum.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etClientCompName.getWindowToken(), 0);

    }

    @Override
    public void onResume() {
        super.onResume();

        if(client_id==null)  loadDSRList();
        else loadDSRListByClientId(client_id);

    }
}
