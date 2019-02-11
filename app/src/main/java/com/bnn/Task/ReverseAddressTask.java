package com.bnn.Task;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by LENOVO on 10/10/2016.
 */
public class ReverseAddressTask extends AsyncTask<Void, String, String> {
    private static final String TAG = "LocationAddress";
    private ReverseAddress reverseAddress;
    private Context ctx;
    private double latitude, longitude;
    private String address;

    public interface ReverseAddress{
        void onAdrressListener(String address);
    }

    public ReverseAddressTask(Context ctx, double latitude, double longitude, ReverseAddress reverseAddress){
        this.ctx = ctx;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reverseAddress = reverseAddress;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = null;

        result = getAddressFromLocation(latitude, longitude, ctx);

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s != null){
            reverseAddress.onAdrressListener(s);
        } else {
            reverseAddress.onAdrressListener("No Location Found");
        }
    }

    private String getAddressFromLocation(final double latitude, final double longitude,
                                          final Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);

//                Log.d(TAG, "Address: "+address.toString());

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(" ");
                }

                if (sb.toString().equals("")) {
                    sb.append(!address.getThoroughfare().equals("") ? address.getThoroughfare()+", " : "");
                    sb.append(address.getLocality().equals("") ? address.getLocality()+", " : "");
                    sb.append(address.getSubAdminArea().equals("") ? address.getSubAdminArea()+", " : "");
                    sb.append(address.getAdminArea().equals("") ? address.getAdminArea()+", " : "");
                    sb.append(address.getLocality().equals("") ? address.getLocality()+", " : "");
                    sb.append(address.getPostalCode().equals("") ? address.getPostalCode()+", " : "");
                    sb.append(address.getCountryName().equals("") ? address.getCountryName()+", " : "");
                }

                result = sb.toString();
                return result;
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable connect to Geocoder", e);
        }

        return null;
    }
}
