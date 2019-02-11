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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bnn.Modal.BarangBukti;
import com.bnn.Modal.JenisBarangBukti;
import com.bnn.Modal.Listmenu;
import com.bnn.Modal.SatuanBarangBukti;
import com.bnn.Modal.WargaNegara;
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

public class TambahPemusnahanBarangBuktiActivity extends ActivityBase implements View.OnClickListener, LocationProvider.LocationCallback, MapsActivity.LocationListener {
    private static final String TAG = TambahPemusnahanBarangBuktiActivity.class.getSimpleName();

    ImageView imgToogle, imageback, imgTambahPenyidik, imgTambah, imgTambahBarangBukti, imgUpload1, imgUpload2, imgUpload3;
    EditText txttanggalkejadian, edLokasiPemusnahan, edJumlahBarangBukti, edPenyidik, edKeteranganLain, edNoId, edNoLkn, edPelaksana;
    Button btnsimpan, btnbatal;
    LinearLayout listLayoutPenyidik, listLayoutPelaku;
    ScrollView layoutUbahTambahData;

    Button btnYa, btnTidak;
    ImageView imgClosePopup;

    TextView txtJudul, txtInfoUbah;
    LinearLayout  layoutJudulNoId, layoutJudulNoLkn, headerBarbuk;
    RelativeLayout  layoutContainerNoId, layoutContainerNoLkn;

    //Layout variable untuk tambah ubah barbuk
    ImageView imgTambahBarbuk;
    LinearLayout listLayoutTambahBarbuk;
    Button btnSimpanBarbuk, btnBatalBarbuk;

    //Variable untuk menentukan jenis form ubah atau tambah
    String mode = "", pemusnahanIDIntent = "", no_lkn = "";

    PreferenceUtils prefUtils;
    ImageUtils imageUtils;

    //Ditambahkan pada 06 Nov 2017
    private String[] from = {"Ambil Foto", "Dari Galeri"};
    private Uri fileUri;
    private String uploadFotoKe = "";
    private String foto1 = "", foto2 = "", foto3 = "", fotoUbah1 = "", fotoUbah2 = "", fotoUbah3 = "";

    private String jumlahBarangBukti, kodeSatuanBarangBukti, kasusId, idBarangBukti;

    private ArrayList<BarangBukti> listBarangBukti = new ArrayList<>();
    private ArrayList<JenisBarangBukti> listJenisBarbuk = new ArrayList<>();
//    private ArrayList<SatuanBarangBukti> listSatuanBarbuk = new ArrayList<>();

    //Variable for input kasus
    private String tanggal, lokasi, metaBarbuk;

    private LocationProvider mLocationProvider;

    ProgressDialog progressDialog;
    int sizeUploadBarbuk = 0;
    int sizeUploadPelaku = 0;

    double longitude = 0.0;
    double latitude = 0.0;
    
    String arrayBarbuk = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_tambah_pemusnahanbarangbukti, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);
        imageUtils = ImageUtils.getInstance(this);

        progressDialog = new ProgressDialog(this);
        MapsActivity.locationListener = this;

        pemusnahanIDIntent = getIntent().getStringExtra("pemusnahan_id") != null ? getIntent().getStringExtra("pemusnahan_id") : "";
        no_lkn = getIntent().getStringExtra("no_lkn") != null ? getIntent().getStringExtra("no_lkn"): "";
        mode = getIntent().getStringExtra("mode_form") != null ? getIntent().getStringExtra("mode_form") : "tambah";
//        arrayBarbuk = getIntent().getStringExtra("array_barbuk") != null ? getIntent().getStringExtra("array_barbuk") : "";

        initLayoutTambahUbahData();

