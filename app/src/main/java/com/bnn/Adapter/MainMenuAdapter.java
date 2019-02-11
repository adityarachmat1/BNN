package com.bnn.Adapter;

/**
 * Created by USER on 10/25/2017.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnn.R;
import com.bnn.Modal.MainMenu;

import java.util.ArrayList;

public class MainMenuAdapter extends ArrayAdapter<MainMenu> {

    final Context ctx;
    final ArrayList<MainMenu> items;
    final LayoutInflater vi;


    public MainMenuAdapter(Context ctx, ArrayList<MainMenu> items) {
        super(ctx, 0, items);
        this.ctx = ctx;
        this.items = items;
        vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final MainMenu i = getItem(position);

        v = vi.inflate(R.layout.item_listmenu, null);
        TextView title = (TextView) v.findViewById(R.id.namamenuutama);
        ImageView img = (ImageView) v.findViewById(R.id.imgmenu);

        Log.d("menu id", "menu id: "+i.getMenuId());

        title.setText(i.getNama());
        String tes = ctx.getResources().getResourceEntryName(i.getImg());
        int id = ctx.getResources().getIdentifier(tes, "drawable", ctx.getPackageName());

        img.setImageResource(id);

        return v;
    }
}
