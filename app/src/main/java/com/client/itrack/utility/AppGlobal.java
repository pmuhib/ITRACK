package com.client.itrack.utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.activities.HomeContainer;
import com.client.itrack.activities.LoginContainer;
import com.client.itrack.R;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.ClientModel;
import com.client.itrack.model.DSRModel;
import com.client.itrack.model.DSRStatusModel;
import com.client.itrack.model.LocationPointModel;
import com.client.itrack.model.TrailerTypeModel;
import com.client.itrack.model.TruckStatusModel;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

/**
 * Created by sony on 20-05-2016.
 */
public class AppGlobal {

    private AppGlobal()
    {

    }
    private static AppGlobal instance ;

    public ArrayList<LocationPointModel> listLoadingnPoint;
    public ArrayList<LocationPointModel> listDestinationPoint;
    public ArrayList<DSRStatusModel> listDSRStatuses;
    public ArrayList<ClientModel> listClients;
    public ArrayList<TrailerTypeModel> listTrailerType;
    public ArrayList<TruckStatusModel> listTruckStatus;
    public ArrayList<DSRModel> dsrArrList;

    public SimpleDateFormat dateFormat ,onlyDateFormat,onlyTimeFormat;
    public String userType ;
    public String userId ;
    public String userCompId ;
    public String currency_code = "" ;
    public String country_code = "" ;
    public String ytd_permission = "" ;

    public static String log = "" ;

    public SimpleDateFormat getDateFormat() {
        dateFormat =   new SimpleDateFormat("dd-MMM-yy hh:mma", Locale.US);
        return dateFormat;
    }

    public SimpleDateFormat getOnlyDateFormat() {
        onlyDateFormat =   new SimpleDateFormat("dd-MMM-yy", Locale.US);
        return onlyDateFormat ;
    }

    public SimpleDateFormat getOnlyTimeFormat() {
        onlyTimeFormat =   new SimpleDateFormat("hh:mma", Locale.US);
        return onlyTimeFormat ;
    }

    public  String getCSNumberFormat(double value)
    {
       return  NumberFormat.getNumberInstance(Locale.US).format(value);
    }

