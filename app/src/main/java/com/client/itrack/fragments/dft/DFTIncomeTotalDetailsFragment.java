package com.client.itrack.fragments.dft;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.adapters.DFTPaymentDetailsAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.DateChangeCallback;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.DFTIPaymentIDetailsModel;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utility;
import com.client.itrack.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sony on 15-09-2016.
 */
public class DFTIncomeTotalDetailsFragment extends BaseDFTFragment {


    private String category ;


    ArrayList<DFTIPaymentIDetailsModel> listPayments ;

    RecyclerView recyclerPaymentList ;
    TextView tvDFTCategoryTitle ,tvDFTDate ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.dft_details_fragment,container,false) ;
        Bundle bundle = getArguments() ;
        if(bundle!=null){
            from_date = bundle.getString("from_date") ;
            to_date  = bundle.getString("to_date") ;
            category = bundle.getString("category") ;
            currency_code = bundle.getString("currency_code") ;
        }
        configureGUI(view);
        setupToolBar();
        return view;
    }

    private void configureGUI(View view) {
        tvDFTDate = (TextView)view.findViewById(R.id.tvDFTDate) ;
        tvDFTCategoryTitle = (TextView)view.findViewById(R.id.tvDFTCategoryTitle) ;
        tvDFTCategoryTitle.setText(category);
        recyclerPaymentList = (RecyclerView) view.findViewById(R.id.recyclerPaymentList) ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void setDateToView() {
        super.setDateToView();
        requestDFTIncomeTotalDetails(from_date,to_date,currency_code) ;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestDFTIncomeTotalDetails(from_date,to_date,currency_code) ;
    }


    private void requestDFTIncomeTotalDetails(String from_date, String to_date, final String currency_code) {

        listPayments =  new ArrayList<>() ;
        String url = Constants.BASE_URL+"income_view_details";
        Utils.showLoadingPopup(getActivity());
        final HashMap<String, String> data = new HashMap<>();
        data.put("from_date", from_date);
        data.put("to_date", to_date);
        data.put("currency_code", currency_code);
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();

                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status=jObj.getBoolean("status");
                    if(status) {
                        JSONArray paymentsJArr  =  jObj.getJSONArray("msg");
                        for (int indexPaymentObj = 0; indexPaymentObj < paymentsJArr.length(); indexPaymentObj++) {
                            DFTIPaymentIDetailsModel paymentIDetailsModel = new DFTIPaymentIDetailsModel() ;
                            JSONObject jObjPayment = paymentsJArr.getJSONObject(indexPaymentObj) ;
                            paymentIDetailsModel._id =  jObjPayment.getString("id");
                            paymentIDetailsModel._date =  jObjPayment.getString("date");
                            paymentIDetailsModel.payName =  jObjPayment.getString("name");
                            paymentIDetailsModel.payAmount =  jObjPayment.getString("amount");
                            paymentIDetailsModel.currencyCode =  jObjPayment.getString("currency_code");
                            paymentIDetailsModel.payTypeId =  jObjPayment.getString("type");
                            paymentIDetailsModel.attachmentSrc =  jObjPayment.getString("attachment");
                            paymentIDetailsModel.comment =  jObjPayment.getString("comment");

                            listPayments.add(paymentIDetailsModel);
                        }

                        DFTPaymentDetailsAdapter paymentDetailsAdapter = new DFTPaymentDetailsAdapter(listPayments,getActivity(), new DateChangeCallback() {
                            @Override
                            public void changedDate(String date) {
                                tvDFTDate.setText(date);
                                tvDFTDate.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvDFTDate.setVisibility(View.GONE);
                                    }
                                }, 2000);
                            }
                        });
                        recyclerPaymentList.setAdapter(paymentDetailsAdapter);
                        recyclerPaymentList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerPaymentList.setItemAnimator(new DefaultItemAnimator());
                        JSONObject totalJObj = jObj.getJSONObject("totalincome");
                        tvDFTCategoryTitle.setText("Total Amount : "+currency_code+" "+totalJObj.getString("incometotal"));

                    }
                    else{
                        Toast.makeText(getActivity(), jObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                    Utility.sendReport(getActivity(),"income_view_details",status.toString(),Utils.newGson().toJson(data),responseData);

                }
                catch (Exception e){
                    e.printStackTrace();
                    Utility.sendReport(getActivity(),"income_view_details",e.getMessage(),Utils.newGson().toJson(data),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Utils.hideLoadingPopup();
                Utility.sendReport(getActivity(),"income_view_details","Error",Utils.newGson().toJson(data),errorMessage);
            }
        });
    }


}
