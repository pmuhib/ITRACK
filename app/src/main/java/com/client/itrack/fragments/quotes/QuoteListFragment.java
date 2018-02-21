package com.client.itrack.fragments.quotes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.activities.QuoteDetails;
import com.client.itrack.R;
import com.client.itrack.adapters.QuoteListAdapter;
import com.client.itrack.adapters.QuoteStatusSpinnerAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.ClickListener;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.QuoteDetailsModel;
import com.client.itrack.model.QuoteModel;
import com.client.itrack.utility.AppGlobal;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.DividerItemDecoration;
import com.client.itrack.utility.RecyclerTouchListener;
import com.client.itrack.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by sony on 30-05-2016.
 */
public class QuoteListFragment extends Fragment {


    AppGlobal appGlobal =  AppGlobal.getInstance();

    private String client_id,userType ,userId;
    boolean isSearchPopupOpen,isFiltering,isSearching;

    View view  ;
    RecyclerView recyclerQuoteList ;
    LinearLayout quote_search_form,dateContainer;
    EditText etQuoteId,etClientCompName,etFromDate,etToDate;
    ImageView ivFromDate,ivToDate;
    Spinner spinnerListQuoteStatus ;
    ArrayList<QuoteModel> listQuotes ;
    ArrayList<String> listQuoteStatus ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.quote_list_fragment,container,false);

        Bundle  bundle = getArguments();
        client_id = bundle.getString("client_id");
        userType=appGlobal.userType ;
        userId = appGlobal.userId ;

        setupGUI();
        setUpToolBar();
        setupBottomBar();
        loadQuoteStatusList();
        return view;
    }

    private void loadQuoteStatusList() {
        listQuoteStatus = new ArrayList<>();
        listQuoteStatus.add("Select Status");
        listQuoteStatus.add("New");
        listQuoteStatus.add("Responded");
        QuoteStatusSpinnerAdapter adapter = new QuoteStatusSpinnerAdapter(getActivity(),listQuoteStatus);
        spinnerListQuoteStatus.setAdapter(adapter);
    }

    private void setupGUI() {
        recyclerQuoteList =(RecyclerView) view.findViewById(R.id.recyclerQuoteList);
        recyclerQuoteList.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerQuoteList, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                QuoteModel quoteModel = listQuotes.get(position);
                Intent intentQuote = new Intent(getActivity(), QuoteDetails.class);
                intentQuote.putExtra("action", "view");
                Bundle bundle = new Bundle();
                bundle.putCharSequence("quote_id",quoteModel.quoteId);
                intentQuote.putExtra("bundle",bundle);
                startActivity(intentQuote);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        /*******************************
         * Filter & Search GUI
         * ****************************/

        quote_search_form = (LinearLayout) view.findViewById(R.id.quote_search_form);
        quote_search_form.setVisibility(View.GONE);
        isSearchPopupOpen = false;
        /**  Quote Form Fields  **/
        etQuoteId = (EditText) quote_search_form.findViewById(R.id.etQuoteId);
        etClientCompName = (EditText) quote_search_form.findViewById(R.id.etClientCompName);
        etFromDate = (EditText) quote_search_form.findViewById(R.id.etFromDate);
        etToDate = (EditText) quote_search_form.findViewById(R.id.etToDate);
        ivFromDate = (ImageView) quote_search_form.findViewById(R.id.ivFromDate);
        ivToDate = (ImageView) quote_search_form.findViewById(R.id.ivToDate);
        dateContainer = (LinearLayout) quote_search_form.findViewById(R.id.dateContainer);
        spinnerListQuoteStatus = (Spinner)quote_search_form.findViewById(R.id.spinnerListQuoteStatus);
        spinnerListQuoteStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0)
                {
                    if(isFiltering)
                    {
                       if(Utils.isNetworkConnected(getActivity(),false))
                       {
                           if(client_id.isEmpty())
                               loadQuoteListByStatus( listQuoteStatus.get(i));
                           else
                               loadQuoteClientListByStatus(listQuoteStatus.get(i));

                       }


                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        etFromDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    showFromDateDialog();
                }
            }
        });

        etToDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    showFromDateDialog();
                }
            }
        });

        // Select From Date
        ivFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFromDateDialog();
            }
        });


        // Select To Date
        ivToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToDateDialog();
            }
        });
        TextView tvQuoteSearch = (TextView) quote_search_form.findViewById(R.id.tvQuoteSearch);
        tvQuoteSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quoteNumber = etQuoteId.getText().toString().trim();
                String clientComp = etClientCompName.getText().toString().trim();
                String fromDate = etFromDate.getText().toString().trim();
                String toDate = etToDate.getText().toString().trim();

                String status = listQuoteStatus.get(spinnerListQuoteStatus.getSelectedItemPosition());
