package com.bnn.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bnn.Modal.BarangBukti;
import com.bnn.Modal.Tersangka;
import com.bnn.R;
import com.bnn.Task.LocationProvider;
import com.bnn.Task.ReverseAddressTask;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.Configuration;
import com.bnn.Utils.DateUtils;
import com.bnn.Utils.ImageUtils;
import com.bnn.Utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by USER on 11/1/2017.
 */

public class TambahRaziaActivity extends ActivityBase implements View.OnClickListener, LocationProvider.LocationCallback, MapsActivity.LocationListener {

    ImageView imgToogle, imgBack, imgTambah, imgUpload1, imgUpload2, imgUpload3, imgTambahTerdeteksi, imgBukaLokasi;
    EditText edNotId, edTanggalRazia, edLokasiRazia, edTerdeteksi, edUraianSingkat, edKeteranganLain, edPeserta, edJumlahBarbuk;
    TextView txtJudul, txtInfoUbah;
    AutoCompleteTextView acpelaksana;
    RelativeLayout fotobarangbukti, fotopelaku, fotolokasi;
    Button btnSimpan, btnBatal;
    LinearLayout listLayoutTerdeteksi;

    String tanggal, lokasi, uraian, jml_orang, jml_terindikasi, jml_barbuk, keterangan, fotoUbah1, fotoUbah2, fotoUbah3;

    //Ditambahkan pada 06 Nov 2017
    //Berfungsi untuk ambil gambar
    private String[] from = {"Ambil Foto", "Dari Galeri"};
    private Uri fileUri;
    private String uploadFotoKe = "";

    //Init variable untuk menampilkan pop up
    Button btnYa, btnTidak;
    ImageView imgClosePopup;

    //Variable untuk menentukan jenis form ubah atau tambah
    String mode = "", raziaIDIntent = "", pelaksanaIntent = "";

    private LocationProvider mLocationProvider;

    ProgressDialog progressDialog;
    int sizeUploadBarbuk = 0;
    int sizeUploadPelaku = 0;

    double longitude = 0.0;
    double latitude = 0.0;

    PreferenceUtils prefUtils;
    String foto1 = "", foto2 = "", foto3 = "";
    ImageUtils imageUtils;

    String dataRazia;
    JSONArray jsonArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_tambah_razia, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);
        imageUtils = ImageUtils.getInstance(this);

        progressDialog = new ProgressDialog(this);

        raziaIDIntent = getIntent().getStringExtra("razia_id") != null ? getIntent().getStringExtra("razia_id") : "";
        mode = getIntent().getStringExtra("mode_form") != null ? getIntent().getStringExtra("mode_form") : "tambah";
        dataRazia = prefUtils.getRazia() != null && !prefUtils.getRazia().equals("") ? prefUtils.getRazia() : "";

