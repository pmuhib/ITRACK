package com.client.itrack.fragments.dft;

import android.os.Bundle;
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
import com.client.itrack.adapters.DFTPaymentOperationalDetailsAdapter;
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
public class DFTOperationExpensesDetailsFragment extends BaseDFTFragment {

    private String category ;

    RecyclerView recyclerPaymentList ;
    TextView tvDFTCategoryTitle ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.dft_operation_expenses_details_fragment,container,false) ;
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
        requestDFTOperationExpensesDetails(from_date,to_date,currency_code);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestDFTOperationExpensesDetails(from_date,to_date,currency_code);
    }
    ArrayList<DFTIPaymentIDetailsModel> listPayments ;
    private void requestDFTOperationExpensesDetails(String from_date, String to_date, final String currency_code) {
        listPayments = new ArrayList<>();
        String url = Constants.BASE_URL+"operation_expenses_details";
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
                            String amount_type =  jObjPayment.getString("type");
                            switch(amount_type)
                            {
                                case "1" :
                                    paymentIDetailsModel.payName = "Advance" ;
                                    break ;

                                case "2" :
                                    paymentIDetailsModel.payName = "Border Charge";
                                    break ;

                                case "3" :
                                    paymentIDetailsModel.payName = "Balance" ;
                                    break ;

                                case "4" :
                                    paymentIDetailsModel.payName = "Detention Charge" ;
                                    break ;

                                case "5" :
                                    paymentIDetailsModel.payName = "Cancel" ;
                                    break ;

                            }
                            paymentIDetailsModel.payAmount =  jObjPayment.getString("amount");
                            paymentIDetailsModel.currencyCode =  jObjPayment.getString("currency_code");
                            paymentIDetailsModel.attachmentSrc =  jObjPayment.getString("attachment");
                            paymentIDetailsModel.comment =  jObjPayment.getString("comment");
                            paymentIDetailsModel.dsr_ref_no =  jObjPayment.getString("dsr_ref_no");
                            paymentIDetailsModel.dsr_id =  jObjPayment.getString("dsr_id");

                            paymentIDetailsModel.createEmpType =  jObjPayment.getString("employee_type");
                            paymentIDetailsModel.createEmpId =  jObjPayment.getString("employee_id");
                            String payAmount  =  paymentIDetailsModel.payAmount.trim() ;
                            if(!payAmount.isEmpty() && Double.parseDouble(payAmount) != 0.0 )
                                listPayments.add(paymentIDetailsModel);
                        }

                        DFTPaymentOperationalDetailsAdapter paymentDetailsAdapter = new DFTPaymentOperationalDetailsAdapter(listPayments, getActivity(), new DateChangeCallback() {
                            @Override
                            public void changedDate(String date) {

                            }
                        });
                        recyclerPaymentList.setAdapter(paymentDetailsAdapter);
                        recyclerPaymentList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerPaymentList.setItemAnimator(new DefaultItemAnimator());

                        JSONObject totalJObj = jObj.getJSONObject("operationexpensestotal");
                        tvDFTCategoryTitle.setText("Total Amount : "+currency_code+" "+totalJObj.getString("operationtotal"));

                    }
                    else{
                        Toast.makeText(getActivity(), jObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                    Utility.sendReport(getActivity(),"operation_expenses_details",status.toString(),Utils.newGson().toJson(data),responseData);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Utility.sendReport(getActivity(),"operation_expenses_details",e.getMessage(),Utils.newGson().toJson(data),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Utils.hideLoadingPopup();
                Utility.sendReport(getActivity(),"operation_expenses_details","Error",Utils.newGson().toJson(data),errorMessage);
            }
        });
    }

}
