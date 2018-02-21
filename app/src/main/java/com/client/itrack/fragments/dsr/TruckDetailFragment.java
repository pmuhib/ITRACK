package com.client.itrack.fragments.dsr;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.AdditionCostModel;
import com.client.itrack.model.TruckDocDetailModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DocumentHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sony on 12-05-2016.
 */


public class TruckDetailFragment extends Fragment{

    AppGlobal appGlobal = AppGlobal.getInstance();

    TextView tvTruckDriverName ,tvTruckDriverPhone ,tvTypeOfTrailer,tvVehicalNumber,tvCurrencyCode,tvTruckCurrentLocation,
            tvOffLoadingDate,tvDetention,tvDetentionForClient,tvDetentionRate,tvDetentionRateForClient,tvRemark ,tvStatus,tvDeliveryNoteReceivedDate,
            tvOurPriceToCustomer,tvOurCost,tvAdvance,tvBalance,tvBorderCharge,tvBalancePaid,dest_curr_code,tvDetentionChargesForClient,tvDetentionCharges
            ,tvforcost,tvforcostclient,tvforcosttype,tvforaddcosttype,tvdetentionloc;

    RelativeLayout  dnrDTCont,detentionDaysCont,offLoadingDTCont,detentionChargesCont,detentionRateCont ;
    LinearLayout documentContainer,truckViewContainer ;

