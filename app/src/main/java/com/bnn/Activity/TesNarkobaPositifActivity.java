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
import com.bnn.Adapter.ListMenuTesUrineAdapter;
import com.bnn.Modal.Listmenu;
import com.bnn.R;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.PreferenceUtils;
import com.bnn.Utils.Configuration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by USER on 10/25/2017.
 */

public class TesNarkobaPositifActivity extends ActivityBase implements View.OnClickListener {
    private static final String TAG = TesNarkobaPositifActivity.class.getSimpleName();
    //DiPerbaharui pada 14 Nov 2017
    ListView menulist;
    ImageView imgToogle,imgtambah, imgBack;
    Spinner spinnerlist;
    Button btnCari;
    int id;
    LinearLayout pengisian,tanggal, linearSearch, footer;
    TextView txttanggalberfore, txttanggalafter, txtinput;
    EditText edKataKunci;
    Button btnsearch;
    PreferenceUtils prefUtils;
    ArrayList<Listmenu> menulists = new ArrayList<>();
    ListMenuTesUrineAdapter adapter;

    //Ditambahkan pada 03 Nov 2017
    Calendar cal;
    String now = "";
    ProgressBar progressBar;

    String modeCari = "";
    String tglFrom, tglTo, katakunci;

    boolean isLoadMore = false, isLast = false;
    int pageRequest = 1, index = 0;

    private int preLast;

    int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount, currentScrollState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_list_tesnarkobapositif, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);

        tanggal = (LinearLayout) findViewById(R.id.lineartanggal);
        pengisian = (LinearLayout) findViewById(R.id.linearinputanlist);
        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imgtambah = (ImageView) findViewById(R.id.imageadd);
        imgBack = (ImageView) findViewById(R.id.imageback);
        spinnerlist = (Spinner) findViewById(R.id.spinnersearch);
        edKataKunci = (EditText) findViewById(R.id.edKataKunci);
        btnCari = (Button) findViewById(R.id.button_search);
        linearSearch = (LinearLayout) findViewById(R.id.linearbuttonsearch);
        txttanggalberfore = (EditText) findViewById(R.id.texttanggalbefore);
        txttanggalafter = (EditText) findViewById(R.id.texttanggalafter);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        menulist = (ListView) findViewById(R.id.listview_tesnarkobapositif);
        footer = (LinearLayout) this.getLayoutInflater().inflate(R.layout.footer_loadmore, menulist, false);


        ArrayList<String> searchlist = new ArrayList<>(Arrays.asList(
                EnumSearch.PILIH.toString(),
                EnumSearch.SEMUA.toString(),
                EnumSearch.TANGGAL.toString(),
                EnumSearch.LOKASI.toString()));

        ArrayAdapter<String> searchByAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, searchlist);
        spinnerlist.setAdapter(searchByAdapter);

        imgtambah.setOnClickListener(this);

        //Ditambahkan pada 03 nov 2017
        txttanggalberfore.setOnClickListener(this);
        txttanggalafter.setOnClickListener(this);
        btnCari.setOnClickListener(this);
        imgBack.setOnClickListener(this);

//        menulists.add(new Listmenu("03 November 2017", "SASARAN INFO", "Pelaksana", "-", "JAKARTA", ""));

