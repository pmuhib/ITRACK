package com.client.itrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.LocationPointModel;

import java.util.ArrayList;


/**
 * Created by admin on 2/29/2016.
 */
public class LocationPointAdapter extends BaseAdapter implements Filterable
{
    Context mctx;
    ArrayList<LocationPointModel> mlist;
    private ArrayList<LocationPointModel> suggestions = new ArrayList<>();
    private CustomFilter filter = new CustomFilter();
    private static LayoutInflater inflater=null;

    public LocationPointAdapter(Context ctx, ArrayList<LocationPointModel> list){
        this.mctx=ctx;
        this.mlist=list;
        inflater = ( LayoutInflater )mctx.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class Holder
    {
        TextView tv;

    }

    @Override
    public Object getItem(int position) {
        return suggestions.get(position);
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

        holder.tv.setText(suggestions.get(position).locPoint);
        if(!parent.isEnabled())
        {
            holder.tv.setEnabled(false);
            holder.tv.setFocusable(false);
        }

        return rowView;
    }

    private class CustomFilter extends  Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            suggestions.clear();

            if(mlist!=null && charSequence!=null){
                for (int i = 0; i < mlist.size(); i++) {
                    if (mlist.get(i).locPoint.toLowerCase().contains(charSequence.toString().toLowerCase())) { // Compare item in original list if it contains constraints.
                        suggestions.add(mlist.get(i)); // If TRUE add item in Suggestions.
                    }
                }
            }

            FilterResults results = new FilterResults(); // Create new Filter Results and return this to publishResults;
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String locPoint =  ((LocationPointModel)resultValue).locPoint ;
            return locPoint;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (filterResults.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }



}
