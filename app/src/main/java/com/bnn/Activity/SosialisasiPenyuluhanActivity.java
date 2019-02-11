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

public class SosialisasiPenyuluhanActivity extends ActivityBase implements View.OnClickListener {

    //Ditambahkan pada 04 Nov 2017
    ListView menulist;
    ImageView imgToogle, imgTambah, imgBack;
    Spinner spinnerCari;
    int id;
    LinearLayout linearLayoutPengisian, linearLayoutTanggal, linearLayoutSearch;
    TextView edTanggalBefore, edTanggalAfter, edInput;
    Button btnCari;
    PreferenceUtils prefUtils;
    ArrayList<Listmenu> menuLists = new ArrayList<>();
    ListMenuAdapter listMenuAdapter;
    Calendar cal;
    String now = "";
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_list_sosialisasipenyuluhan, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);

        linearLayoutTanggal = (LinearLayout) findViewById(R.id.linearTanggal);
        linearLayoutPengisian = (LinearLayout) findViewById(R.id.linearInputList);
        linearLayoutSearch = (LinearLayout) findViewById(R.id.linearButtonSearch);
        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        spinnerCari = (Spinner) findViewById(R.id.spinnerSearch);
        btnCari = (Button) findViewById(R.id.btnSearch);
        edTanggalBefore = (EditText) findViewById(R.id.edTanggalBefore);
        edTanggalAfter = (EditText) findViewById(R.id.edTanggalAfter);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        menulist = (ListView) findViewById(R.id.listViewSosialisasiPenyuluhan);

        ArrayList<String> searchlist = new ArrayList<>(Arrays.asList(
                EnumSearch.PILIH.toString(),
                EnumSearch.SEMUA.toString(),
                EnumSearch.JENIS_KEGIATAN.toString(),
                EnumSearch.TANGGAL.toString(),
                EnumSearch.INSTANSI.toString(),
                EnumSearch.SASARAN.toString()));

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
                String penyuluhanId = listMenuAdapter.getItem(i).getNoid();

                Intent detilKasus = new Intent(SosialisasiPenyuluhanActivity.this, DetilSosialisasiPenyuluhanActivity.class);
                detilKasus.putExtra("penyuluhan_id", penyuluhanId);
                startActivity(detilKasus);
                finish();
            }
        });

        new DataUploadTask().execute();

        spinnerCari.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (view != null) {
                    String selected = ((TextView) view).getText().toString();

                    if (selected.equalsIgnoreCase(EnumSearch.JENIS_KEGIATAN.toString()) ||
                            selected.equalsIgnoreCase(EnumSearch.INSTANSI.toString()) ||
                            selected.equalsIgnoreCase(EnumSearch.SASARAN.toString())) {
                        linearLayoutTanggal.setVisibility(View.GONE);
                        linearLayoutPengisian.setVisibility(View.VISIBLE);
                        linearLayoutSearch.setVisibility(View.VISIBLE);
                    } else if (selected.equalsIgnoreCase(EnumSearch.TANGGAL.toString())) {
                        linearLayoutPengisian.setVisibility(View.GONE);
                        linearLayoutTanggal.setVisibility(View.VISIBLE);
                        linearLayoutSearch.setVisibility(View.VISIBLE);
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

    }

    @Override
    public void onClick(View view) {
        cal = Calendar.getInstance();
        now = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

        switch (view.getId()) {
            case R.id.imgAdd:
                Intent y = new Intent(SosialisasiPenyuluhanActivity.this, TambahSosialisasiPenyuluhanActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.btnSearch:

                break;
            case R.id.imgBack:
                Intent pencegahan = new Intent(SosialisasiPenyuluhanActivity.this, PencegahanActivity.class);
                startActivity(pencegahan);
                finish();
                break;
            case R.id.edTanggalBefore:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(SosialisasiPenyuluhanActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        edTanggalBefore.setText(getNewDate(sDate));
                    }
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
                //                    dpd.getDatePicker().setMaxDate(new SimpleDateFormat("yyyy-MM-dd").parse(now).getTime());
                dpd.show();
                break;
            case R.id.edTanggalAfter:
                Calendar calendar1 = Calendar.getInstance();
                DatePickerDialog dpd1 = new DatePickerDialog(SosialisasiPenyuluhanActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        edTanggalAfter.setText(getNewDate(sDate));
                    }
                }, calendar1.get(java.util.Calendar.YEAR), calendar1.get(java.util.Calendar.MONTH), calendar1.get(java.util.Calendar.DAY_OF_MONTH));
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

    class DataUploadTask extends AsyncTask<Bitmap, Integer, String> {
        private String responseServer;
        private String keyKataKunci;

        public DataUploadTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseServer = "";

            progressBar.setVisibility(View.VISIBLE);
            expandList.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/advoasistensi")
                    .addHeader("Authorization", "Bearer " + prefUtils.getTokenKey())
                    .build();

            Response httpResponse = null;

            try {
                httpResponse = httpclient.newCall(httpRequest).execute();
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
                                !list.getString("materi").equals("null") ? list.getString("materi") : "-",
                                !list.getString("created_by_username").equals("null") ? list.getString("created_by_username") : "-",
                                !list.getString("id").equals("null") ? list.getString("id") : "-",
                                !list.getString("lokasi_kegiatan").equals("null") ? list.getString("lokasi_kegiatan") : "-",
                                !list.getString("created_by_username").equals("null") ? list.getString("created_by_username") : "-"));
                    }

                    listMenuAdapter = new ListMenuAdapter(SosialisasiPenyuluhanActivity.this, menuLists);
                    menulist.setAdapter(listMenuAdapter);

                    progressBar.setVisibility(View.GONE);
                    expandList.setVisibility(View.VISIBLE);
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    progressBar.setVisibility(View.GONE);

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SosialisasiPenyuluhanActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(SosialisasiPenyuluhanActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(SosialisasiPenyuluhanActivity.this, "Failed detect.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SosialisasiPenyuluhanActivity.this, PencegahanActivity.class));
        finish();
    }
}