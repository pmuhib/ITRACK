package com.client.itrack.activities;

import android.Manifest;
import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.util.HashMap;

public class ClientDetail extends AppCompatActivity {

    Toolbar toolbar;
    ImageView btn_nav;

    // Company Info Variables
    String companyid;
    String companyName;
    String companyAddr1;
    String companyAddr2;
    String country;
    String city;
    String zipcode;
    String companyPhone;
    String countryCodePhone ;
    String companyEmail;
    String companyFax;
    String companyDName;
    String compCode = "";
    String companyLogo = "";
    String company_type = "-1";
    AppGlobal appGlobal = AppGlobal.getInstance();
    TextView mTitle;
    private static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientdetail);
        setUpToolbar();
        Intent intent = getIntent();
        //Get Comapny Intent Extra
        companyid = intent.getStringExtra("companyid");

        ((TextView) findViewById(R.id.btn_viewemployee)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewEmployeeList();
            }
        });

    }

    private void viewEmployeeList() {
        Intent intent = new Intent(getApplicationContext(), CategoryContainer.class);
        intent.putExtra("catpos", 100);
        intent.putExtra("CompanyName", companyName);
        intent.putExtra("CompanyLogo", companyLogo);
        intent.putExtra("CompanyType", company_type);
        intent.putExtra("CompanyId", companyid);
        startActivity(intent);
    }

    private void showClientDetail(String companyid) {

        Utils.showLoadingPopup(ClientDetail.this);

        String url = Constants.BASE_URL + "clientview";

        final HashMap<String, String> hm = new HashMap<>();
        hm.put("client_id", companyid);

        HttpPostRequest.doPost(ClientDetail.this, url, Utils.newGson().toJson(hm), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {
                    //DatabaseHandler databaseHandler = new DatabaseHandler(ClientDetail.this);
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {
                        JSONObject jsonArray = jobj.getJSONObject("client_details");
                        companyName = jsonArray.getString("company_name");
                        companyAddr1 = jsonArray.getString("company_add1");
                        companyAddr2 = jsonArray.getString("company_add2");
                        String[] addres;
                        if(!companyAddr2.trim().isEmpty())
                        {
                            addres  =  companyAddr1.split(companyAddr2);
                            companyAddr1 = addres[0].trim();
                        }

                        country = jsonArray.getString("country");
                        city = jsonArray.getString("city");
                        zipcode = jsonArray.getString("zipcode");
                        companyPhone = jsonArray.getString("company_phone");
                        countryCodePhone = jsonArray.getString("code_phone_no");
                        companyEmail = jsonArray.getString("company_email");
                        companyFax = jsonArray.getString("company_fax");
                        companyDName = jsonArray.getString("company_domain_name");
                        compCode = jsonArray.getString("comp_code");
                        mTitle.setText(companyName.trim()); // Company Name
                        companyLogo = Constants.IMAGE_BASE_URL + jsonArray.getString("logo_thumb"); // Company Logo
                        //Load Company Logo
                        Picasso.with(ClientDetail.this).load(companyLogo).placeholder(R.drawable.circledefault).error(R.drawable.circledefault).into(((ImageView) findViewById(R.id.companyLogo)));
                        company_type =  jsonArray.getString("company_type");
                        //Initialize  Company Details
                        ((TextView) findViewById(R.id.companyName)).setText(companyName);
                        ((TextView) findViewById(R.id.phoneno)).setText("+"+countryCodePhone+"-"+companyPhone);
                        ((TextView) findViewById(R.id.clientemail)).setText(companyEmail);
                        ((TextView) findViewById(R.id.clientfax)).setText(companyFax);
                        ((TextView) findViewById(R.id.companywebsite)).setText(companyDName);
                        ((TextView) findViewById(R.id.companyaddress)).setText(jsonArray.getString("company_add1")+"\n"+ jsonArray.getString("zipcode"));


                    } else {
                        Toast.makeText(ClientDetail.this, jobj.getString("msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                    Utility.sendReport(ClientDetail.this,"clientview","Success",Utils.newGson().toJson(hm),responseData);
                } catch (Exception e) {
                    Utility.sendReport(ClientDetail.this,"clientview",e.getMessage(),Utils.newGson().toJson(hm),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) ((Toolbar) findViewById(R.id.toolbar)).findViewById(R.id.txt_heading);
        mTitle.setText("");
        btn_nav = (ImageView) toolbar.findViewById(R.id.btn_navigation);

        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        // More option Button
        ImageView clientMoreOption = (ImageView) toolbar.findViewById(R.id.client_detail_more_option);
        clientMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMenu(view);

            }
        });

        // Edit Client Details
        ImageView clientDetailEdit = (ImageView) toolbar.findViewById(R.id.client_detail_edit);
        clientDetailEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(getApplicationContext(), CategoryDetailContainer.class);
                    intent.putExtra("catpos", -1);
                    intent.putExtra("companyAddr1", companyAddr1);
                    intent.putExtra("companyAddr2", companyAddr2);
                    intent.putExtra("companyDName", companyDName);
                    intent.putExtra("companyEmail", companyEmail);
                    intent.putExtra("companyFax", companyFax);
                    intent.putExtra("companyid", companyid);
                    intent.putExtra("CompanyType", company_type);
                    intent.putExtra("companyName", companyName);
                    intent.putExtra("zipcode", zipcode);
                    intent.putExtra("companyPhone", companyPhone);
                    intent.putExtra("countryCodePhone", countryCodePhone);
                    intent.putExtra("compCode", compCode);
                    intent.putExtra("country", country);
                    intent.putExtra("city", city);

                   intent.putExtra("logo", companyLogo);
                    startActivity(intent);
                }
               catch (Exception ex)
               {
                   ex.printStackTrace();
               }

            }
        });
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if (Utils.isNetworkConnected(this,false)) {
            showClientDetail(companyid);
        }

    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        
        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_quotes:
                        Intent intentQuote = new Intent(getApplicationContext(), CategoryContainer.class);
                        intentQuote.putExtra("catpos", 30);
                        intentQuote.putExtra("CompanyId",companyid);
                        startActivity(intentQuote);
                        break;
                    case R.id.action_dsr:
                        Intent intentDSR = new Intent(getApplicationContext(), CategoryContainer.class);
                        intentDSR.putExtra("catpos", 20);
                        intentDSR.putExtra("CompanyId", companyid); /*Client Id*/
                        startActivity(intentDSR);
                        break;
                    case R.id.action_view_employee: viewEmployeeList();break;
                    case R.id.action_email:sendEmailToClientCompany(); break;
                    case R.id.action_msg: /* This Feature Not-Allowed to Client only Employee */ break;
                    case R.id.action_call: call();break;
                    case R.id.action_delete: confirmEmployeeDelete();break;
                    default:break;
                }
                return false;
            }
        });
        popup.inflate(R.menu.client_menu);
        popup.show();
    }

    private void confirmEmployeeDelete() {
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_delete));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Utils.isNetworkConnected(getApplicationContext()))
                {
                    deleteClientComp(companyid);
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

    private void deleteClientComp(String client_id) {


        Utils.showLoadingPopup(ClientDetail.this);

        String url = Constants.BASE_URL + "clientdelete";

        HashMap<String, String> data = new HashMap<>();
        data.put("client_id", client_id);

        HttpPostRequest.doPost(ClientDetail.this, url, Utils.newGson().toJson(data), new HttpRequestCallback() {
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
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });


    }

    private void sendEmailToClientCompany() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClientDetail.this);

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
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{companyEmail.toLowerCase().trim()});
                i.putExtra(Intent.EXTRA_SUBJECT, "enter subject");
                i.putExtra(Intent.EXTRA_TEXT, "enter body");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ClientDetail.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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

    private void call() {

        try {
            // set the data
            String uri = "tel:" + ((TextView) findViewById(R.id.phoneno)).getText().toString();
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) this,
                            Manifest.permission.CALL_PHONE) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) this,
                            Manifest.permission.READ_PHONE_STATE)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.


                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions((Activity) this,
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
                    Toast.makeText(ClientDetail.this, incomingNumber + " calls you",
                            Toast.LENGTH_LONG).show();
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // one call exists that is dialing, active, or on hold
                    Toast.makeText(ClientDetail.this, "on call...",
                            Toast.LENGTH_LONG).show();
                    //because user answers the incoming call
                    onCall = true;
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    // in initialization of the class and at the end of phone call

                    // detect flag from CALL_STATE_OFFHOOK
                    if (onCall == true) {
                        Toast.makeText(ClientDetail.this, "restart app after call",
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


}
