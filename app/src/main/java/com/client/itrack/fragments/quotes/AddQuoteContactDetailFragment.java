package com.client.itrack.fragments.quotes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.adapters.AutoSearchAdapter;
import com.client.itrack.adapters.QuoteStatusSpinnerAdapter;
import com.client.itrack.adapters.SpinnerAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.CityModel;
import com.client.itrack.model.CountryModel;
import com.client.itrack.model.LocationPointModel;
import com.client.itrack.model.QuoteDetailsModel;
import com.client.itrack.model.StateModel;
import com.client.itrack.notification.IMethodNotification;
import com.client.itrack.notification.NotificationSendHandler;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.SharedPreferenceStore;
import com.client.itrack.utility.Utility;
import com.client.itrack.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sony on 18-06-2016.
 */
public class AddQuoteContactDetailFragment extends Fragment {

    AppGlobal appGlobal = AppGlobal.getInstance();
    View view  ;
    TextView  mTitle ;
    EditText etAccountNumber,etClientEmpName,etClientCompany,etPhone,etPhoneCountryCode,etFax,etEmail,etAddress,etZipCode,etIndustry ;
    Spinner spinnerContactMethodList;
    AutoCompleteTextView autocompleteTVCountry ,autocompleteTVCity;
    String action;
    QuoteDetailsModel quoteDetailsModel;
    ArrayList<CountryModel> listCountry ;

