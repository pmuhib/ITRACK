package com.client.itrack.fragments.SignUp;

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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;

import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.perm_handler.PermissionRequestHandler;
import com.client.itrack.utility.Constants;

import com.client.itrack.utility.Utils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sony on 07-03-2017.
 */

public class AddCompany extends Fragment implements View.OnClickListener {
    private static final int PICK_IMAGE = 1;
    private static final int PICK_Camera_IMAGE = 102;


    View view;
    ImageView imageCompany;
    TextView Companyname,Submit,Exit,txt_uploadcompimag;
    EditText firstName, lastName, designation, phone, email, username, password,etCountryCode;
    String name,mail,companyemail,companyType,client_id;


    byte[] img ;
    private Uri imageUri;
    String bitmapString;
    Bitmap bitmap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.signup_addcompany,null,false);

        Companyname= (TextView) view.findViewById(R.id.txt_compNamee);
        email= (EditText) view.findViewById(R.id.et_emailAddress);
        Submit= (TextView) view.findViewById(R.id.txt_submit);
        Exit= (TextView) view.findViewById(R.id.txt_exit);
        Submit.setOnClickListener(this);
        Exit.setOnClickListener(this);
        Bundle bund=getArguments();
        if(bund!=null)
        {
            name=bund.getString("company_name");
            mail=bund.getString("company_email");
            companyType = bund.getString("CompanyType");
            client_id =bund.getString("client_id");
            Companyname.setText(name);
        }
        companyemail=mail.substring(mail.indexOf("@"));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TextView)getActivity().findViewById(R.id.txt_heading)).setText("Add Company");
        ((ImageView)getActivity().findViewById(R.id.btn_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount()>0)
                {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        imageCompany=(ImageView)getView().findViewById(R.id.imageCompany);
        if(img!=null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            bitmapString = Base64.encodeToString(img, Base64.DEFAULT);
            imageCompany.setImageBitmap(bmp);
        }
        txt_uploadcompimag= (TextView) getView().findViewById(R.id.tvUploadCompanyImg);
        txt_uploadcompimag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        firstName= (EditText) getView().findViewById(R.id.et_firstname);
        lastName=(EditText) getView().findViewById(R.id.et_lastName);
        designation=(EditText) getView().findViewById(R.id.et_designation);
        phone=(EditText) getView().findViewById(R.id.et_phone);
        username=(EditText) getView().findViewById(R.id.et_username);
        password=(EditText) getView().findViewById(R.id.et_password);
        etCountryCode=(EditText) getView().findViewById(R.id.et_CountryCode);

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
                    if(PermissionRequestHandler.requestPermissionToCamera(getActivity(),AddCompany.this))
                    {
                        camFunction();
                    }
                    // }

                } else if (items[item].equals("Choose from Gallery")) {
                    // if (result)
                    // {
                    if(PermissionRequestHandler.requestPermissionToGallary(getActivity(),AddCompany.this))
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

        imageCompany.setImageBitmap(bitmap);

        bitmapString=Utils.BitMapToString(bitmap);


    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        
        String emailtext=email.getText().toString();
        String newCompanymail = "";
        if(!emailtext.isEmpty()) {
         newCompanymail = emailtext.substring(emailtext.indexOf("@"));
        }
        switch (id)
        {
            case R.id.txt_submit:


                if (Utils.isNetworkConnected(getActivity(),false))
                {
                    if (Utils.isValidEmail(email.getText().toString().trim()))
                    {
                        if (companyemail.equalsIgnoreCase(newCompanymail))
                        {

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
                            if (password.getText().toString().length() == 0) {
                                Toast.makeText(getActivity(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            registerAPI();
                            Log.d("companymail",companyemail);
                            Log.d("New mail",newCompanymail);
                        }
                        else {
                            Toast.makeText(getActivity(), "Seems you do not have Company Email id.Please enter Company Email id", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        Toast.makeText(getActivity(), "Please enter valid email", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Internet not connected", Toast.LENGTH_SHORT).show();
                }



                /*if (companyemail.equalsIgnoreCase(newCompanymail))
                {
                    Toast.makeText(getActivity(),"Great",Toast.LENGTH_LONG).show();
                    Log.d("companymail",companyemail);
                    Log.d("New mail",newCompanymail);
                }*/

                break;
            case R.id.txt_exit:
                getActivity().getFragmentManager().popBackStack();
                break;
        }
    }

    private void registerAPI() {
        Utils.showLoadingPopup(getActivity());
        String url = "";
        HashMap<String, String> data = new HashMap<>();
        url = Constants.WEB_BASE_URL + "addcompanyuser";
        data.put("password", password.getText().toString());
        data.put("comp_name", name);
        data.put("client_id", client_id+"");
        data.put("company_type",companyType);
        data.put("fname", firstName.getText().toString().trim());
        data.put("lname", lastName.getText().toString().trim());
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
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
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
}
