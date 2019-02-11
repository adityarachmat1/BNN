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
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bnn.Modal.BarangBukti;
import com.bnn.Modal.JenisBarangBukti;
import com.bnn.Modal.Pelaksana;
import com.bnn.Modal.SatuanBarangBukti;
import com.bnn.Modal.Tersangka;
import com.bnn.Modal.WargaNegara;
import com.bnn.Task.LocationProvider;
import com.bnn.Task.ReverseAddressTask;
import com.bnn.Utils.ActivityBase;
import com.bnn.R;
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
import java.util.ArrayList;
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

public class TambahPenangananKasusNarkobaActivity extends ActivityBase implements View.OnClickListener, LocationProvider.LocationCallback, MapsActivity.LocationListener {

    ImageView imgToogle, imageback, imgTambah, imgBukaMap, imgTambahPenyidik, imgTambahPelaku, imgTambahBarangBukti, imgUpload1, imgUpload2, imgUpload3;
    EditText txttanggalkejadian, txttkpkasus, txtjumlahbarangbukti, txtpenyidik, txtpelaku, edNoId, edNoLkn, edPelaksana, edUraianSingkat, edKeteranganLain;
    RelativeLayout fotobarangbukti, fotopelaku, fotolokasi;
    Button btnsimpan, btnbatal;
    LinearLayout listLayoutPenyidik, listLayoutPelaku;
    ScrollView layoutUbahTambahData;
    Spinner spinnerPelaksanaKasus;

    TextView txtJudul, txtInfoUbah;
    LinearLayout layoutJudulNoId, layoutJudulNoLkn, headerBarbuk;
    RelativeLayout layoutContainerNoId, layoutContainerNoLkn;

    //Layout variable untuk tambah ubah barbuk
    ImageView imgTambahBarbuk;
    LinearLayout listLayoutTambahBarbuk;

    //Ditambahkan pada 04 Nov 2017
    PreferenceUtils prefUtils;
    ImageUtils imageUtils;

    Button btnYa, btnTidak;
    ImageView imgClosePopup;

    //Variable untuk menentukan jenis form ubah atau tambah
    String mode = "", eventIDIntent = "", pelaksanaIntent = "", arrayBarbuk = "", arrayTersangka = "";

    //Ditambahkan pada 06 Nov 2017
    private String[] from = {"Ambil Foto", "Dari Galeri"};
    private Uri fileUri;
    private String uploadFotoKe = "";

    private ArrayList<BarangBukti> listBarangBukti = new ArrayList<>();
    private ArrayList<JenisBarangBukti> listJenisBarbuk = new ArrayList<>();
    private ArrayList<SatuanBarangBukti> listSatuanBarbuk = new ArrayList<>();

    private ArrayList<String> listPenyidik = new ArrayList<>();
    private ArrayList<Tersangka> listTersangka = new ArrayList<>();
    private ArrayList<WargaNegara> listWargaNegara = new ArrayList<>();

    private LocationProvider mLocationProvider;

    double longitude = 0.0;
    double latitude = 0.0;

    //Variable for input kasus
    private String tanggal, lokasi, noLkn, koordinat, uraianSingkat, keteranganLain;

    //Variable for input tersangka
    private String kasusBarbukId, jumlahBarangBukti, kodeSatuanBarangBukti, kasusId, idBarangBukti;
    private String kasusTersangkaId, namaPelaku, jekelPelaku, kodeNegaraPelaku, tanggalLahirPelaku, tempatLahirPelaku, pelaksana_;

    ProgressDialog progressDialog;
    int sizeUploadBarbuk = 0;
    int sizeUploadPelaku = 0;
    ArrayList<Pelaksana>pelaksana_array = new ArrayList<>();

