package com.client.itrack.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.DSRModel;
import com.client.itrack.utility.AppGlobal;


import java.util.List;

/**
 * Created by Sony on 5/8/2016.
 */
public class DSRAdapter extends RecyclerView.Adapter<DSRAdapter.DSRViewHolder> {
    private  Context context;
    private List<DSRModel> dsrList ;
    AppGlobal appGlobal = AppGlobal.getInstance();
    public DSRAdapter(List<DSRModel> dsrList, Context context) {
        this.dsrList = dsrList ;
        this.context = context ;
    }

    @Override
    public DSRViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dsr_list_item, parent, false);
        return new DSRViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DSRViewHolder holder, int position) {
        DSRModel dsrModel =  dsrList.get(position);
        holder.tvDsrCreatedDate.setText(dsrModel.dsrCreatedDate);
        holder.tvComapnyDSRName.setText(dsrModel.clientCompName+"-"+dsrModel.dsrRefNumber);
        holder.tvCreatedBy.setText(dsrModel.createEmpName.trim());

        switch (dsrModel.dsrStatus)
        {
            case "39" : // Full Paid
                holder.ivDsr.setImageResource(R.drawable.dsr_list_icn_lime_green);
                break;

            case "41" : // Not Paid With 10 Days
                holder.ivDsr.setImageResource(R.drawable.dsr_list_icn_red);
                break;

            case "40" : // partial Paid
                holder.ivDsr.setImageResource(R.drawable.dsr_list_icn_yellow);
                break;

           default:
                holder.ivDsr.setImageResource(R.drawable.dsr_list_icn);
        }

    }

    @Override
    public int getItemCount() {
        return dsrList.size();
    }

    public class DSRViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivDsr ;
        public TextView tvComapnyDSRName ,tvDsrCreatedDate,tvCreatedBy;


        public DSRViewHolder(View itemView) {
            super(itemView);
            tvComapnyDSRName = (TextView) itemView.findViewById(R.id.tvComapnyDSRName);
            tvDsrCreatedDate = (TextView) itemView.findViewById(R.id.tvDsrCreatedDate);
            tvCreatedBy = (TextView) itemView.findViewById(R.id.tvCreatedBy);
            ivDsr = (ImageView) itemView.findViewById(R.id.ivDsr);
        }
    }
}
