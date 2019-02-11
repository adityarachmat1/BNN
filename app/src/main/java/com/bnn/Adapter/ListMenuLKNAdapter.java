package com.bnn.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bnn.Modal.Listmenu;
import com.bnn.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by USER on 10/27/2017.
 */

public class ListMenuLKNAdapter extends ArrayAdapter<Listmenu> {

    final Context ctx;
    final ArrayList<Listmenu> items;
    final LayoutInflater vi;
    private ArrayList<Listmenu> arraylist;

    public ListMenuLKNAdapter(Context ctx, ArrayList<Listmenu> items) {
        super(ctx, 0, items);
        this.ctx = ctx;
        this.items = items;
        this.arraylist = new ArrayList<Listmenu>();
        this.arraylist.addAll(items);
        vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final Listmenu i = getItem(position);

        v = vi.inflate(R.layout.item_list_lkn, null);
        TextView jenisBrg = (TextView) v.findViewById(R.id.nm_jenis_brg_lkn);
        TextView lokasi = (TextView) v.findViewById(R.id.lokasi_lkn);
        TextView total = (TextView) v.findViewById(R.id.total_brgLkn);

        if (i.getTanggal() != null && !i.getTanggal().equals("null")) {
            jenisBrg.setText(i.getTanggal());
        } else {
            jenisBrg.setText("-");
        }

        if (i.getLkn() != null && !i.getLkn().equals("null") && !i.getLkn().equals("")) {
            lokasi.setText(i.getLkn());
        } else {
            lokasi.setText("-");
        }

        if (i.getJenisbarang() != null && !i.getJenisbarang().equals("null")) {
            total.setText(i.getJenisbarang());
        } else {
            total.setText("-");
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

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        items.clear();
        if (charText.length() == 0) {
            items.addAll(arraylist);
        } else {
            for (Listmenu wp : arraylist) {
                if (wp.getJenisbarang().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    items.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
