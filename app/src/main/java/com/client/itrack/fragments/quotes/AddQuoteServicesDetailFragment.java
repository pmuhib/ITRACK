package com.client.itrack.fragments.quotes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.adapters.AutoSearchAdapter;
import com.client.itrack.adapters.LocationPointAdapter;
import com.client.itrack.adapters.SpinnerAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.CountryModel;
import com.client.itrack.model.LocationPointModel;
import com.client.itrack.model.QuoteDetailsModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sony on 18-06-2016.
 */
public class AddQuoteServicesDetailFragment extends Fragment {

    View view  ;
    EditText etCommodity,etPieces ,etWeightCommodity,etOther , etQuoteMessage ,etAdditionalServices,etLoadingPoint,etDestinationPoint;
    TextView  mTitle ;
    RelativeLayout servicesOfInterestContainer ;
    LinearLayout destOtherOptionContainer ,originOtherOptionContainer ;
    AutoCompleteTextView spinnerDestCountryList,spinnerDestCityList,
            spinnerOriginCountryList,spinnerOriginCityList ;
    AppGlobal  appGlobal= AppGlobal.getInstance();
    QuoteDetailsModel quoteDetailsModel ;
    String action;//Spinner spinnerOriginList ,spinnerDestinationList
    ArrayList<LocationPointModel> listLoadingPointList ;
    ArrayList<LocationPointModel> listDestinationPointList;
    ArrayList<CountryModel> listCountry ;
    ArrayList<CountryModel> listState ,listStateD;
    ArrayList<CountryModel> listCity ,listCityD;
    boolean isOtherOrigin ,isOtherDestination ;

