package com.bnn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bnn.Modal.BarangBukti;
import com.bnn.Modal.Peserta;
import com.bnn.R;

import java.util.ArrayList;

/**
 * Created by ferdinandprasetyo on 11/5/17.
 */

public class ListPesertaAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Peserta> pesertaArrayList = new ArrayList<>();

    public ListPesertaAdapter(Context context, ArrayList<Peserta> pesertaArrayList) {
        this.context = context;
        this.pesertaArrayList = pesertaArrayList;
    }

    @Override
    public int getCount() {
        return pesertaArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return pesertaArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_detil_listpeserta, viewGroup, false);
        }

        TextView txtNamaPeserta = (TextView) view.findViewById(R.id.txtNamaPeserta);
        TextView txtJekelPeserta = (TextView) view.findViewById(R.id.txtJekelPeserta);

        txtNamaPeserta.setText(pesertaArrayList.get(i).getNama());
        txtJekelPeserta.setText(pesertaArrayList.get(i).getJenisKelamin());

        return view;
    }
}