    String foto1 = "", foto2 = "", foto3 = "", fotoUbah1 = "", fotoUbah2 = "", fotoUbah3 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_tambah_penanganankasusnarkoba, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);
        imageUtils = ImageUtils.getInstance(this);

        progressDialog = new ProgressDialog(this);
        MapsActivity.locationListener = this;

        eventIDIntent = getIntent().getStringExtra("event_id") != null ? getIntent().getStringExtra("event_id") : "";
        pelaksanaIntent = getIntent().getStringExtra("pelaksana") != null ? getIntent().getStringExtra("pelaksana") : "";
        mode = getIntent().getStringExtra("mode_form") != null ? getIntent().getStringExtra("mode_form") : "tambah";
        arrayBarbuk = getIntent().getStringExtra("array_barbuk") != null ? getIntent().getStringExtra("array_barbuk") : "";
        arrayTersangka = getIntent().getStringExtra("array_tersangka") != null ? getIntent().getStringExtra("array_tersangka") : "";

        new AmbileDataPelaksanaTask().execute();

        initLayoutTambahUbahData();

        if (!arrayBarbuk.equals("")) {
            try {
                JSONArray barbukObj = new JSONArray(arrayBarbuk);

                if (barbukObj.length() > 0) {
                    txtjumlahbarangbukti.setVisibility(View.GONE);
                } else {
                    txtjumlahbarangbukti.setVisibility(View.VISIBLE);
                }

                for (int i = 0;i < barbukObj.length();i++) {
                    LayoutInflater inflaterBarbuk= getLayoutInflater();
                    final RelativeLayout layoutBarbuk = (RelativeLayout) inflaterBarbuk.inflate(R.layout.item_list_barbuk, listLayoutTambahBarbuk, false);

                    final AutoCompleteTextView spinnerJenis = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerJenis);
                    AutoCompleteTextView spinnerSatuan = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerSatuan);
                    AutoCompleteTextView spinnerJumlah = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerJumlah);
                    final ImageView imgHapus = (ImageView) layoutBarbuk.findViewById(R.id.imgHapusBarbuk);

                    spinnerJenis.setAdapter(new ArrayAdapter<JenisBarangBukti>(TambahPenangananKasusNarkobaActivity.this,
                            R.layout.spinner_item_small, listJenisBarbuk));
                    spinnerSatuan.setAdapter(new ArrayAdapter<SatuanBarangBukti>(TambahPenangananKasusNarkobaActivity.this,
                            R.layout.spinner_item_small, listSatuanBarbuk));

                    spinnerJenis.setTag(((JSONObject)barbukObj.get(i)).getString("kasus_barang_bukti_id"));
                    spinnerJenis.setText(((JSONObject)barbukObj.get(i)).getString("nm_brgbukti"));
                    spinnerJumlah.setText(""+Double.parseDouble(((JSONObject)barbukObj.get(i)).getString("jumlah_barang_bukti")));
                    spinnerSatuan.setText(((JSONObject)barbukObj.get(i)).getString("nm_satuan"));

                    imgHapus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mode.equals("ubah")) {
                                int ind = ((LinearLayout)((RelativeLayout)((LinearLayout)spinnerJenis.getParent()).getParent()).getParent()).indexOfChild(((RelativeLayout)((LinearLayout)spinnerJenis.getParent()).getParent()));

                                progressDialog.setMessage("Menghapus data..");
                                progressDialog.show();

                                new DataSimpanUbahTask("hapus_barbuk", (String)spinnerJenis.getTag()).execute();
                            }

                            listLayoutTambahBarbuk.removeView(layoutBarbuk);

                            if (listLayoutTambahBarbuk.getChildCount() == 0) {
                                txtjumlahbarangbukti.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    listLayoutTambahBarbuk.addView(layoutBarbuk);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!arrayTersangka.equals("")) {
            try {
                JSONArray pelakuObj = new JSONArray(arrayTersangka);

                if (pelakuObj.length() > 0) {
                    txtpelaku.setVisibility(View.GONE);
                } else {
                    txtpelaku.setVisibility(View.VISIBLE);
                }

                for (int i = 0;i < pelakuObj.length();i++) {
                    LayoutInflater inflaterPelaku = getLayoutInflater();
                    final LinearLayout layoutPelaku = (LinearLayout) inflaterPelaku.inflate(R.layout.item_list_pelaku, listLayoutPelaku, false);

                    EditText edNama = (EditText) layoutPelaku.findViewById(R.id.edNama);
                    ImageView imgHapusPelaku = (ImageView)layoutPelaku.findViewById(R.id.imgHapus);
                    Spinner spinnerJekel = (Spinner) layoutPelaku.findViewById(R.id.spinnerJenisKelamin);
                    AutoCompleteTextView edWaneg = (AutoCompleteTextView)layoutPelaku.findViewById(R.id.spinnerKewarganegaraan);
                    EditText edTempatLahir = (EditText)layoutPelaku.findViewById(R.id.edTempatLahir);
                    final EditText edTanggaLahir = (EditText) layoutPelaku.findViewById(R.id.edTanggalLahir);

                    String[] listJekel = {"Laki-Laki", "Perempuan"};

                    spinnerJekel.setAdapter(new ArrayAdapter<String>(TambahPenangananKasusNarkobaActivity.this,
                            R.layout.spinner_item_small, listJekel));

                    imgHapusPelaku.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            if (mode.equals("ubah")) {
//                                int ind = ((LinearLayout)((RelativeLayout)imgHapusPelaku.getParent()).getParent()).indexOfChild(((RelativeLayout)imgHapus.getParent()));
//
//                                new DataSimpanUbahTask("hapus_barbuk", listBarangBukti.get(ind).getIdBarang()).execute();
//                            }

                            listLayoutPelaku.removeView(layoutPelaku);

                            if (listLayoutPelaku.getChildCount() == 0) {
                                txtpelaku.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    edNama.setText(((JSONObject)pelakuObj.get(i)).getString("tersangka_nama"));
                    String jekel = ((JSONObject)pelakuObj.get(i)).getString("kode_jenis_kelamin").equals("L") ? "Laki-Laki" : "Perempuan";
                    edNama.setTag(((JSONObject)pelakuObj.get(i)).getString("tersangka_id"));

                    edWaneg.setText(!((JSONObject)pelakuObj.get(i)).getString("nama_negara").equalsIgnoreCase("null") ? ((JSONObject)pelakuObj.get(i)).getString("nama_negara") : "-");
                    edTempatLahir.setText(!((JSONObject)pelakuObj.get(i)).getString("tersangka_tempat_lahir").equalsIgnoreCase("null") ? ((JSONObject)pelakuObj.get(i)).getString("tersangka_tempat_lahir") : "-");
                    edTanggaLahir.setText(!((JSONObject)pelakuObj.get(i)).getString("tersangka_tanggal_lahir").equalsIgnoreCase("null") ? getNewDate(((JSONObject)pelakuObj.get(i)).getString("tersangka_tanggal_lahir")) : "");

                    edTanggaLahir.setFocusable(false);
                    edTanggaLahir.setCursorVisible(false);
                    edTanggaLahir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pilihTanggalLahir(edTanggaLahir);
                        }
                    });

                    if (jekel.equalsIgnoreCase("Laki-Laki")) {
                        spinnerJekel.setSelection(0);
                    } else {
                        spinnerJekel.setSelection(1);
                    }

                    listLayoutPelaku.addView(layoutPelaku);

//                    listTersangka.add(new Tersangka(((JSONObject)pelakuObj.get(i)).getString("tersangka_id"),
//                            ((JSONObject)pelakuObj.get(i)).getString("tersangka_nama"), jekel,
//                            "", ((JSONObject)pelakuObj.get(i)).getString("tersangka_tempat_lahir"),
//                            ((JSONObject)pelakuObj.get(i)).getString("tersangka_tanggal_lahir")));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        detectLocation();

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent y = new Intent(TambahPenangananKasusNarkobaActivity.this, PenangananKasusNarkobaActivity.class);
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

    private void detectLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                new ReverseAddressTask(TambahPenangananKasusNarkobaActivity.this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
                    @Override
                    public void onAdrressListener(String address) {
                        if (mode.equals("tambah")) {
                            txttkpkasus.setText(address);
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
                        txttkpkasus.setText(address);
                    }
                }
            }).execute();
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        mLocationProvider = new LocationProvider(this, this);
        mLocationProvider.connect();
    }

    private void initLayoutTambahUbahData() {

        //Init layout tambah ubah data baru
        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imageback = (ImageView) findViewById(R.id.imageback);
        imgBukaMap = (ImageView) findViewById(R.id.imageViewgpstkpkasus);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);

        //Ditambahkan pada 05 Nov 2017
        layoutJudulNoId = (LinearLayout) findViewById(R.id.layoutJudulNoId);
        layoutJudulNoLkn = (LinearLayout) findViewById(R.id.layoutJudulNoLkn);
        layoutContainerNoId = (RelativeLayout) findViewById(R.id.layoutContainerNoId);
        layoutContainerNoLkn = (RelativeLayout) findViewById(R.id.layoutContainerNoLkn);

        edNoId = (EditText) findViewById(R.id.edNoId);
        edNoLkn = (EditText) findViewById(R.id.edNoLKN);
        edPelaksana = (EditText) findViewById(R.id.edPelaksana);
        edUraianSingkat = (EditText) findViewById(R.id.edUraianKejadian);
        edKeteranganLain = (EditText) findViewById(R.id.edKeterangan);
        txttanggalkejadian = (EditText) findViewById(R.id.tanggalkejadian);
        txttkpkasus = (EditText) findViewById(R.id.editTexttkpkasus);
        txtjumlahbarangbukti = (EditText) findViewById(R.id.editTextjumlahbarangbukti);
        txtpenyidik = (EditText) findViewById(R.id.editTextpenyidik);
        txtpelaku = (EditText) findViewById(R.id.editTextpelaku);
        fotobarangbukti = (RelativeLayout) findViewById(R.id.fotobarangbukti);
        fotopelaku = (RelativeLayout) findViewById(R.id.fotopelaku);
        fotolokasi = (RelativeLayout) findViewById(R.id.fotolokasi);
        txtInfoUbah = (TextView) findViewById(R.id.txtInfoUbah);

        //Ditambahkan pada 09 Nov 2017
        spinnerPelaksanaKasus = (Spinner) findViewById(R.id.spinnerPelaksanaKasus);

        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        btnsimpan = (Button) findViewById(R.id.buttonsimpan);
        btnbatal = (Button) findViewById(R.id.buttonbatal);
        imgTambahBarangBukti = (ImageView) findViewById(R.id.imgViewbarangbukti);
        txtJudul = (TextView) findViewById(R.id.textViewjudul);

        layoutUbahTambahData = (ScrollView) findViewById(R.id.layoutUbahTambahData);
        headerBarbuk = (LinearLayout) findViewById(R.id.headerBarbuk);

        //Init layoutlist penyidik
        imgTambahPenyidik = (ImageView) findViewById(R.id.imgAddPenyidik);
        listLayoutPenyidik = (LinearLayout) findViewById(R.id.layoutListPenyidik);

        //Init layoutlist pelaku
        imgTambahPelaku = (ImageView) findViewById(R.id.imgAddPelaku);
        listLayoutPelaku = (LinearLayout) findViewById(R.id.layoutListPelaku);
        listLayoutTambahBarbuk = (LinearLayout) findViewById(R.id.listLayoutTambahBarbuk);

        btnsimpan.setOnClickListener(this);
        btnbatal.setOnClickListener(this);
        imgTambahPenyidik.setOnClickListener(this);
        imgTambahPelaku.setOnClickListener(this);
        imgTambahBarangBukti.setOnClickListener(this);
        imgUpload1.setOnClickListener(this);
        imgUpload2.setOnClickListener(this);
        imgUpload3.setOnClickListener(this);

        //Ditambahkan pada 04 Nov 2017
        imgBukaMap.setOnClickListener(this);

        layoutJudulNoId.setVisibility(View.GONE);
        layoutContainerNoId.setVisibility(View.GONE);
        layoutJudulNoLkn.setVisibility(View.GONE);
        layoutContainerNoLkn.setVisibility(View.GONE);

        new AmbileDataTask("satuan").execute();
        new AmbileDataTask("jenis").execute();
        new AmbileDataTask("warga_negara").execute();

        if (mode.equals("ubah")) {
            txtJudul.setText("UBAH DATA");
            imgTambah.setVisibility(View.VISIBLE);

            txttanggalkejadian.setFocusable(true);
            txttanggalkejadian.setCursorVisible(true);
            txttanggalkejadian.setOnClickListener(this);

            loadData();
        } else {
            imgTambah.setVisibility(View.GONE);

            txttanggalkejadian.setText(getTodayDate());
            txttanggalkejadian.setOnClickListener(this);

            txtJudul.setText("TAMBAH DATA BARU");
            edNoId.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tanggalkejadian:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TambahPenangananKasusNarkobaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        txttanggalkejadian.setText(getNewDate(sDate));
                    }
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
                dpd.show();
                break;
            case R.id.imageViewgpstkpkasus:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogView = getLayoutInflater().inflate(R.layout.popup_bukamap, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialog = new AlertDialog.Builder(TambahPenangananKasusNarkobaActivity.this)
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

                        Intent bukaMap = new Intent(TambahPenangananKasusNarkobaActivity.this, MapsActivity.class);
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
            case R.id.buttonsimpan:
                if (Configuration.isDeviceOnline(TambahPenangananKasusNarkobaActivity.this)) {
                    if (mode.equals("tambah")) {
                        if (isEmptyField(txttanggalkejadian)) {
                            Toast.makeText(TambahPenangananKasusNarkobaActivity.this,
                                    "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (isEmptyField(txttkpkasus)) {
                            Toast.makeText(TambahPenangananKasusNarkobaActivity.this,
                                    "Tkp kasus tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (!isEmptyField(txttanggalkejadian) && !isEmptyField(txttkpkasus)) {
                            tanggal = txttanggalkejadian.getText().toString();
                            lokasi = txttkpkasus.getText().toString();
                            noLkn = "";
                            uraianSingkat = edUraianSingkat.getText().toString();
                            keteranganLain = edKeteranganLain.getText().toString();

                            if (listLayoutTambahBarbuk != null && listLayoutTambahBarbuk.getChildCount() > 0) {
                                int count = 0;

                                for (int i = 0; i < listLayoutTambahBarbuk.getChildCount(); i++) {
                                    if (listLayoutTambahBarbuk.getChildAt(i) instanceof RelativeLayout) {
                                        RelativeLayout relative = (RelativeLayout) listLayoutTambahBarbuk.getChildAt(i);

                                        if (relative.getChildAt(0) instanceof LinearLayout) {
                                            LinearLayout linear = (LinearLayout) relative.getChildAt(0);

                                            String jenis = "";
                                            String satuan = "";
                                            String jumlah = "";

                                            if (linear.getChildAt(0).getId() == R.id.spinnerJenis) {
                                                jenis = ((AutoCompleteTextView) linear.getChildAt(0).findViewById(R.id.spinnerJenis)).getText().toString();
                                            }

                                            if (linear.getChildAt(2).getId() == R.id.spinnerSatuan) {
                                                satuan = ((AutoCompleteTextView) linear.getChildAt(2).findViewById(R.id.spinnerSatuan)).getText().toString();
                                            }

                                            if (linear.getChildAt(1).getId() == R.id.spinnerJumlah) {
                                                jumlah = ((AutoCompleteTextView) linear.getChildAt(1).findViewById(R.id.spinnerJumlah)).getText().toString();
                                            }

                                            if (!jumlah.equals("") && !satuan.equals("") && !jenis.equals("")) {
                                                listBarangBukti.add(new BarangBukti(jenis, jumlah, satuan, ""));
                                            }

                                        }
                                    }
                                }
                            }

                            if (listLayoutPelaku != null && listLayoutPelaku.getChildCount() > 0) {
                                for (int a = 0; a < listLayoutPelaku.getChildCount(); a++) {
                                    LinearLayout linearLayout = (LinearLayout) listLayoutPelaku.getChildAt(a);
                                    String namaPelaku = "";
                                    String jekel = "";
                                    String koneg = "";
                                    String tempatLahir = "";
                                    String tanggalLahir = "";

                                    if (linearLayout.getChildAt(1).getId() == R.id.edNama) {
                                        namaPelaku = ((EditText) linearLayout.getChildAt(1).findViewById(R.id.edNama)).getText().toString();
                                    }

                                    if (((Spinner) ((LinearLayout) linearLayout.getChildAt(3)).getChildAt(0)).getId() == R.id.spinnerJenisKelamin) {
                                        jekel = ((Spinner) ((LinearLayout) linearLayout.getChildAt(3)).getChildAt(0).findViewById(R.id.spinnerJenisKelamin)).getSelectedItem().toString();
                                    }

                                    if (((AutoCompleteTextView) ((LinearLayout) linearLayout.getChildAt(3)).getChildAt(2)).getId() == R.id.spinnerKewarganegaraan) {
                                        String waneg = ((AutoCompleteTextView) ((LinearLayout) linearLayout.getChildAt(3)).getChildAt(2).findViewById(R.id.spinnerKewarganegaraan)).getText().toString();

                                        if (!waneg.equals("")) {
                                            for (int i = 0; i < listWargaNegara.size(); i++) {
                                                if (listWargaNegara.get(i).getWargaNegara().equalsIgnoreCase(waneg)) {
                                                    koneg = listWargaNegara.get(i).getKode();
                                                }
                                            }
                                        }
                                    }

                                    if (((EditText) ((LinearLayout) linearLayout.getChildAt(6)).getChildAt(0)).getId() == R.id.edTempatLahir) {
                                        tempatLahir = ((EditText) ((LinearLayout) linearLayout.getChildAt(6)).getChildAt(0).findViewById(R.id.edTempatLahir)).getText().toString();
                                    }

                                    if (((EditText) ((LinearLayout) linearLayout.getChildAt(6)).getChildAt(1)).getId() == R.id.edTanggalLahir) {
                                        tanggalLahir = ((EditText) ((LinearLayout) linearLayout.getChildAt(6)).getChildAt(1).findViewById(R.id.edTanggalLahir)).getText().toString();
                                    }

                                    Log.d("Nama tersangka", "Nama: " + namaPelaku);

                                    listTersangka.add(new Tersangka("", namaPelaku, jekel, koneg, tempatLahir, DateUtils.getNewDateForServer(tanggalLahir)));
                                }
                            }

                            progressDialog.setMessage("Menyimpan data..");
                            progressDialog.show();
                            new DataSimpanUbahTask(mode).execute();
                        }

                        Log.d("Simpan", "Simpan " + listTersangka.size());
                    } else if (mode.equals("ubah")) {

                        if (isEmptyField(txttanggalkejadian)) {
                            Toast.makeText(TambahPenangananKasusNarkobaActivity.this,
                                    "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (isEmptyField(txttkpkasus)) {
                            Toast.makeText(TambahPenangananKasusNarkobaActivity.this,
                                    "Tkp kasus tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (!isEmptyField(txttanggalkejadian) && !isEmptyField(txttkpkasus)) {
                            tanggal = txttanggalkejadian.getText().toString();
                            lokasi = txttkpkasus.getText().toString();
                            noLkn = "";
                            uraianSingkat = edUraianSingkat.getText().toString();
                            keteranganLain = edKeteranganLain.getText().toString();

                            if (listLayoutTambahBarbuk != null && listLayoutTambahBarbuk.getChildCount() > 0) {
                                int count = 0;

                                for (int i = 0; i < listLayoutTambahBarbuk.getChildCount(); i++) {
                                    if (listLayoutTambahBarbuk.getChildAt(i) instanceof RelativeLayout) {
                                        RelativeLayout relative = (RelativeLayout) listLayoutTambahBarbuk.getChildAt(i);

                                        if (relative.getChildAt(0) instanceof LinearLayout) {
                                            LinearLayout linear = (LinearLayout) relative.getChildAt(0);

                                            String jenis = "";
                                            String satuan = "";
                                            String jumlah = "";
                                            String kasusIdBarbuk = "";

                                            if (linear.getChildAt(0).getId() == R.id.spinnerJenis) {
                                                AutoCompleteTextView atJenis = ((AutoCompleteTextView) linear.getChildAt(0).findViewById(R.id.spinnerJenis));
                                                jenis = atJenis.getText().toString();
                                                kasusIdBarbuk = atJenis.getTag() != null ? (String)atJenis.getTag() : "";
                                            }

                                            if (linear.getChildAt(2).getId() == R.id.spinnerSatuan) {
                                                satuan = ((AutoCompleteTextView) linear.getChildAt(2).findViewById(R.id.spinnerSatuan)).getText().toString();
                                            }

                                            if (linear.getChildAt(1).getId() == R.id.spinnerJumlah) {
                                                jumlah = ((AutoCompleteTextView) linear.getChildAt(1).findViewById(R.id.spinnerJumlah)).getText().toString();
                                            }

                                            if (!jumlah.equals("") && !satuan.equals("") && !jenis.equals("")) {
                                                Log.d("Kasus id", "Kasus id: "+ satuan);
                                                listBarangBukti.add(new BarangBukti(jenis, jumlah, satuan, kasusIdBarbuk));
                                            }

                                        }
                                    }
                                }
                            }

                            if (listLayoutPelaku != null && listLayoutPelaku.getChildCount() > 0) {
                                for (int a = 0; a < listLayoutPelaku.getChildCount(); a++) {
                                    LinearLayout linearLayout = (LinearLayout) listLayoutPelaku.getChildAt(a);
                                    String namaPelaku = "";
                                    String jekel = "";
                                    String koneg = "";
                                    String tempatLahir = "";
                                    String tanggalLahir = "";
                                    String idTersangka = "";

                                    if (linearLayout.getChildAt(1).getId() == R.id.edNama) {
                                        EditText edNamaPelaku = ((EditText) linearLayout.getChildAt(1).findViewById(R.id.edNama));
                                        namaPelaku = edNamaPelaku.getText().toString();
                                        idTersangka = edNamaPelaku.getTag() != null ? (String)edNamaPelaku.getTag() : "";
                                    }

                                    if (((Spinner) ((LinearLayout) linearLayout.getChildAt(3)).getChildAt(0)).getId() == R.id.spinnerJenisKelamin) {
                                        jekel = ((Spinner) ((LinearLayout) linearLayout.getChildAt(3)).getChildAt(0).findViewById(R.id.spinnerJenisKelamin)).getSelectedItem().toString();
                                    }

                                    if (((AutoCompleteTextView) ((LinearLayout) linearLayout.getChildAt(3)).getChildAt(2)).getId() == R.id.spinnerKewarganegaraan) {
                                        String waneg = ((AutoCompleteTextView) ((LinearLayout) linearLayout.getChildAt(3)).getChildAt(2).findViewById(R.id.spinnerKewarganegaraan)).getText().toString();

                                        if (!waneg.equals("")) {
                                            for (int i = 0; i < listWargaNegara.size(); i++) {
                                                if (listWargaNegara.get(i).getWargaNegara().equalsIgnoreCase(waneg)) {
                                                    koneg = listWargaNegara.get(i).getKode();
                                                }
                                            }
                                        }
                                    }

                                    if (((EditText) ((LinearLayout) linearLayout.getChildAt(6)).getChildAt(0)).getId() == R.id.edTempatLahir) {
                                        tempatLahir = ((EditText) ((LinearLayout) linearLayout.getChildAt(6)).getChildAt(0).findViewById(R.id.edTempatLahir)).getText().toString();
                                    }

                                    if (((EditText) ((LinearLayout) linearLayout.getChildAt(6)).getChildAt(1)).getId() == R.id.edTanggalLahir) {
                                        tanggalLahir = ((EditText) ((LinearLayout) linearLayout.getChildAt(6)).getChildAt(1).findViewById(R.id.edTanggalLahir)).getText().toString();
                                    }

                                    Log.d("Nama tersangka", "Nama: " + idTersangka);

                                    listTersangka.add(new Tersangka(idTersangka, namaPelaku, jekel, koneg, tempatLahir, DateUtils.getNewDateForServer(tanggalLahir)));
                                }
                            }

                            progressDialog.setMessage("Mengubah data..");
                            progressDialog.show();
                            new DataSimpanUbahTask(mode).execute();
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
            case R.id.buttonbatal:
                //function batal
                Intent y = new Intent(TambahPenangananKasusNarkobaActivity.this, PenangananKasusNarkobaActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.imgAdd:
                Intent tambahBaruIntent = new Intent(TambahPenangananKasusNarkobaActivity.this, TambahPenangananKasusNarkobaActivity.class);
                tambahBaruIntent.putExtra("mode_form", "tambah");
                startActivity(tambahBaruIntent);
                finish();
                break;
            case R.id.imgAddPelaku:
                txtpelaku.setVisibility(View.GONE);

                LayoutInflater inflaterPelaku = getLayoutInflater();
                final LinearLayout layoutPelaku = (LinearLayout) inflaterPelaku.inflate(R.layout.item_list_pelaku, listLayoutPelaku, false);

                ImageView imgHapusPelaku = (ImageView)layoutPelaku.findViewById(R.id.imgHapus);
                Spinner spinnerJekel = (Spinner) layoutPelaku.findViewById(R.id.spinnerJenisKelamin);
                AutoCompleteTextView spinnerWaneg = (AutoCompleteTextView) layoutPelaku.findViewById(R.id.spinnerKewarganegaraan);
                final EditText edTanggaLahir = (EditText) layoutPelaku.findViewById(R.id.edTanggalLahir);

                String[] listJekel = {"Laki-Laki", "Perempuan"};

                spinnerJekel.setAdapter(new ArrayAdapter<String>(TambahPenangananKasusNarkobaActivity.this,
                        R.layout.spinner_item_small, listJekel));
                spinnerWaneg.setAdapter(new ArrayAdapter<WargaNegara>(TambahPenangananKasusNarkobaActivity.this,
                        R.layout.spinner_item_small, listWargaNegara));

                edTanggaLahir.setFocusable(false);
                edTanggaLahir.setCursorVisible(false);
                edTanggaLahir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pilihTanggalLahir(edTanggaLahir);
                    }
                });

                imgHapusPelaku.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listLayoutPelaku.removeView(layoutPelaku);

                        if (listLayoutPelaku.getChildCount() == 0) {
                            txtpelaku.setVisibility(View.VISIBLE);
                        }
                    }
                });

                listLayoutPelaku.addView(layoutPelaku);
                break;
            case R.id.imgViewbarangbukti:
                txtjumlahbarangbukti.setVisibility(View.GONE);
                headerBarbuk.setVisibility(View.VISIBLE);

                LayoutInflater inflaterBarbuk= getLayoutInflater();
                final RelativeLayout layoutBarbuk = (RelativeLayout) inflaterBarbuk.inflate(R.layout.item_list_barbuk, listLayoutTambahBarbuk, false);

                final AutoCompleteTextView spinnerJenis = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerJenis);
                AutoCompleteTextView spinnerSatuan = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerSatuan);
                final ImageView imgHapus = (ImageView) layoutBarbuk.findViewById(R.id.imgHapusBarbuk);

                spinnerJenis.setAdapter(new ArrayAdapter<JenisBarangBukti>(TambahPenangananKasusNarkobaActivity.this,
                        R.layout.spinner_item_small, listJenisBarbuk));
                spinnerSatuan.setAdapter(new ArrayAdapter<SatuanBarangBukti>(TambahPenangananKasusNarkobaActivity.this,
                        R.layout.spinner_item_small, listSatuanBarbuk));

                imgHapus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mode.equals("ubah")) {
                            int ind = ((LinearLayout)((RelativeLayout)((LinearLayout)spinnerJenis.getParent()).getParent()).getParent()).indexOfChild(((RelativeLayout)((LinearLayout)spinnerJenis.getParent()).getParent()));
                        }

                        listLayoutTambahBarbuk.removeView(layoutBarbuk);

                        if (listLayoutTambahBarbuk.getChildCount() == 0) {
                            txtjumlahbarangbukti.setVisibility(View.VISIBLE);
                            headerBarbuk.setVisibility(View.GONE);
                        }
                    }
                });

                listLayoutTambahBarbuk.addView(layoutBarbuk);
                break;
            case R.id.imageViewbarangbukti:
                uploadFotoKe = "1";

                AlertDialog alertUpload1 = new AlertDialog.Builder(TambahPenangananKasusNarkobaActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahPenangananKasusNarkobaActivity.this);

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
                break;
            case R.id.imageViewpelaku:
                uploadFotoKe = "2";

                AlertDialog alertUpload2 = new AlertDialog.Builder(TambahPenangananKasusNarkobaActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahPenangananKasusNarkobaActivity.this);

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
                alertUpload2.show();
                break;

            case R.id.imageViewlokasi:
                uploadFotoKe = "3";

                AlertDialog alertUpload3 = new AlertDialog.Builder(TambahPenangananKasusNarkobaActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahPenangananKasusNarkobaActivity.this);

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
                alertUpload3.show();
                break;
        }
    }

    private void pilihTanggalLahir(final EditText edTanggalLahir) {
        Calendar calendar1 = Calendar.getInstance();
        DatePickerDialog dpd1 = new DatePickerDialog(TambahPenangananKasusNarkobaActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                int month = monthOfYear + 1;
                String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                edTanggalLahir.setText(getNewDate(sDate));
            }
        }, calendar1.get(java.util.Calendar.YEAR), calendar1.get(java.util.Calendar.MONTH), calendar1.get(java.util.Calendar.DAY_OF_MONTH));
        dpd1.show();
    }

    //Inflate setiap components ui yang akan kita implement di alert dialog
    private void initDialogView(View dialogView) {
        btnTidak = (Button) dialogView.findViewById(R.id.btnTidak);
        btnYa = (Button) dialogView.findViewById(R.id.btnYa);
        imgClosePopup = (ImageView) dialogView.findViewById(R.id.imgClosePopup);
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
                    foto1 = ImageUtils.getInstance(TambahPenangananKasusNarkobaActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahPenangananKasusNarkobaActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahPenangananKasusNarkobaActivity.this).encodeBase64String(bmp);
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
                    foto1 = ImageUtils.getInstance(TambahPenangananKasusNarkobaActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahPenangananKasusNarkobaActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahPenangananKasusNarkobaActivity.this).encodeBase64String(bmp);
                }

            } else {
                Toast.makeText(this, "You haven't picked image.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void handleNewLocation(Location location) {
        Log.d("Location", location.toString());

//        txttkpkasus.setText(""+location.getLatitude());

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        new ReverseAddressTask(this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
            @Override
            public void onAdrressListener(final String address) {
                if (mode.equals("tambah")) {
                    txttkpkasus.setText(address);
                }
            }
        }).execute();
    }

    @Override
    public void getLatLong(double lat, double lng) {
        new ReverseAddressTask(this, lat, lng, new ReverseAddressTask.ReverseAddress() {
            @Override
            public void onAdrressListener(String address) {
                txttkpkasus.setText(address);
            }
        }).execute();
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
                    .url(Configuration.baseUrl+"/api/kasus/"+eventIDIntent)
                    .addHeader("Authorization", "Bearer " + prefUtils.getTokenKey())
                    .addHeader("Content-Type", "application/json")
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

                    txttanggalkejadian.setText(!data.getString("kasus_tanggal").equals("null") ? getNewDate1(data.getString("kasus_tanggal")) : "");
                    txttkpkasus.setText(!data.getString("kasus_tkp").equals("null") ? data.getString("kasus_tkp") : "");
                    edUraianSingkat.setText(!data.getString("uraian_singkat").equals("null") ? data.getString("uraian_singkat") : "");
                    edKeteranganLain.setText(!data.getString("keterangan_lainnya").equals("null") ? data.getString("keterangan_lainnya") : "");

                    fotoUbah1 = !data.getString("foto1").equalsIgnoreCase("null") ? data.getString("foto1") : "";
                    fotoUbah2 = !data.getString("foto2").equalsIgnoreCase("null") ? data.getString("foto2") : "";
                    fotoUbah3 = !data.getString("foto3").equalsIgnoreCase("null") ? data.getString("foto3") : "";

                    loadFoto(fotoUbah1, imgUpload1);
                    loadFoto(fotoUbah2, imgUpload2);
                    loadFoto(fotoUbah3, imgUpload3);

                    if (!fotoUbah1.equals("") || !fotoUbah2.equals("") || !fotoUbah3.equals("")) {
                        txtInfoUbah.setVisibility(View.VISIBLE);
                    } else {
                        txtInfoUbah.setVisibility(View.GONE);
                    }

                    progressDialog.dismiss();
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    progressDialog.dismiss();

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahPenangananKasusNarkobaActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahPenangananKasusNarkobaActivity.this, LoginActivity.class);
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

    class AmbileDataTask extends AsyncTask<Bitmap, Integer, String> {
        private String responseServer;
        private String typeGet;

        public AmbileDataTask(String typeGet) {
            this.typeGet = typeGet;
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

            if (typeGet.equals("jenis")) {
                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/jnsbrgbuktimobile")
                        .addHeader("Authorization", "Bearer " + prefUtils.getTokenKey())
                        .addHeader("Content-Type", "application/json")
                        .post(new FormBody.Builder().build())
                        .build();
            } else if (typeGet.equals("satuan")) {
                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/getsatuan")
                        .addHeader("Authorization", "Bearer " + prefUtils.getTokenKey())
                        .addHeader("Content-Type", "application/json")
                        .build();
            } else if (typeGet.equalsIgnoreCase("warga_negara")) {
                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/negara")
                        .addHeader("Authorization", "Bearer " + prefUtils.getTokenKey())
                        .addHeader("Content-Type", "application/json")
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
            Log.d("test", result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
//                    JSONObject data = jsonObject.getJSONObject("data");
                    if (typeGet.equals("jenis")) {
                        JSONArray dataJenis = jsonObject.getJSONArray("data");

                        for (int i = 0;i < dataJenis.length();i++) {
                            listJenisBarbuk.add(new JenisBarangBukti(dataJenis.getJSONObject(i).getString("id_brgbukti"),
                                    dataJenis.getJSONObject(i).getString("nm_brgbukti"),
                                    dataJenis.getJSONObject(i).getString("nm_jnsbrgbukti")));
                        }
                    } else if (typeGet.equals("satuan")) {
                        JSONArray dataSatuan = jsonObject.getJSONArray("data");

                        for (int i = 0;i < dataSatuan.length();i++) {
                            listSatuanBarbuk.add(new SatuanBarangBukti(dataSatuan.getJSONObject(i).getString("kd_satuan"),
                                    dataSatuan.getJSONObject(i).getString("nm_satuan")));
                        }
                    }  else if (typeGet.equals("warga_negara")) {
                        JSONArray dataSatuan = jsonObject.getJSONArray("data");

                        for (int i = 0;i < dataSatuan.length();i++) {
                            listWargaNegara.add(new WargaNegara(dataSatuan.getJSONObject(i).getString("kode"),
                                    dataSatuan.getJSONObject(i).getString("nama_negara")));
                        }
                    }

                    for (int i = 0; i < listLayoutTambahBarbuk.getChildCount(); i++) {
                        if (listLayoutTambahBarbuk.getChildAt(i) instanceof RelativeLayout) {
                            RelativeLayout relative = (RelativeLayout) listLayoutTambahBarbuk.getChildAt(i);

                            if (relative.getChildAt(0) instanceof LinearLayout) {
                                LinearLayout linear = (LinearLayout) relative.getChildAt(0);
                                if (linear.getChildAt(0).getId() == R.id.spinnerJenis) {
                                    AutoCompleteTextView atJenis = (AutoCompleteTextView) linear.getChildAt(0).findViewById(R.id.spinnerJenis);

                                    atJenis.setAdapter(new ArrayAdapter<JenisBarangBukti>(TambahPenangananKasusNarkobaActivity.this,
                                            R.layout.spinner_item_small, listJenisBarbuk));

                                }

                                if (linear.getChildAt(2).getId() == R.id.spinnerSatuan) {
                                    AutoCompleteTextView atSatuan = (AutoCompleteTextView) linear.getChildAt(2).findViewById(R.id.spinnerSatuan);

                                    atSatuan.setAdapter(new ArrayAdapter<SatuanBarangBukti>(TambahPenangananKasusNarkobaActivity.this,
                                            R.layout.spinner_item_small, listSatuanBarbuk));
                                }
                            }
                        }
                    }

                    if (listLayoutPelaku != null && listLayoutPelaku.getChildCount() > 0) {
                        for (int a = 0; a < listLayoutPelaku.getChildCount(); a++) {
                            LinearLayout linearLayout = (LinearLayout) listLayoutPelaku.getChildAt(a);

                            if (((AutoCompleteTextView) ((LinearLayout) linearLayout.getChildAt(3)).getChildAt(2)).getId() == R.id.spinnerKewarganegaraan) {
                                AutoCompleteTextView atWaneg = ((AutoCompleteTextView) ((LinearLayout) linearLayout.getChildAt(3)).getChildAt(2).findViewById(R.id.spinnerKewarganegaraan));

                                atWaneg.setAdapter(new ArrayAdapter<WargaNegara>(TambahPenangananKasusNarkobaActivity.this,
                                        R.layout.spinner_item_small, listWargaNegara));
                            }
                        }
                    }

                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
//                    progressBar.setVisibility(View.GONE);

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahPenangananKasusNarkobaActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahPenangananKasusNarkobaActivity.this, LoginActivity.class);
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
        private String idHapus;

        public DataSimpanUbahTask(String modeTask) {
            this.modeTask = modeTask;
        }

        public DataSimpanUbahTask(String modeTask, String idHapus) {
            this.modeTask = modeTask;
            this.idHapus = idHapus;
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

            Headers headers = new Headers.Builder().add("Authorization", "Bearer " + prefUtils.getTokenKey())
                    .add("Accept", "application/json").build();
                    //.add("Content-Type", "application/json")
                    //.build();

            if (modeTask.equals("tambah")) {
                FormBody.Builder form = new FormBody.Builder().add("kasus_tanggal", getNewDateForServer(tanggal))
                        .add("kasus_tkp", lokasi)
//                        .add("kasus_no", noLkn)
                        .add("uraian_singkat", uraianSingkat)
                        .add("keterangan_lainnya", keteranganLain)
                        .add("id_instansi", pelaksana_)
                        .add("koordinat", latitude+","+longitude);



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
                        .url(Configuration.baseUrl+"/api/kasus")
                       // .url(Configuration.DEV_URL_OSB + "/berantas/kasus")
                        .headers(headers)
                        .post(requestBody)
                        .build();
            } else if (modeTask.equals("ubah")) {
                FormBody.Builder form = new FormBody.Builder().add("kasus_tanggal", DateUtils.getNewDateForServer(tanggal))
                        .add("kasus_tkp", lokasi)
//                        .add("kasus_no", noLkn)
                        .add("uraian_singkat", uraianSingkat)
                        .add("keterangan_lainnya", keteranganLain)
                        .add("id_instansi", prefUtils.getInstansi())
                        .add("koordinat", latitude+","+longitude);

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
                        .url(Configuration.baseUrl+"/api/kasus/"+eventIDIntent)
                        .headers(headers)
                        .put(requestBody)
                        .build();
            } else if (modeTask.equals("tambah_barbuk")) {
                Log.d("Haha", "Haha "+sizeUploadBarbuk);

                requestBody = new FormBody.Builder().add("jumlah_barang_bukti", jumlahBarangBukti)
                        .add("kode_satuan_barang_bukti", kodeSatuanBarangBukti)
                        .add("kasus_id", kasusId)
                        .add("id_brgbukti", idBarangBukti).build();

                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/kasusbrgbukti")
                        //.url(Configuration.baseUrl + "/berantas/kasus")
                        .headers(headers)
                        .post(requestBody)
                        .build();
            } else if (modeTask.equals("ubah_barbuk")) {
                Log.d("Haha ubah", "Haha ubah"+sizeUploadBarbuk);

                requestBody = new FormBody.Builder().add("jumlah_barang_bukti", jumlahBarangBukti)
                        .add("kode_satuan_barang_bukti", kodeSatuanBarangBukti)
                        .add("kasus_id", kasusId)
                        .add("id_brgbukti", idBarangBukti).build();

                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/kasusbrgbukti/"+kasusBarbukId)
                        .headers(headers)
                        .put(requestBody)
                        .build();
            } else if (modeTask.equals("tambah_pelaku")) {
                Log.d("Haha", "Haha "+sizeUploadPelaku);

                requestBody = new FormBody.Builder().add("kode_jenis_kelamin", jekelPelaku)
                        .add("tersangka_nama", namaPelaku)
                        .add("kode_negara", kodeNegaraPelaku)
                        .add("tersangka_tempat_lahir", tempatLahirPelaku)
                        .add("tersangka_tanggal_lahir", tanggalLahirPelaku)
                        .add("kasus_id", kasusId)
                        .add("alamatdomisili","")
                        .add("alamatdomisili_idkabkota","")
                        .add("alamatdomisili_idprovinsi","")
                        .add("alamatdomisili_kodepos","")
                        .add("alamatktp_idkabkota","")
                        .add("alamatktp_idprovinsi","")
                        .add("alamatktp_kodepos","")
                        .add("alamatlainnya","")
                        .add("alamatlainnya_idkabkota","")
                        .add("alamatlainnya_idprovinsi","")
                        .add("alamatlainnya_kodepos","")
                        .add("create_date","")
                        .add("created_by","")
                        .add("kode_jenis_kelamin","")
                        .add("kode_jenisidentitas","")
                        .add("kode_kelompok_usia","")
                        .add("kode_negara","")
                        .add("kode_pekerjaan", "")
                        .add("kode_pendidikan_akhir", "")
                        .add("kode_peran_tersangka","201")
                        .add("kode_warga_negara","")
                        .add("no_identitas", "")
                        .add("pasal","")
                        .add("tersangka_alamat", "")
                        .add("tersangka_nama_alias","")
                        .add("tersangka_tanggal_lahir","2014-09-19")
                        .add("tersangka_tempat_lahir","")
                        .add("tersangka_usia", "201")
                        .add("update_date","2006-08-20T00:27:14+07:00")
                        .add("updated_by","201").build();


                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/tersangka")
                        //.url(Configuration.baseUrl + "/berantas/tersangka")
                        .headers(headers)
                        .post(requestBody)
                        .build();
            } else if (modeTask.equals("ubah_pelaku")) {
                Log.d("Haha", "Haha "+sizeUploadPelaku);

                requestBody = new FormBody.Builder().add("kode_jenis_kelamin", jekelPelaku)
                        .add("tersangka_nama", namaPelaku)
                        .add("kode_negara", kodeNegaraPelaku)
                        .add("tersangka_tempat_lahir", tempatLahirPelaku)
                        .add("tersangka_tanggal_lahir", tanggalLahirPelaku)
                        .add("kasus_id", kasusId).build();

                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/tersangka/"+kasusTersangkaId)
                        .headers(headers)
                        .put(requestBody)
                        .build();
            } else if (modeTask.equals("hapus_barbuk")) {
                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/kasusbrgbukti/"+idHapus)
                        .headers(headers)
                        .delete()
                        .build();
            }  else if (modeTask.equals("hapus_pelaku")) {
                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/tersangka/"+idHapus)
                        .headers(headers)
                        .delete()
                        .build();
            }

            Response httpResponse = null;

            try {
                httpResponse = httpclient.newCall(httpRequest).execute();
                Log.d("Jenis konten: ",httpResponse.body().contentType().toString());
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
                    if (modeTask.equals("ubah")) {
//                        JSONObject data = jsonObject.getJSONObject("data");

                        if (listBarangBukti.size() > 0) {
                            kasusId = eventIDIntent;
                            uploadBarBuk(progressDialog, eventIDIntent);
                        }  else if (listTersangka.size() > 0) {
                            kasusId = eventIDIntent;
                            uploadPelaku(progressDialog, eventIDIntent);
                        } else {
                            progressDialog.dismiss();

                            Toast.makeText(TambahPenangananKasusNarkobaActivity.this, "Data berhasil diubah.", Toast.LENGTH_LONG).show();
                            Intent listKasus = new Intent(TambahPenangananKasusNarkobaActivity.this, PenangananKasusNarkobaActivity.class);
                            startActivity(listKasus);
                            finish();
                        }

//                        progressDialog.dismiss();
//
//                        Toast.makeText(TambahPenangananKasusNarkobaActivity.this, "Data berhasil di ubah.", Toast.LENGTH_LONG).show();
//                        Intent listKasus = new Intent(TambahPenangananKasusNarkobaActivity.this, PenangananKasusNarkobaActivity.class);
//                        startActivity(listKasus);
//                        finish();
                    } else if (modeTask.equals("tambah")) {
                        JSONObject data = jsonObject.getJSONObject("data");

                        if (listBarangBukti.size() > 0) {
                            kasusId = data.getString("eventID");
                            uploadBarBuk(progressDialog, data.getString("eventID"));
                        }  else if (listTersangka.size() > 0) {
                            kasusId = data.getString("eventID");
                            uploadPelaku(progressDialog, data.getString("eventID"));
                        } else {
                            progressDialog.dismiss();

                            Toast.makeText(TambahPenangananKasusNarkobaActivity.this, "Data berhasil disimpan.", Toast.LENGTH_LONG).show();
                            Intent listKasus = new Intent(TambahPenangananKasusNarkobaActivity.this, PenangananKasusNarkobaActivity.class);
                            startActivity(listKasus);
                            finish();
                        }
                    } else if (modeTask.equals("tambah_barbuk")) {

                        if (sizeUploadBarbuk > (listBarangBukti.size()-1)) {
                            if (progressDialog != null) {
                                if (listTersangka.size() > 0) {
                                    uploadPelaku(progressDialog, kasusId);
                                } else {
                                    progressDialog.dismiss();

                                    Toast.makeText(TambahPenangananKasusNarkobaActivity.this, "Data berhasil disimpan.", Toast.LENGTH_LONG).show();
                                    Intent listKasus = new Intent(TambahPenangananKasusNarkobaActivity.this, PenangananKasusNarkobaActivity.class);
                                    startActivity(listKasus);
                                    finish();

                                    sizeUploadBarbuk = 0;
                                }
                            }
                        } else {
                            uploadBarBuk(progressDialog, kasusId);
                        }
                    } else if (modeTask.equals("ubah_barbuk")) {

                        Log.d("Size ubah", "Size ubah "+(sizeUploadBarbuk > (listBarangBukti.size()-1)));

                        if (sizeUploadBarbuk > (listBarangBukti.size()-1)) {
                            if (progressDialog != null) {
                                if (listTersangka.size() > 0) {
                                    uploadPelaku(progressDialog, kasusId);
                                } else {
                                    progressDialog.dismiss();

                                    Toast.makeText(TambahPenangananKasusNarkobaActivity.this, "Data berhasil diubah.", Toast.LENGTH_LONG).show();
                                    Intent listKasus = new Intent(TambahPenangananKasusNarkobaActivity.this, PenangananKasusNarkobaActivity.class);
                                    startActivity(listKasus);
                                    finish();

                                    sizeUploadBarbuk = 0;
                                }
                            }
                        } else {
                            uploadBarBuk(progressDialog, kasusId);
                        }
                    } else if (modeTask.equals("tambah_pelaku")) {

                        if (sizeUploadPelaku > (listTersangka.size()-1)) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();

                                Toast.makeText(TambahPenangananKasusNarkobaActivity.this, "Data berhasil disimpan.", Toast.LENGTH_LONG).show();
                                Intent listKasus = new Intent(TambahPenangananKasusNarkobaActivity.this, PenangananKasusNarkobaActivity.class);
                                startActivity(listKasus);
                                finish();

                                sizeUploadPelaku = 0;
                            }
                        } else {
                            uploadPelaku(progressDialog, kasusId);
                        }
                    } else if (modeTask.equals("ubah_pelaku")) {
                        if (sizeUploadPelaku > (listTersangka.size()-1)) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();

                                Toast.makeText(TambahPenangananKasusNarkobaActivity.this, "Data berhasil diubah.", Toast.LENGTH_LONG).show();
                                Intent listKasus = new Intent(TambahPenangananKasusNarkobaActivity.this, PenangananKasusNarkobaActivity.class);
                                startActivity(listKasus);
                                finish();

                                sizeUploadPelaku = 0;
                            }
                        } else {
                            uploadPelaku(progressDialog, kasusId);
                        }
                    } else if (modeTask.equals("hapus_barbuk")) {
                        progressDialog.dismiss();

                        Toast.makeText(TambahPenangananKasusNarkobaActivity.this, "Data berhasil dihapus.", Toast.LENGTH_LONG).show();

                    } else if (modeTask.equals("hapus_pelalu")) {
                        progressDialog.dismiss();

                        Toast.makeText(TambahPenangananKasusNarkobaActivity.this, "Data berhasil dihapus.", Toast.LENGTH_LONG).show();

                    }
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    progressDialog.dismiss();

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahPenangananKasusNarkobaActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahPenangananKasusNarkobaActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(TambahPenangananKasusNarkobaActivity.this, "Terjadi kesalahan, silahkan coba lagi.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    class AmbileDataPelaksanaTask extends AsyncTask<Bitmap, Integer, String> {
        private String responseServer;
        private String typeGet;

        public AmbileDataPelaksanaTask() {
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

            requestBody = new FormBody.Builder().add("a", "a").build();

                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/instansi")
                        .addHeader("Authorization", "Bearer " + prefUtils.getTokenKey())
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .post(requestBody)
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
//                    JSONObject data = jsonObject.getJSONObject("data");

                    JSONArray dataJenis = jsonObject.getJSONArray("data");

                    for (int i = 0; i < dataJenis.length(); i++) {
                        final Pelaksana pelaksana = new Pelaksana();
                        pelaksana.setId(dataJenis.getJSONObject(i).getString("id_instansi"));
                        pelaksana.setNama(dataJenis.getJSONObject(i).getString("nm_instansi"));
                        pelaksana_array.add(pelaksana);

                        AutoCompleteTextView spinner_pelaksana = (AutoCompleteTextView) findViewById(R.id.spinner_pelaksana);


                        spinner_pelaksana.setAdapter(new ArrayAdapter<Pelaksana>(TambahPenangananKasusNarkobaActivity.this,
                                R.layout.spinner_item_small, pelaksana_array));

                        spinner_pelaksana.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                                Log.i("", String.valueOf(arg0.getItemAtPosition(pos)));
                                Log.d("select", pelaksana_array.get(pos).getId());
                                pelaksana_ = pelaksana_array.get(pos).getId();
                            }
                        });

//                        spinner_pelaksana.setSelection(i);
//                        Log.d("spinner pelaksana", String.valueOf(spinner_pelaksana.getSelectionEnd()));
                    }

                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
