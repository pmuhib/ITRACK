package com.client.itrack.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.ClientModel;
import com.client.itrack.model.CompanyModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sony on 04-03-2017.
 */

public class CompanySearchAdapter extends RecyclerView.Adapter<CompanySearchAdapter.MyViewHolde> {
    private List<ClientModel> comp;
    Context ctx;
    AppGlobal appGlobal = AppGlobal.getInstance();
    public class MyViewHolde extends RecyclerView.ViewHolder {
        public TextView companyname,companyaddress,companypone,clientcode;
        public ImageView circleicon;
        public MyViewHolde(View itemView) {
            super(itemView);
            companyname= (TextView) itemView.findViewById(R.id.txt_CompanyName);
            companyaddress= (TextView) itemView.findViewById(R.id.txt_compaddress);
            companypone= (TextView) itemView.findViewById(R.id.txt_comPhone);
            clientcode= (TextView) itemView.findViewById(R.id.txt_clientcode);
            circleicon = (ImageView) itemView.findViewById(R.id.Im_imageicon);
        }
    }
    public  CompanySearchAdapter(List<ClientModel> compy, Activity ctx)
    {
        this.comp=compy;
        this.ctx=ctx;
    }
    @Override
    public MyViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.companyitemlayout,parent,false);
        return new MyViewHolde(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolde holder, int position) {
        ClientModel cmp=comp.get(position);
      holder.companyname.setText(cmp.companyName.trim());
        holder.companyaddress.setText("Address : "+cmp.address.trim());
        holder.companypone.setText("Tel : "+cmp.phoneNumber);
        holder.clientcode.setText("Client Code : "+cmp.companyCode);
        Picasso.with(ctx).load(Constants.IMAGE_BASE_URL+cmp.companyLogoName).placeholder(R.drawable.circledefault).into(holder.circleicon);
    }

    @Override
    public int getItemCount() {
        return comp.size();
    }


}