//        detectLocation();

        imgToogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDrawerLayout.openDrawer(linearLayout2);
            }
        });

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent y = new Intent(TambahPemusnahanBarangBuktiActivity.this, PemusnahanBarangBuktiActivity.class);
                startActivity(y);
                finish();
            }
        });

        if (!arrayBarbuk.equals("")) {
            try {
                JSONArray barbukObj = new JSONArray(arrayBarbuk);

                if (barbukObj.length() > 0) {
                    edJumlahBarangBukti.setVisibility(View.GONE);
                } else {
                    edJumlahBarangBukti.setVisibility(View.VISIBLE);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initLayoutTambahUbahData() {
        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imageback = (ImageView) findViewById(R.id.imageback);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        btnsimpan = (Button) findViewById(R.id.btnSimpan);
        btnbatal = (Button) findViewById(R.id.btnBatal);
        txtInfoUbah = (TextView) findViewById(R.id.txtInfoUbah);

        btnsimpan.setOnClickListener(this);
        btnbatal.setOnClickListener(this);

//        imgBukaMap = (ImageView) findViewById(R.id.imgLokasiPemusnahan);

//        //Ditambahkan pada 05 Nov 2017
        layoutJudulNoId = (LinearLayout) findViewById(R.id.layoutJudulNoId);
        layoutJudulNoLkn = (LinearLayout) findViewById(R.id.layoutJudulNoLkn);
        layoutContainerNoId = (RelativeLayout) findViewById(R.id.layoutContainerNoId);
        layoutContainerNoLkn = (RelativeLayout) findViewById(R.id.layoutContainerNoLkn);
        headerBarbuk = (LinearLayout) findViewById(R.id.headerBarbuk);

        txttanggalkejadian = (EditText) findViewById(R.id.tanggalkejadian);
        edLokasiPemusnahan = (EditText) findViewById(R.id.edLokasiPemusnahan);
        edKeteranganLain = (EditText) findViewById(R.id.edKeteranganLain);
        edPenyidik = (EditText) findViewById(R.id.edPenyidik);
        edJumlahBarangBukti = (EditText) findViewById(R.id.edJumlahBarangBukti);

        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        imgTambahBarangBukti = (ImageView) findViewById(R.id.imgTambahBarbuk);
        txtJudul = (TextView) findViewById(R.id.textViewjudul);

        layoutUbahTambahData = (ScrollView) findViewById(R.id.layoutUbahTambahData);

        //Init layoutlist pelaku
        listLayoutTambahBarbuk = (LinearLayout) findViewById(R.id.listLayoutTambahBarbuk);

          //Init layoutlist penyidik
        imgTambahPenyidik = (ImageView) findViewById(R.id.imgAddPenyidik);
        listLayoutPenyidik = (LinearLayout) findViewById(R.id.layoutListPenyidik);

        imgTambahPenyidik.setOnClickListener(this);
        imgTambahBarangBukti.setOnClickListener(this);
        imgUpload1.setOnClickListener(this);
        imgUpload2.setOnClickListener(this);
        imgUpload3.setOnClickListener(this);
        imgTambah.setOnClickListener(this);

        //Ditambahkan pada 06 Nov 2017
//        imgBukaMap.setOnClickListener(this);

//        new AmbileDataTask("satuan").execute();
        new AmbileDataTask().execute();

        if (mode.equals("ubah")) {
            txtJudul.setText("UBAH DATA");
            imgTambah.setVisibility(View.VISIBLE);

            txttanggalkejadian.setFocusable(true);
            txttanggalkejadian.setCursorVisible(true);
            txttanggalkejadian.setOnClickListener(this);

            new DataUploadTask().execute();
        } else {
            txttanggalkejadian.setText(getTodayDate());
            txttanggalkejadian.setOnClickListener(this);

            imgTambah.setVisibility(View.GONE);

            txtJudul.setText("TAMBAH DATA BARU");
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

                new ReverseAddressTask(TambahPemusnahanBarangBuktiActivity.this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
                    @Override
                    public void onAdrressListener(String address) {
                        if (mode.equals("tambah")) {
                            edLokasiPemusnahan.setText(address);
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
                        edLokasiPemusnahan.setText(address);
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
                Intent tambahBaruIntent = new Intent(TambahPemusnahanBarangBuktiActivity.this, TambahPemusnahanBarangBuktiActivity.class);
                tambahBaruIntent.putExtra("mode_form", "tambah");
                startActivity(tambahBaruIntent);
                finish();
                break;
            case R.id.tanggalkejadian:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TambahPemusnahanBarangBuktiActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        txttanggalkejadian.setText(getNewDate(sDate));
                    }
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
                dpd.show();
                break;
//            case R.id.imgLokasiPemusnahan:
//                //Inflate custom layout untuk menampilkan form out-office
//                View dialogView = getLayoutInflater().inflate(R.layout.popup_bukamap, null);
//                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
//                final AlertDialog alertDialog = new AlertDialog.Builder(TambahPemusnahanBarangBuktiActivity.this)
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
//                        Intent bukaMap = new Intent(TambahPemusnahanBarangBuktiActivity.this, MapsActivity.class);
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
//            case R.id.imgTambahBarbuk:
//                edJumlahBarangBukti.setVisibility(View.GONE);
//                headerBarbuk.setVisibility(View.VISIBLE);
//
//                LayoutInflater inflaterBarbuk= getLayoutInflater();
//                final RelativeLayout layoutBarbuk = (RelativeLayout) inflaterBarbuk.inflate(R.layout.item_list_barbuk, listLayoutTambahBarbuk, false);
//
//                AutoCompleteTextView spinnerJenis = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerJenis);
//                AutoCompleteTextView spinnerJumlah = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerJumlah);
//                AutoCompleteTextView spinnerSatuan = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerSatuan);
//                ImageView imgHapus = (ImageView) layoutBarbuk.findViewById(R.id.imgHapusBarbuk);
//
//                spinnerJenis.setAdapter(new ArrayAdapter<JenisBarangBukti>(TambahPemusnahanBarangBuktiActivity.this,
//                        R.layout.spinner_item_small, listJenisBarbuk));
////                spinnerSatuan.setAdapter(new ArrayAdapter<SatuanBarangBukti>(TambahPemusnahanBarangBuktiActivity.this,
////                        R.layout.spinner_item_small, listSatuanBarbuk));
//
//                imgHapus.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        listLayoutTambahBarbuk.removeView(layoutBarbuk);
//
//                        if (listLayoutTambahBarbuk.getChildCount() == 0) {
//                            edJumlahBarangBukti.setVisibility(View.VISIBLE);
//                            headerBarbuk.setVisibility(View.GONE);
//                        }
//                    }
//                });
//
//                listLayoutTambahBarbuk.addView(layoutBarbuk);
//                break;
            case R.id.btnSimpan:
                if (Configuration.isDeviceOnline(TambahPemusnahanBarangBuktiActivity.this)) {
                    if (mode.equals("tambah")) {
                        if (isEmptyField(txttanggalkejadian)) {
                            Toast.makeText(TambahPemusnahanBarangBuktiActivity.this,
                                    "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (isEmptyField(edLokasiPemusnahan)) {
                            Toast.makeText(TambahPemusnahanBarangBuktiActivity.this,
                                    "Lokasi pemusnahan tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (!isEmptyField(txttanggalkejadian) && !isEmptyField(edLokasiPemusnahan)) {
                            tanggal = txttanggalkejadian.getText().toString();
                            lokasi = edLokasiPemusnahan.getText().toString();
                            metaBarbuk = "";
//                            keteranganLainnya = edKeteranganLain.getText().toString();

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
                                                listBarangBukti.add(new BarangBukti(jenis, jumlah, satuan));
                                            }

                                        }
                                    }
                                }
                            }

                            JSONArray arrayBarbuk = new JSONArray();

                            if (listBarangBukti.size() > 0) {
                                for (int i = 0; i < listBarangBukti.size(); i++) {
                                    JSONObject objectBarbuk = new JSONObject();

                                    try {
                                        objectBarbuk.put("nm_brgbukti", listBarangBukti.get(i).getJenisBarang());
                                        objectBarbuk.put("jumlah_barang_bukti", listBarangBukti.get(i).getJumlah());
                                        objectBarbuk.put("nm_satuan", listBarangBukti.get(i).getSatuan());

                                        arrayBarbuk.put(objectBarbuk);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                metaBarbuk = arrayBarbuk.toString();
                            }

                            progressDialog.setMessage("Menyimpan data..");
                            progressDialog.show();
                            new DataSimpanUbahTask(mode).execute();
                        }
                    } else if (mode.equals("ubah")) {
                        if (isEmptyField(txttanggalkejadian)) {
                            Toast.makeText(TambahPemusnahanBarangBuktiActivity.this,
                                    "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (isEmptyField(edLokasiPemusnahan)) {
//                            Toast.makeText(TambahPemusnahanBarangBuktiActivity.this,
//                                    "Tkp kasus tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                            edLokasiPemusnahan.setText("null");
                        } else if (!isEmptyField(txttanggalkejadian) && !isEmptyField(edLokasiPemusnahan)) {
                            tanggal = txttanggalkejadian.getText().toString();
                            lokasi = edLokasiPemusnahan.getText().toString();
                            metaBarbuk = "";
//                            keteranganLainnya = edKeteranganLain.getText().toString();

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
                                                listBarangBukti.add(new BarangBukti(jenis, jumlah, satuan));
                                            }

                                        }
                                    }
                                }
                            }

                            JSONArray arrayBarbuk = new JSONArray();

                            if (listBarangBukti.size() > 0) {
                                for (int i = 0; i < listBarangBukti.size(); i++) {
                                    JSONObject objectBarbuk = new JSONObject();

                                    try {
                                        objectBarbuk.put("nm_brgbukti", listBarangBukti.get(i).getJenisBarang());
                                        objectBarbuk.put("jumlah_barang_bukti", listBarangBukti.get(i).getJumlah());
                                        objectBarbuk.put("nm_satuan", listBarangBukti.get(i).getSatuan());

                                        arrayBarbuk.put(objectBarbuk);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                metaBarbuk = arrayBarbuk.toString();
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
            case R.id.btnBatal:
                //function batal
                Intent y = new Intent(TambahPemusnahanBarangBuktiActivity.this, PemusnahanBarangBuktiActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.imgAddPenyidik:
                edPenyidik.setVisibility(View.GONE);

                LayoutInflater inflaterPenyidik = getLayoutInflater();
                final RelativeLayout layoutPenyidik = (RelativeLayout) inflaterPenyidik.inflate(R.layout.item_list_penyidik, listLayoutPenyidik, false);

                ImageView imgHapusPenyidik = (ImageView)layoutPenyidik.findViewById(R.id.imgHapus);
                Spinner spinnerNamePenyidik = (Spinner) layoutPenyidik.findViewById(R.id.spinnerNama);

                String[] listPenyidik = {"Penyidik 1", "Penyidik 2", "Penyidik 3", "Penyidik 4"};

                spinnerNamePenyidik.setAdapter(new ArrayAdapter<String>(TambahPemusnahanBarangBuktiActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, listPenyidik));

                imgHapusPenyidik.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listLayoutPenyidik.removeView(layoutPenyidik);

                        if (listLayoutPenyidik.getChildCount() == 0) {
                            edPenyidik.setVisibility(View.VISIBLE);
                        }
                    }
                });

                listLayoutPenyidik.addView(layoutPenyidik);
                break;
            case R.id.imageViewbarangbukti:
                uploadFotoKe = "1";

                AlertDialog alertUpload1 = new AlertDialog.Builder(TambahPemusnahanBarangBuktiActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahPemusnahanBarangBuktiActivity.this);

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

                AlertDialog alertUpload2 = new AlertDialog.Builder(TambahPemusnahanBarangBuktiActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahPemusnahanBarangBuktiActivity.this);

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

                AlertDialog alertUpload3 = new AlertDialog.Builder(TambahPemusnahanBarangBuktiActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahPemusnahanBarangBuktiActivity.this);

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
                    foto1 = ImageUtils.getInstance(TambahPemusnahanBarangBuktiActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahPemusnahanBarangBuktiActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahPemusnahanBarangBuktiActivity.this).encodeBase64String(bmp);
                }

                Log.d("Base64", "Base64: "+ImageUtils.getInstance(TambahPemusnahanBarangBuktiActivity.this).encodeBase64String(bmp));

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
                    foto1 = ImageUtils.getInstance(TambahPemusnahanBarangBuktiActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahPemusnahanBarangBuktiActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahPemusnahanBarangBuktiActivity.this).encodeBase64String(bmp);
                }

                Log.d("Base64", "Base64: "+ImageUtils.getInstance(TambahPemusnahanBarangBuktiActivity.this).encodeBase64String(bmp));

            } else {
                Toast.makeText(this, "You haven't picked image.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TambahPemusnahanBarangBuktiActivity.this, PemusnahanBarangBuktiActivity.class));
        finish();
    }

    public String getTodayDate(){
        String newDate = "01-01-2016";

        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
        newDate = newFormat.format(d);

        return newDate;
    }

    @Override
    public void handleNewLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        new ReverseAddressTask(this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
            @Override
            public void onAdrressListener(final String address) {
                if (mode.equals("tambah")) {
                    edLokasiPemusnahan.setText(address);
                }
            }
        }).execute();
    }

    @Override
    public void getLatLong(double lat, double lng) {
        if (lat != 0.0 && lng != 0.0) {
            new ReverseAddressTask(this, lat, lng, new ReverseAddressTask.ReverseAddress() {
                @Override
                public void onAdrressListener(String address) {
                    edLokasiPemusnahan.setText(address);
                }
            }).execute();
        }
    }

    class DataUploadTask extends AsyncTask<Bitmap, Integer, String> {
        private String responseServer;
        private String keyKataKunci;

        public DataUploadTask() {
            progressDialog.setMessage("Mengambil data.");
            progressDialog.show();
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

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/pemusnahandetail/"+pemusnahanIDIntent)
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

                    txttanggalkejadian.setText(!data.getString("tgl_pemusnahan").equals("null") ? getNewDate(data.getString("tgl_pemusnahan")) : "");
                    edLokasiPemusnahan.setText(!data.getString("lokasi").equals("null") ? data.getString("lokasi") : "");
//                    edKeteranganLain.setText(!data.getString("keterangan_lainnya").equals("null") ? data.getString("keterangan_lainnya"): "");

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

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahPemusnahanBarangBuktiActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahPemusnahanBarangBuktiActivity.this, LoginActivity.class);
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

        public AmbileDataTask() {
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
//            Request httpRequest = null;

//            if (typeGet.equals("jenis")) {
//                httpRequest = new Request.Builder()
//                        .url(Configuration.baseUrl+"/api/jnsbrgbuktimobile")
//                        .addHeader("Authorization", "Bearer " + prefUtils.getTokenKey())
//                        .post(new FormBody.Builder().build())
//                        .build();
//            } else if (typeGet.equals("satuan")) {
//                httpRequest = new Request.Builder()
//                        .url(Configuration.baseUrl+"/api/getsatuan")
//                        .addHeader("Authorization", "Bearer " + prefUtils.getTokenKey())
//                        .build();
//            }
//            RequestBody requestBody = null;
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
                    JSONArray data = jsonObject.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject list = data.getJSONObject(i);
                        JSONArray jarr = list.getJSONArray("bbdetail");
                        for (int j = 0; j < jarr.length(); j++) {
                            JSONObject jobs = jarr.getJSONObject(j);

                            String jumlah = list.getString("bbdetail").equals("null") ? "-" :
                                    String.valueOf(new JSONArray(list.getString("bbdetail")).length());

                            listJenisBarbuk.add(new JenisBarangBukti(jobs.getString("nm_brgbukti"),
                                    jobs.getString("jumlah_dimusnahkan"),
                                    jobs.getString("nm_satuan")));

                            headerBarbuk.setVisibility(View.VISIBLE);

                                LayoutInflater inflaterBarbuk= getLayoutInflater();
                                final RelativeLayout layoutBarbuk = (RelativeLayout) inflaterBarbuk.inflate(R.layout.item_list_barbuk, listLayoutTambahBarbuk, false);

                                final AutoCompleteTextView spinnerJenis = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerJenis);
                                AutoCompleteTextView spinnerSatuan = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerSatuan);
                                AutoCompleteTextView spinnerJumlah = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerJumlah);
                                final ImageView imgHapus = (ImageView) layoutBarbuk.findViewById(R.id.imgHapusBarbuk);

                                spinnerJenis.setAdapter(new ArrayAdapter<JenisBarangBukti>(TambahPemusnahanBarangBuktiActivity.this,
                                        R.layout.spinner_item_small, listJenisBarbuk));
//                    spinnerSatuan.setAdapter(new ArrayAdapter<SatuanBarangBukti>(TambahPemusnahanBarangBuktiActivity.this,
//                            R.layout.spinner_item_small, listSatuanBarbuk));

                                spinnerJenis.setText(!jobs.getString("nm_brgbukti").equals("null") ? jobs.getString("nm_brgbukti") : "");
                                spinnerJumlah.setText(!jobs.getString("jumlah_dimusnahkan").equals("null") ? jobs.getString("jumlah_dimusnahkan") : "-");
                                spinnerSatuan.setText(!jobs.getString("nm_satuan").equals("null") ? jobs.getString("nm_satuan") : "GRAM");
                                imgHapus.setEnabled(false);
                                imgHapus.setVisibility(View.GONE);
                                spinnerJenis.setEnabled(false);
                                spinnerSatuan.setEnabled(false);
                                imgHapus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (mode.equals("ubah")) {
                                            int ind = ((LinearLayout)((RelativeLayout)((LinearLayout)spinnerJenis.getParent()).getParent()).getParent()).indexOfChild(((RelativeLayout)((LinearLayout)spinnerJenis.getParent()).getParent()));

//                                Log.d("Index", "index "+spinnerJenis.getText().toString());
                                            progressDialog.setMessage("Menghapus data..");
                                            progressDialog.show();

                                            new TambahPemusnahanBarangBuktiActivity.DataSimpanUbahTask("hapus_barbuk", (String)spinnerJenis.getTag()).execute();
                                        }

                                        listLayoutTambahBarbuk.removeView(layoutBarbuk);

                                        if (listLayoutTambahBarbuk.getChildCount() == 0) {
                                            edJumlahBarangBukti.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });

                                listLayoutTambahBarbuk.addView(layoutBarbuk);

//                    listBarangBukti.add(new BarangBukti(((JSONObject)barbukObj.get(i)).getString("nm_brgbukti"),
//                            ((JSONObject)barbukObj.get(i)).getString("jumlah_barang_bukti"),
//                            ((JSONObject)barbukObj.get(i)).getString("nm_satuan"),
//                            ((JSONObject)barbukObj.get(i)).getString("kasus_barang_bukti_id")));




//                            menuLists.add(new Listmenu(jobs.getString("nm_brgbukti"),
//                                    "",
//                                    jobs.getString("jumlah_dimusnahkan"),
//                                    "",
//                                    "",
//                                    "",
//                                    "",
//                                    ""));
                        }
                    }



