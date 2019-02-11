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
import com.bnn.Adapter.ListMenuKegiatanAdapter;
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

public class KegiatanAsistensiActivity extends ActivityBase implements View.OnClickListener {

    //Ditambahkan pada 04 Nov 2017
    ListView menulist;
    ImageView imgToogle, imgTambah, imgBack;
    Spinner spinnerCari;
    int id;
    LinearLayout linearLayoutPengisian, linearLayoutTanggal, linearLayoutSearch, footer;
    TextView edTanggalBefore, edTanggalAfter, edInput;
    Button btnCari;
    EditText edKataKunci;
    PreferenceUtils prefUtils;
    ArrayList<Listmenu> menuLists = new ArrayList<>();
    ListMenuKegiatanAdapter listMenuAdapter;
    Calendar cal;
    String now = "";
    String modeCari = "";
    String tglFrom, tglTo, katakunci;
    ProgressBar progressBar;

    boolean isLoadMore = false, isLast = false;
    int pageRequest = 1, index = 0;

    private int preLast;
    int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount, currentScrollState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_list_kegiatanasistensi, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);

        linearLayoutTanggal = (LinearLayout) findViewById(R.id.linearTanggal);
        linearLayoutPengisian = (LinearLayout) findViewById(R.id.linearInputList);
        linearLayoutSearch = (LinearLayout) findViewById(R.id.linearButtonSearch);
        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        spinnerCari = (Spinner) findViewById(R.id.spinnerSearch);
        edKataKunci = (EditText) findViewById(R.id.edKataKunci);
        btnCari = (Button) findViewById(R.id.btnSearch);
        edTanggalBefore = (EditText) findViewById(R.id.edTanggalBefore);
        edTanggalAfter = (EditText) findViewById(R.id.edTanggalAfter);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        menulist = (ListView) findViewById(R.id.listViewKegiatanAsistensi);
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
                String asistensiId = listMenuAdapter.getItem(i).getNoid();

                Intent detilKasus = new Intent(KegiatanAsistensiActivity.this, DetilKegiatanAsistensiActivity.class);
                detilKasus.putExtra("id", asistensiId);
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
                            selected.equalsIgnoreCase(EnumSearch.LOKASI.toString())) {

                        edKataKunci.setText("");
                        katakunci = "";

                        linearLayoutTanggal.setVisibility(View.GONE);
                        linearLayoutPengisian.setVisibility(View.VISIBLE);
                        linearLayoutSearch.setVisibility(View.VISIBLE);

                        isLoadMore = false;
                        pageRequest = 1;
                    } else if (selected.equalsIgnoreCase(EnumSearch.TANGGAL.toString())) {
                        linearLayoutPengisian.setVisibility(View.GONE);
                        linearLayoutTanggal.setVisibility(View.VISIBLE);
                        linearLayoutSearch.setVisibility(View.VISIBLE);

                        tglFrom = "";
                        tglTo = "";
                        edTanggalBefore.setText("");
                        edTanggalAfter.setText("");

                        isLoadMore = false;
                        pageRequest = 1;
                    } else if (selected.equalsIgnoreCase(EnumSearch.SEMUA.toString())) {
                        linearLayoutPengisian.setVisibility(View.GONE);
                        linearLayoutTanggal.setVisibility(View.GONE);
                        linearLayoutSearch.setVisibility(View.VISIBLE);

                        isLoadMore = false;
                        pageRequest = 1;
                    } else if (selected.equalsIgnoreCase(EnumSearch.PILIH.toString())) {
                        linearLayoutPengisian.setVisibility(View.GONE);
                        linearLayoutTanggal.setVisibility(View.GONE);
                        linearLayoutSearch.setVisibility(View.GONE);

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
            case R.id.imgAdd:
                Intent y = new Intent(KegiatanAsistensiActivity.this, TambahKegiatanAsistensiActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.btnSearch:
                if (Configuration.isDeviceOnline(KegiatanAsistensiActivity.this)) {

                    expandList.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    if (menulist.getFooterViewsCount() == 1) {
                        menulist.removeFooterView(footer);
                    }

                    if (modeCari.equalsIgnoreCase(EnumSearch.LOKASI.toString())) {

                        if (edKataKunci.getText().toString().equals("")) {
                            showToast("Masukan kata kunci pencarian.");
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
                            showToast("Pilih tanggal awal.");
                        } else if (edTanggalAfter.getText().toString().equals("")) {
                            showToast("Pilih tanggal akhir.");
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
            case R.id.imgBack:
                Intent pencegahan = new Intent(KegiatanAsistensiActivity.this, PencegahanActivity.class);
                startActivity(pencegahan);
                finish();
                break;
            case R.id.edTanggalBefore:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(KegiatanAsistensiActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        tglFrom = sDate;
                        edTanggalBefore.setText(getNewDate(sDate));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                //                    dpd.getDatePicker().setMaxDate(new SimpleDateFormat("yyyy-MM-dd").parse(now).getTime());
                dpd.show();
                break;
            case R.id.edTanggalAfter:
                Calendar calendar1 = Calendar.getInstance();
                DatePickerDialog dpd1 = new DatePickerDialog(KegiatanAsistensiActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        tglTo = sDate;
                        edTanggalAfter.setText(getNewDate(sDate));
                    }
                }, calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
                //                    dpd1.getDatePicker().setMaxDate(new SimpleDateFormat("yyyy-MM-dd").parse(now).getTime());
                dpd1.show();
                break;
        }
    }

    public String getNewDate(String date) {
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

            Request httpRequest1 = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/listasistensi")
                    .addHeader("Authorization", "Bearer "+ prefUtils.getTokenKey())
                    .post(requestBody)
                    .build();

            Response httpResponse = null;
            Log.d("test1", Configuration.baseUrl +" "+ bodyToString(httpRequest1) +" "+ prefUtils.getTokenKey());

            try {
                httpResponse = httpclient.newCall(httpRequest1).execute();
            } catch (IOException e) {
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

                        menuLists.add(new Listmenu(!list.getString("tgl_pelaksanaan").equals("null") ? list.getString("tgl_pelaksanaan") : "-",
                                !list.getString("kodesasaran").equals("null") ? list.getString("kodesasaran") : "-",
                                !list.getString("jumlah_peserta").equals("null") ? list.getString("jumlah_peserta") : "-",
                                !list.getString("id").equals("null") ? list.getString("id") : "-",
                                !list.getString("lokasi_kegiatan").equals("null") ? list.getString("lokasi_kegiatan") : "-",
                                !list.getString("created_by_username").equals("null") ? list.getString("created_by_username") : "-"));
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
                        listMenuAdapter = new ListMenuKegiatanAdapter(KegiatanAsistensiActivity.this, menuLists);
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

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(KegiatanAsistensiActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(KegiatanAsistensiActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(KegiatanAsistensiActivity.this, "Failed detect.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                loadData();
                e.printStackTrace();
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(KegiatanAsistensiActivity.this, PencegahanActivity.class));
        finish();
    }
}