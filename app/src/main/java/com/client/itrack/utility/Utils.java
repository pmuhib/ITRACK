package com.client.itrack.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;


import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Created by Technocrats on 9/5/2015.
 */
public class Utils {
    static ProgressDialog progressDialog;

    public static boolean isNetworkConnected(Activity activity, boolean checkInBackground) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        else if (!checkInBackground)
            showSettingsPopup(activity);
        return false;
    }

    public static boolean isNetworkConnected(Context activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        return false;
    }

    public static void printLog(String log) {
        Log.e(Constants.LOG_TAG, log + "");
    }

    public static void printLog2(String log) {
        Log.e(Constants.LOG_TAG2, log + "");
    }

    public static void showLoadingPopup(Activity activity) {
        if (progressDialog != null)
        {
            progressDialog.dismiss();

        }

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
//            int i = 10 / 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideLoadingPopup() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    public static Gson newGson() {
        return new Gson();
    }

    /*public static StandardResponse parseResponse(String responseData) {
        StandardResponse standardResponse = new StandardResponse();
        try {
            StandardResponse serverResponse = newGson().fromJson(responseData, StandardResponse.class);
            if (serverResponse != null)
                return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return standardResponse;
    }*/

    public static String fetchObject(String jsonString, String key) {
        String value = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has(key))
                return jsonObject.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    public static JSONObject fetchJsonObject(String jsonString, String key) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has(key))
                return jsonObject.getJSONObject(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

   /* public static void saveToken(Context context, String token) {
        SharedPreferenceStore.storeValue(context, Constants.TOKEN, token);
    }

    public static String retrieveToken(Context context) {
        return SharedPreferenceStore.getValue(context, Constants.TOKEN, (String) null);
    }

    public static boolean userProfileComplete(UserModel userModel) {
        if (notNull(userModel.socialMedia.facebookToken) || notNull(userModel.socialMedia.twitterToken) || notNull(userModel.socialMedia.googlePlusToken)) {
            return true;
        }
        return false;
    }
*/
    public static boolean isUserSignedIn(Context context) {
//        String userObject = SharedPreferenceStore.getValue(context, Constants.USER, "");
//        if (userObject != null && !userObject.equals(""))
//            return true;
        return false;
    }

  /*  public static void saveUser(Context context, UserModel userModel) {
        if (userModel != null) {
            SharedPreferenceStore.storeValue(context, Constants.USER, newGson().toJson(userModel));
        }
    }

    public static void deleteUser(Context context) {
        SharedPreferenceStore.deleteValue(context, Constants.USER);
    }*/

    //    public static
//    public static UserModel getUser(Context context) {
//        String userObject = SharedPreferenceStore.getValue(context, Constants.USER, "");
//        if (!userObject.equals("")) {
//            try {
//                return newGson().fromJson(userObject, UserModel.class);
//            } catch (Exception e) {
//            }
//        }
//        return null;
//    }

    public static void showSettingsPopup(final Activity activity) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("No Internet connection.");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }


    public static void showMessage(Context context, String responseMessage) {
        Toast.makeText(context, responseMessage, Toast.LENGTH_SHORT).show();
    }

    public static void removeOtherFromBackStack(android.app.FragmentManager manager) {
        int totalFragemantsInStack = manager.getBackStackEntryCount();
        while (totalFragemantsInStack > 1) {
            manager.popBackStack();
            totalFragemantsInStack--;
        }

    }

    static Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");

    public static boolean isValidEmail(String email) {
        Matcher m = emailPattern.matcher(email);
        return m.matches();
    }

    public static void addToBackStack(FragmentTransaction transaction, Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        transaction.addToBackStack(backStateName);
    }

    public static void callNow(Activity activity, String mobileNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(mobileNumber));
        activity.startActivity(intent);
    }

    public static void shareNow(Activity activity, String subject, String content) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, content);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        activity.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    public static boolean isNull(Activity activity, String text[], String message[]) {
        for (int i = 0; i < text.length; i++) {
            if (text[i] == null || text[i].equals("")) {
                Utils.showMessage(activity, message[i] + " must not null!");
                return true;
            }
        }
        return false;
    }

    public static boolean gpsEnabled(Activity activity) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(activity);
            return false;
        }
        return true;
    }

    static AlertDialog alert = null;

    private static void buildAlertMessageNoGps(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        alert.cancel();
                    }
                })
                .setNegativeButton("Enter Location", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        alert.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static Bitmap decodeFile(Context context, File f) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inDither = false; // Disable Dithering mode
        options.inPurgeable = true; // Tell to gc that whether it needs free memory,
        options.inInputShareable = true; // Which kind of reference will be used to
        try {
            BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        final int REQUIRED_SIZE = 300;
        int width_tmp = options.outWidth, height_tmp = options.outHeight;
        while (true) {
            if (width_tmp / 1.5 < REQUIRED_SIZE && height_tmp / 1.5 < REQUIRED_SIZE)
                break;
            width_tmp /= 1.5;
            height_tmp /= 1.5;
        }
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        options.inDither = false; // Disable Dithering mode
        options.inPurgeable = true; // Tell to gc that whether it needs free memory,
        options.inInputShareable = true; // Which kind of reference will be used to
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
            System.out.println(" IW " + width_tmp);
            System.out.println("IHH " + height_tmp);
            int iW = width_tmp;
            int iH = height_tmp;
            return Bitmap.createScaledBitmap(bitmap, iW, iH, true);

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

   /* public static String getValidDateFormat(String dateString) {
        try {
            Utils.printLog("<><><><><><><>><<<><><><><><><><><><><><><><><><>");
            Utils.printLog(dateString);
            Utils.printLog("<><><><><><><>><<<><><><><><><><><><><><><><><><>");
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = originalFormat.parse(dateString);
            String formattedDate = targetFormat.format(date);
            formattedDate = formattedDate.toUpperCase();
            return formattedDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }*/

  /*  public static String getGcmId(Context context) {
        return SharedPreferenceStore.getValue(context, Constants.GCM_ID_SCN, "NA");
    }

    public static void registerGCM(final Activity activity) {
        String gcmID = SharedPreferenceStore.getValue(activity, Constants.GCM_ID_SCN, "");
        if (gcmID != null && !gcmID.equals(""))
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InstanceID instanceID = InstanceID.getInstance(activity);
                    final String token = instanceID.getToken(activity.getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    SharedPreferenceStore.storeValue(activity.getApplicationContext(), Constants.GCM_ID_SCN, token);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            registerWithServer(token, activity);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void unregisterWithServer(Activity activity) {
        String gcmID = SharedPreferenceStore.getValue(activity, Constants.GCM_ID_SCN, "");
        SharedPreferenceStore.deleteValue(activity, Constants.GCM_ID_SCN);
        String url = "http://raghukaka.com/api/RemoveGcmUser?gcm_id=" + gcmID;

    }
*/
    public static String getDeviceID(Context context) {
        String deviceId = "NA";
        try {

            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            deviceId = deviceUuid.toString();
        } catch (Exception e) {

        }
        return deviceId;
    }


    /*public static void showExitAlert(final Activity activity) {
        String title = "Alert!";
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(title);
        builder.setMessage("Exit now?").setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }
*/
    public static DatePickerDialog getDatePicker(Activity activity, DatePickerDialog.OnDateSetListener listener, boolean isDayRequired) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(activity, listener, year, month, day);
        if (!isDayRequired)
            try {
                java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
                for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                    if (datePickerDialogField.getName().equals("mDatePicker")) {
                        datePickerDialogField.setAccessible(true);
                        DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                        java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                        for (java.lang.reflect.Field datePickerField : datePickerFields) {
                            if ("mDaySpinner".equals(datePickerField.getName())) {
                                datePickerField.setAccessible(true);
                                Object dayPicker = datePickerField.get(datePicker);
                                ((View) dayPicker).setVisibility(View.GONE);
                            }
                        }
                    }
                }
//                dpd.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis() - 1000);
                return dpd;
            } catch (Exception ex) {
                return new DatePickerDialog(activity, listener, year, month, day);
            }
        dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        return dpd;
    }

    public static String parseDecimal(double d) {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(d);
    }

    public static void show(Context applicationContext, String message) {
        Toast.makeText(applicationContext, "" + message, Toast.LENGTH_SHORT).show();
    }

    public static boolean notNull(String value) {
        if (value == null || value.equals(""))
            return false;
        return true;
    }




    public static String BitMapToString(Bitmap bitmap){
        String temp="";
        if(bitmap!=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
            byte[] b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
        }


        return temp;
    }

    public static boolean isValidDomain(String s) {
        String patternDomain = "((https?://)(www[.])?|www[.])([^/\\s]*)" ;
        return  s.matches(patternDomain);
    }
}
