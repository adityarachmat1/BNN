package com.bnn.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bnn.Adapter.ListBarangBuktiAdapter;
import com.bnn.Modal.BarangBukti;
import com.bnn.R;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.Configuration;
import com.bnn.Utils.PreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ferdinandprasetyo on 11/4/17.
 */

public class DetilSosialisasiviavideotronActivity extends ActivityBase implements View.OnClickListener {
    //DiPerbaharui pada 14 Nov 2017
    ImageView imgBack, imgTambah, toggleMenu, imgBukaLokasi, imgLihatBarangBukti, imgClosePopup, iconTitle, imgUpload1, imgUpload2, imgUpload3;
    Button btnHapus, btnUbah, btnYa, btnTidak, btnKembali;
    EditText edNoid, edNoLkn, edTglPelaksanaan, edlokasipenempatan, edJmlBarbuk, edPelaksana, edPenyidik, edPelaku;
    TextView txtTitle;
    ScrollView layoutDetilVideotron;
    RelativeLayout layoutDetilBarangBukti;

    PreferenceUtils prefUtils;
    String IDIntent, pelaksanaIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_detil_sosialisasiviavideotron, linearLayout);

        IDIntent = getIntent().getStringExtra("id");
        pelaksanaIntent = getIntent().getStringExtra("nm_instansi");

        prefUtils = PreferenceUtils.getInstance(this);

        toggleMenu = (ImageView) findViewById(R.id.imgToogleMenu);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        imgBukaLokasi = (ImageView) findViewById(R.id.imglokasiPenempatan);

        //Ditambahkan pada 05 No 2017
        edNoid = (EditText) findViewById(R.id.edNoId);
        edTglPelaksanaan = (EditText) findViewById(R.id.edTanggalPelaksanaan);
        edlokasipenempatan = (EditText) findViewById(R.id.edLokasiPenempatan);
        edPelaksana = (EditText) findViewById(R.id.edPelaksana);

        //Ditambahkan pada 06 No 2017
        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        imgUpload1.setImageResource(R.drawable.img1);
        imgUpload2.setImageResource(R.drawable.img2);
        imgUpload3.setImageResource(R.drawable.img3);

        btnUbah = (Button) findViewById(R.id.btnHapus);
        btnHapus = (Button) findViewById(R.id.btnUbah);
        layoutDetilVideotron = (ScrollView) findViewById(R.id.layoutDetilvideotron);

        toggleMenu.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgTambah.setOnClickListener(this);
        imgBukaLokasi.setOnClickListener(this);
        btnHapus.setOnClickListener(this);
        btnUbah.setOnClickListener(this);

        new DataUploadTask().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgToogleMenu:
                menuDrawerLayout.openDrawer(linearLayout2);
                break;
            case R.id.imgBack:
                Intent penangananKasus = new Intent(DetilSosialisasiviavideotronActivity.this, SosialisasiViaVideotronActivity.class);
                startActivity(penangananKasus);
                finish();
                break;
            case R.id.imgAdd:
                Intent tambahPenangananKasus = new Intent(DetilSosialisasiviavideotronActivity.this, TambahSosialisasiViaVideotronActivity.class);
                startActivity(tambahPenangananKasus);
                finish();
                break;
            case R.id.imglokasiPenempatan:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogView = getLayoutInflater().inflate(R.layout.popup_custom, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialog = new AlertDialog.Builder(DetilSosialisasiviavideotronActivity.this)
                        .setCancelable(false)
                        .setView(dialogView).create();
                initDialogView(dialogView);

                alertDialog.show();
                //Membuat alert dialog layout menjadi responsive ketika keyboard muncul
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                //Membuat background menjadi transparent
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                txtTitle.setText("BUKA PETA LOKASI");
                iconTitle.setImageResource(R.drawable.pin_copy);

                btnTidak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnYa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();

                        Intent logoutIntent = new Intent(DetilSosialisasiviavideotronActivity.this, MapsActivity.class);
                        startActivity(logoutIntent);
                    }
                });

                imgClosePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                break;
            case R.id.btnHapus:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogViewJml = getLayoutInflater().inflate(R.layout.popup_custom, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialogJml = new AlertDialog.Builder(DetilSosialisasiviavideotronActivity.this)
                        .setCancelable(false)
                        .setView(dialogViewJml).create();
                initDialogView(dialogViewJml);

                alertDialogJml.show();
                //Membuat alert dialog layout menjadi responsive ketika keyboard muncul
                alertDialogJml.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                //Membuat background menjadi transparent
                alertDialogJml.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                txtTitle.setText("HAPUS DATA");
                iconTitle.setImageResource(R.drawable.trash_copy);

                btnTidak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogJml.dismiss();
                    }
                });

                btnYa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent logoutIntent = new Intent(DetilSosialisasiviavideotronActivity.this, LoginActivity.class);
                        startActivity(logoutIntent);
                        finish();
                    }
                });

                imgClosePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogJml.dismiss();
                    }
                });
                break;
            case R.id.btnUbah:
                Intent ubahIntent = new Intent(DetilSosialisasiviavideotronActivity.this, TambahSosialisasiViaVideotronActivity.class);
                ubahIntent.putExtra("id", IDIntent);
                ubahIntent.putExtra("nm_instansi", pelaksanaIntent);
                ubahIntent.putExtra("mode_form", "ubah");
                startActivity(ubahIntent);
                finish();
                break;
        }
    }

    //Inflate setiap components ui yang akan kita implement di alert dialog
    private void initDialogView(View dialogView) {
        btnTidak = (Button) dialogView.findViewById(R.id.btnTidak);
        btnYa = (Button) dialogView.findViewById(R.id.btnYa);
        imgClosePopup = (ImageView) dialogView.findViewById(R.id.imgClosePopup);
        txtTitle = (TextView) dialogView.findViewById(R.id.txtTitle);
        iconTitle = (ImageView) dialogView.findViewById(R.id.iconTitle);
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

//            progressBar.setVisibility(View.VISIBLE);
//            expandList.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/disemvideotron/"+IDIntent)
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
                    JSONObject data = jsonObject.getJSONObject("data");

                    edNoid.setText(!data.getString("id").equals("null") ? data.getString("id") : "-");
                    edTglPelaksanaan.setText(!data.getString("tanggal_pelaksanaan").equals("null") ? getNewDate(data.getString("tanggal_pelaksanaan")) : "-");
                    edlokasipenempatan.setText(!data.getString("lokasi_penempatan").equals("null") ? data.getString("lokasi_penempatan") : "");
                    edPelaksana.setText(pelaksanaIntent);

//                    progressBar.setVisibility(View.GONE);
//                    expandList.setVisibility(View.VISIBLE);
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
//                    progressBar.setVisibility(View.GONE);

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetilSosialisasiviavideotronActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(DetilSosialisasiviavideotronActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    }
                }
            } catch(JSONException e){
                    e.printStackTrace();
            }
        }
    }

    public String getNewDate(String date){
        String newDate = "01-01-2016";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = simpleDateFormat.parse(date);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
            newDate = newFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetilSosialisasiviavideotronActivity.this, SosialisasiViaVideotronActivity.class));
        finish();
    }
}