    AutoCompleteTextView autocompleteTOrigin,autocompleteTDestination ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.add_quote_services_details_fragment,container,false);
        setupTopBar();
        setupGUI();
        bindDataToGUI();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeSpinner();
    }



    private void initializeSpinner() {
        /******************************/

        listCountry= new ArrayList<>();

        AutoSearchAdapter adapterO = new AutoSearchAdapter(getActivity(), listCountry);
        spinnerOriginCountryList.setAdapter(adapterO);
        adapterO.notifyDataSetChanged();

        AutoSearchAdapter adapterD = new AutoSearchAdapter(getActivity(), listCountry);
        spinnerDestCountryList.setAdapter(adapterD);
        adapterD.notifyDataSetChanged();


        listCity= new ArrayList<>();
        listCityD= new ArrayList<>();

        AutoSearchAdapter adapter2 = new AutoSearchAdapter(getActivity(), listCity);
        spinnerOriginCityList.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();

        AutoSearchAdapter adapter2D = new AutoSearchAdapter(getActivity(), listCityD);
        spinnerDestCityList.setAdapter(adapter2D);
        adapter2D.notifyDataSetChanged();

        /********************************/
        listLoadingPointList = new ArrayList<>();
        listDestinationPointList = new ArrayList<>();

        LocationPointAdapter locationPointAdapter;

        locationPointAdapter = new LocationPointAdapter(getActivity(),listLoadingPointList);
        autocompleteTOrigin.setAdapter(locationPointAdapter);

        locationPointAdapter = new LocationPointAdapter(getActivity(),listDestinationPointList);
        autocompleteTDestination.setAdapter(locationPointAdapter);

        // if(appGlobal.listLoadingnPoint==null || appGlobal.listLoadingnPoint.size()<=0)
        loadLoadingPoints();
        // else  Set Loading Point
        //if(appGlobal.listDestinationPoint==null || appGlobal.listDestinationPoint.size()<=0)
        loadDestinationPoints();
        // else  Set Destination Point
        loadCountry();
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
                    int selectedIndexD= -1 ;
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
                            if(quoteDetailsModel.loadingCountry!=null && quoteDetailsModel.loadingCountry.equals(jobject.getString("name")))
                            {
                                selectedIndex = i ;
                            }
                            if(quoteDetailsModel.destinationCountry!=null && quoteDetailsModel.destinationCountry.equals(jobject.getString("name")))
                            {
                                selectedIndexD = i ;
                            }

                            listCountry.add(countryModel);
                        }

                        AutoSearchAdapter adapter = new AutoSearchAdapter(getActivity(), listCountry);
                        spinnerOriginCountryList.setAdapter(adapter);
                        if(selectedIndex!=-1)
                        {
                            CountryModel model =  listCountry.get(selectedIndex);
                            spinnerOriginCityList.setText(model.name);
                            loadOriginCities(model.id);
                        }
                        adapter.notifyDataSetChanged();

                        AutoSearchAdapter adapterD = new AutoSearchAdapter(getActivity(), listCountry);
                        spinnerDestCountryList.setAdapter(adapterD);
                        if(selectedIndexD!=-1)
                        {
                            CountryModel model =  listCountry.get(selectedIndexD);
                            spinnerDestCountryList.setText(model.name);
                            loadDestinationCities(model.id);
                        }
                        adapterD.notifyDataSetChanged();

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

    private void setupGUI() {
        servicesOfInterestContainer = (RelativeLayout) view.findViewById(R.id.containerSOI);

        int countSOI = servicesOfInterestContainer.getChildCount();

        // Set CheckBoxes Listener
        for (int indexService = 0; indexService < countSOI; indexService++) {
            CheckBox chkBoxService =  (CheckBox)servicesOfInterestContainer.getChildAt(indexService);
            chkBoxService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) selectServicesOfInterest(compoundButton.getText().toString());
                    else unselectServicesOfInterest(compoundButton.getText().toString());
                }
            });
        }


        autocompleteTOrigin = (AutoCompleteTextView) view.findViewById(R.id.autocompleteTOrigin);
        autocompleteTOrigin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LocationPointModel locationPointModel = (LocationPointModel) ((ListView)adapterView).getAdapter().getItem(i) ;
                setOriginPointTOGUI(locationPointModel);
            }
        });


        autocompleteTDestination = (AutoCompleteTextView) view.findViewById(R.id.autocompleteTDestination);
        autocompleteTDestination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LocationPointModel locationPointModel = (LocationPointModel) ((ListView)adapterView).getAdapter().getItem(i) ;
                setDestinationTOGUI(locationPointModel) ;
            }
        });
        etCommodity = (EditText) view.findViewById(R.id.etCommodity);
        etPieces = (EditText) view.findViewById(R.id.etPieces);
        etWeightCommodity = (EditText) view.findViewById(R.id.etWeightCommodity);
        etOther = (EditText) view.findViewById(R.id.etOther);
        etQuoteMessage = (EditText) view.findViewById(R.id.etQuoteMessage);
        etAdditionalServices = (EditText) view.findViewById(R.id.etAdditionalServices);
        TextView tvSubmitServiceDetails = (TextView) view.findViewById(R.id.tvSubmitServiceDetails);
        tvSubmitServiceDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validationQuoteContact())
                {
                    AddQuoteContactDetailFragment contactDetailFragment =  new AddQuoteContactDetailFragment();
                    Bundle bundle  = new Bundle();
                    bundle.putCharSequence("action",action);
                    bundle.putSerializable("details",quoteDetailsModel);
                    contactDetailFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction() ;
                    fragmentTransaction.replace(R.id.quote_detail_container,contactDetailFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit() ;
                }
            }
        });

        destOtherOptionContainer =   (LinearLayout)view.findViewById(R.id.destOtherOptionContainer);
        etDestinationPoint =(EditText) destOtherOptionContainer.findViewById(R.id.etLocationPoint);
        spinnerDestCountryList = (AutoCompleteTextView) destOtherOptionContainer.findViewById(R.id.autocompleteTVCountry);
        spinnerDestCountryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CountryModel countryModel = (CountryModel) ((ListView)adapterView).getAdapter().getItem(i) ;
                loadDestinationCities(countryModel.id);
                quoteDetailsModel.destinationCountry =  countryModel.name;
            }
        });

        spinnerDestCityList = (AutoCompleteTextView)destOtherOptionContainer.findViewById(R.id.autocompleteTVCity) ;
        spinnerDestCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CountryModel modelCity  = (CountryModel)adapterView.getAdapter().getItem(i) ;
                quoteDetailsModel.destinationCity  =  modelCity.name ;
            }
        });

        originOtherOptionContainer =  (LinearLayout)view.findViewById(R.id.originOtherOptionContainer);
        etLoadingPoint = (EditText) originOtherOptionContainer.findViewById(R.id.etLocationPoint);
        spinnerOriginCountryList =(AutoCompleteTextView)originOtherOptionContainer.findViewById(R.id.autocompleteTVCountry);
        spinnerOriginCountryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CountryModel countryModel = (CountryModel) ((ListView)adapterView).getAdapter().getItem(i) ;
                loadOriginCities(countryModel.id);
                quoteDetailsModel.loadingCountry =  countryModel.name ;
            }
        });

        spinnerOriginCityList = (AutoCompleteTextView)originOtherOptionContainer.findViewById(R.id.autocompleteTVCity) ;
        spinnerOriginCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CountryModel modelCity  = (CountryModel)adapterView.getAdapter().getItem(i) ;
                quoteDetailsModel.loadingCity  =  modelCity.name ;
            }
        });

    }

    private void setDestinationTOGUI(LocationPointModel locationPointModel) {
        isOtherDestination =  false  ;
        hideOtherFields("destination") ;

        if(!locationPointModel.locPoint.toLowerCase().equals("others")){
            quoteDetailsModel.destination = locationPointModel.locPoint ;
            quoteDetailsModel.destinationCity =  locationPointModel.city ;
            quoteDetailsModel.destinationState =  "" ;
            quoteDetailsModel.destinationCountry =  locationPointModel.country ;
        }
        else
        {
            isOtherDestination = true  ;
            etDestinationPoint.setText(quoteDetailsModel.destination);
            etDestinationPoint.setHint("Enter Destination");
            showOtherFields("destination");
        }
        autocompleteTDestination.setText(locationPointModel.locPoint);
    }

    private void setOriginPointTOGUI(LocationPointModel locationPointModel) {
        isOtherOrigin =  false  ;
        hideOtherFields("origin") ;
        if(!locationPointModel.locPoint.toLowerCase().equals("others")){
            quoteDetailsModel.loading_id =  locationPointModel.locId ;
            quoteDetailsModel.origin = locationPointModel.locPoint ;
            quoteDetailsModel.loadingCity =  locationPointModel.city ;
            quoteDetailsModel.loadingState = "" ;
            quoteDetailsModel.loadingCountry =  locationPointModel.country ;
        }
        else
        {

            isOtherOrigin =  true  ;
            quoteDetailsModel.loading_id =  locationPointModel.locId ;
            etLoadingPoint.setText(quoteDetailsModel.origin); // May Put Blank
            etLoadingPoint.setHint("Enter Origin");
            showOtherFields("origin");
        }
        autocompleteTOrigin.setText(locationPointModel.locPoint);
    }

    private void hideOtherFields(String location) {

        switch(location)
        {
            case "origin" :
                originOtherOptionContainer.setVisibility(View.GONE);
                break ;

            case "destination" :
                destOtherOptionContainer.setVisibility(View.GONE);
                break ;
        }

    }
    private void showOtherFields(String location) {
        switch(location)
        {
            case "origin" :
                originOtherOptionContainer.setVisibility(View.VISIBLE);
                break ;

            case "destination" :
                destOtherOptionContainer.setVisibility(View.VISIBLE);
                break ;
        }
    }


    private boolean validationQuoteContact() {

        if(services.size()<=0)  {
            Toast.makeText(getActivity(), "Select at least One Service!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else quoteDetailsModel.servicesOfInterests = strSOI();

        if(autocompleteTOrigin.getText().toString().isEmpty())
        {
            Toast.makeText(getActivity(), "Select Origin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            if(isOtherOrigin)
            {
                String loadingPoint = etLoadingPoint.getText().toString().trim();
                if(loadingPoint.isEmpty()){
                    Toast.makeText(getActivity(), "Please Enter Origin!", Toast.LENGTH_SHORT).show();
                    return false ;
                }else quoteDetailsModel.origin =  loadingPoint ;
                if(spinnerOriginCountryList.getText().toString().trim().isEmpty()){
                    Toast.makeText(getActivity(), "Please Select Loading Country!", Toast.LENGTH_SHORT).show();
                    return false ;
                }
                if(spinnerOriginCityList.getText().toString().trim().isEmpty()){
                    Toast.makeText(getActivity(), "Please Select Loading City!", Toast.LENGTH_SHORT).show();
                    return false ;
                } //else quoteDetailsModel.loadingCity = listCity.get(spinnerOriginCityList.getSelectedItemPosition()).name ;

            }
        }

        if(autocompleteTDestination.getText().toString().isEmpty())
        {
            Toast.makeText(getActivity(), "Select Destination!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            if(isOtherDestination)
            {
                String destinationPoint = etDestinationPoint.getText().toString().trim();
                if(destinationPoint.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please Enter Destination!", Toast.LENGTH_SHORT).show();
                    return false ;
                }else quoteDetailsModel.destination =  destinationPoint ;
                if(spinnerDestCountryList.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(getActivity(), "Please Select Destination Country!", Toast.LENGTH_SHORT).show();
                    return false ;
                }
                if(spinnerDestCityList.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(getActivity(), "Please Select Destination City!", Toast.LENGTH_SHORT).show();
                    return false ;
                } //else quoteDetailsModel.destinationCity = listCityD.get(spinnerDestCityList.getSelectedItemPosition()).name ;
            }
        }
        String commodity  = etCommodity.getText().toString().trim() ;
        quoteDetailsModel.commodity =  commodity;

        String weight  = etWeightCommodity.getText().toString().trim() ;
        if(!weight.isEmpty()) quoteDetailsModel.weight = weight ;
        else {
            Toast.makeText(getActivity(), "Enter Commodity Weight!!", Toast.LENGTH_SHORT).show();
            return false;
        }
        quoteDetailsModel.isOtherOrigin =  isOtherOrigin ;
        quoteDetailsModel.others =  etOther.getText().toString().trim() ;
        quoteDetailsModel.contactMessage = etQuoteMessage.getText().toString().trim() ;
        String addServices  =  etAdditionalServices.getText().toString().trim() ;
        quoteDetailsModel.addServices = addServices ;
        return true ;
    }

    private void bindDataToGUI() {
        Bundle bundle =  getArguments();
        action =  bundle.getString("action");
        quoteDetailsModel = (QuoteDetailsModel) bundle.getSerializable("details");
        setSelectedServicesOfInterest(quoteDetailsModel.servicesOfInterests);
        etCommodity.setText(quoteDetailsModel.commodity);
        etPieces.setText(quoteDetailsModel.pieces);
        etWeightCommodity.setText(quoteDetailsModel.weight);
        etOther.setText(quoteDetailsModel.others);
        etQuoteMessage.setText(quoteDetailsModel.contactMessage);
        etAdditionalServices.setText(quoteDetailsModel.addServices);

        switch(action)
        {
            case "edit":
                mTitle.setText(getResources().getText(R.string.edit_quote));
                break ;
            case "add" :
                mTitle.setText(getResources().getText(R.string.add_quote));
                break;
        }

    }
    ArrayList<String> services =  new ArrayList<>();
    private void setSelectedServicesOfInterest(String servicesOfInterests) {
        String[] selectedSOIs = servicesOfInterests.split(",");

        int countSOI = servicesOfInterestContainer.getChildCount();

        for (String selectedSOI:selectedSOIs) {
            selectedSOI = selectedSOI.toLowerCase().trim();
            for (int indexService = 0; indexService < countSOI; indexService++) {
                CheckBox chkBoxService =  (CheckBox)servicesOfInterestContainer.getChildAt(indexService);
                String txtSOI = chkBoxService.getText().toString().toLowerCase().trim();
                if(selectedSOI.equals(txtSOI)){
                    // services.add(txtSOI);
                    chkBoxService.setChecked(true);
                    break ;
                }
            }
        }
    }
    private void selectServicesOfInterest(String servicesOfInterests){
        services.add(servicesOfInterests);
        Log.d("Service Added ","\'"+servicesOfInterests+"\'");
    }
    private void unselectServicesOfInterest(String servicesOfInterests){
        services.remove(servicesOfInterests);
        Log.d("Service Removed ","\'"+servicesOfInterests+"\'");
    }
    private String strSOI() {
        String soiStr = "" ;
        for (String txtSOI:services) soiStr += txtSOI+"," ;
        if(!soiStr.isEmpty()) soiStr = soiStr.substring(0,soiStr.length()-1);
        return soiStr ;
    }

    private void loadLoadingPoints() {
        String url =  Constants.BASE_URL + "loding_point_list";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                //Utils.hideLoadingPopup();
                try {
                    int selectedIndex =  -1 ;
                    JSONObject jObj = new JSONObject(responseData);
                    listLoadingPointList.clear();
                    LocationPointModel locationPointModel;
                    Boolean status = jObj.getBoolean("status");
                    if (status) {
                        JSONArray  loadingPointJArr = jObj.getJSONArray("loding_point_id") ;

                        for (int indexLPoint = 0; indexLPoint < loadingPointJArr.length(); indexLPoint++) {
                            JSONObject loadingPointJObj = loadingPointJArr.getJSONObject(indexLPoint);
                            locationPointModel= new LocationPointModel();
                            String loadingPoint  =  loadingPointJObj.getString("loading_point");
                            locationPointModel.locPoint = loadingPoint;
                            if(loadingPoint.toLowerCase().trim().equals(quoteDetailsModel.origin.toLowerCase().trim()))
                            {
                                selectedIndex =  indexLPoint ;
                            }
                            locationPointModel.locId =  loadingPointJObj.getString("loading_id");
                            locationPointModel.city =  loadingPointJObj.getString("city");
                            locationPointModel.country =  loadingPointJObj.getString("country");
                            locationPointModel.created_date =  loadingPointJObj.getString("create_date");
                            listLoadingPointList.add(locationPointModel);
                        }
                        locationPointModel = new LocationPointModel();
                        locationPointModel.locPoint = "Others" ;
                        listLoadingPointList.add(locationPointModel);
                        appGlobal.listLoadingnPoint = listLoadingPointList;

                        LocationPointAdapter locationPointAdapter = new LocationPointAdapter(getActivity(),listLoadingPointList);
                        autocompleteTOrigin.setAdapter(locationPointAdapter);
                        LocationPointModel originModel ;
                        if(selectedIndex>=0) {
                            originModel =  listLoadingPointList.get(selectedIndex);
                            setOriginPointTOGUI(originModel);
                        }
                        else if(!quoteDetailsModel.origin.isEmpty())
                        {
                            originModel = listLoadingPointList.get(listLoadingPointList.size()-1);
                            setOriginPointTOGUI(originModel);
                        }
                    }
                }
                catch (Exception ex){ex.getMessage();}
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }
    private void loadDestinationPoints() {
        //Utils.showLoadingPopup(getActivity());

        String url =  Constants.BASE_URL + "destination_point_list";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                // Utils.hideLoadingPopup();
                try {
                    int selectedIndex= -1 ;
                    JSONObject jObj = new JSONObject(responseData);
                    listDestinationPointList.clear();
                    LocationPointModel locationPointModel;
                    Boolean status = jObj.getBoolean("status");
                    if (status) {
                        JSONArray  loadingPointJArr = jObj.getJSONArray("destination_point_id") ;

                        for (int indexLPoint = 0; indexLPoint < loadingPointJArr.length(); indexLPoint++) {
                            JSONObject loadingPointJObj = loadingPointJArr.getJSONObject(indexLPoint);
                            locationPointModel= new LocationPointModel();
                            String loadingPoint  =  loadingPointJObj.getString("destination_point");
                            locationPointModel.locPoint = loadingPoint;
                            if(loadingPoint.toLowerCase().trim().equals(quoteDetailsModel.destination.toLowerCase().trim()))
                            {
                                selectedIndex =  indexLPoint ;
                            }
                            locationPointModel.locId =  loadingPointJObj.getString("destination_id");
                            locationPointModel.city =  loadingPointJObj.getString("city");
                            locationPointModel.country =  loadingPointJObj.getString("country");
                            locationPointModel.created_date =  loadingPointJObj.getString("create_date");
                            listDestinationPointList.add(locationPointModel);
                        }
                        locationPointModel = new LocationPointModel();
                        locationPointModel.locPoint = "Others" ;
                        listDestinationPointList.add(locationPointModel);
                        appGlobal.listDestinationPoint = listDestinationPointList;
                        LocationPointAdapter locationPointAdapter = new LocationPointAdapter(getActivity(),listDestinationPointList);
                        autocompleteTDestination.setAdapter(locationPointAdapter);
                        LocationPointModel destinationModel ;
                        if(selectedIndex>=0 )
                        {
                            destinationModel = listDestinationPointList.get(selectedIndex);
                            setDestinationTOGUI(destinationModel);
                        }else if(!quoteDetailsModel.destination.isEmpty())
                        {
                            destinationModel = listDestinationPointList.get(listDestinationPointList.size()-1);
                            setDestinationTOGUI(destinationModel);
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.getMessage();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }
    private void setupTopBar() {

        Toolbar quote_tool_bar =  (Toolbar) getActivity().findViewById(R.id.quote_tool_bar);
        mTitle =(TextView) quote_tool_bar.findViewById(R.id.txt_heading);

        TextView language = (TextView) quote_tool_bar.findViewById(R.id.navigationdot) ;
        ImageView btn_navigation = (ImageView)quote_tool_bar.findViewById(R.id.btn_navigation) ;
        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        ImageView quote_services_detail_more_option = (ImageView)quote_tool_bar.findViewById(R.id.client_detail_more_option) ;
        quote_services_detail_more_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        ImageView quote_services_detail_edit = (ImageView)quote_tool_bar.findViewById(R.id.client_detail_edit) ;
        quote_services_detail_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //GUI Setting
        quote_services_detail_edit.setVisibility(View.GONE);
        quote_services_detail_more_option.setVisibility(View.GONE);

    }


    private void loadOriginCities(String id) {
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
                    countryModel = new CountryModel();
                    countryModel.id="0";
                    countryModel.name="Select City";
                    listCity.add(countryModel);
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
                            if(quoteDetailsModel.loadingCity!=null && quoteDetailsModel.loadingCity.equals(jobject.getString("name")))
                            {
                                selectedIndex = i ;
                            }

                            listCity.add(countryModel);
                        }

                        AutoSearchAdapter adapter = new AutoSearchAdapter(getActivity(), listCity);
                        spinnerOriginCityList.setAdapter(adapter);
                        if(selectedIndex!=-1)
                        {
                            CountryModel model  = listCity.get(selectedIndex);
                            spinnerOriginCityList.setText(model.name);
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
    private void loadDestinationCities(String id) {
        String url =  Constants.BASE_URL + "city";
        HashMap<String,String> hmap= new HashMap<>();//{"country_id":"101"}
        hmap.put("country_id", id);
        HttpPostRequest.doPost(getActivity(), url,new Gson().toJson(hmap), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                //Utils.hideLoadingPopup();
                try {
                    listCityD.clear();
                    CountryModel countryModel;
                    countryModel = new CountryModel();
                    countryModel.id="0";
                    countryModel.name="Select City";
                    listCityD.add(countryModel);
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
                            if(quoteDetailsModel.destinationCity!=null && quoteDetailsModel.destinationCity.equals(jobject.getString("name")))
                            {
                                selectedIndex = i ;
                            }

                            listCityD.add(countryModel);
                        }

                        AutoSearchAdapter adapter = new AutoSearchAdapter(getActivity(), listCityD);
                        spinnerDestCityList.setAdapter(adapter);
                        if(selectedIndex!=-1)
                        {
                            CountryModel model  = listCity.get(selectedIndex);
                            spinnerDestCityList.setText(model.name);
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

    @Override
    public void onResume() {
        super.onResume();
        switch(action)
        {
            case "edit" :
                ((TextView)getActivity().findViewById(R.id.txt_heading)).setText("Edit Quote");
                break ;

            case "add" :
                ((TextView)getActivity().findViewById(R.id.txt_heading)).setText("Add Quote");
                break ;
        }
    }
}
