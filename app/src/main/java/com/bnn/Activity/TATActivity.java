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

public class TATActivity extends ActivityBase implements View.OnClickListener {
    //Ini tag
    private static final String TAG = TATActivity.class.getSimpleName();

    ListView menulist;
    ImageView imgToogle,imgtambah, imgBack;
    Spinner spinnerlist;
    Button btnCari;
    int id;
    LinearLayout pengisian,tanggal, linearSearch;
    TextView txttanggalberfore, txttanggalafter, txtinput;
    EditText edKataKunci;
    Button btnsearch;
    PreferenceUtils prefUtils;
    ArrayList<Listmenu> menulists = new ArrayList<>();
    ListMenuAdapter adapter;

    //Ditambahkan pada 03 Nov 2017
    Calendar cal;
    String now = "";
    ProgressBar progressBar;

    String modeCari = "";
    String tglFrom, tglTo, katakunci;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_list_tat, linearLayout);

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

        menulist = (ListView) findViewById(R.id.listview_tat);

        ArrayList<String> searchlist = new ArrayList<>(Arrays.asList(
                EnumSearch.PILIH.toString(),
                EnumSearch.SEMUA.toString(),
                EnumSearch.TANGGAL.toString(),
                EnumSearch.LOKASI.toString(),
                EnumSearch.PELAKSANA.toString(),
                EnumSearch.PENYIDIK.toString(),
                EnumSearch.JENIS_KASUS.toString(),
                EnumSearch.WILAYAH.toString()));

        ArrayAdapter<String> searchByAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, searchlist);
        spinnerlist.setAdapter(searchByAdapter);

        imgtambah.setOnClickListener(this);

        //Ditambahkan pada 03 nov 2017
        txttanggalberfore.setOnClickListener(this);
        txttanggalafter.setOnClickListener(this);
        btnCari.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        menulists.add(new Listmenu("03 November 2017", "Nama", "Laki-Laki", "-"));

        adapter = new ListMenuAdapter(TATActivity.this, menulists);
        menulist.setAdapter(adapter);

        menulist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String eventID = adapter.getItem(i).getNoid();
//                String pelaksana = adapter.getItem(i).getPelaksana();

                Intent detilKasus = new Intent(TATActivity.this, DetilTATActivity.class);
//                detilKasus.putExtra("event_id", eventID);
//                detilKasus.putExtra("pelaksana", pelaksana);
                startActivity(detilKasus);
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
                            selected.equalsIgnoreCase(EnumSearch.WILAYAH.toString()) ||
                            selected.equalsIgnoreCase(EnumSearch.LOKASI.toString()) ||
                            selected.equalsIgnoreCase(EnumSearch.JENIS_KASUS.toString()) ||
                            selected.equalsIgnoreCase(EnumSearch.SEMUA.toString())) {

//                        tanggal.setVisibility(View.GONE);
//                        pengisian.setVisibility(View.GONE);
//                        linearSearch.setVisibility(View.VISIBLE);
//                    } else if (selected.equalsIgnoreCase(EnumSearch.SEMUA.toString())) {
                        tanggal.setVisibility(View.GONE);
                        pengisian.setVisibility(View.VISIBLE);
                        linearSearch.setVisibility(View.VISIBLE);
                    } else if (selected.equalsIgnoreCase(EnumSearch.TANGGAL.toString())) {
                        pengisian.setVisibility(View.GONE);
                        tanggal.setVisibility(View.VISIBLE);
                        linearSearch.setVisibility(View.VISIBLE);
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

//        DataUploadTask dataUploadTask = new DataUploadTask();
//        dataUploadTask.execute();
    }


    @Override
    public void onClick(View view) {
        cal = Calendar.getInstance();
        now = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

        switch (view.getId()) {
            case R.id.imageadd:
                Intent y = new Intent(TATActivity.this, TambahTATActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.button_search:
                expandList.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                if (menulists.size() > 0) {
                    menulists.clear();
                    adapter.clear();
                }

//                DataUploadTask dataUploadTask = new DataUploadTask();
//                dataUploadTask.execute();
                break;
            case R.id.imageback:
                Intent dashboardBack = new Intent(TATActivity.this, RehabilitasiActivity.class);
                startActivity(dashboardBack);
                finish();
                break;
            case R.id.texttanggalbefore:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TATActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                DatePickerDialog dpd1 = new DatePickerDialog(TATActivity.this, new DatePickerDialog.OnDateSetListener() {
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

            progressBar.setVisibility(View.VISIBLE);
            expandList.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            RequestBody requestBody = null;

            if (modeCari.equalsIgnoreCase(EnumSearch.TANGGAL.toString())) {
                requestBody = new FormBody.Builder()
                        .add("tgl_from", tglFrom)
                        .add("tgl_to", tglTo)
                        .add("id_wilayah", prefUtils.getWilayah())
                        .build();
            } else if (modeCari.equalsIgnoreCase(EnumSearch.PELAKSANA.toString()) ||
                    modeCari.equalsIgnoreCase(EnumSearch.PENYIDIK.toString()) ||
                    modeCari.equalsIgnoreCase(EnumSearch.WILAYAH.toString()) ||
                    modeCari.equalsIgnoreCase(EnumSearch.LOKASI.toString()) ||
                    modeCari.equalsIgnoreCase(EnumSearch.JENIS_KASUS.toString())) {
                    requestBody = new FormBody
                        .Builder()
                        .add(keyKataKunci, katakunci)
                            .add("id_wilayah", prefUtils.getWilayah())
                            .build();
            } else {
                requestBody = new FormBody.Builder()
//                        .add("tgl_from", "2017-10-30")
//                        .add("tgl_to", "2017-11-05")
                        .add("id_wilayah", prefUtils.getWilayah())
                        .build();
            }

            Log.d("test", modeCari + " : "+requestBody.toString());

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/listkasus")
                    .addHeader("Authorization", "Bearer "+ prefUtils.getTokenKey())
                    .post(requestBody)
                    .build();

//            Request httpRequest = new Request.Builder()
//                    .url(Configuration.baseUrl+"/api/kasus")
//                    .addHeader("Authorization", "Bearer "+ prefUtils.getTokenKey())
//                    .build();
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

                        menulists.add(new Listmenu(list.getString("tgl"),
                                list.getString("no_lap"),
                                list.getString("kelompok"),
                                list.getString("eventID"),
                                list.getString("instansi"),
                                list.getString("instansi")));

                        Log.d("cobadatalist","data : " + data.toString() + "\ntgl : " + list.getString("tgl")
                                + "\nlkn : " + list.getString("no_lap") + "\nbarangbukti : " + list.getString("kelompok")
                                + "\nlokasi : " + list.getString("instansi"));
                    }

                    adapter = new ListMenuAdapter(TATActivity.this, menulists);
                    menulist.setAdapter(adapter);

                    progressBar.setVisibility(View.GONE);
                    expandList.setVisibility(View.VISIBLE);
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    progressBar.setVisibility(View.GONE);

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TATActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TATActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(TATActivity.this, "Failed detect.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TATActivity.this, RehabilitasiActivity.class));
        finish();
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