//        adapter = new ListMenuAdapter(TesNarkobaPositifActivity.this, menulists);
//        menulist.setAdapter(adapter);

        menulist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String eventID = adapter.getItem(i).getNoid();
                String pelaksana = adapter.getItem(i).getJenisbarang();

                Intent detilTesNarkoba = new Intent(TesNarkobaPositifActivity.this, DetilTesNarkobaPositifActivity.class);
                detilTesNarkoba.putExtra("header_id", eventID);
                detilTesNarkoba.putExtra("nm_instansi", pelaksana);
                startActivity(detilTesNarkoba);
                finish();
            }
        });

        spinnerlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (view != null) {
                    String selected = ((TextView) view).getText().toString();

                    modeCari = selected;

                    if (selected.equalsIgnoreCase(EnumSearch.PELAKSANA.toString()) ||
                            selected.equalsIgnoreCase(EnumSearch.PENYIDIK.toString()) ||
                            selected.equalsIgnoreCase(EnumSearch.LOKASI.toString())) {

                        edKataKunci.setText("");
                        katakunci = "";

                        tanggal.setVisibility(View.GONE);
                        pengisian.setVisibility(View.VISIBLE);
                        linearSearch.setVisibility(View.VISIBLE);

                        isLoadMore = false;
                        pageRequest = 1;
                    } else if (selected.equalsIgnoreCase(EnumSearch.TANGGAL.toString())) {
                        txttanggalberfore.setText("");
                        txttanggalafter.setText("");
                        tglFrom = "";
                        tglTo = "";

                        pengisian.setVisibility(View.GONE);
                        tanggal.setVisibility(View.VISIBLE);
                        linearSearch.setVisibility(View.VISIBLE);

                        isLoadMore = false;
                        pageRequest = 1;
                    }  else if (selected.equalsIgnoreCase(EnumSearch.PILIH.toString())) {
                        pengisian.setVisibility(View.GONE);
                        tanggal.setVisibility(View.GONE);
                        linearSearch.setVisibility(View.GONE);

                        isLoadMore = false;
                        pageRequest = 1;
                    } else if (selected.equalsIgnoreCase(EnumSearch.SEMUA.toString())) {
                        pengisian.setVisibility(View.GONE);
                        tanggal.setVisibility(View.GONE);
                        linearSearch.setVisibility(View.VISIBLE);

                        isLoadMore = false;
                        pageRequest = 1;
                    }
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
                if (menulist.getFooterViewsCount() == 0 && menulists.size() >= 10 && !isLast) {
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

//        menulist.addFooterView(footer);

        loadData();
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

    private void loadData() {
        if (Configuration.isDeviceOnline(this)) {
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
        cal = Calendar.getInstance();
        now = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

        switch (view.getId()) {
            case R.id.imageadd:
                Intent y = new Intent(TesNarkobaPositifActivity.this, TambahTesNarkobaPositifActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.button_search:
                if (Configuration.isDeviceOnline(TesNarkobaPositifActivity.this)) {
                    expandList.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    if (menulist.getFooterViewsCount() == 1) {
                        menulist.removeFooterView(footer);
                    }

                    if (modeCari.equalsIgnoreCase(EnumSearch.LOKASI.toString())) {

                        if (edKataKunci.getText().toString().equals("")) {
                            showToast("Masukan kata kunci pencarian.");
                        } else if (!edKataKunci.getText().toString().equals("")) {
                            if (menulists.size() > 0) {
                                menulists.clear();
                                adapter.clear();
                            }

                            DataUploadTask dataUploadTask = new DataUploadTask();
                            dataUploadTask.execute();
                        }
                    } else if (modeCari.equalsIgnoreCase(EnumSearch.TANGGAL.toString())) {
                        if (txttanggalberfore.getText().toString().equals("")) {
                            showToast("Pilih tanggal awal.");
                        } else if (txttanggalberfore.getText().toString().equals("")) {
                            showToast("Pilih tanggal akhir.");
                        } else if (!txttanggalberfore.getText().toString().equals("") && !txttanggalafter.getText().toString().equals("")) {
                            if (menulists.size() > 0) {
                                menulists.clear();
                                adapter.clear();
                            }

                            DataUploadTask dataUploadTask = new DataUploadTask();
                            dataUploadTask.execute();
                        }
                    } else {
                        if (menulists.size() > 0) {
                            menulists.clear();
                            adapter.clear();
                        }

                        DataUploadTask dataUploadTask = new DataUploadTask();
                        dataUploadTask.execute();
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
            case R.id.imageback:
                Intent dashboardBack = new Intent(TesNarkobaPositifActivity.this, PemberdayaanActivity.class);
                startActivity(dashboardBack);
                finish();
                break;
            case R.id.texttanggalbefore:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TesNarkobaPositifActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        tglFrom = sDate;
                        txttanggalberfore.setText(getNewDate(sDate));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                //                    dpd.getDatePicker().setMaxDate(new SimpleDateFormat("yyyy-MM-dd").parse(now).getTime());
                dpd.show();
                break;
            case R.id.texttanggalafter:
                Calendar calendar1 = Calendar.getInstance();
                DatePickerDialog dpd1 = new DatePickerDialog(TesNarkobaPositifActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        tglTo = sDate;
                        txttanggalafter.setText(getNewDate(sDate));
                    }
                }, calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
                //                    dpd1.getDatePicker().setMaxDate(new SimpleDateFormat("yyyy-MM-dd").parse(now).getTime());
                dpd1.show();
                break;
        }
    }

    public String getNewDate(String date){
        String newDate = "01-01-2016";
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

//    public String getFormattedDate(String date){
//        String newDate = "01 Jan 2016";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date d = simpleDateFormat.parse(date);
//            SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yy");
//            newDate = newFormat.format(d);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return newDate;
//    }

    class DataUploadTask extends AsyncTask<Bitmap, Integer, String> {
        private String responseServer;
        private String keyKataKunci;

        public DataUploadTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseServer = "";

            modeCari.equalsIgnoreCase(EnumSearch.LOKASI.toString());
                keyKataKunci = "lokasi";

            katakunci = edKataKunci.getText().toString().toUpperCase();

            if (!isLoadMore) {
                progressBar.setVisibility(View.VISIBLE);
                expandList.setVisibility(View.GONE);
            } else {
//                menulist.addFooterView(footer);
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
            } else if (modeCari.equalsIgnoreCase(EnumSearch.LOKASI.toString())) {
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
                    .url(Configuration.baseUrl+"/api/listtesnarkoba")
                    //.url(Configuration.DEV_URL_OSB + "/dayamas/tesnarkoba")
                    .addHeader("Authorization", "Bearer "+ prefUtils.getTokenKey())
                    .post(requestBody)
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
            Log.d("test", result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
                    JSONArray data = jsonObject.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject list = data.getJSONObject(i);

                        menulists.add(new Listmenu(list.getString("tgl_test"),
                                list.getString("jmlh_peserta"),
                                list.getString("jmlh_positif"),
                                list.getString("header_id"),
                                list.getString("lokasi"),
                                ""));

//                        Log.d("cobadatalist","data : " + data.toString() + "\ntgl : " + list.getString("create_date")
//                                + "\nlkn : " + list.getString("id_brgbukti") + "\nbarangbukti : " + list.getString("peserta_inisial")
//                                + "\nlokasi : " + list.getString("peserta_id"));
                    }

                    if (isLoadMore) {
                        isLoadMore = false;

                        adapter.notifyDataSetChanged();

                        if (data.length() == 0) {
                            menulist.removeFooterView(footer);
                            isLast = true;
                        } else {
                            isLast = false;
                        }

                        if (menulists.size() >= 10 && menulist.getFooterViewsCount() == 0 && data.length() > 0) {
                            menulist.addFooterView(footer);
                        }
                    } else {
                        adapter = new ListMenuTesUrineAdapter(TesNarkobaPositifActivity.this, menulists);
                        menulist.setAdapter(adapter);

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

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TesNarkobaPositifActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TesNarkobaPositifActivity.this, LoginActivity.class);
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

                    Toast.makeText(TesNarkobaPositifActivity.this, "Failed detect.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                progressBar.setVisibility(View.GONE);
                loadData();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TesNarkobaPositifActivity.this, PemberdayaanActivity.class));
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
