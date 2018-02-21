package com.client.itrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.DFTPaymentTypeModel;
import com.client.itrack.model.TrailerTypeModel;

import java.util.ArrayList;

/**
 * Created by sony on 14-05-2016.
 */
public class DFTPaymentTypesAdapter extends BaseAdapter {

    private Context  context ;
    private LayoutInflater inflater  ;
    private ArrayList<DFTPaymentTypeModel> listDftPaymentType ;
    @Override
    public int getCount() {
        return listDftPaymentType.size();
    }

    @Override
    public Object getItem(int position) {
        return listDftPaymentType.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.customlayout,null);

        DFTPaymentTypeModel paymentTypeModel =  listDftPaymentType.get(position);
        TextView tvPaymentType   = (TextView)view.findViewById(R.id.customtext) ;
        tvPaymentType.setText(paymentTypeModel.getPayType());
        if(!parent.isEnabled())
        {
            tvPaymentType.setEnabled(false);
            tvPaymentType.setFocusable(false);
        }
        return view ;
    }

    public DFTPaymentTypesAdapter(Context  context, ArrayList<DFTPaymentTypeModel> listDftPaymentType) {
        this.context  = context ;
        this.listDftPaymentType = listDftPaymentType  ;
        inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
