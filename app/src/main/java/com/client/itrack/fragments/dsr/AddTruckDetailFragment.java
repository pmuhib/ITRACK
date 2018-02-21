package com.client.itrack.fragments.dsr;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.client.itrack.activities.DSRDetails;
import com.client.itrack.R;
import com.client.itrack.adapters.CurrencyCodeAdapter;
import com.client.itrack.adapters.TrailerTypesAdapter;
import com.client.itrack.adapters.TruckStatusAdapter;
import com.client.itrack.adapters.VehicleListAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.AdditionCostModel;
import com.client.itrack.model.CurrencyCodeModel;
import com.client.itrack.model.DSRCargoDetailModel;
import com.client.itrack.model.DSRTruckDetailModel;
import com.client.itrack.model.TrailerTypeModel;
import com.client.itrack.model.TruckDocDetailModel;
import com.client.itrack.model.TruckFinancialDetailModel;
import com.client.itrack.model.TruckStatusModel;
import com.client.itrack.model.VehicleModel;
import com.client.itrack.perm_handler.PermissionRequestHandler;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DocumentHandler;
import com.client.itrack.utility.Utils;
import com.google.gson.Gson;
import com.intsig.csopen.sdk.CSOpenAPI;
import com.intsig.csopen.sdk.CSOpenApiFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.client.itrack.R.id.lbladdmore;

public class AddTruckDetailFragment extends Fragment{

    /**************************************/
    private final int REQ_CODE_PICK_IMAGE = 1;
    private final int REQ_CODE_CALL_CAMSCANNER = 2;
    private static final String APP_KEY = "Ha1F3C7yBKr5NtJUQ4YRSSQQ";
    private static final String DIR_IMAGE = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/CSOpenApiDemo";
    // three values for save instance;
    private static final String SCANNED_IMAGE = "scanned_img";
    private static final String SCANNED_PDF = "scanned_pdf";
    private static final String ORIGINAL_IMG = "ori_img";
    private String mSourceImagePath;
    private String mOutputImagePath;
    private String mOutputPdfPath;
    private String mOutputOrgPath;

    CSOpenAPI mApi ;

    /**************************************/

    /***************************************/
    private static final int PICK_IMAGE = 101;
    private static final int PICK_Camera_IMAGE = 102;
    Uri imageUri;
    /******************************************/

    EditText etOurPriceToCustomer, etOurCost, etAdvance, etBalance, etBorderCharge;
    TextView tvBalancePaidYes, tvBalancePaidNo, btn_add_document,lblBalancePaid,lblBorderCharge,tvforcost,tvforcostclient,tvforcosttype,tvforaddcosttype;
    LinearLayout balanceStatusCont;
    GridLayout doc_container;
    RelativeLayout borderChargeCont,dnrDTCont,detentionDaysCont,offLoadingDTCont,detentionRateCont,detentionChargesCont ;

    EditText etTruckDriverName ,etPhoneCountryCode,etPhoneCountryCode1,etPhoneCountryCode2,etTruckDriverPhone,etTruckDriverPhone1,
            etTruckDriverPhone2,etOffLoadingDate,etOffLoadingTime ,etDetention,etDetentionForClient,etDetentionRateForClient,
            etDetentionRate,etDetentionCharges ,etRemarkTruck,etTruckCurrentLocation,etDeliveryNoteReceivedDate,
            etDeliveryNoteReceivedTime,etFordetentionLocation,etAddtionalcost,etaddcostClient,etcostType;
    Spinner spinnerListTypeOfTrailer ,spinnerListVehicleNumber,spinnerListTrucksStatus,spinnerListCurrencyCode ;
    ImageView ivOffLoadingDate,ivOffLoadingTime ,ivDeliveryNoteReceivedDate,ivDeliveryNoteReceivedTime,ivAddTruckStatus ,ivAddTrailerType,ivAddVehicleNumber,ivaddmore;

    ArrayList<TrailerTypeModel> listTrailerTypes ;
    ArrayList<TruckStatusModel> listTruckStatuses ;
    ArrayList<VehicleModel> listVehicle ;
    ArrayList<CurrencyCodeModel> listCurrencyCode ;
    ArrayList<CurrencyCodeModel> listDestCurrencyCode ;
    DSRDetails dsrDetailsObj ;
    DSRCargoDetailModel dsrCargoDetailModel ;
    AddDSRCargoDetailFragment addDSRCargoDetailFragment ;
    ArrayList<DSRTruckDetailModel> listDSRTruckDetails ;
    int currentTab ;
    private String balance_paid;

    private AppGlobal appGlobal = AppGlobal.getInstance() ;
    String action ,vehicle_number,currency_code;
    View view,separatorBalanceStatus,separatorBorderCharge;
    private CheckBox chkBoxBalancePaid;
    private Spinner spinnerListDestCurrencyCode;
    private RelativeLayout balancePaidStatusContainer;
    CheckBox chBorderCharge;
    LinearLayout container ;
    List<AdditionCostModel> listAdditionalCost ;
    private String border_charge_include;
    //TruckFinancialDetailModel truckFinancialDetailModel;
    // TextView  tvTypeOfTrailer,tvVehicleNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.add_truck_detail_fragment ,container,false) ;
        // All views
        setupGUI();

