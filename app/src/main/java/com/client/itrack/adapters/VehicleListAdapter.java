package com.client.itrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.TruckStatusModel;
import com.client.itrack.model.VehicleModel;

import java.util.ArrayList;

/**
 * Created by sony on 14-05-2016.
 */
public class VehicleListAdapter extends BaseAdapter {

    private Context  context ;
    private LayoutInflater inflater  ;
    private ArrayList<VehicleModel> listVehicle ;
    @Override
    public int getCount() {
        return listVehicle.size();
    }

    @Override
    public Object getItem(int position) {
        return listVehicle.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.customlayout,null);

        VehicleModel vehicleModel =  listVehicle.get(position);
        TextView tvVehicle   = (TextView)view.findViewById(R.id.customtext) ;
      //  tvStatusName.setLayoutParams(new LinearLayout.LayoutParams(200,LinearLayout.LayoutParams.WRAP_CONTENT));
        tvVehicle.setText(vehicleModel.vehicle_number);
        if(!parent.isEnabled())
        {
            tvVehicle.setEnabled(false);
            tvVehicle.setFocusable(false);
        }
        return view ;
    }

    public VehicleListAdapter(Context  context, ArrayList<VehicleModel> listVehicle) {
        this.context  = context ;
        this.listVehicle = listVehicle  ;

        inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
