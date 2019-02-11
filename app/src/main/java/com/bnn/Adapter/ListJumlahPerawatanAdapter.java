package com.bnn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bnn.Modal.JumlahPerawatan;
import com.bnn.Modal.JumlahPeserta;
import com.bnn.R;

import java.util.ArrayList;

/**
 * Created by Ramdan Tri Kusumawijaya on 11/5/17.
 */

public class ListJumlahPerawatanAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<JumlahPerawatan> jumlahPerawaten = new ArrayList<>();

    public ListJumlahPerawatanAdapter(Context context, ArrayList<JumlahPerawatan> jumlahPerawatanArrayList) {
        this.context = context;
        this.jumlahPerawaten = jumlahPerawatanArrayList;
    }

    @Override
    public int getCount() {
        return jumlahPerawaten.size();
    }

    @Override
    public Object getItem(int i) {
        return jumlahPerawaten.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_detil_listjumlahperawatan, viewGroup, false);
        }

        TextView txtNama = (TextView) view.findViewById(R.id.txtNama);

        txtNama.setText(jumlahPerawaten.get(i).getNama());

        return view;
    }
}
