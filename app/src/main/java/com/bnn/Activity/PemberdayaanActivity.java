package com.bnn.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bnn.Adapter.SubMenuAdapter;
import com.bnn.Modal.SubMenu;
import com.bnn.R;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by USER on 10/26/2017.
 */

public class PemberdayaanActivity extends ActivityBase {
    //DiPerbaharui pada 14 Nov 2017
    private ListView submenu;
    private ArrayList<SubMenu> subMenus = new ArrayList<>();
    private SubMenuAdapter adapter;
    private long backPressed;
    private ImageView imgToogle;

    //Added in 21 Nov 2017
    private ArrayList<SubMenu> subMenusUser = new ArrayList<>();
    private ArrayList<Integer> menus = new ArrayList<>();
    private PreferenceUtils prefUtils;

    private String menuUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_submenupemberdayaanmasyarakat, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);
        menuUser = prefUtils.getUserMenu() != null && !prefUtils.getUserMenu().equals("") ? prefUtils.getUserMenu() : "";

        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        submenu = (ListView) findViewById(R.id.list_submenu);

        subMenus.add(new SubMenu(209, "Tes Urine"));
        subMenus.add(new SubMenu(210, "Alih Fungsi / Peninjauan Lahan"));

        if (!menuUser.equalsIgnoreCase("")) {
            for (int i = 0; i < menuUser.split(",").length; i++) {

                menus.add(Integer.valueOf(menuUser.split(",")[i]));
            }
        }

        if (menus.size() > 0) {
            for (int i = 0; i < menus.size(); i++) {
                for (int x = 0; x < subMenus.size(); x++) {
                    if (subMenus.get(x).getMenuId() == menus.get(i)) {

                        subMenusUser.add(subMenus.get(x));
                    }
                }
            }
        }

        adapter = new SubMenuAdapter(this, subMenusUser);
        submenu.setAdapter(adapter);

        submenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SubMenu sub = adapter.getItem(i);
                Intent intent = null;

                switch (sub.getMenuId()) {
                    case 209:
                        intent = new Intent(PemberdayaanActivity.this, TesNarkobaPositifActivity.class);
                        break;
                    case 210:
                        intent = new Intent(PemberdayaanActivity.this, AlihFungsiLahanActivity.class);
                        break;
                }

                startActivity(intent);
                finish();
            }
        });

        imgToogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDrawerLayout.openDrawer(linearLayout2);
            }
        });
    }

    private void showToast(int dur, String msg) {
        Toast.makeText(this, msg, dur == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PemberdayaanActivity.this, DashboardActivity.class));
        finish();
    }
}