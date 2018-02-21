package com.client.itrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.TrailerTypeModel;
import com.client.itrack.model.TruckStatusModel;

import java.util.ArrayList;

/**
 * Created by sony on 14-05-2016.
 */
public class TrailerTypesAdapter extends BaseAdapter {

    private Context  context ;
    private LayoutInflater inflater  ;
    private ArrayList<TrailerTypeModel> listTrailerType ;
    @Override
    public int getCount() {
        return listTrailerType.size();
    }

    @Override
    public Object getItem(int position) {
        return listTrailerType.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.customlayout,null);

        TrailerTypeModel trailerTypeModel =  listTrailerType.get(position);
        TextView tvTrailerType   = (TextView)view.findViewById(R.id.customtext) ;
      //  tvStatusName.setLayoutParams(new LinearLayout.LayoutParams(200,LinearLayout.LayoutParams.WRAP_CONTENT));
        tvTrailerType.setText(trailerTypeModel.trailorType);
        if(!parent.isEnabled())
        {
            tvTrailerType.setEnabled(false);
            tvTrailerType.setFocusable(false);
        }
        return view ;
    }

    public TrailerTypesAdapter(Context  context, ArrayList<TrailerTypeModel> listTrailerType) {
        this.context  = context ;
        this.listTrailerType = listTrailerType  ;

        inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
