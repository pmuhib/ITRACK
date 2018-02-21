package com.client.itrack.fragments.SignUp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.activities.CategoryDetailContainer;
import com.client.itrack.adapters.AutoSearchAdapter;
import com.client.itrack.fragments.client.AddClientFragments;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.CountryModel;
import com.client.itrack.perm_handler.PermissionRequestHandler;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sony on 29-03-2017.
 */

public class SignUpAddClient extends Fragment
{  AutoCompleteTextView autocompleteTVCountry ;
    AutoCompleteTextView autocompleteTVCity ;
    EditText company,address1,address2,zipcode,phone,email,fax,domainName,etClientCode,etCountryCode;
    TextView btnCancel,btnNext,tvAddWithCompanyDomain,tvAddAsIndividual;
    ArrayList<CountryModel> countrylist,citylist;
    RelativeLayout addClientFormActionCont ;
    ScrollView addClientFormCont ;
    LinearLayout choiceContainer ;
    View view ;
    ImageView txt_upload;
    String bitmapString;
    Bitmap bitmap;
    String country_id ,city_id ;
    int countryitempos=0,cityitempos=0;

    int choiceCompanyType = -1 ;
    private static final int PICK_IMAGE = 101;
    private static final int PICK_Camera_IMAGE = 102;
    Uri imageUri;
    String city ,stateCode,countryCode,compCode,comp_id,addressStr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.addclientfragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupGUI();
        bindDataToGUI();
        setupGUISettings();

