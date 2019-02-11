package com.bnn.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bnn.Activity.Preview.ImagePreviewActivity;
import com.bnn.Adapter.ListBarangBuktiAdapter;
import com.bnn.Modal.BarangBukti;
import com.bnn.Modal.Listmenu;
import com.bnn.R;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.Configuration;
import com.bnn.Utils.DateUtils;
import com.bnn.Utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ferdinandprasetyo on 11/4/17.
 */

public class DetilRaziaActivity extends ActivityBase implements View.OnClickListener {
    private static final String TAG = DetilRaziaActivity.class.getSimpleName();

    ImageView imgBack, imgTambah, toggleMenu, imgBukaLokasi, imgClosePopup, iconTitle, imgUpload1, imgUpload2, imgUpload3;
    Button btnHapus, btnUbah, btnYa, btnTidak, btnKembali;
    EditText edNoid, edNoLkn, edTglRazia, edLokasiRazia, edTerindikasi, edOrang, edUraianSingkat, edKeterangan, edJumlahBarbuk;
    TextView txtTitle;
    ScrollView layoutDetilRazia;
    LinearLayout listLayoutTerdeteksi;

    PreferenceUtils prefUtils;
    String raziaIDIntent;
    String dataRazia;

    String foto1 = "", foto2 = "", foto3 = "";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_detil_razia, linearLayout);

        raziaIDIntent = getIntent().getStringExtra("razia_id");

        prefUtils = PreferenceUtils.getInstance(this);
        progressDialog = new ProgressDialog(this);

        dataRazia = prefUtils.getRazia() != null && !prefUtils.getRazia().equals("") ? prefUtils.getRazia() : "";

        toggleMenu = (ImageView) findViewById(R.id.imgToogleMenu);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
//        imgBukaLokasi = (ImageView) findViewById(R.id.imgLokasiRazia);

        //Ditambahkan pada 07 No 2017
        edNoid = (EditText) findViewById(R.id.edNoId);
        edNoLkn = (EditText) findViewById(R.id.edNoLKN);
        edTglRazia = (EditText) findViewById(R.id.edTanggalRazia);
        edLokasiRazia = (EditText) findViewById(R.id.edLokasiRazia);
        edTerindikasi = (EditText) findViewById(R.id.edJumlahTerindikasi);
        edOrang = (EditText) findViewById(R.id.edJumlahOrang);
        edUraianSingkat = (EditText) findViewById(R.id.edUraianPelaksanaan);
        edKeterangan = (EditText) findViewById(R.id.edKeteranganLain);
        edJumlahBarbuk = (EditText) findViewById(R.id.edJumlahBarangBukti);

        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        btnUbah = (Button) findViewById(R.id.btnUbah);
        btnHapus = (Button) findViewById(R.id.btnHapus);
        layoutDetilRazia = (ScrollView) findViewById(R.id.layoutDetilRazia);
//        listLayoutTerdeteksi = (LinearLayout) findViewById(R.id.layoutListTerdeteksi);

        toggleMenu.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgTambah.setOnClickListener(this);
//        imgBukaLokasi.setOnClickListener(this);
        btnHapus.setOnClickListener(this);
        btnUbah.setOnClickListener(this);

        imgUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previewActivity = new Intent(DetilRaziaActivity.this, ImagePreviewActivity.class);

                if (foto1 != null) {
                    if (!foto1.equals("")) {
                        previewActivity.putExtra("foto", foto1);
                        previewActivity.putExtra("title", "Foto 1");

                        startActivity(previewActivity);
                    }
                }
            }
        });

        imgUpload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previewActivity = new Intent(DetilRaziaActivity.this, ImagePreviewActivity.class);

                if (foto2 != null) {
                    if (!foto2.equals("")) {
                        previewActivity.putExtra("foto", foto2);
                        previewActivity.putExtra("title", "Foto 2");

                        startActivity(previewActivity);
                    }
                }
            }
        });

        imgUpload3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previewActivity = new Intent(DetilRaziaActivity.this, ImagePreviewActivity.class);

                if (foto3 != null) {
                    if (!foto3.equals("")) {
                        previewActivity.putExtra("foto", foto3);
                        previewActivity.putExtra("title", "Foto 3");

                        startActivity(previewActivity);
                    }
                }
            }
        });

