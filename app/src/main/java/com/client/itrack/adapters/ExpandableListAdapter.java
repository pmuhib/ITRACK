package com.client.itrack.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.model.DrawerModel;
import com.client.itrack.utility.AppGlobal;

import java.util.List;
import java.util.Map;

/**
 * Created by NITISH on 4/16/2016.
 */
/*
public class ExpandableListAdapter {
}
*/
public class ExpandableListAdapter extends BaseExpandableListAdapter {


    private Activity context;
    private Map<String, List<String>> laptopCollections;
    private List<DrawerModel> laptops;
    AppGlobal appGlobal =  AppGlobal.getInstance() ;

    public ExpandableListAdapter(Activity context, List<DrawerModel> laptops,
                                 Map<String, List<String>> laptopCollections) {
        this.context = context;
        this.laptopCollections = laptopCollections;
        this.laptops = laptops;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return this.laptopCollections.get((getGroup(groupPosition)).getItemTitle()).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String laptop = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.rawarchilditem, parent,false);
        }

        TextView item = (TextView) convertView.findViewById(R.id.laptop);


        item.setText(laptop);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
       /* return laptopCollections.get(laptops.get(groupPosition).getItemTitle()).size();*/
            return this.laptopCollections.get((getGroup(groupPosition)).getItemTitle()).size();
    }

    public DrawerModel getGroup(int groupPosition) {
        return laptops.get(groupPosition);
    }

    public int getGroupCount() {
        return laptops.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.menu_item_layout,
                    parent,false);
        }
        TextView item = (TextView) convertView.findViewById(R.id.item_title);
        ImageView itemimage = (ImageView) convertView.findViewById(R.id.item_icon);
        ImageView rightitemimage = (ImageView) convertView.findViewById(R.id.drawer_righticon);

        itemimage.setImageResource(laptops.get(groupPosition).getDrawerCardIcon());
        String userType = appGlobal.userType ;

        switch(userType)
        {
            case "employee":
                if(groupPosition == 1 || (groupPosition == 2)||(groupPosition==3)||(groupPosition==4)) {
                    if(isExpanded){
                        rightitemimage.setImageResource(R.drawable.minus_icon);
                    }
                    else {
                        rightitemimage.setImageResource(R.drawable.plus_icon);
                    }
                }
                else {
                    rightitemimage.setImageResource(0);
                }
                break ;
            default:
                if(groupPosition == 2) {
                    if(isExpanded){
                        rightitemimage.setImageResource(R.drawable.minus_icon);
                    }
                    else {
                        rightitemimage.setImageResource(R.drawable.plus_icon);
                    }
                }
                else {
                    rightitemimage.setImageResource(0);
                }
                break ;
        }


//        if((groupPosition == 2)||(groupPosition==3)||(groupPosition==4)){
//            rightitemimage.setImageResource(R.drawable.plus_icon);
//        }
       // item.setTypeface(null, Typeface.BOLD);
        item.setText(laptops.get(groupPosition).itemTitle);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
