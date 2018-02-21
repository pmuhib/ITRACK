package com.client.itrack.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.activities.HomeContainer;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.R;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.SharedPreferenceStore;
import com.client.itrack.utility.Utils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginFragments extends Fragment {

    View view;
    EditText userName, password;
    TextView txtForgot, txtLogin, txtSignUP, txtSkip;
    AppGlobal appGlobal = AppGlobal.getInstance() ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       /* return super.onCreateView(inflater, container, savedInstanceState);*/
        view = inflater.inflate(R.layout.loginfragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((TextView) getActivity().findViewById(R.id.txt_heading)).setText("Login");

        userName = (EditText) view.findViewById(R.id.etxt_username);
        password = (EditText) view.findViewById(R.id.etxt_pwd);
        txtForgot = (TextView) view.findViewById(R.id.txt_forgotpwd);
        txtLogin = (TextView) view.findViewById(R.id.txt_login);
        txtSignUP = (TextView) view.findViewById(R.id.txt_signUp);
        txtSkip = (TextView) view.findViewById(R.id.txt_skip);

        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                ForgotFragments forgotFragments = new ForgotFragments();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.framecontainer, forgotFragments);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        txtSignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                RegisterFragments registerFragments = new RegisterFragments();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.framecontainer, registerFragments);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = userName.getText().toString().trim();
                String pswd = password.getText().toString().trim() ;
                if (Utils.isNetworkConnected(getActivity(),false)) {
                    if (uName.length() == 0 || pswd.length() == 0) {
                        Toast.makeText(getActivity(), "Please enter username or password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    loginAPI(uName, pswd);
                } else {
                    Toast.makeText(getActivity(), "Internet not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int skipCount = SharedPreferenceStore.getValue(getActivity(),"SkipCount",0);
                 if(skipCount < 3) {
                     appGlobal.userType = "guest" ;
                     appGlobal.userId = "";
                     SharedPreferenceStore.storeValue(getActivity(), "SkipCount", ++skipCount);
                     Intent intent = new Intent(getActivity(), HomeContainer.class);
                     startActivity(intent);
                 }
                else {
                     Toast.makeText(getActivity(), "You cannot skip more then three times", Toast.LENGTH_SHORT).show();
                 }
            }
        });
    }

    private void loginAPI(String username, String pwd) {
        String url = Constants.BASE_URL + "login";
        Utils.showLoadingPopup(getActivity());

        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", pwd);
       // data.put("device_id", device_id);
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    String msg  = jobj.getString("msg");
                    if (status) {

                        JSONObject jsonObject = jobj.optJSONObject("user_detail");
                        JSONObject jsonObjectLP = jobj.optJSONObject("loding_point_detail");
                        String loginType = jsonObject.getString("type");
                        String uid = jsonObject.getString("user_id");

                        SharedPreferenceStore.storeValue(getActivity(), "Userid", uid);
                        SharedPreferenceStore.storeValue(getActivity(), "UserName", jsonObject.getString("f_name")+" " + jsonObject.getString("l_name"));
                        SharedPreferenceStore.storeValue(getActivity(), "PhoneNo", jsonObject.getString("phone_no"));
                        SharedPreferenceStore.storeValue(getActivity(), "PhoneCode", jsonObject.getString("code_phone_no"));
                        SharedPreferenceStore.storeValue(getActivity(), "Designation", jsonObject.getString("designation"));
                        SharedPreferenceStore.storeValue(getActivity(), "Email", jsonObject.getString("email"));

                        SharedPreferenceStore.storeValue(getActivity(), "Type",loginType);
                        appGlobal.userType = loginType ;
                        appGlobal.userId =  uid ;

                        SharedPreferenceStore.storeValue(getActivity(), "usernameId", jsonObject.getString("user_name"));
                        if(loginType.toLowerCase().equals("employee"))
                        {
                            appGlobal.userCompId =  "1" ; // Admin ID
                            SharedPreferenceStore.storeValue(getActivity(), "client_comp_id","1");
                            SharedPreferenceStore.storeValue(getActivity(), "img", jsonObject.getString("image"));
                            SharedPreferenceStore.storeValue(getActivity(), "LoadingPoint", jsonObject.getString("loading_point"));
                        }
                        else
                        {
                            appGlobal.userCompId =  jsonObject.getString("company") ;
                            SharedPreferenceStore.storeValue(getActivity(), "client_comp_id",jsonObject.getString("company"));
                            SharedPreferenceStore.storeValue(getActivity(), "img", jsonObject.getString("emp_img"));
                            SharedPreferenceStore.storeValue(getActivity(), "LoadingPoint", "");
                        }

                        if (jsonObjectLP!=null)
                        {
                            SharedPreferenceStore.storeValue(getActivity(), "LoadingPointName", jsonObjectLP.getString("loading_point"));
                            SharedPreferenceStore.storeValue(getActivity(), "city", jsonObjectLP.getString("city"));
                            SharedPreferenceStore.storeValue(getActivity(), "country", jsonObjectLP.getString("country"));
                        }
                        else
                        {
                            SharedPreferenceStore.storeValue(getActivity(), "LoadingPointName","" );
                            SharedPreferenceStore.storeValue(getActivity(), "city","");
                            SharedPreferenceStore.storeValue(getActivity(), "country","");
                        }
                        getDeviceRegistrationTokenId();
                        Intent intent = new Intent(getActivity(), HomeContainer.class);
                        startActivity(intent);
                        getActivity().finish();
                        Toast.makeText(getActivity(),msg , Toast.LENGTH_SHORT).show();
                      //  Snackbar.make(,"hello",Snackbar.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                    }
                    //  Log.e(Constants.LOG_TAG, responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void getDeviceRegistrationTokenId() {
               String token =  FirebaseInstanceId.getInstance().getToken();
                if(token!=null)
                {
                    updateDeviceTokenOnServer(token) ;
                }
            }

            @Override
            public void onError(String errorMessage) {
                Utils.hideLoadingPopup();
            }
        });
    }

    private void updateDeviceTokenOnServer(String token) {

        String url = Constants.BASE_URL + "get_device_id_after_login";
        HashMap<String, String> data = new HashMap<>();
        data.put("user_id", appGlobal.userId);
        data.put("device_id", token);
        // data.put("device_id", device_id);
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(String errorMessage) {
                Utils.hideLoadingPopup();
            }
        });
    }


}
