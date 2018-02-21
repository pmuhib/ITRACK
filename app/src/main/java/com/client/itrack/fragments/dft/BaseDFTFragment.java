package com.client.itrack.fragments.dft;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.client.itrack.activities.DFTMainActivity;
import com.client.itrack.R;
import com.client.itrack.utility.AppGlobal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by sony on 19-09-2016.
 */
public class BaseDFTFragment extends Fragment {

    public AppGlobal appGlobal = AppGlobal.getInstance() ;
    public String from_date ;
    public String to_date ;
    public String currency_code;
    protected SimpleDateFormat dateFormat ;

    DatePickerDialog fromDatePickerDialog ;
    DatePickerDialog toDatePickerDialog ;

    TextView tvDftDateRange ;

    public void setupToolBar() {
        Toolbar dft_toolbar = (Toolbar) getActivity().findViewById(R.id.dft_toolbar);

        dft_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { getActivity().onBackPressed();}
        });

        tvDftDateRange = (TextView) dft_toolbar.findViewById(R.id.tvDftDateRange) ;
        tvDftDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFromDateDialog();
            }
        });
    }



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
                String dateStr = sdf.format(myCalendar.getTime());
                from_date = dateStr;
                to_date = dateStr ;
                setDateToView();
                showToDateDialog(myCalendar.getTime().getTime());
            }
        }, year, month, day);
        fromDatePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        fromDatePickerDialog.setTitle("Select from Date");
        fromDatePickerDialog.show();

    }

    private void showToDateDialog(final long milis) {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        toDatePickerDialog = new DatePickerDialog(getActivity(), R.style.GLCalenderStyle,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                to_date = sdf.format(myCalendar.getTime());
                setDateToView();
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
        toDatePickerDialog.getDatePicker().setMinDate(milis);
        toDatePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());

        toDatePickerDialog.setTitle("Select to Date");
        toDatePickerDialog.show();

    }
    protected void setDateToView() {
        DFTMainActivity dftMainActivity = (DFTMainActivity)getActivity();

        dftMainActivity.from_date = from_date ;
        dftMainActivity.to_date = to_date ;

        final SimpleDateFormat dfForView = new SimpleDateFormat("dd-MM-yyyy",Locale.US);
        String fromDate ="" ;
        String toDate ="" ;
        try {
            fromDate = dfForView.format(dateFormat.parse(from_date));
            toDate  = dfForView.format(dateFormat.parse(to_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(fromDate.equals(toDate)) {
            tvDftDateRange.setText(fromDate);
            tvDftDateRange.setTextSize(30f);
        }
        else {
            tvDftDateRange.setText(fromDate+"\n"+toDate);
            tvDftDateRange.setTextSize(22f);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }
}
