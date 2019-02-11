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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bnn.Modal.Pelaksana;
import com.bnn.R;
import com.bnn.Task.LocationProvider;
import com.bnn.Task.ReverseAddressTask;
import com.bnn.Task.ReverseKabupatenTask;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.Configuration;
import com.bnn.Utils.DateUtils;
import com.bnn.Utils.ImageUtils;
import com.bnn.Utils.LayoutUtil;
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
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by USER on 11/1/2017.
 */

public class TambahKegiatanAsistensiActivity extends ActivityBase implements View.OnClickListener, LocationProvider.LocationCallback, MapsActivity.LocationListener {

    ImageView imgToogle, imageback, imgTambah, imgLokasiKegiatan, imgJumlahPeserta, imgUpload1, imgUpload2,
            imgUpload3, iconTitle;
    EditText edNoId, edJenisKegiatan, edTanggalKegiatan, edLokasiKegiatan, edUraianSingkatMateri, edInstansi, 
            edSasaran, edJumlahPeserta, edAsalPesertaKegiatan, edLaporanAttach;
    Button btnsimpan, btnbatal;
    TextView txtJudul, txtTitle, txtMessage, txtInfoUbah;

    Spinner spinnerPelaksanaKegiatan, spinAsal;

    LinearLayout layoutJudulNoId;
    RelativeLayout layoutContainerNoId;

    PreferenceUtils prefUtils;
    Button btnYa, btnTidak;
    ImageView imgClosePopup;
    ImageUtils imageUtils;
    ArrayList<Pelaksana>pelaksanaArray= new ArrayList<>();

    String mode = "", asistensiIDIntent = "";

    //Ditambahkan pada 07 Nov 2017
    private String[] from = {"Ambil Foto", "Dari Galeri"};
    private Uri fileUri;
    private String uploadFotoKe = "";

    private LocationProvider mLocationProvider;

    // added by anta40
    LinearLayout headerInstansi, listLayoutTambahInstansi;
    ImageView imgTambahInstansi;

    ProgressDialog progressDialog;

    double longitude = 0.0;
    double latitude = 0.0;

    String tanggal, asalpeserta, jumlahPeserta, lokasiKegiatan, materi;

    String foto1 = "", foto2 = "", foto3 = "", fotoUbah1 = "", fotoUbah2 = "", fotoUbah3 = "";
    private ArrayAdapter<String> adapter;
    private RadioGroup radioSasaran;
    private RadioButton radioInstPem, radioInstSwast, radioLingPend, radioLinkMas;
    private String sasaran;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_tambah_kegiatanasistensi, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);
        imageUtils = ImageUtils.getInstance(this);
        progressDialog = new ProgressDialog(this);
        MapsActivity.locationListener = this;

        new AmbileDataPelaksanaTask().execute();

        asistensiIDIntent = getIntent().getStringExtra("id") != null ? getIntent().getStringExtra("id") : "";
        mode = getIntent().getStringExtra("mode_form") != null ? getIntent().getStringExtra("mode_form") : "tambah";

        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imageback = (ImageView) findViewById(R.id.imgBack);
        btnsimpan = (Button) findViewById(R.id.btnSimpan);
        btnbatal = (Button) findViewById(R.id.btnBatal);
        txtJudul = (TextView) findViewById(R.id.textViewjudul);
        txtInfoUbah = (TextView) findViewById(R.id.txtInfoUbah);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        spinnerPelaksanaKegiatan = (Spinner) findViewById(R.id.spinnerPelaksanaKegiatan);

        layoutJudulNoId = (LinearLayout) findViewById(R.id.layoutJudulNoId);
        layoutContainerNoId = (RelativeLayout) findViewById(R.id.layoutContainerNoId);
        edNoId = (EditText) findViewById(R.id.edNoId);

        edJenisKegiatan = (EditText) findViewById(R.id.edJenisKegiatan);
        edTanggalKegiatan = (EditText) findViewById(R.id.edTanggalKegiatan);
        edLokasiKegiatan = (EditText) findViewById(R.id.edLokasiKegiatan);
