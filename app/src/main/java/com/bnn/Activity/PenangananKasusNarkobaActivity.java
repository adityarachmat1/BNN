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
import com.bnn.Adapter.ListMenuPengananKasusAdapter;
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
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by USER on 10/25/2017.
 */

public class PenangananKasusNarkobaActivity extends ActivityBase implements View.OnClickListener {
    private static final String TAG = PenangananKasusNarkobaActivity.class.getSimpleName();

    private ListView menulist;
    private ImageView imgToogle,imgtambah, imgBack;
    private Spinner spinnerlist;
    private Button btnCari;
    private LinearLayout pengisian,tanggal, linearSearch, footer;
    private TextView txttanggalberfore, txttanggalafter, txtinput;
    private EditText edKataKunci;
    private PreferenceUtils prefUtils;
    private ArrayList<Listmenu> menulists = new ArrayList<>();
    private ListMenuPengananKasusAdapter adapter;

    //Ditambahkan pada 03 Nov 2017
    private Calendar cal;
    private ProgressBar progressBar, progressBarLoadMore;
    private int id;

    private String modeCari = "";
    private String tglFrom, tglTo, katakunci;

    private boolean isLoadMore = false, isLast = false;
    private int pageRequest = 1;

    private int preLast;
    private int lastIndex = 0;

