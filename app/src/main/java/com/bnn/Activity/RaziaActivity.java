package com.bnn.Activity;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bnn.Adapter.EnumSearch;
import com.bnn.Adapter.ListMenuAdapter;
import com.bnn.Adapter.ListMenuPemusnahanBarbukAdapter;
import com.bnn.Adapter.ListMenuRaziaAdapter;
import com.bnn.Modal.Listmenu;
import com.bnn.R;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.Configuration;
import com.bnn.Utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by USER on 10/25/2017.
 */

public class RaziaActivity extends ActivityBase implements View.OnClickListener {
    public static final String TAG = RaziaActivity.class.getSimpleName();

    //Ditambahkan pada 07 Nov 2017
    ListView menulist;
    ImageView imgToogle, imgTambah, imgBack;
    Spinner spinnerCari;
    int id;
    LinearLayout linearLayoutPengisian, linearLayoutTanggal, linearLayoutSearch, footer;
    TextView edTanggalBefore, edTanggalAfter, edKataKunci;
    Button btnCari;
    ProgressBar progressBar;

    PreferenceUtils prefUtils;
    ArrayList<Listmenu> menuLists = new ArrayList<>();
    ListMenuRaziaAdapter listMenuAdapter;

    String modeCari = "";
    String tglFrom, tglTo, katakunci;

    String dataRazia;

    private boolean isLoadMore = false, isLast = false;
    private int pageRequest = 1;

