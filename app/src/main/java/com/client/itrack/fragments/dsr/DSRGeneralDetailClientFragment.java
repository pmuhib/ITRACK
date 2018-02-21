package com.client.itrack.fragments.dsr;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.activities.DSRDetails;
import com.client.itrack.R;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.DSRGeneralDetailModel;
import com.client.itrack.model.TrailerTypeModel;
import com.client.itrack.model.TruckStatusModel;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sony on 11-05-2016.
 */
public class DSRGeneralDetailClientFragment extends Fragment {
    View view ;
    TextView tvClientCompName, tvClientCompRefNum, tvComapnyDSRNum, tvCustoms, tvDateReachedCustom, tvDestinationPoint, tvLoadingDate, tvLoadingPoint;
    TextView tvDSRDateTime, tvTT, tvStatusDSR,tvNoOfTrucks, tvETA, tvRemark;
    String dsr_ref_num;
    String dsr_id;
    DSRDetails dsrDetailsObj ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dsr_general_detail_client_fragment, container, false);
        setupGUI();

        try {
            dsr_id = getArguments().getString("dsr_id");
            dsr_ref_num = getArguments().getString("dsr_ref_no");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return view;
    }


    private void setupGUI() {

        tvClientCompName = (TextView) view.findViewById(R.id.tvClientCompName);
        tvClientCompRefNum = (TextView) view.findViewById(R.id.tvClientCompRefNum);
        tvComapnyDSRNum = (TextView) view.findViewById(R.id.tvDsrRefNum);
        tvCustoms = (TextView) view.findViewById(R.id.tvCustoms);
        tvDateReachedCustom = (TextView) view.findViewById(R.id.tvDateReachedCustom);
        tvDestinationPoint = (TextView) view.findViewById(R.id.tvDestinationPoint);
        tvLoadingDate = (TextView) view.findViewById(R.id.tvLoadingDate);
        tvLoadingPoint = (TextView) view.findViewById(R.id.tvLoadingPoint);
        tvDSRDateTime = (TextView) view.findViewById(R.id.tvDSRDateTime);
        tvTT = (TextView) view.findViewById(R.id.tvTT);
        tvStatusDSR = (TextView) view.findViewById(R.id.tvStatusDSR);
        tvNoOfTrucks = (TextView) view.findViewById(R.id.tvNoOfTrucks);
        tvETA = (TextView) view.findViewById(R.id.tvETA);
        tvRemark = (TextView) view.findViewById(R.id.tvRemark);
        TextView btn_next_submit_dsr = (TextView)view.findViewById(R.id.btn_next_submit_dsr);
        btn_next_submit_dsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsrDetailsObj.indexActivatedView = 1 ;
                dsrDetailsObj.doAction();
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dsrDetailsObj =  (DSRDetails) getActivity() ;
        if (Utils.isNetworkConnected(getActivity(),false)) {
            loadDSRDetails(dsr_id);
        }


    }

    private void loadDSRDetails(String dsr_id) {

        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL + "dsrlistbyid";

        HashMap<String, String> hm = new HashMap<>();
        hm.put("dsr_id", dsr_id);

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(hm), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {
                    ArrayList<TruckStatusModel> listTruckStatus = new ArrayList<TruckStatusModel>();
                    ArrayList<TrailerTypeModel> listTrailerType = new ArrayList<TrailerTypeModel>();

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray truckStatusArr = jobj.getJSONArray("truck_status");
                        JSONArray trailorTypeArr = jobj.getJSONArray("trailor_type");

                        /****** Fill Trucks Statuses into Truck Status Lists  ****/

                        for (int indexTruckStatus = 0; indexTruckStatus < truckStatusArr.length(); indexTruckStatus++) {
                            TruckStatusModel modelTruckStatus = new TruckStatusModel();
                            JSONObject truckStatusObj = truckStatusArr.getJSONObject(indexTruckStatus);
                            modelTruckStatus.statusId = truckStatusObj.getString("truck_status_id");
                            modelTruckStatus.statusName = truckStatusObj.getString("status_name");
                            listTruckStatus.add(modelTruckStatus);
                        }

                        dsrDetailsObj.listTruckStatus =  new ArrayList<TruckStatusModel>();
                        dsrDetailsObj.listTruckStatus.addAll(listTruckStatus);



                        /****** Fill Trailer Types into Trailer Type Lists  ****/

                        for (int indexTrailerType = 0; indexTrailerType < trailorTypeArr.length(); indexTrailerType++) {
                            TrailerTypeModel modelTrailerType = new TrailerTypeModel();
                            JSONObject trailerTypeObj = trailorTypeArr.getJSONObject(indexTrailerType);
                            modelTrailerType.trailerId = trailerTypeObj.getString("trailor_id");
                            modelTrailerType.trailorType = trailerTypeObj.getString("trailor_type");
                            modelTrailerType.createDate = trailerTypeObj.getString("create_date");
                            listTrailerType.add(modelTrailerType);
                        }

                        dsrDetailsObj.listTrailerType= new ArrayList<TrailerTypeModel>();
                        dsrDetailsObj.listTrailerType.addAll(listTrailerType);

                        // DSR General Detail model and  View to SCREEN
                        DSRGeneralDetailModel modelDSRGenDetail = new DSRGeneralDetailModel();
                        JSONObject dsrDetailObj = jobj.getJSONObject("general_dsr_info");
                        String dsr_id = dsrDetailObj.getString("dsr_id");
                        String client_id = dsrDetailObj.getString("client_id");
                        String dsr_date_time = dsrDetailObj.getString("dsr_date_time");
                        String client_Ref_no = dsrDetailObj.getString("client_Ref_no");
                        String loading_date = dsrDetailObj.getString("loading_date");
                        String eta = dsrDetailObj.getString("eta");
                        String tt = dsrDetailObj.getString("tt");
                        String loading_point = dsrDetailObj.getString("loading_point");
                        String loading_city = dsrDetailObj.getString("loading_city");
                        String loading_country = dsrDetailObj.getString("loading_country");
                        String destination_point = dsrDetailObj.getString("destination_point");
                        String destination_city = dsrDetailObj.getString("destination_city");
                        String destination_country = dsrDetailObj.getString("destination_country");
                        String num_truck = dsrDetailObj.getString("num_truck");
                        String customs = dsrDetailObj.getString("customs");
                        String date_reached_custom = dsrDetailObj.getString("date_reached_custom");
                        String delete_status = dsrDetailObj.getString("delete_status");
                        String read_status = dsrDetailObj.getString("read_status");
                        String dsr_status = dsrDetailObj.getString("dsr_status");
                        String status_str = dsrDetailObj.getString("status");
                        String remark = dsrDetailObj.getString("remark");
                        String create_date = dsrDetailObj.getString("create_date");
                        String month = dsrDetailObj.getString("month");


                        // Fill Into DSR general Detail Model
                        modelDSRGenDetail.dsrId = dsr_id;
                        modelDSRGenDetail.clientCompName = client_id;
                        modelDSRGenDetail.dsrDateTime = dsr_date_time;
                        modelDSRGenDetail.clientRefNo = client_Ref_no;
                        modelDSRGenDetail.loadingDate = loading_date;
                        modelDSRGenDetail.eta = eta;
                        modelDSRGenDetail.tt = tt;
                        modelDSRGenDetail.loading_point = loading_point;
                        modelDSRGenDetail.loading_city = loading_city;
                        modelDSRGenDetail.loading_country = loading_country;
                        modelDSRGenDetail.destination_point = destination_point;
                        modelDSRGenDetail.destination_city = destination_city;
                        modelDSRGenDetail.destination_country = destination_country;
                        modelDSRGenDetail.num_truck = num_truck;
                        modelDSRGenDetail.date_reached_custom = date_reached_custom;
                        modelDSRGenDetail.delete_status = delete_status;
                        modelDSRGenDetail.read_status = read_status;
                        modelDSRGenDetail.dsr_status = dsr_status;
                        modelDSRGenDetail.status_str = status_str;
                        modelDSRGenDetail.remark = remark;
                        modelDSRGenDetail.create_date = create_date;
                        modelDSRGenDetail.month = month;
                        modelDSRGenDetail.dsrRefNumber = dsr_ref_num ;
                        modelDSRGenDetail.customs = customs ;

                        // Fill into Globally into Activity
                        dsrDetailsObj.dsrGeneralDetailModel = modelDSRGenDetail ;


                        // View On Activity
                        tvClientCompName.setText(client_id);
                        tvClientCompRefNum.setText(client_Ref_no);
                        tvComapnyDSRNum.setText(dsr_ref_num);
                        tvCustoms.setText(customs);
                        tvDateReachedCustom.setText(date_reached_custom);
                        tvDestinationPoint.setText(destination_city+", "+destination_country);
                        tvLoadingDate.setText(loading_date);
                        tvLoadingPoint.setText(loading_city+", "+loading_country);
                        tvDSRDateTime.setText(dsr_date_time);
                        tvTT.setText(tt+" Days");
                        tvStatusDSR.setText(dsr_status);
                        tvETA.setText(eta);
                        tvRemark.setText(remark);
                        tvNoOfTrucks.setText(num_truck);

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


}