    View view  ;
    String truckStatus = "" ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.truck_detail_fragment ,container,false) ;
        setupGUI();
        bindDataToGUI();
        setupGUISettings();
        return view;
    }

    private void setupGUISettings() {
        RelativeLayout.LayoutParams truck_lp = (RelativeLayout.LayoutParams) tvBorderCharge.getLayoutParams() ;
        TextView lblOurCost = (TextView)view.findViewById(R.id.lblOurCost) ;
        TextView lblAdvance = (TextView)view.findViewById(R.id.lblAdvance) ;
        TextView lblBalance = (TextView)view.findViewById(R.id.lblBalance) ;
        if(appGlobal.userType.equals(Constants.CLIENT_EMP_TYPE))
        {
            tvOurCost.setVisibility(View.GONE);
            tvAdvance.setVisibility(View.GONE);
            tvBalance.setVisibility(View.GONE);
            lblOurCost.setVisibility(View.GONE);
            lblAdvance.setVisibility(View.GONE);
            lblBalance.setVisibility(View.GONE);
            truck_lp.addRule(RelativeLayout.BELOW,tvOurPriceToCustomer.getId());
            tvBorderCharge.setLayoutParams(truck_lp);
        }
        else
        {
            tvOurCost.setVisibility(View.VISIBLE);
            tvAdvance.setVisibility(View.VISIBLE);
            tvBalance.setVisibility(View.VISIBLE);
            lblOurCost.setVisibility(View.VISIBLE);
            lblAdvance.setVisibility(View.VISIBLE);
            lblBalance.setVisibility(View.VISIBLE);
            truck_lp.addRule(RelativeLayout.BELOW,tvBalance.getId());
            tvBorderCharge.setLayoutParams(truck_lp);
        }

        if(truckStatus.toLowerCase().equals("delivered") || truckStatus.toLowerCase().equals("offloaded"))
        {
            dnrDTCont.setVisibility(View.VISIBLE);
            offLoadingDTCont.setVisibility(View.VISIBLE);
            detentionDaysCont.setVisibility(View.VISIBLE);
            detentionRateCont.setVisibility(View.GONE);
            detentionChargesCont.setVisibility(View.VISIBLE);

        }
        else
        {
            dnrDTCont.setVisibility(View.GONE);
            offLoadingDTCont.setVisibility(View.GONE);
            detentionDaysCont.setVisibility(View.GONE);
            detentionRateCont.setVisibility(View.VISIBLE);
            detentionChargesCont.setVisibility(View.GONE);
        }



    }

    private void bindDataToGUI() {

        // Get All Argument Values
        Bundle bundle = getArguments() ;
        if(bundle!=null)
        {
            tvTruckDriverName.setText(bundle.getString("driver_name"));
            String phone_str = "";
            String driver_phone = bundle.getString("driver_phone");
            String driver_phone1 = bundle.getString("driver_phone1");
            String driver_phone2 = bundle.getString("driver_phone2");
            if(driver_phone!=null && !driver_phone.trim().isEmpty())
            {
                phone_str += "+"+bundle.getString("code_phone_no")+" "+driver_phone ;
            }
            if(driver_phone1!=null && !driver_phone1.trim().isEmpty())
            {
                phone_str += "\n+"+bundle.getString("code_phone_no1")+" "+driver_phone1 ;
            }
            if(driver_phone2!=null && !driver_phone2.trim().isEmpty())
            {
                phone_str += "\n+"+bundle.getString("code_phone_no2")+" "+driver_phone2 ;
            }





            tvTruckDriverPhone.setText(phone_str.trim());
            tvTypeOfTrailer.setText(bundle.getString("trailor_type"));
            tvVehicalNumber.setText(bundle.getString("vehicle_number"));
            tvOffLoadingDate.setText(bundle.getString("off_loading_date"));
            String detention  = bundle.getString("detention") ;
            String detention_rate  =  bundle.getString("detention_rate");
            String detention_client  = bundle.getString("detention_client") ; // [20-02-2017]
            String detention_rate_client  =  bundle.getString("detention_rate_client"); // [20-02-2017]
            String detention_location  = bundle.getString("detention_location") ;
            String truck_current_location  = bundle.getString("truck_current_location") ;
            String currency_code  =  bundle.getString("currency_code");
            String dest_currency_code  =  bundle.getString("dest_currency_code");
            tvdetentionloc.setText(detention_location);
            tvCurrencyCode.setText(currency_code);
            dest_curr_code.setText(dest_currency_code);
            tvTruckCurrentLocation.setText(truck_current_location);
            tvDetention.setText(detention+"  Days");
            tvDetentionForClient.setText(detention_client+"  Days");// [20-02-2017]
            tvDetentionRate.setText(currency_code+" "+detention_rate+" Per Day");
            tvDetentionRateForClient.setText(currency_code+" "+detention_rate_client+" Per Day");// [20-02-2017]
            long detentionCharges = 0;
            long detentionChargesForClient =0 ;
            try {
                detentionCharges = (Long.parseLong(detention) * Long.parseLong(detention_rate));
                detentionChargesForClient = (Long.parseLong(detention_client) * Long.parseLong(detention_rate_client));// [20-02-2017]
            }catch(NumberFormatException ex)
            {
                ex.printStackTrace();
            }
            tvDetentionCharges.setText(currency_code+" "+String.valueOf(detentionCharges));
            tvDetentionChargesForClient.setText(currency_code+" "+String.valueOf(detentionChargesForClient)); // [20-02-2017]
            tvDeliveryNoteReceivedDate.setText(bundle.getString("del_recive_date"));
            truckStatus = bundle.getString("truck_status") ;
            tvStatus.setText(truckStatus);
            tvRemark.setText(bundle.getString("remark"));
            tvOurPriceToCustomer.setText(currency_code+" "+bundle.getString("our_price_customer"));
            tvOurCost.setText(currency_code+" "+bundle.getString("our_cost"));
            tvAdvance.setText(currency_code+" "+bundle.getString("advance"));
            tvBalance.setText(currency_code+" "+bundle.getString("balance"));
            String border_charge_include = bundle.getString("border_charge_include");
            border_charge_include = border_charge_include!=null? border_charge_include.trim():"";
            if(border_charge_include.toLowerCase().equals("no")){
                border_charge_include = "Included" ;
            }else {
                border_charge_include = "Not included" ;
            }

            tvBorderCharge.setText(currency_code+" "+(bundle.getString("border_charges").isEmpty()?"0":bundle.getString("border_charges"))+" "+border_charge_include);
            tvBalancePaid.setText(bundle.getString("balance_paid"));
            StringBuilder builderAserviceCost = new StringBuilder();
            StringBuilder builderAserviceCost_type = new StringBuilder();
            StringBuilder builderAserviceCharge = new StringBuilder();
            List<AdditionCostModel> costModelList = (List<AdditionCostModel>) bundle.getSerializable("other_costs") ;
            for (int indexACost = 0; indexACost < costModelList.size(); indexACost++) {
                AdditionCostModel additionCostModel =  costModelList.get(indexACost);
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

            ArrayList<TruckDocDetailModel> documents = (ArrayList<TruckDocDetailModel>)bundle.getSerializable("docs");

            for (TruckDocDetailModel truckDocDetailModel:documents) {
                addDocumentToGUI(truckDocDetailModel,truckDocDetailModel.data,truckDocDetailModel.document_name);
            }
        }

    }

    private void setupGUI() {
        // All views
        tvTruckDriverName = (TextView)view.findViewById(R.id.tvTruckDriverName);
        tvTruckDriverPhone = (TextView)view.findViewById(R.id.tvTruckDriverPhone);
        // tvTruckDriverPhone1 = (TextView)view.findViewById(R.id.tvTruckDriverPhone1);
        // tvTruckDriverPhone2 = (TextView)view.findViewById(R.id.tvTruckDriverPhone2);
        tvTypeOfTrailer = (TextView)view.findViewById(R.id.tvTypeOfTrailer);
        tvVehicalNumber = (TextView)view.findViewById(R.id.tvVehicalNumber);
        tvOffLoadingDate = (TextView)view.findViewById(R.id.tvOffLoadingDate);
        tvDetention = (TextView) view.findViewById(R.id.tvDetention);
        tvDetentionForClient = (TextView) view.findViewById(R.id.tvDetentionForClient);
        tvDetentionCharges = (TextView) view.findViewById(R.id.tvDetentionCharges);
        tvDetentionChargesForClient = (TextView) view.findViewById(R.id.tvDetentionChargesForClient);
        tvDetentionRate = (TextView) view.findViewById(R.id.tvDetentionRate);
        tvDetentionRateForClient = (TextView) view.findViewById(R.id.tvDetentionRateForClient);
        tvRemark = (TextView)view.findViewById(R.id.tvRemark);
        tvStatus = (TextView)view.findViewById(R.id.tvStatus);
        tvDeliveryNoteReceivedDate = (TextView)view.findViewById(R.id.tvDeliveryNoteReceivedDate);
        tvAdvance = (TextView) view.findViewById(R.id.tvAdvance);
        tvOurPriceToCustomer = (TextView) view.findViewById(R.id.tvOurPriceToCustomer);
        tvOurCost = (TextView) view.findViewById(R.id.tvOurCost);
        tvCurrencyCode = (TextView) view.findViewById(R.id.tvCurrencyCode);
        tvTruckCurrentLocation = (TextView) view.findViewById(R.id.tvTruckCurrentLocation);
        tvBalance = (TextView) view.findViewById(R.id.tvBalance);
        tvBorderCharge = (TextView) view.findViewById(R.id.tvBorderCharge);
        tvBalancePaid = (TextView) view.findViewById(R.id.tvBalancePaid);
        dest_curr_code = (TextView) view.findViewById(R.id.dest_curr_code);
        truckViewContainer =(LinearLayout) view.findViewById(R.id.truckViewContainer) ;
        documentContainer =(LinearLayout) view.findViewById(R.id.documentContainer) ;

        dnrDTCont = (RelativeLayout) view.findViewById(R.id.dnrDTCont);
        offLoadingDTCont = (RelativeLayout) view.findViewById(R.id.offLoadingDTCont);
        detentionDaysCont = (RelativeLayout) view.findViewById(R.id.detentionDaysCont);
        detentionChargesCont = (RelativeLayout) view.findViewById(R.id.detentionChargesCont);
        detentionRateCont = (RelativeLayout) view.findViewById(R.id.detentionRateCont);
        tvforcost= (TextView) view.findViewById(R.id.tvForCost);
        tvforcostclient=(TextView) view.findViewById(R.id.tvForCostClient);
        tvforcosttype=(TextView) view.findViewById(R.id.tvForCostType);
        tvforaddcosttype= (TextView) view.findViewById(R.id.tvForaddCostType);
        tvdetentionloc=(TextView) view.findViewById(R.id.tvForDetentionLocation);
    }


    private void addDocumentToGUI(TruckDocDetailModel docModel, final String data, final String document_name) {

        docModel.document_name = document_name;
        docModel.data = data;
        TextView tvDoc = new TextView(getActivity());
        tvDoc.setText(document_name);
        tvDoc.setHint(document_name);
        tvDoc.setMaxLines(1);
        tvDoc.setEllipsize(TextUtils.TruncateAt.END);
        if (Build.VERSION.SDK_INT < 23) {
            tvDoc.setTextAppearance(getActivity(), R.style.GLEditTextTheme);
        } else{
            tvDoc.setTextAppearance(R.style.GLEditTextTheme);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        layoutParams.gravity = Gravity.CENTER_VERTICAL ;
        tvDoc.setLayoutParams(layoutParams);
        tvDoc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.doc_file_icn_small, null),null, null, null);

        // Document Click Event
        tvDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentHandler documentHandler = new DocumentHandler(getActivity(),TruckDetailFragment.this);
                documentHandler.showImage(data,document_name);
            }
        });

//        tvDoc.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                //showDocActionOptions();
//                return false;
//            }
//        });

        documentContainer.addView(tvDoc);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
