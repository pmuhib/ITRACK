package com.client.itrack.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.client.itrack.R;

import com.client.itrack.listener.DateChangeCallback;
import com.client.itrack.model.DFTIPaymentIDetailsModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DocumentHandler;

import java.util.List;


public class DFTPaymentDetailsAdapter extends RecyclerView.Adapter<DFTPaymentDetailsAdapter.MyViewHolder> {

    private final DateChangeCallback callback;
    private List<DFTIPaymentIDetailsModel> listPayments ;
    Context ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDFTModeOfPayment, tvDFTModeOfPaymentAmount, tvDFTModeOfPaymentComments ,tvDFTModeOfPaymentDate;
        public ImageView ivDFTDocument;

        public MyViewHolder(View view) {
            super(view);
            tvDFTModeOfPayment = (TextView) view.findViewById(R.id.tvDFTModeOfPayment);
            tvDFTModeOfPaymentDate = (TextView) view.findViewById(R.id.tvDFTModeOfPaymentDate);
            tvDFTModeOfPaymentAmount = (TextView) view.findViewById(R.id.tvDFTModeOfPaymentAmount);
            tvDFTModeOfPaymentComments = (TextView) view.findViewById(R.id.tvDFTModeOfPaymentComments);
            ivDFTDocument = (ImageView) view.findViewById(R.id.ivDFTDocument);
        }
    }


    public DFTPaymentDetailsAdapter(List<DFTIPaymentIDetailsModel> cList, Activity ctx, DateChangeCallback callback) {
        this.listPayments = cList;
        this.ctx=ctx;
        this.callback = callback ;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dft_details_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DFTIPaymentIDetailsModel paymentObj = listPayments.get(position);
        holder.tvDFTModeOfPayment.setText(AppGlobal.getInstance().convertToTitleCase(paymentObj.payName));
        holder.tvDFTModeOfPaymentAmount.setText(paymentObj.currencyCode+" "+paymentObj.payAmount);
        holder.tvDFTModeOfPaymentComments.setText(paymentObj.comment);
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
                    documentHandler.    showImageByURL(url);
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
