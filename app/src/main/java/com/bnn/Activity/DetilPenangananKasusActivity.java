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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bnn.Activity.Preview.ImagePreviewActivity;
import com.bnn.Adapter.ListBarangBuktiAdapter;
import com.bnn.Modal.BarangBukti;
import com.bnn.Modal.Tersangka;
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
import okhttp3.Response;

/**
 * Created by ferdinandprasetyo on 11/4/17.
 */

public class DetilPenangananKasusActivity extends ActivityBase implements View.OnClickListener {

    ImageView imgBack, imgTambah, toggleMenu, imgBukaLokasi, imgLihatBarangBukti, imgClosePopup, iconTitle, imgUpload1, imgUpload2, imgUpload3;
    Button btnHapus, btnUbah, btnYa, btnTidak, btnKembali;
    EditText edNoid, edNoLkn, edTglKejadian, edTkpKasus, edJmlBarbuk, edPelaksana, edPenyidik, edNamaPelaku, edJekelPelaku, edUraianSingkat, edKeteranganLain;
    TextView txtTitle, txtMessage;
    ScrollView layoutDetilKasus;
    RelativeLayout layoutDetilBarangBukti;
    LinearLayout layoutPelakuKosong, listLayoutPelaku;

    //Create list barang bukti
    ListBarangBuktiAdapter listBarangBuktiAdapter;
    ArrayList<BarangBukti> barangBuktiArrayList = new ArrayList<>();
    ListView listViewBarangBukti;

    //Ditambahkan pada 12 Nov 2017
    ArrayList<Tersangka> pelakuArrayList = new ArrayList<>();

    //Ditambahkan pada 16 Nov 2017
    String foto1, foto2, foto3;

