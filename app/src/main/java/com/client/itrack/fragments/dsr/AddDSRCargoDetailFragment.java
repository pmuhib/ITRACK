package com.client.itrack.fragments.dsr;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.activities.CategoryContainer;
import com.client.itrack.activities.DSRDetails;
import com.client.itrack.R;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.AdditionCostModel;
import com.client.itrack.model.DSRCargoDetailModel;
import com.client.itrack.model.DSRGeneralDetailModel;
import com.client.itrack.model.DSRTruckDetailModel;

import com.client.itrack.model.TruckDocDetailModel;
import com.client.itrack.model.TruckFinancialDetailModel;

import com.client.itrack.notification.IMethodNotification;
import com.client.itrack.notification.NotificationSendHandler;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DrawerConst;
import com.client.itrack.utility.SocialMedia;
import com.client.itrack.utility.Utility;
import com.client.itrack.utility.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddDSRCargoDetailFragment extends Fragment {

    AppGlobal appGlobal = AppGlobal.getInstance() ;
    View view;
    Spinner spinnerListNumOfTrucks;
    TextView btn_submit_dsr,btn_prev_submit_dsr;
    RelativeLayout numOfTruckContainer;
    DSRDetails dsrDetailsObj;
    DSRCargoDetailModel dsrCargoDetailModel;
    public ArrayList<DSRTruckDetailModel> listDSRTruckDetails = null;
    HashMap<String, TruckFinancialDetailModel> trucksFinancialDetails;

    String dsr_id , action;
    FragmentTabHost tabhost;
    TabWidget widget;
    private int trucksCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.add_dsrdetail_cargo_fragment, container, false);
        dsrDetailsObj = (DSRDetails) getActivity();
        Bundle bundle = getArguments();

        dsr_id = bundle.getString("dsr_id");
        action = bundle.getString("action");

        dsrCargoDetailModel = dsrDetailsObj.dsrCargoDetailModel;
        setUpToolbar();
        setUpHost();

        /************************************
         *  Number Of Trucks List Setup
         * *********************************/
        spinnerListNumOfTrucks = (Spinner) view.findViewById(R.id.spinnerListNumOfTrucks);
        String[] truckCounts = {"Select Trucks Count","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.customlayout, R.id.customtext, truckCounts);
        spinnerListNumOfTrucks.setAdapter(adapter);

        spinnerListNumOfTrucks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tabhost.clearAllTabs();
                if(position>0)
                {
                    setUpTabHost(Integer.parseInt(spinnerListNumOfTrucks.getSelectedItem().toString()));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Submit Complete Cargo Details

        btn_submit_dsr = (TextView) view.findViewById(R.id.btn_submit_dsr);

        btn_submit_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validationDSRCargoData()) {
                    switch (action) {
                        case "add":
                            requestAddDSRDetails();
                            break;

                        case "edit":
                            requestUpdateTruckDSRDetails();
                            break;

                    }
                }


            }
        });


        btn_prev_submit_dsr = (TextView) view.findViewById(R.id.btn_prev_submit_dsr);

        btn_prev_submit_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackAction();
            }
        });
        numOfTruckContainer =  (RelativeLayout)view.findViewById(R.id.numOfTruckContainer);

        uIFeaturesSetting();
        return view;
    }
    private boolean validationDSRCargoData() {
        for (int indexTruckDetails = 0; indexTruckDetails <this.trucksCount ; indexTruckDetails++) {
            if(!this.listDSRTruckDetails.get(indexTruckDetails).isSubmitted)
            {
                Toast.makeText(getActivity(), "Please submit Truck"+(++indexTruckDetails)+" Detail!", Toast.LENGTH_SHORT).show();
                return false ;
            }
        }
        return true;
    }
    // Add New DSR Entry On Server
    private void requestAddDSRDetails() {
        try {
            Utils.showLoadingPopup(getActivity());
            String url = Constants.BASE_URL + "adddsr";
            final String data = createRequestJsonData();
            HttpPostRequest.doPost(getActivity(), url, data, new HttpRequestCallback() {
                @Override
                public void response(String errorMessage, String responseData) {
                    Utils.hideLoadingPopup();
                    try {
                        JSONObject jobj = new JSONObject(responseData);
                        Boolean status = jobj.getBoolean("status");
                        if (status) {
                            String idDSR =  jobj.getString("dsr_id");
                            dsrDetailsObj.dsr_id = idDSR ;
                            Intent intent = new Intent(getActivity(), CategoryContainer.class);
                            intent.putExtra("catpos", DrawerConst.DSR_LIST);
                            startActivity(intent);
                            getActivity().finish();
                            requestSendMailAddDSRNotification();
                            requestAddDSRNotification(idDSR);
                            requestAddDSRNotificationForSelected(idDSR);
                            Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                        Utility.sendReport(getActivity(),"adddsr","Success",data,responseData);

                    } catch (final Exception e) {
                        Utility.sendReport(getActivity(),"adddsr",e.getMessage(),data,responseData);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Utility.sendReport(getActivity(),"adddsr","Error",data,errorMessage);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            DSRGeneralDetailModel dsrGeneralDetailModel = dsrDetailsObj.dsrGeneralDetailModel;
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
    private void requestAddDSRNotification(String dsrId) {
        try {
            DSRGeneralDetailModel dsrGeneralDetailModel = dsrDetailsObj.dsrGeneralDetailModel;
            JSONObject root = new JSONObject();

            root.put("dsr_id",dsrId );
            root.put("employee_id", appGlobal.userId);
            root.put("employee_type",appGlobal.userType);
            root.put("method_name", IMethodNotification.ADD_DSR);

            root.put("client_id", dsrGeneralDetailModel.clientCompId!=null?dsrGeneralDetailModel.clientCompId:"");

            /*******************************
             * Send Notification on Add DSR
             * *****************************/

            NotificationSendHandler sendHandler = new NotificationSendHandler(getActivity());
            sendHandler.sendNotification(IMethodNotification.ADD_DSR,root.toString(),IMethodNotification.CLIENT_USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestAddDSRNotificationForSelected(String dsrId) {
        try {

            JSONObject root = new JSONObject();

            root.put("dsr_id",dsrId );
            root.put("employee_id", appGlobal.userId);
            root.put("employee_type",appGlobal.userType);
            root.put("method_name", IMethodNotification.ADD_DSR);

            /*******************************
             * Send Notification on Add DSR
             * *****************************/

            NotificationSendHandler sendHandler = new NotificationSendHandler(getActivity());
            sendHandler.sendNotification(IMethodNotification.ADD_DSR,root.toString(),IMethodNotification.ADMIN_USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestSendMailAddDSRNotification() {
        try {
            String url = Constants.BASE_URL + "adddsr_email";
            DSRGeneralDetailModel dsrGeneralDetailModel = dsrDetailsObj.dsrGeneralDetailModel;
            JSONObject root = new JSONObject();
            root.put("client_id", dsrGeneralDetailModel.clientCompId!=null?dsrGeneralDetailModel.clientCompId:"");
            HttpPostRequest.doPost(getActivity(), url, root.toString(), new HttpRequestCallback() {
                @Override
                public void response(String errorMessage, String responseData) {
//                    try {
//                        JSONObject jobj = new JSONObject(responseData);
//                        Boolean status = jobj.getBoolean("status");
//                        if (status) {} else {}
//                    } catch (Exception e) {e.printStackTrace();}
                }

                @Override
                public void onError(String errorMessage) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String createRequestJsonData() throws JSONException {

        String data;
        DSRGeneralDetailModel dsrGeneralDetailModel = dsrDetailsObj.dsrGeneralDetailModel;
        DSRCargoDetailModel dsrCargoDetailModel = this.dsrCargoDetailModel;

        JSONObject root = new JSONObject();
        root.put("employee_id",appGlobal.userId);
        root.put("type",appGlobal.userType);
        root.put("client", dsrGeneralDetailModel.clientCompId!=null?dsrGeneralDetailModel.clientCompId:"");
        root.put("dsr_ref_no", dsrGeneralDetailModel.dsrRefNumber!=null?dsrGeneralDetailModel.dsrRefNumber:"");
        root.put("dsr_dates_time", dsrGeneralDetailModel.dsrDateTime!=null?dsrGeneralDetailModel.dsrDateTime:"");
        root.put("client_ref_no", dsrGeneralDetailModel.clientRefNo!=null?dsrGeneralDetailModel.clientRefNo:"");
        root.put("loading_date", dsrGeneralDetailModel.loadingDate!=null?dsrGeneralDetailModel.loadingDate:"");
        root.put("eta", dsrGeneralDetailModel.eta!=null?dsrGeneralDetailModel.eta:"");
        root.put("tt", dsrGeneralDetailModel.tt!=null?dsrGeneralDetailModel.tt:"");
        root.put("loading_point", dsrGeneralDetailModel.loadingPointId!=null?dsrGeneralDetailModel.loadingPointId:"");
        root.put("loading_city", dsrGeneralDetailModel.loading_city!=null?dsrGeneralDetailModel.loading_city:"");
        root.put("loading_country", dsrGeneralDetailModel.loading_country!=null?dsrGeneralDetailModel.loading_country:"");
        root.put("destination_point", dsrGeneralDetailModel.destinatioPointId!=null?dsrGeneralDetailModel.destinatioPointId:"");
        root.put("destination_city", dsrGeneralDetailModel.destination_city!=null?dsrGeneralDetailModel.destination_city:"");
        root.put("destination_country", dsrGeneralDetailModel.destination_country!=null ?dsrGeneralDetailModel.destination_country:"");
        root.put("customs", dsrGeneralDetailModel.customs!=null?dsrGeneralDetailModel.customs:"");
        root.put("date_reached_customs", dsrGeneralDetailModel.date_reached_custom!=null?dsrGeneralDetailModel.date_reached_custom:"");
        root.put("status", dsrGeneralDetailModel.dsrStatusId);
        root.put("remark", dsrGeneralDetailModel.remark!=null?dsrGeneralDetailModel.remark:"");
        root.put("no_of_truck", this.trucksCount);

        ArrayList<DSRTruckDetailModel> listDSRTruckDetails = this.listDSRTruckDetails;
        JSONArray cargoColl = new JSONArray();
        for (int indexDSRTruckObj = 0; indexDSRTruckObj < listDSRTruckDetails.size(); indexDSRTruckObj++) {
            JSONObject truckDetailJsonObj = new JSONObject();
            DSRTruckDetailModel dsrTruckDetailModel = listDSRTruckDetails.get(indexDSRTruckObj);

            /**********************************
             *  TRUCK DETAILS
             * ********************************/

            truckDetailJsonObj.put("id", dsr_id);
            truckDetailJsonObj.put("driver_name", dsrTruckDetailModel.driver_name!=null?dsrTruckDetailModel.driver_name:"");

            truckDetailJsonObj.put("driver_phone", dsrTruckDetailModel.driver_phone!=null?dsrTruckDetailModel.driver_phone:"");
            truckDetailJsonObj.put("driver_phone1", dsrTruckDetailModel.driver_phone1!=null?dsrTruckDetailModel.driver_phone1:"");
            truckDetailJsonObj.put("driver_phone2", dsrTruckDetailModel.driver_phone2!=null?dsrTruckDetailModel.driver_phone2:"");
            truckDetailJsonObj.put("type_of_trailor", dsrTruckDetailModel.trailor_type!=null?dsrTruckDetailModel.trailor_type:"");
            truckDetailJsonObj.put("vehicle_number", dsrTruckDetailModel.vehicle_number!=null?dsrTruckDetailModel.vehicle_number:"");
            truckDetailJsonObj.put("offloading_date", dsrTruckDetailModel.off_loading_date!=null?dsrTruckDetailModel.off_loading_date:"");
            truckDetailJsonObj.put("detention", dsrTruckDetailModel.detention!=null?dsrTruckDetailModel.detention:"0");
            truckDetailJsonObj.put("detention_client", dsrTruckDetailModel.detention_client!=null?dsrTruckDetailModel.detention_client:"0"); // [20-02-2017]
            //truckDetailJsonObj.put("detention_charge", dsrTruckDetailModel.detention_cost!=null?dsrTruckDetailModel.detention_cost:"0"); // new
            truckDetailJsonObj.put("detention_rate", dsrTruckDetailModel.detention_rate!=null?dsrTruckDetailModel.detention_rate:""); // new
            truckDetailJsonObj.put("detention_rate_client", dsrTruckDetailModel.detention_rate_client!=null?dsrTruckDetailModel.detention_rate_client:""); // new // [20-02-2017]
            truckDetailJsonObj.put("code_phone_no", dsrTruckDetailModel.code_phone_no!=null?dsrTruckDetailModel.code_phone_no:"");// new
            truckDetailJsonObj.put("code_phone_no1", dsrTruckDetailModel.code_phone_no1!=null?dsrTruckDetailModel.code_phone_no1:"");
            truckDetailJsonObj.put("code_phone_no2", dsrTruckDetailModel.code_phone_no2!=null?dsrTruckDetailModel.code_phone_no2:"");
            truckDetailJsonObj.put("currency_code", dsrTruckDetailModel.currency_code!=null?dsrTruckDetailModel.currency_code:"");// new
            truckDetailJsonObj.put("current_location", dsrTruckDetailModel.truck_current_location!=null?dsrTruckDetailModel.truck_current_location:"");// new

            truckDetailJsonObj.put("remark", dsrTruckDetailModel.remark!=null?dsrTruckDetailModel.remark:"");
            truckDetailJsonObj.put("truck_status", dsrTruckDetailModel.truck_status!=null?dsrTruckDetailModel.truck_status:"");
            truckDetailJsonObj.put("delivery_note_recived_date", dsrTruckDetailModel.del_receive_date!=null?dsrTruckDetailModel.del_receive_date:"");

            /**********************************
             *  FINANCIAL
             * ********************************/
            TruckFinancialDetailModel truckFinancialDetailModel = dsrTruckDetailModel.truckFinancialDetailModel;
            truckDetailJsonObj.put("our_price_to_customer", truckFinancialDetailModel.our_price_customer);
            truckDetailJsonObj.put("our_cost", truckFinancialDetailModel.our_cost);
            truckDetailJsonObj.put("advance", truckFinancialDetailModel.advance);
            truckDetailJsonObj.put("balance", truckFinancialDetailModel.balance);
            truckDetailJsonObj.put("border_charges", truckFinancialDetailModel.border_charges);
            truckDetailJsonObj.put("balance_paid", truckFinancialDetailModel.balance_paid);
            truckDetailJsonObj.put("border_charge_include", truckFinancialDetailModel.border_charge_include);
            /**********************************
             *  DOCUMENTS DETAILS
             * ********************************/

            JSONArray docsJsonArr = new JSONArray();
            ArrayList<TruckDocDetailModel> documents = truckFinancialDetailModel.documents;
            if(documents !=null) {
                for (int indexDoc = 0; indexDoc < documents.size(); indexDoc++) {
                    JSONObject jsonObjDoc = new JSONObject();
                    TruckDocDetailModel document = documents.get(indexDoc);
                    jsonObjDoc.put("doc" + indexDoc, document.data);
                    docsJsonArr.put(jsonObjDoc);
                }
            }
            truckDetailJsonObj.put("doc", docsJsonArr);
            cargoColl.put(truckDetailJsonObj);
        }

        root.put("Cargo", cargoColl);

        data = root.toString();

        return data;
    }

    private void uIFeaturesSetting() {

        switch (action) {
            case "edit":
                spinnerListNumOfTrucks.setFocusable(false);
                spinnerListNumOfTrucks.setEnabled(false);

                btn_submit_dsr.setText("Update DSR");
                numOfTruckContainer.setVisibility(View.GONE);
                break;

            case "add":
                // spinnerListNumOfTrucks.setSelection(1);
                btn_submit_dsr.setText("Submit DSR");
                numOfTruckContainer.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (dsr_id != null) {

            // Edit Functioning
            if (this.listDSRTruckDetails == null) {
                // Edit after getting data
                if (Utils.isNetworkConnected(getActivity(),false)) {
                    // Load Cargo Details  using dsr_id
                    loadDSRCargoDetails(dsr_id);
                }

            }
            else { /* //Edit data Already have  spinnerListNumOfTrucks.setSelection(this.listDSRTruckDetails.size());*/
                tabhost.clearAllTabs();
                setUpTabHost(this.listDSRTruckDetails.size()); }
        } else {
            // Add Functioning
            if (this.listDSRTruckDetails == null) {
                this.listDSRTruckDetails = new ArrayList<>();
            }
            spinnerListNumOfTrucks.setSelection(1);
        }
    }

    // Load DSR Cargo Details
    private void loadDSRCargoDetails(String dsr_id) {

        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL + "truck_detail_by_dsr_id";

        HashMap<String, String> hm = new HashMap<>();
        hm.put("dsr_id", dsr_id);

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(hm), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");

                    if (status) {
                        JSONArray trucksDecDetails = jobj.getJSONArray("document_detail");

                        JSONArray truckSDetailsArr = jobj.getJSONArray("truck_detail");
                        int numberOfTrucks = truckSDetailsArr.length();
                        trucksFinancialDetails = new HashMap<>();
                        //  dsrCargoDetailModel.numberOfTrucks = numberOfTrucks;

                        ArrayList<DSRTruckDetailModel> listDSRTruckDetails = new ArrayList<DSRTruckDetailModel>();
                        // Adding Tabs At runtime in Tab Host

                        for (int indexTruckDetailObj = 0; indexTruckDetailObj < numberOfTrucks; indexTruckDetailObj++) {

                            JSONObject truckDetailObj = truckSDetailsArr.getJSONObject(indexTruckDetailObj);

                            DSRTruckDetailModel dsrTruckDetailModel = new DSRTruckDetailModel();

                            String dsr_truck_id = truckDetailObj.getString("dsr_truck_id");
                            String dsr_id = truckDetailObj.getString("dsr_id");
                            String driver_name = truckDetailObj.getString("driver_name");
                            String driver_phone = truckDetailObj.getString("driver_phone");
                            String driver_phone1 = truckDetailObj.getString("driver_phone1");
                            String driver_phone2 = truckDetailObj.getString("driver_phone2");
                            String trailor_type = truckDetailObj.getString("trailor_type");
                            String vehicle_number = truckDetailObj.getString("vehicle_number");
                            String off_loading_date = truckDetailObj.getString("off_loading_date");
                            String detention = truckDetailObj.getString("detention");
                            String detention_client = truckDetailObj.getString("detention_client");
                            String del_receive_date = truckDetailObj.getString("del_recive_date");
                            String truck_status = truckDetailObj.getString("truck_status");
                            String remark = truckDetailObj.getString("remark");
                            String delete_status = truckDetailObj.getString("delete_status");
                            String create_date = truckDetailObj.getString("create_date");

                            String code_phone_no = truckDetailObj.getString("code_phone_no");
                            String code_phone_no1 = truckDetailObj.getString("code_phone_no1");
                            String code_phone_no2 = truckDetailObj.getString("code_phone_no2");
                            String currency_code = truckDetailObj.getString("currency_code");
                            String dest_currency_code = truckDetailObj.getString("dest_currency_code");
                            String current_location = truckDetailObj.getString("current_location");
                            String detention_rate = truckDetailObj.getString("detention_rate");
                            String detention_rate_client = truckDetailObj.getString("detention_rate_client");
                            String detention_location = truckDetailObj.getString("detention_location");
                            dsrTruckDetailModel.dsr_truck_id = dsr_truck_id;
                            dsrTruckDetailModel.dsr_id = dsr_id;
                            dsrTruckDetailModel.driver_name = driver_name;
                            dsrTruckDetailModel.driver_phone = driver_phone;
                            dsrTruckDetailModel.driver_phone1 = driver_phone1;
                            dsrTruckDetailModel.driver_phone2 = driver_phone2;
                            dsrTruckDetailModel.trailor_type = trailor_type;
                            dsrTruckDetailModel.vehicle_number = vehicle_number;
                            dsrTruckDetailModel.off_loading_date = off_loading_date;
                            dsrTruckDetailModel.detention = detention;
                            dsrTruckDetailModel.detention_client = detention_client; // [20-02-2017]
                            dsrTruckDetailModel.del_receive_date = del_receive_date;
                            dsrTruckDetailModel.truck_status = truck_status;
                            dsrTruckDetailModel.remark = remark;
                            dsrTruckDetailModel.delete_status = delete_status;
                            dsrTruckDetailModel.create_date = create_date;

                            dsrTruckDetailModel.code_phone_no = code_phone_no;
                            dsrTruckDetailModel.code_phone_no1 = code_phone_no1;
                            dsrTruckDetailModel.code_phone_no2 = code_phone_no2;
                            dsrTruckDetailModel.currency_code = currency_code;
                            dsrTruckDetailModel.dest_currency_code = dest_currency_code; // [28-02-2017]
                            dsrTruckDetailModel.truck_current_location = current_location;
                            dsrTruckDetailModel.detention_rate = detention_rate;
                            dsrTruckDetailModel.detention_rate_client = detention_rate_client; // [20-02-2017]
                            dsrTruckDetailModel.detention_rate = detention_rate;
                            TruckFinancialDetailModel modelTruckFinancialDetail = new TruckFinancialDetailModel();
                            dsrTruckDetailModel.detention_location = detention_location;
                            // Get Document Details

                            for (int indexTruckDocs = 0; indexTruckDocs < trucksDecDetails.length(); indexTruckDocs++) {
                                JSONObject truckDocsObj = trucksDecDetails.getJSONObject(indexTruckDocs);
                                TruckDocDetailModel truckDocDetail = new TruckDocDetailModel();
                                if (truckDocsObj.getString("dsr_truck_id").equals(truckDetailObj.getString("dsr_truck_id"))) {
                                    truckDocDetail.document_id = truckDocsObj.getString("document_id");
                                    truckDocDetail.document_name = truckDocsObj.getString("document_name");
                                    String regex = "data:image/.+;base64,";
                                    truckDocDetail.data = truckDocsObj.getString("document").replaceAll(regex,"");
                                    truckDocDetail.delete_status = truckDocsObj.getString("delete_status");
                                    truckDocDetail.create_date = truckDocsObj.getString("create_date");
                                    modelTruckFinancialDetail.documents.add(truckDocDetail);
                                }
                            }

                            /***** cost ******/

                            JSONArray cost=truckDetailObj.getJSONArray("cost");
                            JSONArray jcost_client=truckDetailObj.getJSONArray("cost_client");
                            JSONArray jcost_type=truckDetailObj.getJSONArray("cost_type");

                            List<AdditionCostModel> costModelList = new ArrayList<>();
                            if((cost.length() == jcost_client.length()) && (jcost_client.length() == jcost_type.length()))
                            {
                                for (int i = 0; i < cost.length(); i++) {
                                    JSONObject innercost = cost.getJSONObject(i);
                                    JSONObject innercost_client = jcost_client.getJSONObject(i);
                                    JSONObject innercost_type = jcost_type.getJSONObject(i);
                                    AdditionCostModel additionCostModel = new AdditionCostModel();
                                    additionCostModel.cost = innercost.getString("cost");
                                    additionCostModel.cost_client = innercost_client.getString("cost_client");
                                    additionCostModel.cost_type = innercost_type.getString("cost_type") ;
                                    costModelList.add(additionCostModel);
                                }
                            }
                            dsrTruckDetailModel.listAdditionalCost = costModelList ;
                            // Financial Details
                            modelTruckFinancialDetail.our_price_customer = truckDetailObj.getString("our_price_customer");
                            modelTruckFinancialDetail.our_cost = truckDetailObj.getString("our_cost");
                            modelTruckFinancialDetail.advance = truckDetailObj.getString("advance");
                            modelTruckFinancialDetail.balance = truckDetailObj.getString("balance");
                            modelTruckFinancialDetail.border_charges = truckDetailObj.getString("border_charges");
                            modelTruckFinancialDetail.balance_paid = truckDetailObj.getString("balance_paid");
                            modelTruckFinancialDetail.border_charge_include=truckDetailObj.getString("border_charge_include");
                            trucksFinancialDetails.put("truck" + (indexTruckDetailObj + 1), modelTruckFinancialDetail);

                            dsrTruckDetailModel.truckFinancialDetailModel = modelTruckFinancialDetail;
                            listDSRTruckDetails.add(dsrTruckDetailModel);
                        }

                        //dsrCargoDetailModel.listDSRTruckDetails = listDSRTruckDetails;
                        AddDSRCargoDetailFragment.this.listDSRTruckDetails = listDSRTruckDetails;
                        //dsrDetailsObj.dsrCargoDetailModel = dsrCargoDetailModel;

                        tabhost.clearAllTabs();
                        setUpTabHost(numberOfTrucks);

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
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.dsr_toolbar);
        // mTitle = (TextView) (toolbar.findViewById(R.id.txt_heading));

        ImageView clientDetailEdit = (ImageView) toolbar.findViewById(R.id.client_detail_edit);
        clientDetailEdit.setVisibility(View.GONE);

        ImageView btn_nav = (ImageView) toolbar.findViewById(R.id.btn_navigation);
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackAction();
            }
        });
        btn_nav.setVisibility(View.GONE);
    }

    private void goBackAction() {
        switch(dsrDetailsObj.indexActivatedView) {
            case 0 :

                break ;
            case 1 :
                dsrDetailsObj.indexActivatedView = 0;
                dsrDetailsObj.doAction();
                break ;
        }
    }

    private void setUpHost() {
        tabhost = (FragmentTabHost) view.findViewById(R.id.tabhost);
        widget = (TabWidget) view.findViewById(android.R.id.tabs);

        try {
            tabhost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

            tabhost.addTab(tabhost.newTabSpec("truck").setIndicator("Truck"),
                    AddTruckDetailFragment.class, null);
            tabhost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.truck_tab_bg);
            tabhost.getTabWidget().getChildAt(0).setLayoutParams(new LinearLayout.LayoutParams(300, 100));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // Set Up Tab Host into Fragment Using Fragment tab Host
    private void setUpTabHost(int trucksCount) {

        this.trucksCount =  trucksCount ;

        int numberOfTrucks;
        numberOfTrucks = listDSRTruckDetails.size();

        try {

            for (int indexTruck = 0; indexTruck < trucksCount; indexTruck++) {

                if (indexTruck < numberOfTrucks) {
                    // Edit Case and  Add case at condition when Some of the

                    DSRTruckDetailModel dsrTruckDetailModel = listDSRTruckDetails.get(indexTruck);
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("action", action);
                    bundle.putCharSequence("dsr_truck_id", dsrTruckDetailModel.dsr_truck_id);
                    bundle.putCharSequence("dsr_id", dsrTruckDetailModel.dsr_id);
                    bundle.putCharSequence("driver_name", dsrTruckDetailModel.driver_name);
                    bundle.putCharSequence("driver_phone", dsrTruckDetailModel.driver_phone);
                    bundle.putCharSequence("driver_phone1", dsrTruckDetailModel.driver_phone1);
                    bundle.putCharSequence("driver_phone2", dsrTruckDetailModel.driver_phone2);
                    bundle.putCharSequence("trailor_type", dsrTruckDetailModel.trailor_type);
                    bundle.putCharSequence("vehicle_number", dsrTruckDetailModel.vehicle_number);
                    bundle.putCharSequence("off_loading_date", dsrTruckDetailModel.off_loading_date);
                    bundle.putCharSequence("detention", dsrTruckDetailModel.detention);
                    bundle.putCharSequence("detention_client", dsrTruckDetailModel.detention_client);  // [20-02-2017]
                    bundle.putCharSequence("del_recive_date", dsrTruckDetailModel.del_receive_date);
                    bundle.putCharSequence("truck_status", dsrTruckDetailModel.truck_status);
                    bundle.putCharSequence("remark", dsrTruckDetailModel.remark);
                    bundle.putCharSequence("delete_status", dsrTruckDetailModel.delete_status);
                    bundle.putCharSequence("create_date", dsrTruckDetailModel.create_date);
                    bundle.putCharSequence("currency_code", dsrTruckDetailModel.currency_code);
                    bundle.putCharSequence("dest_currency_code", dsrTruckDetailModel.dest_currency_code);
                    bundle.putCharSequence("truck_current_location", dsrTruckDetailModel.truck_current_location);
                    bundle.putCharSequence("detention_rate", dsrTruckDetailModel.detention_rate);
                    bundle.putCharSequence("detention_rate_client", dsrTruckDetailModel.detention_rate_client); // [20-02-2017]
                    bundle.putCharSequence("detention_location", dsrTruckDetailModel.detention_location);
                    bundle.putCharSequence("code_phone_no", dsrTruckDetailModel.code_phone_no);
                    bundle.putCharSequence("code_phone_no1", dsrTruckDetailModel.code_phone_no1);
                    bundle.putCharSequence("code_phone_no2", dsrTruckDetailModel.code_phone_no2);
                    // Change
                    bundle.putCharSequence("our_price_customer", dsrTruckDetailModel.truckFinancialDetailModel.our_price_customer);
                    bundle.putCharSequence("our_cost", dsrTruckDetailModel.truckFinancialDetailModel.our_cost);
                    bundle.putCharSequence("advance", dsrTruckDetailModel.truckFinancialDetailModel.advance);
                    bundle.putCharSequence("balance", dsrTruckDetailModel.truckFinancialDetailModel.balance);
                    bundle.putCharSequence("border_charges", dsrTruckDetailModel.truckFinancialDetailModel.border_charges);
                    bundle.putCharSequence("balance_paid", dsrTruckDetailModel.truckFinancialDetailModel.balance_paid);
                    bundle.putCharSequence("border_charge_include", dsrTruckDetailModel.truckFinancialDetailModel.border_charge_include);

                    bundle.putSerializable("docs",dsrTruckDetailModel.truckFinancialDetailModel.documents);
                    bundle.putSerializable("other_costs", (Serializable) dsrTruckDetailModel.listAdditionalCost);




                    tabhost.addTab(tabhost.newTabSpec("truck" + (indexTruck + 1)).setIndicator("Truck " + (indexTruck + 1)),
                            AddTruckDetailFragment.class, bundle);
                    // Styling Tab
                    View viewTab  = tabhost.getTabWidget().getChildAt(indexTruck);
                    viewTab.setBackgroundResource(R.drawable.truck_tab_bg);
                    TextView tabTextView =  (TextView) viewTab.findViewById(android.R.id.title);
                    if (Build.VERSION.SDK_INT < 23) {
                        tabTextView.setTextAppearance(getActivity(), R.style.GLTabStyle);
                        viewTab.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT ));
                    } else{
                        tabTextView.setTextAppearance(R.style.GLTabStyle);
                        viewTab.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 90));
                    }
                    tabTextView.setAllCaps(false);
                    // viewTab.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                    tabhost.getTabWidget().getChildAt(indexTruck).setBackgroundResource(R.drawable.truck_tab_bg);
//                    tabhost.getTabWidget().getChildAt(indexTruck).setLayoutParams(new LinearLayout.LayoutParams(300, 100));
                } else {
                    trucksFinancialDetails = new HashMap<>();
                    DSRTruckDetailModel dsrTruckDetailModel = new DSRTruckDetailModel();
                    dsrTruckDetailModel.dsr_truck_id = "";
                    dsrTruckDetailModel.dsr_id = "";
                    dsrTruckDetailModel.driver_name ="";
                    dsrTruckDetailModel.driver_phone="";
                    dsrTruckDetailModel.driver_phone1="";
                    dsrTruckDetailModel.driver_phone2="";
                    dsrTruckDetailModel.trailor_type="";
                    dsrTruckDetailModel.vehicle_number="" ;
                    dsrTruckDetailModel.off_loading_date="";
                    dsrTruckDetailModel.detention="";
                    dsrTruckDetailModel.detention_client="0"; // [20-02-2017]
                    dsrTruckDetailModel.del_receive_date = "";
                    dsrTruckDetailModel.truck_status= "" ;
                    dsrTruckDetailModel.remark ="";
                    dsrTruckDetailModel.delete_status = "";
                    dsrTruckDetailModel.create_date="";
                    /*****************************************/
                    dsrTruckDetailModel.currency_code = "" ;
                    dsrTruckDetailModel.dest_currency_code = "" ;
                    dsrTruckDetailModel.truck_current_location = "" ;
                    dsrTruckDetailModel.code_phone_no = "";
                    dsrTruckDetailModel.code_phone_no1 = "";
                    dsrTruckDetailModel.code_phone_no2 = "";
                    dsrTruckDetailModel.detention_rate = "0" ;
                    dsrTruckDetailModel.detention_rate_client = "0" ; // [20-02-2017]
                    dsrTruckDetailModel.detention_location="";
                    /*****************************************/
                    AdditionCostModel additionCostModel = new AdditionCostModel();
                    additionCostModel.cost = "";
                    additionCostModel.cost_client = "";
                    additionCostModel.cost_type = "";
                    List<AdditionCostModel> listAdditionalCost = new ArrayList<>();
                    dsrTruckDetailModel.listAdditionalCost = listAdditionalCost;
                    TruckFinancialDetailModel modelTruckFinancialDetail = new TruckFinancialDetailModel();
                    modelTruckFinancialDetail.our_price_customer = "";
                    modelTruckFinancialDetail.our_cost = "";
                    modelTruckFinancialDetail.advance = "" ;
                    modelTruckFinancialDetail.balance_paid = "No";
                    modelTruckFinancialDetail.balance = "";
                    modelTruckFinancialDetail.border_charges = "";
                    modelTruckFinancialDetail.border_charge_include="Yes";
                    ArrayList<TruckDocDetailModel> listDocDetail = new ArrayList<>();
                    modelTruckFinancialDetail.documents = listDocDetail;
                    dsrTruckDetailModel.truckFinancialDetailModel = modelTruckFinancialDetail;
                    this.listDSRTruckDetails.add(dsrTruckDetailModel);
                    trucksFinancialDetails.put("truck" + (indexTruck + 1), modelTruckFinancialDetail); // Remove able
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("action", action);
                    /*****************************************/
                    bundle.putCharSequence("dsr_truck_id", dsrTruckDetailModel.dsr_truck_id);
                    bundle.putCharSequence("dsr_id", dsrTruckDetailModel.dsr_id);
                    bundle.putCharSequence("driver_name", dsrTruckDetailModel.driver_name);
                    bundle.putCharSequence("driver_phone", dsrTruckDetailModel.driver_phone);
                    bundle.putCharSequence("driver_phone1", dsrTruckDetailModel.driver_phone1);
                    bundle.putCharSequence("driver_phone2", dsrTruckDetailModel.driver_phone2);
                    bundle.putCharSequence("trailor_type", dsrTruckDetailModel.trailor_type);
                    bundle.putCharSequence("vehicle_number", dsrTruckDetailModel.vehicle_number);
                    bundle.putCharSequence("off_loading_date", dsrTruckDetailModel.off_loading_date);
                    bundle.putCharSequence("detention", dsrTruckDetailModel.detention);
                    bundle.putCharSequence("detention_client", dsrTruckDetailModel.detention_client); // [20-02-
                    bundle.putCharSequence("detention_location", dsrTruckDetailModel.detention_location);
                    bundle.putCharSequence("del_recive_date", dsrTruckDetailModel.del_receive_date);
                    bundle.putCharSequence("truck_status", dsrTruckDetailModel.truck_status);
                    bundle.putCharSequence("remark", dsrTruckDetailModel.remark);
                    bundle.putCharSequence("delete_status", dsrTruckDetailModel.delete_status);
                    bundle.putCharSequence("create_date", dsrTruckDetailModel.create_date);

                    /****************************************************/
                    bundle.putCharSequence("currency_code", dsrTruckDetailModel.currency_code);
                    bundle.putCharSequence("dest_currency_code", dsrTruckDetailModel.dest_currency_code);
                    bundle.putCharSequence("truck_current_location", dsrTruckDetailModel.truck_current_location);
                    bundle.putCharSequence("detention_rate", dsrTruckDetailModel.detention_rate);
                    bundle.putCharSequence("detention_rate_client", dsrTruckDetailModel.detention_rate_client); // [20-02-2017]
                    bundle.putCharSequence("code_phone_no", dsrTruckDetailModel.code_phone_no);
                    bundle.putCharSequence("code_phone_no1", dsrTruckDetailModel.code_phone_no1);
                    bundle.putCharSequence("code_phone_no2", dsrTruckDetailModel.code_phone_no2);
                    /****************************************************/

                    // Change
                    bundle.putCharSequence("our_price_customer", dsrTruckDetailModel.truckFinancialDetailModel.our_price_customer);
                    bundle.putCharSequence("our_cost", dsrTruckDetailModel.truckFinancialDetailModel.our_cost);
                    bundle.putCharSequence("advance", dsrTruckDetailModel.truckFinancialDetailModel.advance);
                    bundle.putCharSequence("balance", dsrTruckDetailModel.truckFinancialDetailModel.balance);
                    bundle.putCharSequence("border_charges", dsrTruckDetailModel.truckFinancialDetailModel.border_charges);
                    bundle.putCharSequence("balance_paid", dsrTruckDetailModel.truckFinancialDetailModel.balance_paid);
                    bundle.putCharSequence("border_charge_include", dsrTruckDetailModel.truckFinancialDetailModel.border_charge_include);
                    bundle.putSerializable("docs",dsrTruckDetailModel.truckFinancialDetailModel.documents);
                    bundle.putSerializable("other_costs", (Serializable) dsrTruckDetailModel.listAdditionalCost);
                    /****************************************************************************************/
                    tabhost.addTab(tabhost.newTabSpec("truck" + (indexTruck + 1)).setIndicator("Truck " + (indexTruck + 1)),
                            AddTruckDetailFragment.class, bundle);
                    /// Styling Tab
                    View viewTab  = tabhost.getTabWidget().getChildAt(indexTruck);
                    viewTab.setBackgroundResource(R.drawable.truck_tab_bg);
                    TextView tabTextView =  (TextView) viewTab.findViewById(android.R.id.title);
                    if (Build.VERSION.SDK_INT < 23) {
                        tabTextView.setTextAppearance(getActivity(), R.style.GLTabStyle);
                        viewTab.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT ));
                    } else{
                        tabTextView.setTextAppearance(R.style.GLTabStyle);
                        viewTab.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 90));
                    }
                    tabTextView.setAllCaps(false);
                    //viewTab.setLayoutParams(new LinearLayout.LayoutParams(250, LinearLayout.LayoutParams.WRAP_CONTENT));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
    //  Fragment Replacement On demand
//    private void replaceFragment(int oldFragmentId, Fragment registerFragments) {
//        FragmentManager fm = getChildFragmentManager();
//        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//        fragmentTransaction.replace(oldFragmentId, registerFragments);
//        // fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
//    }
    // Request For updating Trucks details DSR on Server
    private void requestUpdateTruckDSRDetails() {

        String data = null;
        try {
            data = createUpdateReqJsonData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL + "update_truck_detail";

        HttpPostRequest.doPost(getActivity(), url, data, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {

                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        /*dsrDetailsObj.indexActivatedView = 1;
                        dsrDetailsObj.doAction()*/;
                        requestUpdateDSRNotification(dsr_id);
                        requestUpdateDSRNotificationForSelected(dsr_id);
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

    // Update Cargo Detail On Server
    private String createUpdateReqJsonData() throws JSONException {

        String data;
        //DSRCargoDetailModel dsrCargoDetailModel = this.dsrCargoDetailModel;
        JSONObject root = new JSONObject();

        root.put("dsr_id", dsr_id);
        root.put("employee_id",appGlobal.userId);
        root.put("type",appGlobal.userType);

        JSONObject cargoDetailJsonObj = new JSONObject();

        ArrayList<DSRTruckDetailModel> listDSRTruckDetails = this.listDSRTruckDetails;
        JSONArray trucksDetailJsonArr = new JSONArray();

        for (int indexDSRTruckObj = 0; indexDSRTruckObj < listDSRTruckDetails.size(); indexDSRTruckObj++) {
            JSONObject truckDetailJsonObj = new JSONObject();
            DSRTruckDetailModel dsrTruckDetailModel = listDSRTruckDetails.get(indexDSRTruckObj);

            /**********************************
             *  TRUCK DETAILS
             **********************************/
            truckDetailJsonObj.put("driver_name", dsrTruckDetailModel.driver_name);
            truckDetailJsonObj.put("dsr_truck_id", dsrTruckDetailModel.dsr_truck_id);
            truckDetailJsonObj.put("detention_charge", dsrTruckDetailModel.detention_cost);
            truckDetailJsonObj.put("driver_name", dsrTruckDetailModel.driver_name);
            truckDetailJsonObj.put("driver_phone", dsrTruckDetailModel.driver_phone);
            truckDetailJsonObj.put("driver_phone1", dsrTruckDetailModel.driver_phone1);
            truckDetailJsonObj.put("driver_phone2", dsrTruckDetailModel.driver_phone2);
            truckDetailJsonObj.put("type_of_trailor", dsrTruckDetailModel.trailor_type);
            truckDetailJsonObj.put("vehicle_number", dsrTruckDetailModel.vehicle_number);
            truckDetailJsonObj.put("offloading_date", dsrTruckDetailModel.off_loading_date);
            truckDetailJsonObj.put("detention", dsrTruckDetailModel.detention);
            truckDetailJsonObj.put("detention_client", dsrTruckDetailModel.detention_client);  // [20-02-2017]
            truckDetailJsonObj.put("remark", dsrTruckDetailModel.remark);
            truckDetailJsonObj.put("truck_status", dsrTruckDetailModel.truck_status);
            truckDetailJsonObj.put("delivery_recived_date", dsrTruckDetailModel.del_receive_date);
            truckDetailJsonObj.put("detention_location",dsrTruckDetailModel.detention_location);

            /****************************New*************************************/
            truckDetailJsonObj.put("code_phone_no", dsrTruckDetailModel.code_phone_no!=null?dsrTruckDetailModel.code_phone_no:"");// new
            truckDetailJsonObj.put("code_phone_no1", dsrTruckDetailModel.code_phone_no1!=null?dsrTruckDetailModel.code_phone_no1:"");// new
            truckDetailJsonObj.put("code_phone_no2", dsrTruckDetailModel.code_phone_no2!=null?dsrTruckDetailModel.code_phone_no2:"");// new
            truckDetailJsonObj.put("current_location", dsrTruckDetailModel.truck_current_location!=null?dsrTruckDetailModel.truck_current_location:"");// new
            /****************************New*************************************/


            /*********** FINANCIAL DETAILS ******************/
            JSONObject truckFinancialJsonObj = new JSONObject();

            TruckFinancialDetailModel truckFinancialDetailModel = dsrTruckDetailModel.truckFinancialDetailModel;
            truckFinancialJsonObj.put("detention_rate", dsrTruckDetailModel.detention_rate!=null?dsrTruckDetailModel.detention_rate:"0"); // new
            truckFinancialJsonObj.put("detention_rate_client", dsrTruckDetailModel.detention_rate_client!=null?dsrTruckDetailModel.detention_rate_client:"0"); // new // [20-02-2017]

            truckFinancialJsonObj.put("our_price_to_customer", truckFinancialDetailModel.our_price_customer);
            truckFinancialJsonObj.put("our_cost", truckFinancialDetailModel.our_cost);
            truckFinancialJsonObj.put("advance", truckFinancialDetailModel.advance);
            truckFinancialJsonObj.put("balance", truckFinancialDetailModel.balance);
            truckFinancialJsonObj.put("border_charge_include", truckFinancialDetailModel.border_charge_include);
            truckFinancialJsonObj.put("border_charges", truckFinancialDetailModel.border_charges);
            truckFinancialJsonObj.put("paid_status", truckFinancialDetailModel.balance_paid);
            truckFinancialJsonObj.put("currency_code", dsrTruckDetailModel.currency_code!=null?dsrTruckDetailModel.currency_code:"");// new
            truckFinancialJsonObj.put("dest_currency_code", dsrTruckDetailModel.dest_currency_code!=null?dsrTruckDetailModel.dest_currency_code:"");// new

            JSONArray truckAdditionalCostJsonObj = new JSONArray();
            List<AdditionCostModel> additionCostModelList =  dsrTruckDetailModel.listAdditionalCost ;
            if(additionCostModelList!=null) {

                for (int indexOtherCost = 0; indexOtherCost < additionCostModelList.size(); indexOtherCost++) {
                    JSONObject otherCostJsonObj = new JSONObject();
                    AdditionCostModel otherCostModel = additionCostModelList.get(indexOtherCost);
                    otherCostJsonObj.put("cost", otherCostModel.cost);
                    otherCostJsonObj.put("cost_client", otherCostModel.cost_client);
                    otherCostJsonObj.put("cost_type", otherCostModel.cost_type);

                    truckAdditionalCostJsonObj.put(indexOtherCost, otherCostJsonObj);
                    additionCostModelList.get(indexOtherCost);
                }

            }
            truckDetailJsonObj.put("other_amounts",truckAdditionalCostJsonObj) ;

            /********** DOCUMENTS DETAILS ***********/
            JSONArray docsJsonArr = new JSONArray();
            ArrayList<TruckDocDetailModel> documents = truckFinancialDetailModel.documents;

            for (int indexDoc = 0; indexDoc < documents.size(); indexDoc++) {
                JSONObject jsonObjDoc = new JSONObject();
                TruckDocDetailModel document = documents.get(indexDoc);
                jsonObjDoc.put("doc" + (indexDoc + 1), document.data == null ? "" : document.data);
                docsJsonArr.put(jsonObjDoc);
            }

            truckFinancialJsonObj.put("doc", docsJsonArr);
            /******** DOCUMENTS DETAILS End *************/

            truckDetailJsonObj.put("finance_details", truckFinancialJsonObj);

            /*********** FINANCIAL DETAILS END ******************/
            trucksDetailJsonArr.put(truckDetailJsonObj);

        }
        cargoDetailJsonObj.put("trucks", trucksDetailJsonArr);
        root.put("cargo_dsr_detail", cargoDetailJsonObj);

        data = root.toString();

        return data;
    }
}