    public boolean checkIsUserAuthenticated(final Activity context) {
        if(userId != null && !userId.isEmpty())
        {
            return true ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater() ;
        View view = inflater.inflate(R.layout.login_form_comp,null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        ImageView ivCloseLoginPopup = (ImageView)view.findViewById(R.id.ivCloseLoginPopup);
        ivCloseLoginPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final EditText etxt_username = (EditText) view.findViewById(R.id.etxt_username);

        final EditText etxt_pwd = (EditText)view.findViewById(R.id.etxt_pwd);
        final ImageView imgPassShowHide = (ImageView) view.findViewById(R.id.imgPassShowHide);
        imgPassShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.isSelected())
                {
                    v.setSelected(false);
                    etxt_pwd.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);

                }
                else{
                    v.setSelected(true);
                    etxt_pwd.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                etxt_pwd.setSelection(etxt_pwd.getText().length());
            }
        });
        TextView txt_forgotpwd = (TextView)view.findViewById(R.id.txt_forgotpwd);
        txt_forgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginContainer.class);
                intent.putExtra("goto","forgot-pwd");
                context.startActivity(intent);
            }
        });
        TextView txt_login = (TextView)view.findViewById(R.id.txt_login);
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = etxt_username.getText().toString().trim();
                String pswd = etxt_pwd.getText().toString().trim() ;
                    if (Utils.isNetworkConnected(context,false)) {
                    if (uName.length() == 0 || pswd.length() == 0) {
                        Toast.makeText(context, "Please enter username or password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    loginAPI(uName, pswd,context);
                } else {
                    Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TextView txt_signUp = (TextView)view.findViewById(R.id.txt_signUp);
        txt_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
                Intent intent = new Intent(context, LoginContainer.class);
                intent.putExtra("goto","sign-up");
                context.startActivity(intent);
            }
        });
        TextView txt_skip = (TextView)view.findViewById(R.id.txt_skip);
        txt_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        return false ;
    }

    private void loginAPI(String username, String pwd,final Activity context) {
        String url = Constants.BASE_URL + "login";
        Utils.showLoadingPopup(context);

        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", pwd);

        HttpPostRequest.doPost(context, url, Utils.newGson().toJson(data), new HttpRequestCallback() {
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
                        String per=  jsonObject.getString("ytd_permission");
                        SharedPreferenceStore.storeValue(context, "Userid", uid);
                        SharedPreferenceStore.storeValue(context, "UserName", jsonObject.getString("f_name")+" " + jsonObject.getString("l_name"));
                        SharedPreferenceStore.storeValue(context, "PhoneNo", jsonObject.getString("phone_no"));
                        SharedPreferenceStore.storeValue(context, "PhoneCode", jsonObject.getString("code_phone_no"));
                        SharedPreferenceStore.storeValue(context, "Designation", jsonObject.getString("designation"));
                        SharedPreferenceStore.storeValue(context, "Email", jsonObject.getString("email"));
                        SharedPreferenceStore.storeValue(context, "Type",loginType);
                        SharedPreferenceStore.storeValue(context,"ytdpres",per);

                        userType = loginType ;
                        userId =  uid ;
                        ytd_permission=per;
                        SharedPreferenceStore.storeValue(context, "usernameId", jsonObject.getString("user_name"));
                        if(loginType.equals("employee"))
                        {
                            userCompId =  "1" ; // Admin ID
                            SharedPreferenceStore.storeValue(context, "client_comp_id","1");
                            SharedPreferenceStore.storeValue(context, "img", jsonObject.getString("image"));
                            SharedPreferenceStore.storeValue(context, "LoadingPoint", jsonObject.getString("loading_point"));
                            country_code =  jsonObject.getString("country_code") ;
                            currency_code =  jsonObject.getString("currency_code") ;
                            SharedPreferenceStore.storeValue(context, "country_code",country_code);
                            SharedPreferenceStore.storeValue(context,"currency_code",currency_code);
                        }
                        else
                        {
                            userCompId =  jsonObject.getString("company") ;
                            SharedPreferenceStore.storeValue(context, "client_comp_id",jsonObject.getString("company"));
                            SharedPreferenceStore.storeValue(context, "img", jsonObject.getString("emp_img"));
                            SharedPreferenceStore.storeValue(context, "LoadingPoint", "");
                            SharedPreferenceStore.storeValue(context, "country_code","");
                            SharedPreferenceStore.storeValue(context,"currency_code","");
                        }

                        if (jsonObjectLP!=null)
                        {
                            SharedPreferenceStore.storeValue(context, "LoadingPointName", jsonObjectLP.getString("loading_point"));
                            SharedPreferenceStore.storeValue(context, "city", jsonObjectLP.getString("city"));
                            SharedPreferenceStore.storeValue(context, "country", jsonObjectLP.getString("country"));
                        }
                        else
                        {
                            SharedPreferenceStore.storeValue(context, "LoadingPointName","" );
                            SharedPreferenceStore.storeValue(context, "city","");
                            SharedPreferenceStore.storeValue(context, "country","");
                        }
                        getDeviceRegistrationTokenId(context);
                        Intent intent = new Intent(context, HomeContainer.class);
                        context.startActivity(intent);
                        context.finish();
                        Toast.makeText(context,msg , Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                    //  Log.e(Constants.LOG_TAG, responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void getDeviceRegistrationTokenId(final Activity context) {
                String token =  FirebaseInstanceId.getInstance().getToken();
                if(token!=null)
                {
                    updateDeviceTokenOnServer(token,context) ;
                }
            }

            @Override
            public void onError(String errorMessage) {
                Utils.hideLoadingPopup();
            }
        });
    }

    public static String generateRandomString(int length)
    {
        String keyCode = "0123456789";
        char[] chars = keyCode.toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output;
    }

    private void updateDeviceTokenOnServer(String token,final Activity context) {

        String url = Constants.BASE_URL + "get_device_id_after_login";
        HashMap<String, String> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("device_id", token);
        // data.put("device_id", device_id);
        HttpPostRequest.doPost(context, url, Utils.newGson().toJson(data), new HttpRequestCallback() {
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

    public String convertToTitleCase(String input) {
        String titleCaseValue = "" ;
        if(input!=null && !input.trim().isEmpty()) {
            String[] words = input.split(" ");
            StringBuilder sb = new StringBuilder();
            if (words[0].length() > 0) {
                sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString());
                for (int i = 1; i < words.length; i++) {
                    String word = words[i].trim() ;
                    sb.append(" ");
                    if(!word.isEmpty()) {
                        sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString());
                    }
                }
            }
            titleCaseValue = sb.toString();
        }
        return titleCaseValue;
    }

    public static synchronized AppGlobal getInstance(){
        if(instance==null){
            instance=new AppGlobal();
        }
        return instance;
    }



}