    int currentFirstVisibleItem, currentVisibleItemCount, currentTotalItemCount, currentScrollState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_list_penanganankasusnarkoba, linearLayout);

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

        menulist = (ListView) findViewById(R.id.listview_penanganankasusnarkoba);
        footer = (LinearLayout) this.getLayoutInflater().inflate(R.layout.footer_loadmore, menulist, false);

        initSpinnerPencarian();

        imgBack.setOnClickListener(this);
        imgtambah.setOnClickListener(this);
        txttanggalberfore.setOnClickListener(this);
        txttanggalafter.setOnClickListener(this);
        btnCari.setOnClickListener(this);

        menulist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String eventID = adapter.getItem(i).getNoid();

                /*
                String pelaksana = adapter.getItem(i).getPelaksana();
                String arrayBarbuk = adapter.getItem(i).getArrayStringBarbuk();
                String arrayTersangka = adapter.getItem(i).getArrayStringTersangka();
                */

                String pelaksana = "";
                String arrayBarbuk = "";
                String arrayTersangka = "";

                Intent detilKasus = new Intent(PenangananKasusNarkobaActivity.this, DetilPenangananKasusActivity.class);
                detilKasus.putExtra("event_id", eventID);
                detilKasus.putExtra("pelaksana", pelaksana);
                detilKasus.putExtra("array_barbuk", arrayBarbuk);
                detilKasus.putExtra("array_tersangka", arrayTersangka);
                startActivity(detilKasus);
                finish();

                //Toast.makeText(getApplicationContext(), "Event ID: "+eventID, Toast.LENGTH_LONG).show();
            }
        });

        spinnerlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (view != null) {
                    String selected = ((TextView) view).getText().toString();

                    modeCari = selected;

                    if (/*selected.equalsIgnoreCase(EnumSearch.PELAKSANA.toString()) ||
                            */selected.equalsIgnoreCase(EnumSearch.LOKASI.toString())) {
                        tanggal.setVisibility(View.GONE);
                        pengisian.setVisibility(View.VISIBLE);
                        linearSearch.setVisibility(View.VISIBLE);

                        katakunci = "";
                        edKataKunci.setText("");

                        isLoadMore = false;
                        pageRequest = 1;
                    } else if (selected.equalsIgnoreCase(EnumSearch.TANGGAL.toString())) {
                        pengisian.setVisibility(View.GONE);
                        tanggal.setVisibility(View.VISIBLE);
                        linearSearch.setVisibility(View.VISIBLE);

                        tglFrom = "";
                        tglTo = "";
                        txttanggalberfore.setText("");
                        txttanggalafter.setText("");

                        isLoadMore = false;
                        pageRequest = 1;
                    } else if (selected.equalsIgnoreCase(EnumSearch.SEMUA.toString())) {
                        pengisian.setVisibility(View.GONE);
                        tanggal.setVisibility(View.GONE);
                        linearSearch.setVisibility(View.VISIBLE);

                        isLoadMore = false;
                        pageRequest = 1;
                    } else if (selected.equalsIgnoreCase(EnumSearch.PILIH.toString())) {
                        pengisian.setVisibility(View.GONE);
                        tanggal.setVisibility(View.GONE);
                        linearSearch.setVisibility(View.GONE);

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

    private void initSpinnerPencarian() {
        ArrayList<String> searchlist = new ArrayList<>(Arrays.asList(
                EnumSearch.PILIH.toString(),
                EnumSearch.SEMUA.toString(),
                EnumSearch.TANGGAL.toString(),
                EnumSearch.LOKASI.toString()/*,
                EnumSearch.PELAKSANA.toString()*/));

        ArrayAdapter<String> searchByAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, searchlist);
        spinnerlist.setAdapter(searchByAdapter);
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
            case R.id.imageadd:
                Intent y = new Intent(PenangananKasusNarkobaActivity.this, TambahPenangananKasusNarkobaActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.button_search:
                if (Configuration.isDeviceOnline(PenangananKasusNarkobaActivity.this)) {

                    expandList.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    if (menulist.getFooterViewsCount() == 1) {
                        menulist.removeFooterView(footer);
                    }

                    if (/*modeCari.equalsIgnoreCase(EnumSearch.PELAKSANA.toString()) ||
                            */modeCari.equalsIgnoreCase(EnumSearch.LOKASI.toString())) {

                        if (edKataKunci.getText().toString().equals("")) {
                            showToast(1, "Masukan kata kunci pencarian.");
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
                            showToast(1, "Pilih tanggal awal.");
                        } else if (txttanggalberfore.getText().toString().equals("")) {
                            showToast(1, "Pilih tanggal akhir.");
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
                Intent dashboardBack = new Intent(PenangananKasusNarkobaActivity.this, PemberantasanActivity.class);
                startActivity(dashboardBack);
                finish();
                break;
            case R.id.texttanggalbefore:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(PenangananKasusNarkobaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        tglFrom = sDate;
                        txttanggalberfore.setText(getNewDate(sDate));
                    }
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));

                dpd.show();
                break;
            case R.id.texttanggalafter:
                Calendar calendar1 = Calendar.getInstance();
                DatePickerDialog dpd1 = new DatePickerDialog(PenangananKasusNarkobaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        tglTo = sDate;
                        txttanggalafter.setText(getNewDate(sDate));
                    }
                }, calendar1.get(java.util.Calendar.YEAR), calendar1.get(java.util.Calendar.MONTH), calendar1.get(java.util.Calendar.DAY_OF_MONTH));

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
                    Log.d("Here", "Here: "+menulists.size());

                    pageRequest = pageRequest + 1;

                    requestBody = new FormBody.Builder()
                            .add("page", String.valueOf(pageRequest))
                            .add("id_wilayah", prefUtils.getWilayah())
                            .build();
                } else {
                    Log.d("Here1", "Here1: "+pageRequest);

                    requestBody = new FormBody.Builder()
                            .add("id_wilayah", prefUtils.getWilayah())
                            .build();
                }
            }

            /*
            String jsonstr = "{\n" +
                    "  \"paginate\" : {\n" +
                    "    \"page\" : 0,\n" +
                    "    \"limit\" : 2\n" +
                    "  }\n" +
                    "}";
            */

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/listkasus")
                    //.url(Configuration.DEV_URL_OSB+ "/berantas/kasuslist")
                    .addHeader("Authorization", "Bearer "+ prefUtils.getTokenKey())
                    //.post(RequestBody.create(MediaType.parse("application/json"), makeParam(0,2))).build();
                    .post(requestBody).build();

           // Log.d("url yg dipake", Configuration.DEV_URL_OSB);

            Response httpResponse = null;

            try {
                httpResponse = httpclient.newCall(httpRequest).execute();
            }  catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (httpResponse != null) {
                    responseServer = httpResponse.body().string();

                    Log.d(TAG, "Response: "+responseServer);
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

                        menulists.add(new Listmenu(
                                list.getString("tgl"),
                                list.getString("no_lap"),
                                list.getString("kelompok"),
                                list.getString("eventID"),
                                list.getString("tkp"),
                                list.getString("instansi"),
                                list.getString("BrgBukti").equals("null") ? "": list.getString("BrgBukti"),
                                list.getString("tersangka").equals("null") ? "": list.getString("tersangka")
                        ));

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
                        adapter = new ListMenuPengananKasusAdapter(PenangananKasusNarkobaActivity.this, menulists);
                        menulist.setAdapter(adapter);

                        progressBar.setVisibility(View.GONE);
                        expandList.setVisibility(View.VISIBLE);
                    }

//                    if (menulists.size() > 10) {
//                        menulist.setSelection(menulists.size() - 10);
//                    }

                    Log.d("Size", "Size: "+menulists.size()+" "+lastIndex);
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    if (isLoadMore) {
                        isLoadMore = false;
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PenangananKasusNarkobaActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(PenangananKasusNarkobaActivity.this, LoginActivity.class);
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
                Toast.makeText(getApplicationContext(),"Exception: "+e.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                loadData();
            }
        }
    }

    private void showToast(int dur, String msg) {
        Toast.makeText(this, msg, dur == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PenangananKasusNarkobaActivity.this, PemberantasanActivity.class));
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