//        edAlamatLokasiKabupaten = (AutoCompleteTextView) findViewById(R.id.spinner_kabupaten);
//        edAsalPesertaKegiatan = (EditText) findViewById(R.id.edAsalPesertaKegiatan);
        edJumlahPeserta = (EditText) findViewById(R.id.edJumlahPeserta);
        edUraianSingkatMateri = (EditText) findViewById(R.id.edUraianSingkatMateri);
//        spinAsal = (Spinner) findViewById(R.id.spinAsal);

        radioSasaran = (RadioGroup) findViewById(R.id.radiosasaran);
        radioInstPem = (RadioButton) findViewById(R.id.INSTITUSI_PEMERINTAH);
        radioInstSwast = (RadioButton) findViewById(R.id.INSTITUSI_SWASTA);
        radioLingPend = (RadioButton) findViewById(R.id.LINGKUNGAN_PENDIDIKAN);
        radioLinkMas = (RadioButton) findViewById(R.id.LINGKUNGAN_MASYARAKAT);

        imgLokasiKegiatan = (ImageView) findViewById(R.id.imgLokasiKegiatan);

        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        edTanggalKegiatan.setOnClickListener(this);
        imgLokasiKegiatan.setOnClickListener(this);
        btnsimpan.setOnClickListener(this);
        btnbatal.setOnClickListener(this);
        imgTambah.setOnClickListener(this);
        imgUpload1.setOnClickListener(this);
        imgUpload2.setOnClickListener(this);
        imgUpload3.setOnClickListener(this);

        detectLocation();

        // added by anta40
        edInstansi = (EditText) findViewById(R.id.edInstansi);
        imgTambahInstansi = (ImageView) findViewById(R.id.imgTambahInstansi);
        imgTambahInstansi.setOnClickListener(this);
        headerInstansi = (LinearLayout) findViewById(R.id.headerInstansi);
        listLayoutTambahInstansi = (LinearLayout) findViewById(R.id.listLayoutTambahInstansi);

