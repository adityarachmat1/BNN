package com.bnn.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bnn.Adapter.SubMenuPencegahan;
import com.bnn.Modal.ExpandedSubmenu;
import com.bnn.R;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by USER on 10/26/2017.
 */

public class PencegahanActivity extends ActivityBase{
    private static final String TAG = PencegahanActivity.class.getSimpleName();

    protected ExpandableListView expandedSubmenu;
    private List<ExpandedSubmenu> listDataHeader;
    private HashMap<ExpandedSubmenu, List<ExpandedSubmenu>> listDataChild;
    private SubMenuPencegahan adapter;
    private ImageView imgToogle;

    private List<ExpandedSubmenu> listDataHeaderUser;
    HashMap<ExpandedSubmenu, List<ExpandedSubmenu>> listDataChildUser;
    List<Integer> listMenu = new ArrayList<>();

    private PreferenceUtils prefUtils;
    private String menuUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_submenupencegahan, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);
        menuUser = prefUtils.getUserMenu() != null && !prefUtils.getUserMenu().equals("") ? prefUtils.getUserMenu() : "";

        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        expandedSubmenu = (ExpandableListView) findViewById(R.id.expandablepencegahan);

        if (!menuUser.equalsIgnoreCase("")) {
            for (int i = 0; i < menuUser.split(",").length; i++) {
                listMenu.add(Integer.valueOf(menuUser.split(",")[i]));
            }
        }

        listDataUser(expandList, listMenu);

        adapter = new SubMenuPencegahan(this, listDataHeaderUser, listDataChildUser);
        expandedSubmenu.setAdapter(adapter);
        expandedSubmenu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                ExpandedSubmenu expandedSubmenu = (ExpandedSubmenu)adapter.getChild(i, i1);

                onNavigationItemSelectedId(expandedSubmenu.getMenuId());
                return false;
            }
        });

        expandedSubmenu.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if (adapter.getChildrenCount(i) == 0) {
                }

                return false;
            }
        });

        imgToogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDrawerLayout.openDrawer(linearLayout2);
            }
        });
    }

    private void listDataUser(ExpandableListView expandList, List<Integer> menuId) {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataHeaderUser = new ArrayList<>();
        listDataChildUser = new HashMap<>();

        listDataHeader.add(new ExpandedSubmenu(211,"Advokasi"));
        List<ExpandedSubmenu> orderChild = new ArrayList<ExpandedSubmenu>();
        orderChild.add(new ExpandedSubmenu(214,"Kegiatan Rapat Koordinasi"));
        orderChild.add(new ExpandedSubmenu(215,"Kegiatan Membangun Jejaring"));
        orderChild.add(new ExpandedSubmenu(216,"Kegiatan Asistensi"));
        orderChild.add(new ExpandedSubmenu(217,"Kegiatan Intervensi"));
        orderChild.add(new ExpandedSubmenu(218,"Kegiatan Supervisi"));
        orderChild.add(new ExpandedSubmenu(219,"Kegiatan Monitoring dan Evaluasi"));
        orderChild.add(new ExpandedSubmenu(220,"Kegiatan Bimbingan Teknis"));
        orderChild.add(new ExpandedSubmenu(221,"Kegiatan KIE"));
        listDataChild.put(listDataHeader.get(0), orderChild);

        listDataHeader.add(new ExpandedSubmenu(212,"Diseminasi Informasi"));
        List<ExpandedSubmenu> orderChild1 = new ArrayList<ExpandedSubmenu>();

        //Edit on 18 November 2017
        orderChild1.add(new ExpandedSubmenu(222,"Media Online"));
        orderChild1.add(new ExpandedSubmenu(223,"Media Penyiaran"));
        orderChild1.add(new ExpandedSubmenu(224,"Media Cetak"));
        orderChild1.add(new ExpandedSubmenu(225,"Media Konvensional"));
        listDataChild.put(listDataHeader.get(1), orderChild1);

        if (menuId.size() > 0) {
            for (int x = 0; x < menuId.size(); x++) {
                for (int z = 0; z < listDataHeader.size(); z++) {
                    if (listDataHeader.get(z).getMenuId() == menuId.get(x)) {
                        listDataHeaderUser.add(listDataHeader.get(z));
                    }
                }
            }

            for (int z = 0; z < listDataHeaderUser.size(); z++) {

                if (listDataChild.get(listDataHeaderUser.get(z)) != null) {
                    List<ExpandedSubmenu> orderChildUser = new ArrayList<>();
                    for (int i = 0; i < listDataChild.get(listDataHeaderUser.get(z)).size(); i++) {

                        for (int x = 0; x < menuId.size(); x++) {

                            if (listDataChild.get(listDataHeaderUser.get(z)).get(i).getMenuId() == menuId.get(x)) {
                                orderChildUser.add(listDataChild.get(listDataHeaderUser.get(z)).get(i));
                            }

                            listDataChildUser.put(listDataHeaderUser.get(z), orderChildUser);
                        }
                    }

                    listDataChildUser.put(listDataHeaderUser.get(z), orderChildUser);
                }
            }
        }
    }

    private void onNavigationItemSelectedId (int id) {
        Intent intent = null;

        //Ditambahkan pada 21 Nov 2017
        switch (id) {
            case 214:
                intent = new Intent(PencegahanActivity.this, KegiatanRapatKoordinasiActivity.class);
                break;
            case 215:
                intent = new Intent(PencegahanActivity.this, KegiatanMembangunJejaringActivity.class);
                break;
            case 216:
                intent = new Intent(PencegahanActivity.this, KegiatanAsistensiActivity.class);
                break;
            case 217:
                intent = new Intent(PencegahanActivity.this, KegiatanIntervensiActivity.class);
                break;
            case 218:
                intent = new Intent(PencegahanActivity.this, KegiatanSupervisiActivity.class);
                break;
            case 219:
                intent = new Intent(PencegahanActivity.this, KegiatanMonitoringdanEvaluasiActivity.class);
                break;
            case 220:
                intent = new Intent(PencegahanActivity.this, KegiatanBimbinganTeknisActivity.class);
                break;
            case 221:
                intent = new Intent(PencegahanActivity.this, KegiatanSosialisasiActivity.class);
                break;
            case 222:
                intent = new Intent(PencegahanActivity.this, SosialisasiViaMediaOnlineActivity.class);
                break;
            case 223:
                intent = new Intent(PencegahanActivity.this, SosialisasiViaMediaPenyiaranActivity.class);
                break;
            case 224:
                intent = new Intent(PencegahanActivity.this, SosialisasiViaMediaCetakActivity.class);
                break;
            case 225:
                intent = new Intent(PencegahanActivity.this, SosialisasiViaMediaKonvensionalActivity.class);
                break;
        }

        startActivity(intent);
        finish();
    }

    private void showToast(int dur, String msg) {
        Toast.makeText(this, msg, dur == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PencegahanActivity.this, DashboardActivity.class));
        finish();
    }
}
