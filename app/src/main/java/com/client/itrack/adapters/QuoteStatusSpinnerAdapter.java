package com.client.itrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.DSRStatusModel;

import java.util.ArrayList;

/**
 * Created by sony on 14-05-2016.
 */
public class QuoteStatusSpinnerAdapter extends BaseAdapter {

    private Context  context ;
    private LayoutInflater inflater  ;
    private ArrayList<String> listQuoteStatus ;
    @Override
    public int getCount() {
        return listQuoteStatus.size();
    }

    @Override
    public Object getItem(int position) {
        return listQuoteStatus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.customlayout,null);

        String quoteStatus =  listQuoteStatus.get(position);
        TextView tvStatusName   = (TextView)view.findViewById(R.id.customtext) ;
        tvStatusName.setText(quoteStatus);
        return view ;
    }

    public QuoteStatusSpinnerAdapter(Context  context, ArrayList<String> listQuoteStatus) {
        this.context  = context ;
        this.listQuoteStatus = listQuoteStatus  ;

        inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