//        initViewTerdeteksi();

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
//                    Log.d("Array", "Array: "+raziaIDIntent+" "+String.valueOf(jsonArray.length()-i));
//
//                    if (raziaIDIntent.equals(jsonObject.getString("id"))) {
//                        edNoid.setText(raziaIDIntent);
//                        edTglRazia.setText(!jsonObject.getString("tgl").equals("") ? getNewDate(jsonObject.getString("tgl")) : "-");
//                        edLokasiRazia.setText(!jsonObject.getString("lokasi").equals("") ? jsonObject.getString("lokasi") : "-");
//                        edTerindikasi.setText(!jsonObject.getString("jumlah_terindikasi").equals("") ? jsonObject.getString("jumlah_terindikasi") : "0");
//                        edOrang.setText(!jsonObject.getString("jumlah_orang").equals("") ? jsonObject.getString("jumlah_orang") : "0");
//                        edUraianSingkat.setText(!jsonObject.getString("uraian").equals("") ? jsonObject.getString("uraian") : "-");
//                        edKeterangan.setText(!jsonObject.getString("keterangan_lain").equals("") ? jsonObject.getString("keterangan_lain") : "-");
//                        edJumlahBarbuk.setText(!jsonObject.getString("jumlah_barbuk").equals("") ? jsonObject.getString("jumlah_barbuk") : "0");
//                        foto1 = !jsonObject.getString("foto_satu").equalsIgnoreCase("null") ? jsonObject.getString("foto_satu") : "";
//                        foto2 = !jsonObject.getString("foto_dua").equalsIgnoreCase("null") ? jsonObject.getString("foto_dua") : "";
//                        foto3 = !jsonObject.getString("foto_tiga").equalsIgnoreCase("null") ? jsonObject.getString("foto_tiga") : "";
//
//                        loadFoto(foto1, imgUpload1);
//                        loadFoto(foto2, imgUpload2);
//                        loadFoto(foto3, imgUpload3);
//                    }
//
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

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

    private void loadFoto(String base64, ImageView fotoFrame) {
        if (!base64.equalsIgnoreCase("")) {
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            fotoFrame.setImageBitmap(decodedByte);
        }
    }

//    private void initViewTerdeteksi() {
//        edTerdeteksi.setVisibility(View.GONE);
//
//        LayoutInflater inflaterPenyidik = getLayoutInflater();
//        final LinearLayout layoutPenyidik = (LinearLayout) inflaterPenyidik.inflate(R.layout.item_listterdeteksi, listLayoutTerdeteksi, false);
//
//        ImageView imgHapusPenyidik = (ImageView)layoutPenyidik.findViewById(R.id.imgHapusTerdeteksi);
//        EditText edNamaTerdeteksi = (EditText) layoutPenyidik.findViewById(R.id.edNamaTerdeteksi);
//        Spinner spinnerJekel = (Spinner) layoutPenyidik.findViewById(R.id.spinnerJekel);
//        Spinner spinnerWaneg = (Spinner) layoutPenyidik.findViewById(R.id.spinnerWaneg);
//
//        imgHapusPenyidik.setVisibility(View.GONE);
//        edNamaTerdeteksi.setFocusable(false);edNamaTerdeteksi.setCursorVisible(false);
//        spinnerJekel.setEnabled(false);
//        spinnerWaneg.setEnabled(false);
//
//        String[] listJekel = {"Laki-Laki", "Perempuan"};
//        String[] listWN = {/*"Amerika",*/ "Indonesia", "Malaysia", "Singapura"};
//
//        spinnerJekel.setAdapter(new ArrayAdapter<String>(DetilRaziaActivity.this,
//                R.layout.spinner_item_small, listJekel));
//        spinnerWaneg.setAdapter(new ArrayAdapter<String>(DetilRaziaActivity.this,
//                R.layout.spinner_item_small, listWN));
//
//        imgHapusPenyidik.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listLayoutTerdeteksi.removeView(layoutPenyidik);
//
//                if (listLayoutTerdeteksi.getChildCount() == 0) {
//                    edTerdeteksi.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//
//        listLayoutTerdeteksi.addView(layoutPenyidik);
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgToogleMenu:
                menuDrawerLayout.openDrawer(linearLayout2);
                break;
            case R.id.imgBack:
                Intent penangananKasus = new Intent(DetilRaziaActivity.this, RaziaActivity.class);
                startActivity(penangananKasus);
                finish();
                break;
            case R.id.imgAdd:
                Intent tambahRazia = new Intent(DetilRaziaActivity.this, TambahRaziaActivity.class);
                startActivity(tambahRazia);
                finish();
                break;
