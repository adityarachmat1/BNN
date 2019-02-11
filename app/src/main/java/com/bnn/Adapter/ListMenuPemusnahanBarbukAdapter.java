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

public class ListMenuPemusnahanBarbukAdapter extends ArrayAdapter<Listmenu> {

    final Context ctx;
    final ArrayList<Listmenu> items;
    final LayoutInflater vi;
    private ArrayList<Listmenu> arraylist;

    public ListMenuPemusnahanBarbukAdapter(Context ctx, ArrayList<Listmenu> items) {
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

        v = vi.inflate(R.layout.item_listsearch_pemusnahanbarbuk, null);
        TextView tanggallist = (TextView) v.findViewById(R.id.txtTitle1);
        TextView nomerid = (TextView) v.findViewById(R.id.txtTitle2);
        TextView lokasi = (TextView) v.findViewById(R.id.txtTitle3);
        TextView lkn = (TextView) v.findViewById(R.id.txtTitle4);
        TextView jenisbarang = (TextView) v.findViewById(R.id.txtTitle5);

        if (i.getTanggal() != null && !i.getTanggal().equals("null")) {
            tanggallist.setText(getNewDate(i.getTanggal()));
        } else {
            tanggallist.setText("-");
        }

        if (i.getLkn() != null && !i.getLkn().equals("null")) {
            lkn.setText(i.getLkn());
            Log.d("lkn", i.getLkn());
        } else {
            lkn.setText("-");
        }

        if (i.getJenisbarang() != null && !i.getJenisbarang().equals("null")) {
            jenisbarang.setText(i.getJenisbarang());
        } else {
            jenisbarang.setText("-");
        }

        if (i.getLokasi() != null && !i.getLokasi().equals("null") && !i.getLokasi().equals("")) {
            lokasi.setText(i.getLokasi());
        } else {
            lokasi.setText("-");
        }

        if (i.getNoid() != null && !i.getNoid().equals("null")) {
            nomerid.setText(i.getNoid());
        } else {
            nomerid.setText("-");
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
