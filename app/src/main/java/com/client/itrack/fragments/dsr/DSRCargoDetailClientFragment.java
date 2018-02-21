package com.client.itrack.fragments.dsr;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.activities.DSRDetails;
import com.client.itrack.R;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.AdditionCostModel;
import com.client.itrack.model.DSRCargoDetailModel;
import com.client.itrack.model.DSRTruckDetailModel;
import com.client.itrack.model.TrailerTypeModel;
import com.client.itrack.model.TruckDocDetailModel;
import com.client.itrack.model.TruckFinancialDetailModel;
import com.client.itrack.model.TruckStatusModel;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DSRCargoDetailClientFragment extends Fragment {
    ViewPager viewPager ;
    View view;
    TextView tvNumberOfTrucks;
    FragmentTabHost tabhost;

    String dsr_id, dsr_ref_num; // Request Parameters

    String section;
    int numberOfTrucks = 0;
    TabWidget widget;
    //HorizontalScrollView hs;
    HashMap<String, TruckFinancialDetailModel> trucksFinancialDetails;
    DSRDetails dsrDetailsObj;

    public DSRCargoDetailClientFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dsr_cargo_detail_fragment, container, false);
        tvNumberOfTrucks = (TextView) view.findViewById(R.id.tvNumberOfTrucks);
        TextView btn_prev_submit_dsr = (TextView)view.findViewById(R.id.btn_prev_submit_dsr);
        btn_prev_submit_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsrDetailsObj.indexActivatedView = 0 ;
                dsrDetailsObj.doAction();
            }
        });
        // Set up Tab host
        setUpTabHost();

        dsr_id = getArguments().getString("dsr_id");
        dsr_ref_num = getArguments().getString("dsr_ref_no");

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dsrDetailsObj = (DSRDetails) getActivity();


        if (Utils.isNetworkConnected(getActivity(),false)) {
            // Load Cargo Details  using dsr_id
            loadDSRCargoDetails(dsr_id);
        }

    }

    /**
     * **************************
     * Methods  Functioning
     * ***************************
     */


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
                        DSRCargoDetailModel dsrCargoDetailModel = new DSRCargoDetailModel();
                        ArrayList<TrailerTypeModel> listTrailerType = dsrDetailsObj.listTrailerType;
                        ArrayList<TruckStatusModel> listTruckStatus = dsrDetailsObj.listTruckStatus;

                        JSONArray truckSDetailsArr = jobj.getJSONArray("truck_detail");
                        numberOfTrucks = truckSDetailsArr.length();
                        tvNumberOfTrucks.setText("Number Of Trucks   " + numberOfTrucks);
                        tabhost.clearAllTabs();
                        trucksFinancialDetails = new HashMap<>();

                        dsrCargoDetailModel.numberOfTrucks =  numberOfTrucks ;

                       ArrayList<DSRTruckDetailModel> listDSRTruckDetails =  new ArrayList<DSRTruckDetailModel>();
                        // Adding Tabs At runtime in Tab Host
                        String[] tabs = new String[10];
                        for (int indexTruckDetailObj = 0; indexTruckDetailObj < numberOfTrucks; indexTruckDetailObj++) {

                            JSONObject truckDetailObj = truckSDetailsArr.getJSONObject(indexTruckDetailObj);

                            DSRTruckDetailModel dsrTruckDetailModel = new DSRTruckDetailModel();

                            String dsr_truck_id     = truckDetailObj.getString("dsr_truck_id") ;
                            String dsr_id           = truckDetailObj.getString("dsr_id") ;
                            String driver_name      = truckDetailObj.getString("driver_name") ;
                            String driver_phone     = truckDetailObj.getString("driver_phone");
                            String driver_phone1     = truckDetailObj.getString("driver_phone1");
                            String driver_phone2     = truckDetailObj.getString("driver_phone2");
                            String trailor_type     = truckDetailObj.getString("trailor_type");
                            String vehicle_number   = truckDetailObj.getString("vehicle_number") ;
                            String off_loading_date = truckDetailObj.getString("off_loading_date");
                            String detention        = truckDetailObj.getString("detention");
                            String detention_client        = truckDetailObj.getString("detention_client");
                            detention_client = !detention_client.isEmpty()?detention_client :"0";
                            String del_receive_date = truckDetailObj.getString("del_recive_date");
                            String truck_status     = truckDetailObj.getString("truck_status") ;
                            String remark           = truckDetailObj.getString("remark") ;
                            String delete_status    = truckDetailObj.getString("delete_status") ;
                            String create_date      = truckDetailObj.getString("create_date") ;
                            String code_phone_no    = truckDetailObj.getString("code_phone_no");
                            String code_phone_no1    = truckDetailObj.getString("code_phone_no1");
                            String code_phone_no2    = truckDetailObj.getString("code_phone_no2");
                            String currency_code    = truckDetailObj.getString("currency_code");
                            String dest_currency_code    = "";
                            String detention_location=truckDetailObj.getString("detention_location");
                            String current_location = truckDetailObj.getString("current_location");
                            String detention_rate   = truckDetailObj.getString("detention_rate");
                            String detention_rate_client   = truckDetailObj.getString("detention_rate_client");
                            detention_rate_client = !detention_rate_client.isEmpty()?detention_rate_client :"0";
                            dsrTruckDetailModel.dsr_truck_id = dsr_truck_id ;
                            dsrTruckDetailModel.dsr_id = dsr_id ;
                            dsrTruckDetailModel.driver_name = driver_name ;
                            dsrTruckDetailModel.driver_phone = driver_phone ;
                            dsrTruckDetailModel.driver_phone1 = driver_phone1 ;
                            dsrTruckDetailModel.driver_phone2 = driver_phone2 ;
                            dsrTruckDetailModel.trailor_type = trailor_type ;
                            dsrTruckDetailModel.vehicle_number = vehicle_number ;
                            dsrTruckDetailModel.off_loading_date = off_loading_date ;
                            dsrTruckDetailModel.detention = detention ;
                            dsrTruckDetailModel.detention_rate = detention_rate;
                            dsrTruckDetailModel.detention_client = detention_client ; // [20-02-2017]
                            dsrTruckDetailModel.detention_rate_client = detention_rate_client; // [20-02-2017]
                            dsrTruckDetailModel.detention_location = detention_location ;
                            dsrTruckDetailModel.del_receive_date = del_receive_date ;
                            dsrTruckDetailModel.truck_status = truck_status ;
                            dsrTruckDetailModel.remark = remark ;
                            dsrTruckDetailModel.delete_status = delete_status ;
                            dsrTruckDetailModel.create_date = create_date ;
                            dsrTruckDetailModel.code_phone_no = code_phone_no;
                            dsrTruckDetailModel.code_phone_no1 = code_phone_no1;
                            dsrTruckDetailModel.code_phone_no2 = code_phone_no2;
                            dsrTruckDetailModel.currency_code = currency_code;
                            dsrTruckDetailModel.dest_currency_code = dest_currency_code; // [28-02-2017]
                            dsrTruckDetailModel.truck_current_location = current_location;


                            TruckFinancialDetailModel modelTruckFinancialDetail = new TruckFinancialDetailModel();
                            Bundle bundle = new Bundle();

                            bundle.putCharSequence("code_phone_no",code_phone_no );
                            bundle.putCharSequence("code_phone_no1",code_phone_no1 );
                            bundle.putCharSequence("code_phone_no2",code_phone_no2 );
                            bundle.putCharSequence("currency_code", currency_code);
                            bundle.putCharSequence("dest_currency_code", dest_currency_code);
                            bundle.putCharSequence("truck_current_location",current_location);
                            bundle.putCharSequence("detention_rate",detention_rate );
                            bundle.putCharSequence("detention_rate_client",detention_rate_client );
                            bundle.putCharSequence("detention_location",detention_location );
                            bundle.putCharSequence("dsr_truck_id",dsr_truck_id );
                            bundle.putCharSequence("dsr_id", dsr_id);
                            bundle.putCharSequence("driver_name",driver_name);
                            bundle.putCharSequence("driver_phone",driver_phone );
                            bundle.putCharSequence("driver_phone1",driver_phone1 );
                            bundle.putCharSequence("driver_phone2",driver_phone2 );

                            /****  Mapping Trailer Types using Trailer type Id Status Id  ***********/

                            int trailerTypeID = Integer.parseInt(trailor_type);


                            String trailerType = "";
                            int sizeListTrailerType = listTrailerType.size();
                            for (int indexTrailor = 0; indexTrailor < sizeListTrailerType; indexTrailor++) {
                                TrailerTypeModel modelTrailerType = listTrailerType.get(indexTrailor);
                                int trailerId = Integer.parseInt(modelTrailerType.trailerId);
                                if (trailerId == trailerTypeID) {
                                    trailerType = modelTrailerType.trailorType;
                                    break;
                                }
                            }

                            bundle.putCharSequence("trailor_type", trailerType);
                            bundle.putCharSequence("vehicle_number",vehicle_number );
                            bundle.putCharSequence("off_loading_date",off_loading_date );
                            bundle.putCharSequence("detention", detention);
                            bundle.putCharSequence("detention_client", detention_client);
                            bundle.putCharSequence("del_recive_date",del_receive_date);


                            /*****cost*****/
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
                            bundle.putSerializable("other_costs", (Serializable) costModelList);

                            /****  Mapping Truck Statuse using Truck Status Id  ***********/

                            int truckStatusId = Integer.parseInt(truck_status);
                            String truckStatusName = "";
                            int sizeListTruckStatus = listTruckStatus.size();
                            for (int indexStatus = 0; indexStatus < sizeListTruckStatus; indexStatus++) {
                                TruckStatusModel modelTruckStatus = listTruckStatus.get(indexStatus);
                                int statusId = Integer.parseInt(modelTruckStatus.statusId);

                                if (statusId == truckStatusId) {
                                    truckStatusName = modelTruckStatus.statusName;
                                    break;
                                }
                            }

                            bundle.putCharSequence("truck_status", truckStatusName);

                            bundle.putCharSequence("remark", remark);
                            bundle.putCharSequence("delete_status",delete_status );
                            bundle.putCharSequence("status",  truckDetailObj.getString("status") );
                            bundle.putCharSequence("create_date",create_date );

                            // Get Document Details
                            ArrayList<TruckDocDetailModel> docs = new ArrayList<>();
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
                                    docs.add(truckDocDetail);
                                }
                            }
                            modelTruckFinancialDetail.documents =  docs ;
                            // Financial Details
                            modelTruckFinancialDetail.our_price_customer = truckDetailObj.getString("our_price_customer");
                            modelTruckFinancialDetail.our_cost = truckDetailObj.getString("our_cost");
                            modelTruckFinancialDetail.advance = truckDetailObj.getString("advance");
                            modelTruckFinancialDetail.balance = truckDetailObj.getString("balance");
                            modelTruckFinancialDetail.border_charges = truckDetailObj.getString("border_charges");
                            modelTruckFinancialDetail.balance_paid = truckDetailObj.getString("balance_paid");
                            modelTruckFinancialDetail.border_charge_include=truckDetailObj.getString("border_charge_include");

                            /************ Changes *************/

                            bundle.putCharSequence("our_price_customer",truckDetailObj.getString("our_price_customer") );
                            bundle.putCharSequence("our_cost",truckDetailObj.getString("our_cost"));
                            bundle.putCharSequence("advance",truckDetailObj.getString("advance"));
                            bundle.putCharSequence("balance",truckDetailObj.getString("balance") );
                            bundle.putCharSequence("border_charges",truckDetailObj.getString("border_charges"));
                            bundle.putCharSequence("balance_paid",truckDetailObj.getString("balance_paid"));
                            bundle.putCharSequence("border_charge_include",truckDetailObj.getString("border_charge_include"));
                            bundle.putSerializable("docs",docs);
                            //bundle.put("docs",docs);

                            /************ Changes *************/
                            String tag = "truck" + (indexTruckDetailObj + 1);
                            tabs[indexTruckDetailObj] = tag ;
                            trucksFinancialDetails.put(tag, modelTruckFinancialDetail);
                            //Create Tab
                            tabhost.addTab(tabhost.newTabSpec(tag).setIndicator("Truck " + (indexTruckDetailObj + 1)),
                                    TruckDetailClientFragment.class, bundle);

                            // Styling Tab
                            View viewTab  = tabhost.getTabWidget().getChildAt(indexTruckDetailObj);
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
                            dsrTruckDetailModel.truckFinancialDetailModel  = modelTruckFinancialDetail ;
                            listDSRTruckDetails.add(dsrTruckDetailModel);
                        }

                       /* ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), tabs);
                        viewPager.setAdapter(pagerAdapter);*/

                       /* viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                tabhost.setCurrentTab(position);
                            }

                            @Override
                            public void onPageSelected(int position) {

                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {
                                viewPager.setCurrentItem(tabhost.getCurrentTab());
                            }
                        });*/
                        dsrCargoDetailModel.listDSRTruckDetails = listDSRTruckDetails;
                        dsrDetailsObj.dsrCargoDetailModel = dsrCargoDetailModel  ;
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


    // Set Up Tab Host into Fragment Using Fragment tab Host
    private void setUpTabHost() {

       // viewPager = (ViewPager) view.findViewById(R.id.pager);
        tabhost = (FragmentTabHost) view.findViewById(R.id.tabhost);
        widget = (TabWidget) view.findViewById(android.R.id.tabs);
        //hs = (HorizontalScrollView) view.findViewById(R.id.horizontalScrollView);

        try {
            tabhost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
            // Dummy Tabs
            tabhost.addTab(tabhost.newTabSpec("truck").setIndicator("Truck"),
                    TruckDetailClientFragment.class, null);
            tabhost.getTabWidget().getChildAt(0).setBackgroundResource(R.color.white);
            tabhost.getTabWidget().getChildAt(0).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));


        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }



}
