package com.bnn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bnn.Modal.BarangBukti;
import com.bnn.R;

import java.util.ArrayList;

/**
 * Created by ferdinandprasetyo on 11/5/17.
 */

public class ListBarangBuktiAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<BarangBukti> barangBuktiArrayList = new ArrayList<>();

    public ListBarangBuktiAdapter(Context context, ArrayList<BarangBukti> barangBuktiArrayList) {
        this.context = context;
        this.barangBuktiArrayList = barangBuktiArrayList;
    }

    @Override
    public int getCount() {
        return barangBuktiArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return barangBuktiArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_detil_listbarangbukti, viewGroup, false);
        }

        TextView txtJenis = (TextView) view.findViewById(R.id.txtJenis);
        TextView txtJumlah = (TextView) view.findViewById(R.id.txtJumlah);
        TextView txtSatuan = (TextView) view.findViewById(R.id.txtSatuan);

        txtJenis.setText(barangBuktiArrayList.get(i).getJenisBarang());
        txtJumlah.setText(barangBuktiArrayList.get(i).getJumlah());
        txtSatuan.setText(barangBuktiArrayList.get(i).getSatuan());

        return view;
    }
}
