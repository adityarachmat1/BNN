package com.bnn.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bnn.R;
import com.bnn.Adapter.MainMenuAdapter;
import com.bnn.Modal.MainMenu;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.PreferenceUtils;
import java.util.ArrayList;

public class DashboardActivity extends ActivityBase {

    private ListView listMenu;
    private ArrayList<MainMenu> menuList = new ArrayList<>();
    private ArrayList<MainMenu> menuListUser = new ArrayList<>();
    private ArrayList<Integer> menus = new ArrayList<>();
    private MainMenuAdapter adapter;
    private long backPressed;
    private ImageView imgToogle;

    private PreferenceUtils prefUtils;
    private String menuUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.dashboard, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);

        menuUser = prefUtils.getUserMenu() != null && !prefUtils.getUserMenu().equals("") ? prefUtils.getUserMenu() : "";

        listMenu = (ListView) findViewById(R.id.list_menuutama);
        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);

        menuList.add(new MainMenu(202, "pemberantasan", R.drawable.base_menu_pemberantasan));
        menuList.add(new MainMenu(203, "pemberdayaan masyarakat", R.drawable.base_menu_pemberdayaan));
        menuList.add(new MainMenu(204, "pencegahan", R.drawable.base_menu_pencegahan));
        menuList.add(new MainMenu(205, "rehabilitasi", R.drawable.base_menu_rehabilitasi));

        if (!menuUser.equalsIgnoreCase("")) {
            for (int i = 0; i < menuUser.split(",").length; i++) {
                menus.add(Integer.valueOf(menuUser.split(",")[i]));
            }
        }

        if (menus.size() > 0) {
            for (int i = 0; i < menus.size(); i++) {
                for (int x = 0; x < menuList.size(); x++) {
                    if (menuList.get(x).getMenuId() == menus.get(i)) {
                        menuListUser.add(menuList.get(x));
                    }
                }
            }
        }

        adapter = new MainMenuAdapter(this, menuListUser);
        listMenu.setAdapter(adapter);

        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainMenu main = adapter.getItem(i);
                Intent intent = null;

                switch (main.getMenuId()) {
                    case 202:
                        intent = new Intent(DashboardActivity.this, PemberantasanActivity.class);
                        break;
                    case 203:
                        intent = new Intent(DashboardActivity.this, PemberdayaanActivity.class);
                        break;
                    case 204:
                        intent = new Intent(DashboardActivity.this, PencegahanActivity.class);
                        break;
                    case 205:
                        intent = new Intent(DashboardActivity.this, RehabilitasiActivity.class);
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
        if (backPressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            finish();
        } else {
            showToast(1, "Press once again to exit!");
            backPressed = System.currentTimeMillis();
        }
    }
}