//        jsonArray = new JSONArray();
//
//        Log.d("Js", "Js: "+dataRazia);
//
//        try {
//            JSONArray jsonArray1 = new JSONArray(dataRazia);
//
//            for (int i = 0; i < jsonArray1.length(); i++) {
//                JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
//
//                jsonArray.put(jsonObject1);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        MapsActivity.locationListener = this;

        initLayoutTambahUbahData();
    }

    private void initLayoutTambahUbahData() {
        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        btnSimpan = (Button) findViewById(R.id.btnSimpan);
        btnBatal = (Button) findViewById(R.id.btnBatal);
        imgBukaLokasi = (ImageView) findViewById(R.id.imgLokasiRazia);
        edTanggalRazia = (EditText) findViewById(R.id.edTanggalRazia);
        edLokasiRazia = (EditText) findViewById(R.id.edLokasiRazia);
        edPeserta = (EditText) findViewById(R.id.edJumlahOrang);
        edUraianSingkat = (EditText) findViewById(R.id.edUraianPelaksanaan);
        edKeteranganLain = (EditText) findViewById(R.id.edKeteranganLain);
        edTerdeteksi = (EditText) findViewById(R.id.edJumlahTerindikasi);
        txtJudul = (TextView) findViewById(R.id.textViewjudul);
        edJumlahBarbuk = (EditText) findViewById(R.id.edJumlahBarangBukti);
        txtInfoUbah = (TextView)findViewById(R.id.txtInfoUbah);

        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        imgTambah.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);
        btnBatal.setOnClickListener(this);
        imgBukaLokasi.setOnClickListener(this);

        imgUpload1.setOnClickListener(this);
        imgUpload2.setOnClickListener(this);
        imgUpload3.setOnClickListener(this);

        detectLocation();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent y = new Intent(TambahRaziaActivity.this, RaziaActivity.class);
                startActivity(y);
                finish();
            }
        });

        imgToogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDrawerLayout.openDrawer(linearLayout2);
            }
        });

        if (mode.equals("ubah")) {
            txtJudul.setText("UBAH DATA");

            imgTambah.setVisibility(View.VISIBLE);

            edTanggalRazia.setFocusable(true);
            edTanggalRazia.setCursorVisible(true);
            edTanggalRazia.setOnClickListener(this);

            loadData();
        } else {
            txtJudul.setText("TAMBAH DATA BARU");

            edTanggalRazia.setOnClickListener(this);

            edLokasiRazia.setText("");

            edTanggalRazia.setText(getTodayDate());
            imgTambah.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        if (Configuration.isDeviceOnline(this)) {
//            try {
//                JSONArray jsonArray = new JSONArray(dataRazia);
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                    if (raziaIDIntent.equals(jsonObject.getString("id"))) {
//                        edTanggalRazia.setText(!jsonObject.getString("tgl").equals("") ? getNewDate(jsonObject.getString("tgl")) : "-");
//                        edLokasiRazia.setText(!jsonObject.getString("lokasi").equals("") ? jsonObject.getString("lokasi") : "-");
//                        edTerdeteksi.setText(!jsonObject.getString("jumlah_terindikasi").equals("") ? jsonObject.getString("jumlah_terindikasi") : "0");
//                        edPeserta.setText(!jsonObject.getString("jumlah_orang").equals("") ? jsonObject.getString("jumlah_orang") : "0");
//                        edUraianSingkat.setText(!jsonObject.getString("uraian").equals("") ? jsonObject.getString("uraian") : "-");
//                        edKeteranganLain.setText(!jsonObject.getString("keterangan_lain").equals("") ? jsonObject.getString("keterangan_lain") : "-");
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

    private void detectLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                new ReverseAddressTask(TambahRaziaActivity.this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
                    @Override
                    public void onAdrressListener(String address) {
                        if (mode.equals("tambah")) {
                            edLokasiRazia.setText(address);
                        }
                    }
                }).execute();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (latitude != 0.0 && longitude != 0.0) {
            new ReverseAddressTask(this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
                @Override
                public void onAdrressListener(String address) {
                    if (mode.equals("tambah")) {
                        edLokasiRazia.setText(address);
                    }
                }
            }).execute();
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        mLocationProvider = new LocationProvider(this, this);
        mLocationProvider.connect();
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgAdd:
                Intent tambahBaruIntent = new Intent(TambahRaziaActivity.this, TambahRaziaActivity.class);
                tambahBaruIntent.putExtra("mode_form", "tambah");
                startActivity(tambahBaruIntent);
                finish();
                break;
            case R.id.btnSimpan:
                if (Configuration.isDeviceOnline(TambahRaziaActivity.this)) {
                    if (mode.equals("tambah")) {
                        if (isEmptyField(edTanggalRazia)) {
                            Toast.makeText(TambahRaziaActivity.this,
                                    "Tanggal razia tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (isEmptyField(edLokasiRazia)) {
                            Toast.makeText(TambahRaziaActivity.this,
                                    "Lokasi razia tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (!isEmptyField(edTanggalRazia) && !isEmptyField(edLokasiRazia)) {
                            tanggal = edTanggalRazia.getText().toString();
                            lokasi = edLokasiRazia.getText().toString();
                            uraian = edUraianSingkat.getText().toString();
                            jml_orang = edPeserta.getText().toString();
                            jml_terindikasi = edTerdeteksi.getText().toString();
                            jml_barbuk = edJumlahBarbuk.getText().toString();
                            keterangan = edKeteranganLain.getText().toString();

                            progressDialog.setMessage("Menyimpan data..");
                            progressDialog.show();
                            new DataSimpanUbahTask(mode).execute();

//                            JSONObject jsonObject = new JSONObject();
//                            try {
//                                jsonObject.put("id", jsonArray.length() == 0 ? "1" : String.valueOf(Integer.valueOf(((JSONObject)jsonArray.get(jsonArray.length()-1)).getString("id"))+1));
//                                jsonObject.put("tgl", DateUtils.getNewDateForServer(edTanggalRazia.getText().toString()));
//                                jsonObject.put("lokasi", edLokasiRazia.getText().toString());
//                                jsonObject.put("uraian", edUraianSingkat.getText().toString());
//                                jsonObject.put("jumlah_orang", edPeserta.getText().toString());
//                                jsonObject.put("jumlah_terindikasi", edTerdeteksi.getText().toString());
//                                jsonObject.put("jumlah_barbuk", edJumlahBarbuk.getText().toString());
//                                jsonObject.put("keterangan_lain", edKeteranganLain.getText().toString());
//                                jsonObject.put("foto_satu", foto1);
//                                jsonObject.put("foto_dua", foto2);
//                                jsonObject.put("foto_tiga", foto3);
//
//                                jsonArray.put(jsonObject);
//
//                                Log.d("Array", "Array: "+jsonArray.toString());
//
//                                prefUtils.setRazia(jsonArray.toString());
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }

//                            Intent batal = new Intent(TambahRaziaActivity.this, RaziaActivity.class);
//                            startActivity(batal);
//                            finish();
                        }

                    } else if (mode.equals("ubah")) {
                        if (isEmptyField(edTanggalRazia)) {
                            Toast.makeText(TambahRaziaActivity.this,
                                    "Tanggal razia tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (isEmptyField(edLokasiRazia)) {
                            Toast.makeText(TambahRaziaActivity.this,
                                    "Lokasi razia tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (!isEmptyField(edTanggalRazia) && !isEmptyField(edLokasiRazia)) {
                            tanggal = edTanggalRazia.getText().toString();
                            lokasi = edLokasiRazia.getText().toString();
                            uraian = edUraianSingkat.getText().toString();
                            jml_orang = edPeserta.getText().toString();
                            jml_terindikasi = edTerdeteksi.getText().toString();
                            jml_barbuk = edJumlahBarbuk.getText().toString();
                            keterangan = edKeteranganLain.getText().toString();

                            progressDialog.setMessage("Mengubah data..");
                            progressDialog.show();
                            new DataSimpanUbahTask(mode).execute();

//                            JSONObject jsonObject = new JSONObject();
//                            try {
//                                JSONArray jsonArray = new JSONArray(dataRazia);
//
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject jsonObjectEdit = jsonArray.getJSONObject(i);
//
//                                    if (raziaIDIntent.equals(jsonObjectEdit.getString("id"))) {
//                                        jsonObjectEdit.put("id", jsonArray.length() == 0 ? "1" : String.valueOf(jsonArray.length()+1));
//                                        jsonObjectEdit.put("tgl", DateUtils.getNewDateForServer(edTanggalRazia.getText().toString()));
//                                        jsonObjectEdit.put("lokasi", edLokasiRazia.getText().toString());
//                                        jsonObjectEdit.put("uraian", edUraianSingkat.getText().toString());
//                                        jsonObjectEdit.put("jumlah_orang", edPeserta.getText().toString());
//                                        jsonObjectEdit.put("jumlah_terindikasi", edTerdeteksi.getText().toString());
//                                        jsonObjectEdit.put("jumlah_barbuk", edJumlahBarbuk.getText().toString());
//                                        jsonObjectEdit.put("keterangan_lain", edKeteranganLain.getText().toString());
//                                        jsonObjectEdit.put("foto_satu", foto1);
//                                        jsonObjectEdit.put("foto_dua", foto2);
//                                        jsonObjectEdit.put("foto_tiga", foto3);
//
//                                        jsonArray.put(i, jsonObjectEdit);
//
//                                        prefUtils.setRazia(jsonArray.toString());
//                                    }
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            Intent batal = new Intent(TambahRaziaActivity.this, RaziaActivity.class);
//                            startActivity(batal);
//                            finish();
                        }
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
            case R.id.btnBatal:
                //function batal
                Intent batal = new Intent(TambahRaziaActivity.this, RaziaActivity.class);
                startActivity(batal);
                finish();
                break;
            case R.id.imgBack:
                //function batal
                Intent kembali = new Intent(TambahRaziaActivity.this, RaziaActivity.class);
                startActivity(kembali);
                finish();
                break;
//            case R.id.imgTambahTerdeteksi:
//                initViewTerdeteksi();
//                break;
            case R.id.edTanggalRazia:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TambahRaziaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        edTanggalRazia.setText(getNewDate(sDate));
                    }
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
                dpd.show();
                break;
            case R.id.imgLokasiRazia:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogView = getLayoutInflater().inflate(R.layout.popup_bukamap, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialog = new AlertDialog.Builder(TambahRaziaActivity.this)
                        .setCancelable(false)
                        .setView(dialogView).create();
                initDialogView(dialogView);

                alertDialog.show();
                //Membuat alert dialog layout menjadi responsive ketika keyboard muncul
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                //Membuat background menjadi transparent
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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

                        Intent bukaMap = new Intent(TambahRaziaActivity.this, MapsActivity.class);
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
            case R.id.imageViewbarangbukti:
                uploadFotoKe = "1";

                tampilkanPilihamAmbilGambar();
                break;
            case R.id.imageViewpelaku:
                uploadFotoKe = "2";

                tampilkanPilihamAmbilGambar();
                break;
            case R.id.imageViewlokasi:
                uploadFotoKe = "3";

                tampilkanPilihamAmbilGambar();
                break;
        }
    }

    private void tampilkanPilihamAmbilGambar() {
        AlertDialog alertUpload1 = new AlertDialog.Builder(TambahRaziaActivity.this)
                .setTitle("Pilih Gambar")
                .setItems(from, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        switch (pos) {
                            case 0:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                fileUri = ImageUtils.getOutputMediaFileUri(TambahRaziaActivity.this);

                                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                                // start the image capture Intent
                                startActivityForResult(intent, 98);
                                break;
                            case 1:
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                // Start the Intent
                                startActivityForResult(galleryIntent, 99);
                                break;
                        }
                    }
                }).create();
        alertUpload1.show();
    }

    //Inflate setiap components ui yang akan kita implement di alert dialog
    private void initDialogView(View dialogView) {
        btnTidak = (Button) dialogView.findViewById(R.id.btnTidak);
        btnYa = (Button) dialogView.findViewById(R.id.btnYa);
        imgClosePopup = (ImageView) dialogView.findViewById(R.id.imgClosePopup);
    }

    private void initViewTerdeteksi() {
        edTerdeteksi.setVisibility(View.GONE);

        LayoutInflater inflaterTerdeteksi = getLayoutInflater();
        final LinearLayout layoutTerdeteksi = (LinearLayout) inflaterTerdeteksi.inflate(R.layout.item_listterdeteksi, listLayoutTerdeteksi, false);

        ImageView imgHapusTerdeteksi = (ImageView)layoutTerdeteksi.findViewById(R.id.imgHapusTerdeteksi);
        Spinner spinnerJekel = (Spinner) layoutTerdeteksi.findViewById(R.id.spinnerJekel);
        Spinner spinnerWaneg = (Spinner) layoutTerdeteksi.findViewById(R.id.spinnerWaneg);

        String[] listJekel = {"", "Laki-Laki", "Perempuan"};
        String[] listWN = {"", "Amerika", "Indonesia", "Malaysia", "Singapura"};

        spinnerJekel.setAdapter(new ArrayAdapter<String>(TambahRaziaActivity.this,
                R.layout.spinner_item_small, listJekel));
        spinnerWaneg.setAdapter(new ArrayAdapter<String>(TambahRaziaActivity.this,
                R.layout.spinner_item_small, listWN));

        imgHapusTerdeteksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listLayoutTerdeteksi.removeView(layoutTerdeteksi);

                if (listLayoutTerdeteksi.getChildCount() == 0) {
                    edTerdeteksi.setVisibility(View.VISIBLE);
                }
            }
        });

        listLayoutTerdeteksi.addView(layoutTerdeteksi);
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
//                    .post(requestBody)
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

//                    edNotId.setText(!data.getString("id").equals("") ? getNewDate(data.getString("id")) : "-");
                    edTanggalRazia.setText(!data.getString("tgl_razia").equals("") ? getNewDate(data.getString("tgl_razia")) : "-");
                    edLokasiRazia.setText(!data.getString("lokasi").equals("") ? data.getString("lokasi") : "-");
                    edTerdeteksi.setText(!data.getString("jumlah_terindikasi").equals("") ? data.getString("jumlah_terindikasi") : "0");
                    edPeserta.setText(!data.getString("jumlah_dirazia").equals("") ? data.getString("jumlah_dirazia") : "0");
                    edUraianSingkat.setText(!data.getString("uraian_singkat").equals("") ? data.getString("uraian_singkat") : "-");
                    edKeteranganLain.setText(!data.getString("keterangan_lainnya").equals("") ? data.getString("keterangan_lainnya") : "-");
                    edJumlahBarbuk.setText(!data.getString("jumlah_ditemukan").equals("") ? data.getString("jumlah_ditemukan") : "0");

                    if (edTanggalRazia.getText().toString().equals("null")){
                        edTanggalRazia.setText("");
                    }

                    if (edLokasiRazia.getText().toString().equals("null")){
                        edLokasiRazia.setText("");
                    }

                    if (edPeserta.getText().toString().equals("null")){
                        edPeserta.setText("");
                        edPeserta.setHint("0");
                    }

                    if (edUraianSingkat.getText().toString().equals("null")){
                        edUraianSingkat.setText("");
                    }

                    if (edKeteranganLain.getText().toString().equals("null")){
                        edKeteranganLain.setText("");
                    }

                    if (edTerdeteksi.getText().toString().equals("null")){
                        edTerdeteksi.setText("");
                        edTerdeteksi.setHint("0");
                    }
                    if (edJumlahBarbuk.getText().toString().equals("null")){
                        edJumlahBarbuk.setText("");
                        edJumlahBarbuk.setHint("0");
                    }

                    fotoUbah1 = !data.getString("foto1").equalsIgnoreCase("null") ? data.getString("foto1") : "";
                    fotoUbah2 = !data.getString("foto2").equalsIgnoreCase("null") ? data.getString("foto2") : "";
                    fotoUbah3 = !data.getString("foto3").equalsIgnoreCase("null") ? data.getString("foto3") : "";

                    if (!fotoUbah1.equals("") || !fotoUbah2.equals("") || !fotoUbah3.equals("")) {
                        txtInfoUbah.setVisibility(View.VISIBLE);
                    } else {
                        txtInfoUbah.setVisibility(View.GONE);
                    }

                    loadFoto(fotoUbah1, imgUpload1);
                    loadFoto(fotoUbah2, imgUpload2);
                    loadFoto(fotoUbah3, imgUpload3);

                    progressDialog.dismiss();
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    progressDialog.dismiss();

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahRaziaActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahRaziaActivity.this, LoginActivity.class);
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

    class DataSimpanUbahTask extends AsyncTask<Bitmap, Integer, String> {
        private String responseServer;
        private String modeTask;

        public DataSimpanUbahTask(String modeTask) {
            this.modeTask = modeTask;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseServer = "";
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            RequestBody requestBody = null;
            Request httpRequest = null;

            Headers headers = new Headers.Builder()
                    .add("Authorization", "Bearer " + prefUtils.getTokenKey())
                    .add("Content-Type","application/x-www-form-urlencoded")
                    .add("Accept", "application/json")
                    .build();

            if (modeTask.equals("tambah")) {
                FormBody.Builder form = new FormBody.Builder()
                        .add("tgl_razia", DateUtils.getNewDateForServer(tanggal))
                        .add("jumlah_dirazia", jml_orang)
                        .add("jumlah_terindikasi", jml_terindikasi)
                        .add("jumlah_ditemukan", jml_barbuk)
                        .add("lokasi",lokasi)
                        .add("keterangan_lainnya", keterangan)
                        .add("koordinat", latitude+","+longitude)
                        .add("id_instansi", prefUtils.getInstansi())
                        .add("uraian_singkat", uraian);


                if (!foto1.equals("")) {
                    form.add("foto1", foto1);
                }

                if (!foto2.equals("")) {
                    form.add("foto2", foto2);
                }

                if (!foto3.equals("")) {
                    form.add("foto3", foto3);
                }

                requestBody = form.build();

                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/berantasrazia")
                        .headers(headers)
                        .post(requestBody)
                        .build();

            } else if (modeTask.equals("ubah")) {
                FormBody.Builder form = new FormBody.Builder()
                        .add("tgl_razia", DateUtils.getNewDateForServer(tanggal))
                        .add("jumlah_dirazia", jml_orang)
                        .add("jumlah_terindikasi", jml_terindikasi)
                        .add("jumlah_ditemukan", jml_barbuk)
                        .add("lokasi",lokasi)
                        .add("keterangan_lainnya", keterangan)
                        .add("koordinat", latitude+","+longitude)
                        .add("id_instansi", prefUtils.getInstansi())
                        .add("uraian_singkat", uraian);

                if (!foto1.equals("") && !foto1.equals(fotoUbah1)) {
                    form.add("foto1", foto1);
                }

                if (!foto2.equals("") && !foto2.equals(fotoUbah2)) {
                    form.add("foto2", foto2);
                }

                if (!foto3.equals("") && !foto3.equals(fotoUbah3)) {
                    form.add("foto3", foto3);
                }

                requestBody = form.build();


                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/berantasrazia/"+raziaIDIntent)
                        .headers(headers)
                        .put(requestBody)
                        .build();
            }

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
            Log.d("test simpan", result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
//                    JSONObject data = jsonObject.getJSONObject("data");

                    if (modeTask.equals("ubah")) {
                        progressDialog.dismiss();

                        Toast.makeText(TambahRaziaActivity.this, "Data berhasil di ubah.", Toast.LENGTH_LONG).show();
                        Intent listKasus = new Intent(TambahRaziaActivity.this, RaziaActivity.class);
                        startActivity(listKasus);
                        finish();
                    } else if (modeTask.equals("tambah")) {
//                        JSONObject data = jsonObject.getJSONObject("data");

                        progressDialog.dismiss();

                        Toast.makeText(TambahRaziaActivity.this, "Data berhasil disimpan.", Toast.LENGTH_LONG).show();
                        Intent listKasus = new Intent(TambahRaziaActivity.this, RaziaActivity.class);
                        startActivity(listKasus);
                        finish();

                    }
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    progressDialog.dismiss();

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahRaziaActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahRaziaActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(TambahRaziaActivity.this, "Terjadi kesalahan, silahkan coba lagi.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 98){
            if (resultCode == RESULT_OK){
                // bimatp factory
                BitmapFactory.Options options = new BitmapFactory.Options();

                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 8;

                final File img = new File(fileUri.getPath());

                Bitmap bmp = null;
                try {
                    bmp = ImageUtils.modifyOrientation(imageUtils.decodeFile(img), fileUri.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!uploadFotoKe.equals("") && uploadFotoKe.equals("1")) {
                    imgUpload1.setImageBitmap(bmp);
                    foto1 = ImageUtils.getInstance(TambahRaziaActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahRaziaActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahRaziaActivity.this).encodeBase64String(bmp);
                }

            } else {
                Toast.makeText(this, "You haven't take photo.",
                        Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 99){
            if (resultCode == RESULT_OK && data != null){
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                String imgDecodableString;
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                Bitmap bmp = null;

                File img = new File(imgDecodableString);

                try {
                    bmp = ImageUtils.modifyOrientation(imageUtils.decodeFile(img), imgDecodableString);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!uploadFotoKe.equals("") && uploadFotoKe.equals("1")) {
                    imgUpload1.setImageBitmap(bmp);
                    foto1 = ImageUtils.getInstance(TambahRaziaActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahRaziaActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahRaziaActivity.this).encodeBase64String(bmp);
                }

            } else {
                Toast.makeText(this, "You haven't picked image.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getTodayDate(){
        String newDate = "01-01-2016";

        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
        newDate = newFormat.format(d);

        return newDate;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TambahRaziaActivity.this, RaziaActivity.class));
        finish();
    }

    @Override
    public void handleNewLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        new ReverseAddressTask(this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
            @Override
            public void onAdrressListener(final String address) {
                if (mode.equals("tambah")) {
                    edLokasiRazia.setText(address);
                }
            }
        }).execute();
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
    public void getLatLong(double lat, double lng) {
        if (lat != 0.0 && lng != 0.0) {
            new ReverseAddressTask(this, lat, lng, new ReverseAddressTask.ReverseAddress() {
                @Override
                public void onAdrressListener(String address) {
                    edLokasiRazia.setText(address);
                }
            }).execute();
        }
    }

    private void loadFoto(String base64, ImageView fotoFrame) {
        if (!base64.equalsIgnoreCase("")) {
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            fotoFrame.setImageBitmap(decodedByte);
        }
    }

    private Boolean isEmptyField(EditText editText) {
        String fieldData = editText.getText().toString();

        if (fieldData.equals("")) {
            return true;
        }

        return false;
    }
}

