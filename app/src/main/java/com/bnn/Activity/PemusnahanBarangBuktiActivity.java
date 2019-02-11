package com.bnn.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.bnn.Modal.Listmenu;
import com.bnn.Modal.Lkn;
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
import java.util.Date;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ferdinandprasetyo on 11/4/17.
 */

public class PemusnahanBarangBuktiActivity extends ActivityBase implements View.OnClickListener {
    private static final String TAG = PemusnahanBarangBuktiActivity.class.getSimpleName();

    //Ditambahkan pada 04 Nov 2017
    private ListView menulist;
    private ImageView imgToogle, imgTambah, imgBack;
    private Spinner spinnerCari;
    int id;
    private LinearLayout linearLayoutPengisian, linearLayoutTanggal, linearLayoutSearch, footer;
    private EditText edTanggalBefore, edTanggalAfter, edKataKunci, editsearch;
    private Button btnCari;
    private PreferenceUtils prefUtils;
    private ArrayList<Listmenu> menuLists = new ArrayList<>();
    private ListMenuPemusnahanBarbukAdapter listMenuAdapter;
    private Calendar cal;
    private String now = "";
    private ProgressBar progressBar;

    private String modeCari = "", value;
    private String tglFrom, tglTo, katakunci;

    private boolean isLoadMore = false, isLast = false;
    private int pageRequest = 1, index = 0;
    JSONArray jarr;
    private int preLast;
    int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount, currentScrollState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_list_pemusnahanbarangbukti, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);

        linearLayoutTanggal = (LinearLayout) findViewById(R.id.linearTanggal);
        linearLayoutPengisian = (LinearLayout) findViewById(R.id.linearInputan);
        linearLayoutSearch = (LinearLayout) findViewById(R.id.linearButtonSearch);
        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imgTambah = (ImageView) findViewById(R.id.imageAdd);
        imgBack = (ImageView) findViewById(R.id.imageBack);
        spinnerCari = (Spinner) findViewById(R.id.spinnerSearch);
        btnCari = (Button) findViewById(R.id.button_search);
        edTanggalBefore = (EditText) findViewById(R.id.edTanggalBefore);
        edTanggalAfter = (EditText) findViewById(R.id.edTanggalAfter);
        edKataKunci = (EditText) findViewById(R.id.edKataKunci);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        menulist = (ListView) findViewById(R.id.listViewPemusnahanBarangBukti);
        footer = (LinearLayout) this.getLayoutInflater().inflate(R.layout.footer_loadmore, menulist, false);

        ArrayList<String> searchlist = new ArrayList<>(Arrays.asList(
                EnumSearch.PILIH.toString(),
                EnumSearch.SEMUA.toString(),
                EnumSearch.TANGGAL.toString()));

        ArrayAdapter<String> searchByAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, searchlist);
        spinnerCari.setAdapter(searchByAdapter);

        imgTambah.setOnClickListener(this);

        //Ditambahkan pada 04 nov 2017
        edTanggalAfter.setOnClickListener(this);
        edTanggalBefore.setOnClickListener(this);
        btnCari.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        editsearch = (EditText) findViewById(R.id.search);
        editsearch.setEnabled(false);
        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                listMenuAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        menulist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String pemusnahanId = listMenuAdapter.getItem(i).getNoid();
                String arrayBarbuk = listMenuAdapter.getItem(i).getArrayStringBarbuk();



                Intent detilKasus = new Intent(PemusnahanBarangBuktiActivity.this, PemusnahanBarangBuktiActivity2.class);
                detilKasus.putExtra("pemusnahan_id", listMenuAdapter.getItem(i).getNoid());
                detilKasus.putExtra("tanggal", listMenuAdapter.getItem(i).getTanggal());
                detilKasus.putExtra("no_lkn", listMenuAdapter.getItem(i).getJenisbarang());
                startActivity(detilKasus);
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

