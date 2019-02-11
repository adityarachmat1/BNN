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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
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
import android.widget.Toast;

import com.bnn.Activity.Preview.ImagePreviewActivity;
import com.bnn.Adapter.ListBarangBuktiAdapter;
import com.bnn.Modal.BarangBukti;
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
import java.util.Date;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ferdinandprasetyo on 11/4/17.
 */

public class DetilPemusnahanBarangBuktiActivity extends ActivityBase implements View.OnClickListener {

    ImageView imgBack, imgTambah, toggleMenu, imgBukaLokasi, imgLihatBarangBukti, imgClosePopup, iconTitle, imgUpload1, imgUpload2, imgUpload3;
    Button btnHapus, btnUbah, btnYa, btnTidak, btnKembali;
    EditText edNoid, edNoLkn, edTglPemusnahan, edLokasiPemusnahan, edJmlBarbuk, edPenyidik, edPelaksana, edKeteranganLain/*,  edPelaku*/;
    TextView txtTitle;
    ScrollView layoutDetilPemusnahan;
    RelativeLayout layoutDetilBarangBukti;

    //Create list barang bukti
    ListBarangBuktiAdapter listBarangBuktiAdapter;
    ArrayList<BarangBukti> barangBuktiArrayList = new ArrayList<>();
    ListView listViewBarangBukti;

    PreferenceUtils prefUtils;
    String pemusnahanIDIntent;

    String foto1 = "", foto2 = "", foto3 = "";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_detil_pemusnahanbarangbukti, linearLayout);

        pemusnahanIDIntent = getIntent().getStringExtra("pemusnahan_id");
//        arrayBarbuk = getIntent().getStringExtra("array_barbuk");

        prefUtils = PreferenceUtils.getInstance(this);
        progressDialog = new ProgressDialog(this);

        toggleMenu = (ImageView) findViewById(R.id.imgToogleMenu);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        imgLihatBarangBukti = (ImageView) findViewById(R.id.imgJumlahBarangBukti);

        //Ditambahkan pada 05 No 2017
        edNoid = (EditText) findViewById(R.id.edNoId);
        edTglPemusnahan = (EditText) findViewById(R.id.edTanggalPemusnahan);
        edLokasiPemusnahan = (EditText) findViewById(R.id.edLokasiPemusnahan);
        edJmlBarbuk = (EditText) findViewById(R.id.edJumlahBarangBukti);
        edPenyidik = (EditText) findViewById(R.id.edPenyidik);
        edPelaksana = (EditText) findViewById(R.id.edPelaksana);
        edKeteranganLain = (EditText) findViewById(R.id.edKeteranganLain);

        //Ditambahkan pada 06 No 2017
        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        btnUbah = (Button) findViewById(R.id.btnHapus);
        btnHapus = (Button) findViewById(R.id.btnUbah);
        layoutDetilPemusnahan = (ScrollView) findViewById(R.id.layoutDetilPemusnahan);

        //Init view untuk layout list barang bukti
        listViewBarangBukti = (ListView) findViewById(R.id.listviewBarangBukti);
        layoutDetilBarangBukti = (RelativeLayout) findViewById(R.id.layoutDetilBarangBukti);
        btnKembali = (Button) findViewById(R.id.btnKembaliBarangBukti);

        toggleMenu.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgTambah.setOnClickListener(this);
        imgLihatBarangBukti.setOnClickListener(this);
        btnHapus.setOnClickListener(this);
        btnUbah.setOnClickListener(this);
        btnKembali.setOnClickListener(this);

        imgUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previewActivity = new Intent(DetilPemusnahanBarangBuktiActivity.this, ImagePreviewActivity.class);

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
                Intent previewActivity = new Intent(DetilPemusnahanBarangBuktiActivity.this, ImagePreviewActivity.class);

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
                Intent previewActivity = new Intent(DetilPemusnahanBarangBuktiActivity.this, ImagePreviewActivity.class);

                if (foto3 != null) {
                    if (!foto3.equals("")) {
                        previewActivity.putExtra("foto", foto3);
                        previewActivity.putExtra("title", "Foto 3");

                        startActivity(previewActivity);
                    }
                }
            }
        });

//        if (!arrayBarbuk.equals("")) {
//            try {
//                JSONArray barbukObj = new JSONArray(arrayBarbuk);
//
//                edJmlBarbuk.setText(String.valueOf(barbukObj.length()));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

        loadData();
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
            case R.id.imgToogleMenu:
                menuDrawerLayout.openDrawer(linearLayout2);
                break;
            case R.id.imgBack:
                Intent penangananKasus = new Intent(DetilPemusnahanBarangBuktiActivity.this, PemusnahanBarangBuktiActivity.class);
                startActivity(penangananKasus);
                finish();
                break;
            case R.id.imgAdd:
                Intent tambahPenangananKasus = new Intent(DetilPemusnahanBarangBuktiActivity.this, TambahPemusnahanBarangBuktiActivity.class);
                startActivity(tambahPenangananKasus);
                finish();
                break;
            case R.id.imgJumlahBarangBukti:
                layoutDetilPemusnahan.setVisibility(View.GONE);
                layoutDetilBarangBukti.setVisibility(View.VISIBLE);

