package com.client.itrack.fragments.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
 * Created by sony on 28-07-2016.
 */
public class ContactCallBackFragment extends Fragment  {
    View view  ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.contact_callback_fragment,container,false);
        final EditText etName =  (EditText)view.findViewById(R.id.etName) ;
        final EditText  etPhone = (EditText)  view.findViewById(R.id.etPhone);

        TextView  tvSendCallBack = (TextView)  view.findViewById(R.id.tvSendCallBack);
        tvSendCallBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name  =  etName.getText().toString().trim() ;
                String phone =   etPhone.getText().toString().trim() ;
                if(!name.isEmpty() && !phone.isEmpty())
                {
                    sendCallBack(name,phone) ;
                }
                else
                {
                    if(name.isEmpty())
                        Toast.makeText(getActivity(), "Please Enter name !", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "Please Enter Phone !", Toast.LENGTH_SHORT).show();
                }
            }

            private void sendCallBack(String name, String phone) {
                Utils.showLoadingPopup(getActivity());
                String url = Constants.BASE_URL + "call_request";
                HashMap<String, String> data = new HashMap<>();
                data.put("name", name);
                data.put("phone", phone);
                HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
                    @Override
                    public void response(String errorMessage, String responseData) {
                        Utils.hideLoadingPopup();
                        try {
                            JSONObject jobj = new JSONObject(responseData);
                            Boolean status = jobj.getBoolean("status");
                            if (status) Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                            else Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
