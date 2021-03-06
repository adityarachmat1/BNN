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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bnn.R;
import com.bnn.Task.LocationProvider;
import com.bnn.Task.ReverseAddressTask;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.Configuration;
import com.bnn.Utils.DateUtils;
import com.bnn.Utils.ImageUtils;
import com.bnn.Utils.PreferenceUtils;

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

public class TambahSosialisasiViaMediaKonvensionalActivity extends ActivityBase implements View.OnClickListener, LocationProvider.LocationCallback, MapsActivity.LocationListener {
    //DiPerbaharui pada 14 Nov 2017
    private ImageView imgToogle, imageback, imgTambah, imgLokasiKegiatan, imgJumlahPeserta, imgUpload1,
            imgUpload2, imgUpload3, iconTitle;
    private EditText edNoId, edTanggalKegiatan, edLokasiKegiatan, edMateri, edSasaran,
            edJumlahPeserta, edLaporanAttach, edJenisKegiatan, edJumlah;
    private Button btnsimpan, btnbatal, btnKembaliPeserta;
    private TextView txtJudul, txtTitle, txtMessage, txtInfoUbah;
    private ScrollView layoutUbahTambahViaKonvensional;

    private Spinner spinnerPelaksanaKegiatan;

    private LinearLayout linearid;
    private RelativeLayout relativeid;
    private RadioGroup radioSasaran;

    private RadioButton radioInstPem, radioInstSwast, radioLingPend, radioLinkMas;

    private PreferenceUtils prefUtils;
    private Button btnYa, btnTidak;
    private ImageView imgClosePopup;
    private ImageUtils imageUtils;

    private String mode = "", IDIntent = "";

    //Ditambahkan pada 07 Nov 2017
    private String[] from = {"Ambil Foto", "Dari Galeri"};
    private Uri fileUri;
    private String uploadFotoKe = "";

    private LocationProvider mLocationProvider;

    private double longitude = 0.0;
    private double latitude = 0.0;

    private String tanggal,lokasi,materi,sasaran, jenisKegiatan, jumlah;

    private ProgressDialog progressDialog;