//        String[] listJekel = {"INSTITUSI_PEMERINTAH","INSTITUSI_SWASTA","LINGKUNGAN_PENDIDIKAN","LINGKUNGAN_MASYARAKAT"};
//
//        adapter = new ArrayAdapter<String>(TambahKegiatanAsistensiActivity.this,
//                R.layout.spinner_item, listJekel);
//        spinAsal.setAdapter(adapter);

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent y = new Intent(TambahKegiatanAsistensiActivity.this, KegiatanAsistensiActivity.class);
                startActivity(y);
                finish();
            }
        });

        imgToogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDrawerLayout.openDrawer(linearLayout2);

                int id = radioSasaran.getCheckedRadioButtonId();

                if (((RadioButton)findViewById(id)) == radioInstPem) {
                    Log.d("Jenis", "Jenis: INSTITUSI_PEMERINTAH");
                } else if (((RadioButton)findViewById(id)) == radioInstSwast) {
                    Log.d("Jenis", "Jenis: INSTITUSI_SWASTA");
                } else if (((RadioButton)findViewById(id)) == radioLingPend) {
                    Log.d("Jenis", "Jenis: LINGKUNGAN_PENDIDIKAN");
                } else if (((RadioButton)findViewById(id)) == radioLinkMas) {
                    Log.d("Jenis", "Jenis: LINGKUNGAN_MASYARAKAT");
                }
            }
        });

        if (mode.equals("ubah")) {
            txtJudul.setText("UBAH DATA");
            imgTambah.setVisibility(View.VISIBLE);

            layoutJudulNoId.setVisibility(View.VISIBLE);
            layoutContainerNoId.setVisibility(View.VISIBLE);

            edTanggalKegiatan.setFocusable(true);
            edTanggalKegiatan.setCursorVisible(true);
            edTanggalKegiatan.setOnClickListener(this);

            loadData();
        } else {
            txtJudul.setText("TAMBAH DATA BARU");

            edTanggalKegiatan.setText(getTodayDate());

            imgTambah.setVisibility(View.GONE);
        }
    }

    private String getValueSasaran(RadioGroup radioGroup) {
        String jenis = "";

        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.INSTITUSI_PEMERINTAH:
                jenis = "INSTITUSI_PEMERINTAH";
                break;
            case R.id.INSTITUSI_SWASTA:
                jenis = "INSTITUSI_SWASTA";
                break;
            case R.id.LINGKUNGAN_PENDIDIKAN:
                jenis = "LINGKUNGAN_PENDIDIKAN";
                break;
            case R.id.LINGKUNGAN_MASYARAKAT:
                jenis = "LINGKUNGAN_MASYARAKAT";
                break;
        }

        return jenis;
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

                new ReverseAddressTask(TambahKegiatanAsistensiActivity.this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
                    @Override
                    public void onAdrressListener(String address) {
                        if (mode.equals("tambah")) {
                            edLokasiKegiatan.setText(address);
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
                        edLokasiKegiatan.setText(address);
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
            case R.id.btnSimpan:
                if (mode.equals("tambah")) {
                    if (isEmptyField(edTanggalKegiatan)) {
                        Toast.makeText(TambahKegiatanAsistensiActivity.this,
                                "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (isEmptyField(edLokasiKegiatan)) {
                        Toast.makeText(TambahKegiatanAsistensiActivity.this,
                                "Lokasi kegiatan tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (!isEmptyField(edTanggalKegiatan) && !isEmptyField(edLokasiKegiatan)) {

                        tanggal = edTanggalKegiatan.getText().toString();
                        lokasiKegiatan = edLokasiKegiatan.getText().toString();
                        jumlahPeserta = edJumlahPeserta.getText().toString();
//                        asalpeserta = edAsalPesertaKegiatan.getText().toString();
                        materi = edUraianSingkatMateri.getText().toString();
                        sasaran = getValueSasaran(radioSasaran);

                        progressDialog.setMessage("Menyimpan data..");
                        progressDialog.show();
                        new DataSimpanUbahTask(mode).execute();
                    }
                } else if (mode.equals("ubah")) {
                    if (isEmptyField(edTanggalKegiatan)) {
                        Toast.makeText(TambahKegiatanAsistensiActivity.this,
                                "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (isEmptyField(edLokasiKegiatan)) {
                        Toast.makeText(TambahKegiatanAsistensiActivity.this,
                                "Lokasi kegiatan tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (!isEmptyField(edTanggalKegiatan) && !isEmptyField(edLokasiKegiatan)) {

                        tanggal = edTanggalKegiatan.getText().toString();
                        lokasiKegiatan = edLokasiKegiatan.getText().toString();
                        jumlahPeserta = edJumlahPeserta.getText().toString();
//                        asalpeserta = edAsalPesertaKegiatan.getText().toString();
                        materi = edUraianSingkatMateri.getText().toString();
                        sasaran = getValueSasaran(radioSasaran);

                        progressDialog.setMessage("Mengubah data..");
                        progressDialog.show();
                        new DataSimpanUbahTask(mode).execute();
                    }
                }

                break;
            case R.id.btnBatal:
                //function batal
                Intent y = new Intent(TambahKegiatanAsistensiActivity.this, KegiatanAsistensiActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.imgAdd:
                //function batal
                Intent tambah = new Intent(TambahKegiatanAsistensiActivity.this, TambahKegiatanAsistensiActivity.class);
                tambah.putExtra("mode_form", "tambah");
                startActivity(tambah);
                finish();
                break;
            case R.id.edTanggalKegiatan:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TambahKegiatanAsistensiActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        edTanggalKegiatan.setText(getNewDate(sDate));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
                break;
            case R.id.imgLokasiKegiatan:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogView = getLayoutInflater().inflate(R.layout.popup_bukamap, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialog = new AlertDialog.Builder(TambahKegiatanAsistensiActivity.this)
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

                        Intent mapIntent = new Intent(TambahKegiatanAsistensiActivity.this, MapsActivity.class);
                        startActivity(mapIntent);
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

                AlertDialog alertUpload1 = new AlertDialog.Builder(TambahKegiatanAsistensiActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahKegiatanAsistensiActivity.this);

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

                AlertDialog alertUpload2 = new AlertDialog.Builder(TambahKegiatanAsistensiActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahKegiatanAsistensiActivity.this);

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

                AlertDialog alertUpload3 = new AlertDialog.Builder(TambahKegiatanAsistensiActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahKegiatanAsistensiActivity.this);

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

            case R.id.imgTambahInstansi:
                edInstansi.setVisibility(View.GONE);
                headerInstansi.setVisibility(View.VISIBLE);

                LayoutInflater inflaterInstansi = getLayoutInflater();
                final RelativeLayout layoutInstansi = (RelativeLayout) inflaterInstansi.inflate(R.layout.item_list_instansi_2,
                        listLayoutTambahInstansi, false);

                AutoCompleteTextView edNamaInstansi = (AutoCompleteTextView) layoutInstansi.findViewById(R.id.add_nama_instansi);
                AutoCompleteTextView edJumlahPeserta = (AutoCompleteTextView) layoutInstansi.findViewById(R.id.add_jumlah_peserta);
                ImageView imgHapusInstansi = (ImageView) layoutInstansi.findViewById(R.id.imgHapusInstansi);

                imgHapusInstansi.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        listLayoutTambahInstansi.removeView(layoutInstansi);

                        if (listLayoutTambahInstansi.getChildCount() == 0){
                            edInstansi.setVisibility(View.VISIBLE);
                            headerInstansi.setVisibility(View.GONE);
                        }
                    }
                });

                listLayoutTambahInstansi.addView(layoutInstansi);
                break;

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
                    foto1 = ImageUtils.getInstance(TambahKegiatanAsistensiActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahKegiatanAsistensiActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahKegiatanAsistensiActivity.this).encodeBase64String(bmp);
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
                    foto1 = ImageUtils.getInstance(TambahKegiatanAsistensiActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahKegiatanAsistensiActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahKegiatanAsistensiActivity.this).encodeBase64String(bmp);
                }

            } else {
                Toast.makeText(this, "You haven't picked image.",
                        Toast.LENGTH_LONG).show();
            }
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
    public void handleNewLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        new ReverseAddressTask(this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
            @Override
            public void onAdrressListener(final String address) {
                if (mode.equals("tambah")) {
                    edLokasiKegiatan.setText(address);
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
                    edLokasiKegiatan.setText(address);
                }
            }).execute();
        }
    }

    class DataUploadTask extends AsyncTask<Bitmap, Integer, String> {
        private String responseServer;

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
                    .url(Configuration.baseUrl+"/api/advoasistensi/"+asistensiIDIntent)
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
headerInstansi.setVisibility(View.VISIBLE);
                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
                    JSONObject data = jsonObject.getJSONObject("data");

                    String meta_instansi = data.getString("meta_instansi");
                    try {
                        JSONArray jarr = new JSONArray(meta_instansi);
                        for (int i = 0; i < jarr.length(); i++){
                            JSONObject obj = jarr.getJSONObject(i);
                            String list_nama_instansi = obj.getString("list_nama_instansi");
                            String list_jumlah_peserta = obj.getString("list_jumlah_peserta");
                            Log.d("list_nama_instansi", list_nama_instansi);
                            Log.d("list_jumlah_peserta", list_jumlah_peserta);

                            LayoutInflater inflaterInstansi = getLayoutInflater();
                            final RelativeLayout layoutInstansi = (RelativeLayout) inflaterInstansi.inflate(R.layout.item_list_instansi_2,
                                    listLayoutTambahInstansi, false);

                            AutoCompleteTextView edNamaInstansi = (AutoCompleteTextView) layoutInstansi.findViewById(R.id.add_nama_instansi);
                            AutoCompleteTextView edJumlahPeserta = (AutoCompleteTextView) layoutInstansi.findViewById(R.id.add_jumlah_peserta);

                            edNamaInstansi.setText(list_nama_instansi);
                            edJumlahPeserta.setText(list_jumlah_peserta);

                            ImageView imgHapusInstansi = (ImageView) layoutInstansi.findViewById(R.id.imgHapusInstansi);

                            imgHapusInstansi.setOnClickListener(new View.OnClickListener(){

                                @Override
                                public void onClick(View view) {
                                    listLayoutTambahInstansi.removeView(layoutInstansi);

                                }
                            });

                            headerInstansi.setVisibility(View.VISIBLE);
                            listLayoutTambahInstansi.addView(layoutInstansi);

                        }
                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + meta_instansi + "\"");
                    }




                    edNoId.setText(!data.getString("id").equals("null") ? data.getString("id") : "");
                    edTanggalKegiatan.setText(!data.getString("tgl_pelaksanaan").equals("null") ? getNewDate(data.getString("tgl_pelaksanaan")) : "");
                    edLokasiKegiatan.setText(!data.getString("lokasi_kegiatan").equals("null") ? data.getString("lokasi_kegiatan") : "");
//                    edAsalPesertaKegiatan.setText(!data.getString("asal_peserta").equals("null") ? data.getString("asal_peserta") : "");
                    edJumlahPeserta.setText(!data.getString("jumlah_peserta").equals("null") ? data.getString("jumlah_peserta") : "");
                    edUraianSingkatMateri.setText(!data.getString("uraian_singkat").equals("null") ? data.getString("uraian_singkat") : "");
//                    spinAsal.setSelection(adapter.getPosition(!data.getString("kodesasaran").equals("null") ? data.getString("kodesasaran") : ""));

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

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahKegiatanAsistensiActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahKegiatanAsistensiActivity.this, LoginActivity.class);
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

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(5, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS);

            jumlahPeserta = LayoutUtil.getJumlahPesertaFromMetaInstansi(listLayoutTambahInstansi);
            String meta_instansi = LayoutUtil.getMetaInstansi(listLayoutTambahInstansi);
            Log.d("meta_instansi", meta_instansi);

            if (modeTask.equals("tambah")) {
                FormBody.Builder form = new FormBody.Builder()
                        .add("tgl_pelaksanaan", DateUtils.getNewDateForServer(tanggal))
                        .add("lokasi_kegiatan", lokasiKegiatan)
                        .add("koordinat", latitude+","+longitude)
//                        .add("asal_peserta", asalpeserta)
                        .add("meta_instansi", meta_instansi)
                        .add("kodesasaran", sasaran)
                        .add("jumlah_peserta", jumlahPeserta)
                        .add("idpelaksana", prefUtils.getInstansi())
                        .add("uraian_singkat", materi);

                Log.d("tgl_pelaksanaan", DateUtils.getNewDateForServer(tanggal));
                Log.d("lokasi_kegiatan", lokasiKegiatan);
                Log.d("koordinat", latitude+","+longitude);
                Log.d("lokasi_kegiatan", lokasiKegiatan);
                Log.d("meta_instansi", meta_instansi);
                Log.d("kodesasaran", sasaran);
                Log.d("jumlah_peserta", jumlahPeserta);
                Log.d("idpelaksana", prefUtils.getInstansi());
                Log.d("uraian_singkat", materi);

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
                        .url(Configuration.baseUrl+"/api/advoasistensi")
                        .headers(headers)
                        .post(requestBody)
                        .build();
            } else if (modeTask.equals("ubah")) {

                FormBody.Builder form = new FormBody.Builder()
                        .add("tgl_pelaksanaan", DateUtils.getNewDateForServer(tanggal))
                        .add("lokasi_kegiatan", lokasiKegiatan)
                        .add("koordinat", latitude+","+longitude)
//                        .add("asal_peserta", asalpeserta)
                        .add("meta_instansi", meta_instansi)
                        .add("kodesasaran", sasaran)
                        .add("jumlah_peserta", jumlahPeserta)
                        .add("idpelaksana", prefUtils.getInstansi())
                        .add("uraian_singkat", materi);

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
                        .url(Configuration.baseUrl+"/api/advoasistensi/"+asistensiIDIntent)
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
            Log.d("test", result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {

                    if (modeTask.equals("ubah")) {
                        progressDialog.dismiss();

                        Toast.makeText(TambahKegiatanAsistensiActivity.this, "Data berhasil di ubah.", Toast.LENGTH_LONG).show();
                        Intent listKasus = new Intent(TambahKegiatanAsistensiActivity.this, KegiatanAsistensiActivity.class);
                        startActivity(listKasus);
                        finish();
                    } else if (modeTask.equals("tambah")) {
                        progressDialog.dismiss();

                        Toast.makeText(TambahKegiatanAsistensiActivity.this, "Data berhasil disimpan.", Toast.LENGTH_LONG).show();
                        Intent listPemusnahan = new Intent(TambahKegiatanAsistensiActivity.this, KegiatanAsistensiActivity.class);
                        startActivity(listPemusnahan);
                        finish();

                    }
                }
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    class AmbileDataPelaksanaTask extends AsyncTask<Bitmap, Integer, String> {
        private String responseServer;

        public AmbileDataPelaksanaTask() {

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
                    .url(Configuration.baseUrl+"/api/getpropkab")
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
//                    JSONObject data = jsonObject.getJSONObject("data");

                    JSONObject dataJenis = jsonObject.getJSONObject("data");
                    JSONObject sumatera_selatan = dataJenis.getJSONObject("SUMATERA SELATAN");
                    JSONObject belitung = dataJenis.getJSONObject("KEP. BANGKA BELITUNG");
                    JSONObject gorontalo = dataJenis.getJSONObject("GORONTALO");
                    JSONObject sulawesi_barat = dataJenis.getJSONObject("SULAWESI BARAT");
                    JSONObject MALUKU = dataJenis.getJSONObject("MALUKU");
                    JSONObject MALUKU_UTARA = dataJenis.getJSONObject("MALUKU UTARA");
                    JSONObject PAPUA = dataJenis.getJSONObject("PAPUA");
                    JSONObject KALIMANTAN_TIMUR = dataJenis.getJSONObject("KALIMANTAN TIMUR");
                    JSONObject SUMATERA_UTARA = dataJenis.getJSONObject("SUMATERA UTARA");
                    JSONObject BENGKULU = dataJenis.getJSONObject("BENGKULU");
                    JSONObject LAMPUNG = dataJenis.getJSONObject("LAMPUNG");
                    JSONObject KEPULAUAN_RIAU = dataJenis.getJSONObject("KEPULAUAN RIAU");
                    JSONObject DKI_JAKARTA = dataJenis.getJSONObject("DKI JAKARTA");
                    JSONObject NUSA_TENGGARA_TIMUR = dataJenis.getJSONObject("NUSA TENGGARA TIMUR");
                    JSONObject SULAWESI_TENGAH = dataJenis.getJSONObject("SULAWESI TENGAH");
                    JSONObject SULAWESI_SELATAN = dataJenis.getJSONObject("SULAWESI SELATAN");
                    JSONObject KALIMANTAN_SELATAN = dataJenis.getJSONObject("KALIMANTAN SELATAN");
                    JSONObject KALIMANTAN_TENGAH = dataJenis.getJSONObject("KALIMANTAN TENGAH");
                    JSONObject BANTEN = dataJenis.getJSONObject("BANTEN");
                    JSONObject JAWA_BARAT = dataJenis.getJSONObject("JAWA BARAT");
                    JSONObject PAPUA_BARAT = dataJenis.getJSONObject("PAPUA BARAT");
                    JSONObject SULAWESI_UTARA = dataJenis.getJSONObject("SULAWESI UTARA");
                    JSONObject BALI = dataJenis.getJSONObject("BALI");
                    JSONObject DAERAH_YOGYAKARTA = dataJenis.getJSONObject("DAERAH IST. YOGYAKARTA");
                    JSONObject SUMATERA_BARAT = dataJenis.getJSONObject("SUMATERA BARAT");
                    JSONObject KALIMANTAN_BARAT = dataJenis.getJSONObject("KALIMANTAN BARAT");
                    JSONObject JAWA_TIMUR = dataJenis.getJSONObject("JAWA TIMUR");
                    JSONObject NTB = dataJenis.getJSONObject("NUSA TENGGARA BARAT");
                    JSONObject ACEH = dataJenis.getJSONObject("ACEH");
                    JSONObject JAWA_TENGAH = dataJenis.getJSONObject("JAWA TENGAH");
                    JSONObject RIAU = dataJenis.getJSONObject("RIAU");
                    JSONObject SULAWESI_TENGGARA = dataJenis.getJSONObject("SULAWESI TENGGARA");
                    JSONObject KALIMANTAN_UTARA = dataJenis.getJSONObject("KALIMANTAN UTARA");

                    Pelaksana pelaksana = new Pelaksana();
                    ArrayList<String> array_propv = new ArrayList<>();
                    ArrayList<String> array_kota = new ArrayList<>();
                    pelaksana.setProvinsi(sumatera_selatan.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(belitung.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(gorontalo.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(sulawesi_barat.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(MALUKU.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(MALUKU_UTARA.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(PAPUA.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(KALIMANTAN_TIMUR.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(SUMATERA_UTARA.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(BENGKULU.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(LAMPUNG.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(KEPULAUAN_RIAU.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(DKI_JAKARTA.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(NUSA_TENGGARA_TIMUR.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(SULAWESI_TENGAH.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(SULAWESI_SELATAN.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(KALIMANTAN_SELATAN.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(KALIMANTAN_TENGAH.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(BANTEN.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(JAWA_BARAT.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(PAPUA_BARAT.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(SULAWESI_UTARA.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(BALI.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(DAERAH_YOGYAKARTA.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(SUMATERA_BARAT.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(KALIMANTAN_BARAT.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(JAWA_TIMUR.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(NTB.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(ACEH.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(JAWA_TENGAH.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(SULAWESI_TENGGARA.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(KALIMANTAN_UTARA.toString().replace("{","").replace("}",""));
                    pelaksana.setProvinsi(RIAU.toString().replace("{","").replace("}",""));

                        pelaksanaArray.add(pelaksana);
                        for (int i = 0; i < pelaksanaArray.size();i++){
                            Log.d("array_prop", pelaksanaArray.get(i).getProvinsi());
                            String a = pelaksanaArray.get(i).getProvinsi();
                            String[] items = a.split(",");
                            for (String item : items)
                            {
                                Log.d("kota", item);
                                array_kota.add(item);
                            }
                        }

                        for (int i = 0; i< array_kota.size();i++){
                            Log.d("array kota", array_kota.get(i));
                        }


//                    for(int i = 0; i < pelaksanaArray.size(); i++){
//                        array_kota.add(String.valueOf(pelaksanaArray.get(i).getProvinsi().split(",")));
//                        for(int j = 0; j < array_kota.size(); j++){
//                            System.out.println(array_kota.get(j));
//                            array_propv.add(array_kota.get(j));
//                        }
//                    }
//
//                    array_kota.clear();
                        AutoCompleteTextView spinner_pelaksana = (AutoCompleteTextView) findViewById(R.id.spinner_kabupaten);
//
//                    for(int i = 0; i < array_propv.size(); i++){
//                        StringTokenizer st = new StringTokenizer(array_propv.get(i), ":");
//                        String id = st.nextToken();
//                        String kota = st.nextToken();
//                        array_kota.add(kota);
//                        Log.d("kota", kota);
//                    }
                    progressDialog.dismiss();

                        spinner_pelaksana.setAdapter(new ArrayAdapter<Pelaksana>(TambahKegiatanAsistensiActivity.this,
                                R.layout.spinner_item_small, pelaksanaArray));

                        spinner_pelaksana.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                                Log.i("", String.valueOf(arg0.getItemAtPosition(pos)));
//                                pelaksana_ = pelaksanaArray.get(pos).getId();
                            }
                        });

//                        spinner_pelaksana.setSelection(i);
//                        Log.d("spinner pelaksana", String.valueOf(spinner_pelaksana.getSelectionEnd()));


                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
//                    progressBar.setVisibility(View.GONE);

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahKegiatanAsistensiActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahKegiatanAsistensiActivity.this, LoginActivity.class);
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
        startActivity(new Intent(TambahKegiatanAsistensiActivity.this, KegiatanAsistensiActivity.class));
        finish();
    }
}

