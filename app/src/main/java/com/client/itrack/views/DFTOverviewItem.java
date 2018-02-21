package com.client.itrack.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.client.itrack.R;
import com.client.itrack.utility.AppGlobal;

/**
 * Created by sony on 15-09-2016.
 */
public class DFTOverviewItem extends LinearLayout {

    private TextView tvDFTCategory,tvDFTCategoryTotalAmount,tvAddDetailToDFTCategory,tvViewDFTCategoryDetails;


    public DFTOverviewItem(Context context) {
        super(context);
        init();
    }

    public DFTOverviewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DFTOverviewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.dft_overview_item,this) ;

        tvDFTCategory = (TextView) this.findViewById(R.id.tvDFTCategory);
        tvDFTCategoryTotalAmount = (TextView)this.findViewById(R.id.tvDFTCategoryTotalAmount);
        tvAddDetailToDFTCategory = (TextView)this.findViewById(R.id.tvAddDetailToDFTCategory);
        tvViewDFTCategoryDetails = (TextView) this.findViewById(R.id.tvViewDFTCategoryDetails);

    }

    public void setDFTCategory(String name)
    {
        tvDFTCategory.setText(name);
    }

    public void setDFTCategoryTotalAmount(String amount)
    {
        tvDFTCategoryTotalAmount.setText(amount);
    }
    public TextView getAddDetailsButton()
    {
        return tvAddDetailToDFTCategory ;
    }

    public TextView getViewDetailsButton()
    {
        return tvViewDFTCategoryDetails ;
    }



}
