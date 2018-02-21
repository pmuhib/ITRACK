package com.client.itrack.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.client.itrack.R;

/**
 * Created by sony on 15-09-2016.
 */
public class DFTDetailsItem extends RelativeLayout {

    private TextView tvDFTModeOfPayment,tvDFTModeOfPaymentAmount,tvDFTModeOfPaymentComments;
    private GridLayout  dftMOPDocumentsContainer ;

    public DFTDetailsItem(Context context) {
        super(context);
        init();
    }

    public DFTDetailsItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DFTDetailsItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.dft_details_item,this) ;

        tvDFTModeOfPayment = (TextView) this.findViewById(R.id.tvDFTModeOfPayment);
        tvDFTModeOfPaymentAmount = (TextView)this.findViewById(R.id.tvDFTModeOfPaymentAmount);
        tvDFTModeOfPaymentComments = (TextView)this.findViewById(R.id.tvDFTModeOfPaymentComments);
        dftMOPDocumentsContainer = (GridLayout) this.findViewById(R.id.dftMOPDocumentsContainer);

    }


}