//            case R.id.imgLokasiRazia:
//                //Inflate custom layout untuk menampilkan form out-office
//                View dialogView = getLayoutInflater().inflate(R.layout.popup_custom, null);
//                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
//                final AlertDialog alertDialog = new AlertDialog.Builder(DetilRaziaActivity.this)
//                        .setCancelable(false)
//                        .setView(dialogView).create();
//                initDialogView(dialogView);
//
//                alertDialog.show();
//                //Membuat alert dialog layout menjadi responsive ketika keyboard muncul
//                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                //Membuat background menjadi transparent
//                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//                txtTitle.setText("BUKA PETA LOKASI");
//                iconTitle.setImageResource(R.drawable.pin_copy);
//
//                btnTidak.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                    }
//                });
//
//                btnYa.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//
//                        Intent bukaMap = new Intent(DetilRaziaActivity.this, MapsActivity.class);
//                        startActivity(bukaMap);
//                    }
//                });
//
//                imgClosePopup.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                    }
//                });
//                break;
            case R.id.btnHapus:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogViewJml = getLayoutInflater().inflate(R.layout.popup_custom, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialogJml = new AlertDialog.Builder(DetilRaziaActivity.this)
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
                        new DataDeleteTask().execute();
//                        JSONArray jsonArray = null;
//                        JSONArray newJsonArray = null;
//                        try {
//                            jsonArray = new JSONArray(dataRazia);
//                            newJsonArray = new JSONArray();
//
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                                if (raziaIDIntent.equals(jsonObject.getString("id"))) {
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                        jsonArray.remove(i);
//
//                                        newJsonArray = jsonArray;
//                                    }
//                                } else {
//                                    newJsonArray.put(jsonObject);
//                                }
//                            }
//
//                            prefUtils.setRazia(newJsonArray.toString());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        alertDialogJml.dismiss();
//                        Intent logoutIntent = new Intent(DetilRaziaActivity.this, RaziaActivity.class);
//                        startActivity(logoutIntent);
//                        finish();
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
                Intent ubahIntent = new Intent(DetilRaziaActivity.this, TambahRaziaActivity.class);
                ubahIntent.putExtra("razia_id", raziaIDIntent);
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

            progressDialog.setMessage("Mengambil data.");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            RequestBody requestBody = null;

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/berantasrazia/"+raziaIDIntent)
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
            Log.d(TAG, "Result: "+result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
                    JSONObject data = jsonObject.getJSONObject("data");

                    edNoid.setText(!data.getString("id").equals("") ? data.getString("id") : "-");
                    edTglRazia.setText(!data.getString("tgl_razia").equals("") ? getNewDate(data.getString("tgl_razia")) : "-");
                    edLokasiRazia.setText(!data.getString("lokasi").equals("") ? data.getString("lokasi") : "-");
                    edTerindikasi.setText(!data.getString("jumlah_terindikasi").equals("") ? data.getString("jumlah_terindikasi") : "0");
                    edOrang.setText(!data.getString("jumlah_dirazia").equals("") ? data.getString("jumlah_dirazia") : "0");
                    edUraianSingkat.setText(!data.getString("uraian_singkat").equals("") ? data.getString("uraian_singkat") : "-");
                    edKeterangan.setText(!data.getString("keterangan_lainnya").equals("") ? data.getString("keterangan_lainnya") : "-");
                    edJumlahBarbuk.setText(!data.getString("jumlah_ditemukan").equals("") ? data.getString("jumlah_ditemukan") : "0");

                    if (edKeterangan.getText().toString().equals("null")){
                        edKeterangan.setText("");
                    }
                    if (edTerindikasi.getText().toString().equals("null")){
                        edTerindikasi.setText("0");
                    }
                    if (edJumlahBarbuk.getText().toString().equals("null")){
                        edJumlahBarbuk.setText("0");
                    }

                    foto1 = !data.getString("foto1").equalsIgnoreCase("null") ? data.getString("foto1") : "";
                    foto2 = !data.getString("foto2").equalsIgnoreCase("null") ? data.getString("foto2") : "";
                    foto3 = !data.getString("foto3").equalsIgnoreCase("null") ? data.getString("foto3") : "";

                    loadFoto(foto1, imgUpload1);
                    loadFoto(foto2, imgUpload2);
                    loadFoto(foto3, imgUpload3);

                    progressDialog.dismiss();
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    progressDialog.dismiss();

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetilRaziaActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(DetilRaziaActivity.this, LoginActivity.class);
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

    class DataDeleteTask extends AsyncTask<Bitmap, Integer, String> {
        private String responseServer;
        private String keyKataKunci;

        public DataDeleteTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseServer = "";

            progressDialog.setMessage("Menghapus data..");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            Headers headers = new Headers.Builder().add("Authorization", "Bearer " + prefUtils.getTokenKey())
                    .add("Accept", "application/json").build();

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/berantasrazia/"+raziaIDIntent)
                    .headers(headers)
                    .delete()
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
            progressDialog.dismiss();

            Log.d("Result", result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
                    showToast("Berhasil menghapus data.");

                    Intent logoutIntent = new Intent(DetilRaziaActivity.this, RaziaActivity.class);
                    startActivity(logoutIntent);
                    finish();
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetilRaziaActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(DetilRaziaActivity.this, LoginActivity.class);
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

    public String getNewDateForServer(String date){
        String newDate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d = simpleDateFormat.parse(date);
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
            newDate = newFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetilRaziaActivity.this, RaziaActivity.class));
        finish();
    }
}