//                if (!arrayBarbuk.equals("")) {
//                    try {
//                        JSONArray barbukObj = new JSONArray(arrayBarbuk);
//
//                        for (int i = 0;i < barbukObj.length();i++) {
//                            String namaBarbuk = !((JSONObject)barbukObj.get(i)).getString("nm_brgbukti").equalsIgnoreCase("null") ? ((JSONObject)barbukObj.get(i)).getString("nm_brgbukti") : "-";
//                            double jmlBarbuk = Double.parseDouble(!((JSONObject)barbukObj.get(i)).getString("jumlah_barang_bukti").equalsIgnoreCase("null") ? ((JSONObject)barbukObj.get(i)).getString("jumlah_barang_bukti") : "0");
//                            String satuanBarbuk = !((JSONObject)barbukObj.get(i)).getString("nm_satuan").equalsIgnoreCase("null") ? ((JSONObject)barbukObj.get(i)).getString("nm_satuan") : "-";
//
//                            barangBuktiArrayList.add(new BarangBukti(namaBarbuk,
//                                    String.valueOf(jmlBarbuk),
//                                    satuanBarbuk));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                listBarangBuktiAdapter = new ListBarangBuktiAdapter(DetilPemusnahanBarangBuktiActivity.this, barangBuktiArrayList);
//                listViewBarangBukti.setAdapter(listBarangBuktiAdapter);
//                break;
            case R.id.btnHapus:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogViewJml = getLayoutInflater().inflate(R.layout.popup_custom, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialogJml = new AlertDialog.Builder(DetilPemusnahanBarangBuktiActivity.this)
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
                        alertDialogJml.dismiss();
                        new DataDeleteTask().execute();
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
                Intent ubahIntent = new Intent(DetilPemusnahanBarangBuktiActivity.this, TambahPemusnahanBarangBuktiActivity.class);
                ubahIntent.putExtra("pemusnahan_id", pemusnahanIDIntent);
//                ubahIntent.putExtra("array_barbuk", arrayBarbuk);
                ubahIntent.putExtra("mode_form", "ubah");
                startActivity(ubahIntent);
                finish();
                break;
            case R.id.btnKembaliBarangBukti:
                layoutDetilPemusnahan.setVisibility(View.VISIBLE);
                layoutDetilBarangBukti.setVisibility(View.GONE);

                barangBuktiArrayList.clear();
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

            progressDialog.setMessage("Mengambil data..");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            RequestBody requestBody = null;

            Request httpRequest = new Request.Builder()
                    //.url(Configuration.baseUrl+"/api/pemusnahanmobile/"+pemusnahanIDIntent)
                    .url(Configuration.baseUrl + "/api/pemusnahandetail/" + pemusnahanIDIntent)
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
            progressDialog.dismiss();

            Log.d("test data dapet apa", result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
                    JSONObject data = jsonObject.getJSONObject("data");

                    /*
                    edNoid.setText(!data.getString("id").equals("null") ? data.getString("id") : "-");
                    edTglPemusnahan.setText(!data.getString("tgl_pemusnahan").equals("null") ? getNewDate(data.getString("tgl_pemusnahan")) : "-");
                    edPelaksana.setText(!data.getString("created_by_username").equals("null") ? data.getString("created_by_username") : "-");
                    edLokasiPemusnahan.setText(!data.getString("lokasi").equals("null") ? data.getString("lokasi") : "-");
                    edKeteranganLain.setText(!data.getString("keterangan_lainnya").equals("null") ? data.getString("keterangan_lainnya") : "-");
                    */

                    edNoid.setText(!data.getString("id").equals("null") ? data.getString("id") : "-");
                    edTglPemusnahan.setText(!data.getString("tgl_pemusnahan").equals("null") ? getNewDate(data.getString("tgl_pemusnahan")): "-");
                    edPelaksana.setText(!data.getString("created_by_username").equals("null") ? data.getString("created_by_username") : "-");
                    edLokasiPemusnahan.setText("");
                    edKeteranganLain.setText("");
                    edJmlBarbuk.setText(!data.getString("jumlah_barang_bukti").equals("null") ? data.getString("jumlah_barang_bukti") : "-");

                    foto1 = !data.getString("foto1").equalsIgnoreCase("null") ? data.getString("foto1") : "";
                    foto2 = !data.getString("foto2").equalsIgnoreCase("null") ? data.getString("foto2") : "";
                    foto3 = !data.getString("foto3").equalsIgnoreCase("null") ? data.getString("foto3") : "";

                    loadFoto(foto1, imgUpload1);
                    loadFoto(foto2, imgUpload2);
                    loadFoto(foto3, imgUpload3);
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetilPemusnahanBarangBuktiActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(DetilPemusnahanBarangBuktiActivity.this, LoginActivity.class);
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

            RequestBody requestBody = null;

            Headers headers = new Headers.Builder().add("Authorization", "Bearer " + prefUtils.getTokenKey())
                    .add("Accept", "application/json").build();

            Request httpRequest = new Request.Builder()
                    //.url(Configuration.baseUrl+"/api/pemusnahanmobile/"+pemusnahanIDIntent)
                    .url(Configuration.baseUrl+"/api/pemusnahan/"+pemusnahanIDIntent)
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
                    Toast.makeText(DetilPemusnahanBarangBuktiActivity.this, "Berhasil menghapus data pemusnahan.", Toast.LENGTH_LONG).show();

                    Intent logoutIntent = new Intent(DetilPemusnahanBarangBuktiActivity.this, PemusnahanBarangBuktiActivity.class);
                    startActivity(logoutIntent);
                    finish();
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetilPemusnahanBarangBuktiActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(DetilPemusnahanBarangBuktiActivity.this, LoginActivity.class);
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

    private void loadFoto(String base64, ImageView fotoFrame) {
        if (!base64.equalsIgnoreCase("")) {
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            fotoFrame.setImageBitmap(decodedByte);
        }
    }

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(DetilPemusnahanBarangBuktiActivity.this, PemusnahanBarangBuktiActivity.class);
//        startActivity(intent);
//        finish();
//    }
}
