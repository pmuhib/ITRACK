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
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.MyViewHolder> {

    private List<ClientModel> clientList;
    Context ctx;
    AppGlobal appGlobal = AppGlobal.getInstance();
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvClientName, tvClientEmail, tvClientPhone,tvClientCode;
        public ImageView circleicon;

        public MyViewHolder(View view) {
            super(view);
            tvClientName = (TextView) view.findViewById(R.id.tvClientName);
            tvClientEmail = (TextView) view.findViewById(R.id.tvClientEmail);
            tvClientPhone = (TextView) view.findViewById(R.id.tvClientPhone);
            tvClientCode = (TextView) view.findViewById(R.id.tvClientCode);
            circleicon = (ImageView) view.findViewById(R.id.imageicon);
        }
    }

    public ClientAdapter(List<ClientModel> cList, Activity ctx) {
        this.clientList = cList;
        this.ctx=ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.clientitemlayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ClientModel client = clientList.get(position);
        holder.tvClientName.setText(client.companyName.trim());
        holder.tvClientEmail.setText("Email : "+client.email);
        holder.tvClientPhone.setText("Tel : "+client.phoneNumber);
        holder.tvClientCode.setText("Client Code : "+client.companyCode);
        Picasso.with(ctx).load(Constants.IMAGE_BASE_URL+client.companyLogoName).placeholder(R.drawable.circledefault).error(R.drawable.circledefault).into(holder.circleicon);
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }
}
