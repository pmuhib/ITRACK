package com.client.itrack.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.client.itrack.activities.DSRDetails;
import com.client.itrack.R;
import com.client.itrack.listener.DateChangeCallback;
import com.client.itrack.model.DFTIPaymentIDetailsModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DocumentHandler;

import java.util.List;


public class DFTPaymentOperationalDetailsAdapter extends RecyclerView.Adapter<DFTPaymentOperationalDetailsAdapter.MyViewHolder> {

    private final DateChangeCallback callback;
    private List<DFTIPaymentIDetailsModel> listPayments ;
    Context ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDFTModeOfPayment, tvDFTModeOfPaymentAmount, tvDFTDSRRefNumber ,tvDFTModeOfPaymentDate;
        public ImageView ivDFTDocument;

        public MyViewHolder(View view) {
            super(view);
            tvDFTModeOfPayment = (TextView) view.findViewById(R.id.tvDFTModeOfPayment);
            tvDFTModeOfPaymentDate = (TextView) view.findViewById(R.id.tvDFTModeOfPaymentDate);
            tvDFTModeOfPaymentAmount = (TextView) view.findViewById(R.id.tvDFTModeOfPaymentAmount);
            tvDFTDSRRefNumber = (TextView) view.findViewById(R.id.tvDFTDSRRefNumber);
            ivDFTDocument = (ImageView) view.findViewById(R.id.ivDFTDocument);
        }
    }


    public DFTPaymentOperationalDetailsAdapter(List<DFTIPaymentIDetailsModel> cList, Activity ctx, DateChangeCallback callback) {
        this.listPayments = cList;
        this.ctx=ctx;
        this.callback = callback ;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dft_operational_details_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DFTIPaymentIDetailsModel paymentObj = listPayments.get(position);
        holder.tvDFTModeOfPayment.setText(AppGlobal.getInstance().convertToTitleCase(paymentObj.payName));
        holder.tvDFTModeOfPaymentAmount.setText(paymentObj.currencyCode+" "+paymentObj.payAmount);
        holder.tvDFTDSRRefNumber.setText(paymentObj.dsr_ref_no);
        holder.tvDFTDSRRefNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String dsr_id = paymentObj.dsr_id ;
                final String dsr_ref_nu = paymentObj.dsr_ref_no ;

                final String createEmpId = paymentObj.createEmpId ;
                final String createEmpType = paymentObj.createEmpType ;
                Intent intent = new Intent(ctx, DSRDetails.class);
                intent.putExtra("dsr_id", dsr_id);
                intent.putExtra("dsr_ref_no", dsr_ref_nu);
                intent.putExtra("action", "view");
                intent.putExtra("client_comp_name","");
                intent.putExtra("createEmpId",createEmpId);
                intent.putExtra("createEmpType", createEmpType);
                ctx.startActivity(intent);
            }
        });
        holder.tvDFTModeOfPaymentDate.setText(paymentObj._date);
        final String attachmentSrc = paymentObj.attachmentSrc;
        if(attachmentSrc!=null && !attachmentSrc.isEmpty() && attachmentSrc.contains(".png"))
        {
            holder.ivDFTDocument.setVisibility(View.VISIBLE);
            holder.ivDFTDocument.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = Constants.DFT_DOC_BASE_URL + attachmentSrc ;
                    DocumentHandler documentHandler = new DocumentHandler((Activity) ctx,new Fragment());
                    documentHandler.showImageByURL(url);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return listPayments.size();
    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.d("refresh Date",holder.tvDFTModeOfPaymentDate.getText().toString());
       // callback.changedDate(holder.tvDFTModeOfPaymentDate.getText().toString());
    }

}