        return view;
    }
    private void setupGUI() {
        tvforcost= (TextView) view.findViewById(R.id.tvForCost);
        tvforcostclient=(TextView) view.findViewById(R.id.tvForCostClient);
        tvforcosttype=(TextView) view.findViewById(R.id.tvForCostType);
        tvforaddcosttype= (TextView) view.findViewById(R.id.tvForaddCostType);
       ivaddmore= (ImageView) view.findViewById(R.id.ivAddmore);
        container = (LinearLayout) view.findViewById(R.id.conatiner);
        container.setVisibility(View.GONE);
        ivaddmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    addLayout();
            }
        });

        ivAddTruckStatus = (ImageView)view.findViewById(R.id.ivAddTruckStatus);
        ivAddTruckStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddStatusPopup();
            }
        });

        /****************************
         *  Truck Driver Name
         * *************************/

        etTruckDriverName = (EditText) view.findViewById(R.id.etTruckDriverName);
        etTruckDriverName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String value =  s.toString().trim();
                if(!value.isEmpty()) {
                    if (listDSRTruckDetails != null) {
                        listDSRTruckDetails.get(currentTab).driver_name = value;
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**** Truck Driver Phone ******/
        etPhoneCountryCode = (EditText)view.findViewById(R.id.etPhoneCountryCode);
        etPhoneCountryCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value =  s.toString().trim();
                if(!value.isEmpty()) {
                    if (listDSRTruckDetails != null) {

                        listDSRTruckDetails.get(currentTab).code_phone_no = Long.parseLong(value)+"";
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPhoneCountryCode1 = (EditText)view.findViewById(R.id.etPhoneCountryCode1);
        etPhoneCountryCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value =  s.toString().trim();
                if(!value.isEmpty()) {
                    if (listDSRTruckDetails != null) {

                        listDSRTruckDetails.get(currentTab).code_phone_no1 = Long.parseLong(value)+"";
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPhoneCountryCode2 = (EditText)view.findViewById(R.id.etPhoneCountryCode2);
        etPhoneCountryCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value =  s.toString().trim();
                if(!value.isEmpty()) {
                    if (listDSRTruckDetails != null) {

                        listDSRTruckDetails.get(currentTab).code_phone_no2 = Long.parseLong(value)+"";
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etTruckDriverPhone = (EditText)view.findViewById(R.id.etTruckDriverPhone);
        etTruckDriverPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value =  s.toString().trim();
                if(!value.isEmpty()) {
                    if (listDSRTruckDetails != null) {
                        listDSRTruckDetails.get(currentTab).driver_phone = Long.parseLong(value)+"";
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etTruckDriverPhone1 = (EditText)view.findViewById(R.id.etTruckDriverPhone1);
        etTruckDriverPhone1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value =  s.toString().trim();
                if(!value.isEmpty()) {
                    if (listDSRTruckDetails != null) {
                        listDSRTruckDetails.get(currentTab).driver_phone1 = Long.parseLong(value)+"";
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etTruckDriverPhone2 = (EditText)view.findViewById(R.id.etTruckDriverPhone2);
        etTruckDriverPhone2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value =  s.toString().trim();
                if(!value.isEmpty()) {
                    if (listDSRTruckDetails != null) {
                        listDSRTruckDetails.get(currentTab).driver_phone2 = Long.parseLong(value)+"";
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**** OffLoading Date  & Time ******/
        etOffLoadingDate = (EditText)view.findViewById(R.id.etOffLoadingDate);
        etOffLoadingTime = (EditText)view.findViewById(R.id.etOffLoadingTime);
        ivOffLoadingDate = (ImageView)view.findViewById(R.id.ivOffLoadingDate);
        ivOffLoadingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog("offloading");
            }
        });

        ivOffLoadingTime = (ImageView)view.findViewById(R.id.ivOffLoadingTime);
        ivOffLoadingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog("offloading");
            }
        });
        /**** Detention ******/
        etDetention = (EditText) view.findViewById(R.id.etDetention);
        etDetentionForClient = (EditText) view.findViewById(R.id.etDetentionForClient);
        etDetention.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateDetentionCharges();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etDetentionForClient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateDetentionCharges();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etDetentionRate =  (EditText)view.findViewById(R.id.etDetentionRate);
        etDetentionRateForClient =  (EditText)view.findViewById(R.id.etDetentionRateForClient);
        etDetentionRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateDetentionCharges();
            }



            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etDetentionRateForClient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateDetentionCharges();
            }



            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etDetentionCharges =  (EditText)view.findViewById(R.id.etDetentionCharges);
        /**** Remark for truck Driver  ******/
        etRemarkTruck = (EditText)view.findViewById(R.id.etRemarkTruck);

        etRemarkTruck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value =  s.toString().trim();
                if(!value.isEmpty()) {
                    if (listDSRTruckDetails != null) {
                        listDSRTruckDetails.get(currentTab).remark = value;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etTruckCurrentLocation = (EditText)view.findViewById(R.id.etTruckCurrentLocation);
        etTruckCurrentLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value =  s.toString().trim();
                if(!value.isEmpty()) {
                    if (listDSRTruckDetails != null) {
                        listDSRTruckDetails.get(currentTab).truck_current_location = value;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /**** Delivery Note Received Date  & Time ******/
        etDeliveryNoteReceivedDate = (EditText)view.findViewById(R.id.etDeliveryNoteReceivedDate);
        etDeliveryNoteReceivedTime = (EditText)view.findViewById(R.id.etDeliveryNoteReceivedTime);
        ivDeliveryNoteReceivedDate= (ImageView)view.findViewById(R.id.ivDeliveryNoteReceivedDate);
        ivDeliveryNoteReceivedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog("delivery");
            }
        });
        ivDeliveryNoteReceivedTime= (ImageView)view.findViewById(R.id.ivDeliveryNoteReceivedTime);
        ivDeliveryNoteReceivedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog("delivery");
            }
        });

        /**** List Type of Trailer ******/
        ivAddTrailerType = (ImageView) view.findViewById(R.id.ivAddTrailerType);
        ivAddTrailerType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTrailerTypePopup();
            }
        });
        spinnerListTypeOfTrailer =  (Spinner)view.findViewById(R.id.spinnerListTypeOfTrailer) ;
        spinnerListTypeOfTrailer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0)
                {
                    TrailerTypeModel trailerTypeModel =  listTrailerTypes.get(position);
                    loadVehicleList(trailerTypeModel.trailerId);
                    listDSRTruckDetails.get(currentTab).trailor_type = trailerTypeModel.trailerId ;
                }
                else
                {
                    loadVehicleList("");
                    if(listDSRTruckDetails!=null)
                        listDSRTruckDetails.get(currentTab).trailor_type = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /**** List of Vehicle Number ****/
        ivAddVehicleNumber = (ImageView) view.findViewById(R.id.ivAddVehicleNumber);
        ivAddVehicleNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerListTypeOfTrailer.getSelectedItemPosition()>0) showAddVehicleNumberPopup();
                else {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Please select Trailer type!")
                            .create()
                            .show();
                }

            }
        });

        spinnerListVehicleNumber = (Spinner)view.findViewById(R.id.spinnerListVehicleNumber);
        spinnerListVehicleNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0) {
                    listDSRTruckDetails.get(currentTab).vehicle_number = listVehicle.get(position).vehicle_number;
                }
                else
                {
                    if(listDSRTruckDetails!=null){
                        listDSRTruckDetails.get(currentTab).vehicle_number = "";
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**** List of Truck Statuses ******/
        spinnerListTrucksStatus = (Spinner)view.findViewById(R.id.spinnerListTrucksStatus);
        spinnerListTrucksStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0) {
                    listDSRTruckDetails.get(currentTab).truck_status = listTruckStatuses.get(position).statusId;
                    String statusName = listTruckStatuses.get(position).statusName.toLowerCase();
                    if(statusName.equals("delivered") || statusName.equals("offloaded"))
                    {
                        if(action.equals("edit"))
                        {
                            dnrDTCont.setVisibility(View.VISIBLE);
                            offLoadingDTCont.setVisibility(View.VISIBLE);
                            detentionDaysCont.setVisibility(View.VISIBLE);
                            detentionRateCont.setVisibility(View.GONE);
                            detentionChargesCont.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        dnrDTCont.setVisibility(View.GONE);
                        offLoadingDTCont.setVisibility(View.GONE);
                        detentionDaysCont.setVisibility(View.GONE);
                        detentionRateCont.setVisibility(View.VISIBLE);
                        detentionChargesCont.setVisibility(View.GONE);
                        // etDetention.setText("0");
                        etDeliveryNoteReceivedTime.setText("");
                        etDeliveryNoteReceivedDate.setText("");
                        etOffLoadingDate.setText("");
                        etOffLoadingTime.setText("");
                        listDSRTruckDetails.get(currentTab).off_loading_date = etOffLoadingDate.getText().toString()+" "+etOffLoadingTime.getText().toString();
                        listDSRTruckDetails.get(currentTab).del_receive_date = etDeliveryNoteReceivedDate.getText().toString()+" "+etDeliveryNoteReceivedTime.getText().toString();
                    }

                }
                else
                {
                    if(listDSRTruckDetails!=null)
                        listDSRTruckDetails.get(currentTab).truck_status = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_add_document = (TextView) view.findViewById(R.id.btn_add_document);
        btn_add_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /****rEAL tIME *****/
              /*  DocumentHandler documentHandler = new DocumentHandler(getActivity(), AddTruckDetailFragment.this);
                documentHandler.selectDocumentToAdd();*/


                final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Image");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        // boolean result = Utility.checkPermission(EditUserProfileDetailFragment.this);
                        if (items[item].equals("Take Photo")) {
                            // if (result) {
                            if(PermissionRequestHandler.requestPermissionToCamera(getActivity(),AddTruckDetailFragment.this))
                            {
                                camFunction();
                            }
                            // }

                        } else if (items[item].equals("Choose from Gallery")) {
                            // if (result)
                            // {
                            if(PermissionRequestHandler.requestPermissionToGallary(getActivity(),AddTruckDetailFragment.this))
                            {
                                gallaryFun();
                                //go2Gallery();
                            }
                            //  }

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

            }
        });

        TextView btn_submit_truck = (TextView) view.findViewById(R.id.btn_submit_truck);
        btn_submit_truck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validationTruckDetailSubmit()) {

                    DSRTruckDetailModel dsrTruckDetailModel = listDSRTruckDetails.get(currentTab);
                    dsrTruckDetailModel.isSubmitted = true  ;
                    dsrTruckDetailModel.listAdditionalCost = listAdditionalCost ;
                   // dsrTruckDetailModel.truckFinancialDetailModel = truckFinancialDetailModel;
                    listDSRTruckDetails.set(currentTab, dsrTruckDetailModel);
                    // addDSRCargoDetailFragment.listDSRTruckDetails = listDSRTruckDetails ;
                    Toast.makeText(getActivity(), "Truck Detail Submitted Successfully!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        spinnerListCurrencyCode = (Spinner)view.findViewById(R.id.spinnerListCurrencyCode);
        spinnerListCurrencyCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0) {
                    listDSRTruckDetails.get(currentTab).currency_code = listCurrencyCode.get(position).currency_code;
                }
                else
                {
                    if(listDSRTruckDetails!=null) {
                        listDSRTruckDetails.get(currentTab).currency_code = "";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerListDestCurrencyCode = (Spinner)view.findViewById(R.id.spinnerListDestCurrencyCode);
        spinnerListDestCurrencyCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0) {
                    listDSRTruckDetails.get(currentTab).dest_currency_code = listDestCurrencyCode.get(position).currency_code;
                }
                else
                {
                    if(listDSRTruckDetails!=null) {
                        listDSRTruckDetails.get(currentTab).dest_currency_code = "";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        final Spinner spinner =  spinnerListCurrencyCode ;
//        etTruckCurrentLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i == EditorInfo.IME_ACTION_NEXT) {
//                    if(action.equals("add")) {
//                        hideKeyboard();
//                        textView.clearFocus();
//                        spinner.requestFocus();
//                        spinner.performClick();
//                    }
//                }
//                return true;
//            }
//            private void hideKeyboard() {
//                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
//                        InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//
//        });

        etOurPriceToCustomer = (EditText) view.findViewById(R.id.etOurPriceToCustomer);
        etOurPriceToCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(listDSRTruckDetails!=null)
                    listDSRTruckDetails.get(currentTab).truckFinancialDetailModel.our_price_customer = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etOurCost = (EditText) view.findViewById(R.id.etOurCost);
        etAdvance = (EditText) view.findViewById(R.id.etAdvance);
        etBalance = (EditText) view.findViewById(R.id.etBalance);

        lblBalancePaid = (TextView)view.findViewById(R.id.lblBalancePaid);
        balanceStatusCont = (LinearLayout)view.findViewById(R.id.balanceStatusCont);
        lblBorderCharge = (TextView)view.findViewById(R.id.lblBorderCharge);
        separatorBalanceStatus = view.findViewById(R.id.separatorBalanceStatus);
        separatorBorderCharge = view.findViewById(R.id.separatorBorderCharge);
        borderChargeCont = (RelativeLayout) view.findViewById(R.id.borderChargeCont);
        dnrDTCont = (RelativeLayout) view.findViewById(R.id.dnrDTCont);
        offLoadingDTCont = (RelativeLayout) view.findViewById(R.id.offLoadingDTCont);
        detentionDaysCont = (RelativeLayout) view.findViewById(R.id.detentionDaysCont);
        chBorderCharge= (CheckBox) view.findViewById(R.id.chborderchek);
        chBorderCharge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    border_charge_include = "No";

                }else {
                    border_charge_include = "Yes" ;

                }

                listDSRTruckDetails.get(currentTab).truckFinancialDetailModel.border_charge_include = border_charge_include ;
            }
        });
        etBorderCharge = (EditText) view.findViewById(R.id.etBorderCharge);
        etBorderCharge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(listDSRTruckDetails!=null)
                    listDSRTruckDetails.get(currentTab).truckFinancialDetailModel.border_charges = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etFordetentionLocation= (EditText) view.findViewById(R.id.tvForDetentionLocation);


        etFordetentionLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(listDSRTruckDetails!=null)
                    listDSRTruckDetails.get(currentTab).detention_location = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvBalancePaidYes = (TextView) view.findViewById(R.id.tvBalancePaidYes);
        chkBoxBalancePaid = (CheckBox) view.findViewById(R.id.chkBoxBalancePaid);
        chkBoxBalancePaid.setText("No");
        chkBoxBalancePaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    balance_paid = "Yes";
                    chkBoxBalancePaid.setText("Yes");

                }else {
                    balance_paid = "No" ;
                    chkBoxBalancePaid.setText("No");
                }

                listDSRTruckDetails.get(currentTab).truckFinancialDetailModel.balance_paid = balance_paid ;
            }
        });
        tvBalancePaidYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!balance_paid.toLowerCase().equals("yes"))
                {
                    balance_paid = "Yes";
                    double balanceVal = 0.0 ;
                    listDSRTruckDetails.get(currentTab).truckFinancialDetailModel.balance = String.valueOf(balanceVal);
                    etBalance.setText(String.valueOf(balanceVal));
                    setBalancePaid(balance_paid.toLowerCase());
                }
            }
        });

        tvBalancePaidNo = (TextView) view.findViewById(R.id.tvBalancePaidNo);
        tvBalancePaidNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balance_paid = "No";
                setBalancePaid(balance_paid.toLowerCase());
            }
        });

        doc_container = (GridLayout) view.findViewById(R.id.doc_container);


        /************************/
        detentionChargesCont =  (RelativeLayout) view.findViewById(R.id.detentionChargesCont) ;
        detentionRateCont =  (RelativeLayout) view.findViewById(R.id.detentionRateCont) ;
        balancePaidStatusContainer =  (RelativeLayout) view.findViewById(R.id.balancePaidStatusContainer) ;





    }

    private void addLayout() {
        View layout=LayoutInflater.from(getContext()).inflate(R.layout.costlayout,container,false);
        container.addView(layout);
        container.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "A new field set has been added", Toast.LENGTH_SHORT).show();

    }

    private final int PERMISSIONS_REQUEST_CAMERA = 0 ;
    private final int PERMISSIONS_REQUEST_GALLARY = 1 ;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    camFunction();
                } else {

                }
                return;
            }

            case PERMISSIONS_REQUEST_GALLARY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gallaryFun();
                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void updateDetentionCharges() {

        String detention = etDetention.getText().toString().trim();
        detention = detention.isEmpty() ? "0" : detention;

        String detention_rate = etDetentionRate.getText().toString().trim();
        detention_rate = detention_rate.isEmpty() ? "0" : detention_rate;

        String detention_client = etDetentionForClient.getText().toString().trim();
        detention_client = detention_client.isEmpty() ? "0" : detention_client;

        String detention_rate_client = etDetentionRateForClient.getText().toString().trim();
        detention_rate = detention_rate.isEmpty() ? "0" : detention_rate;


        long detention_cost = Long.parseLong(detention) * Long.parseLong(detention_rate);

        etDetentionCharges.setText(String.valueOf(detention_cost));
        listDSRTruckDetails.get(currentTab).detention = detention;
        listDSRTruckDetails.get(currentTab).detention_rate = detention_rate;
        listDSRTruckDetails.get(currentTab).detention_client = detention_client; // [20-02-2017]
        listDSRTruckDetails.get(currentTab).detention_rate_client = detention_rate_client; // [20-02-2017]

        listDSRTruckDetails.get(currentTab).detention_cost = String.valueOf(detention_cost);
    }

    private void initializeSpinner() {
        //Get List collection
        listTrailerTypes = appGlobal.listTrailerType ;
        if(listTrailerTypes==null || listTrailerTypes.size()<=0) {
            listTrailerTypes = new ArrayList<>();
            TrailerTypeModel modelTrailerType = null ;
            modelTrailerType =new TrailerTypeModel();
            modelTrailerType.trailerId = "0";
            modelTrailerType.trailorType ="Select Trailer Type";
            listTrailerTypes.add(modelTrailerType);
        }
        TrailerTypesAdapter trailerTypesAdapter = new TrailerTypesAdapter(getActivity(),listTrailerTypes) ;
        spinnerListTypeOfTrailer.setAdapter(trailerTypesAdapter);
        trailerTypesAdapter.notifyDataSetChanged();

        listTruckStatuses =  appGlobal.listTruckStatus ;
        if(listTruckStatuses==null || listTruckStatuses.size()<=0)
        {
            listTruckStatuses = new ArrayList<>();
            TruckStatusModel modelTruckStatus = null ;
            modelTruckStatus = new TruckStatusModel();
            modelTruckStatus.statusId =  "0" ;
            modelTruckStatus.statusName = "Select Truck Status"  ;
            listTruckStatuses.add(modelTruckStatus);
        }
        //Set Adapters
        TruckStatusAdapter truckStatusAdapter = new TruckStatusAdapter(getActivity(),listTruckStatuses) ;
        spinnerListTrucksStatus.setAdapter(truckStatusAdapter);
        truckStatusAdapter.notifyDataSetChanged();

        listVehicle = new ArrayList<>();
        //initVehicleList();
        listCurrencyCode = new ArrayList<>();
        CurrencyCodeModel currencyCodeModel ;
        currencyCodeModel= new CurrencyCodeModel();
        currencyCodeModel.id="0";
        currencyCodeModel.currency_code="Select Current Code";

        listCurrencyCode.add(currencyCodeModel);

        listDestCurrencyCode = new ArrayList<>();
        CurrencyCodeModel dest_currencyCodeModel ;
        dest_currencyCodeModel= new CurrencyCodeModel();
        dest_currencyCodeModel.id="0";
        dest_currencyCodeModel.currency_code="Select Current Code";

        listDestCurrencyCode.add(dest_currencyCodeModel);

    }

    public void setBalancePaid(String s) {
        this.listDSRTruckDetails.get(currentTab).truckFinancialDetailModel.balance_paid = s;
        switch (s) {
            case "yes":
                tvBalancePaidYes.setSelected(true);
                tvBalancePaidNo.setSelected(false);
                tvBalancePaidYes.setActivated(true);
                tvBalancePaidNo.setActivated(false);
                tvBalancePaidYes.setTextColor(Color.BLACK);
                tvBalancePaidNo.setTextColor(Color.WHITE);
                break;

            case "no":
                tvBalancePaidYes.setSelected(false);
                tvBalancePaidNo.setSelected(true);
                tvBalancePaidYes.setActivated(false);
                tvBalancePaidNo.setActivated(true);
                tvBalancePaidNo.setTextColor(Color.BLACK);
                tvBalancePaidYes.setTextColor(Color.WHITE);
                break;

            default:

                tvBalancePaidYes.setSelected(false);
                tvBalancePaidNo.setSelected(false);
                tvBalancePaidYes.setActivated(false);
                tvBalancePaidNo.setActivated(false);
                tvBalancePaidNo.setTextColor(Color.WHITE);
                tvBalancePaidYes.setTextColor(Color.WHITE);
                break;

        }
    }

    private boolean validationTruckDetailSubmit() {

        DSRTruckDetailModel dsrTruckDetailModel = this.listDSRTruckDetails.get(currentTab);
        if (dsrTruckDetailModel.driver_name == null || dsrTruckDetailModel.driver_name.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please Enter driver name!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dsrTruckDetailModel.code_phone_no == null || dsrTruckDetailModel.code_phone_no.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please Enter Phone Code.!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dsrTruckDetailModel.driver_phone == null || dsrTruckDetailModel.driver_phone.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please Enter driver phone number.!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dsrTruckDetailModel.trailor_type.isEmpty()) {
            Toast.makeText(getActivity(), "Please Select Trailer Type.!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dsrTruckDetailModel.vehicle_number.isEmpty()) {
            Toast.makeText(getActivity(), "Please Select Vehicle number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dsrTruckDetailModel.truck_status ==null || dsrTruckDetailModel.truck_status.isEmpty())
        {
            Toast.makeText(getActivity(), "Please Select Truck Status!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dsrTruckDetailModel.detention == null || dsrTruckDetailModel.detention.isEmpty()) {
            dsrTruckDetailModel.detention = "0";
        }

        // [20-02-2017]
        if (dsrTruckDetailModel.detention_client == null || dsrTruckDetailModel.detention_client.isEmpty()) {
            dsrTruckDetailModel.detention_client = "0";
        }

        if (spinnerListCurrencyCode.getSelectedItemPosition() <= 0) {
            Toast.makeText(getActivity(), "Please Select Currency Code.!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(dsrTruckDetailModel.truckFinancialDetailModel.our_price_customer ==null || dsrTruckDetailModel.truckFinancialDetailModel.our_price_customer.trim().isEmpty())
        {
            Toast.makeText(getActivity(), "Please Enter our price to customer!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(dsrTruckDetailModel.truckFinancialDetailModel.our_cost ==null || dsrTruckDetailModel.truckFinancialDetailModel.our_cost.trim().isEmpty())
        {
            Toast.makeText(getActivity(), "Please Enter our cost!", Toast.LENGTH_SHORT).show();
            return false;
        }


        switch (action) {
            case "add":
                if(balance_paid.isEmpty())
                {
                    dsrTruckDetailModel.truckFinancialDetailModel.balance_paid = "No" ;
                }
                break;
        }
            if(container.getVisibility()==View.VISIBLE) {
                if (!validateOtherAdditionalCost()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

        return true ;
    }

    private boolean validateOtherAdditionalCost() {

        final LinearLayout container = (LinearLayout) view.findViewById(R.id.conatiner);
        for (int indexChild = 0; indexChild < container.getChildCount(); indexChild++) {
            View childView = container.getChildAt(indexChild);
            EditText tvaddForCost = (EditText) childView.findViewById(R.id.tvaddForCost);
            EditText tvaddForCostClient = (EditText) childView.findViewById(R.id.tvaddForCostClient);
            EditText tvaddForCostType = (EditText) childView.findViewById(R.id.tvaddForCostType);
            String cost = tvaddForCost.getText().toString().trim();
            String costClient = tvaddForCostClient.getText().toString().trim();
            String costType = tvaddForCostType.getText().toString().trim();

            if (!cost.isEmpty() || !costClient.isEmpty() || !costType.isEmpty()) {
                if (!cost.isEmpty() && !costClient.isEmpty() && !costType.isEmpty()) {
                    AdditionCostModel additionCostModel = new AdditionCostModel();
                    additionCostModel.cost = cost;
                    additionCostModel.cost_client = costClient;
                    additionCostModel.cost_type = costType;
                    listAdditionalCost.add(additionCostModel);
//
                }
                else
                    {
                    Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }


    /*************************************
     *  Show Add Popup Events
     *************************************/

    private void showAddTrailerTypePopup() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LinearLayout layout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        final EditText etTrailerType = new EditText(getActivity());
        etTrailerType.setHint("Enter Trailer Type");
        layout.addView(etTrailerType);
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setTitle("Add Trailer Type");

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
                String trailerType = etTrailerType.getText().toString().trim();
                if(!trailerType.isEmpty()) requestAddTrailerType(trailerType) ;
                else Toast.makeText(getActivity(), "Please enter Trailer Type!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        try { alertDialog.show();}
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddVehicleNumberPopup() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LinearLayout layout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        final EditText etVehicleNumber = new EditText(getActivity());
        etVehicleNumber.setHint("Enter Vehicle Number");
        final Spinner spinner  = new Spinner(getActivity());
        TrailerTypesAdapter trailerTypesAdapter = new TrailerTypesAdapter(getActivity(),listTrailerTypes) ;
        spinner.setAdapter(trailerTypesAdapter);
        spinner.setSelection(spinnerListTypeOfTrailer.getSelectedItemPosition());
        trailerTypesAdapter.notifyDataSetChanged();
        layout.addView(spinner);
        layout.addView(etVehicleNumber);
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setTitle("Add Vehicle Number");

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
                String vehicleNumber = etVehicleNumber.getText().toString().trim();
                if(!vehicleNumber.isEmpty() && spinner.getSelectedItemPosition()>0 )
                {
                    String trailerId  = listTrailerTypes.get(spinner.getSelectedItemPosition()).trailerId;
                    requestAddVehicleNumber(trailerId,vehicleNumber) ;
                }
                else Toast.makeText(getActivity(), "All Fields are required!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        try { alertDialog.show();}
        catch (Exception e) {
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
        alertDialogBuilder.setTitle("Add Truck Status");

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
                if(!status.isEmpty()) requestAddDSRtatus(status) ;
                else Toast.makeText(getActivity(), "Please enter status title!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        try { alertDialog.show();}
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*********************************
     *  Server Request API
     *********************************/
    private void loadCurrencyCode() {

        String url =  Constants.BASE_URL + "currency_code_list";

        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                try {
                    listCurrencyCode.clear();
                    listDestCurrencyCode.clear();
                    CurrencyCodeModel currencyCodeModel ;
                    currencyCodeModel= new CurrencyCodeModel();
                    currencyCodeModel.id="0";
                    currencyCodeModel.currency_code="Select Currency Code";
                    listCurrencyCode.add(currencyCodeModel);
                    listDestCurrencyCode.add(currencyCodeModel);
                    int selectedIndex = -1;
                    int selectedDestIndex = -1;
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {

                        JSONArray jsonArray = jobj.getJSONArray("currency_code");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            currencyCodeModel= new CurrencyCodeModel();
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            currencyCodeModel.id = jobject.getString("id");
                            currencyCodeModel.country_name = jobject.getString("country_name");
                            currencyCodeModel.currency_code  = jobject.getString("currency_code");
                            if(listDSRTruckDetails.get(currentTab).currency_code.trim().toLowerCase().equals(currencyCodeModel.currency_code.toLowerCase().trim()))
                            {
                                selectedIndex =  i  ;
                            }

                            if(listDSRTruckDetails.get(currentTab).dest_currency_code.trim().toLowerCase().equals(currencyCodeModel.currency_code.toLowerCase().trim()))
                            {
                                selectedDestIndex =  i  ;
                            }


                            currencyCodeModel.id="0";
                            listCurrencyCode.add(currencyCodeModel);
                            listDestCurrencyCode.add(currencyCodeModel);
                        }


                        CurrencyCodeAdapter adapter = new CurrencyCodeAdapter(getActivity(), listCurrencyCode);
                        spinnerListCurrencyCode.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        if(selectedIndex!=-1)
                        {
                            spinnerListCurrencyCode.setSelection(selectedIndex+1);
                        }
                        else
                        {
                            spinnerListCurrencyCode.setSelection(0);
                        }


                        CurrencyCodeAdapter adapterDest = new CurrencyCodeAdapter(getActivity(), listDestCurrencyCode);
                        spinnerListDestCurrencyCode.setAdapter(adapterDest);
                        adapterDest.notifyDataSetChanged();

                        if(selectedDestIndex!=-1)
                        {
                            spinnerListDestCurrencyCode.setSelection(selectedDestIndex+1);
                        }
                        else
                        {
                            spinnerListDestCurrencyCode.setSelection(0);
                        }



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

    private void loadVehiclesByTrailerId(String trailor_id) {

        String url =  Constants.BASE_URL + "vehiclenumber";
        HashMap<String,String> hmap= new HashMap<>();
        hmap.put("trailor_id", trailor_id);
        HttpPostRequest.doPost(getActivity(), url,new Gson().toJson(hmap), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                try {

                    int selectedIndex= -1 ;
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        listVehicle.clear();
                        VehicleModel vehicleModel = null ;
                        vehicleModel = new VehicleModel() ;
                        vehicleModel.vehicle_id = "0" ;
                        vehicleModel.vehicle_number =  "Select Vehicle Number" ;
                        listVehicle.add(vehicleModel);
                        JSONArray jsonArray = jobj.getJSONArray("vehiclenumber");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            vehicleModel = new VehicleModel() ;
                            vehicleModel.vehicle_id = jobject.getString("vehicle_id") ;
                            String vehicleNum  = jobject.getString("vehicle_number");
                            vehicleModel.vehicle_number =  vehicleNum   ;

                            if(vehicleNum.trim().toLowerCase().equals(listDSRTruckDetails.get(currentTab).vehicle_number.trim().toLowerCase()))
                            {
                                selectedIndex = i ;
                            }
                            listVehicle.add(vehicleModel);
                        }
                        VehicleListAdapter vehicleListAdapter = new VehicleListAdapter(getActivity(),listVehicle);
                        vehicleListAdapter.notifyDataSetChanged();
                        spinnerListVehicleNumber.setAdapter(vehicleListAdapter);
                        if(selectedIndex!=-1)
                        {
                            spinnerListVehicleNumber.setSelection(selectedIndex+1);
                        }
                        vehicleListAdapter.notifyDataSetChanged();

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

    private void requestAddDSRtatus(final String statusStr) {

        if(checkExistStatus(statusStr))
        {
            return ;
        }

        // Utils.showLoadingPopup(getActivity());

        String url= Constants.BASE_URL + "add_truck_status";
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
                        TruckStatusModel truckStatusModel = new TruckStatusModel();
                        truckStatusModel.statusName  =  statusStr ;
                        truckStatusModel.statusId = jobj.getString("dsr_status_id") ;
                        appGlobal.listTruckStatus.add(truckStatusModel);
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

    private void requestAddTrailerType(final String trailerType) {
//        if(checkExistStatus(trailerType))
//        {
//            return ;
//        }

        // Utils.showLoadingPopup(getActivity());

        String url= Constants.BASE_URL + "add_trailor_type";
        HashMap<String, String> data = new HashMap<>();
        data.put("trailor_type",trailerType);

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                // Utils.hideLoadingPopup();
                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        TrailerTypeModel trailerTypeModel = new TrailerTypeModel();
                        trailerTypeModel.trailorType  =  trailerType ;
                        trailerTypeModel.trailerId = jobj.getString("trailor_type_id") ;
                        appGlobal.listTrailerType.add(trailerTypeModel);
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
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

    private void requestAddVehicleNumber(String trailerId, final String vehicleNumber) {
//        if(checkExistStatus(trailerType))
//        {
//            return ;
//        }

        // Utils.showLoadingPopup(getActivity());

        String url= Constants.BASE_URL + "add_vehicle_number";
        HashMap<String, String> data = new HashMap<>();
        data.put("trailor_id",trailerId);
        data.put("vehicle_number",vehicleNumber);

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                // Utils.hideLoadingPopup();
                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        VehicleModel vehicleModel = new VehicleModel();
                        vehicleModel.vehicle_number  =  vehicleNumber ;
                        vehicleModel.vehicle_id  =  jobj.getString("Vehicle Number_id") ;

                        listVehicle.add(vehicleModel);
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
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

    private boolean checkExistStatus(String status) {

        ArrayList<TruckStatusModel> listLocationPoint ;

        listLocationPoint = listTruckStatuses ;

        for (TruckStatusModel statusModel:listLocationPoint) {
            if(statusModel.statusName.toLowerCase().trim().equals(status.toLowerCase().trim()))
            {
                Toast.makeText(getActivity(),"Status already exists!", Toast.LENGTH_SHORT).show();
                return true ;
            }
        }
        return false ;

    }

    private void loadVehicleList(String trailor_id) {
        /******* Request to load vehicle number List *******/
        if(!trailor_id.isEmpty()) loadVehiclesByTrailerId(trailor_id) ;
        else  {
            listVehicle.clear();
            initVehicleList();
        }
    }

    private void initVehicleList() {
        VehicleModel vehicleModel = null ;
        vehicleModel = new VehicleModel() ;
        vehicleModel.vehicle_id = "0" ;
        vehicleModel.vehicle_number =  "Select Vehicle Number" ;
        listVehicle.add(vehicleModel);
        VehicleListAdapter vehicleListAdapter = new VehicleListAdapter(getActivity(),listVehicle);
        vehicleListAdapter.notifyDataSetChanged();
        spinnerListVehicleNumber.setAdapter(vehicleListAdapter);
    }

    /***********************************/

    private void setTruckStatusSelection(String truck_status) {
        if (truck_status != null && !truck_status.isEmpty()) {
            for (int indexDP = 0; indexDP < listTruckStatuses.size(); indexDP++) {
                if (truck_status.equals(listTruckStatuses.get(indexDP).statusId)) {
                    spinnerListTrucksStatus.setSelection(indexDP);
                    break;
                }
            }
        } else {
            //  spinnerListTrucksStatus.setSelection(1); // Default Selected
            for (int indexDP = 0; indexDP < listTruckStatuses.size(); indexDP++) {
                if (listTruckStatuses.get(indexDP).statusName.toLowerCase().equals("just loaded")) {
                    spinnerListTrucksStatus.setSelection(indexDP);
                    break;
                }
            }
        }
    }

    private void setTraileTyperSelection(String trailerId) {
        if (trailerId != null && !trailerId.isEmpty()) {
            for (int indexDP = 0; indexDP < listTrailerTypes.size(); indexDP++) {
                if (trailerId.equals(listTrailerTypes.get(indexDP).trailerId)) {
                    spinnerListTypeOfTrailer.setSelection(indexDP);
                    break;
                }
            }
        } else { spinnerListTypeOfTrailer.setSelection(0); }
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
                Calendar calender = Calendar.getInstance();
                calender.set(Calendar.YEAR, year);
                calender.set(Calendar.MONTH, monthOfYear);
                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String dateTimeLoading = appGlobal.getOnlyDateFormat().format(calender.getTime());
                dt = dateTimeLoading.split(" ");
                switch (field) {
                    case "offloading": {
                        etOffLoadingDate.setText(dateTimeLoading);
                        // update Loading Date Time ,date time ETa And days TT
                        if(etOffLoadingTime.getText().length()>0)
                            listDSRTruckDetails.get(currentTab).off_loading_date = etOffLoadingDate.getText().toString()+" "+etOffLoadingTime.getText().toString(); // dateTimeReachedCustom;
                        else showTimeDialog("offloading");
                    }
                    break;

                    case "delivery": {
                        try {
                            etDeliveryNoteReceivedDate.setText(dateTimeLoading);
                            //etDeliveryNoteReceivedTime.setText(dt[1]);

                            if(etDeliveryNoteReceivedTime.getText().length()>0)
                                listDSRTruckDetails.get(currentTab).del_receive_date = etDeliveryNoteReceivedDate.getText().toString() +" "+etDeliveryNoteReceivedTime.getText().toString();
                            else showTimeDialog("delivery");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


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
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.GLCalenderStyle, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calender = Calendar.getInstance();
                calender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calender.set(Calendar.MINUTE, minute);

                String dateTimeLoading = appGlobal.getOnlyTimeFormat().format(calender.getTime());
                switch (field) {
                    case "offloading": {
                        etOffLoadingTime.setText(dateTimeLoading);
                        // update Loading Date Time ,date time ETa And days TT
                        if(etOffLoadingDate.getText().length()>0)
                            listDSRTruckDetails.get(currentTab).off_loading_date = etOffLoadingDate.getText().toString()+" "+etOffLoadingTime.getText().toString(); // dateTimeReachedCustom;
                        else showDateDialog("offloading");
                    }
                    break;

                    case "delivery": {
                        try {
                            etDeliveryNoteReceivedTime.setText(dateTimeLoading);
                            //etDeliveryNoteReceivedTime.setText(dt[1]);

                            if(etDeliveryNoteReceivedDate.getText().length()>0)
                                listDSRTruckDetails.get(currentTab).del_receive_date = etDeliveryNoteReceivedDate.getText().toString() +" "+etDeliveryNoteReceivedTime.getText().toString();
                            else showDateDialog("delivery");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


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
        this.dsrDetailsObj = (DSRDetails)getActivity();
        this.addDSRCargoDetailFragment = (AddDSRCargoDetailFragment) getParentFragment();
        this.listDSRTruckDetails = addDSRCargoDetailFragment.listDSRTruckDetails ;
        this.currentTab =  addDSRCargoDetailFragment.tabhost.getCurrentTab();
        this.dsrCargoDetailModel = addDSRCargoDetailFragment.dsrCargoDetailModel ;
        bindDataTOGUI();
        uIFeaturesSetting();

        loadCurrencyCode();
        mApi = CSOpenApiFactory.createCSOpenApi(getActivity(), APP_KEY, null);
    }

    private void uIFeaturesSetting() {
        if(action !=null) {

            balanceStatusCont.setVisibility(View.GONE);

            switch (action) {
                case "add":
                    etTruckDriverName.setFocusable(true);
                    etTruckDriverName.setEnabled(true);

                    etPhoneCountryCode.setFocusable(true);
                    etPhoneCountryCode.setEnabled(true);
                    etPhoneCountryCode1.setFocusable(true);
                    etPhoneCountryCode1.setEnabled(true);
                    etPhoneCountryCode2.setFocusable(true);
                    etPhoneCountryCode2.setEnabled(true);

                    etTruckDriverPhone.setFocusable(true);
                    etTruckDriverPhone.setEnabled(true);
                    etTruckDriverPhone1.setFocusable(true);
                    etTruckDriverPhone1.setEnabled(true);
                    etTruckDriverPhone2.setFocusable(true);
                    etTruckDriverPhone2.setEnabled(true);

                    ivAddTrailerType.setVisibility(View.VISIBLE);
                    ivAddVehicleNumber.setVisibility(View.VISIBLE);

                    spinnerListTypeOfTrailer.setFocusable(true);
                    spinnerListTypeOfTrailer.setEnabled(true);

                    spinnerListVehicleNumber.setFocusable(true);
                    spinnerListVehicleNumber.setEnabled(true);

                    etDetention.setEnabled(false);
                    etDetention.setFocusable(false);
                    etDetentionRate.setEnabled(true);
                    etDetentionRate.setFocusable(true);

                    etDetentionForClient.setEnabled(false);
                    etDetentionForClient.setFocusable(false);
                    etDetentionRateForClient.setEnabled(true);
                    etDetentionRateForClient.setFocusable(true);

                    spinnerListCurrencyCode.setFocusable(true);
                    spinnerListCurrencyCode.setEnabled(true);

                    spinnerListTrucksStatus.setEnabled(true);
                    spinnerListTrucksStatus.setFocusable(true);
                    //  ivAddTruckStatus.setVisibility(View.GONE);

                    etDeliveryNoteReceivedDate.setEnabled(false);
                    etDeliveryNoteReceivedDate.setFocusable(false);
                    etDeliveryNoteReceivedTime.setEnabled(false);
                    etDeliveryNoteReceivedTime.setFocusable(false);
                    // ivDeliveryNoteReceivedDate.setEnabled(false);
                    //  ivDeliveryNoteReceivedDate.setFocusable(false);


                    etOurPriceToCustomer.setEnabled(true);
                    etOurPriceToCustomer.setFocusable(true);
                    etOurCost.setEnabled(true);
                    etOurCost.setFocusable(true);
                    etAdvance.setEnabled(true);
                    etAdvance.setFocusable(true);
                    borderChargeCont.setVisibility(View.GONE);
                    lblBorderCharge.setVisibility(View.GONE);
                    etBorderCharge.setVisibility(View.GONE);

                    ivOffLoadingDate.setVisibility(View.GONE);
                    ivOffLoadingTime.setVisibility(View.GONE);

                    ivDeliveryNoteReceivedDate.setVisibility(View.GONE);
                    ivDeliveryNoteReceivedTime.setVisibility(View.GONE);

                    detentionRateCont.setVisibility(View.VISIBLE);
                    detentionChargesCont.setVisibility(View.GONE);
                    balancePaidStatusContainer.setVisibility(View.GONE);
                    break;

                case "edit":

                    etTruckDriverName.setFocusable(false);
                    etTruckDriverName.setEnabled(false);

                    etPhoneCountryCode.setFocusable(false);
                    etPhoneCountryCode.setEnabled(false);
                    etPhoneCountryCode1.setFocusable(false);
                    etPhoneCountryCode1.setEnabled(false);
                    etPhoneCountryCode2.setFocusable(false);
                    etPhoneCountryCode2.setEnabled(false);

                    etTruckDriverPhone.setFocusable(false);
                    etTruckDriverPhone.setEnabled(false);
                    etTruckDriverPhone1.setFocusable(false);
                    etTruckDriverPhone1.setEnabled(false);
                    etTruckDriverPhone2.setFocusable(false);
                    etTruckDriverPhone2.setEnabled(false);

                    ivAddTrailerType.setVisibility(View.GONE);
                    ivAddVehicleNumber.setVisibility(View.GONE);

                    spinnerListTypeOfTrailer.setFocusable(false);
                    spinnerListTypeOfTrailer.setEnabled(false);

                    spinnerListVehicleNumber.setFocusable(false);
                    spinnerListVehicleNumber.setEnabled(false);

                    spinnerListTrucksStatus.setEnabled(true);
                    spinnerListTrucksStatus.setFocusable(true);

                    spinnerListCurrencyCode.setFocusable(false);
                    spinnerListCurrencyCode.setEnabled(false);

                    etOurPriceToCustomer.setEnabled(false);
                    etOurPriceToCustomer.setFocusable(false);
                    etOurCost.setEnabled(false);
                    etOurCost.setFocusable(false);
                    etAdvance.setEnabled(false);
                    etAdvance.setFocusable(false);
                    etDetentionRate.setEnabled(true);
                    etDetentionRate.setFocusable(true);
                    etDetention.setEnabled(true);
                    etDetention.setFocusable(true);

                    etDetentionRateForClient.setEnabled(true);
                    etDetentionRateForClient.setFocusable(true);
                    etDetentionForClient.setEnabled(true);
                    etDetentionForClient.setFocusable(true);

                    etDeliveryNoteReceivedDate.setEnabled(false);
                    etDeliveryNoteReceivedDate.setFocusable(false);
                    etDeliveryNoteReceivedTime.setEnabled(false);
                    etDeliveryNoteReceivedTime.setFocusable(false);

                   /* balanceStatusCont.setEnabled(true);
                    balanceStatusCont.setFocusable(true);
*/

                    borderChargeCont.setVisibility(View.VISIBLE);
                    lblBorderCharge.setVisibility(View.VISIBLE);
                    etBorderCharge.setVisibility(View.VISIBLE);

                    ivOffLoadingDate.setVisibility(View.VISIBLE);
                    ivOffLoadingTime.setVisibility(View.VISIBLE);

                    //  ivAddTruckStatus.setVisibility(View.VISIBLE);
                    ivDeliveryNoteReceivedDate.setVisibility(View.VISIBLE);
                    ivDeliveryNoteReceivedTime.setVisibility(View.VISIBLE);

                    detentionRateCont.setVisibility(View.VISIBLE);
                    detentionChargesCont.setVisibility(View.GONE);

                    balancePaidStatusContainer.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    private void bindDataTOGUI() {

        // Get All Argument Values
        Bundle bundle = getArguments() ;

        if(bundle!=null)
        {
            initializeSpinner();
            action =  bundle.getString("action");
            String driver_name = bundle.getString("driver_name") ;
            String driver_phone = bundle.getString("driver_phone");
            String driver_phone1 = bundle.getString("driver_phone1");
            String driver_phone2 = bundle.getString("driver_phone2");
            String trailor_type = bundle.getString("trailor_type");
            String truck_status = bundle.getString("truck_status");
            vehicle_number = bundle.getString("vehicle_number");
            String off_loading_date  = bundle.getString("off_loading_date");
            String  detention = bundle.getString("detention") ;
            String  detention_client = bundle.getString("detention_client") ;
            String  detention_location = bundle.getString("detention_location") ;
            String code_phone_no =   bundle.getString("code_phone_no");
            String code_phone_no1 =   bundle.getString("code_phone_no1");
            String code_phone_no2 =   bundle.getString("code_phone_no2");
            String detention_rate =  bundle.getString("detention_rate");
            String detention_rate_client =  bundle.getString("detention_rate_client"); // [20-02-2017]

            currency_code =  bundle.getString("currency_code");
            String truck_current_location =  bundle.getString("truck_current_location");

            String del_receive_date = bundle.getString("del_recive_date") ;
            String remark =  bundle.getString("remark") ;
            String our_price_customer  = bundle.getString("our_price_customer");
            String our_cost  = bundle.getString("our_cost");
            String balance  = bundle.getString("balance");
            String advance  = bundle.getString("advance");
            balance_paid  = bundle.getString("balance_paid");
            String border_charges  = bundle.getString("border_charges");
            border_charge_include  = bundle.getString("border_charge_include");
            ArrayList<TruckDocDetailModel> docs  = (ArrayList<TruckDocDetailModel>)bundle.getSerializable("docs"); // [20-02-2017]
            etTruckDriverName.setText(driver_name==null?"":driver_name.trim());
            etPhoneCountryCode.setText(code_phone_no!=null?code_phone_no:"0");
            etPhoneCountryCode1.setText(code_phone_no1!=null?code_phone_no1:"0");
            etPhoneCountryCode2.setText(code_phone_no2!=null?code_phone_no2:"0");
            etTruckDriverPhone.setText(driver_phone!=null?driver_phone.trim():"");
            etTruckDriverPhone1.setText(driver_phone1!=null?driver_phone1.trim():"");
            etTruckDriverPhone2.setText(driver_phone2!=null?driver_phone2.trim():"");
            off_loading_date = off_loading_date!=null?off_loading_date.trim():"";
            String[] oldArr = off_loading_date.split(" ");
            etOffLoadingDate.setText(oldArr[0].trim());
            if(oldArr.length>1)
            {
                etOffLoadingTime.setText(oldArr[1].trim());
            }
            etDetention.setText(detention!=null?detention.trim():"");
            etDetentionRate.setText(detention_rate!=null?detention_rate:"0");
            etDetentionForClient.setText(detention_client!=null?detention_client.trim():"");
            etDetentionRateForClient.setText(detention_rate_client!=null?detention_rate_client:"0");
            etFordetentionLocation.setText(detention_location);
            del_receive_date = del_receive_date!=null?del_receive_date.trim():"";
            oldArr = del_receive_date.split(" ");
            etDeliveryNoteReceivedDate.setText(oldArr[0].trim());
            if(oldArr.length>1)
            {
                etDeliveryNoteReceivedTime.setText(oldArr[1].trim());
            }
            etRemarkTruck.setText(remark!=null?remark.trim():"");
            etTruckCurrentLocation.setText(truck_current_location!=null?truck_current_location:"");
            setTraileTyperSelection(trailor_type.trim());
            setTruckStatusSelection(truck_status.trim());

            etOurPriceToCustomer.setText(our_price_customer != null ? our_price_customer.trim() : "");
            etOurCost.setText(our_cost != null ? our_cost.trim() : "");
            etOurCost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateCostValues();
                }



                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etAdvance.setText(advance != null ? advance.trim() : "0");
            etAdvance.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateCostValues();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            /*Atul*/

            StringBuilder builderAserviceCost = new StringBuilder();
            StringBuilder builderAserviceCost_type = new StringBuilder();
            StringBuilder builderAserviceCharge = new StringBuilder();
            listAdditionalCost  = (List<AdditionCostModel>) bundle.getSerializable("other_costs") ;
            for (int indexACost = 0; indexACost < listAdditionalCost.size(); indexACost++) {
                AdditionCostModel additionCostModel =  listAdditionalCost.get(indexACost);
                if(indexACost!=0){
                    builderAserviceCharge.append("\n");
                    builderAserviceCost.append("\n");
                    builderAserviceCost_type.append("\n");
                }
                builderAserviceCost.append(currency_code).append(" ").append(additionCostModel.cost);
                builderAserviceCharge.append(currency_code).append(" ").append(additionCostModel.cost_client);
                builderAserviceCost_type.append(additionCostModel.cost_type);
            }


            tvforcost.setText(builderAserviceCost.toString());

            tvforcostclient.setText(builderAserviceCharge.toString());

            tvforcosttype.setText(builderAserviceCost_type.toString());
            tvforaddcosttype.setText(builderAserviceCost_type.toString());

            /*Atul*/


            etBalance.setText(balance != null ? balance.trim() : "");
            etBorderCharge.setText(border_charges != null ? border_charges : "0");
            border_charge_include = border_charge_include != null ? border_charge_include : "";
            balance_paid = balance_paid != null ? balance_paid : "";
            // setBalancePaid(balance_paid.trim().toLowerCase());
            if(balance_paid.toLowerCase().trim().equals("yes")){
                chkBoxBalancePaid.setChecked(true);
                chkBoxBalancePaid.setEnabled(false);
                chkBoxBalancePaid.setFocusable(false);
                spinnerListDestCurrencyCode.setFocusable(false);
                spinnerListDestCurrencyCode.setEnabled(false);
            }
            else chkBoxBalancePaid.setChecked(false);
            if(border_charge_include.toLowerCase().trim().equals("no")){
                chBorderCharge.setChecked(true);
                chBorderCharge.setEnabled(false);
                chBorderCharge.setFocusable(false);
                etBorderCharge.setFocusable(false);
                etBorderCharge.setEnabled(false);
            }else{
                chBorderCharge.setChecked(false);
            }


            doc_container.removeAllViews();
            int docCount = docs.size();
            for (int indexDocument = 0; indexDocument < docCount; indexDocument++) {
                TruckDocDetailModel modelTruckDocDetail = docs.get(indexDocument);
                addDocumentToGUI(modelTruckDocDetail, modelTruckDocDetail.data, modelTruckDocDetail.document_name, "");
            }



        }

    }

    private void updateCostValues() {
        String ourCost = etOurCost.getText().toString().trim();
        ourCost = ourCost.isEmpty() ? "0" : ourCost;

        String advance = etAdvance.getText().toString().trim();
        advance = advance.isEmpty() ? "0" : advance;

        double balanceVal = Double.parseDouble(ourCost) - Double.parseDouble(advance);
        etBalance.setText(String.valueOf(balanceVal));

        listDSRTruckDetails.get(currentTab).truckFinancialDetailModel.our_cost = ourCost;
        listDSRTruckDetails.get(currentTab).truckFinancialDetailModel.advance = advance;
        listDSRTruckDetails.get(currentTab).truckFinancialDetailModel.balance = String.valueOf(balanceVal);
    }

    /********* Add Document  ***********/

    private  void gallaryFun(){

        try {

            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            Intent gintent = new Intent();
            gintent.setType("image/*");
            gintent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    i,
                    PICK_IMAGE);
        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
    }
    private void camFunction() {
        // TODO Auto-generated method stub

        // String fileName = "new-photo-name.jpg";

        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //create parameters for Intent with filename
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image captured by camera");
        //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //create new Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, PICK_Camera_IMAGE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri = null;
        String filePath = null;
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImageUri = data.getData();
                    //camScannerApiMethod(selectedImageUri);
                }
                break;
            case PICK_Camera_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    //use imageUri here to access the image
                    selectedImageUri = imageUri;
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Picture was not taken", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Picture was not taken", Toast.LENGTH_SHORT).show();
                }
                break;

        }

        if(selectedImageUri != null){
            try {
                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);

                if (selectedImagePath != null) {
                    filePath = selectedImagePath;
                } else if (filemanagerstring != null) {
                    filePath = filemanagerstring;
                } else {
                    Toast.makeText(getActivity(), "Unknown path",
                            Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }

                decodeFile(filePath);


            } catch (Exception e) {
                Toast.makeText(getActivity(), "Internal error",
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
    public void decodeFile(String filePath) {
        //Bitmap bp=null;
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
        String bitmapString=Utils.BitMapToString(bitmap);
        TruckDocDetailModel truckDocDetailModel = new TruckDocDetailModel();
        String document_name = String.valueOf(System.currentTimeMillis());
        addDocumentToGUI(truckDocDetailModel, bitmapString, document_name, ".png");
        this.listDSRTruckDetails.get(currentTab).truckFinancialDetailModel.documents.add(truckDocDetailModel);

    }
    private void addDocumentToGUI(TruckDocDetailModel docModel,final String data, String document_name, String docTypeExt) {
        document_name = document_name + docTypeExt;
        final String doc_name  = document_name ;
        docModel.document_name = document_name;
        docModel.data = data;
        TextView tvDoc = new TextView(getActivity());
        tvDoc.setText(document_name);
        tvDoc.setHint(document_name);
        tvDoc.setTextColor(Color.WHITE);
        tvDoc.setMaxLines(3);
        tvDoc.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 20, 20, 20);

        tvDoc.setLayoutParams(layoutParams);
        tvDoc.setCompoundDrawablesWithIntrinsicBounds(null, ResourcesCompat.getDrawable(getResources(), R.drawable.doc_file_icn, null), null, null);

        // Document Click Event
        tvDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentHandler documentHandler = new DocumentHandler(getActivity(),AddTruckDetailFragment.this);
                documentHandler.showImage(data,doc_name);
            }
        });

        doc_container.addView(tvDoc);
    }


    /*********************CAM SCANNER API ******************/

}
