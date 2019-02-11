package com.bnn.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ferdinandprasetyo on 11/22/17.
 */

public class DateUtils {
    public DateUtils() {
    }

    public static String getNewDateForServer(String date){
        String newDate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d = simpleDateFormat.parse(date);
            Log.d("yolo", "date : " + d);
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            newDate = newFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }
}
