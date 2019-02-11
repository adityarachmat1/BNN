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
import com.bnn.Adapter.ListMenuLKNAdapter;
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


public class PemusnahanBarangBuktiActivity2 extends ActivityBase{
    private static final String TAG = PemusnahanBarangBuktiActivity.class.getSimpleName();

    //Ditambahkan pada 04 Nov 2017
    private ListView menulist;
    private ImageView imgToogle,imgBack;
    private Spinner spinnerCari;
    int id;
    private LinearLayout footer;
    private PreferenceUtils prefUtils;
    private ArrayList<Listmenu> menuLists = new ArrayList<>();
    private ListMenuLKNAdapter listMenuAdapter;
    private Calendar cal;
    private String now = "", id_, tanggal, nama_barang, no_lkn;
    private ProgressBar progressBar;

    private boolean isLoadMore = false, isLast = false;
    private int pageRequest = 1, index = 0, indexs, total;

    private int preLast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_list_lkn, linearLayout);

        id_ = getIntent().getStringExtra("pemusnahan_id");
        tanggal = getIntent().getStringExtra("tanggal");
        no_lkn = getIntent().getStringExtra("no_lkn");

        Log.d("id", id_);
        Log.d("tanggal", tanggal);
        Log.d("no_lkn", no_lkn);

        TextView tvLn = (TextView) findViewById(R.id.tvLN);
        tvLn.setText(no_lkn);

        prefUtils = PreferenceUtils.getInstance(this);

        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imgBack = (ImageView) findViewById(R.id.imageBack);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        menulist = (ListView) findViewById(R.id.listViewPemusnahanBarangBukti);

        menulist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String pemusnahanId = listMenuAdapter.getItem(i).getNoid();
                String arrayBarbuk = listMenuAdapter.getItem(i).getArrayStringBarbuk();

                Intent detilKasus = new Intent(PemusnahanBarangBuktiActivity2.this, TambahPemusnahanBarangBuktiActivity.class);
                detilKasus.putExtra("pemusnahan_id", id_);
                detilKasus.putExtra("no_lkn", no_lkn);
                detilKasus.putExtra("mode_form", "ubah");
//                detilKasus.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(detilKasus);
                finish();
            }
        });

        imgToogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDrawerLayout.openDrawer(linearLayout2);
            }
        });

        new DataUploadTask().execute();
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

            if (!isLoadMore) {
                progressBar.setVisibility(View.VISIBLE);
                expandList.setVisibility(View.GONE);
            }
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            RequestBody requestBody = null;
            requestBody = new FormBody.Builder()
                    .add("nomor_lkn", no_lkn)
                    .build();
            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl + "/api/listpemusnahan")
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
                        JSONArray jarr = list.getJSONArray("bbdetail");
                        for (int j = 0; j < jarr.length(); j++) {
                            JSONObject jobs = jarr.getJSONObject(j);

                            String jumlah = list.getString("bbdetail").equals("null") ? "-" :
                                    String.valueOf(new JSONArray(list.getString("bbdetail")).length());



                            menuLists.add(new Listmenu(jobs.getString("nm_brgbukti"),
                                    "",
                                    jobs.getString("jumlah_dimusnahkan"),
                                    "",
                                    "",
                                    "",
                                    "",
                                    ""));
                        }
                    }

                    listMenuAdapter = new ListMenuLKNAdapter(PemusnahanBarangBuktiActivity2.this, menuLists);
                    menulist.setAdapter(listMenuAdapter);

                    progressBar.setVisibility(View.GONE);

                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    progressBar.setVisibility(View.GONE);
                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PemusnahanBarangBuktiActivity2.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(PemusnahanBarangBuktiActivity2.this, LoginActivity.class);
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

                progressBar.setVisibility(View.GONE);
//                loadData();
            }
        }
    }


    private void showToast(int dur, String msg) {
        Toast.makeText(this, msg, dur == 1 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PemusnahanBarangBuktiActivity2.this, PemusnahanBarangBuktiActivity.class));
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
