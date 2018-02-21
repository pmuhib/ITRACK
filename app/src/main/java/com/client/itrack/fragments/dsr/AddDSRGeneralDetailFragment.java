package com.client.itrack.fragments.dsr;


import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TimePicker;
import android.widget.Toast;


import com.client.itrack.activities.DSRDetails;
import com.client.itrack.R;
import com.client.itrack.adapters.AutoSearchAdapter;
import com.client.itrack.adapters.CompanySelectorAdapter;
import com.client.itrack.adapters.DSRCustomsPointAdapter;
import com.client.itrack.adapters.DSRStatusSpinnerAdapter;
import com.client.itrack.adapters.LocationPointAdapter;
import com.client.itrack.adapters.SpinnerAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.ClientModel;
import com.client.itrack.model.CountryModel;
import com.client.itrack.model.CustomsPointModel;
import com.client.itrack.model.DSRGeneralDetailModel;
import com.client.itrack.model.DSRModel;
import com.client.itrack.model.DSRStatusModel;
import com.client.itrack.model.LocationPointModel;
import com.client.itrack.notification.IMethodNotification;
import com.client.itrack.notification.NotificationSendHandler;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utility;
import com.client.itrack.utility.Utils;
import com.client.itrack.views.CustomAutoCompleteTextView;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sony on 11-05-2016.
 */
public class AddDSRGeneralDetailFragment extends Fragment {

    View view;
    EditText etDsrRefNum, etDSRDate, etDSRTime, etClientCompRefNum, etLoadingDate, etLoadingTime, etETATime;
    EditText etRemark, etCustoms, etDateReachedCustom, etTimeReachedCustom, etETADate, etTT,etLoadingPointCity,etLoadingPointCountry,
            etDestinationPointCity,etDestinationPointCountry;
    Spinner spinnerListClientComp, spinnerListStatusDSR,spinnerListCustoms;
    ImageView ivLoadingDate,ivLoadingTime, ivDateReachedCustom,ivTimeReachedCustom, ivETA,ivETATime, ivAddLoadingPoint, ivAddDestinationPoint,ivAddStatus,
            ivDSRTime,ivDSRDate,ivAddCustomPoint;

    //Spinner spinnerListLoadingPoint , spinnerListDestinationPoint;
    TextView btn_next_submit_dsr,btn_save_general_dsr ,tvNowDateTime,lblStatusDSR;


    AutoCompleteTextView autocompleteTVLoadingPoint,autocompleteTVDestinationPoint;

    String action;
    DSRDetails dsrDetailsObj;
    DSRGeneralDetailModel dsrGeneralDetailModel;

