package com.client.itrack.fragments.client;

import android.app.Activity;
import android.content.ContentValues;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.perm_handler.PermissionRequestHandler;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.SharedPreferenceStore;
import com.client.itrack.utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sony on 09-06-2016.
 */
public class EditUserProfileDetailFragment extends AppCompatActivity {

    View view  ;
    EditText etUserFName ,etUserLName ,etUserDesignation,etUserEmail ,etUserPhoneNumber,etUsername ,etCountryCode;
    TextView tvUploadEmployeeImg ,tvUpdateAdminEmployeeDetail,tvCancelUpdate ;
    ImageView imageAdminEmployee ;
    private static final int PICK_IMAGE = 101;
    private static final int PICK_Camera_IMAGE = 102;
    Uri imageUri;
    String uId ,uType ,base64String;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile_detail_frag);

        imageAdminEmployee = (ImageView)this.findViewById(R.id.imageAdminEmployee);
        etUserFName=(EditText) this.findViewById(R.id.etUserFName) ;
        etUserLName=(EditText) this.findViewById(R.id.etUserLName) ;
        etUserDesignation = (EditText)this.findViewById(R.id.etUserDesignation) ;
        etUserEmail = (EditText) this.findViewById(R.id.etUserEmail) ;
        etUserPhoneNumber = (EditText)this.findViewById(R.id.etUserPhoneNumber) ;
        etUsername= (EditText)this.findViewById(R.id.etUsername) ;
        etCountryCode = (EditText)this.findViewById(R.id.etCountryCode);

        tvUploadEmployeeImg =  (TextView)this.findViewById(R.id.tvUploadEmployeeImg);
        tvUploadEmployeeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(EditUserProfileDetailFragment.this);
                builder.setTitle("Select Image");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                       // boolean result = Utility.checkPermission(EditUserProfileDetailFragment.this);
                        if (items[item].equals("Take Photo")) {
                           // if (result) {
                           if(PermissionRequestHandler.requestPermissionToCamera(EditUserProfileDetailFragment.this,null))
                           {
                               camFunction();
                           }
                           // }

                        } else if (items[item].equals("Choose from Gallery")) {
                           // if (result)
                           // {
                            if(PermissionRequestHandler.requestPermissionToGallary(EditUserProfileDetailFragment.this,null))
                            {
                                gallaryFun();
                            }
                          //  }

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.create();
                builder.show();

            }
        });

        tvUpdateAdminEmployeeDetail =  (TextView) this.findViewById(R.id.tvUpdateAdminEmployeeDetail) ;
        tvUpdateAdminEmployeeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userId =  uId ;
                String userType =  uType ;
                String uFName =  etUserFName.getText().toString().trim();
                String uLName = etUserLName.getText().toString().trim() ;
                String uEmail= etUserEmail.getText().toString().trim() ;
                String uPhone =  etUserPhoneNumber.getText().toString().trim() ;
                uPhone = Long.parseLong(uPhone)+"";
                String uPhoneCode =  etCountryCode.getText().toString().trim();
                uPhoneCode = Long.parseLong(uPhoneCode)+"";
                String designation  = etUserDesignation.getText().toString().trim();

                Bitmap bitmap = ((BitmapDrawable) imageAdminEmployee.getDrawable()).getBitmap();
                base64String= Utils.BitMapToString(bitmap);
                if(uFName.isEmpty())
                {
                    return  ;
                }
                if(uLName.isEmpty())
                {
                    return  ;
                }
                if(uEmail.isEmpty())
                {
                    return  ;
                }
                if(uPhone.isEmpty())
                {
                    return  ;
                }
                if(uPhoneCode.isEmpty())
                {
                    return ;
                }


                updateAdminUserDetails(userId,userType ,uFName ,uLName ,uEmail ,uPhone,uPhoneCode,designation);

            }
        });

        tvCancelUpdate =  (TextView)this.findViewById(R.id.tvCancelUpdate) ;
        tvCancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bindDetailToGUI();

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
                    gallaryFun();
                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void updateAdminUserDetails(final String userId, final String userType, final String uFName, final String uLName, final String uEmail, final String uPhone, final String uPhoneCode, String designation) {

        String url = "";

        switch(userType)
        {
            case Constants.ADMIN_EMP_TYPE :
                url = Constants.BASE_URL+"updateglobal_employee";
                break ;

            case Constants.CLIENT_EMP_TYPE :
                url = Constants.BASE_URL+"employeeupdate_by_id" ;
                break ;
        }
        Utils.showLoadingPopup(EditUserProfileDetailFragment.this);

        HashMap<String, String> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("fname", uFName);
        data.put("lname", uLName);
        data.put("designation",designation);
        data.put("phone_no",uPhone);
        data.put("code_phone_no",uPhoneCode);
        data.put("file", base64String);


        HttpPostRequest.doPost(this, url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();

                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status=jObj.getBoolean("status");
                    if(status) {
                        SharedPreferenceStore.storeValue(getApplicationContext(), "Userid", userId);
                        SharedPreferenceStore.storeValue(getApplicationContext(), "UserName", uFName+" " + uLName);
                        SharedPreferenceStore.storeValue(getApplicationContext(), "PhoneNo", uPhone);
                        SharedPreferenceStore.storeValue(getApplicationContext(), "PhoneCode", uPhoneCode);
                        SharedPreferenceStore.storeValue(getApplicationContext(), "Email", uEmail);
                        SharedPreferenceStore.storeValue(getApplicationContext(), "Type",userType);
                        JSONObject jsonObjectImage;
                        String image;
                        switch(userType)
                        {
                            case "employee" :
                                jsonObjectImage = jObj.getJSONObject("global_image_name");
                                image =  jsonObjectImage.getString("image");
                                break ;
                           default :
                               jsonObjectImage = jObj.getJSONObject("image_name");
                               image =  jsonObjectImage.getString("emp_img");
                                break ;
                        }
                        SharedPreferenceStore.storeValue(getApplicationContext(), "img",image );
                        finish();
                    }
                    else{

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

    private void bindDetailToGUI() {
        try
        {
            Intent intent = getIntent() ;

            uId =   intent.getStringExtra("id") ;
            uType = intent.getStringExtra("uType") ;

            String uNameFull  =  intent.getStringExtra("uName") ;

            etUserDesignation.setText(intent.getStringExtra("uDesignation"));
            etUserEmail.setText(intent.getStringExtra("uEmail"));
            etUserPhoneNumber.setText(intent.getStringExtra("uPhone"));
            etCountryCode.setText(intent.getStringExtra("uPhoneCode"));
            etUsername.setText(intent.getStringExtra("uNameId"));
            String uImage = intent.getStringExtra("uImage");
            if(!uImage.isEmpty()) {
                Picasso.with(getApplicationContext()).load(Constants.IMAGE_BASE_URL_ADMIN +uImage).placeholder(R.drawable.circledefault).error(R.drawable.circledefault).into(imageAdminEmployee);
            }
            etUserFName.setText(uNameFull.split(" ")[0]);
            etUserLName.setText(uNameFull.split(" ")[1]);

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }


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
            Toast.makeText(getApplicationContext(),
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
        imageUri = getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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
                    Toast.makeText(getApplicationContext(), "Picture was not taken", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Picture was not taken", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Unknown path",
                            Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }

                decodeFile(filePath);


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Internal error",
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA };
        Cursor cursor = this.managedQuery(uri, projection, null, null, null);
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
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);

        imageAdminEmployee.setImageBitmap(bitmap);




        //	imgView.setImageBitmap(bitmap);

    }

}
