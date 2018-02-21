package com.client.itrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.CustomsPointModel;
import com.client.itrack.model.DSRStatusModel;

import java.util.ArrayList;

/**
 * Created by sony on 14-05-2016.
 */
public class DSRCustomsPointAdapter extends BaseAdapter {

    private Context  context ;
    private LayoutInflater inflater  ;
    private ArrayList<CustomsPointModel> listCustomsPoint ;
    @Override
    public int getCount() {
        return listCustomsPoint.size();
    }

    @Override
    public Object getItem(int position) {
        return listCustomsPoint.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position , View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.customlayout,null);

        CustomsPointModel customsPointModel =  listCustomsPoint.get(position);
        TextView tvCustomsPointName   = (TextView)view.findViewById(R.id.customtext) ;
      //  tvStatusName.setLayoutParams(new LinearLayout.LayoutParams(200,LinearLayout.LayoutParams.WRAP_CONTENT));
        tvCustomsPointName.setText(customsPointModel.pointName);
        if(!parent.isEnabled())
        {
            tvCustomsPointName.setEnabled(false);
            tvCustomsPointName.setFocusable(false);
        }
        return view ;
    }

    public DSRCustomsPointAdapter(Context  context, ArrayList<CustomsPointModel> listCustomsPoint) {
        this.context  = context ;
        this.listCustomsPoint = listCustomsPoint  ;
        inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