    ArrayList<ClientModel> listClients;
    ArrayList<DSRStatusModel> listDSRStatuses;
    ArrayList<LocationPointModel> listLoadingnPoint;
    ArrayList<LocationPointModel> listDestinationPoint;
    ArrayList<CustomsPointModel> listCustomsPoint;
    private AppGlobal appGlobal = AppGlobal.getInstance();
    private boolean isSaving= false;
    ArrayList<CountryModel> listCountry = new ArrayList<>();
    ArrayList<CountryModel> listState = new ArrayList<>();
    ArrayList<CountryModel> listCity = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.add_dsrdetail_general_fragment, container, false);
        Bundle bundle = getArguments();
        action = bundle.getString("action");
        dsrDetailsObj = (DSRDetails) getActivity();
        dsrGeneralDetailModel = dsrDetailsObj.dsrGeneralDetailModel;
        if (dsrGeneralDetailModel == null) {
            dsrGeneralDetailModel = new DSRGeneralDetailModel();
        }
        setupGUI();
        initializeSpinner();
        uIFeaturesSetting();
        bindDataToGUI();
        return view;
    }

    private void initializeSpinner() {


        /*********************************
         *
         *  listClients = appGlobal.listClients;
         *  listDSRStatuses = appGlobal.listDSRStatuses;
         *  lstLoadingPoint = appGlobal.lstLoadingPoint;
         *  listDestinationPoint = appGlobal.listDestinationPoint;
         *  Need to Fetch At runtime
         *
         * *******************************/


        // Set Adapters
        listClients = appGlobal.listClients;
        listDSRStatuses = appGlobal.listDSRStatuses;
        listLoadingnPoint = appGlobal.listLoadingnPoint;
        listDestinationPoint = appGlobal.listDestinationPoint;


        if(listClients!=null && listClients.size()>0){
            CompanySelectorAdapter clientCompAdapter = new CompanySelectorAdapter(getActivity(), listClients);
            spinnerListClientComp.setAdapter(clientCompAdapter);
            clientCompAdapter.notifyDataSetChanged();
        }
        else {
            loadClientsList();
        }
        if(listDSRStatuses!=null && listDSRStatuses.size()>0){
            // Remove all  value from list
            for (DSRStatusModel model :listDSRStatuses) {
                if(Integer.parseInt(model.dsrStatusID.trim())== -1) {
                    listDSRStatuses.remove(model);
                }
            }
            DSRStatusSpinnerAdapter dsrStatusAdapter = new DSRStatusSpinnerAdapter(getActivity(), listDSRStatuses);
            spinnerListStatusDSR.setAdapter(dsrStatusAdapter);
            dsrStatusAdapter.notifyDataSetChanged();
        }else {
            loadDSRStatusList();
        }
        if(listLoadingnPoint!=null && listLoadingnPoint.size()>0) {
            LocationPointAdapter loadingPointAdapter = new LocationPointAdapter(getActivity(), listLoadingnPoint);
            //spinnerListLoadingPoint.setAdapter(loadingPointAdapter);
            autocompleteTVLoadingPoint.setAdapter(loadingPointAdapter);
            loadingPointAdapter.notifyDataSetChanged();
        }
        else {
            loadLoadingPointList();
        }
        if(listDestinationPoint!=null && listDestinationPoint.size()>0) {
            LocationPointAdapter destinationPointAdapter = new LocationPointAdapter(getActivity(), listDestinationPoint);
            //spinnerListDestinationPoint.setAdapter(destinationPointAdapter);
            autocompleteTVDestinationPoint.setAdapter(destinationPointAdapter);
            destinationPointAdapter.notifyDataSetChanged();
        }
        else {
            loadDestinationPointList();
        }
        loadCustomsPoints();

    }

    private void loadCustomsPoints() {

        listCustomsPoint = new ArrayList<>();
        CustomsPointModel customsPointModel = new CustomsPointModel();
        customsPointModel.pointId = "0" ;
        customsPointModel.pointName = "Select Customs" ;
        listCustomsPoint.add(customsPointModel) ;
        final DSRCustomsPointAdapter dsrCustomsPointAdapter = new DSRCustomsPointAdapter(getActivity(), listCustomsPoint);
        spinnerListCustoms.setAdapter(dsrCustomsPointAdapter);
        dsrCustomsPointAdapter.notifyDataSetChanged();

        String url  =   Constants.BASE_URL+"custom_point_list";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                try {
                    int selectedIndex= -1 ;
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("custom_point_id");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            CustomsPointModel cmodel = new CustomsPointModel();
                            String pointId =  jobject.getString("custom_id");
                            cmodel.pointId = pointId;
                            String pointName =   jobject.getString("custom_point");
                            cmodel.pointName = pointName ;
                            if(dsrGeneralDetailModel.customs !=null && dsrGeneralDetailModel.customs.trim().equals(pointName.trim()))
                            {
                                selectedIndex = i ;
                            }
                            listCustomsPoint.add(cmodel);
                        }


                        spinnerListCustoms.setAdapter(dsrCustomsPointAdapter);
                        if(selectedIndex!=-1)
                        {
                            spinnerListCustoms.setSelection(selectedIndex+1);
                        }
                        dsrCustomsPointAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {}
        });

    }

    private void setupGUI() {
        /** Client Company List **/
        spinnerListClientComp = (Spinner) view.findViewById(R.id.spinnerListClientComp);
        spinnerListClientComp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    String compCode = listClients.get(position).companyCode;
                    //update value in Local
                    dsrGeneralDetailModel.clientCompId = listClients.get(position).companyId;
                    dsrGeneralDetailModel.clientCompName = listClients.get(position).companyName;

                    String dsrRefNumber = "";
                    switch (action) {
                        case "add":
                            Calendar calendar = Calendar.getInstance();
                            String dateTimeDSR = appGlobal.getDateFormat().format(calendar.getTime());
                            String[] dt = dateTimeDSR.split(" ");
                            etDSRDate.setText(dt[0]);
                            etDSRTime.setText(dt[1]);
                            int monthCount = calendar.get(Calendar.MONTH) + 1;
                            String monthStr = (monthCount > 9) ? String.valueOf(monthCount) : "0" + monthCount;
                            dsrRefNumber = compCode + monthStr+ (calendar.getTimeInMillis()%10000000) ;//AppGlobal.generateRandomString(4); //+ getNextClientDSRCount(listClients.get(position).companyName);
                            etDsrRefNum.setText(dsrRefNumber);

                            // Update DSR Date Time , month
                            dsrGeneralDetailModel.dsrDateTime = dateTimeDSR;
                            dsrGeneralDetailModel.month = monthStr;
                            break;
                        case "edit":
                            dsrRefNumber = dsrGeneralDetailModel.dsrRefNumber;
                            //dsrRefNumber = dsrRefNumber.replaceFirst("^.{0,3}", compCode);
                            break;
                    }

                    // Update DSR Ref Number
                    dsrGeneralDetailModel.dsrRefNumber = dsrRefNumber;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /** Auto-Generated DSR Ref Number **/
        etDsrRefNum = (EditText) view.findViewById(R.id.etDsrRefNum);

        /** DSR Date/Time **/
        ivDSRDate = (ImageView) view.findViewById(R.id.ivDSRDate);
        ivDSRTime = (ImageView) view.findViewById(R.id.ivDSRTime);
        etDSRDate = (EditText) view.findViewById(R.id.etDSRDate);
        etDSRTime = (EditText) view.findViewById(R.id.etDSRTime);

        /** Client Company Ref Number **/
        etClientCompRefNum = (EditText) view.findViewById(R.id.etClientCompRefNum);

        /****************************
         *  Loading Date/Time
         ****************************/
        etLoadingDate = (EditText) view.findViewById(R.id.etLoadingDate);
        etLoadingTime = (EditText) view.findViewById(R.id.etLoadingTime);

        // Fill Current Date Time
        tvNowDateTime =  (TextView)view.findViewById(R.id.tvNowDateTime);
        tvNowDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance() ;
                calendar.getTime();
                String dateTimeLoading = appGlobal.getDateFormat().format(calendar.getTime());
                String[] dt = dateTimeLoading.split(" ");
                etLoadingDate.setText(dt[0]);
                etLoadingTime.setText(dt[1]);
                calendar.add(Calendar.DATE, 7);

                String dateTimeETA = appGlobal.getDateFormat().format(calendar.getTime());
                dt = dateTimeETA.split(" ");
                etTT.setText("7");
                etETADate.setText(dt[0]);
                etETATime.setText(dt[1]);

                // update Loading Date Time ,date time ETa And days TT
                dsrGeneralDetailModel.loadingDate = dateTimeLoading;
                dsrGeneralDetailModel.eta = dateTimeETA;
                dsrGeneralDetailModel.tt = "7";
            }
        });

        //Loading Date
        ivLoadingDate = (ImageView) view.findViewById(R.id.ivLoadingDate);
        ivLoadingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog("loading");
            }
        });

        // Loading Time
        ivLoadingTime = (ImageView) view.findViewById(R.id.ivLoadingTime);
        ivLoadingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog("loading") ;
            }
        });

        /****************************/

        /**Add Loading point & destination Points**/

        ivAddDestinationPoint = (ImageView) view.findViewById(R.id.ivAddDestinationPoint);
        ivAddDestinationPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddLocationPointPopup("destination");
            }
        });
        ivAddLoadingPoint = (ImageView) view.findViewById(R.id.ivAddLoadingPoint);
        ivAddLoadingPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddLocationPointPopup("loading");
            }
        });


        /**  DSR ETA **/
        etETADate = (EditText) view.findViewById(R.id.etETADate);
        etETATime = (EditText) view.findViewById(R.id.etETATime);
        ivETA = (ImageView) view.findViewById(R.id.ivETA);
        ivETATime = (ImageView) view.findViewById(R.id.ivETATime);
        ivETA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog("eta");
            }
        });

        /** DSR TT **/
        etTT = (EditText) view.findViewById(R.id.etTT);
        etTT.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try
                {
                    if(!s.toString().isEmpty()) {
                        SimpleDateFormat sff = appGlobal.getDateFormat();
                        int tt = Integer.parseInt(s.toString());
                        String dateTimeLoadingStr = etLoadingDate.getText().toString() + " " + etLoadingTime.getText().toString();
                        Date dateTimeLoading = sff.parse(dateTimeLoadingStr);
                        Calendar calLoadingDT = Calendar.getInstance();
                        calLoadingDT.setTime(dateTimeLoading);
                        calLoadingDT.add(Calendar.DATE, tt);
                        String dtETAStr = sff.format(calLoadingDT.getTime());
                        String[] dt = dtETAStr.split(" ");

                        etETADate.setText(dt[0]);
                        if (dt.length > 1) {
                            etETATime.setText(dt[1]);
                        }

                        dsrGeneralDetailModel.eta = dtETAStr;
                        dsrGeneralDetailModel.tt = s.toString();
                    } else {
                        etETADate.setText("");
                        etETATime.setText("");
                        dsrGeneralDetailModel.eta = "";
                        dsrGeneralDetailModel.tt = s.toString();
                    }
                }
                catch (ParseException ex){}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /** Loading Point **/
        etLoadingPointCity = (EditText)view.findViewById(R.id.etLoadingPointCity);
        etLoadingPointCountry = (EditText)view.findViewById(R.id.etLoadingPointCountry);
        etDestinationPointCity = (EditText)view.findViewById(R.id.etDestinationPointCity);
        etDestinationPointCountry = (EditText)view.findViewById(R.id.etDestinationPointCountry);


        autocompleteTVLoadingPoint = (AutoCompleteTextView) view.findViewById(R.id.autocompleteTVLoadingPoint);
        autocompleteTVLoadingPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // if (position > 0) {
                // Update Loading Point model data
                LocationPointModel locationPointModel =  (LocationPointModel)((ListView) adapterView).getAdapter().getItem(position);
                dsrGeneralDetailModel.loadingPointId = locationPointModel.locId;
                dsrGeneralDetailModel.loading_point = locationPointModel.locPoint;
                dsrGeneralDetailModel.loading_city = locationPointModel.city;
                dsrGeneralDetailModel.loading_country = locationPointModel.country;
                etLoadingPointCity.setText(locationPointModel.city);
                etLoadingPointCountry.setText(locationPointModel.country);
                //  }
            }
        });

        /** Destination Point **/
        autocompleteTVDestinationPoint = (AutoCompleteTextView) view.findViewById(R.id.autocompleteTVDestinationPoint);
        autocompleteTVDestinationPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //  if (position > 0) {
                // Update Destination Point model data
                LocationPointModel locationPointModel = (LocationPointModel) ((ListView) adapterView).getAdapter().getItem(position);
                dsrGeneralDetailModel.destinatioPointId = locationPointModel.locId;
                dsrGeneralDetailModel.destination_point = locationPointModel.locPoint;
                dsrGeneralDetailModel.destination_city = locationPointModel.city;
                dsrGeneralDetailModel.destination_country = locationPointModel.country;
                etDestinationPointCity.setText(locationPointModel.city);
                etDestinationPointCountry.setText(locationPointModel.country);
                //    }
            }
        });


        /** Customs **/
        // etCustoms = (EditText) view.findViewById(R.id.etCustoms);

        /** Date Reached Custom **/
        etDateReachedCustom = (EditText) view.findViewById(R.id.etDateReachedCustom);
        etTimeReachedCustom = (EditText) view.findViewById(R.id.etTimeReachedCustom);
        ivDateReachedCustom = (ImageView) view.findViewById(R.id.ivDateReachedCustom);
        ivDateReachedCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog("custom");
            }
        });
        ivTimeReachedCustom = (ImageView) view.findViewById(R.id.ivTimeReachedCustom);
        ivTimeReachedCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog("custom");
            }
        });

        /** DSR Status List **/
        ivAddStatus = (ImageView) view.findViewById(R.id.ivAddStatus);
        ivAddStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddStatusPopup();
            }
        });

        ivAddCustomPoint = (ImageView) view.findViewById(R.id.ivAddCustomPoint);
        ivAddCustomPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCustomPointsPopup();
            }
        });

        spinnerListStatusDSR = (Spinner) view.findViewById(R.id.spinnerListStatusDSR);

        spinnerListStatusDSR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    DSRStatusModel dsrStatusModel = listDSRStatuses.get(position);
                    // update DSR Status Id and name
                    dsrGeneralDetailModel.dsrStatusId = dsrStatusModel.dsrStatusID;
                    dsrGeneralDetailModel.dsr_status = dsrStatusModel.dsrStatusName;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinnerListCustoms = (Spinner) view.findViewById(R.id.spinnerListCustoms);
        spinnerListCustoms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    CustomsPointModel customsPointModel = listCustomsPoint.get(position);
                    // update DSR Status Id and name
                    dsrGeneralDetailModel.customs = customsPointModel.pointName;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        /**
         * ***********
         *  Remark
         * ************
         * **/
        etRemark = (EditText) view.findViewById(R.id.etRemark);


        /**
         * *****************
         * Next Button
         * *****************
         * **/

        btn_next_submit_dsr = (TextView) view.findViewById(R.id.btn_next_submit_dsr);
        btn_next_submit_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDSRGeneralData();
            }
        });

        btn_save_general_dsr = (TextView) view.findViewById(R.id.btn_save_general_dsr);
        btn_save_general_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDSRGeneralData();
                isSaving =  true ;
            }
        });

        lblStatusDSR = (TextView) view.findViewById(R.id.lblStatusDSR);

    }
    private void loadCountry(final CustomAutoCompleteTextView spinnerCountryList) {

        String url =  Constants.BASE_URL + "country";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                //Utils.hideLoadingPopup();
                try {

                    listCountry.clear();
                    CountryModel countryModel;
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("country_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            countryModel = new CountryModel();
                            countryModel.id = jobject.getString("id");
                            countryModel.name = jobject.getString("name");
                            countryModel.sortname = jobject.getString("sortname");
                            listCountry.add(countryModel);
                        }

                        AutoSearchAdapter adapter = new AutoSearchAdapter(getActivity(), listCountry);
                        spinnerCountryList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }


                }
                catch (Exception ex){ex.getMessage();}
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }


    private void loadDestinationPointList() {
    }

    private void loadLoadingPointList() {
    }

    private void loadDSRStatusList() {
    }

    private void loadClientsList() {
    }

    /**
     * ****************************************************
     * Open Adding Loading Point & Destination Point PopUp
     * ****************************************************
     **/
    private void showAddLocationPointPopup(final String point) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LinearLayout layout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        final EditText etPoint = new EditText(getActivity());
        etPoint.setHint("Enter "+(point.equals("loading")?"Loading":"Destination")+ " Point");

        AutoSearchAdapter adapter ;
        final CustomAutoCompleteTextView spinnerCountryList = new CustomAutoCompleteTextView(getActivity());
        final  CustomAutoCompleteTextView spinnerCityList = new CustomAutoCompleteTextView(getActivity());
        spinnerCountryList.setHint("Select "+(point.equals("loading")?"Loading":"Destination")+ " Country");
        spinnerCityList.setHint("Select "+(point.equals("loading")?"Loading":"Destination")+ " City");
        spinnerCityList.setThreshold(1);
        spinnerCountryList.setThreshold(1);

        spinnerCountryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                loadCities(((CountryModel)adapterView.getAdapter().getItem(i)).id);
            }

            private void loadCities(String id) {
                String url =  Constants.BASE_URL + "city";
                HashMap<String,String> hmap= new HashMap<>();//{"country_id":"101"}
                hmap.put("country_id", id);
                HttpPostRequest.doPost(getActivity(), url,new Gson().toJson(hmap), new HttpRequestCallback() {
                    @Override
                    public void response(String errorMessage, String responseData) {
                        //Utils.hideLoadingPopup();
                        try {
                            listCity.clear();
                            CountryModel countryModel;

                            JSONObject jobj = new JSONObject(responseData);
                            Boolean status = jobj.getBoolean("status");
                            if (status) {
                                JSONArray jsonArray = jobj.getJSONArray("city_list");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobject = jsonArray.getJSONObject(i);
                                    countryModel = new CountryModel();
                                    countryModel.id = jobject.getString("id");
                                    countryModel.name = jobject.getString("name");
                                    //countryModel.sortname = jobject.getString("sortname");
                                    listCity.add(countryModel);
                                }

                                AutoSearchAdapter adapter = new AutoSearchAdapter(getActivity(), listCity);
                                spinnerCityList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();


                            } else {
                                Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        }
                        catch (Exception ex){ex.getMessage();}
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
            }

        });


        listCountry= new ArrayList<>();
        adapter= new AutoSearchAdapter(getActivity(), listCountry);
        spinnerCountryList.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        listCity= new ArrayList<>();
        adapter = new AutoSearchAdapter(getActivity(), listCity);
        spinnerCityList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(Utils.isNetworkConnected(getActivity(),false)) {
            loadCountry(spinnerCountryList);
        }
        layout.addView(etPoint);
        layout.addView(spinnerCountryList);
        layout.addView(spinnerCityList);

        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setTitle((point.equals("loading")?"Loading":"Destination")+ " Location");

        alertDialogBuilder.setCancelable(false);

        // Setting Negative "Cancel" Button
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        // Setting Positive "OK" Button
        alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String lPoint = etPoint.getText().toString().trim();
                String countryName  = spinnerCountryList.getText().toString().trim() ;
                String cityName  =  spinnerCityList.getText().toString().trim();
                if(!lPoint.isEmpty() && !cityName.isEmpty() && !countryName.isEmpty() ) {
                    String lCity = cityName ;
                    String lCountry =  countryName;
                    requestAddLocationPoint(point, lPoint, lCity, lCountry);
                }
                else
                    Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
            }


        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        try {
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void showAddStatusPopup() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LinearLayout layout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        final EditText etStatus = new EditText(getActivity());
        etStatus.setHint("Enter Status Title");
        layout.addView(etStatus);
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setTitle("Add Status");

        alertDialogBuilder.setCancelable(false);

        // Setting Negative "Cancel" Button
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        // Setting Positive "OK" Button
        alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String status = etStatus.getText().toString().trim();

                if(!status.isEmpty())
                {
                    requestAddDSRtatus(status) ;
                }
                else
                    Toast.makeText(getActivity(), "Please enter status title!", Toast.LENGTH_SHORT).show();

            }


        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        try {
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddCustomPointsPopup() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LinearLayout layout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        final EditText etStatus = new EditText(getActivity());
        etStatus.setHint("Enter Custom Point");
        layout.addView(etStatus);
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setTitle("Add Custom");

        alertDialogBuilder.setCancelable(false);

        // Setting Negative "Cancel" Button
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        // Setting Positive "OK" Button
        alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String customPoint = etStatus.getText().toString().trim();

                if(!customPoint.isEmpty())
                {
                    requestAddCustomPoint(customPoint) ;
                }
                else
                    Toast.makeText(getActivity(), "Please enter Custom Point!", Toast.LENGTH_SHORT).show();

            }


        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        try {
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestAddCustomPoint(final String customPoint) {

        if(checkExistCustomPoint(customPoint))
        {
            return ;
        }


        String url= Constants.BASE_URL + "add_custom_point";
        final HashMap<String, String> data = new HashMap<>();
        data.put("custom_point",customPoint);

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        CustomsPointModel customsPointModel = new CustomsPointModel();
                        customsPointModel.pointName  =  customPoint ;
                        customsPointModel.pointId = jobj.getString("custom_point_id") ;
                        listCustomsPoint.add(customsPointModel);
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                    Utility.sendReport(getActivity(),"add_custom_point","Success",Utils.newGson().toJson(data),responseData);
                } catch (Exception e) {
                    Utility.sendReport(getActivity(),"add_custom_point",e.getMessage(),Utils.newGson().toJson(data),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    private void requestAddDSRtatus(final String statusStr) {
        if(checkExistStatus(statusStr))
        {
            return ;
        }
        // Utils.showLoadingPopup(getActivity());

        String url= Constants.BASE_URL + "add_dsr_status";
        HashMap<String, String> data = new HashMap<>();
        data.put("status_name",statusStr);

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                // Utils.hideLoadingPopup();
                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        DSRStatusModel dsrStatusModel = new DSRStatusModel();
                        dsrStatusModel.dsrStatusName  =  statusStr ;
                        dsrStatusModel.dsrStatusID = jobj.getString("dsr_status_id") ;
                        appGlobal.listDSRStatuses.add(dsrStatusModel);
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error getting Data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    /**
     * ****************************************************
     * Request for Adding Loading Point & Destination Point
     * ****************************************************
     **/
    private void requestAddLocationPoint(final String point, final String lPoint , final String city , final String country ) {

        // Utils.showLoadingPopup(getActivity());
        if(checkExistLocationPoint(point,lPoint))
        {
            return ;
        }
        String url="";
        HashMap<String, String> data = new HashMap<>();
        switch(point)
        {
            case "loading":
                url= Constants.BASE_URL + "add_loding_point";
                data.put("loding_point",lPoint);
                data.put("loding_city",city);
                data.put("loading_country",country);
                data.put("status","1");
                break ;

            case "destination" :
                url= Constants.BASE_URL + "add_destination_point";
                data.put("destination_point",lPoint);
                data.put("destination_city",city);
                data.put("destination_country",country);
                data.put("status","1");
                break ;
        }



        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                // Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        LocationPointModel locationPointModel = new LocationPointModel();
                        locationPointModel.locPoint  =  lPoint ;
                        locationPointModel.city = city ;
                        locationPointModel.country =  country ;
                        switch(point)
                        {

                            case "loading" :
                                locationPointModel.locId = jobj.getString("loding_point_id") ;
                                appGlobal.listLoadingnPoint.add(locationPointModel);
                                break ;

                            case "destination" :
                                locationPointModel.locId = jobj.getString("destination_point_id") ;
                                appGlobal.listDestinationPoint.add(locationPointModel);
                                break ;
                        }

                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error getting Data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }

    /**
     * ************************************************************
     * Check Whether Loading Point & Destination Point Exist or not.
     * ************************************************************
     **/

    private boolean checkExistLocationPoint(String point, String lPoint) {

        ArrayList<LocationPointModel> listLocationPoint ;

        listLocationPoint = point.equals("loading")?listLoadingnPoint : listDestinationPoint ;

        for (LocationPointModel lPointModel:listLocationPoint) {
            if(lPointModel.locPoint.toLowerCase().trim().equals(lPoint.toLowerCase().trim()))
            {
                Toast.makeText(getActivity(),(point.equals("loading")?"Loading":"Destination")+" point already exists!", Toast.LENGTH_SHORT).show();
                return true ;
            }
        }
        return false ;

    }

    private boolean checkExistStatus(String status) {

        ArrayList<DSRStatusModel> listLocationPoint ;

        listLocationPoint = listDSRStatuses ;

        for (DSRStatusModel statusModel:listLocationPoint) {
            if(statusModel.dsrStatusName.toLowerCase().trim().equals(status.toLowerCase().trim()))
            {
                Toast.makeText(getActivity(),"Status already exists!", Toast.LENGTH_SHORT).show();
                return true ;
            }
        }
        return false ;

    }

    private boolean checkExistCustomPoint(String custom) {
        for (CustomsPointModel pointModel:this.listCustomsPoint) {
            if(pointModel.pointName.toLowerCase().trim().equals(custom.toLowerCase().trim()))
            {
                Toast.makeText(getActivity(),"Custom Point already exists!", Toast.LENGTH_SHORT).show();
                return true ;
            }
        }
        return false ;

    }

    private void updateDSRGeneralData() {
        boolean isValidationSuccess = validationDSRGeneralData();
        if (isValidationSuccess) {
            dsrDetailsObj.isValidationSuccess = isValidationSuccess;
            switch (action) {
                case "add":
                    dsrDetailsObj.indexActivatedView = 1;
                    dsrDetailsObj.doAction();
                    dsrDetailsObj.dsrGeneralDetailModel = dsrGeneralDetailModel;
                    break;

                case "edit":
                    updateDSRGeneralDataOnServer();
                    break;
            }


        }

        /** update dsr General data into Global in activity **/
        dsrDetailsObj.dsrGeneralDetailModel = dsrGeneralDetailModel;


    }

    private void updateDSRGeneralDataOnServer() {

        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL + "update_general_dsr";

        HashMap<String, String> data = new HashMap<>();
        data.put("employee_id",appGlobal.userId);
        data.put("type",appGlobal.userType);
        data.put("dsr_id", dsrGeneralDetailModel.dsrId != null ? dsrGeneralDetailModel.dsrId : "");
        data.put("client", dsrGeneralDetailModel.clientCompId != null ? dsrGeneralDetailModel.clientCompId : "");
        data.put("dsr_ref_no", dsrGeneralDetailModel.dsrRefNumber != null ? dsrGeneralDetailModel.dsrRefNumber : "");
        data.put("dsr_dates_time", dsrGeneralDetailModel.dsrDateTime != null ? dsrGeneralDetailModel.dsrDateTime : "");
        data.put("client_ref_no", dsrGeneralDetailModel.clientRefNo != null ? dsrGeneralDetailModel.clientRefNo : "");
        data.put("loading_date", dsrGeneralDetailModel.loadingDate != null ? dsrGeneralDetailModel.loadingDate : "");
        data.put("eta", dsrGeneralDetailModel.eta != null ? dsrGeneralDetailModel.eta : "");
        data.put("tt", dsrGeneralDetailModel.tt != null ? dsrGeneralDetailModel.tt : "");
        data.put("loading_point", dsrGeneralDetailModel.loadingPointId != null ? dsrGeneralDetailModel.loadingPointId : "");
        data.put("loading_city", dsrGeneralDetailModel.loading_city != null ? dsrGeneralDetailModel.loading_city : "");
        data.put("loading_country", dsrGeneralDetailModel.loading_country != null ? dsrGeneralDetailModel.loading_country : "");
        data.put("destination_point", dsrGeneralDetailModel.destinatioPointId != null ? dsrGeneralDetailModel.destinatioPointId : "");
        data.put("destination_city", dsrGeneralDetailModel.destination_city != null ? dsrGeneralDetailModel.destination_city : "");
        data.put("destination_country", dsrGeneralDetailModel.destination_country != null ? dsrGeneralDetailModel.destination_country : "");
        data.put("customs", dsrGeneralDetailModel.customs != null ? dsrGeneralDetailModel.customs : "");
        data.put("date_reached_customs", dsrGeneralDetailModel.date_reached_custom != null ? dsrGeneralDetailModel.date_reached_custom : "");
        data.put("dsr_status", dsrGeneralDetailModel.dsrStatusId != null ? dsrGeneralDetailModel.dsrStatusId : "");
        data.put("status", dsrGeneralDetailModel.status_str != null ? dsrGeneralDetailModel.status_str : "");
        data.put("remark", dsrGeneralDetailModel.remark != null ? dsrGeneralDetailModel.remark : "");
        data.put("no_of_truck", dsrGeneralDetailModel.num_truck != null ? dsrGeneralDetailModel.num_truck : "");
        data.put("month", dsrGeneralDetailModel.month != null ? dsrGeneralDetailModel.month : "");

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        dsrDetailsObj.dsrGeneralDetailModel = dsrGeneralDetailModel;

                        if(isSaving)
                        {
                           /* dsrDetailsObj.indexActivatedView = 0;
                            dsrDetailsObj.doAction();*/
                            isSaving = false ;
                            requestUpdateDSRNotification(dsrGeneralDetailModel.dsrId);
                            requestUpdateDSRNotificationForSelected(dsrGeneralDetailModel.dsrId);
                            Toast.makeText(getActivity(), "DSR Saved Successfully", Toast.LENGTH_SHORT).show();

                        }else {
                            dsrDetailsObj.indexActivatedView = 1;
                            dsrDetailsObj.doAction();
                            Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                        }




                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error getting Data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }

    private void requestUpdateDSRNotification(String dsrId) {

        try {
            DSRGeneralDetailModel dsrGeneralDetailModel = dsrDetailsObj.dsrGeneralDetailModel;
            JSONObject root = new JSONObject();

            root.put("dsr_id",dsrId );
            root.put("employee_id", appGlobal.userId);
            root.put("employee_type",appGlobal.userType);
            root.put("method_name", IMethodNotification.UPDATE_DSR);
            root.put("client_id", dsrGeneralDetailModel.clientCompId!=null?dsrGeneralDetailModel.clientCompId:"");

            /*******************************
             * Send Notification on Add DSR
             * *****************************/

            NotificationSendHandler sendHandler = new NotificationSendHandler(getActivity());
            sendHandler.sendNotification(IMethodNotification.UPDATE_DSR,root.toString(),IMethodNotification.CLIENT_USER);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void requestUpdateDSRNotificationForSelected(String dsrId) {

        try {

            JSONObject root = new JSONObject();

            root.put("dsr_id",dsrId );
            root.put("employee_id", appGlobal.userId);
            root.put("employee_type",appGlobal.userType);
            root.put("method_name", IMethodNotification.UPDATE_DSR);

            /*******************************
             * Send Notification on Add DSR
             * *****************************/

            NotificationSendHandler sendHandler = new NotificationSendHandler(getActivity());
            sendHandler.sendNotification(IMethodNotification.UPDATE_DSR,root.toString(),IMethodNotification.ADMIN_USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String msg;

    public boolean validationDSRGeneralData() {

        try {
            switch (action) {
                case "add":
                    if (spinnerListClientComp.getSelectedItemPosition() <= 0) {
                        msg = "Please Select Client.";
                        dsrDetailsObj.msg_fail_validation = msg;
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    String clientCompRefNum = etClientCompRefNum.getText().toString().trim();
                    if (clientCompRefNum.isEmpty()) {
                        dsrGeneralDetailModel.clientRefNo = clientCompRefNum;
                    } else {
                        dsrGeneralDetailModel.clientRefNo = clientCompRefNum;
                    }

                    if (etLoadingDate.getText().toString().isEmpty()) {
                        msg = "Please Select Loading Date.";
                        dsrDetailsObj.msg_fail_validation = msg;
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    // if (autocompleteTVLoadingPoint.getSelectedItemPosition() <= 0) {
                    if (dsrGeneralDetailModel.loading_point==null || dsrGeneralDetailModel.loading_point.isEmpty()  || autocompleteTVLoadingPoint.getText().toString().trim().isEmpty()) {
                        msg = "Please Select Loading Point.";
                        dsrDetailsObj.msg_fail_validation = msg;
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if (dsrGeneralDetailModel.destination_point==null || dsrGeneralDetailModel.destination_point.isEmpty() || autocompleteTVDestinationPoint.getText().toString().trim().isEmpty()) {
                        msg = "Please Select Destination Point.";
                        dsrDetailsObj.msg_fail_validation = msg;
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    break;
            }

            if (Integer.parseInt(etTT.getText().toString()) <= 0) {
                msg = "Please Enter TT Value";
                dsrDetailsObj.msg_fail_validation = msg;
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                return false;
            }

            dsrGeneralDetailModel.remark = etRemark.getText().toString().trim();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }

    private String getNextClientDSRCount(String compId) {

        int count = 0;
        ArrayList<DSRModel> dsrArrList = appGlobal.dsrArrList;
        if (dsrArrList != null) {
            for (int indexDSR = 0; indexDSR < dsrArrList.size(); indexDSR++) {
                String clientComp = dsrArrList.get(indexDSR).clientCompName;
                if (clientComp.equals(compId)) {
                    count++;
                }
            }
        }
        String nextCountStr = String.valueOf(++count);
        StringBuilder builder = new StringBuilder();
        int lenNextCountStr = nextCountStr.length();
        for (int i = lenNextCountStr; i < 4; i++) {
            builder.append("0");
        }
        builder.append(nextCountStr);

        return builder.toString();
    }

    private void uIFeaturesSetting() {

        /**********************************
         * Common Settings for Add & Edit
         * *********************************/
        etDsrRefNum.setEnabled(false);
        etDsrRefNum.setFocusable(false);

        etDSRDate.setFocusable(false);
        etDSRDate.setEnabled(false);
        etDSRTime.setFocusable(false);
        etDSRTime.setEnabled(false);

        etDateReachedCustom.setEnabled(false);
        etDateReachedCustom.setFocusable(false);
        etTimeReachedCustom.setEnabled(false);
        etTimeReachedCustom.setFocusable(false);

        etLoadingDate.setEnabled(false);
        etLoadingDate.setFocusable(false);
        etLoadingTime.setEnabled(false);
        etLoadingTime.setFocusable(false);

        etETADate.setEnabled(false);
        etETADate.setFocusable(false);
        etETATime.setEnabled(false);
        etETATime.setFocusable(false);

        switch (action) {
            case "edit":
                /**************************************/
                spinnerListClientComp.setEnabled(false);
                spinnerListClientComp.setFocusable(false);

                etClientCompRefNum.setEnabled(false);
                etClientCompRefNum.setFocusable(false);

                spinnerListCustoms.setEnabled(false);
                spinnerListCustoms.setFocusable(false);

                tvNowDateTime.setVisibility(View.GONE);
                ivLoadingDate.setVisibility(View.GONE);
                ivLoadingTime.setVisibility(View.GONE);

                ivDSRDate.setVisibility(View.GONE);
                ivDSRTime.setVisibility(View.GONE);

                ivAddStatus.setVisibility(View.VISIBLE);

                ivAddLoadingPoint.setVisibility(View.GONE);

                ivAddDestinationPoint.setVisibility(View.GONE);

//                spinnerListLoadingPoint.setEnabled(false);
//                spinnerListLoadingPoint.setFocusable(false);

                autocompleteTVLoadingPoint.setEnabled(false);
                autocompleteTVLoadingPoint.setFocusable(false);

                autocompleteTVDestinationPoint.setEnabled(false);
                autocompleteTVDestinationPoint.setFocusable(false);

                etTT.setEnabled(false);
                etTT.setFocusable(false);

                lblStatusDSR.setVisibility(View.VISIBLE);
                spinnerListStatusDSR.setVisibility(View.VISIBLE);
                ivDateReachedCustom.setVisibility(View.GONE);
                ivTimeReachedCustom.setVisibility(View.GONE);
                btn_save_general_dsr.setVisibility(View.VISIBLE);

                /**************************************/
                break;

            case "add":
                spinnerListClientComp.setEnabled(true);
                spinnerListClientComp.setFocusable(true);

//                etCustoms.setEnabled(true);
//                etCustoms.setFocusable(true);
                spinnerListCustoms.setEnabled(true);
                spinnerListCustoms.setFocusable(true);

                seDSRStatusSelection(null);

               /* spinnerListStatusDSR.setEnabled(false);
                spinnerListStatusDSR.setFocusable(false);*/

                /**************************************/
                autocompleteTVLoadingPoint.setEnabled(true);
                autocompleteTVLoadingPoint.setFocusable(true);
                autocompleteTVDestinationPoint.setEnabled(true);
                autocompleteTVDestinationPoint.setFocusable(true);


                ivLoadingDate.setVisibility(View.VISIBLE);
                ivLoadingTime.setVisibility(View.VISIBLE);
                tvNowDateTime.setVisibility(View.VISIBLE);
                ivDSRDate.setVisibility(View.GONE);
                ivDSRTime.setVisibility(View.GONE);
                ivAddStatus.setVisibility(View.GONE);
                ivAddLoadingPoint.setVisibility(View.VISIBLE);
                ivAddDestinationPoint.setVisibility(View.VISIBLE);
                etTT.setEnabled(true);
                etTT.setFocusable(true);

                lblStatusDSR.setVisibility(View.GONE);
                spinnerListStatusDSR.setVisibility(View.GONE);
                ivDateReachedCustom.setVisibility(View.VISIBLE);
                ivTimeReachedCustom.setVisibility(View.GONE);
                btn_save_general_dsr.setVisibility(View.GONE);
                /**************************************/
                break;
        }

    }

    private void bindDataToGUI() {

        if (dsrGeneralDetailModel != null) {
            try {
                String[] dt = null;

                setClientSelection(dsrGeneralDetailModel.clientCompName);
                etDsrRefNum.setText(dsrGeneralDetailModel.dsrRefNumber==null?"":dsrGeneralDetailModel.dsrRefNumber);

                if(dsrGeneralDetailModel.dsrDateTime!=null) {
                    dt = dsrGeneralDetailModel.dsrDateTime.split(" ");
                    etDSRDate.setText(dt[0]);
                    if (dt.length > 1) {
                        etDSRTime.setText(dt[1]);
                    }
                }

                etClientCompRefNum.setText(dsrGeneralDetailModel.clientRefNo==null?"N/A":dsrGeneralDetailModel.clientRefNo);

                if (dsrGeneralDetailModel.loadingDate != null) {
                    dt = dsrGeneralDetailModel.loadingDate.split(" ");
                    etLoadingDate.setText(dt[0]);
                    if (dt.length > 1) {
                        etLoadingTime.setText(dt[1]);
                    }
                }

                if (dsrGeneralDetailModel.eta != null) {
                    dt = dsrGeneralDetailModel.eta.split(" ");
                    etETADate.setText(dt[0]);
                    if (dt.length > 1) {
                        etETATime.setText(dt[1]);
                    }
                }

                etTT.setText(dsrGeneralDetailModel.tt == null ? "" : dsrGeneralDetailModel.tt);
                setLoadingPointSelection(dsrGeneralDetailModel.loading_point);
                setDestinationPointSelection(dsrGeneralDetailModel.destination_point);

                // etCustoms.setText(dsrGeneralDetailModel.customs == null ? "" : dsrGeneralDetailModel.customs);

                if (dsrGeneralDetailModel.date_reached_custom != null) {
                    dt = dsrGeneralDetailModel.date_reached_custom.split(" ");
                    etDateReachedCustom.setText(dt[0]);
                    if (dt.length > 1) {
                        etTimeReachedCustom.setText(dt[1]);
                    }
                }

                seDSRStatusSelection(dsrGeneralDetailModel.dsr_status);
                etRemark.setText(dsrGeneralDetailModel.remark == null ? "" : dsrGeneralDetailModel.remark);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    private void seDSRStatusSelection(String dsr_status) {
        if (dsr_status != null) {
            for (int indexDP = 0; indexDP < listDSRStatuses.size(); indexDP++) {
                if (dsr_status.equals(listDSRStatuses.get(indexDP).dsrStatusName)) {
                    spinnerListStatusDSR.setSelection(indexDP);
                    break;
                }
            }
        } else {
            for (int indexDP = 0; indexDP < listDSRStatuses.size(); indexDP++) {
                if ("new".equals(listDSRStatuses.get(indexDP).dsrStatusName.toLowerCase())) {
                    spinnerListStatusDSR.setSelection(indexDP);
                    if(spinnerListStatusDSR.getVisibility()== View.GONE)
                    {
                        dsrGeneralDetailModel.dsrStatusId = listDSRStatuses.get(indexDP).dsrStatusID ;
                        dsrGeneralDetailModel.dsr_status = listDSRStatuses.get(indexDP).dsrStatusName ;
                    }
                    break;
                }
            }
            //spinnerListStatusDSR.setSelection(1);
        }

    }

    private void setDestinationPointSelection(String destination_point) {
        if (destination_point != null) {
            for (int indexDP = 0; indexDP < listDestinationPoint.size(); indexDP++) {
                if (destination_point.equals(listDestinationPoint.get(indexDP).locPoint)) {
                    autocompleteTVDestinationPoint.setText(destination_point);
                    LocationPointModel locationPointModel = listDestinationPoint.get(indexDP);
                    dsrGeneralDetailModel.destinatioPointId =  locationPointModel.locId ;
                    dsrGeneralDetailModel.destination_point =  locationPointModel.locPoint ;
                    dsrGeneralDetailModel.destination_city =  locationPointModel.city ;
                    dsrGeneralDetailModel.destination_country = locationPointModel.country ;
                    etDestinationPointCity.setText(locationPointModel.city);
                    etDestinationPointCountry.setText(locationPointModel.country);
                    break;
                }
            }
        } else {
            autocompleteTVDestinationPoint.setSelection(0);
        }

    }

    private void setLoadingPointSelection(String loading_point) {
        if (loading_point != null) {
            for (int indexDP = 0; indexDP < listLoadingnPoint.size(); indexDP++) {
                if (loading_point.equals(listLoadingnPoint.get(indexDP).locPoint)) {
                    autocompleteTVLoadingPoint.setText(loading_point);
                    LocationPointModel locationPointModel = listLoadingnPoint.get(indexDP);
                    dsrGeneralDetailModel.loadingPointId =  locationPointModel.locId ;
                    dsrGeneralDetailModel.loading_point =  locationPointModel.locPoint ;
                    dsrGeneralDetailModel.loading_city =  locationPointModel.city ;
                    dsrGeneralDetailModel.loading_country = locationPointModel.country ;
                    etLoadingPointCity.setText(locationPointModel.city);
                    etLoadingPointCountry.setText(locationPointModel.country);

                    break;
                }
            }
        }

    }

    private void setClientSelection(String compName) {
        if (compName != null) {
            for (int indexDP = 0; indexDP < listClients.size(); indexDP++) {
                if (compName.equals(listClients.get(indexDP).companyName)) {
                    spinnerListClientComp.setSelection(indexDP);
                    ClientModel clientModel =listClients.get(indexDP) ;
                    dsrGeneralDetailModel.clientCompId = clientModel.companyId ;
                    dsrGeneralDetailModel.clientCompName = clientModel.companyName ;
                    break;
                }
            }
        } else {
            spinnerListClientComp.setSelection(0);
        }
    }

    private void showDateDialog(final String field) {

        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.GLCalenderStyle, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String[] dt = null;
                switch (field) {
                    case "loading": {
                        Calendar loadingPointCalender = Calendar.getInstance();
                        loadingPointCalender.set(Calendar.YEAR, year);
                        loadingPointCalender.set(Calendar.MONTH, monthOfYear);
                        loadingPointCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String dateTimeLoading = appGlobal.getOnlyDateFormat().format(loadingPointCalender.getTime());

                        etLoadingDate.setText(dateTimeLoading);
                        String loadingDateTime  = etLoadingDate.getText().toString()+" "+etLoadingTime.getText().toString();
                        dsrGeneralDetailModel.loadingDate = loadingDateTime;
                        if(etLoadingTime.getText().length()>0) {
                            try {
                                Date date = appGlobal.getDateFormat().parse(loadingDateTime);
                                loadingPointCalender.setTime(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            loadingPointCalender.add(Calendar.DATE, 7);

                            String dateTimeETA = appGlobal.getDateFormat().format(loadingPointCalender.getTime());
                            dt = dateTimeETA.split(" ");
                            etTT.setText("7");
                            etETADate.setText(dt[0]);
                            etETATime.setText(dt[1]);

                            // update Loading Date Time ,date time ETa And days TT

                            dsrGeneralDetailModel.eta = dateTimeETA;
                            dsrGeneralDetailModel.tt = "7";
                        }
                        else{showTimeDialog("loading");}
                    }
                    break;
                    case "eta": {
                        try {
                            String dateTimeEtaStr = etLoadingDate.getText().toString() + " " + etLoadingTime.getText().toString();
                            Date dateTimeETa = appGlobal.getDateFormat().parse(dateTimeEtaStr);
                            Calendar etaCalendar = Calendar.getInstance();
                            etaCalendar.set(Calendar.YEAR, year);
                            etaCalendar.set(Calendar.MONTH, monthOfYear);
                            etaCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            String dateTimeETA = appGlobal.getDateFormat().format(etaCalendar.getTime());
                            dt = dateTimeETA.split(" ");

                            long millis = etaCalendar.getTime().getTime() - dateTimeETa.getTime();
                            long daysTT = millis / (1000 * 60 * 60 * 24);

                            etTT.setText(String.valueOf(daysTT));
                            etETADate.setText(dt[0]);
                            //etETATime.setText(dt[1]);

                            // update date time ETa And days TT
                            dsrGeneralDetailModel.eta = dateTimeETA;
                            dsrGeneralDetailModel.tt = String.valueOf(daysTT);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    break;

                    case "custom": {
                        Calendar loadingPointCalender = Calendar.getInstance();

                        loadingPointCalender.set(Calendar.YEAR, year);
                        loadingPointCalender.set(Calendar.MONTH, monthOfYear);
                        loadingPointCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String dateTimeReachedCustom = appGlobal.getOnlyDateFormat().format(loadingPointCalender.getTime());
                        etDateReachedCustom.setText(dateTimeReachedCustom);
                        // update Date Reached Customs
                        //if(etTimeReachedCustom.getText().length()>0)
                        dsrGeneralDetailModel.date_reached_custom = etDateReachedCustom.getText().toString()+" "+etTimeReachedCustom.getText().toString(); // dateTimeReachedCustom;
                        //else showTimeDialog("custom");
                    }

                    break;
                }


            }
        }, year, month, day);

        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();

    }

    private void showTimeDialog(final String field) {
        final Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog  timePickerDialog = new TimePickerDialog(getActivity(), R.style.GLCalenderStyle, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                switch (field) {
                    case "loading": {

                        Calendar loadingPointCalender = Calendar.getInstance();
                        loadingPointCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        loadingPointCalender.set(Calendar.MINUTE, minute);
                        String dateTimeLoading = appGlobal.getOnlyTimeFormat().format(loadingPointCalender.getTime());
                        etLoadingTime.setText(dateTimeLoading);
                        String loadingDateTime  = etLoadingDate.getText().toString()+" "+etLoadingTime.getText().toString();
                        dsrGeneralDetailModel.loadingDate = loadingDateTime;
                        if(etLoadingDate.getText().length()>0) {
                            try {
                                Date date = appGlobal.getDateFormat().parse(loadingDateTime);
                                loadingPointCalender.setTime(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            loadingPointCalender.add(Calendar.DATE, 7);
                            String dateTimeETA = appGlobal.getDateFormat().format(loadingPointCalender.getTime());
                            String[] dt = dateTimeETA.split(" ");
                            etTT.setText("7");
                            etETADate.setText(dt[0]);
                            etETATime.setText(dt[1]);
                            // update Loading Date Time ,date time ETa And days TT
                            dsrGeneralDetailModel.eta = dateTimeETA;
                            dsrGeneralDetailModel.tt = "7";
                        }
                        else { showDateDialog("loading"); }
                    }
                    break;
                    case "custom": {
                        Calendar loadingPointCalender = Calendar.getInstance();
                        loadingPointCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        loadingPointCalender.set(Calendar.MINUTE, minute);
                        String timeReachedCustom = appGlobal.getOnlyTimeFormat().format(loadingPointCalender.getTime());
                        etTimeReachedCustom.setText(timeReachedCustom);
                        // update Date Reached Customs
                        if(etDateReachedCustom.getText().length()>0)
                            dsrGeneralDetailModel.date_reached_custom = etDateReachedCustom.getText().toString()+" "+etTimeReachedCustom.getText().toString(); // dateTimeReachedCustom;
                        else showDateDialog("custom");
                    }

                    break;
                }
            }
        },hour,minute,false);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
