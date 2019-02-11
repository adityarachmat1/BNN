package com.bnn.Utils;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by acilryandi on 10/25/17.
 */

public class Configuration {
    //private static final String DEV_URL = "http://103.3.70.166/bnn/public";
    private static final String DEV_URL = "http://103.3.70.168/bnn_api/public";
    public static final String DEV_URL_OSB = "http://103.3.70.160:7004/sinapp";
    private static final String PROD_URL = "http://sin.bnn.go.id";
    public static final String baseUrl = DEV_URL;
//    public static final String baseUrl = "http://103.3.70.86";
//    public static final String baseUrl = "http://103.3.70.91";

    //function to check build version of the device
    public static Double buildVersion(){
        String info = Build.VERSION.RELEASE;

        if (info != null){
            Double version = Double.valueOf(info.substring(0, 3));
            return version;
        }
        return -1.0;
    }

    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = null;
        boolean enabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            try {
                enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return enabled;
    }

    public static AlertDialog.Builder showAlertNoTitle(Context ctx, String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
        alert.setMessage(message)
                .setCancelable(false);
        return alert;
    }

    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static boolean isDeviceOnlineForLogin(Context context) {
        boolean internet = false;
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    URL url = new URL("https://www.google.com/");
                    HttpsURLConnection urlc = (HttpsURLConnection)url.openConnection();
                    urlc.setRequestMethod("GET");
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        internet = true;
                    } else {
                        internet = false;
                    }
                } catch (IOException e) {
                    internet = false;
                }
            }
        } else {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    URL url = new URL("https://www.google.com/");
                    HttpsURLConnection urlc = (HttpsURLConnection) url.openConnection();
                    urlc.setRequestMethod("GET");
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        internet = true;
                    } else {
                        internet = false;
                    }
                } catch (IOException e) {
                    internet = false;
                }
            }
        }
        return internet;
    }

    public static String getVersionApp(Context context){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "0.0";
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String formatYearMonthDate(String ddMMyyyy){
        String reformattedStr = null;
        SimpleDateFormat fromUser = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            reformattedStr = myFormat.format(fromUser.parse(ddMMyyyy));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public static String formatDateMonthYear(String yyyyMMdd){
        String newDate = "2016-01-01";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = simpleDateFormat.parse(yyyyMMdd);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
            newDate = newFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }

    public static boolean isMockSettingsOn(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    public static String getStringRes(Context context, int id) {
        return context.getResources().getString(id);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAutodatetime(Context context) {
        int autoValue = 0;
        try {
            autoValue = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (autoValue == 1) {
            return true;
        }

        return false;
    }

    public static void showToast(Context context, int dur, String msg) {
        Toast.makeText(context, msg, dur == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }
}
