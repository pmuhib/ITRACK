package com.client.itrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.DSRStatusModel;

import java.util.ArrayList;

/**
 * Created by sony on 14-05-2016.
 */
public class DSRStatusSpinnerAdapter extends BaseAdapter {

    private Context  context ;
    private LayoutInflater inflater  ;
    private ArrayList<DSRStatusModel> listDSRStatus ;
    @Override
    public int getCount() {
        return listDSRStatus.size();
    }

    @Override
    public Object getItem(int position) {
        return listDSRStatus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.customlayout,null);

        DSRStatusModel modelDSRStatus =  listDSRStatus.get(position);
        TextView tvStatusName   = (TextView)view.findViewById(R.id.customtext) ;
      //  tvStatusName.setLayoutParams(new LinearLayout.LayoutParams(200,LinearLayout.LayoutParams.WRAP_CONTENT));
        tvStatusName.setText(modelDSRStatus.dsrStatusName);
        if(!parent.isEnabled())
        {
            tvStatusName.setEnabled(false);
            tvStatusName.setFocusable(false);
        }
        return view ;
    }

    public DSRStatusSpinnerAdapter(Context  context, ArrayList<DSRStatusModel> listDSRStatus) {
        this.context  = context ;
        this.listDSRStatus = listDSRStatus  ;
        inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