    PreferenceUtils prefUtils;
    String eventIDIntent, pelaksanaIntent, arrayBarbuk, arrayTersangka;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_detil_penanganankasus, linearLayout);

        eventIDIntent = getIntent().getStringExtra("event_id");
        pelaksanaIntent = getIntent().getStringExtra("pelaksana");
        arrayBarbuk = getIntent().getStringExtra("array_barbuk");
        arrayTersangka = getIntent().getStringExtra("array_tersangka");

        prefUtils = PreferenceUtils.getInstance(this);
        progressDialog = new ProgressDialog(this);

        toggleMenu = (ImageView) findViewById(R.id.imgToogleMenu);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        imgBukaLokasi = (ImageView) findViewById(R.id.imgTkpKasus);
        imgLihatBarangBukti = (ImageView) findViewById(R.id.imgJumlahBarangBukti);

        //Ditambahkan pada 05 No 2017
        edNoid = (EditText) findViewById(R.id.edNoId);
        edNoLkn = (EditText) findViewById(R.id.edNoLKN);
        edTglKejadian = (EditText) findViewById(R.id.edTanggalKejadian);
        edUraianSingkat = (EditText) findViewById(R.id.edUraianSingkatKejadian);
        edKeteranganLain = (EditText) findViewById(R.id.edKeteranganLain);
        edTkpKasus = (EditText) findViewById(R.id.edTkpKasus);
        edJmlBarbuk = (EditText) findViewById(R.id.edJumlahBarangBukti);
        edPelaksana = (EditText) findViewById(R.id.edPelaksana);
        edNamaPelaku = (EditText) findViewById(R.id.edNamaPelaku);
        edJekelPelaku = (EditText) findViewById(R.id.edJekelPelaku);

        //Ditambahkan pada 06 No 2017
        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        btnHapus = (Button) findViewById(R.id.btnHapus);
        btnUbah = (Button) findViewById(R.id.btnUbah);
        layoutDetilKasus = (ScrollView) findViewById(R.id.layoutDetilKasus);
        layoutPelakuKosong = (LinearLayout) findViewById(R.id.layoutPelakuKosong);
        listLayoutPelaku = (LinearLayout) findViewById(R.id.listLayoutPelaku); 

        //Init view untuk layout list barang bukti
        listViewBarangBukti = (ListView) findViewById(R.id.listviewBarangBukti);
        layoutDetilBarangBukti = (RelativeLayout) findViewById(R.id.layoutDetilBarangBukti);
        btnKembali = (Button) findViewById(R.id.btnKembaliBarangBukti);

        toggleMenu.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgTambah.setOnClickListener(this);
        imgLihatBarangBukti.setOnClickListener(this);
        imgBukaLokasi.setOnClickListener(this);
        btnHapus.setOnClickListener(this);
        btnUbah.setOnClickListener(this);
        btnKembali.setOnClickListener(this);

        imgUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previewActivity = new Intent(DetilPenangananKasusActivity.this, ImagePreviewActivity.class);

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
                Intent previewActivity = new Intent(DetilPenangananKasusActivity.this, ImagePreviewActivity.class);

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
                Intent previewActivity = new Intent(DetilPenangananKasusActivity.this, ImagePreviewActivity.class);

                if (foto3 != null) {
                    if (!foto3.equals("")) {
                        previewActivity.putExtra("foto", foto3);
                        previewActivity.putExtra("title", "Foto 3");

                        startActivity(previewActivity);
                    }
                }
            }
        });

        if (!arrayBarbuk.equals("")) {
            try {
                JSONArray barbukObj = new JSONArray(arrayBarbuk);

                edJmlBarbuk.setText(String.valueOf(barbukObj.length()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!arrayTersangka.equals("")) {
            try {
                JSONArray pelakuObj = new JSONArray(arrayTersangka);

                if (pelakuObj.length() > 0) {
                    layoutPelakuKosong.setVisibility(View.GONE);
                } else {
                    layoutPelakuKosong.setVisibility(View.VISIBLE);
                }

                for (int i = 0;i < pelakuObj.length();i++) {
                    LayoutInflater inflaterPelaku = getLayoutInflater();
                    final LinearLayout layoutPelaku = (LinearLayout) inflaterPelaku.inflate(R.layout.item_detil_listpelaku, listLayoutPelaku, false);

                    EditText edNama = (EditText)layoutPelaku.findViewById(R.id.edNama);
                    EditText edJekel = (EditText)layoutPelaku.findViewById(R.id.edJenisKelamin);
                    EditText edWaneg = (EditText)layoutPelaku.findViewById(R.id.edKewarganegaraan);
                    EditText edTempatLahir = (EditText)layoutPelaku.findViewById(R.id.edTempatLahir);
                    EditText edTanggalLahir = (EditText)layoutPelaku.findViewById(R.id.edTanggalLahir);

                    edNama.setText(!((JSONObject)pelakuObj.get(i)).getString("tersangka_nama").equalsIgnoreCase("null") ? ((JSONObject)pelakuObj.get(i)).getString("tersangka_nama") : "-");
                    edJekel.setText(((JSONObject)pelakuObj.get(i)).getString("kode_jenis_kelamin").equals("L") ? "Laki-Laki" : "Perempuan");
                    edWaneg.setText(!((JSONObject)pelakuObj.get(i)).getString("nama_negara").equalsIgnoreCase("null") ? ((JSONObject)pelakuObj.get(i)).getString("nama_negara") : "-");
                    edTempatLahir.setText(!((JSONObject)pelakuObj.get(i)).getString("tersangka_tempat_lahir").equalsIgnoreCase("null") ? ((JSONObject)pelakuObj.get(i)).getString("tersangka_tempat_lahir") : "-");
                    edTanggalLahir.setText(!((JSONObject)pelakuObj.get(i)).getString("tersangka_tanggal_lahir").equalsIgnoreCase("null") ? getNewDate(((JSONObject)pelakuObj.get(i)).getString("tersangka_tanggal_lahir")) : "");
//                    edTanggalLahir.setText(getNewDate(((JSONObject)pelakuObj.get(i)).getString("tersangka_tanggal_lahir")));

                    listLayoutPelaku.addView(layoutPelaku);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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
                Intent penangananKasus = new Intent(DetilPenangananKasusActivity.this, PenangananKasusNarkobaActivity.class);
                startActivity(penangananKasus);
                finish();
                break;
            case R.id.imgAdd:
                Intent tambahPenangananKasus = new Intent(DetilPenangananKasusActivity.this, TambahPenangananKasusNarkobaActivity.class);
                startActivity(tambahPenangananKasus);
                finish();
                break;
            case R.id.imgJumlahBarangBukti:
                layoutDetilKasus.setVisibility(View.GONE);
                layoutDetilBarangBukti.setVisibility(View.VISIBLE);

                if (!arrayBarbuk.equals("")) {
                    try {
                        JSONArray barbukObj = new JSONArray(arrayBarbuk);

                        for (int i = 0;i < barbukObj.length();i++) {
                            String namaBarbuk = ((JSONObject)barbukObj.get(i)).getString("nm_brgbukti");
                            double jmlBarbuk = Double.parseDouble(((JSONObject)barbukObj.get(i)).getString("jumlah_barang_bukti"));
                            String satuanBarbuk = ((JSONObject)barbukObj.get(i)).getString("nm_satuan");

                            barangBuktiArrayList.add(new BarangBukti(namaBarbuk,
                                    String.valueOf(jmlBarbuk),
                                    satuanBarbuk));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                listBarangBuktiAdapter = new ListBarangBuktiAdapter(DetilPenangananKasusActivity.this, barangBuktiArrayList);
                listViewBarangBukti.setAdapter(listBarangBuktiAdapter);
                break;
            case R.id.imgTkpKasus:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogView = getLayoutInflater().inflate(R.layout.popup_custom, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialog = new AlertDialog.Builder(DetilPenangananKasusActivity.this)
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

                        Intent bukaMap = new Intent(DetilPenangananKasusActivity.this, MapsActivity.class);
                        startActivity(bukaMap);
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
                //Inflate custom layout
                View dialogViewJml = getLayoutInflater().inflate(R.layout.popup_custom, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan box
                final AlertDialog alertDialogJml = new AlertDialog.Builder(DetilPenangananKasusActivity.this)
                        .setCancelable(false)
                        .setView(dialogViewJml).create();
                initDialogView(dialogViewJml);

                alertDialogJml.show();
                //Membuat alert dialog layout menjadi responsive ketika keyboard muncul
                alertDialogJml.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                //Membuat background menjadi transparent
                alertDialogJml.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                txtTitle.setText("HAPUS DATA");

                txtMessage.setText("Apakah anda yakin ingin menghapus data ini?");
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
                Intent ubahIntent = new Intent(DetilPenangananKasusActivity.this, TambahPenangananKasusNarkobaActivity.class);
                ubahIntent.putExtra("event_id", eventIDIntent);
                ubahIntent.putExtra("pelaksana", pelaksanaIntent);
                ubahIntent.putExtra("mode_form", "ubah");
                ubahIntent.putExtra("array_barbuk", arrayBarbuk);
                ubahIntent.putExtra("array_tersangka", arrayTersangka);
                startActivity(ubahIntent);
                finish();
                break;
            case R.id.btnKembaliBarangBukti:
                layoutDetilKasus.setVisibility(View.VISIBLE);
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
        txtMessage = (TextView) dialogView.findViewById(R.id.txtMessage);
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

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/kasus/"+eventIDIntent)
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

            Log.d("test", result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
                    JSONObject data = jsonObject.getJSONObject("data");

                    edNoid.setText(!data.getString("kasus_id").equals("null") ? data.getString("kasus_id") : "-");
                    edNoLkn.setText(!data.getString("kasus_no").equals("null") ? data.getString("kasus_no") : "-");
                    edTglKejadian.setText(!data.getString("kasus_tanggal").equals("null") ? getNewDate(data.getString("kasus_tanggal")) : "-");
                    edUraianSingkat.setText(!data.getString("uraian_singkat").equals("null") ? data.getString("uraian_singkat") : "-");
                    edKeteranganLain.setText(!data.getString("keterangan_lainnya").equals("null") ? data.getString("keterangan_lainnya") : "-");
                    edTkpKasus.setText(!data.getString("kasus_tkp").equals("null") ? data.getString("kasus_tkp") : "");
                    edPelaksana.setText(!pelaksanaIntent.equals("null") ? pelaksanaIntent : "-");

                    foto1 = !data.getString("foto1").equalsIgnoreCase("null") ? data.getString("foto1") : "";
                    foto2 = !data.getString("foto2").equalsIgnoreCase("null") ? data.getString("foto2") : "";
                    foto3 = !data.getString("foto3").equalsIgnoreCase("null") ? data.getString("foto3") : "";

                    loadFoto(foto1, imgUpload1);
                    loadFoto(foto2, imgUpload2);
                    loadFoto(foto3, imgUpload3);

                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetilPenangananKasusActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(DetilPenangananKasusActivity.this, LoginActivity.class);
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
                    .url(Configuration.baseUrl+"/api/kasus/"+eventIDIntent)
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
                    Toast.makeText(DetilPenangananKasusActivity.this, "Berhasil menghapus data kasus.", Toast.LENGTH_LONG).show();

                    Intent logoutIntent = new Intent(DetilPenangananKasusActivity.this, PenangananKasusNarkobaActivity.class);
                    startActivity(logoutIntent);
                    finish();
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetilPenangananKasusActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(DetilPenangananKasusActivity.this, LoginActivity.class);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetilPenangananKasusActivity.this, PenangananKasusNarkobaActivity.class));
        finish();
    }
}