    private String foto1 = "", foto2 = "", foto3 = "", fotoUbah1 = "", fotoUbah2 = "", fotoUbah3 = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_tambah_sosialisasiviamediakonvensional, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);
        progressDialog = new ProgressDialog(this);
        imageUtils = ImageUtils.getInstance(this);

        MapsActivity.locationListener = this;

        IDIntent = getIntent().getStringExtra("id") != null ? getIntent().getStringExtra("id") : "";
        mode = getIntent().getStringExtra("mode_form") != null ? getIntent().getStringExtra("mode_form") : "tambah";

        initLayoutTambahUbahData();
        detectLocation();

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent y = new Intent(TambahSosialisasiViaMediaKonvensionalActivity.this, SosialisasiViaMediaKonvensionalActivity.class);
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

                new ReverseAddressTask(TambahSosialisasiViaMediaKonvensionalActivity.this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
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

    private void initLayoutTambahUbahData() {

        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imageback = (ImageView) findViewById(R.id.imgBack);
        btnsimpan = (Button) findViewById(R.id.btnSimpan);
        btnbatal = (Button) findViewById(R.id.btnBatal);
        txtJudul = (TextView) findViewById(R.id.textViewjudul);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        txtInfoUbah = (TextView) findViewById(R.id.txtInfoUbah);

        edNoId = (EditText) findViewById(R.id.edNoId);
        edTanggalKegiatan = (EditText) findViewById(R.id.edTanggalKegiatan);
        edLokasiKegiatan = (EditText) findViewById(R.id.edLokasiKegiatan);
        edMateri = (EditText) findViewById(R.id.edMateri);
        edJenisKegiatan = (EditText) findViewById(R.id.edJenisKegiatanMediaKonvensional);
        edJumlah = (EditText) findViewById(R.id.edJumlahMediaKonvensional);

        radioSasaran = (RadioGroup) findViewById(R.id.radiosasaran);
        radioInstPem = (RadioButton) findViewById(R.id.INSTITUSI_PEMERINTAH);
        radioInstSwast = (RadioButton) findViewById(R.id.INSTITUSI_SWASTA);
        radioLingPend = (RadioButton) findViewById(R.id.LINGKUNGAN_PENDIDIKAN);
        radioLinkMas = (RadioButton) findViewById(R.id.LINGKUNGAN_MASYARAKAT);

        imgLokasiKegiatan = (ImageView) findViewById(R.id.imgLokasiKegiatan);
        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        linearid = (LinearLayout) findViewById(R.id.llid);
        relativeid = (RelativeLayout) findViewById(R.id.rlid);
        layoutUbahTambahViaKonvensional = (ScrollView) findViewById(R.id.layoutUbahTambahviakonvensional);

        edTanggalKegiatan.setOnClickListener(this);
        imgLokasiKegiatan.setOnClickListener(this);
        btnsimpan.setOnClickListener(this);
        btnbatal.setOnClickListener(this);
        imgTambah.setOnClickListener(this);
        imgUpload1.setOnClickListener(this);
        imgUpload2.setOnClickListener(this);
        imgUpload3.setOnClickListener(this);

        if (mode.equals("ubah")) {
            txtJudul.setText("UBAH DATA");

            linearid.setVisibility(View.VISIBLE);
            relativeid.setVisibility(View.VISIBLE);

            loadData();
        } else {
            linearid.setVisibility(View.GONE);
            relativeid.setVisibility(View.GONE);

            edTanggalKegiatan.setText(getTodayDate());
            txtJudul.setText("TAMBAH DATA BARU");
        }
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
            case R.id.btnSimpan:
                if (mode.equals("tambah")) {

                    if (isEmptyField(edTanggalKegiatan)) {
                        Toast.makeText(TambahSosialisasiViaMediaKonvensionalActivity.this,
                                "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (isEmptyField(edLokasiKegiatan)) {
                        Toast.makeText(TambahSosialisasiViaMediaKonvensionalActivity.this,
                                "Lokasi tes tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (!isEmptyField(edTanggalKegiatan) && !isEmptyField(edLokasiKegiatan)) {
                        tanggal = edTanggalKegiatan.getText().toString();
                        lokasi = edLokasiKegiatan.getText().toString();
                        materi = edMateri.getText().toString();
                        sasaran = getValueSasaran(radioSasaran);
                        jenisKegiatan = edJenisKegiatan.getText().toString();
                        jumlah = edJumlah.getText().toString();

                        progressDialog.setMessage("Menyimpan data..");
                        progressDialog.show();
                        new DataSimpanUbahTask(mode).execute();
                    }

                } else if (mode.equals("ubah")) {

                    if (isEmptyField(edTanggalKegiatan)) {
                        Toast.makeText(TambahSosialisasiViaMediaKonvensionalActivity.this,
                                "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (isEmptyField(edLokasiKegiatan)) {
                        Toast.makeText(TambahSosialisasiViaMediaKonvensionalActivity.this,
                                "lokasi kasus tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (!isEmptyField(edTanggalKegiatan) && !isEmptyField(edLokasiKegiatan)) {
                        tanggal = edTanggalKegiatan.getText().toString();
                        lokasi = edLokasiKegiatan.getText().toString();
                        materi = edMateri.getText().toString();
                        sasaran = getValueSasaran(radioSasaran);
                        jenisKegiatan = edJenisKegiatan.getText().toString();
                        jumlah = edJumlah.getText().toString();

                        progressDialog.setMessage("Mengubah data..");
                        progressDialog.show();
                        new DataSimpanUbahTask(mode).execute();
                    }
                }
                break;
            case R.id.btnBatal:
                Intent batal = new Intent(TambahSosialisasiViaMediaKonvensionalActivity.this, SosialisasiViaMediaKonvensionalActivity.class);
                startActivity(batal);
                finish();
                break;
            case R.id.imgAdd:
                Intent tambah = new Intent(TambahSosialisasiViaMediaKonvensionalActivity.this, TambahSosialisasiViaMediaKonvensionalActivity.class);
                tambah.putExtra("mode_form", "tambah");
                startActivity(tambah);
                finish();
                break;
            case R.id.edTanggalKegiatan:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TambahSosialisasiViaMediaKonvensionalActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                final AlertDialog alertDialog = new AlertDialog.Builder(TambahSosialisasiViaMediaKonvensionalActivity.this)
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

                        Intent logoutIntent = new Intent(TambahSosialisasiViaMediaKonvensionalActivity.this, MapsActivity.class);
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
            case R.id.imageViewbarangbukti:
                uploadFotoKe = "1";

                AlertDialog alertUpload1 = new AlertDialog.Builder(TambahSosialisasiViaMediaKonvensionalActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahSosialisasiViaMediaKonvensionalActivity.this);

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

                AlertDialog alertUpload2 = new AlertDialog.Builder(TambahSosialisasiViaMediaKonvensionalActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahSosialisasiViaMediaKonvensionalActivity.this);

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

                AlertDialog alertUpload3 = new AlertDialog.Builder(TambahSosialisasiViaMediaKonvensionalActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahSosialisasiViaMediaKonvensionalActivity.this);

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
                    foto1 = ImageUtils.getInstance(TambahSosialisasiViaMediaKonvensionalActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahSosialisasiViaMediaKonvensionalActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahSosialisasiViaMediaKonvensionalActivity.this).encodeBase64String(bmp);
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
                    foto1 = ImageUtils.getInstance(TambahSosialisasiViaMediaKonvensionalActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahSosialisasiViaMediaKonvensionalActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahSosialisasiViaMediaKonvensionalActivity.this).encodeBase64String(bmp);
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
        private String keyKataKunci;

        public DataUploadTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseServer = "";

            progressDialog.setMessage("Mengambil data..");
            progressDialog.show();
//            progressBar.setVisibility(View.VISIBLE);
//            expandList.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            RequestBody requestBody = null;

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/disemkonven/"+IDIntent)
                    .addHeader("Authorization", "Bearer " + prefUtils.getTokenKey())
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "Content-application/x-www-form-urlencoded")
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
            progressDialog.dismiss();
            Log.d("test", result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
                    JSONObject data = jsonObject.getJSONObject("data");

                    edNoId.setText(!data.getString("id").equals("null") ? data.getString("id") : "-");
                    edTanggalKegiatan.setText(!data.getString("tgl_pelaksanaan").equals("null") ? getNewDate(data.getString("tgl_pelaksanaan")) : "-");
//                    edSasaran.setText(!data.getString("kodesasaran").equals("null") ? data.getString("kodesasaran") : "");
                    edMateri.setText(!data.getString("materi").equals("null") ? data.getString("materi") : "");
                    edLokasiKegiatan.setText(!data.getString("lokasi_kegiatan").equals("null") ? data.getString("lokasi_kegiatan") : "");
                    edJenisKegiatan.setText(!data.getString("jenis_kegiatan").equals("null") ? data.getString("jenis_kegiatan") : "");
                    edJumlah.setText(!data.getString("jumlah_peserta").equals("null") ? data.getString("jumlah_peserta") : "");

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

//                    progressBar.setVisibility(View.GONE);
//                    expandList.setVisibility(View.VISIBLE);
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
//                    progressBar.setVisibility(View.GONE);

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahSosialisasiViaMediaKonvensionalActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahSosialisasiViaMediaKonvensionalActivity.this, LoginActivity.class);
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
                        .add("tgl_pelaksanaan", DateUtils.getNewDateForServer(tanggal))
                        .add("lokasi_kegiatan", lokasi)
                        .add("materi", materi)
                        .add("kodesasaran", sasaran)
                        .add("jenis_kegiatan", jenisKegiatan)
                        .add("jumlah_peserta", jumlah)
                        .add("idpelaksana", prefUtils.getInstansi())
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
                        .url(Configuration.baseUrl+"/api/disemkonven")
                        .headers(headers)
                        .post(requestBody)
                        .build();
            } else if (modeTask.equals("ubah")) {
                FormBody.Builder form = new FormBody.Builder()
                        .add("tgl_pelaksanaan", DateUtils.getNewDateForServer(tanggal))
                        .add("lokasi_kegiatan", lokasi)
                        .add("materi", materi)
                        .add("kodesasaran", sasaran)
                        .add("jenis_kegiatan", jenisKegiatan)
                        .add("jumlah_peserta", jumlah)
                        .add("idpelaksana", prefUtils.getInstansi())
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
                        .url(Configuration.baseUrl+"/api/disemkonven/"+IDIntent)
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

                        Toast.makeText(TambahSosialisasiViaMediaKonvensionalActivity.this, "Data berhasil di ubah.", Toast.LENGTH_LONG).show();
                        Intent listKasus = new Intent(TambahSosialisasiViaMediaKonvensionalActivity.this, SosialisasiViaMediaKonvensionalActivity.class);
                        startActivity(listKasus);
                        finish();
                    } else if (modeTask.equals("tambah")) {

                        progressDialog.dismiss();

                        Toast.makeText(TambahSosialisasiViaMediaKonvensionalActivity.this, "Data berhasil disimpan.", Toast.LENGTH_LONG).show();
                        Intent listKasus = new Intent(TambahSosialisasiViaMediaKonvensionalActivity.this, SosialisasiViaMediaKonvensionalActivity.class);
                        startActivity(listKasus);
                        finish();

                    }
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahSosialisasiViaMediaKonvensionalActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahSosialisasiViaMediaKonvensionalActivity.this, LoginActivity.class);
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

    public String getNewDate1(String date){
        String newDate = "01 Jan 2016";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = simpleDateFormat.parse(date);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd MMMM yyyy");
            newDate = newFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
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

    private void loadFoto(String base64, ImageView fotoFrame) {
        if (!base64.equalsIgnoreCase("")) {
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            fotoFrame.setImageBitmap(decodedByte);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TambahSosialisasiViaMediaKonvensionalActivity.this, SosialisasiViaMediaKonvensionalActivity.class));
        finish();
    }
}