    private int preLast;
    int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount, currentScrollState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_list_razia, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);

        Log.d(TAG, "BaseUrl: "+Configuration.baseUrl);

        dataRazia = prefUtils.getRazia() != null && !prefUtils.getRazia().equals("") ? prefUtils.getRazia() : "";

        linearLayoutTanggal = (LinearLayout) findViewById(R.id.linearTanggal);
        linearLayoutPengisian = (LinearLayout) findViewById(R.id.linearInputan);
        linearLayoutSearch = (LinearLayout) findViewById(R.id.linearButtonSearch);
        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imgTambah = (ImageView) findViewById(R.id.imageAdd);
        imgBack = (ImageView) findViewById(R.id.imageBack);
        spinnerCari = (Spinner) findViewById(R.id.spinnerSearch);
        btnCari = (Button) findViewById(R.id.buttonSearch);
        edTanggalBefore = (EditText) findViewById(R.id.edTanggalBefore);
        edTanggalAfter = (EditText) findViewById(R.id.edTanggalAfter);
        edKataKunci = (EditText) findViewById(R.id.edKataKunci);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        menulist = (ListView) findViewById(R.id.listViewRazia);
        footer = (LinearLayout) this.getLayoutInflater().inflate(R.layout.footer_loadmore, menulist, false);

        ArrayList<String> searchlist = new ArrayList<>(Arrays.asList(
                EnumSearch.PILIH.toString(),
                EnumSearch.SEMUA.toString(),
                EnumSearch.TANGGAL.toString(),
                EnumSearch.LOKASI.toString()));

        ArrayAdapter<String> searchByAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, searchlist);
        spinnerCari.setAdapter(searchByAdapter);

        imgTambah.setOnClickListener(this);

        //Ditambahkan pada 04 nov 2017
        edTanggalAfter.setOnClickListener(this);
        edTanggalBefore.setOnClickListener(this);
        btnCari.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        menulist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String raziaId = listMenuAdapter.getItem(i).getNoid();

                Intent detilRazia = new Intent(RaziaActivity.this, DetilRaziaActivity.class);
                detilRazia.putExtra("razia_id", raziaId);
                startActivity(detilRazia);
                finish();
            }
        });

        spinnerCari.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (view != null) {
                    String selected = ((TextView) view).getText().toString();

                    modeCari = selected;

                    if (selected.equalsIgnoreCase(EnumSearch.PELAKSANA.toString()) ||
                            selected.equalsIgnoreCase(EnumSearch.PENYIDIK.toString()) ||
                            selected.equalsIgnoreCase(EnumSearch.WILAYAH.toString()) ||
                            selected.equalsIgnoreCase(EnumSearch.LOKASI.toString()) ||
                            selected.equalsIgnoreCase(EnumSearch.JENIS_KASUS.toString())) {
                        linearLayoutTanggal.setVisibility(View.GONE);
                        linearLayoutPengisian.setVisibility(View.VISIBLE);
                        linearLayoutSearch.setVisibility(View.VISIBLE);
                    } else if (selected.equalsIgnoreCase(EnumSearch.TANGGAL.toString())) {
                        linearLayoutPengisian.setVisibility(View.GONE);
                        linearLayoutTanggal.setVisibility(View.VISIBLE);
                        linearLayoutSearch.setVisibility(View.VISIBLE);

                        tglFrom = "";
                        tglTo = "";
                        edTanggalBefore.setText("");
                        edTanggalAfter.setText("");
                    } else if (selected.equalsIgnoreCase(EnumSearch.SEMUA.toString())) {
                        linearLayoutPengisian.setVisibility(View.GONE);
                        linearLayoutTanggal.setVisibility(View.GONE);
                        linearLayoutSearch.setVisibility(View.VISIBLE);
                    } else if (selected.equalsIgnoreCase(EnumSearch.PILIH.toString())) {
                        linearLayoutPengisian.setVisibility(View.GONE);
                        linearLayoutTanggal.setVisibility(View.GONE);
                        linearLayoutSearch.setVisibility(View.GONE);
                    }

//                    if (selected.equalsIgnoreCase(EnumSearch.PILIH.toString())) {
//                        linearLayoutPengisian.setVisibility(View.GONE);
//                        linearLayoutTanggal.setVisibility(View.GONE);
//                        linearLayoutSearch.setVisibility(View.GONE);
//                    } else {
//                        showToast(1, "Pencarian belum tersedia untuk sementara.");
//                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imgToogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDrawerLayout.openDrawer(linearLayout2);
            }
        });

        menulist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (menulist.getFooterViewsCount() == 0 && menuLists.size() >= 10 && !isLast) {
                    menulist.addFooterView(footer);
                }

                currentScrollState = scrollState;
                if (scrollState == SCROLL_STATE_IDLE) {
                    isScrollCompleted();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItemCount = visibleItemCount;
                currentTotalItemCount = totalItemCount;
            }
        });

        loadData();
    }

    private void loadData() {
        if (Configuration.isDeviceOnline(this)) {
//            try {
//                JSONArray jsonArray = new JSONArray(dataRazia);
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                    menuLists.add(new Listmenu(!jsonObject.getString("tgl").equals("") ? jsonObject.getString("tgl") : "-",
//                            !jsonObject.getString("jumlah_orang").equals("") ? jsonObject.getString("jumlah_orang") : "-",
//                            !jsonObject.getString("jumlah_terindikasi").equals("") ? jsonObject.getString("jumlah_terindikasi") : "-",
//                            !jsonObject.getString("id").equals("") ? jsonObject.getString("id") : "-",
//                            !jsonObject.getString("lokasi").equals("") ? jsonObject.getString("lokasi") : "-",
//                            ""));
//
//                    Collections.sort(menuLists, new Comparator<Listmenu>() {
//                        @Override
//                        public int compare(Listmenu o1, Listmenu o2) {
//                            return o2.getNoid().compareToIgnoreCase(o1.getNoid());
//                        }
//                    });
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

//            listMenuAdapter = new ListMenuRaziaAdapter(RaziaActivity.this, menuLists);
//            menulist.setAdapter(listMenuAdapter);

            DataUploadTask dataUploadTask = new DataUploadTask();
            dataUploadTask.execute();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("Tidak dapat melakukan proses. Tolong cek koneksi anda dan coba lagi.")
                    .setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("COBA LAGI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            loadData();
                        }
                    });
            alert.create().show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageAdd:
                Intent y = new Intent(RaziaActivity.this, TambahRaziaActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.buttonSearch:
                if (Configuration.isDeviceOnline(RaziaActivity.this)) {

                    if (modeCari.equalsIgnoreCase(EnumSearch.PELAKSANA.toString()) ||
                            modeCari.equalsIgnoreCase(EnumSearch.LOKASI.toString())) {

                        if (edKataKunci.getText().toString().equals("")) {
                            showToast(1, "Masukkan kata kunci pencarian.");
                        } else if (!edKataKunci.getText().toString().equals("")) {
                            if (menuLists.size() > 0) {
                                menuLists.clear();
                                listMenuAdapter.clear();
                            }

                            DataUploadTask dataUploadTask = new DataUploadTask();
                            dataUploadTask.execute();
                        }
                    } else if (modeCari.equalsIgnoreCase(EnumSearch.TANGGAL.toString())) {

                        if (edTanggalBefore.getText().toString().equals("")) {
                            showToast(1, "Pilih tanggal awal.");
                        } else if (edTanggalAfter.getText().toString().equals("")) {
                            showToast(1, "Pilih tanggal akhir.");
                        } else if (!edTanggalBefore.getText().toString().equals("") && !edTanggalAfter.getText().toString().equals("")) {
                            if (menuLists.size() > 0) {
                                menuLists.clear();
                                listMenuAdapter.clear();
                            }

                            DataUploadTask dataUploadTask = new DataUploadTask();
                            dataUploadTask.execute();
                        }
                    } else {
                        if (menuLists.size() > 0) {
                            menuLists.clear();
                            listMenuAdapter.clear();
                        }
                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setMessage("Tidak dapat melakukan proses. Tolong cek koneksi anda dan coba lagi.")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alert.create().show();
                }

                break;
            case R.id.imageBack:
                Intent dashboardBack = new Intent(RaziaActivity.this, PemberantasanActivity.class);
                startActivity(dashboardBack);
                finish();
                break;
            case R.id.edTanggalBefore:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(RaziaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        edTanggalBefore.setText(getNewDate(sDate));
                        tglFrom = sDate;
                    }
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
                //                    dpd.getDatePicker().setMaxDate(new SimpleDateFormat("yyyy-MM-dd").parse(now).getTime());
                dpd.show();
                break;
            case R.id.edTanggalAfter:
                Calendar calendar1 = Calendar.getInstance();
                DatePickerDialog dpd1 = new DatePickerDialog(RaziaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        edTanggalAfter.setText(getNewDate(sDate));
                        tglTo = sDate;
                    }
                }, calendar1.get(java.util.Calendar.YEAR), calendar1.get(java.util.Calendar.MONTH), calendar1.get(java.util.Calendar.DAY_OF_MONTH));
                //                    dpd1.getDatePicker().setMaxDate(new SimpleDateFormat("yyyy-MM-dd").parse(now).getTime());
                dpd1.show();
                break;
        }
    }

    public String getNewDate(String date){
        String newDate = "01/01/16";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = simpleDateFormat.parse(date);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yy");
            newDate = newFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }


    class DataUploadTask extends AsyncTask<Bitmap, Integer, String> {
        private String responseServer;
        private String keyKataKunci;

        public DataUploadTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseServer = "";

            if (modeCari.equalsIgnoreCase(EnumSearch.PELAKSANA.toString())) {
                keyKataKunci = "pelaksana";
            } else if (modeCari.equalsIgnoreCase(EnumSearch.PENYIDIK.toString())) {
                keyKataKunci = "penyidik";
            } else if (modeCari.equalsIgnoreCase(EnumSearch.LOKASI.toString())) {
                keyKataKunci = "lokasi";
            } else if (modeCari.equalsIgnoreCase(EnumSearch.JENIS_KASUS.toString())) {
                keyKataKunci = "jenis";
            }

            katakunci = edKataKunci.getText().toString().toUpperCase();

            if (!isLoadMore) {
                progressBar.setVisibility(View.VISIBLE);
                expandList.setVisibility(View.GONE);
            }
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            RequestBody requestBody = null;

            if (modeCari.equalsIgnoreCase(EnumSearch.TANGGAL.toString())) {
                if (isLoadMore) {
                    pageRequest = pageRequest + 1;

                    requestBody = new FormBody.Builder()
                            .add("tgl_from", tglFrom)
                            .add("tgl_to", tglTo)
                            .add("page", String.valueOf(pageRequest))
                            .add("id_wilayah", prefUtils.getWilayah())
                            .build();
                } else {
                    requestBody = new FormBody.Builder()
                            .add("tgl_from", tglFrom)
                            .add("tgl_to", tglTo)
                            .add("id_wilayah", prefUtils.getWilayah())
                            .build();
                }
            } else if (modeCari.equalsIgnoreCase(EnumSearch.PELAKSANA.toString()) ||
                    modeCari.equalsIgnoreCase(EnumSearch.PENYIDIK.toString()) ||
                    modeCari.equalsIgnoreCase(EnumSearch.WILAYAH.toString()) ||
                    modeCari.equalsIgnoreCase(EnumSearch.LOKASI.toString()) ||
                    modeCari.equalsIgnoreCase(EnumSearch.JENIS_KASUS.toString())) {

                if (isLoadMore) {
                    pageRequest = pageRequest + 1;

                    requestBody = new FormBody.Builder()
                            .add(keyKataKunci, katakunci)
                            .add("page", String.valueOf(pageRequest))
                            .add("id_wilayah", prefUtils.getWilayah())
                            .build();
                } else {
                    requestBody = new FormBody
                            .Builder()
                            .add(keyKataKunci, katakunci)
                            .add("id_wilayah", prefUtils.getWilayah())
                            .build();
                }
            } else {
                if (isLoadMore) {
                    pageRequest = pageRequest + 1;

                    requestBody = new FormBody.Builder()
                            .add("page", String.valueOf(pageRequest))
                            .add("id_wilayah", prefUtils.getWilayah())
                            .build();
                } else {
                    requestBody = new FormBody.Builder()
                            .add("id_wilayah", prefUtils.getWilayah())
                            .build();
                }
            }

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/listberantasrazia")
                    .addHeader("Authorization", "Bearer "+ prefUtils.getTokenKey())
                    .post(requestBody)
                    .build();

            Log.d("Sini", "Sini"+tglFrom+" "+tglTo);

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

            Log.d(TAG, "Result: "+modeCari+" "+result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
                    JSONArray data = jsonObject.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject list = data.getJSONObject(i);

//                        String jumlah = list.getString("meta_brgbukti").equals("null") ? "-" : String.valueOf(new JSONArray(list.getString("meta_brgbukti")).length());

                        menuLists.add(new Listmenu(list.getString("tgl_razia"),
                                list.getString("jumlah_dirazia").equals("null") ? "": list.getString("jumlah_dirazia"),
                                list.getString("jumlah_terindikasi").equals("null") ? "": list.getString("jumlah_terindikasi"),
                                list.getString("id"),
                                list.getString("lokasi"),
                                "-",
                                list.getString("jumlah_ditemukan").equals("null") ? "": list.getString("jumlah_ditemukan"),
                                ""));
                    }

                    if (isLoadMore) {
                        isLoadMore = false;

                        listMenuAdapter.notifyDataSetChanged();

                        if (data.length() == 0) {
                            menulist.removeFooterView(footer);
                            isLast = true;
                        } else {
                            isLast = false;
                        }

                        if (menuLists.size() >= 10 && menulist.getFooterViewsCount() == 0 && data.length() > 0) {
                            menulist.addFooterView(footer);
                        }
                    } else {
                        listMenuAdapter = new ListMenuRaziaAdapter(RaziaActivity.this, menuLists);
                        menulist.setAdapter(listMenuAdapter);

                        progressBar.setVisibility(View.GONE);
                        expandList.setVisibility(View.VISIBLE);
                    }
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    if (isLoadMore) {
                        isLoadMore = false;
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RaziaActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(RaziaActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    }
                } else {
                    if (isLoadMore) {
                        isLoadMore = false;
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }

                    showToast(1, "Failed detect.");
                }
            } catch (JSONException e) {
                e.printStackTrace();

                progressBar.setVisibility(View.GONE);
                loadData();
            }
        }
    }

    private void isScrollCompleted() {
        final int lastItem = currentFirstVisibleItem + currentVisibleItemCount;

        if(lastItem == currentTotalItemCount) {
            if(preLast != lastItem) {
                preLast = lastItem;

                isLoadMore = true;

                new DataUploadTask().execute();
            }
        }
    }

    private void showToast(int dur, String msg) {
        Toast.makeText(this, msg, dur == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RaziaActivity.this, PemberantasanActivity.class));
        finish();
    }
}
