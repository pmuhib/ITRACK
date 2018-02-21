package com.client.itrack.fragments.dft;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.activities.DFTMainActivity;
import com.client.itrack.R;
import com.client.itrack.adapters.DFTPaymentTypesAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.DFTPaymentTypeModel;
import com.client.itrack.perm_handler.PermissionRequestHandler;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DocumentHandler;
import com.client.itrack.utility.Utility;
import com.client.itrack.utility.Utils;
import com.client.itrack.views.DFTOverviewItem;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sony on 15-09-2016.
 */
public class DFTOverviewFragment extends BaseDFTFragment {

    View view ;

    String base64Data ="";
    final static String OPENING_AMOUNT = "Opening Balance";
    final static String INCOME_TOTAL = "Cash Reciept";
    final static String OPERATION_EXPENSES = "Operation Cash Out" ;
    final static String OTHER_EXPENSES = "Other Cash Out";

    LinearLayout documentContainer ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.dft_overview_fragment,container,false) ;
        setupToolBar();

        showCountrySelectionPopup();
        return view;
    }

    private void showCountrySelectionPopup() {
        final DFTMainActivity activity = (DFTMainActivity)getActivity();
        if(activity.isCountrySelected)
        {
            return  ;
        }
        CharSequence countries[] = new CharSequence[] {"Kuwait", "UAE", "Oman", "Saudi Arabia"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Country");
        builder.setItems(countries, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i)
                {
                    case 0 :
                        currency_code = "KWD" ;
                        break ;
                    case 1 :
                        currency_code = "AED" ;
                        break ;
                    case 2:
                        currency_code = "OMR";
                        break ;

                    case 3:
                        currency_code = "SAR" ;
                        break ;
                }
                setDateToView();
                activity.currency_code = currency_code ;
                activity.isCountrySelected = true ;
            }
        });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        final DFTMainActivity activity = (DFTMainActivity)getActivity();
        from_date =  activity.from_date ;
        to_date = activity.to_date  ;
        currency_code =  activity.currency_code;// Retrieve from AppGlobal which set on user Login
        setDateToView();
    }

    @Override
    protected void setDateToView() {
        super.setDateToView();
        requestDFTOverview(from_date,to_date,currency_code);
    }



    private void requestDFTOverview(final String from ,final String to,final String currency_code) {

        String url = Constants.BASE_URL+"dft";
        Utils.showLoadingPopup(getActivity());
        final HashMap<String, String> data = new HashMap<>();
        data.put("from_date", from);
        data.put("to_date", to);
        data.put("currency_code", currency_code);
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();

                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status=jObj.getBoolean("status");
                    if(status) {

                        /***********************
                         *  Opening balance
                         *  ********************/

                        JSONObject  openingBalObj  = jObj.getJSONObject("openingamounttotal");
                        String openingAmount = openingBalObj.getString("openingamount");

                        /****************
                         *  INCOME TOTAL
                         * ***************/

                        JSONObject  incomeTotalObj  = jObj.getJSONObject("incometotal");
                        DFTOverviewItem dftIncomeTotal = new DFTOverviewItem(getActivity());
                        dftIncomeTotal.setDFTCategory(INCOME_TOTAL);
                        String incomeTotal = incomeTotalObj.getString("incometotal");
                        dftIncomeTotal.setDFTCategoryTotalAmount(currency_code+" "+ appGlobal.getCSNumberFormat(Double.parseDouble(incomeTotal)));
                        final TextView addIncomeTotalDetails =  dftIncomeTotal.getAddDetailsButton();
                        addIncomeTotalDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showAddIncomeTotalDetailPopup();
                            }
                        });

                        final TextView viewIncomeTotalDetails =  dftIncomeTotal.getViewDetailsButton();
                        viewIncomeTotalDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DFTIncomeTotalDetailsFragment dftIncomeTotalDetailsFragment =  new DFTIncomeTotalDetailsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("from_date",from);
                                bundle.putString("to_date",to);
                                bundle.putString("currency_code",currency_code);
                                bundle.putString("category",INCOME_TOTAL);
                                dftIncomeTotalDetailsFragment.setArguments(bundle);
                                goTODFTDetails(dftIncomeTotalDetailsFragment);
                            }
                        });

                        /***********************
                         *  OPERATIONAL EXPENSES
                         * **********************/

                        JSONObject  operationexpensestotal  = jObj.getJSONObject("operationexpensestotal");
                        DFTOverviewItem dftOperationExpenses = new DFTOverviewItem(getActivity());
                        dftOperationExpenses.setDFTCategory(OPERATION_EXPENSES);
                        String operationtotal = operationexpensestotal.getString("operationtotal");
                        dftOperationExpenses.setDFTCategoryTotalAmount(currency_code+" "+appGlobal.getCSNumberFormat(Double.parseDouble(operationtotal)));
                        final TextView addIOperationExpensesDetails =  dftOperationExpenses.getAddDetailsButton();

                        addIOperationExpensesDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showAddOperationExpenseDetailPopup();
                            }
                        });
                        final TextView viewOperationExpensesDetails =  dftOperationExpenses.getViewDetailsButton();
                        viewOperationExpensesDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DFTOperationExpensesDetailsFragment dftDetailsFragment =  new DFTOperationExpensesDetailsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("from_date",from);
                                bundle.putString("to_date",to);
                                bundle.putString("currency_code",currency_code);
                                bundle.putString("category",OPERATION_EXPENSES);
                                dftDetailsFragment.setArguments(bundle);
                                goTODFTDetails(dftDetailsFragment);
                            }
                        });

                        /******************
                         *  OTHER EXPENSES
                         * *****************/

                        JSONObject  otherexpensestotal  = jObj.getJSONObject("otherexpensestotal");
                        DFTOverviewItem dftOtherExpenses = new DFTOverviewItem(getActivity());
                        dftOtherExpenses.setDFTCategory(OTHER_EXPENSES);
                        String otherexpenses =  otherexpensestotal.getString("otherexpenses");
                        dftOtherExpenses.setDFTCategoryTotalAmount(currency_code+" "+appGlobal.getCSNumberFormat(Double.parseDouble(otherexpenses)));
                        final TextView addIOtherExpensesDetails =  dftOtherExpenses.getAddDetailsButton();

                        addIOtherExpensesDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showAddOtherExpenseDetailPopup();
                            }
                        });

                        final TextView viewOtherExpensesDetails =  dftOtherExpenses.getViewDetailsButton();
                        viewOtherExpensesDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DFTOtherExpensesDetailsFragment dftDetailsFragment =  new DFTOtherExpensesDetailsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("from_date",from);
                                bundle.putString("to_date",to);
                                bundle.putString("currency_code",currency_code);
                                bundle.putString("category",OTHER_EXPENSES);
                                dftDetailsFragment.setArguments(bundle);
                                goTODFTDetails(dftDetailsFragment);
                            }
                        });

                        final LinearLayout dftOverviewContainer = (LinearLayout) view.findViewById(R.id.dftOverviewContainer);
                        dftOverviewContainer.removeAllViews();

                        dftOverviewContainer.addView(dftIncomeTotal);
                        dftOverviewContainer.addView(dftOperationExpenses);
                        dftOverviewContainer.addView(dftOtherExpenses);

                        double balance  = Double.parseDouble(openingAmount) + Double.parseDouble(incomeTotal)- Double.parseDouble(operationtotal)-Double.parseDouble(otherexpenses) ;
                        final TextView tvDFTBalanceAmount = (TextView) view.findViewById(R.id.tvDFTBalanceAmount);
                        tvDFTBalanceAmount.setText(currency_code+" "+ appGlobal.getCSNumberFormat(balance));

                        final TextView tvDFTOpeningBalanceAmount = (TextView) view.findViewById(R.id.tvDFTOpeningBalanceAmount);
                        tvDFTOpeningBalanceAmount.setText(currency_code+" "+ appGlobal.getCSNumberFormat(Double.parseDouble(openingAmount)));

                        if(!appGlobal.currency_code.toLowerCase().trim().equals(currency_code.toLowerCase().trim()))
                        {
                            addIncomeTotalDetails.setVisibility(View.GONE);
                            addIOperationExpensesDetails.setVisibility(View.GONE);
                            addIOtherExpensesDetails.setVisibility(View.GONE);
                        }

                    }
                    else{
                        Toast.makeText(getActivity(), jObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                    Utility.sendReport(getActivity(),"dft",status.toString(),Utils.newGson().toJson(data),responseData);

                }
                catch (Exception e){
                    e.printStackTrace();

                    Utility.sendReport(getActivity(),"dft",e.getMessage(),Utils.newGson().toJson(data),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {

                Utils.hideLoadingPopup();
                Utility.sendReport(getActivity(),"dft","Error",Utils.newGson().toJson(data),errorMessage);

            }
        });
    }

    private void showAddIncomeTotalDetailPopup() {
        base64Data = "";
        documentContainer = null ;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater() ;

        View view =  inflater.inflate(R.layout.dft_add_incometotal_details,null);


        final EditText etDftPaymentTypeName = (EditText) view.findViewById(R.id.etDftPaymentTypeName) ;

        // Popup Title
        final Spinner spinnerDftPaymentType = (Spinner) view.findViewById(R.id.spinnerDftPaymentType) ;
        final ArrayList<DFTPaymentTypeModel> lisrDftPaymentTypes =  new ArrayList<>();
        lisrDftPaymentTypes.add(new DFTPaymentTypeModel("Select Mode of Payment","0"));
        lisrDftPaymentTypes.add(new DFTPaymentTypeModel("Cash","1"));
        lisrDftPaymentTypes.add(new DFTPaymentTypeModel("Bank","2"));
        lisrDftPaymentTypes.add(new DFTPaymentTypeModel("Others","3"));
        DFTPaymentTypesAdapter adapter = new DFTPaymentTypesAdapter(getActivity(),lisrDftPaymentTypes);
        spinnerDftPaymentType.setAdapter(adapter);

        spinnerDftPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i==3) etDftPaymentTypeName.setVisibility(View.VISIBLE);
                    else etDftPaymentTypeName.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Popup Title
        final TextView tvDFTCategory = (TextView) view.findViewById(R.id.tvDFTCategory) ;

        tvDFTCategory.setText(INCOME_TOTAL);

        // Payment Amount EditText
        final EditText etPaymentAmount = (EditText) view.findViewById(R.id.etPaymentAmount) ;

        documentContainer = (LinearLayout) view.findViewById(R.id.documentContainer) ;

        // Currency Code
        final EditText etPaymentCurrencyCode = (EditText) view.findViewById(R.id.etPaymentCurrencyCode) ;
        etPaymentCurrencyCode.setText(currency_code);

        // Add Document Button
        final TextView tvDftDocument = (TextView) view.findViewById(R.id.tvDftDocument);
        tvDftDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectDocumentOption();
            }
        });

        // Payment Comment EditText
        final EditText etDFTPaymentComment = (EditText) view.findViewById(R.id.etDFTPaymentComment);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        // Close Popup button
        final ImageView ivCloseLoginPopup = (ImageView) view.findViewById(R.id.ivCloseLoginPopup);
        ivCloseLoginPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //Submit add Payment details
        final TextView tvAddDftDetail = (TextView) view.findViewById(R.id.tvAddDftDetail);
        tvAddDftDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = spinnerDftPaymentType.getSelectedItemPosition() ;
                if(pos<=0)
                {
                    Toast.makeText(getActivity(), "Please select Mode of payment.", Toast.LENGTH_SHORT).show();
                    return  ;
                }
                String amount  =  etPaymentAmount.getText().toString().trim() ;
                if(amount.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please enter amount.", Toast.LENGTH_SHORT).show();
                    return  ;
                }

                String type = lisrDftPaymentTypes.get(pos).getPayTypeId() ;
                String name = etDftPaymentTypeName.getText().toString().trim();
                if(pos!=3 || name.isEmpty())   name  =  lisrDftPaymentTypes.get(pos).getPayType() ;

                String currencyCode  =  currency_code ;

                String comment  =  etDFTPaymentComment.getText().toString().trim() ;
                requestAddIncomeTotalDetails(name,type,amount,comment,currencyCode,dialog);
            }
        });

        dialog.show();

    }

    private void showAddOtherExpenseDetailPopup() {
        base64Data = "";
        documentContainer = null ;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater() ;

        View view =  inflater.inflate(R.layout.dft_add_otherexpenses_details,null);
        // Popup Title
        final TextView tvDFTCategory = (TextView) view.findViewById(R.id.tvDFTCategory) ;
        tvDFTCategory.setText(OTHER_EXPENSES);

        // Payment Amount EditText
        final EditText etPaymentTypeName = (EditText) view.findViewById(R.id.etPaymentTypeName) ;

        // Payment Amount EditText
        final EditText etPaymentAmount = (EditText) view.findViewById(R.id.etPaymentAmount) ;

        // Currency Code
        final EditText etPaymentCurrencyCode = (EditText) view.findViewById(R.id.etPaymentCurrencyCode) ;
        etPaymentCurrencyCode.setText(currency_code);

        documentContainer = (LinearLayout) view.findViewById(R.id.documentContainer) ;

        // Add Document Button
        final TextView tvDftDocument = (TextView) view.findViewById(R.id.tvDftDocument);
        tvDftDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { showSelectDocumentOption(); }
        });

        // Payment Comment EditText
        final EditText etDFTPaymentComment = (EditText) view.findViewById(R.id.etDFTPaymentComment);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        // Close Popup button
        final ImageView ivCloseLoginPopup = (ImageView) view.findViewById(R.id.ivCloseLoginPopup);
        ivCloseLoginPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { dialog.dismiss(); }
        });

        //Submit add Payment details
        final TextView tvAddDftDetail = (TextView) view.findViewById(R.id.tvAddDftDetail);
        tvAddDftDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name  =  etPaymentTypeName.getText().toString().trim() ;
                String currencyCode  =  currency_code ;
                String amount  =  etPaymentAmount.getText().toString().trim() ;
                String comment  =  etDFTPaymentComment.getText().toString().trim() ;

                if(name.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please enter name.", Toast.LENGTH_SHORT).show();
                    return  ;
                }

                if(amount.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please enter amount.", Toast.LENGTH_SHORT).show();
                    return  ;
                }

                requestAddOtherExpensesDetails(name ,amount,comment,currencyCode,dialog);
            }
        });

        dialog.show();

    }

    private void showAddOperationExpenseDetailPopup() {
        base64Data = "";
        documentContainer = null ;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater() ;

        View view =  inflater.inflate(R.layout.dft_add_operationexpenses_details,null);

        final Spinner spinnerDftPaymentType = (Spinner) view.findViewById(R.id.spinnerDftPaymentType) ;
        final ArrayList<DFTPaymentTypeModel> lisrDftPaymentTypes =  new ArrayList<>();
        lisrDftPaymentTypes.add(new DFTPaymentTypeModel("Select Mode of Payment","0"));
        lisrDftPaymentTypes.add(new DFTPaymentTypeModel("Advance","1"));
        lisrDftPaymentTypes.add(new DFTPaymentTypeModel("Balance","3"));
        lisrDftPaymentTypes.add(new DFTPaymentTypeModel("Border Charge","2"));
        lisrDftPaymentTypes.add(new DFTPaymentTypeModel("Detention Charge","4"));
        DFTPaymentTypesAdapter adapter = new DFTPaymentTypesAdapter(getActivity(),lisrDftPaymentTypes);
        spinnerDftPaymentType.setAdapter(adapter);

        // Popup Title
        final TextView tvDFTCategory = (TextView) view.findViewById(R.id.tvDFTCategory) ;
        tvDFTCategory.setText(OPERATION_EXPENSES);

        documentContainer = (LinearLayout) view.findViewById(R.id.documentContainer) ;

        // Add Document Button
        final TextView tvDftDocument = (TextView) view.findViewById(R.id.tvDftDocument);
        tvDftDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectDocumentOption();
            }
        });

        // Payment Comment EditText
        final EditText etDFTPaymentComment = (EditText) view.findViewById(R.id.etDFTPaymentComment);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        // Close Popup button
        final ImageView ivCloseLoginPopup = (ImageView) view.findViewById(R.id.ivCloseLoginPopup);
        ivCloseLoginPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //Submit add Payment details
        final TextView tvAddDftDetail = (TextView) view.findViewById(R.id.tvAddDftDetail);
        tvAddDftDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = spinnerDftPaymentType.getSelectedItemPosition() ;
                if(pos<=0)
                {
                    Toast.makeText(getActivity(), "Please select Mode of payment.", Toast.LENGTH_SHORT).show();
                    return  ;
                }

                String type = lisrDftPaymentTypes.get(pos).getPayTypeId() ;
                String comment  =  etDFTPaymentComment.getText().toString().trim() ;
                if(comment.isEmpty())
                {
                    Toast.makeText(getActivity(), "Please select comment.", Toast.LENGTH_SHORT).show();
                    return  ;
                }

                requestAddOperationExpensesDetails(type,comment,dialog);
            }
        });

        dialog.show();

    }

    private void goTODFTDetails(Fragment fragment) {
        FragmentManager fm =  getActivity().getSupportFragmentManager() ;
        FragmentTransaction fragmentTransaction = fm.beginTransaction() ;
        fragmentTransaction.replace(R.id.dftContentContainer,fragment).addToBackStack(null).commit();
    }

    private void requestAddIncomeTotalDetails(String name, String type, String amount, String comment, String currencyCode, final AlertDialog dialog) {

        String url = Constants.BASE_URL+"add_income";
        Utils.showLoadingPopup(getActivity());
        final HashMap<String, String> data = new HashMap<>();

        data.put("name",name);
        data.put("amount", amount);
        data.put("type", type);
        data.put("comment", comment);
        data.put("currency_code", currencyCode);
        data.put("document", base64Data);

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status=jObj.getBoolean("status");
                    if(status) {
                        Toast.makeText(getActivity(), jObj.getString("msg"), Toast.LENGTH_SHORT).show();
                        requestDFTOverview(from_date,to_date,currency_code);
                        dialog.dismiss();
                    }
                    else Toast.makeText(getActivity(), jObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    Utility.sendReport(getActivity(),"add_income","Success",Utils.newGson().toJson(data),responseData);
                }
                catch (Exception e){
                    Utility.sendReport(getActivity(),"add_income",e.getMessage(),Utils.newGson().toJson(data),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Utility.sendReport(getActivity(),"add_income","Error",Utils.newGson().toJson(data),errorMessage);
            }
        });
    }

    private void requestAddOperationExpensesDetails(String type, String comment, final AlertDialog dialog) {
        String url = Constants.BASE_URL+"add_operation_expenses";
        Utils.showLoadingPopup(getActivity());
        final HashMap<String, String> data = new HashMap<>();
        data.put("type",type);
        data.put("comment", comment);
        data.put("document", base64Data);
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status=jObj.getBoolean("status");
                    if(status) {
                        Toast.makeText(getActivity(), jObj.getString("msg"), Toast.LENGTH_SHORT).show();
                        requestDFTOverview(from_date,to_date,currency_code);
                        dialog.dismiss();
                    }
                    else Toast.makeText(getActivity(), jObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    Utility.sendReport(getActivity(),"add_operation_expenses","Success",Utils.newGson().toJson(data),responseData);
                }
                catch (Exception e){
                    Utility.sendReport(getActivity(),"add_operation_expenses",e.getMessage(),Utils.newGson().toJson(data),responseData);
                  //  e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Utils.hideLoadingPopup();
            }
        });
    }

    private void requestAddOtherExpensesDetails(String name, String amount, String comment, String currencyCode, final AlertDialog dialog) {
        String url = Constants.BASE_URL+"add_other_expenses";
        Utils.showLoadingPopup(getActivity());
        final HashMap<String, String> data = new HashMap<>();

        data.put("name",name);
        data.put("amount", amount);
        data.put("comment", comment);
        data.put("currency_code", currencyCode);
        data.put("document", base64Data);

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    Boolean status=jObj.getBoolean("status");
                    if(status) {
                        Toast.makeText(getActivity(), jObj.getString("msg"), Toast.LENGTH_SHORT).show();
                        requestDFTOverview(from_date,to_date,currency_code);
                        dialog.dismiss();
                    }
                    else Toast.makeText(getActivity(), jObj.getString("msg"), Toast.LENGTH_SHORT).show();
                    Utility.sendReport(getActivity(),"add_other_expenses","Success",Utils.newGson().toJson(data),responseData);
                }
                catch (Exception e){
                    Utility.sendReport(getActivity(),"add_other_expenses",e.getMessage(),Utils.newGson().toJson(data),responseData);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Utils.hideLoadingPopup();
            }
        });
    }



    /******
     *********************************************
     * Image Selection from galary or using camera
     *********************************************
     * **/


    private static final int PICK_IMAGE = 101;
    private static final int PICK_Camera_IMAGE = 102;
    Uri imageUri;

    private void showSelectDocumentOption() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if(PermissionRequestHandler.requestPermissionToCamera(getActivity(),DFTOverviewFragment.this)) camFunction();
                } else if (items[item].equals("Choose from Gallery")) {
                    if(PermissionRequestHandler.requestPermissionToGallary(getActivity(),DFTOverviewFragment.this)) gallaryFun();
                } else if (items[item].equals("Cancel")) dialog.dismiss();
            }
        });
        builder.show();
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

    private final int PERMISSIONS_REQUEST_CAMERA = 0 ;
    private final int PERMISSIONS_REQUEST_GALLARY = 1 ;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    camFunction();
                return;
            }
            case PERMISSIONS_REQUEST_GALLARY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) gallaryFun();
                return;
            }
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
            case PICK_Camera_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    //use imageUri here to access the image
                    selectedImageUri = imageUri;
                    // camScannerApiMethod(selectedImageUri);
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
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
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
        base64Data  =Utils.BitMapToString(bitmap);
        String document_name = String.valueOf(System.currentTimeMillis());
        addDocumentToGUI(base64Data,document_name+".png");
    }

    private void addDocumentToGUI(final String data, final String document_name) {

        TextView tvDoc = new TextView(getActivity());
        tvDoc.setText(document_name);
        tvDoc.setHint(document_name);
        tvDoc.setMaxLines(1);
        tvDoc.setEllipsize(TextUtils.TruncateAt.END);
        if (Build.VERSION.SDK_INT < 23) {
            tvDoc.setTextAppearance(getActivity(), R.style.GLEditTextTheme);
        } else{
            tvDoc.setTextAppearance(R.style.GLEditTextTheme);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        layoutParams.gravity = Gravity.CENTER_VERTICAL ;
        tvDoc.setLayoutParams(layoutParams);
        tvDoc.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.doc_file_icn_small, null),null, null, null);

        // Document Click Event
        tvDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentHandler documentHandler = new DocumentHandler(getActivity(),DFTOverviewFragment.this);
                documentHandler.showImage(data,document_name);
            }
        });
        documentContainer.removeAllViews();
        documentContainer.addView(tvDoc);
    }


}
