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
import com.client.itrack.model.EmployeeModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by NITISH on 4/15/2016.
 */
/*
public class EventAdapter  extends RecyclerView.A{
}
*/


public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.MyViewHolder> {

    private List<EmployeeModel> clientList;
    Context ctx;
    AppGlobal appGlobal = AppGlobal.getInstance();
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView newsNumber, mdate, desc;
        public ImageView circleicon;

        public MyViewHolder(View view) {
            super(view);
            newsNumber = (TextView) view.findViewById(R.id.newsNumber);
            mdate = (TextView) view.findViewById(R.id.mdate);
            desc = (TextView) view.findViewById(R.id.description);
            circleicon = (ImageView) view.findViewById(R.id.imageicon);
        }
    }


    public EmployeeAdapter(List<EmployeeModel> cList, Activity ctx) {
        this.clientList = cList;
        this.ctx=ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employeeitemlayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EmployeeModel client = clientList.get(position);
        holder.newsNumber.setText(client.companyName.trim());
        holder.mdate.setText("Email : "+client.email);
        holder.desc.setText("Tel : "+client.phoneNumber);

        Picasso.with(ctx).load(Constants.IMAGE_BASE_URL+client.companyLogoName).placeholder(R.drawable.circledefault).error(R.drawable.circledefault).into(holder.circleicon);
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }
}
