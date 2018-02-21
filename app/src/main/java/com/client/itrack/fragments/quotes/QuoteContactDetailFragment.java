package com.client.itrack.fragments.quotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.activities.CategoryContainer;
import com.client.itrack.R;
import com.client.itrack.adapters.CurrencyCodeAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.CurrencyCodeModel;
import com.client.itrack.model.QuoteDetailsModel;
import com.client.itrack.notification.IMethodNotification;
import com.client.itrack.notification.NotificationSendHandler;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DrawerConst;
import com.client.itrack.utility.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sony on 18-06-2016.
 */
public class QuoteContactDetailFragment extends Fragment {

    AppGlobal appGlobal = AppGlobal.getInstance();
    View view  ;
    ImageView  ivComposeMessage ;
    TextView etAccountNumber,etClientEmpName,etClientCompany,etPhone,tvSubmitQuoteProposal,
            etFax,etEmail,etAddress,tvCountry,tvCity,etZipCode,etIndustry,etContactMethod;
    String action ,userType , userId ;
    QuoteDetailsModel quoteDetailsModel ;
    ArrayList<CurrencyCodeModel> listCurrencyCode ;
    private LinearLayout quoteProposalContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.quote_contact_detail_fragment,container,false);
        userType = appGlobal.userType ;
        userId = appGlobal.userId ;
        setupTopBar();
        setupGUI();
        bindDataToGUI();
        loadCountry();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    private void setupTopBar() {

        Toolbar quote_tool_bar =  (Toolbar) getActivity().findViewById(R.id.quote_tool_bar);
        TextView language = (TextView)quote_tool_bar.findViewById(R.id.navigationdot) ;
        ImageView btn_navigation = (ImageView)quote_tool_bar.findViewById(R.id.btn_navigation) ;
        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        ImageView client_detail_more_option = (ImageView)quote_tool_bar.findViewById(R.id.client_detail_more_option) ;
        ImageView client_detail_edit = (ImageView)quote_tool_bar.findViewById(R.id.client_detail_edit) ;

        //GUI Setting
        client_detail_more_option.setVisibility(View.GONE);
        client_detail_edit.setVisibility(View.GONE);
        language.setVisibility(View.GONE);

    }
    private void setupGUI() {
        etAccountNumber = (TextView)view.findViewById(R.id.etAccountNumber) ;
        etClientEmpName = (TextView) view.findViewById(R.id.etClientEmpName);
        etClientCompany = (TextView)view.findViewById(R.id.etClientCompany) ;
        etPhone = (TextView) view.findViewById(R.id.etPhone) ;
        etFax = (TextView)view.findViewById(R.id.etFax) ;
        etEmail = (TextView)view.findViewById(R.id.etEmail) ;
        etAddress = (TextView)view.findViewById(R.id.etAddress) ;
        tvCountry = (TextView) view.findViewById(R.id.spinnerCountryList);
        tvCity = (TextView) view.findViewById(R.id.spinnerCityList);
        etZipCode = (TextView) view.findViewById(R.id.etZipCode) ;
        etIndustry = (TextView) view.findViewById(R.id.etIndustry) ;
        etContactMethod = (TextView)view.findViewById(R.id.etContactMethod) ;
        ivComposeMessage = (ImageView) view.findViewById(R.id.ivComposeMessage) ;
        ivComposeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CategoryContainer.class);
                intent.putExtra("catpos", DrawerConst.COMPOSE_MSG );
                intent.putExtra("senderId",appGlobal.userCompId) ;
                intent.putExtra("senderEmpId",appGlobal.userId) ;
                intent.putExtra("senderType", appGlobal.userType) ;
                intent.putExtra("receiverId",quoteDetailsModel.client_id);
                intent.putExtra("receiverType",Constants.CLIENT_EMP_TYPE);
                intent.putExtra("receiverEmpId",quoteDetailsModel.clientEmpId);
                intent.putExtra("msgSubject","");
                startActivity(intent);
            }
        });

        quoteProposalContainer =  (LinearLayout) view.findViewById(R.id.quoteProposalContainer);
        quoteProposalContainer.setVisibility(View.GONE);

        tvSubmitQuoteProposal = (TextView)view.findViewById(R.id.tvSubmitQuoteProposal) ;

        tvSubmitQuoteProposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()) ;
                LinearLayout form = new LinearLayout(getActivity());
                form.setOrientation(LinearLayout.VERTICAL);
                form.setPadding(20,10,20,10);
                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setGravity(Gravity.CENTER_VERTICAL);
                linearLayout.setPadding(5,5,10,5);
                final Spinner spinner = new Spinner(getActivity());

                listCurrencyCode = new ArrayList<>();
                CurrencyCodeModel currencyCodeModel ;
                currencyCodeModel= new CurrencyCodeModel();
                currencyCodeModel.id="0";
                currencyCodeModel.currency_code="Select Currency Code";
                listCurrencyCode.add(currencyCodeModel);
                loadCurrencyCode(spinner);
                spinner.setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT));
                final EditText etPrice  = new EditText(getActivity()) ;
                etPrice.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                etPrice.setMaxLines(1);
                etPrice.setPadding(5,5,5,5);
                etPrice.setBackgroundColor(Color.parseColor("#f0f0f0"));
                etPrice.setHint("Enter Price");

                etPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
                linearLayout.addView(spinner);
                linearLayout.addView(etPrice);

                final EditText etMessage = new EditText(getActivity());
                etMessage.setHint("Enter Message Note");
                etMessage.setPadding(10,10,10,10);
                etMessage.setGravity(Gravity.TOP);
                etMessage.setBackgroundColor(Color.parseColor("#f0f0f0"));
                etMessage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,200));
                form.addView(linearLayout);
                form.addView(etMessage);

                builder.setView(form);

                builder.setPositiveButton("Submit Proposal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String message = etMessage.getText().toString().trim();
                        String price = etPrice.getText().toString().trim();
                        String quoteId = quoteDetailsModel.quoteId ;

                        if(spinner.getSelectedItemPosition()<=0)
                        {
                            Toast.makeText(getActivity(), "Please Select Currency Code!", Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        if(price.isEmpty())
                        {
                            Toast.makeText(getActivity(), "Please enter Price !", Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        if(spinner.getSelectedItemPosition()<=0)
                        {
                            Toast.makeText(getActivity(), "Please Select Currency!", Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        String currencyCode =  listCurrencyCode.get(spinner.getSelectedItemPosition()).currency_code;
                        requestSubmitProposal(quoteId,currencyCode ,price,message);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create();
                builder.show() ;
            }

            private void loadCurrencyCode(final Spinner spinner) {

                String url =  Constants.BASE_URL + "currency_code_list";

                HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
                    @Override
                    public void response(String errorMessage, String responseData) {
                        try {
                            listCurrencyCode.clear();
                            CurrencyCodeModel currencyCodeModel ;
                            currencyCodeModel= new CurrencyCodeModel();
                            currencyCodeModel.id="0";
                            currencyCodeModel.currency_code="Select Currency Code";
                            listCurrencyCode.add(currencyCodeModel);
                            JSONObject jobj = new JSONObject(responseData);
                            Boolean status = jobj.getBoolean("status");
                            if (status) {

                                JSONArray jsonArray = jobj.getJSONArray("currency_code");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    currencyCodeModel= new CurrencyCodeModel();
                                    JSONObject jobject = jsonArray.getJSONObject(i);
                                    currencyCodeModel.id = jobject.getString("id");
                                    currencyCodeModel.country_name = jobject.getString("country_name");
                                    currencyCodeModel.currency_code  = jobject.getString("currency_code");
                                    listCurrencyCode.add(currencyCodeModel);
                                }


                                CurrencyCodeAdapter adapter = new CurrencyCodeAdapter(getActivity(), listCurrencyCode);
                                spinner.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        }
                        catch (Exception ex){ex.getMessage();}
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
            }
        });
    }



    private void requestSubmitProposal(String quoteId, final String currencyCode , final String price, final String message) {
        Utils.showLoadingPopup(getActivity());
        String url =  Constants.BASE_URL + "quoteresponsebyemployee";
        HashMap<String,String> data = new HashMap<>();
        data.put("quote_id",quoteId);
        data.put("price",price);
        data.put("currency_code",currencyCode);
        data.put("responsed_message",message) ;
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        requestSubmitQuoteResponseEmail();
                        if(!quoteDetailsModel.client_id.isEmpty()) requestSubmitQuoteResponseNotification();
                        requestSubmitQuoteResponseNotificationForSelected();
                        quoteDetailsModel.quoteStatus =  "responded" ;
                        quoteDetailsModel.currency_code =  currencyCode ;
                        quoteDetailsModel.price =  price ;
                        quoteDetailsModel.response_message =  message ;
                        bindDataToGUI();
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex){ex.getMessage();}
            }
            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    private void requestSubmitQuoteResponseEmail() {

            String url =  Constants.BASE_URL + "quote_response_email";
            HashMap<String,String> data = new HashMap<>();
            data.put("email",quoteDetailsModel.clientEmpEmail);

            HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
                @Override
                public void response(String errorMessage, String responseData) {
                    Utils.hideLoadingPopup();
                    try {
                        JSONObject jobj = new JSONObject(responseData);
                        Boolean status = jobj.getBoolean("status");
                        if (status) {
                            Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception ex){ex.getMessage();}
                }
                @Override
                public void onError(String errorMessage) {
                }
            });
    }

    private void requestSubmitQuoteResponseNotification() {
        try {

            JSONObject root = new JSONObject();
            root.put("quote_id",quoteDetailsModel.quoteId );
            root.put("client_id",quoteDetailsModel.client_id );
            root.put("method_name", IMethodNotification.QUOTE_RESPONSE);
            /*******************************
             * Send Notification on Add DSR
             * *****************************/
            NotificationSendHandler sendHandler = new NotificationSendHandler(getActivity());
            sendHandler.sendNotification(IMethodNotification.QUOTE_RESPONSE,root.toString(),IMethodNotification.LP_USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestSubmitQuoteResponseNotificationForSelected() {
        try {

            JSONObject root = new JSONObject();

            root.put("quote_id",quoteDetailsModel.quoteId);
            root.put("method_name", IMethodNotification.QUOTE_RESPONSE);

            /*******************************
             * Send Notification on Add DSR
             * *****************************/

            NotificationSendHandler sendHandler = new NotificationSendHandler(getActivity());
            sendHandler.sendNotification(IMethodNotification.QUOTE_RESPONSE,root.toString(),IMethodNotification.ADMIN_USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }






    private void bindDataToGUI() {
        Bundle bundle = getArguments();
        if(bundle!=null)
        {
            action = bundle.getString("action");
            quoteDetailsModel = (QuoteDetailsModel)bundle.getSerializable("details");
            etAccountNumber.setText(quoteDetailsModel.account);
            etClientEmpName.setText(quoteDetailsModel.empName);
            etClientCompany.setText(quoteDetailsModel.clientCompName);
            etPhone.setText("+"+quoteDetailsModel.countryCodePhone+" "+quoteDetailsModel.clientEmpPhone);
            etFax.setText(quoteDetailsModel.clientEmpFax);
            etEmail.setText(quoteDetailsModel.clientEmpEmail);
            etAddress.setText(quoteDetailsModel.clientEmpAddres);
            etZipCode.setText(quoteDetailsModel.ziocode);
            etIndustry.setText(quoteDetailsModel.industry);
            etContactMethod.setText(quoteDetailsModel.contactMethod);
            if(quoteDetailsModel.client_id.trim().equals("0"))
            {
                ivComposeMessage.setVisibility(View.GONE);
            }

            /*********** Quote Response *************/
            if(quoteDetailsModel.quoteStatus.toLowerCase().equals("responded"))
            {
                quoteProposalContainer.setVisibility(View.VISIBLE);
                TextView tvOfferedPrices =(TextView) quoteProposalContainer.findViewById(R.id.tvOfferedPrices);
                TextView etQuoteResponseMessage =(TextView) quoteProposalContainer.findViewById(R.id.etQuoteResponseMessage);
                TextView etQuoteProposalStatus =(TextView) quoteProposalContainer.findViewById(R.id.etQuoteProposalStatus);
                String currency_code = quoteDetailsModel.currency_code;
                String price = quoteDetailsModel.price ;
                String response_message = quoteDetailsModel.response_message ;
                String proposal_status = "";
                switch(Integer.parseInt(quoteDetailsModel.parposal_status)){
                    case 0 :
                        proposal_status = "Accept";
                        break ;

                    case 1:
                        proposal_status = "Decline";
                        break ;

                    case 2 :
                        proposal_status = "Undecided";
                        break ;
                }

                tvOfferedPrices.setText(currency_code+" "+price);
                etQuoteResponseMessage.setText(response_message);
                etQuoteProposalStatus.setText(proposal_status);
                tvSubmitQuoteProposal.setText("Edit Proposal");
            }

        }
    }

    private void loadCountry() {
        String url =  Constants.BASE_URL + "country";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("country_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            String id  = jobject.getString("id");
                            if(quoteDetailsModel.countryCode!=null && quoteDetailsModel.countryCode.equals(id)){
                                loadCities(id);
                                String name  = jobject.getString("name");
                                tvCountry.setText(name);
                                break;
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex){ex.getMessage();}
            }
            @Override
            public void onError(String errorMessage) {
            }
        });
    }

    private void loadCities(String id) {
        String url =  Constants.BASE_URL + "city";
        HashMap<String,String> hmap= new HashMap<>();//{"country_id":"101"}
        hmap.put("country_id", id);
        HttpPostRequest.doPost(getActivity(), url,new Gson().toJson(hmap), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("city_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            if (quoteDetailsModel.cityCode != null && quoteDetailsModel.cityCode.equals(jobject.getString("name"))) {
                                String name  = jobject.getString("name");
                                tvCity.setText(name);
                                break;
                            } else if(quoteDetailsModel.cityCode!=null && quoteDetailsModel.cityCode.equals(jobject.getString("id")))
                            {
                                String name  = jobject.getString("name");
                                tvCity.setText(name);
                                break;
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception ex){ex.getMessage();}
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }


}
