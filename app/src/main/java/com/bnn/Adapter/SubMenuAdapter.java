package com.bnn.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnn.Modal.SubMenu;
import com.bnn.R;

import java.util.ArrayList;

/**
 * Created by USER on 10/26/2017.
 */

public class SubMenuAdapter extends ArrayAdapter<SubMenu> {

    final Context ctx;
    final ArrayList<SubMenu> items;
    final LayoutInflater vi;


    public SubMenuAdapter(Context ctx, ArrayList<SubMenu> items) {
        super(ctx, 0, items);
        this.ctx = ctx;
        this.items = items;
        vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final SubMenu i = getItem(position);

        v = vi.inflate(R.layout.item_listkategori, null);
        TextView title = (TextView) v.findViewById(R.id.namakategory);

        title.setText(i.getnamaKategory());

        return v;
    }
}
