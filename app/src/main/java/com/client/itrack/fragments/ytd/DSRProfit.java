package com.client.itrack.fragments.ytd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.client.itrack.R;

/**
 * Created by root on 24/7/17.
 */

public class DSRProfit extends Fragment implements View.OnClickListener {
    LinearLayout layout1,layout2;
   LinearLayout inclay,inclay1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dsr_profit,container,false);
        layout1= (LinearLayout) view.findViewById(R.id.linear_1);
        layout2= (LinearLayout) view.findViewById(R.id.linear_2);
        inclay= (LinearLayout) view.findViewById(R.id.detail);
        inclay1= (LinearLayout) view.findViewById(R.id.detail1);
        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    LinearLayout  layout ;
    @Override
    public void onClick(View v) {
        if(layout!=null)
        {
            layout.setVisibility(View.GONE);
        }
        switch (v.getId())
        {
            case R.id.linear_1:
                inclay.setVisibility(View.VISIBLE);
                layout=inclay;
                break;
            case R.id.linear_2:
                inclay1.setVisibility(View.VISIBLE);
                layout=inclay1;
                break;

        }
    }
}
