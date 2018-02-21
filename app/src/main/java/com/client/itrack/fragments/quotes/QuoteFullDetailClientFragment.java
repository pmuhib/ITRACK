package com.client.itrack.fragments.quotes;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.QuoteDetailsModel;
import com.client.itrack.notification.IMethodNotification;
import com.client.itrack.notification.NotificationSendHandler;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by sony on 18-06-2016.
 */
public class QuoteFullDetailClientFragment extends Fragment implements View.OnClickListener {
    AppGlobal appGlobal = AppGlobal.getInstance();
    View view  ;
    TextView tvOrigin,tvDestination ,etPieces ,etCommodity,etWeightCommodity,etOther,etQuoteMessage,etAdditionalServices ,
            etAccountNumber,etClientEmpName,etClientCompany,etPhone,etFax,etEmail,etAddress,tvCountry,tvCity,etZipCode,etIndustry,etContactMethod,lblstat,etlablstat;

    GridLayout servicesOfInterestContainer ;
    LinearLayout quoteProposalContainer,Bt_listcontainer ;
    String quote_id ,userId,userType;
    Button Accept,decline,undecided;
    QuoteDetailsModel quoteDetailsModel ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.quote_full_detail_client_fragment,container,false);
        userId = appGlobal.userId ;
        userType = appGlobal.userType ;
        quote_id = getArguments().getString("quote_id");
        setupTopBar();
        setupGUI();

        return view;
    }

    private void setupGUI() {
        servicesOfInterestContainer = (GridLayout) view.findViewById(R.id.servicesOfInterestContainer);
        tvOrigin = (TextView) view.findViewById(R.id.tvOrigin);
        Accept= (Button) view.findViewById(R.id.btn_accept);
        decline= (Button) view.findViewById(R.id.btn_decline);
        undecided= (Button) view.findViewById(R.id.btn_Undecided);
        Accept.setOnClickListener(this);
        decline.setOnClickListener(this);
        undecided.setOnClickListener(this);
        tvDestination = (TextView) view.findViewById(R.id.tvDestination);
        etCommodity = (TextView) view.findViewById(R.id.etCommodity);
        etPieces = (TextView) view.findViewById(R.id.etPieces);
        etWeightCommodity = (TextView) view.findViewById(R.id.etWeightCommodity);
        etOther = (TextView) view.findViewById(R.id.etOther);
        etQuoteMessage = (TextView) view.findViewById(R.id.etQuoteMessage);
        etAdditionalServices = (TextView) view.findViewById(R.id.etAdditionalServices);
        TextView tvViewContactDetails = (TextView) view.findViewById(R.id.tvViewContactDetails);
        tvViewContactDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        lblstat= (TextView) view.findViewById(R.id.lblstat);
        etlablstat= (TextView) view.findViewById(R.id.etlablstat);
        quoteProposalContainer =  (LinearLayout) view.findViewById(R.id.quoteProposalContainer);
        Bt_listcontainer= (LinearLayout) view.findViewById(R.id.btn_list);
        Bt_listcontainer.setVisibility(View.GONE);
        quoteProposalContainer.setVisibility(View.GONE);
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
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        ImageView client_detail_more_option = (ImageView)quote_tool_bar.findViewById(R.id.client_detail_more_option) ;
        client_detail_more_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ImageView client_detail_edit = (ImageView)quote_tool_bar.findViewById(R.id.client_detail_edit) ;
        client_detail_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddQuoteServicesDetailFragment detailFragment = new AddQuoteServicesDetailFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                Bundle bundle = new Bundle();
                bundle.putSerializable("details",quoteDetailsModel);
                bundle.putCharSequence("action","edit");
                detailFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.quote_detail_container,detailFragment);
                fragmentTransaction.commit();
            }
        });

        //GUI Setting
        client_detail_more_option.setVisibility(View.VISIBLE);
        client_detail_edit.setVisibility(View.VISIBLE);
        language.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView)getActivity().findViewById(R.id.txt_heading)).setText("Global Logistics");
        if(quote_id!=null && !quote_id.isEmpty())
        {
            loadQuoteDetailByQuoteId(quote_id);
        }
    }

    private void loadQuoteDetailByQuoteId(final String quote_id) {
        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL + "quotedetailbyid";

        HashMap<String, String> hm = new HashMap<>();
        hm.put("quote_id", quote_id);

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(hm), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status = jObj.getBoolean("status");

                    if (status) {
                        quoteDetailsModel = new QuoteDetailsModel();
                        JSONObject quoteJObj = jObj.getJSONObject("quote_detail") ;

                        /*******Services Quote Details*******/
                        String quote_id = quoteJObj.getString("quote_id");
                        quote_id = quote_id!=null?quote_id.trim():"";
                        String service_of_interest = quoteJObj.getString("service_of_interest");
                        service_of_interest=service_of_interest!=null?service_of_interest.trim():"";
                        String origin = quoteJObj.getString("origin");
                        origin = origin!=null?origin.trim():"";
                        String loding_city = quoteJObj.getString("loding_city");
                        loding_city =  loding_city!=null?loding_city.trim():"" ;
                        String loading_state = quoteJObj.getString("loading_state");
                        loading_state =  loading_state!=null?loading_state.trim():"" ;

                        String loding_country = quoteJObj.getString("loding_country");
                        loding_country = loding_country!=null?loding_country.trim():"";
                        String destination = quoteJObj.getString("destination");
                        destination = destination!=null ?destination.trim() :"" ;
                        String destination_state = quoteJObj.getString("destination_state");
                        destination_state = destination_state!=null?destination_state.trim():"" ;

                        String destination_city = quoteJObj.getString("destination_city");
                        destination_city = destination_city!=null?destination_city.trim():"" ;

                        String destination_country = quoteJObj.getString("destination_country");
                        destination_country = destination_country!=null?destination_country.trim():"" ;
                        String commodity = quoteJObj.getString("commodity");
                        commodity= commodity!=null?commodity.trim():"";
                        String pieces = quoteJObj.getString("pieces");
                        pieces= pieces!=null?pieces.trim():"";
                        String weight = quoteJObj.getString("weight");
                        weight = weight!=null ?weight.trim():"";
                        String other = quoteJObj.getString("other");
                        other = other!=null ?other.trim() :"" ;
                        String additional_service = quoteJObj.getString("additional_service");
                        additional_service= additional_service!=null ?additional_service.trim() :"" ;
                        String contact_message = quoteJObj.getString("contact_message");
                        contact_message =  contact_message!=null?contact_message.trim():"" ;
                        quoteDetailsModel.quoteId = quote_id ;
                        quoteDetailsModel.servicesOfInterests = service_of_interest ;
                        quoteDetailsModel.origin = origin ;
                        quoteDetailsModel.loadingCity = loding_city ;
                        quoteDetailsModel.loadingState= loading_state ;
                        quoteDetailsModel.loadingCountry = loding_country ;
                        quoteDetailsModel.destination = destination ;
                        quoteDetailsModel.destinationCity = destination_city ;
                        quoteDetailsModel.destinationState = destination_state ;
                        quoteDetailsModel.destinationCountry = destination_country ;
                        quoteDetailsModel.commodity = commodity ;
                        quoteDetailsModel.contactMessage =  contact_message ;
                        quoteDetailsModel.pieces = pieces ;
                        quoteDetailsModel.weight = weight ;
                        quoteDetailsModel.others = other ;
                        quoteDetailsModel.addServices = additional_service ;
                        servicesOfInterestContainer.removeAllViews();
                        String[] servicesArr  =  service_of_interest.split(",");
                        for (String service:servicesArr) {
                            if(!service.trim().isEmpty())
                            {
                                TextView textView = new TextView(getActivity());
                                textView.setText(service.trim());
                                if (Build.VERSION.SDK_INT < 23) {
                                    textView.setTextAppearance(getActivity(), R.style.GLLblNormalStyle);
                                } else{
                                    textView.setTextAppearance(R.style.GLLblNormalStyle);
                                }
                                textView.setEllipsize(TextUtils.TruncateAt.END);
                                //textView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,GridView.LayoutParams.WRAP_CONTENT));
                                servicesOfInterestContainer.addView(textView);
                            }
                        }
                        tvOrigin.setText(origin);
                        tvDestination.setText(destination);
                        etCommodity.setText(commodity);
                        etPieces.setText(pieces);
                        etWeightCommodity.setText(weight);
                        etOther.setText(other);
                        etAdditionalServices.setText(additional_service);
                        etQuoteMessage.setText(contact_message);

                        /*******Contact Quote Details*******/
                        String account = quoteJObj.getString("account");
                        String name = quoteJObj.getString("name");
                        String company = quoteJObj.getString("company");
                        String countryCodePhone = quoteJObj.getString("code_phone_no");
                        String phone = quoteJObj.getString("phone");
                        String fax = quoteJObj.getString("fax");
                        String email = quoteJObj.getString("email");
                        String address  = quoteJObj.getString("address");
                        String city  = quoteJObj.getString("city");
                        String country  =  quoteJObj.getString("country");
                        String zipcode  = quoteJObj.getString("zipcode");
                        String industry  = quoteJObj.getString("industry");
                        String contact_method = quoteJObj.getString("contact_method");

                        String date  = quoteJObj.getString("date");
                        String quoteStatus  = quoteJObj.getString("status");
                        String parposal_status  = quoteJObj.getString("parposal_status");
                        quoteDetailsModel.account = account ;
                        quoteDetailsModel.empName = name ;
                        quoteDetailsModel.clientCompName = company ;
                        quoteDetailsModel.clientEmpPhone = phone ;
                        quoteDetailsModel.countryCodePhone =  countryCodePhone;
                        quoteDetailsModel.clientEmpFax = fax ;
                        quoteDetailsModel.clientEmpEmail = email ;
                        quoteDetailsModel.clientEmpAddres = address ;
                        quoteDetailsModel.cityCode = city ;

                        quoteDetailsModel.countryCode = country ;
                        quoteDetailsModel.ziocode = zipcode ;
                        quoteDetailsModel.industry = industry ;
                        quoteDetailsModel.contactMethod = contact_method ;
                        quoteDetailsModel.contactMessage = contact_message ;
                        quoteDetailsModel.createdDate = date ;
                        quoteDetailsModel.quoteStatus = quoteStatus ;
                        quoteDetailsModel.parposal_status = parposal_status ;
                        etAccountNumber.setText(account);
                        etClientEmpName.setText(name);
                        etClientCompany.setText(company);
                        etPhone.setText("+"+countryCodePhone+" "+phone);
                        etFax.setText(fax);
                        etEmail.setText(email);
                        etAddress.setText(address);
                        etZipCode.setText(zipcode);
                        etIndustry.setText(industry);
                        etContactMethod.setText(contact_method);
                      //  etlablstat.setText(parposal_status);
                        if(parposal_status.equalsIgnoreCase("0"))
                        {
                            etlablstat.setText("Accept");
                        }
                        else if(parposal_status.equalsIgnoreCase("1"))
                        {
                            etlablstat.setText("Decline");
                        }
                        else if(parposal_status.equalsIgnoreCase("2"))
                        {
                            etlablstat.setText("Undecided");
                        }

                        loadCountry();
                        /*********** Quote Response *************/
                        if(quoteStatus.toLowerCase().equals("responded"))
                        {
                            quoteProposalContainer.setVisibility(View.VISIBLE);
                            if(parposal_status.equalsIgnoreCase("2"))
                            Bt_listcontainer.setVisibility(View.VISIBLE);
                            else   Bt_listcontainer.setVisibility(View.GONE);

                            TextView tvOfferedPrices =(TextView) quoteProposalContainer.findViewById(R.id.tvOfferedPrices);
                            TextView etQuoteResponseMessage =(TextView) quoteProposalContainer.findViewById(R.id.etQuoteResponseMessage);
                            String currency_code = quoteJObj.getString("currency_code");
                            String price = quoteJObj.getString("price");
                            String response_message = quoteJObj.getString("response_message");
                            tvOfferedPrices.setText(currency_code+" "+price);
                            etQuoteResponseMessage.setText(response_message);
                        }
                    }
                }
                catch(Exception ex)
                {
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
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
                            }
                            else if(quoteDetailsModel.cityCode!=null && quoteDetailsModel.cityCode.equals(jobject.getString("id")))
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

    @Override
    public void onClick(View view) {
        int id=view.getId();
        String prop="";
        switch (id)
        {
            case R.id.btn_accept:
                prop="0";
                if(quote_id!=null && !quote_id.isEmpty())
                {
                     setReview(prop,quote_id);
                    break;
                }
            case  R.id.btn_decline:
                prop="1";
                if(quote_id!=null && !quote_id.isEmpty())
                {
                    setReview(prop,quote_id);
                    break;
                }
            case  R.id.btn_Undecided:
                prop="2";
                if(quote_id!=null && !quote_id.isEmpty())
                {
                    setReview(prop,quote_id);
                    break;
                }
        }
    }

    private void setReview(String prop, String quote_id) {
        Utils.showLoadingPopup(getActivity());
        String url = Constants.BASE_URL + "update_quote_by_praposalstatus";

        HashMap<String, String> hm = new HashMap<>();
        hm.put("parposal_status",prop);
        hm.put("quote_id", quote_id);
        Log.d("Prop",prop);
        Log.d("Id",quote_id);
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(hm), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                       int ps  =  jobj.getInt("parposal_status");
                        switch(ps)
                        {
                            case 0 :
                                etlablstat.setText("Accept");
                                Bt_listcontainer.setVisibility(View.GONE);
                                break ;

                            case 1 :
                                etlablstat.setText("Decline");
                                Bt_listcontainer.setVisibility(View.GONE);
                                break ;

                            case 2 :
                                etlablstat.setText("Undecided");
                                Bt_listcontainer.setVisibility(View.VISIBLE);
                                break ;
                        }
                        requestSubmitQuoteProposalResponseNotification(ps);
                        requestSubmitQuoteProposalResponseNotificationForSelected(ps);

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



    private void requestSubmitQuoteProposalResponseNotification(int proposal_status) {
        try {

            JSONObject root = new JSONObject();
            root.put("proposal_status",proposal_status+"");
            root.put("quote_id",quote_id );
            root.put("method_name", IMethodNotification.Quote_PROPOSAL_RESPONSE);
            root.put("origin",quoteDetailsModel.loading_id);
            /*******************************
             * Send Notification on Add DSR
             * *****************************/
            NotificationSendHandler sendHandler = new NotificationSendHandler(getActivity());
            sendHandler.sendNotification(IMethodNotification.Quote_PROPOSAL_RESPONSE,root.toString(),IMethodNotification.LP_USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestSubmitQuoteProposalResponseNotificationForSelected(int proposal_status) {
        try {

            JSONObject root = new JSONObject();
            root.put("proposal_status",proposal_status+"");
            root.put("quote_id",quoteDetailsModel.quoteId);
            root.put("method_name", IMethodNotification.Quote_PROPOSAL_RESPONSE);

            /*******************************
             * Send Notification on Add DSR
             * *****************************/

            NotificationSendHandler sendHandler = new NotificationSendHandler(getActivity());
            sendHandler.sendNotification(IMethodNotification.Quote_PROPOSAL_RESPONSE,root.toString(),IMethodNotification.ADMIN_USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