    ArrayList<CountryModel> listCity ;
    ArrayList<String> listContactMethod;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.add_quote_contact_detail_fragment,container,false);
        setupTopBar();
        setupGUI();
        bindDataToGUI();
        loadQuoteContactMethodList();
        return view;
    }

    private void loadQuoteContactMethodList() {
        listContactMethod = new ArrayList<>();
        listContactMethod.add("Select Method");
        listContactMethod.add("Fax");
        listContactMethod.add("Phone");
        listContactMethod.add("Email");
        QuoteStatusSpinnerAdapter adapter = new QuoteStatusSpinnerAdapter(getActivity(),listContactMethod);
        spinnerContactMethodList.setAdapter(adapter);
        setContactMethod();

    }
    @Override
    public void onResume() {
        super.onResume();
        switch(action){
            case "edit" :
                ((TextView)getActivity().findViewById(R.id.txt_heading)).setText("Edit Quote");
                break ;
            case "add" :
                ((TextView)getActivity().findViewById(R.id.txt_heading)).setText("Add Quote");
                break ;
        }
    }

    private void setupGUI() {
        etAccountNumber = (EditText)view.findViewById(R.id.etAccountNumber) ;
        etClientEmpName = (EditText) view.findViewById(R.id.etClientEmpName);
        etClientCompany = (EditText)view.findViewById(R.id.etClientCompany) ;
        etPhone = (EditText) view.findViewById(R.id.etPhone) ;
        etPhoneCountryCode = (EditText) view.findViewById(R.id.etPhoneCountryCode) ;
        etFax = (EditText)view.findViewById(R.id.etFax) ;
        etEmail = (EditText)view.findViewById(R.id.etEmail) ;
        etAddress = (EditText)view.findViewById(R.id.etAddress) ;
        etZipCode = (EditText) view.findViewById(R.id.etZipCode) ;
        etIndustry = (EditText) view.findViewById(R.id.etIndustry) ;
        //etContactMethod = (EditText)view.findViewById(R.id.etContactMethod) ;
        spinnerContactMethodList = (Spinner) view.findViewById(R.id.spinnerContactMethodList) ;

        autocompleteTVCountry = (AutoCompleteTextView) view.findViewById(R.id.autocompleteTVCountry) ;
        autocompleteTVCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CountryModel countryModel =  (CountryModel)((ListView) adapterView).getAdapter().getItem(i);
                loadCities(countryModel.id);
                quoteDetailsModel.countryCode =  countryModel.id ;
            }
        });


        autocompleteTVCity = (AutoCompleteTextView) view.findViewById(R.id.autocompleteTVCity) ;
        autocompleteTVCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CountryModel countryModel =  (CountryModel)((ListView) adapterView).getAdapter().getItem(i) ;
                quoteDetailsModel.cityCode  =  countryModel.id ;
            }
        });

        TextView tvSubmitFinalDetails = (TextView)view.findViewById(R.id.tvSubmitFinalDetails) ;
        tvSubmitFinalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validationQuoteContact())
                {
                    switch(action)
                    {
                        case  "add":
                            requestAddNewQuote();
                            break ;

                        case "edit" :
                            requestUpdateQuote();
                            break ;
                    }
                }
            }
        });

    }

    /****
     *
     *
     * SERVER REQUEST SERVICES
     *
     * ADD & UPDATE QUOTE
     *
     * PUSH NOTIFICATION QUOTE ADD UPDATE
     * <>GLOBAL EMPLOYEES ON THE BASIS OF LOADING POINTS</> AND
     *
     * */


    private void requestUpdateQuote() {
        Utils.showLoadingPopup(getActivity());
        String url = Constants.BASE_URL+"updatequote" ;
        String data  =  createJsonData();

        HttpPostRequest.doPost(getActivity(), url, data, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status = jObj.getBoolean("status");

                    if (status) {

                        // EMAIL AND NOTIFICATION SEND REQUEST API CALL For Update Quote
                        requestUpdateQuoteEmail();
                        requestUpdateQuoteNotification(quoteDetailsModel.quoteId);
                        requestUpdateQuoteNotificationForSelected(quoteDetailsModel.quoteId);

                        Toast.makeText(getActivity(),jObj.getString("msg") , Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),jObj.getString("msg") , Toast.LENGTH_SHORT).show();
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


    private void requestAddNewQuote() {
        Utils.showLoadingPopup(getActivity());
        String url = Constants.BASE_URL+"quote" ;
        String data  =  createJsonData();

        HttpPostRequest.doPost(getActivity(), url,data, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status = jObj.getBoolean("status");

                    if (status) {
                        int quote_id = jObj.getInt("msg_data") ;

                        requestAddNewQuoteEmail();
                        requestAddQuoteNotification(quote_id+"");
                        requestAddQuoteNotificationForSelected(quote_id+"");
                        AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Your Quote request has been submitted. Someone from Global Logistics will contact you shortly.");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().finish();
                            }
                        });
                        builder.setCancelable(false);
                        builder.create();
                        builder.show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),jObj.getString("msg") , Toast.LENGTH_SHORT).show();
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

    /***
     * ************************************************
     * QUOTE EMAIL  & PUSH NOTIFICATION SEND SERVICES
     * ***********************************************
     * ***/

    /***
     * ********************
     * EMAIL SENT SERVICES
     * *******************
     ***/
    private void requestAddNewQuoteEmail() {
        String url = Constants.BASE_URL+"add_quote_email" ;
        HashMap<String,String> data  = new HashMap<>();
        data.put("origin",quoteDetailsModel.loading_id);
        HttpPostRequest.doPost(getActivity(), url,Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status = jObj.getBoolean("status");
                    if (status) Toast.makeText(getActivity(),jObj.getString("msg") , Toast.LENGTH_SHORT).show();
                    else Toast.makeText(getActivity(),jObj.getString("msg") , Toast.LENGTH_SHORT).show();
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
    private void requestUpdateQuoteEmail() {
        String url = Constants.BASE_URL+"update_quote_email" ;
        HashMap<String,String> data  = new HashMap<>();
        data.put("origin",quoteDetailsModel.loading_id);
        HttpPostRequest.doPost(getActivity(), url,Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status = jObj.getBoolean("status");
                    if (status) Toast.makeText(getActivity(),jObj.getString("msg") , Toast.LENGTH_SHORT).show();
                    else Toast.makeText(getActivity(),jObj.getString("msg") , Toast.LENGTH_SHORT).show();
                }
                catch(Exception ex){ Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();}
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }

    /***
     * *********************************
     *  PUSH NOTIFICATION SEND SERVICES
     * *********************************
     * **/

    private void requestUpdateQuoteNotification(String quoteId) {
        try {

            JSONObject root = new JSONObject();
            root.put("quote_id",quoteId );
            root.put("method_name", IMethodNotification.UPDATE_QUOTE);
            root.put("origin",quoteDetailsModel.loading_id);
            /*******************************
             * Send Notification on Update Quote
             * *****************************/
            NotificationSendHandler sendHandler = new NotificationSendHandler(getActivity());
            sendHandler.sendNotification(IMethodNotification.UPDATE_QUOTE,root.toString(),IMethodNotification.LP_USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void requestUpdateQuoteNotificationForSelected(String quoteId) {

        try {

            JSONObject root = new JSONObject();

            root.put("quote_id",quoteId );
            root.put("method_name", IMethodNotification.UPDATE_QUOTE);

            /*******************************
             * Send Notification on Add DSR
             * *****************************/

            NotificationSendHandler sendHandler = new NotificationSendHandler(getActivity());
            sendHandler.sendNotification(IMethodNotification.UPDATE_QUOTE,root.toString(),IMethodNotification.ADMIN_USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void requestAddQuoteNotification(String quoteId) {
        try {

            JSONObject root = new JSONObject();
            root.put("quote_id",quoteId );
            root.put("method_name", IMethodNotification.ADD_QUOTE);
            root.put("origin",quoteDetailsModel.loading_id);
            /*******************************
             * Send Notification on Add DSR
             * *****************************/
            NotificationSendHandler sendHandler = new NotificationSendHandler(getActivity());
            sendHandler.sendNotification(IMethodNotification.ADD_QUOTE,root.toString(),IMethodNotification.LP_USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void requestAddQuoteNotificationForSelected(String quoteId) {
        try {

            JSONObject root = new JSONObject();

            root.put("quote_id",quoteId );
            root.put("method_name", IMethodNotification.ADD_QUOTE);

            /*******************************
             * Send Notification on Add DSR
             * *****************************/

            NotificationSendHandler sendHandler = new NotificationSendHandler(getActivity());
            sendHandler.sendNotification(IMethodNotification.ADD_QUOTE,root.toString(),IMethodNotification.ADMIN_USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***** END PUSH NOTIFICATION SEND SERVICES ****/

    private String createJsonData()
    {

        HashMap<String,String> data  = new HashMap<>();
        if(!quoteDetailsModel.quoteId.isEmpty()) data.put("quote_id",quoteDetailsModel.quoteId) ;
        data.put("client_id",appGlobal.userCompId);
        data.put("service_of_interest",quoteDetailsModel.servicesOfInterests);
        data.put("origin",quoteDetailsModel.origin);
        data.put("loding_city",quoteDetailsModel.loadingCity);
        data.put("loading_state",quoteDetailsModel.loadingState);
        data.put("loding_country",quoteDetailsModel.loadingCountry);
        data.put("destination",quoteDetailsModel.destination);
        data.put("destination_city",quoteDetailsModel.destinationCity);
        data.put("destination_state",quoteDetailsModel.destinationState);
        data.put("destination_country",quoteDetailsModel.destinationCountry);
        data.put("commodity",quoteDetailsModel.commodity);
        data.put("pieces",quoteDetailsModel.pieces);
        data.put("weight",quoteDetailsModel.weight);
        data.put("other",quoteDetailsModel.others);
        data.put("additional_service",quoteDetailsModel.addServices);
        data.put("account",quoteDetailsModel.account);
        data.put("name",quoteDetailsModel.empName);
        data.put("company",quoteDetailsModel.clientCompName);
        data.put("code_phone_no",quoteDetailsModel.countryCodePhone);
        data.put("phone",quoteDetailsModel.clientEmpPhone);
        data.put("fax",quoteDetailsModel.clientEmpFax);
        data.put("email",quoteDetailsModel.clientEmpEmail);
        data.put("address",quoteDetailsModel.clientEmpAddres);
        data.put("country",quoteDetailsModel.countryCode);
        data.put("state",quoteDetailsModel.stateCode);
        data.put("city",quoteDetailsModel.cityCode);
        data.put("zipcode",quoteDetailsModel.ziocode);
        data.put("industry",quoteDetailsModel.industry);
        data.put("contact_method",quoteDetailsModel.contactMethod);
        data.put("contact_message",quoteDetailsModel.contactMessage);
        data.put("employee_id",appGlobal.userId);
        return Utils.newGson().toJson(data);
    }

    private boolean validationQuoteContact() {

        String empName = etClientEmpName.getText().toString().trim();
        if(empName.isEmpty()){
            Toast.makeText(getActivity(), "Please Enter Name !", Toast.LENGTH_SHORT).show();
            return false;
        }
        else quoteDetailsModel.empName =  empName ;
        String email =  etEmail.getText().toString().trim() ;
        if(email.isEmpty())
        {
            Toast.makeText(getActivity(), "Please Enter Email Id!", Toast.LENGTH_SHORT).show();
            return false ;
        }
        else if(!Utils.isValidEmail(email))
        {
            Toast.makeText(getActivity(), "Please Enter valid Email!", Toast.LENGTH_SHORT).show();
            return false ;
        }
        else quoteDetailsModel.clientEmpEmail =  email ;







        //        if(spinnerContactMethodList.getSelectedItemPosition()<=0)
//        {
//            Toast.makeText(getActivity(), "Please select Contact Method!", Toast.LENGTH_SHORT).show();
//            return false ;
//        }
//        else
//        {
//            quoteDetailsModel.contactMethod =  listContactMethod.get(spinnerContactMethodList.getSelectedItemPosition());
//            String phoneCode = etPhoneCountryCode.getText().toString().trim() ;
//            String phone =  etPhone.getText().toString().trim() ;
//            String fax  = etFax.getText().toString().trim() ;
//            switch(quoteDetailsModel.contactMethod)
//            {
//                case "Fax" :
//                    if(fax.isEmpty())
//                    {
//                        Toast.makeText(getActivity(), "Please Fill Fax Value!", Toast.LENGTH_SHORT).show();
//                        return false  ;
//                    }
//                    else quoteDetailsModel.clientEmpFax =  fax ;
//                    break ;
//
//                case "Phone" :
//                    if(phoneCode.isEmpty() || phone.isEmpty()) {
//                        Toast.makeText(getActivity(), "Both Phone No. & Country Code is required!", Toast.LENGTH_SHORT).show();
//                        return false  ;
//                    }
//                    else {
//                        quoteDetailsModel.clientEmpPhone =  phone ;
//                        quoteDetailsModel.countryCodePhone =  phoneCode ;
//                    }
//                    break ;
//            }
//        }

        quoteDetailsModel.countryCodePhone =  etPhoneCountryCode.getText().toString().trim() ;
        quoteDetailsModel.account = etAccountNumber.getText().toString().trim() ;
        quoteDetailsModel.clientCompName = etClientCompany.getText().toString().trim() ;
        quoteDetailsModel.clientEmpPhone =  etPhone.getText().toString().trim() ;
        quoteDetailsModel.clientEmpFax = etFax.getText().toString().trim() ;
        quoteDetailsModel.clientEmpAddres = etAddress.getText().toString().trim() ;

        quoteDetailsModel.ziocode = etZipCode.getText().toString().trim() ;
        quoteDetailsModel.industry = etIndustry.getText().toString().trim() ;

        return true  ;
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
            etPhone.setText(quoteDetailsModel.clientEmpPhone);
            etPhoneCountryCode.setText(quoteDetailsModel.countryCodePhone);
            etFax.setText(quoteDetailsModel.clientEmpFax);
            etEmail.setText(quoteDetailsModel.clientEmpEmail);
            etAddress.setText(quoteDetailsModel.clientEmpAddres);
            etZipCode.setText(quoteDetailsModel.ziocode);
            etIndustry.setText(quoteDetailsModel.industry);
            // etContactMethod.setText(quoteDetailsModel.contactMethod);

            switch(action)
            {
                case "edit":
                    mTitle.setText(getResources().getText(R.string.edit_quote));
                    break ;
                case "add" :
                    mTitle.setText(getResources().getText(R.string.add_quote));
                    break;
            }
            // GUI setting
            setupGUISettings();

        }
    }

    private void setContactMethod() {
        if(quoteDetailsModel.contactMethod.trim().isEmpty())
        {
            spinnerContactMethodList.setSelection(0);
        }
        else
        {

            for (int indexCM = 0; indexCM < listContactMethod.size(); indexCM++) {

                String cm = listContactMethod.get(indexCM);
                if(cm.toLowerCase().equals(quoteDetailsModel.contactMethod.trim().toLowerCase()))
                {
                    spinnerContactMethodList.setSelection(indexCM);
                    break ;
                }
            }
        }
    }

    private void setupGUISettings() {
        etAccountNumber.setEnabled(false);
        etAccountNumber.setFocusable(false);


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialiseSpinner();

        switch(action)
        {
            case "edit" :
                setCountry();

                break ;


            case "add" :
                if(!appGlobal.userCompId.trim().isEmpty())
                {
                    loadClientCompDetails(appGlobal.userCompId.trim());
                }
                else
                {
                    setCountry();
                }

                break ;
        }
    }

    private void setCountry() {
        if(Utils.isNetworkConnected(getActivity(),false)) {
            loadCountry();
        }
        else{
            Toast.makeText(getActivity(), "Internet not connected", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadClientCompDetails(String clientCompId) {
        Utils.showLoadingPopup(getActivity());
        String url = Constants.BASE_URL + "clientview";
        HashMap<String, String> hm = new HashMap<>();
        hm.put("client_id", clientCompId);

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(hm), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                try {
                    Utils.hideLoadingPopup();
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONObject jsonArray = jobj.getJSONObject("client_details");
                        String companyName = jsonArray.getString("company_name");
                        String  companyAddr1 = jsonArray.getString("company_add1");
                        String zipcode = jsonArray.getString("zipcode");
                        String  companyPhone = jsonArray.getString("company_phone");
                        String companyEmail = jsonArray.getString("company_email");
                        String companyFax = jsonArray.getString("company_fax");
                        String comp_code = jsonArray.getString("comp_code");
                        String country = jsonArray.getString("country");

                        String city = jsonArray.getString("city");
                        String companyDName = jsonArray.getString("company_domain_name");
                        String countryCodePhone = jsonArray.getString("code_phone_no");
                        String empName = SharedPreferenceStore.getValue(getActivity(),"UserName","");
                        quoteDetailsModel.account = comp_code ;
                        quoteDetailsModel.empName  = empName ;
                        quoteDetailsModel.countryCodePhone = countryCodePhone ;
                        quoteDetailsModel.clientEmpPhone = companyPhone ;
                        quoteDetailsModel.clientCompName = companyName ;
                        quoteDetailsModel.clientEmpFax =  companyFax ;
                        quoteDetailsModel.clientEmpEmail = companyEmail ;
                        quoteDetailsModel.clientEmpAddres = companyAddr1 ;
                        quoteDetailsModel.ziocode =  zipcode ;
                        quoteDetailsModel.countryCode = country ;

                        quoteDetailsModel.cityCode = city ;

                        etAccountNumber.setText(comp_code);
                        etClientEmpName.setText(empName);
                        etClientCompany.setText(companyName);
                        etPhoneCountryCode.setText(countryCodePhone);
                        etPhone.setText(companyPhone);
                        etFax.setText(companyFax);
                        etEmail.setText(companyEmail);
                        etAddress.setText(companyAddr1);
                        etZipCode.setText(zipcode);
                        setCountry();

                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }

    private void initialiseSpinner() {

        listCountry= new ArrayList<>();
        CountryModel countryModel= new CountryModel();
        countryModel.id="0";
        countryModel.name="Country";
        listCountry.add(countryModel);

        AutoSearchAdapter adapter = new AutoSearchAdapter(getActivity(), listCountry);
        autocompleteTVCountry.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listCity= new ArrayList<>();
        CountryModel cityModel= new CountryModel();
        cityModel.id="0";
        cityModel.name="City";
        listCity.add(cityModel);
        AutoSearchAdapter adapter2 = new AutoSearchAdapter(getActivity(), listCity);
        autocompleteTVCity.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();

    }

    private void loadCountry() {
        String url =  Constants.BASE_URL + "country";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                //Utils.hideLoadingPopup();
                try {

                    listCountry.clear();
                    CountryModel countryModel;
                    int selectedIndex= -1 ;
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("country_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            countryModel = new CountryModel();
                            countryModel.id = jobject.getString("id");
                            countryModel.name = jobject.getString("name");
                            countryModel.sortname = jobject.getString("sortname");
                            if(quoteDetailsModel.countryCode!=null && quoteDetailsModel.countryCode.equals(jobject.getString("id")))
                            {
                                selectedIndex = i ;
                            }
                            listCountry.add(countryModel);
                        }

                        AutoSearchAdapter adapter = new AutoSearchAdapter(getActivity(), listCountry);
                        autocompleteTVCountry.setAdapter(adapter);
                        if(selectedIndex!=-1)
                        {
                            autocompleteTVCountry.setText(listCountry.get(selectedIndex).name);
                            loadCities(listCountry.get(selectedIndex).id);
                        }
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
    private void loadCities(String id) {
        String url =  Constants.BASE_URL + "city";
        HashMap<String,String> hmap= new HashMap<>();//{"country_id":"101"}
        hmap.put("country_id", id);
        HttpPostRequest.doPost(getActivity(), url,new Gson().toJson(hmap), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                //Utils.hideLoadingPopup();
                try {
                    listCity.clear();
                    CountryModel countryModel;

                    int selectedIndex= -1 ;
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("city_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            countryModel = new CountryModel();
                            countryModel.id = jobject.getString("id");
                            countryModel.name = jobject.getString("name");
                            //countryModel.sortname = jobject.getString("sortname");
                            if(quoteDetailsModel.cityCode!=null && quoteDetailsModel.cityCode.equals(jobject.getString("name")))
                            {
                                selectedIndex = i ;
                            }
                            else if(quoteDetailsModel.cityCode!=null && quoteDetailsModel.cityCode.equals(jobject.getString("id")))
                            {
                                selectedIndex = i ;
                            }
                            listCity.add(countryModel);
                        }

                        AutoSearchAdapter adapter = new AutoSearchAdapter(getActivity(), listCity);
                        autocompleteTVCity.setAdapter(adapter);
                        if(selectedIndex!=-1)
                        {
                            //autocompleteTVCity.setText(selectedIndex+1);
                            autocompleteTVCity.setText(listCity.get(selectedIndex).name);
                        }
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
    private void setupTopBar() {
        Toolbar quote_tool_bar =  (Toolbar) getActivity().findViewById(R.id.quote_tool_bar);
        mTitle =(TextView) quote_tool_bar.findViewById(R.id.txt_heading);

        TextView language = (TextView)quote_tool_bar.findViewById(R.id.navigationdot) ;
        ImageView btn_navigation = (ImageView)quote_tool_bar.findViewById(R.id.btn_navigation) ;
//        btn_navigation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().getSupportFragmentManager().popBackStack();
//            }
//        });
        ImageView quote_contact_detail_more_option = (ImageView)quote_tool_bar.findViewById(R.id.client_detail_more_option) ;
        quote_contact_detail_more_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ImageView quote_contact_detail_edit = (ImageView)quote_tool_bar.findViewById(R.id.client_detail_edit) ;
        quote_contact_detail_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //GUI Setting


        quote_contact_detail_edit.setVisibility(View.GONE);
        quote_contact_detail_more_option.setVisibility(View.GONE);
    }
}
