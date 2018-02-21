package com.client.itrack.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utils;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by NITISH on 3/20/2016.
 */
public class ForgotFragments extends Fragment{

    RelativeLayout btnSend;
    EditText email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       /* return super.onCreateView(inflater, container, savedInstanceState);*/
        View view=inflater.inflate(R.layout.forgotpwdfragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TextView)getActivity().findViewById(R.id.txt_heading)).setText("Forgot Password");
        ((ImageView)getActivity().findViewById(R.id.btn_back)).setVisibility(View.GONE);
        btnSend=(RelativeLayout)getView().findViewById(R.id.btn_sendMyDeatil);
        email=(EditText)getView().findViewById(R.id.etxt_emailAddress);



        // Button For Sending Request Forgot password

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isNetworkConnected(getActivity(),false)) {

                    if (email.getText().toString().length() == 0) {
                        Toast.makeText(getActivity(), "enter email address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    forgotAPI(email.getText().toString());
                }
                else{
                    Toast.makeText(getActivity(), "Internet not connected", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Implementation Forgot Password API

    private void forgotAPI(String email) {
        String url = Constants.BASE_URL+"forgetpassword";
        Utils.showLoadingPopup(getActivity());
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);
        //data.put("type",type) ;
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status=jobj.getBoolean("status");
                    if(status) {
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                        getFragmentManager().popBackStack();
                    }
                    else{
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                  //  Log.e(Constants.LOG_TAG, responseData);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
               Toast.makeText(getActivity(), errorMessage.toString(), Toast.LENGTH_SHORT).show();
                Utils.hideLoadingPopup();

            }
        });
    }
}
