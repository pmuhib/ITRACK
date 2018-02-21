package com.client.itrack.fragments.client;


import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.adapters.CompanySelectorAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.ClientModel;
import com.client.itrack.perm_handler.PermissionRequestHandler;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class AddEmployeeFragments extends Fragment {

    private static final int PICK_IMAGE = 1;
    private static final int PICK_Camera_IMAGE = 102;

    EditText firstName, lastName, designation, phone, email, username, password,etCountryCode;
    TextView btnCancel, btnNext,tvUploadEmployeeImg;
    ImageView imageClientEmployee ;

    String bitmapString;
    Bitmap bitmap;

    private int client_id;

    String cmpname, cmpemail,companyType;
    private ArrayList<ClientModel> allclientlist = new ArrayList<>();
    private Spinner companySpinner;
    String empDesignation,phoneNo,empUsername,empEmail,employeeId,fName ,lName ,action,countryCodePhone;
    byte[] img ;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle  bundle = getArguments();
        if(bundle!=null)
        {
            cmpname =bundle.getString("CompanyName");
            companyType = bundle.getString("CompanyType");
            cmpemail = bundle.getString("CompanyEmail");
            client_id =bundle.getInt("client_id");
            empDesignation = bundle.getString("designation");
            phoneNo =  bundle.getString("phoneNo");
            countryCodePhone =  bundle.getString("countryCodePhone");
            empUsername = bundle.getString("username");
            empEmail =  bundle.getString("email");
            fName =  bundle.getString("fName");
            lName = bundle.getString("lName");
            employeeId = bundle.getString("employeeId");
            img = bundle.getByteArray("img");
            action = bundle.getString("action");
        }
        return inflater.inflate(R.layout.addemployeefragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EditText company = (EditText) getView().findViewById(R.id.etxt_companyName);
        if (client_id != 0) {
            company.setText(cmpname);
            company.setVisibility(View.VISIBLE);
            company.setFocusable(false);
        } else {
            company.setVisibility(View.INVISIBLE);
        }
        firstName = (EditText) getView().findViewById(R.id.etxt_firrstname);
        firstName.setText(fName!=null?fName:"");
        lastName = (EditText) getView().findViewById(R.id.etxt_lastName);
        lastName.setText(lName!=null?lName:"");
        // address2=(EditText)getView().findViewById(R.id.etxt_secondAddress);
        designation = (EditText) getView().findViewById(R.id.etxt_designation);
        designation.setText(empDesignation!=null?empDesignation:"");
        phone = (EditText) getView().findViewById(R.id.etxt_phone);

        phone.setText(phoneNo!=null?phoneNo:"");
        etCountryCode = (EditText) getView().findViewById(R.id.etCountryCode);
        etCountryCode.setText(countryCodePhone!=null?countryCodePhone.trim():"");
        email = (EditText) getView().findViewById(R.id.etxt_emailAddress);
        email.setText(empEmail!=null?empEmail:"");
        username = (EditText) getView().findViewById(R.id.etxt_username);
        username.setText(empUsername!=null?empUsername:"");
        password = (EditText) getView().findViewById(R.id.etxt_password);

        imageClientEmployee=(ImageView)getView().findViewById(R.id.imageClientEmployee);
        if(img!=null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            bitmapString = Base64.encodeToString(img, Base64.DEFAULT);
            imageClientEmployee.setImageBitmap(bmp);
        }
        tvUploadEmployeeImg=(TextView)getView().findViewById(R.id.tvUploadEmployeeImg);


        btnNext = (TextView) getView().findViewById(R.id.txt_next);
        switch(action)
        {
            case "edit" :
                btnNext.setText("Submit");
                password.setVisibility(View.GONE);
                username.setEnabled(false);
                username.setFocusable(false);
                email.setEnabled(false);
                email.setFocusable(false);
                break ;

            case "add" :
                btnNext.setText("Submit");
                password.setVisibility(View.VISIBLE);
                username.setEnabled(true);
                username.setFocusable(true);
                email.setEnabled(true);
                email.setFocusable(true);
                break ;
        }
        btnCancel = (TextView) getView().findViewById(R.id.txt_cancel);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.isNetworkConnected(getActivity(),false)) {
                    if (Utils.isValidEmail(email.getText().toString().trim()))
                    {

                        if (companySpinner.getSelectedItemPosition() == 0) {
                            Toast.makeText(getActivity(), "Please Select Company Name", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (firstName.getText().toString().length() == 0) {
                            Toast.makeText(getActivity(), "Please Enter First Name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (lastName.getText().toString().length() == 0) {
                            Toast.makeText(getActivity(), "Please Enter Last Name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(etCountryCode.getText().toString().trim().length() == 0){
                            Toast.makeText(getActivity(),"Please Enter Country Code",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (phone.getText().toString().length() == 0) {
                            Toast.makeText(getActivity(), "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (username.getText().toString().length() == 0) {
                            Toast.makeText(getActivity(), "Please Enter User Name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(action.equals("add")) {
                            if (password.getText().toString().length() == 0) {
                                Toast.makeText(getActivity(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        registerAPI();
                    } else {
                        Toast.makeText(getActivity(), "Please enter valid email", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Internet not connected", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if (cmpemail.length() > 1) {
                getFragmentManager().popBackStack();
                //} else {
                getActivity().finish();
                //}

            }
        });

        ((ImageView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).findViewById(R.id.btn_navigation)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if (cmpemail.length() > 1) {
                getFragmentManager().popBackStack();
                //  } else {
                getActivity().finish();
                //  }
                // getActivity().get
            }
        });

//  need to change
        companySpinner = (Spinner) getView().findViewById(R.id.company_spinner);
        if (client_id == 0) {
            ClientModel cmodel1 = new ClientModel();
            cmodel1.companyId = "0";
            cmodel1.companyName = "Please Select Company";
            allclientlist.add(cmodel1);

            companySpinner.setVisibility(View.VISIBLE);
            if (cmpname.length() > 0) {
                companySpinner.setEnabled(false);
                companySpinner.setClickable(false);
            }
            CompanySelectorAdapter adapter = new CompanySelectorAdapter(getActivity(), allclientlist);
            companySpinner.setVisibility(View.VISIBLE);
            companySpinner.setAdapter(adapter);
            adapter.notifyDataSetChanged();
// End to need change
            loadEventData();
        } else {
            companySpinner.setVisibility(View.INVISIBLE);
        }

        tvUploadEmployeeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DocumentHandler  documentHandler = new DocumentHandler(getActivity(),AddEmployeeFragments.this);
//                documentHandler.selectImage();
                selectImage();
            }
        });


    }


    public void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // boolean result = Utility.checkPermission(EditUserProfileDetailFragment.this);
                if (items[item].equals("Take Photo")) {
                    // if (result) {
                    if(PermissionRequestHandler.requestPermissionToCamera(getActivity(),AddEmployeeFragments.this))
                    {
                        camFunction();
                    }
                    // }

                } else if (items[item].equals("Choose from Gallery")) {
                    // if (result)
                    // {
                    if(PermissionRequestHandler.requestPermissionToGallary(getActivity(),AddEmployeeFragments.this))
                    {
                        galleryFun();
                    }
                    //  }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private final int PERMISSIONS_REQUEST_CAMERA = 0 ;
    private final int PERMISSIONS_REQUEST_GALLARY = 1 ;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    camFunction();
                } else {

                }
                return;
            }

            case PERMISSIONS_REQUEST_GALLARY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryFun();
                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private  void galleryFun(){

        try {

            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            Intent gintent = new Intent();
            gintent.setType("image/*");
            gintent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    i,
                    1);
        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
    }

    private void camFunction() {

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
        super.onActivityResult(requestCode,resultCode,data);
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
                    // Bitmap mPic = (Bitmap) data.getExtras().get("data");
                    // selectedImageUri = Uri.parse(MediaStore.Images.Media.insertImage(getC, mPic, getResources().getString(R.string.app_name), Long.toString(System.currentTimeMillis())));*//*
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

        imageClientEmployee.setImageBitmap(bitmap);

        bitmapString=Utils.BitMapToString(bitmap);


    }

    private void registerAPI() {
        Utils.showLoadingPopup(getActivity());
        String url = "";
        HashMap<String, String> data = new HashMap<>();
        switch (action)
        {
            case "add" :
                url = Constants.BASE_URL + "addcompanyuser";
                data.put("password", password.getText().toString());
                break ;

            case "edit" :

                url = Constants.BASE_URL + "employeeupdate";
                data.put("employee_id",employeeId) ;
                break;
        }
        if(client_id==0)
        {
            data.put("comp_name", companySpinner.getSelectedItem() + "");
            data.put("client_id", allclientlist.get(companySpinner.getSelectedItemPosition()).companyId + "");
            data.put("company_type",allclientlist.get(companySpinner.getSelectedItemPosition()).company_type);
        }
        else
        {
            data.put("comp_name", cmpname.trim());
            data.put("client_id", client_id+"");
            data.put("company_type",companyType);
        }
        data.put("fname", firstName.getText().toString().trim());
        data.put("lname", lastName.getText().toString().trim());
        // data.put("city", citySpinner.getSelectedItem()+"");
        data.put("designation", designation.getText().toString().trim());


        data.put("email", email.getText().toString().trim());
        String mobileNo = phone.getText().toString().trim();
        data.put("phone_no",Long.parseLong(mobileNo)+"" );
        String country_code  =  etCountryCode.getText().toString().trim() ;
        data.put("code_phone_no",Integer.parseInt(country_code)+"");
        data.put("user_name", username.getText().toString());
        data.put("file",bitmapString);
        //  data.put("otp", "122978");
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();

                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        switch(action)
                        {
                            case "add" :
                                Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                                break;

                            case "edit" :
                                Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                                break ;
                        }
                        // Toast.makeText(getActivity(), "Employee has been successfully added", Toast.LENGTH_SHORT).show();

                        getActivity().finish();

                    } else {
                        Toast.makeText(getActivity(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }

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

    private void loadEventData() {
        Utils.showLoadingPopup(getActivity());
        String url = Constants.BASE_URL + "clientlist";
        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();
                allclientlist.clear();
                try {
                    int selectedClient = 0 ;
                    ClientModel cmodel1 = new ClientModel();
                    cmodel1.companyId = "0";
                    cmodel1.companyName = "Please Select Company";
                    allclientlist.add(cmodel1);
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONArray jsonArray = jobj.getJSONArray("client_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            ClientModel cmodel = new ClientModel();

                            String compId  = jobject.getString("comp_id");
                            if(Integer.parseInt(compId)==client_id)
                            {
                                selectedClient=i;
                            }
                            cmodel.companyId =compId;
                            cmodel.companyCode = jobject.getString("comp_code");
                            cmodel.companyName = jobject.getString("company_name");
                            cmodel.email = jobject.getString("company_email");
                            cmodel.companyLogoName = jobject.getString("logo_thumb");
                            cmodel.phoneNumber = jobject.getString("company_phone");
                            cmodel.company_type = jobject.getString("company_type");
                            allclientlist.add(cmodel);
                        }

                        CompanySelectorAdapter adapter = new CompanySelectorAdapter(getActivity(), allclientlist);

                        companySpinner.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        // Check Client Id i have or not . Set default first
                        companySpinner.setSelection(selectedClient);

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
}
