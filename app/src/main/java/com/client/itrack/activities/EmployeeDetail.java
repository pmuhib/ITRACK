package com.client.itrack.activities;

import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;

import com.client.itrack.utility.Utility;
import com.client.itrack.utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;


public class EmployeeDetail extends AppCompatActivity {

    AppGlobal appGlobal = AppGlobal.getInstance();
    Toolbar toolbar;
    ImageView btn_nav, employeeEditDetail;
    String companyid,companyType;
    TextView mTitle;

    String companyName, employeeId, fName, lName, email, phoneNo, clientCompId, designation, username,countryCodePhone;
    private static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employeedetail);
        ctx = this;
        setUpToolbar();
        Intent intent = getIntent();
        companyid = intent.getStringExtra("compEmpid");
        companyType = intent.getStringExtra("CompanyType");
        companyName = intent.getStringExtra("CompanyName");
       // ((TextView) findViewById(R.id.companyNamee)).setText(companyName);

        MyPhoneListener phoneListener = new MyPhoneListener();
        TelephonyManager telephonyManager =
                (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        // receive notifications of telephony state changes
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        ((TextView) findViewById(R.id.phoneno)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call();
            }
        });

        ((TextView) findViewById(R.id.clientemail)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendEMailToEmployee();

            }
        });

    }

    private void sendEMailToEmployee() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmployeeDetail.this);

        // Setting Dialog Title
                /*alertDialog.setTitle("Confirm Delete...");*/

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want send email ?");

        // Setting Icon to Dialog
                /*alertDialog.setIcon(R.drawable.delete);*/

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // Write your code here to invoke YES event
                //   Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{((TextView) findViewById(R.id.clientemail)).getText().toString().toLowerCase().trim()});
                i.putExtra(Intent.EXTRA_SUBJECT, "enter subject");
                i.putExtra(Intent.EXTRA_TEXT, "enter body");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(EmployeeDetail.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                //   Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void showClientDetail(String empId) {
        Utils.showLoadingPopup(EmployeeDetail.this);

        String url = Constants.BASE_URL + "employeeview";

        final HashMap<String, String> hm = new HashMap<>();
        hm.put("employee_id", empId);

        HttpPostRequest.doPost(EmployeeDetail.this, url, Utils.newGson().toJson(hm), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONObject jsonArray = jobj.getJSONObject("employee_details");

                        jsonArray.getString("id");
                        employeeId = jsonArray.getString("user_id").trim();
                        fName = jsonArray.getString("f_name").trim();
                        lName = jsonArray.getString("l_name").trim();
                        email = jsonArray.getString("email").trim();
                        phoneNo = jsonArray.getString("phone_no").trim();
                        countryCodePhone = jsonArray.getString("code_phone_no");
                        clientCompId = jsonArray.getString("company").trim();
                        designation = jsonArray.getString("designation").trim();
                        username = jsonArray.getString("user_name").trim();
                        mTitle.setText(fName+" "+lName.trim());
                        String emp_img  = jsonArray.getString("emp_img").trim();
                        jsonArray.getString("emp_img_thumb");
                        jsonArray.getString("type");
                        jsonArray.getString("create_date");


                       // ((TextView) findViewById(R.id.companyName)).setText(fName +" "+ lName);

                        ((TextView) findViewById(R.id.phoneno)).setText("+"+countryCodePhone+"-"+phoneNo);
                        ((TextView) findViewById(R.id.clientemail)).setText(email);
                        ((TextView) findViewById(R.id.clientfax)).setText(designation);
                        ((TextView) findViewById(R.id.clientCompany)).setText(companyName.trim());

                        Picasso.with(EmployeeDetail.this).load(Constants.IMAGE_BASE_URL+emp_img).placeholder(R.drawable.circledefault).error(R.drawable.circledefault).into(((ImageView) findViewById(R.id.companyLogo)));

                    } else {
                        Toast.makeText(EmployeeDetail.this, jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Utility.sendReport(EmployeeDetail.this,"employeeview",e.getMessage(),Utils.newGson().toJson(hm),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.txt_heading);
        mTitle.setText("");
        btn_nav = (ImageView) toolbar.findViewById(R.id.btn_navigation);
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        employeeEditDetail = (ImageView) toolbar.findViewById(R.id.client_detail_edit);
        employeeEditDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CategoryDetailContainer.class);
                intent.putExtra("catpos", -2);
                intent.putExtra("employeeId", employeeId);
                intent.putExtra("client_id", clientCompId);
                intent.putExtra("CompanyName", companyName);
                intent.putExtra("CompanyType", companyType);
                intent.putExtra("fName", fName);
                intent.putExtra("lName", lName);
                intent.putExtra("email", email);
                intent.putExtra("phoneNo", phoneNo);
                intent.putExtra("countryCodePhone", countryCodePhone);
                intent.putExtra("designation", designation);
                intent.putExtra("username", username);
                ImageView ivImageEmp = (ImageView) findViewById(R.id.companyLogo);


                ivImageEmp.setDrawingCacheEnabled(true);

                ivImageEmp.buildDrawingCache();

                Bitmap bm = ivImageEmp.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra("img",byteArray);
                startActivity(intent);

            }
        });
        ImageView employeeMoreOption = (ImageView) toolbar.findViewById(R.id.client_detail_more_option);
        employeeMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMenu(view);
            }
        });

        setSupportActionBar(toolbar);
    }

    // Show Right Side Menu Bar Option
    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.action_quotes: {
                        Intent intent = new Intent(getApplicationContext(), CategoryContainer.class);
                        intent.putExtra("catpos", 30);
                        intent.putExtra("CompanyId",clientCompId);
                        startActivity(intent);
                    }
                        break;

                    case R.id.action_dsr: {
                        Intent intent = new Intent(getApplicationContext(), CategoryContainer.class);
                        intent.putExtra("catpos", 20);
                        startActivity(intent);
                    }
                        break;

                    case R.id.action_view_employee:

                        break;

                    case R.id.action_email:
                        sendEMailToEmployee();
                        break;

                    case R.id.action_msg:
                        sendMessageToClient();
                        break;

                    case R.id.action_call:
                        call();
                        break;

                    case R.id.action_delete:

                        confirmEmployeeDelete();


                        break;
                    default:
                        break;


                }
                return false;
            }
        });
        popup.inflate(R.menu.employee_menu);
        popup.show();
    }

    private void sendMessageToClient() {

        Intent intent = new Intent(getApplicationContext(), CategoryContainer.class);
        intent.putExtra("catpos", 51 );
        intent.putExtra("senderId",appGlobal.userCompId) ;
        intent.putExtra("senderEmpId",appGlobal.userId) ;
        intent.putExtra("senderType", appGlobal.userType) ;
        intent.putExtra("receiverId",clientCompId);
        intent.putExtra("receiverType",Constants.CLIENT_EMP_TYPE);
        intent.putExtra("receiverEmpId",companyid);
        intent.putExtra("msgSubject","");
        startActivity(intent);
    }

    private void confirmEmployeeDelete() {
        AlertDialog.Builder  builder = new AlertDialog.Builder(EmployeeDetail.this);
        builder.setMessage(getString(R.string.confirm_delete));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Utils.isNetworkConnected(getApplicationContext()))
                {
                    deleteClientCompEmployee(employeeId);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show() ;
    }

    // Delete Employee From List of Client Company's Employees
    private void deleteClientCompEmployee(String empId) {


        Utils.showLoadingPopup(EmployeeDetail.this);

        String url = Constants.BASE_URL + "employeedelete";

        final HashMap<String, String> data = new HashMap<>();
        data.put("employee_id", empId);

        HttpPostRequest.doPost(EmployeeDetail.this, url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {

                        Toast.makeText(getApplicationContext(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }

                    finish();
                } catch (Exception e) {
                    Utility.sendReport(EmployeeDetail.this,"employeedelete",e.getMessage(),Utils.newGson().toJson(data),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });


    }

    // Make A call to Employee of Client Company
    private void call() {

        try {
            // set the data
            String uri = "tel:" + ((TextView) findViewById(R.id.phoneno)).getText().toString();
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                if (ContextCompat.checkSelfPermission(ctx,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) ctx,
                            Manifest.permission.CALL_PHONE) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) ctx,
                            Manifest.permission.READ_PHONE_STATE)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.


                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions((Activity) ctx,
                                new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
            }
            startActivity(callIntent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Your call has failed...",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private class MyPhoneListener extends PhoneStateListener {

        private boolean onCall = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // phone ringing...
                    Toast.makeText(EmployeeDetail.this, incomingNumber + " calls you",
                            Toast.LENGTH_LONG).show();
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // one call exists that is dialing, active, or on hold
                    Toast.makeText(EmployeeDetail.this, "on call...",
                            Toast.LENGTH_LONG).show();
                    //because user answers the incoming call
                    onCall = true;
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    // in initialization of the class and at the end of phone call

                    // detect flag from CALL_STATE_OFFHOOK
                    if (onCall == true) {
                        Toast.makeText(EmployeeDetail.this, "restart app after call",
                                Toast.LENGTH_LONG).show();

                        // restart our application
                        Intent restart = getBaseContext().getPackageManager().
                                getLaunchIntentForPackage(getBaseContext().getPackageName());
                        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(restart);

                        onCall = false;
                    }
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isNetworkConnected(this,false)) {
            showClientDetail(companyid);
        }
    }
}
