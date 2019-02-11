package com.bnn.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bnn.Activity.AlihFungsiLahanActivity;
import com.bnn.Activity.DashboardActivity;
import com.bnn.Activity.KegiatanAsistensiActivity;
import com.bnn.Activity.KegiatanBimbinganTeknisActivity;
import com.bnn.Activity.KegiatanIntervensiActivity;
import com.bnn.Activity.KegiatanMembangunJejaringActivity;
import com.bnn.Activity.KegiatanMonitoringdanEvaluasiActivity;
import com.bnn.Activity.KegiatanRapatKoordinasiActivity;
import com.bnn.Activity.KegiatanSosialisasiActivity;
import com.bnn.Activity.KegiatanSupervisiActivity;
import com.bnn.Activity.LoginActivity;
import com.bnn.Activity.PelatihanKonselorActivity;
import com.bnn.Activity.PemberantasanActivity;
import com.bnn.Activity.PemberdayaanActivity;
import com.bnn.Activity.PemusnahanBarangBuktiActivity;
import com.bnn.Activity.PenangananKasusNarkobaActivity;
import com.bnn.Activity.PencegahanActivity;
import com.bnn.Activity.RaziaActivity;
import com.bnn.Activity.RehabilitasiActivity;
import com.bnn.Activity.SosialisasiViaMediaCetakActivity;
import com.bnn.Activity.SosialisasiViaMediaKonvensionalActivity;
import com.bnn.Activity.SosialisasiViaMediaOnlineActivity;
import com.bnn.Activity.SosialisasiViaMediaPenyiaranActivity;
import com.bnn.Activity.TesNarkobaPositifActivity;
import com.bnn.R;
import com.bnn.Adapter.MenuExpandableListAdapter;
import com.bnn.Modal.ExpandedMenuModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ActivityBase extends AppCompatActivity {
    private static final String TAG = ActivityBase.class.getSimpleName();

    private PreferenceUtils prefUtils;
    private ImageView imgPhoto, imgCloseMenu, imgClosePopup;
    private TextView txtName, txtRank;
    //Ditambahkan pada 03 Nov 2017
    private Button btnTidak, btnYa;

    private String name, rank, image, userId, menu;
    protected LinearLayout linearLayout, linearLayout2;
    protected DrawerLayout menuDrawerLayout;
    protected ExpandableListView expandList;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;

    private List<ExpandedMenuModel> listDataHeaderUser;
    private HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChildUser;
    private MenuExpandableListAdapter adapter;

    private List<Integer> listMenu = new ArrayList<>();
    private String menuUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_menu);

        prefUtils = PreferenceUtils.getInstance(this);

        userId = prefUtils.getId() != null && !prefUtils.getId().equals("") ? prefUtils.getId() : "";;
        name = prefUtils.getName() != null && !prefUtils.getName().equals("") ? prefUtils.getName() : "";
        rank = prefUtils.getRank() != null && !prefUtils.getRank().equals("") ? prefUtils.getRank() : "";
        image = prefUtils.getImage() != null && !prefUtils.getImage().equals("") ? prefUtils.getImage() : "";
        menu = prefUtils.getMenu() != null && !prefUtils.getMenu().equals("") ? prefUtils.getMenu() : "";
        menuUser = prefUtils.getUserMenu() != null && !prefUtils.getUserMenu().equals("") ? prefUtils.getUserMenu() : "";

        linearLayout = (LinearLayout)findViewById(R.id.main_container);
        linearLayout2 = (LinearLayout)findViewById(R.id.linear1);
        menuDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        expandList = (ExpandableListView) findViewById(R.id.navigationmenu);

        if (!menuUser.equalsIgnoreCase("")) {
            for (int i = 0; i < menuUser.split(",").length; i++) {
                listMenu.add(Integer.valueOf(menuUser.split(",")[i]));
            }
        }

        listMenu.add(226);
        listDataUser(expandList, listMenu);

        expandList.addHeaderView(headerView(expandList));

        adapter = new MenuExpandableListAdapter(this, listDataHeaderUser, listDataChildUser);
        expandList.setAdapter(adapter);

        expandList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPos, int childPos, long l) {
                ExpandedMenuModel menuModelGroup = (ExpandedMenuModel) adapter.getGroup(groupPos);
                ExpandedMenuModel menuModelChild = (ExpandedMenuModel) adapter.getChild(groupPos, childPos);

                Log.d(TAG, "Menu id: "+menuModelGroup.getMenuId()+" "+menuModelChild.getMenuId());

                switch (menuModelGroup.getMenuId()) {
                    case 202:
                        switch (menuModelChild.getMenuId()) {
                            case 206:
                                Intent penangananKasus = new Intent(ActivityBase.this, PenangananKasusNarkobaActivity.class);
                                startActivity(penangananKasus);
                                finish();
                                break;
                            case 207:
                                Intent pemusnahanBarangBukti = new Intent(ActivityBase.this, PemusnahanBarangBuktiActivity.class);
                                startActivity(pemusnahanBarangBukti);
                                finish();
                                break;
                            case 208:
                                Intent razia = new Intent(ActivityBase.this, RaziaActivity.class);
                                startActivity(razia);
                                finish();
                                break;
                        }
                        break;
                    case 203:
                        switch (menuModelChild.getMenuId()) {
                            case 209:
                                Intent tesNarkobaPositif = new Intent(ActivityBase.this, TesNarkobaPositifActivity.class);
                                startActivity(tesNarkobaPositif);
                                finish();
                                break;
                            case 210:
                                Intent alihFungsiLahan = new Intent(ActivityBase.this, AlihFungsiLahanActivity.class);
                                startActivity(alihFungsiLahan);
                                finish();
                                break;
                        }
                        break;
                    case 204:
                        switch (menuModelChild.getMenuId()) {
                            case 214:
                                Intent rakor = new Intent(ActivityBase.this, KegiatanRapatKoordinasiActivity.class);
                                startActivity(rakor);
                                finish();
                                break;
                            case 215:
                                Intent jejaring = new Intent(ActivityBase.this, KegiatanMembangunJejaringActivity.class);
                                startActivity(jejaring);
                                finish();
                                break;
                            case 216:
                                Intent asistensi = new Intent(ActivityBase.this, KegiatanAsistensiActivity.class);
                                startActivity(asistensi);
                                finish();
                                break;
                            case 217:
                                Intent intervensi = new Intent(ActivityBase.this, KegiatanIntervensiActivity.class);
                                startActivity(intervensi);
                                finish();
                                break;
                            case 218:
                                Intent supervisi = new Intent(ActivityBase.this, KegiatanSupervisiActivity.class);
                                startActivity(supervisi);
                                finish();
                                break;
                            case 219:
                                Intent monev = new Intent(ActivityBase.this, KegiatanMonitoringdanEvaluasiActivity.class);
                                startActivity(monev);
                                finish();
                                break;
                            case 220:
                                Intent bimtek = new Intent(ActivityBase.this, KegiatanBimbinganTeknisActivity.class);
                                startActivity(bimtek);
                                finish();
                                break;
                            case 221:
                                Intent sosialisasi = new Intent(ActivityBase.this, KegiatanSosialisasiActivity.class);
                                startActivity(sosialisasi);
                                finish();
                                break;
                            case 222:
                                Intent mediaOnline = new Intent(ActivityBase.this, SosialisasiViaMediaOnlineActivity.class);
                                startActivity(mediaOnline);
                                finish();
                                break;
                            case 223:
                                Intent penyiaran = new Intent(ActivityBase.this, SosialisasiViaMediaPenyiaranActivity.class);
                                startActivity(penyiaran);
                                finish();
                                break;
                            case 224:
                                Intent cetak = new Intent(ActivityBase.this, SosialisasiViaMediaCetakActivity.class);
                                startActivity(cetak);
                                finish();
                                break;
                            case 225:
                                Intent konvensional = new Intent(ActivityBase.this, SosialisasiViaMediaKonvensionalActivity.class);
                                startActivity(konvensional);
                                finish();
                                break;
                        }
                        break;
                    case 205:
                        switch (menuModelChild.getMenuId()) {
                            case 213:
                                Intent konselor = new Intent(ActivityBase.this, PelatihanKonselorActivity.class);
                                startActivity(konselor);
                                finish();
                                break;
                        }
                        break;
                }

                return false;
            }
        });

        expandList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

                //Ditambahkan pada 03 Nov 2017
                //Fungsi ini dimaksudkan untuk handle ketika user memeilih menu pada list tampilan
                ExpandedMenuModel menuModel = (ExpandedMenuModel) adapter.getGroup(i);

                switch (menuModel.getMenuId()) {
                    case 201:
                        Intent dashboardIntent = new Intent(ActivityBase.this, DashboardActivity.class);
                        startActivity(dashboardIntent);
                        finish();
                        break;
                    case 202:
                        Intent pemberantasanIntent = new Intent(ActivityBase.this, PemberantasanActivity.class);
                        startActivity(pemberantasanIntent);
                        finish();
                        break;
                    case 203:
                        Intent pemberdayaanIntent = new Intent(ActivityBase.this, PemberdayaanActivity.class);
                        startActivity(pemberdayaanIntent);
                        finish();
                        break;
                    case 204:
                        Intent pencegahanIntent = new Intent(ActivityBase.this, PencegahanActivity.class);
                        startActivity(pencegahanIntent);
                        finish();
                        break;
                    case 205:
                        Intent rehabilitasiIntent = new Intent(ActivityBase.this, RehabilitasiActivity.class);
                        startActivity(rehabilitasiIntent);
                        finish();
                        break;
                    case 226:
                        prefUtils.clearPref();

                        menuDrawerLayout.closeDrawers();

                        View dialogView = getLayoutInflater().inflate(R.layout.popup_logout, null);
                        final AlertDialog alertDialog = new AlertDialog.Builder(ActivityBase.this)
                                .setCancelable(false)
                                .setView(dialogView).create();
                        initDialogView(dialogView);

                        alertDialog.show();
                        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        btnTidak.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        btnYa.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent logoutIntent = new Intent(ActivityBase.this, LoginActivity.class);
                                startActivity(logoutIntent);
                                finish();
                            }
                        });

                        imgClosePopup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        break;
                }

                return false;
            }
        });

        if (userId.equals("")) {
            new DataUploadTask().execute();
        }
    }

    //Inflate setiap components ui yang akan kita implement di alert dialog
    private void initDialogView(View dialogView) {
        btnTidak = (Button) dialogView.findViewById(R.id.btnTidak);
        btnYa = (Button) dialogView.findViewById(R.id.btnYa);
        imgClosePopup = (ImageView) dialogView.findViewById(R.id.imgClosePopup);
    }

    private void listDataUser(ExpandableListView expandList, List<Integer> menuId) {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataHeaderUser = new ArrayList<>();
        listDataChildUser = new HashMap<>();

        listDataHeader.add(new ExpandedMenuModel(201, "MENU UTAMA", R.drawable.icon_home));
        listDataHeader.add(new ExpandedMenuModel(202, "PEMBERANTASAN", R.drawable.icon_pemberantasan));
        listDataHeader.add(new ExpandedMenuModel(203, "PEMBERDAYAAN MASYARAKAT", R.drawable.icon_pemberdayaan));
        listDataHeader.add(new ExpandedMenuModel(204, "PENCEGAHAN", R.drawable.icon_pencegahan));
        listDataHeader.add(new ExpandedMenuModel(205, "REHABILITASI", R.drawable.icon_rehabilitasi));
        listDataHeader.add(new ExpandedMenuModel(226, "KELUAR APLIKASI", R.drawable.icon_exit));

        List<ExpandedMenuModel> orderChild = new ArrayList<ExpandedMenuModel>();
        orderChild.add(new ExpandedMenuModel(206, "Penanganan Kasus Narkoba", -1));
        orderChild.add(new ExpandedMenuModel(207, "Pemusnahan Barang Bukti", -1));
        orderChild.add(new ExpandedMenuModel(208, "Razia", -1));
//        listDataChild.put(listDataHeader.get(1), orderChild);

        List<ExpandedMenuModel> orderChild3 = new ArrayList<ExpandedMenuModel>();
        orderChild3.add(new ExpandedMenuModel(209, "Tes Urine", -2));
        orderChild3.add(new ExpandedMenuModel(210, "Alih Fungsi Peninjauan Lahan", -2));
//        listDataChild.put(listDataHeader.get(2), orderChild3);

        List<ExpandedMenuModel> orderChild2 = new ArrayList<ExpandedMenuModel>();
        orderChild2.add(new ExpandedMenuModel(214, "Kegiatan Rapat Koordinasi", -3));
        orderChild2.add(new ExpandedMenuModel(215, "Kegiatan Membangun Jejaring", -3));
        orderChild2.add(new ExpandedMenuModel(216, "Kegiatan Asistensi", -3));
        orderChild2.add(new ExpandedMenuModel(217, "Kegiatan Intervensi", -3));
        orderChild2.add(new ExpandedMenuModel(218, "Kegitan Supervisi", -3));
        orderChild2.add(new ExpandedMenuModel(219, "Kegiatan Monitoring dan Evaluasi", -3));
        orderChild2.add(new ExpandedMenuModel(220, "Kegiatan Bimbingan Teknis", -3));
        orderChild2.add(new ExpandedMenuModel(221, "Kegiatan KIE", -3));

        //Edit on 18 November 2017
        orderChild2.add(new ExpandedMenuModel(222, "Media Online", -3));
        orderChild2.add(new ExpandedMenuModel(223, "Media Penyiaran", -3));
        orderChild2.add(new ExpandedMenuModel(224, "Media Cetak", -3));
        orderChild2.add(new ExpandedMenuModel(225, "Media Konvensional", -3));
//        listDataChild.put(listDataHeader.get(3), orderChild2);

        List<ExpandedMenuModel> orderChild4 = new ArrayList<ExpandedMenuModel>();
//        orderChild4.add(new ExpandedMenuModel(213, "Pelatihan / Bimtek / Monev", -4));
        orderChild4.add(new ExpandedMenuModel(213, "Kegiatan", -4));
//        listDataChild.put(listDataHeader.get(4), orderChild4);

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
                    List<ExpandedMenuModel> orderChildUser = new ArrayList<ExpandedMenuModel>();
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

    public View headerView(ExpandableListView expandableListView) {
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.nav_header_home, expandableListView, false);

        imgPhoto = (ImageView) header.findViewById(R.id.profile_image);
        txtName = (TextView) header.findViewById(R.id.username);
        txtRank = (TextView) header.findViewById(R.id.userjabatan);

        //Ditambahkan pada 03 Nov 2017
        imgCloseMenu = (ImageView) header.findViewById(R.id.imgCloseMenu);

        txtName.setText(name.toUpperCase());
        txtRank.setText(rank);

        if (!image.equals("")) {
            loadFoto(image, imgPhoto);
        } else {
            if (Configuration.isDeviceOnline(this)) {
                new DataUploadTask().execute();
            }
        }

        //Ditambahkan pada 03 Nov 2017
        //Berfungsi untuk close slide menu
        imgCloseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDrawerLayout.closeDrawers();
            }
        });

        return header;
    }

    class DataUploadTask extends AsyncTask<Bitmap, Integer, String> {
        private String responseServer;

        public DataUploadTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseServer = "";
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/users/"+userId)
                    .addHeader("Authorization", "Bearer " + prefUtils.getTokenKey())
                    .build();
            Response httpResponse = null;

            try {
                httpResponse = httpclient.newCall(httpRequest).execute();
            }  catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (httpResponse != null) {
                    responseServer = httpResponse.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseServer;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "Result: "+result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
                    JSONObject data = jsonObject.getJSONObject("data");

                    prefUtils.setImage(!data.getString("foto_pegawai").equalsIgnoreCase("null") ? data.getString("foto_pegawai") : "");

                    loadFoto(!data.getString("foto_pegawai").equalsIgnoreCase("null") ? data.getString("foto_pegawai") : "", imgPhoto);
                } else {
                    prefUtils.setIsLogin(false);

                    if (jsonObject.getString("status").equalsIgnoreCase("Error")) {
                        showToast(1, jsonObject.getString("comment")+".");
                    } else {
                        showToast(1, "Maaf terjadi kesalahan, mohon coba beberapa saat lagi.");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadFoto(String base64, ImageView fotoFrame) {
        if (!base64.equalsIgnoreCase("")) {
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            fotoFrame.setImageBitmap(decodedByte);
        }
    }

    private void showToast(int dur, String msg) {
        Toast.makeText(this, msg, dur == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public DrawerLayout getDrawerLayout(){
        return menuDrawerLayout;
    }
}