//                    progressBar.setVisibility(View.GONE);

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahPenangananKasusNarkobaActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahPenangananKasusNarkobaActivity.this, LoginActivity.class);
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

    private void uploadBarBuk(ProgressDialog progressDialog, String eventIDIntent) {
        if (listBarangBukti.size() > 0 && !eventIDIntent.equals("null")) {
//            Log.d("Kesini ", "Kesini "+sizeUploadBarbuk+" "+(listSatuanBarbuk.size() > 0 && !eventIDIntent.equals("null")));

            jumlahBarangBukti = listBarangBukti.get(sizeUploadBarbuk).getJumlah();
            if (listSatuanBarbuk.size() > 0) {
                for (int x = 0; x < listSatuanBarbuk.size(); x++) {
                    if (listBarangBukti.get(sizeUploadBarbuk).getSatuan().equals(listSatuanBarbuk.get(x).getNama())) {
                        kodeSatuanBarangBukti = listSatuanBarbuk.get(x).getKode();
                    }
                }
            }

            if (listJenisBarbuk.size() > 0) {
                for (int x = 0; x < listJenisBarbuk.size(); x++) {
                    if (listBarangBukti.get(sizeUploadBarbuk).getJenisBarang().equals(listJenisBarbuk.get(x).getNama())) {
                        idBarangBukti = listJenisBarbuk.get(x).getId();
                    }
                }
            }

            if (!listBarangBukti.get(sizeUploadBarbuk).getKasusIdBarbuk().equalsIgnoreCase("")) {
                kasusBarbukId = listBarangBukti.get(sizeUploadBarbuk).getKasusIdBarbuk();
                new DataSimpanUbahTask("ubah_barbuk").execute();
            } else {
                new DataSimpanUbahTask("tambah_barbuk").execute();
            }

            sizeUploadBarbuk = sizeUploadBarbuk + 1;
        }
    }

    private void uploadPelaku(ProgressDialog progressDialog, String eventIDIntent) {
        if (listTersangka.size() > 0 && !eventIDIntent.equals("null")) {
            namaPelaku = listTersangka.get(sizeUploadPelaku).getNama();
            jekelPelaku = listTersangka.get(sizeUploadPelaku).getJekel().equalsIgnoreCase("Laki-Laki") ? "L" : "P";
            kodeNegaraPelaku = listTersangka.get(sizeUploadPelaku).getKodeNegara();
            tempatLahirPelaku = listTersangka.get(sizeUploadPelaku).getTempatLahir();
            tanggalLahirPelaku = listTersangka.get(sizeUploadPelaku).getTanggalLahir();

            if (!listTersangka.get(sizeUploadPelaku).getId().equalsIgnoreCase("")) {
                kasusTersangkaId = listTersangka.get(sizeUploadPelaku).getId();
                new DataSimpanUbahTask("ubah_pelaku").execute();
            } else {
                new DataSimpanUbahTask("tambah_pelaku").execute();
            }

            sizeUploadPelaku = sizeUploadPelaku + 1;
        }
    }

    public String getNewDate1(String date){
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

    public String getTodayDate(){
        String newDate = "01-01-2016";

        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
        newDate = newFormat.format(d);

        return newDate;
    }

    private Boolean isEmptyField(EditText editText) {
        String fieldData = editText.getText().toString();

        if (fieldData.equals("")) {
            return true;
        }

        return false;
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
        startActivity(new Intent(TambahPenangananKasusNarkobaActivity.this, PenangananKasusNarkobaActivity.class));
        finish();
    }
}

