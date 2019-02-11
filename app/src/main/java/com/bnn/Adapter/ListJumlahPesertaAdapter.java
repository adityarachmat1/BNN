package com.bnn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bnn.Modal.BarangBukti;
import com.bnn.Modal.JumlahPeserta;
import com.bnn.R;

import java.util.ArrayList;

/**
 * Created by Ramdan Tri Kusumawijaya on 11/5/17.
 */

public class ListJumlahPesertaAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<JumlahPeserta> jumlahPeserta = new ArrayList<>();

    public ListJumlahPesertaAdapter(Context context, ArrayList<JumlahPeserta> jumlahPesertaArrayList) {
        this.context = context;
        this.jumlahPeserta = jumlahPesertaArrayList;
    }

    @Override
    public int getCount() {
        return jumlahPeserta.size();
    }

    @Override
    public Object getItem(int i) {
        return jumlahPeserta.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_detil_listjumlahpeserta, viewGroup, false);
        }

        TextView txtNama = (TextView) view.findViewById(R.id.txtNama);
        TextView txtJenis = (TextView) view.findViewById(R.id.txtJenis);

        txtNama.setText(jumlahPeserta.get(i).getNama());
        txtJenis.setText(jumlahPeserta.get(i).getJenis());

        return view;
    }
}
