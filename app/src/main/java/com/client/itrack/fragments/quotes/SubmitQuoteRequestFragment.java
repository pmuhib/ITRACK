package com.client.itrack.fragments.quotes;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.itrack.activities.CategoryDetailContainer;
import com.client.itrack.R;
import com.client.itrack.utility.AppGlobal;

public class SubmitQuoteRequestFragment extends Fragment {

    View view ;
    AppGlobal appGlobal = AppGlobal.getInstance() ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.submit_quote_request_frag,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView tvQuoteSubmitRequest = (TextView) view.findViewById(R.id.tvQuoteSubmitRequest);
        tvQuoteSubmitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryDetailContainer.class) ;
                intent.putExtra("catpos",31);
                startActivity(intent);
            }
        });
        TextView tvSubmitQuoteAfterSignIn = (TextView) view.findViewById(R.id.tvSubmitQuoteAfterSignIn);
        tvSubmitQuoteAfterSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               appGlobal.checkIsUserAuthenticated(getActivity());
            }
        });
        setupTopBar();
    }

    private void setupTopBar() {

        Toolbar toolbarSubmitQuote  =(Toolbar) getActivity().findViewById(R.id.toolbar);
        ImageView  btn_navigation = (ImageView) toolbarSubmitQuote.findViewById(R.id.btn_navigation);
        TextView  language = (TextView) toolbarSubmitQuote.findViewById(R.id.navigationdot);
        btn_navigation.setVisibility(View.GONE);
        language.setVisibility(View.GONE);

    }
}
