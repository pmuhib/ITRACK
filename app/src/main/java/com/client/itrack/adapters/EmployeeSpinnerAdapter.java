package com.client.itrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.ClientModel;
import com.client.itrack.model.EmployeeModel;

import java.util.ArrayList;

/**
 * Created by jayant on 24-04-2016.
 */
public class EmployeeSpinnerAdapter extends BaseAdapter
{
    Context mctx;
    ArrayList<EmployeeModel> mlist;
    private static LayoutInflater inflater=null;

    public EmployeeSpinnerAdapter(Context ctx, ArrayList<EmployeeModel> list){
        this.mctx=ctx;
        this.mlist=list;
        inflater = ( LayoutInflater )mctx.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return mlist.size();
    }

    public class Holder
    {
        TextView tv;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.customlayout, null);
        holder.tv=(TextView) rowView.findViewById(R.id.customtext);
        holder.tv.setText(mlist.get(position).companyName);

        return rowView;
    }
}
