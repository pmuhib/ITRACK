package com.client.itrack.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.client.itrack.R;
import com.client.itrack.adapters.ExListAdapter;
import com.client.itrack.http.HttpPostRequest;
import com.client.itrack.listener.HttpRequestCallback;
import com.client.itrack.model.DsrProfitDetail;
import com.client.itrack.model.DsrProfitHead;
import com.client.itrack.utility.Constants;
import com.client.itrack.utility.Utility;
import com.client.itrack.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * Created by root on 24/7/17.
 */

public class YTDActivity extends AppCompatActivity {
    ArrayList<DsrProfitHead> profitHeads=new ArrayList<>();
    LinkedHashMap<String,DsrProfitDetail> map=new LinkedHashMap<>();
    ArrayList<DsrProfitDetail> ddsrProfitDetails;
     ExpandableListView Ex_list;
    ExListAdapter exListAdapter;
    int lastpostion=-1;
    TextView tvDftDateRange,txtDsrProfit,txtNetProfit,txtothercashout;
    public String from_date ;
    public String to_date ;
    DatePickerDialog fromDatePickerDialog,toDatePickerDialog;
    public String currency_code;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ytd_layout);
        txtDsrProfit= (TextView) findViewById(R.id.txt_dsrProfit);
        txtNetProfit= (TextView) findViewById(R.id.txt_totalProfit);
        txtothercashout= (TextView) findViewById(R.id.txt_othercashout);
        // addlist(from_date, to_date);
      //  selectcurrencycode();
        Ex_list= (ExpandableListView) findViewById(R.id.Ex_list);
        exListAdapter=new ExListAdapter(this,profitHeads);
        Ex_list.setAdapter(exListAdapter);
        Ex_list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(lastpostion!=-1 && lastpostion!=groupPosition)
                {
                    Ex_list.collapseGroup(lastpostion);
                }
                lastpostion=groupPosition;
            }
        });
        setUpTopBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectcurrencycode();

    }

    private void selectcurrencycode() {
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        to_date=simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.MONTH,-1);
      //  from_date=simpleDateFormat.format(calendar.getTime());
        from_date=to_date; /*Changed 0n 8 Sep 2017*/

        CharSequence countries[] = new CharSequence[] {"Kuwait", "UAE", "Oman", "Saudi Arabia"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                addlist(from_date,to_date,currency_code);

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void addlist(String fromdate, String todate, String currency_code) {

        if(fromdate.equals(todate)) {
            tvDftDateRange.setText(fromdate);
            tvDftDateRange.setTextSize(30f);
        }
        else {
            tvDftDateRange.setText(fromdate+"\n"+todate);
            tvDftDateRange.setTextSize(22f);
        }
        if(profitHeads!=null)
        {
            profitHeads.clear();
        }
        Utils.showLoadingPopup(this);
        String url = Constants.BASE_URL + "dsr_profit";
        final HashMap<String, String> data = new HashMap<>();
        data.put("from_date",fromdate);
        data.put("to_date",todate);
        data.put("currency_code", this.currency_code);
        final String Currency= this.currency_code;
        HttpPostRequest.doPost(YTDActivity.this, url, Utils.newGson().toJson(data), new HttpRequestCallback() {
            @Override
            public void response(String errorMessage, String responseData) {
                Utils.hideLoadingPopup();
                try {
                    JSONObject jobj = new JSONObject(responseData);
                    String OtherCostTitle=jobj.getString("other_cash_out");
                    txtothercashout.setText(Currency+" "+((!OtherCostTitle.equals(null) || !OtherCostTitle.isEmpty())?OtherCostTitle:"0"));

                    Boolean status = jobj.getBoolean("status");
                    if (status)
                    {
                        JSONArray jsonArray = jobj.getJSONArray("dsr_profit");
                        String DsrProfit=jobj.getString("dsr_net_profit");
                        String TotalProfit=jobj.getString("total_profit");
                        txtDsrProfit.setText(Currency+" "+((!DsrProfit.equals(null) || !DsrProfit.isEmpty())?DsrProfit:"0"));
                        txtNetProfit.setText(Currency+" "+((!TotalProfit.equals(null) || !TotalProfit.isEmpty())?TotalProfit:"0"));

                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            String Ref_No=jobject.getString("dsr_ref_no");
                            JSONObject jsonObjectProfit=jobject.getJSONObject("dsr_net_profit");
                            String Net_Profit=jsonObjectProfit.getString("dsr_net_profit");
                            JSONObject jsonObjecttransportaion=jobject.getJSONObject("transportaion");
                            String transportaion_Income=jsonObjecttransportaion.getString("income");
                            String transportaion_Cost=jsonObjecttransportaion.getString("cost");
                            String transportaion_Net=jsonObjecttransportaion.getString("net");
                            JSONObject jsonObjectBorder=jobject.getJSONObject("border");
                            String border=jsonObjectBorder.getString("border");
                            JSONObject jsonObjectdetention=jobject.getJSONObject("detention");
                            String Detention_Cost=jsonObjectdetention.getString("cost");
                            String Detention_Charge=jsonObjectdetention.getString("charge");
                            String Detention_Net=jsonObjectdetention.getString("net");
                            JSONObject jsonObjectadditional_charges=jobject.getJSONObject("additional_charges");
                            String Additional_charges_Cost=jsonObjectadditional_charges.getString("cost");
                            String Additional_charges_Charge=jsonObjectadditional_charges.getString("charge");
                            String Additional_charges_Net=jsonObjectadditional_charges.getString("net");

                            /*Add HEADINGS*/
                            DsrProfitHead dsrProfitHead=new DsrProfitHead();
                            dsrProfitHead.setRefno(Ref_No);
                            dsrProfitHead.setNetProfit(Currency+" "+Net_Profit);
                            profitHeads.add(dsrProfitHead);

                            /*Add HEADING DETAILS*/
                            ddsrProfitDetails=dsrProfitHead.getDsrprofitdetail();
                            DsrProfitDetail dsrProfitDetail=new DsrProfitDetail();
                            if(Additional_charges_Charge!="null")
                            {
                                dsrProfitDetail.setAdditionalCharge(Currency+" "+Additional_charges_Charge);
                            }
                            else
                            {
                                dsrProfitDetail.setAdditionalCharge(Currency+" "+"0");
                            }
                            if(Additional_charges_Cost!="null" && !Additional_charges_Cost.isEmpty() )
                        {
                            dsrProfitDetail.setAdditionalCost(Currency+" "+Additional_charges_Cost);
                        }
                        else
                        {
                            dsrProfitDetail.setAdditionalCost(Currency+" "+"0");
                        }

                                dsrProfitDetail.setAdditionalNet(Currency+" "+Additional_charges_Net);
                         //   dsrProfitDetail.setAdditionalCharge(Currency+" "+((Additional_charges_Charge.equals(null)) ?Additional_charges_Charge:"0"));
                         //   dsrProfitDetail.setAdditionalCost(Currency+" "+ ((Additional_charges_Cost.equals(null))?Additional_charges_Cost:"0"));
                          //  dsrProfitDetail.setAdditionalNet(Currency+" "+((Additional_charges_Net.equals(null))?Additional_charges_Net:"0"));
                            dsrProfitDetail.setBorder(Currency+" "+border);
                            dsrProfitDetail.setDetentionCharge(Currency+" "+Detention_Charge);
                            dsrProfitDetail.setDetentionCost(Currency+" "+Detention_Cost);
                            dsrProfitDetail.setDetentionNet(Currency+" "+Detention_Net);
                            dsrProfitDetail.setTransporationCost(Currency+" "+transportaion_Cost);
                            dsrProfitDetail.setTransporationNet(Currency+" "+transportaion_Net);
                            dsrProfitDetail.setTransporationincome(Currency+" "+transportaion_Income);
                            ddsrProfitDetails.add(dsrProfitDetail);
                            dsrProfitHead.setDsrprofitdetail(ddsrProfitDetails);
                        }
                    }
                    else {
                        Toast.makeText(YTDActivity.this, jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                        txtDsrProfit.setText(Currency + " " + "0.00");
                        txtNetProfit.setText(Currency + " " + "0.00");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                exListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {

                Utils.hideLoadingPopup();
                Utility.sendReport(YTDActivity.this,"Ytd","Error",Utils.newGson().toJson(data),errorMessage);
                exListAdapter.notifyDataSetChanged();
            }
        });
    }
    private void setUpTopBar() {

        Toolbar ytd_tool_bar =  (Toolbar) findViewById(R.id.ytd_tool_bar);
        ImageView imageView= (ImageView) ytd_tool_bar.findViewById(R.id.btn_navigation);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onBackPressed();}
        });

        tvDftDateRange = (TextView) ytd_tool_bar.findViewById(R.id.tvDftDateRange) ;
        tvDftDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFromDateDialog();
            }
        });
    }

    private void showFromDateDialog() {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
         int date=calendar.get(Calendar.DATE);
        fromDatePickerDialog=new DatePickerDialog(this, R.style.GLCalenderStyle, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar cal=Calendar.getInstance();
                cal.set(Calendar.YEAR,year);
                cal.set(Calendar.MONTH,month);
                cal.set(Calendar.DATE,dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat mformat=new SimpleDateFormat(myFormat,Locale.US);
                String datestr=mformat.format(cal.getTime());
                from_date=datestr;
             //   to_date=datestr;
             //   addlist(from_date,to_date);
                showToDialog(cal.getTime().getTime());
            }
        },year,month,date);
        fromDatePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        fromDatePickerDialog.setTitle("Select from Date");
        fromDatePickerDialog.show();
    }

    private void showToDialog(final long minfromtime) {

        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DATE);

        toDatePickerDialog=new DatePickerDialog(this, R.style.GLCalenderStyle, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar cal=Calendar.getInstance();
                cal.set(Calendar.YEAR,year);
                cal.set(Calendar.MONTH,month);
                cal.set(Calendar.DATE,dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat mformat=new SimpleDateFormat(myFormat,Locale.US);
                String datestr=mformat.format(cal.getTime());
                to_date=datestr;
                addlist(from_date,to_date, currency_code);
            }
        },year,month,day);
        if(fromDatePickerDialog==null)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("Please select from date");
            builder.setTitle("Select From Date");
            builder.create();
            builder.show();
            return;
        }
        toDatePickerDialog.getDatePicker().setMinDate(minfromtime);
        toDatePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        toDatePickerDialog.setTitle("Select to Date");
        toDatePickerDialog.show();

    }
}













   /*    DsrProfitHead dsrProfitHead=new DsrProfitHead();
        dsrProfitHead.setRefno("ABC");
        dsrProfitHead.setNetProfit("80");
        profitHeads.add(dsrProfitHead);
      //  ArrayList<DsrProfitDetail> ddsrProfitDetails=dsrProfitHead.getDsrprofitdetail();
        DsrProfitDetail dsrProfitDetail=new DsrProfitDetail();
        dsrProfitDetail.setAdditionalCharge("100");
        dsrProfitDetail.setAdditionalCost("101");
        dsrProfitDetail.setAdditionalNet("102");
        dsrProfitDetail.setBorder("200");
        dsrProfitDetail.setDetentionCharge("300");
        dsrProfitDetail.setDetentionCost("301");
        dsrProfitDetail.setDetentionNet("302");
        dsrProfitDetail.setTransporationCost("400");
        dsrProfitDetail.setDetentionNet("401");
        dsrProfitDetail.setTransporationincome("402");

        ddsrProfitDetails.add(dsrProfitDetail);
        dsrProfitHead.setDsrprofitdetail(ddsrProfitDetails);*/

/* TabLayout tabLayout;
    ViewPager pager;
tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("DSR Profit"));
        tabLayout.addTab(tabLayout.newTab().setText("Net Profit"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        pager= (ViewPager)findViewById(R.id.viewpager_view);
        DSRProfit dsrProfitfrag=new DSRProfit();
        NetProfit netProfitfrag=new NetProfit();
        PageAdapter pageAdapter=new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount(),dsrProfitfrag,netProfitfrag);
        pager.setAdapter(pageAdapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

*/
/*
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Fragment fragment =null;
        switch(tab.getPosition()){
            case 0 :
                fragment = new DSRProfit() ;
                break ;

            case 1 :
                fragment = new NetProfit() ;
                break ;
        }

        if(fragment!=null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerYTD, fragment, null).addToBackStack(null).commit();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }*/