//        menulist.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (menulist.getFooterViewsCount() == 0 && menuLists.size() >= 10 && !isLast) {
//                    menulist.addFooterView(footer);
//                }
//
//                currentScrollState = scrollState;
//                if (scrollState == SCROLL_STATE_IDLE) {
//                    isScrollCompleted();
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                currentFirstVisibleItem = firstVisibleItem;
//                currentVisibleItemCount = visibleItemCount;
//                currentTotalItemCount = totalItemCount;
//            }
//        });

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
        switch (view.getId()) {
            case R.id.imageAdd:
                //Intent y = new Intent(PemusnahanBarangBuktiActivity.this, TambahPemusnahanBarangBuktiActivity.class);
//                Intent y = new Intent(PemusnahanBarangBuktiActivity.this, PilihLKNActivity.class);
//                startActivity(y);
//                finish();
                break;
            case R.id.button_search:
                if (Configuration.isDeviceOnline(PemusnahanBarangBuktiActivity.this)) {
                    expandList.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    if (menulist.getFooterViewsCount() == 1) {
                        menulist.removeFooterView(footer);
                    }

                    if (modeCari.equalsIgnoreCase(EnumSearch.PELAKSANA.toString()) ||
                            modeCari.equalsIgnoreCase(EnumSearch.PENYIDIK.toString()) ||
                            modeCari.equalsIgnoreCase(EnumSearch.WILAYAH.toString()) ||
                            modeCari.equalsIgnoreCase(EnumSearch.LOKASI.toString()) ||
                            modeCari.equalsIgnoreCase(EnumSearch.JENIS_KASUS.toString())) {

                    } else if (modeCari.equalsIgnoreCase(EnumSearch.TANGGAL.toString())) {
                        Log.d(TAG, "Is Empty: " + edTanggalBefore.getText().toString().equals(""));

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
            case R.id.imageBack:
                Intent dashboardBack = new Intent(PemusnahanBarangBuktiActivity.this, PemberantasanActivity.class);
                startActivity(dashboardBack);
                finish();
                break;
            case R.id.edTanggalBefore:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(PemusnahanBarangBuktiActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        tglFrom = sDate;
                        edTanggalBefore.setText(getNewDate(sDate));
                    }
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
                //                    dpd.getDatePicker().setMaxDate(new SimpleDateFormat("yyyy-MM-dd").parse(now).getTime());
                dpd.show();
                break;
            case R.id.edTanggalAfter:
                Calendar calendar1 = Calendar.getInstance();
                DatePickerDialog dpd1 = new DatePickerDialog(PemusnahanBarangBuktiActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        tglTo = sDate;
                        edTanggalAfter.setText(getNewDate(sDate));
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
//                editsearch.setEnabled(true);
            }
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            RequestBody requestBody = null;
            requestBody = new FormBody.Builder()
                    .add("a", prefUtils.getWilayah())
                    .build();
            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl + "/api/listpemusnahan?page=1&limit=1000")
                    //.url(Configuration.baseUrl+"/api/listpemusnahanmobile")
                    //.url(Configuration.DEV_URL_OSB + "/berantas/pemusnahanlist")
                    .addHeader("Authorization", "Bearer "+ prefUtils.getTokenKey())
                    .post(requestBody)
                    //.post(RequestBody.create(MediaType.parse("application/json"), makeParam(0,2)))
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
                    JSONArray data = jsonObject.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject list = data.getJSONObject(i);
                        Lkn lkns = new Lkn();
                        jarr = list.getJSONArray("bbdetail");
                        for (int j = 0; j < jarr.length(); j++) {
                            JSONObject jobs = jarr.getJSONObject(j);
                        /*
                        String jumlah = list.getString("meta_brgbukti").equals("null") ? "-" :
                                String.valueOf(new JSONArray(list.getString("meta_brgbukti")).length());
                        */

                            String jumlah = list.getString("bbdetail").equals("null") ? "-" :
                                    String.valueOf(new JSONArray(list.getString("bbdetail")).length());

                        /*
                        menuLists.add(new Listmenu(list.getString("tgl_pemusnahan"),
                                jumlah,
                                list.getString("keterangan_lainnya"),
                                list.getString("id"),
                                list.getString("lokasi"),
                                "-",
                                list.getString("meta_brgbukti").equals("null") ? "": list.getString("meta_brgbukti"),
                                ""));
                        */


                            menuLists.add(new Listmenu(list.getString("tgl_pemusnahan"),
                                    jumlah,
                                    list.getString("nomor_lkn"),
                                    list.getString("id"),
                                    "",
                                    jobs.getString("jumlah_dimusnahkan"),
                                    "",
                                    jobs.getString("nm_brgbukti")));
                        }
                    }

                        listMenuAdapter = new ListMenuPemusnahanBarbukAdapter(PemusnahanBarangBuktiActivity.this, menuLists);
                        menulist.setAdapter(listMenuAdapter);

                        progressBar.setVisibility(View.GONE);
                        editsearch.setEnabled(true);

                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                        progressBar.setVisibility(View.GONE);
                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PemusnahanBarangBuktiActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(PemusnahanBarangBuktiActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    }
                } else {
                        progressBar.setVisibility(View.GONE);
                    showToast(1, "Failed detect.");
                }
            } catch (JSONException e) {
                e.printStackTrace();

//                progressBar.setVisibility(View.GONE);
                loadData();
            }
        }
    }

    private void showToast(int dur, String msg) {
        Toast.makeText(this, msg, dur == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PemusnahanBarangBuktiActivity.this, PemberantasanActivity.class);
        startActivity(intent);
        finish();
    }

    private String makeParam(int page, int limit){
        JSONObject jsobj = new JSONObject();
        JSONObject jsobj1 = new JSONObject();

        try {
            jsobj1.put("page", page);
            jsobj1.put("limit", limit);
            jsobj.put("paginate", jsobj1);
        }
        catch (JSONException je){

        }
        return jsobj.toString();
    }
}
