package com.client.itrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.model.DsrProfitDetail;
import com.client.itrack.model.DsrProfitHead;

import java.util.ArrayList;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class ExListAdapter extends BaseExpandableListAdapter{
    Context context;
   ArrayList<DsrProfitHead> listparent;
    public ExListAdapter(Context context, ArrayList<DsrProfitHead> listparent) {
        this.context=context;
        this.listparent=listparent;

    }

    @Override
    public int getGroupCount() {
        return listparent.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<DsrProfitDetail> dsrProfitDetails=listparent.get(groupPosition).getDsrprofitdetail();
        return dsrProfitDetails.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listparent.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<DsrProfitDetail> dsrProfitDetails=listparent.get(groupPosition).getDsrprofitdetail();
        return dsrProfitDetails.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        DsrProfitHead dsrProfitHead= (DsrProfitHead) getGroup(groupPosition);
        if(convertView==null)
        {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.ytdprofit_title,null);
        }
        TextView refno= (TextView) convertView.findViewById(R.id.txt_refno);
        TextView netprofit=(TextView) convertView.findViewById(R.id.txt_netprofit);
        refno.setText(dsrProfitHead.getRefno().trim());
        netprofit.setText(dsrProfitHead.getNetProfit().trim());
        return convertView;
      /*
        String head= (String) getGroup(groupPosition);
        if(convertView==null)
        {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.ytdprofit_title,null);
        }
        TextView refno= (TextView) convertView.findViewById(R.id.txt_refno);
        TextView netprofit=(TextView) convertView.findViewById(R.id.txt_netprofit);
        refno.setText(head);
        return convertView;*/
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
       DsrProfitDetail dsrProfitDetail = (DsrProfitDetail) getChild(groupPosition,childPosition);
        if(convertView==null)
        {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.ytdprofit_titledetail,null);
        }
        TextView Transporationincome= (TextView) convertView.findViewById(R.id.txt_transincome);
        TextView TransporationCost= (TextView) convertView.findViewById(R.id.txt_transcost);
        TextView TransporationNet= (TextView) convertView.findViewById(R.id.txt_transnet);
        TextView Border= (TextView) convertView.findViewById(R.id.txt_Border);
        TextView DetentionCost= (TextView) convertView.findViewById(R.id.txt_deteCost);
        TextView DetentionCharge= (TextView) convertView.findViewById(R.id.txt_deteCharge);
        TextView DetentionNet= (TextView) convertView.findViewById(R.id.txt_deteNet);
        TextView AdditionalCost= (TextView) convertView.findViewById(R.id.txt_additiCost);
        TextView AdditionalCharge= (TextView) convertView.findViewById(R.id.txt_additiCharge);
        TextView AdditionalNet= (TextView) convertView.findViewById(R.id.txt_additiNet);

        Transporationincome.setText(dsrProfitDetail.getTransporationincome().trim());
        TransporationCost.setText(dsrProfitDetail.getTransporationCost().trim());
        TransporationNet.setText(dsrProfitDetail.getTransporationNet().trim());
        Border.setText(dsrProfitDetail.getBorder().trim());
        DetentionCost.setText(dsrProfitDetail.getDetentionCost().trim());
        DetentionCharge.setText(dsrProfitDetail.getDetentionCharge().trim());
        DetentionNet.setText(dsrProfitDetail.getDetentionNet().trim());
        AdditionalCost.setText(dsrProfitDetail.getAdditionalCost().trim());
        AdditionalCharge.setText(dsrProfitDetail.getAdditionalCharge().trim());
        AdditionalNet.setText(dsrProfitDetail.getAdditionalNet().trim());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
