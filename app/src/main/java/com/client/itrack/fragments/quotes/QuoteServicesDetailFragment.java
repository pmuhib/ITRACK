package com.client.itrack.fragments.quotes;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.QuoteDetailsModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utils;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by sony on 18-06-2016.
 */
public class QuoteServicesDetailFragment extends Fragment {
    AppGlobal appGlobal = AppGlobal.getInstance();
    View view  ;
    TextView tvOrigin,tvDestination ,etPieces ,etCommodity,etWeightCommodity,etOther,etQuoteMessage,etAdditionalServices ;
    GridLayout servicesOfInterestContainer ;
    String quote_id ,userId,userType;

    QuoteDetailsModel quoteDetailsModel ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.quote_service_detail_fragment,container,false);
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
                QuoteContactDetailFragment contactDetailFragment = new QuoteContactDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("details",quoteDetailsModel);
                contactDetailFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction() ;
                fragmentTransaction.replace(R.id.quote_detail_container,contactDetailFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
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


        client_detail_more_option.setVisibility(View.GONE);
        client_detail_edit.setVisibility(View.GONE);
        language.setVisibility(View.GONE);


    }

    @Override
    public void onResume() {
        super.onResume();
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
                        String client_id = quoteJObj.getString("client_id");
                        client_id = client_id!=null?client_id.trim():"";
                        String service_of_interest = quoteJObj.getString("service_of_interest");
                        service_of_interest=service_of_interest!=null?service_of_interest.trim():"";
                        String origin = quoteJObj.getString("origin");
                        origin = origin!=null?origin.trim():"";
                        String loding_city = quoteJObj.getString("loding_city");
                        loding_city =  loding_city!=null?loding_city.trim():"" ;
                        String loding_country = quoteJObj.getString("loding_country");
                        loding_country = loding_country!=null?loding_country.trim():"";
                        String destination = quoteJObj.getString("destination");
                        destination = destination!=null ?destination.trim() :"" ;
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
                        String employee_id = quoteJObj.getString("employee_id");
                        employee_id =  employee_id!=null?employee_id.trim():"" ;

                        quoteDetailsModel.client_id = client_id ;
                        quoteDetailsModel.quoteId = quote_id ;
                        quoteDetailsModel.servicesOfInterests = service_of_interest ;
                        quoteDetailsModel.origin = origin ;
                        quoteDetailsModel.loadingCity = loding_city ;
                        quoteDetailsModel.loadingCountry = loding_country ;
                        quoteDetailsModel.destination = destination ;
                        quoteDetailsModel.destinationCity = destination_city ;
                        quoteDetailsModel.destinationCountry = destination_country ;
                        quoteDetailsModel.commodity = commodity ;
                        quoteDetailsModel.contactMessage =  contact_message ;
                        quoteDetailsModel.clientEmpId =  employee_id ;
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
                        String proposal_status  = quoteJObj.getString("parposal_status");

                        quoteDetailsModel.account = account ;
                        quoteDetailsModel.empName = name ;
                        quoteDetailsModel.clientCompName = company ;
                        quoteDetailsModel.clientEmpPhone = phone ;
                        quoteDetailsModel.countryCodePhone = countryCodePhone ;
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

                        /***** Quote Submission Status & Details *****/

                        String response_message = quoteJObj.getString("response_message");
                        response_message =  response_message!=null?response_message.trim():"" ;

                        String currency_code = quoteJObj.getString("currency_code");
                        currency_code =  currency_code!=null?currency_code.trim():"" ;

                        String price = quoteJObj.getString("price");
                        price =  price!=null?price.trim():"" ;


                        quoteDetailsModel.quoteStatus = quoteStatus ;
                        quoteDetailsModel.response_message = response_message ;
                        quoteDetailsModel.price = price ;
                        quoteDetailsModel.currency_code = currency_code ;
                        quoteDetailsModel.parposal_status = proposal_status ;
                    }
                }
                catch(Exception ex)
                {
                    String exc =   ex.getMessage() ;
                    if(exc!=null) Toast.makeText(getActivity(),exc, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }


}
