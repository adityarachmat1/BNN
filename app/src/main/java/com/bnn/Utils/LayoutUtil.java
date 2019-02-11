package com.bnn.Utils;

import android.support.v4.media.AudioAttributesCompat;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andre Tampubolon (andre.tampubolon@idstar.co.id) on 10/26/2018.
 */
    public class LayoutUtil {

    public static String getMetaInstansi(LinearLayout ll){
        int result = 0;
        int total = ll.getChildCount();
        String sss = "";
        JSONArray ja = new JSONArray();

        for (int x = 0; x < total; x++){
            RelativeLayout rl = (RelativeLayout) ll.getChildAt(x);
            LinearLayout ll2 = (LinearLayout) rl.getChildAt(0);
            AutoCompleteTextView aa1 = (AutoCompleteTextView) ll2.getChildAt(0);
            AutoCompleteTextView aa2 = (AutoCompleteTextView) ll2.getChildAt(1);

            JSONObject jso = new JSONObject();
            try {
                jso.put("list_nama_instansi", aa1.getText().toString());
                jso.put("list_jumlah_peserta", aa2.getText().toString());

                ja.put(jso);
            }
            catch (JSONException je){

            }

        }

        return ja.toString();
    }

    public static String getJumlahPesertaFromMetaInstansi(LinearLayout ll){
        int result = 0;
        int total = ll.getChildCount();
        int tmp = 0;

        for (int x = 0; x < total; x++) {
            RelativeLayout rl = (RelativeLayout) ll.getChildAt(x);
            LinearLayout ll2 = (LinearLayout) rl.getChildAt(0);
            AutoCompleteTextView aa1 = (AutoCompleteTextView) ll2.getChildAt(1);
            tmp = Integer.parseInt(aa1.getText().toString());
            result += tmp;
        }

        return ""+result;
    }

}