//
//                    if (typeGet.equals("jenis")) {
//                        JSONArray dataJenis = jsonObject.getJSONArray("data");
//
//                        for (int i = 0;i < dataJenis.length();i++) {
//                            listJenisBarbuk.add(new JenisBarangBukti(dataJenis.getJSONObject(i).getString("id_brgbukti"),
//                                    dataJenis.getJSONObject(i).getString("nm_brgbukti"),
//                                    dataJenis.getJSONObject(i).getString("nm_jnsbrgbukti")));
//                        }
//                    } else if (typeGet.equals("satuan")) {
//                        JSONArray dataSatuan = jsonObject.getJSONArray("data");
//
//                        for (int i = 0;i < dataSatuan.length();i++) {
//                            listSatuanBarbuk.add(new SatuanBarangBukti(dataSatuan.getJSONObject(i).getString("kd_satuan"),
//                                    dataSatuan.getJSONObject(i).getString("nm_satuan")));
//                        }
//                    }
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
//                    progressBar.setVisibility(View.GONE);

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahPemusnahanBarangBuktiActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahPemusnahanBarangBuktiActivity.this, LoginActivity.class);
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
        private String idBarbuk;

        public DataSimpanUbahTask(String modeTask) {
            this.modeTask = modeTask;
        }

        public DataSimpanUbahTask(String modeTask, String idBarbuk) {
            this.modeTask = modeTask;
            this.idBarbuk = idBarbuk;
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

            if (modeTask.equals("tambah")) {
                FormBody.Builder form = new FormBody.Builder().add("tgl_pemusnahan", getNewDateForServer(tanggal))
                        .add("lokasi", lokasi)
//                        .add("keterangan_lainnya", keteranganLainnya)
                        .add("meta_brgbukti", metaBarbuk)
                        .add("id_instansi", prefUtils.getInstansi())
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
                        .url(Configuration.baseUrl+"/api/pemusnahanmobile")
                        .headers(headers)
                        .post(requestBody)
                        .build();
            } else if (modeTask.equals("ubah")) {
                if (latitude == 0 && longitude == 0){
                    Log.d("nol", "nol");
                }
                FormBody.Builder form = new FormBody.Builder().add("tgl_pemusnahan", getNewDateForServer(tanggal))
                        .add("lokasi", lokasi)
//                        .add("keterangan_lainnya", keteranganLainnya)
                        .add("meta_brgbukti", metaBarbuk)
                        .add("id_instansi", prefUtils.getInstansi())
                        .add("koordinat", latitude+","+longitude);

                Log.d("metabarbuk", metaBarbuk);
                Log.d("lokasi", lokasi);
                Log.d("id_instansi", prefUtils.getInstansi());
                Log.d("metabarbuk", metaBarbuk);


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
                        .url(Configuration.baseUrl+"/api/pemusnahanmobile/"+pemusnahanIDIntent)
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
                        .headers(headers)
                        .post(requestBody)
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
            progressDialog.dismiss();

            Log.d("test", result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
                    if (modeTask.equals("ubah")) {
                        progressDialog.dismiss();

                        Toast.makeText(TambahPemusnahanBarangBuktiActivity.this, "Data berhasil diubah.", Toast.LENGTH_LONG).show();
                        Intent listKasus = new Intent(TambahPemusnahanBarangBuktiActivity.this, PemusnahanBarangBuktiActivity.class);
                        startActivity(listKasus);
                        finish();
                    } else if (modeTask.equals("tambah")) {
                        progressDialog.dismiss();

                        Toast.makeText(TambahPemusnahanBarangBuktiActivity.this, "Data berhasil disimpan.", Toast.LENGTH_LONG).show();
                        Intent listPemusnahan = new Intent(TambahPemusnahanBarangBuktiActivity.this, PemusnahanBarangBuktiActivity.class);
                        startActivity(listPemusnahan);
                        finish();
                    }
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    progressDialog.dismiss();

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahPemusnahanBarangBuktiActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahPemusnahanBarangBuktiActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(TambahPemusnahanBarangBuktiActivity.this, "Terjadi kesalahan, silahkan coba lagi.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

//    class DataSimpanUbahTask extends AsyncTask<Bitmap, Integer, String> {
//        private String responseServer;
//        private String modeTask;
//        private String idBarbuk;
//
//        public DataSimpanUbahTask(String modeTask) {
//            this.modeTask = modeTask;
//        }
//
//        public DataSimpanUbahTask(String modeTask, String idBarbuk) {
//            this.modeTask = modeTask;
//            this.idBarbuk = idBarbuk;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            responseServer = "";
//        }
//
//        @Override
//        protected String doInBackground(Bitmap... b) {
//            OkHttpClient httpclient = new OkHttpClient();
//
//            RequestBody requestBody = null;
//            Request httpRequest = null;
//
//            Headers headers = new Headers.Builder().add("Authorization", "Bearer " + prefUtils.getTokenKey())
//                    .add("Accept", "application/json").build();
//
//            if (modeTask.equals("tambah")) {
//                requestBody = new FormBody.Builder().add("tgl_pemusnahan", DateUtils.getNewDateForServer(tanggal))
//                        .add("nomor_lkn", noLkn)
//                        .build();
//
//                httpRequest = new Request.Builder()
//                        .url(Configuration.baseUrl+"/api/pemusnahan")
//                        .headers(headers)
//                        .post(requestBody)
//                        .build();
//            } else if (modeTask.equals("ubah")) {
//                requestBody = new FormBody.Builder().add("tgl_pemusnahan", DateUtils.getNewDateForServer(tanggal))
//                        .build();
//
//                httpRequest = new Request.Builder()
//                        .url(Configuration.baseUrl+"/api/pemusnahan/"+pemusnahanIDIntent)
//                        .headers(headers)
//                        .put(requestBody)
//                        .build();
//            } else if (modeTask.equals("tambah_barbuk")) {
//                Log.d("Haha", "Haha "+sizeUploadBarbuk);
//
//                requestBody = new FormBody.Builder().add("jumlah_barang_bukti", jumlahBarangBukti)
//                        .add("kode_satuan_barang_bukti", kodeSatuanBarangBukti)
//                        .add("kasus_id", kasusId)
//                        .add("id_brgbukti", idBarangBukti).build();
//
//                httpRequest = new Request.Builder()
//                        .url(Configuration.baseUrl+"/api/kasusbrgbukti")
//                        .headers(headers)
//                        .post(requestBody)
//                        .build();
//            }
//
//            Response httpResponse = null;
//
//            try {
//                httpResponse = httpclient.newCall(httpRequest).execute();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                if (httpResponse != null) {
//                    responseServer = httpResponse.body().string();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return responseServer;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            Log.d("test", result);
//            try {
//                JSONObject jsonObject = new JSONObject(result);
//
//                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
//
//                    if (modeTask.equals("ubah")) {
//                        progressDialog.dismiss();
//
//                        Toast.makeText(TambahPemusnahanBarangBuktiActivity.this, "Data berhasil di ubah.", Toast.LENGTH_LONG).show();
//                        Intent listKasus = new Intent(TambahPemusnahanBarangBuktiActivity.this, PemusnahanBarangBuktiActivity.class);
//                        startActivity(listKasus);
//                        finish();
//                    } else if (modeTask.equals("tambah")) {
//                        JSONObject data = jsonObject.getJSONObject("data");
//
//                        if (listBarangBukti.size() > 0) {
//                            kasusId = data.getString("eventID");
//                            uploadBarBuk(progressDialog, data.getString("eventID"));
//                        } else {
//                            progressDialog.dismiss();
//
//                            Toast.makeText(TambahPemusnahanBarangBuktiActivity.this, "Data berhasil disimpan.", Toast.LENGTH_LONG).show();
//                            Intent listPemusnahan = new Intent(TambahPemusnahanBarangBuktiActivity.this, PemusnahanBarangBuktiActivity.class);
//                            startActivity(listPemusnahan);
//                            finish();
//                        }
//                    }
//                }
//            } catch(JSONException e){
//                e.printStackTrace();
//            }
//        }
//    }

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

//    private void uploadBarBuk(ProgressDialog progressDialog, String eventIDIntent) {
//        if (listBarangBukti.size() > 0 && !eventIDIntent.equals("null")) {
//            Log.d("Kesini ", "Kesini "+sizeUploadBarbuk+" "+(listBarangBukti.size() > 0 && !eventIDIntent.equals("null")));
//
//            jumlahBarangBukti = listBarangBukti.get(sizeUploadBarbuk).getJumlah();
//            if (listSatuanBarbuk.size() > 0) {
//                for (int x = 0; x < listSatuanBarbuk.size(); x++) {
//                    if (listBarangBukti.get(sizeUploadBarbuk).getSatuan().equals(listSatuanBarbuk.get(x).getNama())) {
//                        kodeSatuanBarangBukti = listSatuanBarbuk.get(x).getKode();
//                    }
//                }
//            }
//
//            if (listJenisBarbuk.size() > 0) {
//                for (int x = 0; x < listJenisBarbuk.size(); x++) {
//                    if (listBarangBukti.get(sizeUploadBarbuk).getJenisBarang().equals(listJenisBarbuk.get(x).getNama())) {
//                        idBarangBukti = listJenisBarbuk.get(x).getId();
//                    }
//                }
//            }
//
//            new DataSimpanUbahTask("tambah_barbuk").execute();
//
//            sizeUploadBarbuk = sizeUploadBarbuk + 1;
//        }
//    }
}