//
//                if (statusModel != null) {
//                    int dsrStatusId = Integer.parseInt(statusModel.dsrStatusID);
//                    if (dsrStatusId != 0) {
//                        status = statusModel.dsrStatusID;
//                    }
//                }


                if (!quoteNumber.isEmpty() || !clientComp.isEmpty() || (!fromDate.isEmpty() && !toDate.isEmpty()) || spinnerListQuoteStatus.getSelectedItemPosition()>0) {
                    if (Utils.isNetworkConnected(getActivity(),false)) {
                        if(client_id.isEmpty())
                            loadQuoteListFiltered(quoteNumber, clientComp, fromDate, toDate, status);
                        else
                            loadQuoteClientListFiltered(client_id ,quoteNumber, clientComp, fromDate, toDate, status);

                    } else {
                        Toast.makeText(getActivity(), "No Internet Connectivity!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please provide at least one filter value.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadQuoteClientListByStatus(String status) {

        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL + "quotefliter_by_clientid";

        HashMap<String, String> data = new HashMap<>();
        data.put("client_id",client_id);
        data.put("status",status);
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {

                        listQuotes.clear();
                        JSONArray quoteListArr =   jobj.getJSONArray("msg") ;
                        for (int i = 0; i < quoteListArr.length(); i++) {
                            JSONObject quoteJsonObject  =   quoteListArr.getJSONObject(i);
                            QuoteModel  quoteModel = new QuoteModel();
                            String quote_id  =quoteJsonObject.getString("id");
                            String client_name =  quoteJsonObject.getString("company");

                            String date = quoteJsonObject.getString("create_date");
                            String quote_code = quoteJsonObject.getString("quote_code");

                            quoteModel.quoteId =  quote_id!=null?quote_id.trim():"" ;
                            client_id = client_id!=null ?client_id.trim():"";
                            quoteModel.clientCompId = client_id ;
                            client_name =  client_name!=null? client_name.trim():"" ;
                            quoteModel.clientCompName = client_name ;
                            quoteModel.quoteCreatedDate = date!=null? date.trim():"" ;
                            quoteModel.quoteCode = quote_code!=null? quote_code.trim():"" ;
                            listQuotes.add(quoteModel);
                        }

                        QuoteListAdapter messageListAdapter = new QuoteListAdapter(getActivity(),listQuotes) ;
                        recyclerQuoteList.setAdapter(messageListAdapter);
                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerQuoteList.setLayoutManager(new LinearLayoutManager(getActivity()));

                        recyclerQuoteList.setItemAnimator(new DefaultItemAnimator());
                        recyclerQuoteList.addItemDecoration(dividerItemDecoration);
                        hideKeyboard();

                    } else {
                        hideKeyboard();
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

    private void loadQuoteClientListFiltered(String client_id, String quoteNumber, String clientComp, String fromDate, String toDate, String status) {

        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL + "quotesearch_by_clientid";

        HashMap<String, String> data = new HashMap<>();
        data.put("client_id",client_id);
        if (!quoteNumber.isEmpty()) {
            data.put("quote_code", quoteNumber);
        }
        if (!clientComp.isEmpty()) {
            data.put("compnay_name", clientComp);
        }
        if (!fromDate.isEmpty() && !toDate.isEmpty()) {
            data.put("from_date", fromDate);
            data.put("to_date", toDate);
        }
        if (spinnerListQuoteStatus.getSelectedItemPosition()>0) {
            data.put("status", status);
        }

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {

                        listQuotes.clear();
                        JSONArray quoteListArr =   jobj.getJSONArray("client_details") ;
                        for (int i = 0; i < quoteListArr.length(); i++) {
                            JSONObject quoteJsonObject  =   quoteListArr.getJSONObject(i);
                            QuoteModel  quoteModel = new QuoteModel();
                            String quote_id  =quoteJsonObject.getString("id");
                            String client_name =  quoteJsonObject.getString("company");
                            String client_id  =quoteJsonObject.getString("client_id");
                            String date = quoteJsonObject.getString("create_date");
                            String quote_code = quoteJsonObject.getString("quote_code");

                            quoteModel.quoteId =  quote_id!=null?quote_id.trim():"" ;
                            client_id = client_id!=null ?client_id.trim():"";
                            quoteModel.clientCompId = client_id ;
                            client_name =  client_name!=null? client_name.trim():"" ;
                            quoteModel.clientCompName = client_name ;
                            quoteModel.quoteCreatedDate = date!=null? date.trim():"" ;
                            quoteModel.quoteCode = quote_code!=null? quote_code.trim():"" ;
                            listQuotes.add(quoteModel);
                        }

                        QuoteListAdapter messageListAdapter = new QuoteListAdapter(getActivity(),listQuotes) ;
                        recyclerQuoteList.setAdapter(messageListAdapter);
                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerQuoteList.setLayoutManager(new LinearLayoutManager(getActivity()));

                        recyclerQuoteList.setItemAnimator(new DefaultItemAnimator());
                        recyclerQuoteList.addItemDecoration(dividerItemDecoration);
                        hideKeyboard();

                    } else {
                        hideKeyboard();
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

    private void loadQuoteListByStatus(String status) {

        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL + "quotefliter";

        HashMap<String, String> data = new HashMap<>();
        data.put("status",status);
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {

                        listQuotes.clear();
                        JSONArray quoteListArr =   jobj.getJSONArray("msg") ;
                        for (int i = 0; i < quoteListArr.length(); i++) {
                            JSONObject quoteJsonObject  =   quoteListArr.getJSONObject(i);
                            QuoteModel  quoteModel = new QuoteModel();
                            String quote_id  =quoteJsonObject.getString("id");
                            String client_name =  quoteJsonObject.getString("company");

                            String date = quoteJsonObject.getString("create_date");
                            String quote_code = quoteJsonObject.getString("quote_code");

                            quoteModel.quoteId =  quote_id!=null?quote_id.trim():"" ;
                            client_id = client_id!=null ?client_id.trim():"";
                            quoteModel.clientCompId = client_id ;
                            client_name =  client_name!=null? client_name.trim():"" ;
                            quoteModel.clientCompName = client_name ;
                            quoteModel.quoteCreatedDate = date!=null? date.trim():"" ;
                            quoteModel.quoteCode = quote_code!=null? quote_code.trim():"" ;
                            listQuotes.add(quoteModel);
                        }

                        QuoteListAdapter messageListAdapter = new QuoteListAdapter(getActivity(),listQuotes) ;
                        recyclerQuoteList.setAdapter(messageListAdapter);
                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerQuoteList.setLayoutManager(new LinearLayoutManager(getActivity()));

                        recyclerQuoteList.setItemAnimator(new DefaultItemAnimator());
                        recyclerQuoteList.addItemDecoration(dividerItemDecoration);
                        hideKeyboard();

                    } else {
                        hideKeyboard();
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

    private void loadQuoteListFiltered(String quoteNumber, String clientComp, String fromDate, String toDate, String status) {

        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL + "quotesearch";

        HashMap<String, String> data = new HashMap<>();
        if (!quoteNumber.isEmpty()) {
            data.put("quote_code", quoteNumber);
        }
        if (!clientComp.isEmpty()) {
            data.put("compnay_name", clientComp);
        }
        if (!fromDate.isEmpty() && !toDate.isEmpty()) {
            data.put("from_date", fromDate);
            data.put("to_date", toDate);
        }
        if (spinnerListQuoteStatus.getSelectedItemPosition()>0) {
            data.put("status", status);
        }

        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();

                try {

                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    if (status) {

                        listQuotes.clear();
                        JSONArray quoteListArr =   jobj.getJSONArray("client_details") ;
                        for (int i = 0; i < quoteListArr.length(); i++) {
                            JSONObject quoteJsonObject  =   quoteListArr.getJSONObject(i);
                            QuoteModel  quoteModel = new QuoteModel();
                            String quote_id  =quoteJsonObject.getString("id");
                            String client_name =  quoteJsonObject.getString("company");
                            String client_id  =quoteJsonObject.getString("client_id");
                            String date = quoteJsonObject.getString("create_date");
                            String quote_code = quoteJsonObject.getString("quote_code");

                            quoteModel.quoteId =  quote_id!=null?quote_id.trim():"" ;
                            client_id = client_id!=null ?client_id.trim():"";
                            quoteModel.clientCompId = client_id ;
                            client_name =  client_name!=null? client_name.trim():"" ;
                            quoteModel.clientCompName = client_name ;
                            quoteModel.quoteCreatedDate = date!=null? date.trim():"" ;
                            quoteModel.quoteCode = quote_code!=null? quote_code.trim():"" ;
                            listQuotes.add(quoteModel);
                        }

                        QuoteListAdapter messageListAdapter = new QuoteListAdapter(getActivity(),listQuotes) ;
                        recyclerQuoteList.setAdapter(messageListAdapter);
                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerQuoteList.setLayoutManager(new LinearLayoutManager(getActivity()));

                        recyclerQuoteList.setItemAnimator(new DefaultItemAnimator());
                        recyclerQuoteList.addItemDecoration(dividerItemDecoration);
                        hideKeyboard();

                    } else {
                        hideKeyboard();
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
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etQuoteId.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etClientCompName.getWindowToken(), 0);

    }

    private void setUpToolBar() {

        Toolbar quoteToolBar  = (Toolbar) getActivity().findViewById(R.id.toolbar);

        ImageView ivBackBtnNavigation = (ImageView)quoteToolBar.findViewById(R.id.btn_navigation);
        ivBackBtnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void setupBottomBar() {
        ImageView ivQuoteSearchIcon = (ImageView) view.findViewById(R.id.ivQuoteSearchIcon);
        ImageView ivQuoteFilterIcon = (ImageView) view.findViewById(R.id.ivQuoteFilterIcon);
        ImageView addQuoteIcon  = (ImageView) view.findViewById(R.id.addQuoteIcon) ;
        addQuoteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),QuoteDetails.class);
                intent.putExtra("action","add");
                Bundle bundle  = new Bundle();
                bundle.putSerializable("details",new QuoteDetailsModel());
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });
        /** Quote Search Button  **/


        /** Quote Search Popup open Button **/

        ivQuoteSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSearchPopupOpen) {

                    if (isFiltering) {

                        quote_search_form.setVisibility(View.VISIBLE);
                        //spinnerListStatus.setVisibility(View.GONE);
                        (quote_search_form.findViewById(R.id.tvQuoteSearch)).setVisibility(View.VISIBLE);
                        etQuoteId.setVisibility(View.VISIBLE);
                        if(userType.equals("employee"))
                            etClientCompName.setVisibility(View.VISIBLE);

                        dateContainer.setVisibility(View.VISIBLE);

                        isFiltering = false;
                        isSearching = true;
                    } else {
                        quote_search_form.setVisibility(View.GONE);
                        isSearchPopupOpen = false;
                        isSearching = false;
                        isFiltering = false;
                    }

                } else {
                    quote_search_form.animate()
                            .translationX(0)
                            .alpha(1.0f)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    isSearching = true;
                                    isSearchPopupOpen = true;
                                }
                            });


                    //spinnerListStatus.setVisibility(View.GONE);
                    (quote_search_form.findViewById(R.id.tvQuoteSearch)).setVisibility(View.VISIBLE);
                    etQuoteId.setVisibility(View.VISIBLE);
                    if(userType.equals("employee"))
                        etClientCompName.setVisibility(View.VISIBLE);
                    dateContainer.setVisibility(View.VISIBLE);

                    quote_search_form.setVisibility(View.VISIBLE);

                }
            }
        });

        /**** Open Filter Popup ****/
        ivQuoteFilterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSearchPopupOpen) {

                    if (isSearching) {

                        quote_search_form.setVisibility(View.VISIBLE);
                        //spinnerListStatus.setVisibility(View.VISIBLE);
                        (quote_search_form.findViewById(R.id.tvQuoteSearch)).setVisibility(View.GONE);
                        etQuoteId.setVisibility(View.GONE);
                        etClientCompName.setVisibility(View.GONE);
                        dateContainer.setVisibility(View.GONE);
                        isSearching = false;
                        isFiltering = true;
                    } else {
                        quote_search_form.setVisibility(View.GONE);
                        isSearchPopupOpen = false;
                        isFiltering = false;
                        isSearching = false;
                    }

                } else {
                    quote_search_form.animate()
                            .translationX(0)
                            .alpha(1.0f)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    isFiltering = true;
                                    isSearchPopupOpen = true;
                                }
                            });

                   // spinnerListStatus.setVisibility(View.VISIBLE);
                    (quote_search_form.findViewById(R.id.tvQuoteSearch)).setVisibility(View.GONE);
                    etQuoteId.setVisibility(View.GONE);
                    etClientCompName.setVisibility(View.GONE);
                    dateContainer.setVisibility(View.GONE);


                    quote_search_form.setVisibility(View.VISIBLE);

                }
            }
        });

        switch(userType)
        {
            case Constants.ADMIN_EMP_TYPE :
                addQuoteIcon.setVisibility(View.GONE);
                etClientCompName.setVisibility(View.VISIBLE);
                break;
            case Constants.CLIENT_EMP_TYPE :
                addQuoteIcon.setVisibility(View.VISIBLE);
                etClientCompName.setVisibility(View.GONE);
                break;
        }
    }

    DatePickerDialog fromDatePickerDialog ;
    DatePickerDialog toDatePickerDialog ;
    long miliseconds ;
    private void showFromDateDialog() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        fromDatePickerDialog = new DatePickerDialog(getActivity(),R.style.GLCalenderStyle, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                etFromDate.setText(sdf.format(myCalendar.getTime()));
                miliseconds =  myCalendar.getTime().getTime();
            }
        }, year, month, day);

        fromDatePickerDialog.setTitle("Select Date");
        fromDatePickerDialog.show();

    }

    private void showToDateDialog() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        toDatePickerDialog = new DatePickerDialog(getActivity(), R.style.GLCalenderStyle, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                etToDate.setText(sdf.format(myCalendar.getTime()));
            }
        }, year, month, day);
        if(fromDatePickerDialog == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please select from date");
            builder.setTitle("Select From Date");
            builder.create();
            builder.show();
            return ;
        }
        toDatePickerDialog.getDatePicker().setMinDate(miliseconds);
        toDatePickerDialog.setTitle("Select Date");
        toDatePickerDialog.show();

    }
    @Override
    public void onResume() {
        super.onResume();
        listQuotes= new ArrayList<>();
        if(client_id.isEmpty())
            loadQuoteList();
        else
            loadQuoteListByClientId(client_id);
    }

    private void loadQuoteList() {
        Utils.showLoadingPopup(getActivity());

        String url = Constants.BASE_URL+"quotelist";

        HttpPostRequest.doPost(getActivity(), url, new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    HashMap<String,String> clientsHashMap =  new HashMap<>();
                    if (status) {
                        JSONArray clientsArr =   jobj.getJSONArray("client") ;
                        for (int iComp = 0; iComp < clientsArr.length(); iComp++) {
                            JSONObject clientJsonObject  = clientsArr.getJSONObject(iComp);
                            String comp_id = clientJsonObject.getString("comp_id");
                            String company_name = clientJsonObject.getString("company_name");
                            clientsHashMap.put(comp_id,company_name);
                        }
                        JSONArray quoteListArr =   jobj.getJSONArray("quote_list") ;
                        for (int i = 0; i < quoteListArr.length(); i++) {
                            JSONObject quoteJsonObject  =   quoteListArr.getJSONObject(i);
                            QuoteModel  quoteModel = new QuoteModel();
                            String quote_id  =quoteJsonObject.getString("quote_id");
                            String client_name =  quoteJsonObject.getString("client_name");
                            String client_id  =quoteJsonObject.getString("client_id");
                            String date = quoteJsonObject.getString("date");
                            String quote_code = quoteJsonObject.getString("quote_code");

                            quoteModel.quoteId =  quote_id!=null?quote_id.trim():"" ;
                            client_id = client_id!=null ?client_id.trim():"";
                            quoteModel.clientCompId = client_id ;
                            client_name =  client_name!=null? client_name.trim():"" ;
                            if(!client_id.isEmpty() && client_name.isEmpty())
                            {
                                client_name =  clientsHashMap.get(client_id);
                            }
                            quoteModel.clientCompName = client_name ;
                            quoteModel.quoteCreatedDate = date!=null? date.trim():"" ;
                            quoteModel.quoteCode = quote_code!=null? quote_code.trim():"" ;
                            listQuotes.add(quoteModel);
                        }

                        QuoteListAdapter messageListAdapter = new QuoteListAdapter(getActivity(),listQuotes) ;
                        recyclerQuoteList.setAdapter(messageListAdapter);
                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerQuoteList.setLayoutManager(new LinearLayoutManager(getActivity()));

                        recyclerQuoteList.setItemAnimator(new DefaultItemAnimator());
                        recyclerQuoteList.addItemDecoration(dividerItemDecoration);
                       // Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
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

    private void loadQuoteListByClientId(String client_id) {
        Utils.showLoadingPopup(getActivity());
        String url = Constants.BASE_URL+"quotelistbyid";
        HashMap<String ,String> data =  new HashMap<>();
        data.put("client_id",client_id);
        HttpPostRequest.doPost(getActivity(), url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {

                Utils.hideLoadingPopup();
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    Boolean status = jobj.getBoolean("status");
                    HashMap<String,String> clientsHashMap =  new HashMap<>();
                    if (status) {
                        JSONArray clientsArr =   jobj.getJSONArray("client") ;
                        for (int iComp = 0; iComp < clientsArr.length(); iComp++) {
                            JSONObject clientJsonObject  = clientsArr.getJSONObject(iComp);
                            String comp_id = clientJsonObject.getString("comp_id");
                            String company_name = clientJsonObject.getString("company_name");
                            clientsHashMap.put(comp_id,company_name);
                        }
                        JSONArray quoteListArr =   jobj.getJSONArray("quote_list") ;
                        for (int i = 0; i < quoteListArr.length(); i++) {
                            JSONObject quoteJsonObject  =   quoteListArr.getJSONObject(i);
                            QuoteModel  quoteModel = new QuoteModel();
                            String quote_id  =quoteJsonObject.getString("quote_id");
                            String client_name =  quoteJsonObject.getString("client_name");
                            String client_id  =quoteJsonObject.getString("client_id");
                            String date = quoteJsonObject.getString("date");
                            String quote_code = quoteJsonObject.getString("quote_code");

                            quoteModel.quoteId =  quote_id!=null?quote_id.trim():"" ;
                            client_id = client_id!=null ?client_id.trim():"";
                            quoteModel.clientCompId = client_id ;
                            client_name =  client_name!=null? client_name.trim():"" ;
                            if(!client_id.isEmpty() && client_name.isEmpty())
                            {
                                client_name =  clientsHashMap.get(client_id);
                            }
                            quoteModel.clientCompName = client_name ;
                            quoteModel.quoteCreatedDate = date!=null? date.trim():"" ;
                            quoteModel.quoteCode = quote_code!=null? quote_code.trim():"" ;
                            listQuotes.add(quoteModel);
                        }

                        QuoteListAdapter messageListAdapter = new QuoteListAdapter(getActivity(),listQuotes) ;
                        recyclerQuoteList.setAdapter(messageListAdapter);
                        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST);
                        recyclerQuoteList.setLayoutManager(new LinearLayoutManager(getActivity()));

                        recyclerQuoteList.setItemAnimator(new DefaultItemAnimator());
                        recyclerQuoteList.addItemDecoration(dividerItemDecoration);
                        // Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), jobj.getString("msg"), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
