package com.bnn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bnn.Modal.MenuPencegahan;
import com.bnn.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by USER on 10/27/2017.
 */

public class ListMenuOnlineAdapter extends ArrayAdapter<MenuPencegahan> {

    final Context ctx;
    final ArrayList<MenuPencegahan> items;
    final LayoutInflater vi;

    public ListMenuOnlineAdapter(Context ctx, ArrayList<MenuPencegahan> items) {
        super(ctx, 0, items);
        this.ctx = ctx;
        this.items = items;
        vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final MenuPencegahan i = getItem(position);

            v = vi.inflate(R.layout.item_listsearch_online, null);
            TextView txtTanggal = (TextView) v.findViewById(R.id.txtTitle1);
            TextView txtNoId = (TextView) v.findViewById(R.id.txtTitle2);
            TextView txtJudul = (TextView) v.findViewById(R.id.txtTitle3);
            TextView txtPelaksana = (TextView) v.findViewById(R.id.txtTitle4);
            TextView txtJenis = (TextView) v.findViewById(R.id.txtTitle5);

            if (i.getTanggal() != null && !i.getTanggal().equals("null")) {
                txtTanggal.setText(getNewDate(i.getTanggal()));
            } else {
                txtTanggal.setText("-");
            }

            if (i.getPelaksana() != null && !i.getPelaksana().equals("null")) {
                txtPelaksana.setText(i.getPelaksana());
            } else {
                txtPelaksana.setText("-");
            }

            if (i.getJudul() != null && !i.getJudul().equals("null")) {
                txtJudul.setText(i.getJudul());
            } else {
                txtJudul.setText("-");
            }

            if (i.getJenisMedia() != null && !i.getJenisMedia().equals("null")) {
                txtJenis.setText(i.getJenisMedia());
            } else {
                txtJenis.setText("-");
            }

            if (i.getNoid() != null && !i.getNoid().equals("null")) {
                txtNoId.setText(i.getNoid());
            } else {
                txtNoId.setText("-");
            }

            return v;
        }

    public String getNewDate(String date){
        String newDate = "01-01-2016";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = simpleDateFormat.parse(date);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
            newDate = newFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }
}