        ((TextView)getActivity().findViewById(R.id.txt_heading)).setText("Add New Company");
        ((ImageView)getActivity().findViewById(R.id.btn_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount()>0)
                {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        //    countrySpinner
        initializeSpinner();

    }

    private void setupGUISettings() {
        if(choiceCompanyType==-1) {
            addClientFormActionCont.setVisibility(View.GONE);
            addClientFormCont.setVisibility(View.GONE);
            choiceContainer.setVisibility(View.VISIBLE);
        }else
        {
            if(choiceCompanyType==0)
            {
                email.setVisibility(View.VISIBLE);
                domainName.setVisibility(View.VISIBLE);
                addClientFormActionCont.setVisibility(View.VISIBLE);
                addClientFormCont.setVisibility(View.VISIBLE);
                choiceContainer.setVisibility(View.GONE);
            }
            else
            {
                email.setVisibility(View.GONE);
                domainName.setVisibility(View.GONE);
                addClientFormActionCont.setVisibility(View.VISIBLE);
                addClientFormCont.setVisibility(View.VISIBLE);
                choiceContainer.setVisibility(View.GONE);
            }
        }
    }


    private void setupGUI() {

        addClientFormActionCont = (RelativeLayout)view.findViewById(R.id.addClientFormActionCont);
        addClientFormCont = (ScrollView)view.findViewById(R.id.addClientFormCont) ;
        choiceContainer = (LinearLayout) view.findViewById(R.id.choiceContainer);
        tvAddWithCompanyDomain= (TextView) view.findViewById(R.id.tvAddWithCompanyDomain);
        tvAddWithCompanyDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceCompanyType = 0 ;

                // Fields Shown
                email.setVisibility(View.VISIBLE);
                domainName.setVisibility(View.VISIBLE);

                choiceContainer.setVisibility(View.GONE);
                addClientFormActionCont.setVisibility(View.VISIBLE);
                addClientFormCont.setVisibility(View.VISIBLE);
            }
        });

        tvAddAsIndividual =  (TextView) view.findViewById(R.id.tvAddAsIndividual) ;
        tvAddAsIndividual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceCompanyType = 1 ;

                email.setVisibility(View.GONE);
                domainName.setVisibility(View.GONE);

                choiceContainer.setVisibility(View.GONE);
                addClientFormActionCont.setVisibility(View.VISIBLE);
                addClientFormCont.setVisibility(View.VISIBLE);
            }
        });

        autocompleteTVCountry=(AutoCompleteTextView) view.findViewById(R.id.autocompleteTVCountry);

        autocompleteTVCity=(AutoCompleteTextView) view.findViewById(R.id.autocompleteTVCity);
        company=(EditText)view.findViewById(R.id.etxt_companyName);
        company.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    String companyName  = company.getText().toString();
                    companyName = companyName.trim().replaceAll(" ","");
                    if(companyName.length()>2)
                    {
                        compCode  =  companyName.substring(0,3) ;
                        etClientCode.setText(compCode.toUpperCase());
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Company Name must contains at least 3 chars for client code");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                company.setText("");
                                etClientCode.setText("");
                            }
                        });
                        builder.create();
                        builder.show() ;

                    }
                }
            }
        });
        etClientCode=(EditText)view.findViewById(R.id.etClientCode);
        address1=(EditText)view.findViewById(R.id.etxt_firstAddress);
        address2=(EditText)view.findViewById(R.id.etxt_secondAddress);
        zipcode=(EditText)view.findViewById(R.id.etxt_zipCode);
        etCountryCode = (EditText) view.findViewById(R.id.etCountryCode);
        phone=(EditText)view.findViewById(R.id.etxt_phone);
        email=(EditText)view.findViewById(R.id.etxt_emailAddress);
        fax=(EditText)view.findViewById(R.id.etxt_fax);
        domainName=(EditText)view.findViewById(R.id.etxt_domain);
        //txt_upload=(RelativeLayout)view.findViewById(R.id.etxt_cmplogo1);
        txt_upload=(ImageView)view.findViewById(R.id.iconupload);
        btnNext=(TextView)view.findViewById(R.id.txt_next);
        btnCancel=(TextView)view.findViewById(R.id.txt_cancel);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(choiceCompanyType==0? Utils.isValidEmail(email.getText().toString().trim()):true){

                    if(company.getText().toString().trim().length() == 0){
                        Toast.makeText(getActivity(),"Please Enter Company Name",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(etClientCode.getText().toString().trim().length()<3)
                    {
                        Toast.makeText(getActivity(),"Please Enter Client Code (3 chars)!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(choiceCompanyType==0){
                        if(domainName.getText().toString().trim().length() == 0){
                            Toast.makeText(getActivity(),"Please Enter Company Websites",Toast.LENGTH_SHORT).show();
                            return;
                        }}
                    if(etCountryCode.getText().toString().trim().length() == 0){
                        Toast.makeText(getActivity(),"Please Enter Country Code",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(phone.getText().toString().trim().length() == 0){
                        Toast.makeText(getActivity(),"Please Enter Mobile Number",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(autocompleteTVCountry.getText().toString().trim().isEmpty()){
                        Toast.makeText(getActivity(),"Please Select Country",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if( autocompleteTVCity.getText().toString().trim().isEmpty()){
                        Toast.makeText(getActivity(),"Please Select City",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(choiceCompanyType==0?!Utils.isValidDomain(domainName.getText().toString().trim()):false)
                    {
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Please enter valid domain value \nSuch as : \nhttp://example.com \nhttp://www.example.com \nwww.example.com.");
                            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }) ;
                            builder.setTitle("Validation");
                            builder.create() ;

                            builder.show();
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }

                        return ;

                    }

                    String doBtn =  btnNext.getText().toString();
                    if(doBtn.equals("Next")) {
                        registerAPI();
                    }
                    else
                    {
                        // Update Client Details
                        updateClientDetails();
                    }
                }
                else{
                    Toast.makeText(getActivity(),"Please enter valid email",Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish();
            }
        });

        txt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Image");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        // boolean result = Utility.checkPermission(EditUserProfileDetailFragment.this);
                        if (items[item].equals("Take Photo")) {
                            // if (result) {
                            if(PermissionRequestHandler.requestPermissionToCamera(getActivity(),SignUpAddClient.this))
                            {
                                camFunction();
                            }
                            // }

                        } else if (items[item].equals("Choose from Gallery")) {
                            // if (result)
                            // {
                            if(PermissionRequestHandler.requestPermissionToGallary(getActivity(),SignUpAddClient.this))
                            {
                                gallaryFun();
                            }
                            //  }

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

            }
        });


    }

    private final int PERMISSIONS_REQUEST_CAMERA = 0 ;
    private final int PERMISSIONS_REQUEST_GALLARY = 1 ;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    camFunction();
                }
                return;
            }
            case PERMISSIONS_REQUEST_GALLARY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { gallaryFun();}
                return;
            }
        }
    }
    private void bindDataToGUI() {
        Bundle bundle = getArguments();
        if(bundle  !=null)
        {
            btnNext.setText("Update");
            addressStr =  bundle.getString("companyAddr1");
            addressStr =   addressStr.split(",")[0];
            address2.setText(bundle.getString("companyAddr2"));
            zipcode.setText(bundle.getString("zipcode"));
            domainName.setText(bundle.getString("companyDName"));
            email.setText(bundle.getString("companyEmail"));
            fax.setText(bundle.getString("companyFax"));
            comp_id = bundle.getString("companyid");
            company.setText(bundle.getString("companyName"));
            etCountryCode.setText(bundle.getString("countryCodePhone"));
            phone.setText(bundle.getString("companyPhone"));
            compCode = bundle.getString("compCode");
            etClientCode.setText(compCode);
            countryCode = bundle.getString("country");
            stateCode = bundle.getString("state");
            city = bundle.getString("city");
            choiceCompanyType =  Integer.parseInt(bundle.getString("CompanyType"));
            String logo =  bundle.getString("logo");
            if(!logo.isEmpty()) {
                Picasso.with(getActivity()).load(logo).placeholder(R.drawable.circledefault).error(R.drawable.circledefault).into(txt_upload);
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                Bitmap bitmap = ((BitmapDrawable) txt_upload.getDrawable()).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, bStream);
                byte[] bArray = bStream.toByteArray();
                bitmapString = Base64.encodeToString(bArray, Base64.DEFAULT);
            }

            email.setEnabled(false);
            email.setFocusable(false);
        }
        else
        {
            email.setEnabled(true);
            email.setFocusable(true);
        }

    }

    private void initializeSpinner() {

        countrylist= new ArrayList<>();
        citylist= new ArrayList<>();
        CountryModel cmodel1= new CountryModel();
        cmodel1.id="0";
        cmodel1.name="Please Select Country";
        countrylist.add(cmodel1);

        CountryModel cmodel3= new CountryModel();
        cmodel3.id="0";
        cmodel3.name="Please Select City";
        citylist.add(cmodel3);


        AutoSearchAdapter adapter = new AutoSearchAdapter(getActivity(), countrylist);
        autocompleteTVCountry.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        AutoSearchAdapter adapter2 = new AutoSearchAdapter(getActivity(), citylist);
        autocompleteTVCity.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();

        if(Utils.isNetworkConnected(getActivity(),false)) {
            getCountryList();
        }
        else{
            Toast.makeText(getActivity(), "Internet not connected", Toast.LENGTH_SHORT).show();
        }

    }

    private void getCountryList() {
        String url = Constants.BASE_URL+"country";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {


                countrylist.clear();
                try {
                    int selectedIndex= -1 ;
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("country_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            CountryModel cmodel = new CountryModel();
                            cmodel.id = jobject.getString("id");
                            cmodel.name = jobject.getString("name");
                            cmodel.sortname = jobject.getString("sortname");
                            if(countryCode!=null && countryCode.equals(jobject.getString("id")))
                            {
                                selectedIndex = i ;
                            }
                            countrylist.add(cmodel);
                        }

                        AutoSearchAdapter adapter = new AutoSearchAdapter(getActivity(), countrylist);
                        autocompleteTVCountry.setAdapter(adapter);
                        if(selectedIndex!=-1)
                        {
                            autocompleteTVCountry.setText(countrylist.get(selectedIndex).name);
                            country_id  =  countrylist.get(selectedIndex).id ;
                            getCityList(countrylist.get(selectedIndex).id);
                            if(addressStr!=null) {
                                addressStr = addressStr.replace(countrylist.get(selectedIndex).name, "");
                                address1.setText(addressStr);
                            }
                        }
                        adapter.notifyDataSetChanged();

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



        autocompleteTVCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CountryModel countryModel = (CountryModel) ((ListView)adapterView).getAdapter().getItem(i);
                getCityList(countryModel.id);
                countryitempos = i;
                country_id = countryModel.id ;

            }
        });
        autocompleteTVCountry.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboardForAllEditetxt();
                return false;
            }
        });
    }

    private void getCityList(String id) {

    /*    Utils.showLoadingPopup(getActivity());*/

        String url = Constants.BASE_URL+"city";
        HashMap<String,String> hmap= new HashMap<>();//{"country_id":"101"}
        hmap.put("country_id", id);

        String jsonString = new Gson().toJson(hmap);


        HttpPostRequest.doPost(getActivity(), url, jsonString, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                // Utils.hideLoadingPopup();
                citylist.clear();
                CountryModel cmodel3= new CountryModel();
                cmodel3.id="0";
                cmodel3.name="Please Select City";
                citylist.add(cmodel3);

                try {
                    int selectedIndex = -1 ;
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("city_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            CountryModel cmodel = new CountryModel();
                            cmodel.id = jobject.getString("id");
                            cmodel.name = jobject.getString("name");
                            if (city !=null && city.equals(cmodel.id)) {
                                selectedIndex = i;
                            }
                            citylist.add(cmodel);

                        }

                        AutoSearchAdapter adapter = new AutoSearchAdapter(getActivity(), citylist);
                        autocompleteTVCity.setAdapter(adapter);
                        if (selectedIndex != -1)
                        {
                            CountryModel model = citylist.get(selectedIndex) ;
                            autocompleteTVCity.setText(model.name);
                            city_id =  model.id ;
                        }
                        adapter.notifyDataSetChanged();

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

        autocompleteTVCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                CountryModel cityModel = (CountryModel) ((ListView)adapterView).getAdapter().getItem(i);
                city_id =  cityModel.id ;
            }
        });

        autocompleteTVCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboardForAllEditetxt();
                return false;
            }
        });
    }

    public void hideKeyboardForAllEditetxt(){

        InputMethodManager imm=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().findViewById(R.id.etxt_companyName).getWindowToken(), 0);
        imm.hideSoftInputFromWindow(getView().findViewById(R.id.etxt_domain).getWindowToken(), 0);
        imm.hideSoftInputFromWindow(getView().findViewById(R.id.etxt_phone).getWindowToken(), 0);
        imm.hideSoftInputFromWindow(getView().findViewById(R.id.etxt_emailAddress).getWindowToken(), 0);
        imm.hideSoftInputFromWindow(getView().findViewById(R.id.etxt_fax).getWindowToken(), 0);
        imm.hideSoftInputFromWindow(getView().findViewById(R.id.etxt_firstAddress).getWindowToken(), 0);
        imm.hideSoftInputFromWindow(getView().findViewById(R.id.etxt_zipCode).getWindowToken(), 0);

    }

    private  void gallaryFun(){

        try {

            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            Intent gintent = new Intent();
            gintent.setType("image/*");
            gintent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    i,
                    PICK_IMAGE);
        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
    }

    private void camFunction() {
        // TODO Auto-generated method stub

        // String fileName = "new-photo-name.jpg";

        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //create parameters for Intent with filename
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image captured by camera");
        //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //create new Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, PICK_Camera_IMAGE);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri = null;
        String filePath = null;
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImageUri = data.getData();
                }
                break;
            case PICK_Camera_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    //use imageUri here to access the image
                    selectedImageUri = imageUri;
                    //*Bitmap mPic = (Bitmap) data.getExtras().get("data");
                    //selectedImageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), mPic, getResources().getString(R.string.app_name), Long.toString(System.currentTimeMillis())));*//*
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Picture was not taken", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Picture was not taken", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        if(selectedImageUri != null){
            try {
                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                String selectedImagePath = getPath(selectedImageUri);

                if (selectedImagePath != null) {
                    filePath = selectedImagePath;
                } else if (filemanagerstring != null) {
                    filePath = filemanagerstring;
                } else {
                    Toast.makeText(getActivity(), "Unknown path",
                            Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }

                decodeFile(filePath);


            } catch (Exception e) {
                Toast.makeText(getActivity(), "Internal error",
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void decodeFile(String filePath) {
        //Bitmap bp=null;
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        ((ImageView)getView().findViewById(R.id.iconupload)).setImageBitmap(bitmap);

        bitmapString=Utils.BitMapToString(bitmap);


        //	imgView.setImageBitmap(bitmap);

    }

    /**
     * **************************************
     *  Add Client service Method
     * **************************************
     **/

    private void registerAPI() {

        String url = Constants.BASE_URL+"addclient";
        Utils.showLoadingPopup(getActivity());

        HashMap<String, String> data = new HashMap<>();
        data.put("comp_name", company.getText().toString().trim());
        data.put("client_code", etClientCode.getText().toString().trim());
        data.put("address1", address1.getText().toString().trim());
        data.put("address2", address2.getText().toString().trim());
        data.put("country",country_id);

        data.put("city",city_id );
        data.put("zipcode", zipcode.getText().toString().trim());
        data.put("company_type", choiceCompanyType+"");
        if(choiceCompanyType==0) {
            data.put("email", email.getText().toString().trim());
            data.put("domain_name", domainName.getText().toString());
        }
        String mobileNo = phone.getText().toString().trim();
        data.put("phone_no",Long.parseLong(mobileNo)+"" );
        String country_code  =  etCountryCode.getText().toString().trim() ;
        data.put("code_phone_no",Integer.parseInt(country_code)+"");
        data.put("fax", fax.getText().toString().trim());

        data.put("file", bitmapString);

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();

                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status=jObj.getBoolean("status");
                    if(status) {
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), CategoryDetailContainer.class);
                        intent.putExtra("catpos", 12);
                        int clientId = jObj.getInt("client_id");
                        intent.putExtra("CompanyName", company.getText().toString());
                        intent.putExtra("CompanyId", clientId+"");
                        intent.putExtra("CompanyEmail", email.getText().toString());
                        intent.putExtra("CompanyType", choiceCompanyType);
                        intent.putExtra("action", "add");
                        startActivity(intent);
                        Toast.makeText(getActivity(), "Company has been successfully added", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity(), jObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Utils.hideLoadingPopup();
            }
        });
    }

    /**
     * **************************************
     *  Update Client Details service Method
     * **************************************
     **/

    public void updateClientDetails(){


        Utils.showLoadingPopup(getActivity());
        String url = Constants.BASE_URL+"editclient";
        /** Request Params **/

        HashMap<String, String> data = new HashMap<>();

        data.put("client_id",comp_id);
        data.put("comp_name", company.getText().toString());
        data.put("client_code",etClientCode.getText().toString().trim().toUpperCase());
        data.put("address1", address1.getText().toString());
        data.put("address2", address2.getText().toString());
        data.put("country",country_id);

        data.put("city", city_id);
        data.put("zipcode", zipcode.getText().toString());
        data.put("company_type", choiceCompanyType+"");
        if(choiceCompanyType==0) {
            data.put("email", email.getText().toString());
            data.put("domain_name", domainName.getText().toString());
        }
        data.put("phone_no", phone.getText().toString());
        data.put("fax", fax.getText().toString());

        String country_code  =  etCountryCode.getText().toString().trim() ;
        data.put("code_phone_no",Integer.parseInt(country_code)+"");
        data.put("file", bitmapString);

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();

                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status=jObj.getBoolean("status");
                    String msg = jObj.getString("msg");
                    if(status) {
                        getActivity().finish();
                        Toast.makeText(getActivity(),msg, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity(),msg, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
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
