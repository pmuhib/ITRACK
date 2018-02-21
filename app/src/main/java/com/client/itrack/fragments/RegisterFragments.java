package com.client.itrack.fragments;

import android.app.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.adapters.SpinnerAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.CountryModel;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NITISH on 3/30/2016.
 */
public class RegisterFragments extends Fragment {

    Spinner countrySpinner,stateSpinner,citySpinner;
    EditText company,address1,address2,zipcode,phone,email,fax,domainName;
    TextView btnCancel,btnNext;
    ArrayList<CountryModel> countrylist,statelist,citylist;
    RelativeLayout txt_upload;
String bitmapString;
    Bitmap bitmap;
    private static final int PICK_IMAGE = 1;

    int countryitempos=0,stateitempos=0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.registerfragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TextView)getActivity().findViewById(R.id.txt_heading)).setText("Add Your Company");

        countrySpinner=(Spinner)getView().findViewById(R.id.country_spinner);
        stateSpinner=(Spinner)getView().findViewById(R.id.state_spinner);
        citySpinner=(Spinner)getView().findViewById(R.id.city_spinner);

        company=(EditText)getView().findViewById(R.id.etxt_companyName);
        address1=(EditText)getView().findViewById(R.id.etxt_firstAddress);
        address2=(EditText)getView().findViewById(R.id.etxt_secondAddress);
        zipcode=(EditText)getView().findViewById(R.id.etxt_zipCode);
        phone=(EditText)getView().findViewById(R.id.etxt_phone);
        email=(EditText)getView().findViewById(R.id.etxt_emailAddress);
        fax=(EditText)getView().findViewById(R.id.etxt_fax);
        domainName=(EditText)getView().findViewById(R.id.etxt_domain);
        txt_upload=(RelativeLayout)getView().findViewById(R.id.etxt_cmplogo);

        btnNext=(TextView)getView().findViewById(R.id.txt_next);
        btnCancel=(TextView)getView().findViewById(R.id.txt_cancel);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerAPI();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getFragmentManager().popBackStack();
            }
        });

//        countrySpinner.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
//        stateSpinner.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
//        citySpinner.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
        countrylist= new ArrayList<CountryModel>();
        statelist=new ArrayList<CountryModel>();
        citylist= new ArrayList<CountryModel>();

        if(Utils.isNetworkConnected(getActivity(),false)) {
            getCountryList();
        }

    //    countrySpinner

        txt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gallaryFun();
            }
        });

    }

    private void getCountryList() {
        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL+"country";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
               Utils.hideLoadingPopup();

                countrylist.clear();

                try {

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
                            countrylist.add(cmodel);

                        }

                        SpinnerAdapter adapter = new SpinnerAdapter(getActivity(), countrylist);

                        countrySpinner.setAdapter(adapter);
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



        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int initialposition=countrySpinner.getSelectedItemPosition();
                countrySpinner.setSelection(initialposition, false);
                    getStateList(countrylist.get(position).id);
                countryitempos=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getStateList(String id) {

        String url = Constants.BASE_URL+"state";
        HashMap<String,String> hmap= new HashMap<>();//{"country_id":"101"}
        hmap.put("country_id", id);

        String jsonString = new Gson().toJson(hmap);

//newGson().toJson(hmap)

        HttpPostRequest.doPost(getActivity(), url,jsonString, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                statelist.clear();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("state_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            CountryModel cmodel = new CountryModel();
                            cmodel.id = jobject.getString("id");
                            cmodel.name = jobject.getString("name");
                          //  cmodel.sortname = jobject.getString("sortname");
                            statelist.add(cmodel);

                        }

                        SpinnerAdapter adapter = new SpinnerAdapter(getActivity(), statelist);

                        stateSpinner.setAdapter(adapter);
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

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int initialposition=stateSpinner.getSelectedItemPosition();
                stateSpinner.setSelection(initialposition, false);
               getCityList(statelist.get(position).id);
                stateitempos=position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    private void getCityList(String id) {

        String url = Constants.BASE_URL+"city";
        HashMap<String,String> hmap= new HashMap<>();//{"country_id":"101"}
        hmap.put("state_id", id);

        String jsonString = new Gson().toJson(hmap);


        HttpPostRequest.doPost(getActivity(), url, jsonString, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                citylist.clear();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("city_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            CountryModel cmodel = new CountryModel();
                            cmodel.id = jobject.getString("id");
                            cmodel.name = jobject.getString("name");
                     //       cmodel.sortname = jobject.getString("sortname");
                            citylist.add(cmodel);

                        }

                        SpinnerAdapter adapter = new SpinnerAdapter(getActivity(), citylist);

                        citySpinner.setAdapter(adapter);
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

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int initialposition = citySpinner.getSelectedItemPosition();
                citySpinner.setSelection(initialposition, false);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri = null;
        String filePath = null;
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImageUri = data.getData();
                }
                break;
           /* case PICK_Camera_IMAGE:
                if (resultCode == RESULT_OK) {
                    //use imageUri here to access the image
                    selectedImageUri = imageUri;
		 		    	*//*Bitmap mPic = (Bitmap) data.getExtras().get("data");
						selectedImageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), mPic, getResources().getString(R.string.app_name), Long.toString(System.currentTimeMillis())));*//*
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                }
                break;*/
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


        bitmapString=Utils.BitMapToString(bitmap);


        //	imgView.setImageBitmap(bitmap);

    }


    private void registerAPI() {
        String url = Constants.BASE_URL+"addclient";
        Utils.showLoadingPopup(getActivity());
        HashMap<String, String> data = new HashMap<>();
        data.put("comp_name", company.getText().toString());
        data.put("client_code", "ABC");
        data.put("address1", address1.getText().toString());
        data.put("address2", address2.getText().toString());
        data.put("country", countrylist.get(countryitempos).id);
        data.put("state", statelist.get(stateitempos).id);
        data.put("city", citySpinner.getSelectedItem()+"");
        data.put("zipcode", zipcode.getText().toString());
        data.put("email", email.getText().toString());
        data.put("phone_no", phone.getText().toString());
        data.put("fax", fax.getText().toString());
        data.put("domain_name", domainName.getText().toString());
        data.put("file", bitmapString);

        //  data.put("otp", "122978");
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();
                /*Toast.makeText(getActivity(), responseData.toString(), Toast.LENGTH_SHORT).show();
                Log.e(Constants.LOG_TAG, responseData);*/
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status=jobj.getBoolean("status");
                    if(status) {
                        // Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
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
                Utils.hideLoadingPopup();
            }
        });
    }
}
