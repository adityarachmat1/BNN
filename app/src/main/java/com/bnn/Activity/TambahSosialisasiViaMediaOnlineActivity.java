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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
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

public class TambahSosialisasiViaMediaOnlineActivity extends ActivityBase implements View.OnClickListener,  LocationProvider.LocationCallback, MapsActivity.LocationListener {

    ImageView imgToogle, imageback, imgTambah, imgBukaMap, imgUpload1, imgUpload2,
            imgUpload3, iconTitle;
    EditText edNoId, edTanggalMediaOnline, edLokasiMediaOnline, edUraianSingkatMateri, edJenisKegiatan, edJumlah;
    Button btnSimpan, btnBatal;
    TextView txtJudul, txtTitle, txtMessage, txtInfoUbah;
    private RadioGroup radioJenisMediaOnline;
    RadioButton radioMedsos, radioWeb, radioRadStream;
    CheckBox checkBoxFacebook, checkBoxTwitter, checkBoxInstagram, checkBoxPath, checkBoxSms, checkBoxBbm, checkBoxWhatsapp, checkBoxYoutube;

    PreferenceUtils prefUtils;
    ImageUtils imageUtils;
    Button btnYa, btnTidak;
    ImageView imgClosePopup;

    String mode = "", mediaonlineIDIntent = "";
    String tanggal, jenisMedia, namaMedia, materi, lokasi, jenisKegiatan, jumlah;

    //Ditambahkan pada 07 Nov 2017
    private String[] from = {"Ambil Foto", "Dari Galeri"};
    private Uri fileUri;
    private String uploadFotoKe = "";

    private LocationProvider mLocationProvider;

    ProgressDialog progressDialog;

    double longitude = 0.0;
    double latitude = 0.0;

    String foto1 = "", foto2 = "", foto3 = "", fotoUbah1 = "", fotoUbah2 = "", fotoUbah3 = "";

    //added by anta40
    ImageView imgMulaiPublish, imgSelesaiPublish;
    EditText edMulaiPublish, edSelesaiPublish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_tambah_viamediaonline, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);
        imageUtils = ImageUtils.getInstance(this);

        progressDialog = new ProgressDialog(this);
        MapsActivity.locationListener = this;

        mediaonlineIDIntent = getIntent().getStringExtra("mediaonline_id") != null ? getIntent().getStringExtra("mediaonline_id") : "";
        mode = getIntent().getStringExtra("mode_form") != null ? getIntent().getStringExtra("mode_form") : "tambah";

        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imageback = (ImageView) findViewById(R.id.imgBack);
        imgBukaMap = (ImageView) findViewById(R.id.imgLokasiKegiatan);
        btnSimpan = (Button) findViewById(R.id.btnSimpan);
        btnBatal = (Button) findViewById(R.id.btnBatal);
        txtJudul = (TextView) findViewById(R.id.textViewjudul);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        txtInfoUbah = (TextView) findViewById(R.id.txtInfoUbah);

        radioJenisMediaOnline = (RadioGroup) findViewById(R.id.radioJenisMediaOnline);
        radioMedsos = (RadioButton) findViewById(R.id.radioMedsos);
        radioWeb = (RadioButton) findViewById(R.id.radioWeb);
        radioRadStream = (RadioButton) findViewById(R.id.radioRadStream);

        checkBoxFacebook = (CheckBox) findViewById(R.id.chkFacebook);
        checkBoxTwitter = (CheckBox) findViewById(R.id.chkTwitter);
        checkBoxInstagram = (CheckBox) findViewById(R.id.chkInstagram);
        checkBoxPath = (CheckBox) findViewById(R.id.chkPath);
        checkBoxSms = (CheckBox) findViewById(R.id.chkSms);
        checkBoxBbm = (CheckBox) findViewById(R.id.chkBbm);
        checkBoxWhatsapp = (CheckBox) findViewById(R.id.chkWhatsapp);
        checkBoxYoutube = (CheckBox) findViewById(R.id.chkYoutube);

        edTanggalMediaOnline = (EditText) findViewById(R.id.edTanggalMediaOnline);
        edLokasiMediaOnline = (EditText) findViewById(R.id.edLokasiMediaOnline);
        edUraianSingkatMateri = (EditText) findViewById(R.id.edUraianSingkatMateri);
        edJenisKegiatan = (EditText) findViewById(R.id.edJenisKegiatanMediaOnline);
        edJumlah = (EditText) findViewById(R.id.edJumlahMediaOnline);

        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        btnSimpan.setOnClickListener(this);
        btnBatal.setOnClickListener(this);
        imgTambah.setOnClickListener(this);
        imgBukaMap.setOnClickListener(this);

        imgUpload1.setOnClickListener(this);
        imgUpload2.setOnClickListener(this);
        imgUpload3.setOnClickListener(this);

        // added by anta40
        imgMulaiPublish = (ImageView) findViewById(R.id.imgTglMulaiPublish);
        imgSelesaiPublish = (ImageView) findViewById(R.id.imgTglSelesaiPublish);
        imgMulaiPublish.setOnClickListener(this);
        imgSelesaiPublish.setOnClickListener(this);
        edMulaiPublish = (EditText) findViewById(R.id.edTanggalMulaiPublish);
        edSelesaiPublish = (EditText) findViewById(R.id.edTanggalSelesaiPublish);

        detectLocation();

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent y = new Intent(TambahSosialisasiViaMediaOnlineActivity.this, SosialisasiViaMediaOnlineActivity.class);
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

            edTanggalMediaOnline.setOnClickListener(this);

            loadData();
        } else {
            txtJudul.setText("TAMBAH DATA BARU");

            edTanggalMediaOnline.setText(getTodayDate());

            imgTambah.setVisibility(View.GONE);
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

    private void detectLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                new ReverseAddressTask(TambahSosialisasiViaMediaOnlineActivity.this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
                    @Override
                    public void onAdrressListener(String address) {
                        if (mode.equals("tambah")) {
                            edLokasiMediaOnline.setText(address);
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
                        edLokasiMediaOnline.setText(address);
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
                Intent addIntent = new Intent(TambahSosialisasiViaMediaOnlineActivity.this, TambahSosialisasiViaMediaOnlineActivity.class);
                startActivity(addIntent);
                finish();
                break;
            case R.id.edTanggalMediaOnline:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TambahSosialisasiViaMediaOnlineActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        edTanggalMediaOnline.setText(getNewDate(sDate));
                    }
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
                dpd.show();
                break;
            case R.id.imageViewbarangbukti:
                uploadFotoKe = "1";

                AlertDialog alertUpload1 = new AlertDialog.Builder(TambahSosialisasiViaMediaOnlineActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahSosialisasiViaMediaOnlineActivity.this);

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

                AlertDialog alertUpload2 = new AlertDialog.Builder(TambahSosialisasiViaMediaOnlineActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahSosialisasiViaMediaOnlineActivity.this);

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

                AlertDialog alertUpload3 = new AlertDialog.Builder(TambahSosialisasiViaMediaOnlineActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahSosialisasiViaMediaOnlineActivity.this);

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
            case R.id.imgLokasiKegiatan:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogView = getLayoutInflater().inflate(R.layout.popup_custom, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialog = new AlertDialog.Builder(TambahSosialisasiViaMediaOnlineActivity.this)
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

                        Intent mapIntent = new Intent(TambahSosialisasiViaMediaOnlineActivity.this, MapsActivity.class);
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
            case R.id.btnSimpan:
                if (mode.equals("tambah")) {
                    if (isEmptyField(edTanggalMediaOnline)) {
                        showToast("Tanggal tidak boleh kosong.");
                    } else if (isEmptyField(edLokasiMediaOnline)) {
                        showToast("Lokasi kegiatan tidak boleh kosong.");
                    } else if (!isEmptyField(edTanggalMediaOnline) && !isEmptyField(edLokasiMediaOnline)) {
                        tanggal = edTanggalMediaOnline.getText().toString();
                        jenisMedia = getValueJenisMedia(radioJenisMediaOnline);
                        materi = edUraianSingkatMateri.getText().toString();
                        lokasi = edLokasiMediaOnline.getText().toString();
                        jenisKegiatan = edJenisKegiatan.getText().toString();
                        jumlah = edJumlah.getText().toString();

                        ArrayList<CheckBox> checkBoxArrayList = new ArrayList<>();
                        checkBoxArrayList.add(checkBoxFacebook);
                        checkBoxArrayList.add(checkBoxTwitter);
                        checkBoxArrayList.add(checkBoxInstagram);
                        checkBoxArrayList.add(checkBoxPath);
                        checkBoxArrayList.add(checkBoxSms);
                        checkBoxArrayList.add(checkBoxBbm);
                        checkBoxArrayList.add(checkBoxWhatsapp);
                        checkBoxArrayList.add(checkBoxYoutube);

                        namaMedia = getValueNamaMedia(checkBoxArrayList);

                        Log.d("Jenis", "Jenis: "+jenisMedia);
                        Log.d("Nama", "Nama: "+namaMedia);

                        progressDialog.setMessage("Menyimpan data..");
                        progressDialog.show();

                        new DataSimpanUbahTask(mode).execute();
                    }
                } else if (mode.equals("ubah")) {
                    if (isEmptyField(edTanggalMediaOnline)) {
                        showToast("Tanggal tidak boleh kosong.");
                    } else if (isEmptyField(edLokasiMediaOnline)) {
                        showToast("Lokasi kegiatan tidak boleh kosong.");
                    } else if (!isEmptyField(edTanggalMediaOnline) && !isEmptyField(edLokasiMediaOnline)) {
                        tanggal = edTanggalMediaOnline.getText().toString();
                        jenisMedia = getValueJenisMedia(radioJenisMediaOnline);
                        materi = edUraianSingkatMateri.getText().toString();
                        lokasi = edLokasiMediaOnline.getText().toString();
                        jenisKegiatan = edJenisKegiatan.getText().toString();
                        jumlah = edJumlah.getText().toString();

                        ArrayList<CheckBox> checkBoxArrayList = new ArrayList<>();
                        checkBoxArrayList.add(checkBoxFacebook);
                        checkBoxArrayList.add(checkBoxTwitter);
                        checkBoxArrayList.add(checkBoxInstagram);
                        checkBoxArrayList.add(checkBoxPath);
                        checkBoxArrayList.add(checkBoxSms);
                        checkBoxArrayList.add(checkBoxBbm);
                        checkBoxArrayList.add(checkBoxWhatsapp);
                        checkBoxArrayList.add(checkBoxYoutube);

                        namaMedia = getValueNamaMedia(checkBoxArrayList);

                        progressDialog.setMessage("Mengubah data..");
                        progressDialog.show();

                        new DataSimpanUbahTask(mode).execute();
                    }
                }
                break;
            case R.id.btnBatal:
                //function batal
                Intent y = new Intent(TambahSosialisasiViaMediaOnlineActivity.this, SosialisasiViaMediaOnlineActivity.class);
                startActivity(y);
                finish();
                break;

            case R.id.imgTglMulaiPublish:
                Calendar cal = Calendar.getInstance();
                dpd = new DatePickerDialog(TambahSosialisasiViaMediaOnlineActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        edMulaiPublish.setText(getNewDate(sDate));
                    }
                }, cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH));
                dpd.show();
                break;

            case R.id.imgTglSelesaiPublish:
                cal = Calendar.getInstance();
                dpd = new DatePickerDialog(TambahSosialisasiViaMediaOnlineActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        edSelesaiPublish.setText(getNewDate(sDate));
                    }
                }, cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH));
                dpd.show();
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
                    foto1 = ImageUtils.getInstance(TambahSosialisasiViaMediaOnlineActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahSosialisasiViaMediaOnlineActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahSosialisasiViaMediaOnlineActivity.this).encodeBase64String(bmp);
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
                    foto1 = ImageUtils.getInstance(TambahSosialisasiViaMediaOnlineActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahSosialisasiViaMediaOnlineActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahSosialisasiViaMediaOnlineActivity.this).encodeBase64String(bmp);
                }

            } else {
                Toast.makeText(this, "You haven't picked image.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void handleNewLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        new ReverseAddressTask(this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
            @Override
            public void onAdrressListener(final String address) {
                if (mode.equals("tambah")) {
                    edLokasiMediaOnline.setText(address);
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
                    edLokasiMediaOnline.setText(address);
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
            progressDialog.setMessage("Mengambil data.");
            progressDialog.show();

            responseServer = "";
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            Log.d("Id", "id: "+mediaonlineIDIntent);

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/disemonline/"+mediaonlineIDIntent)
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

                    edTanggalMediaOnline.setText(!data.getString("tgl_pelaksanaan").equals("null") ? getNewDate(data.getString("tgl_pelaksanaan")) : "-");
                    edUraianSingkatMateri.setText(!data.getString("materi").equals("null") ? data.getString("materi") : "");
                    edLokasiMediaOnline.setText(!data.getString("lokasi").equals("null") ? data.getString("lokasi") : "");
                    setValueJenisMedia(!data.getString("jenis_media").equals("null") ? data.getString("jenis_media") : "");
                    setValueNamaMedia(!data.getString("meta_media").equals("null") ? data.getString("meta_media") : "");
                    edJenisKegiatan.setText(!data.getString("jenis_kegiatan").equals("null") ? data.getString("jenis_kegiatan") : "");
                    edJumlah.setText(!data.getString("jumlah_yang_melihat").equals("null") ? data.getString("jumlah_yang_melihat") : "");

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
                        progressDialog.dismiss();

                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahSosialisasiViaMediaOnlineActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahSosialisasiViaMediaOnlineActivity.this, LoginActivity.class);
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
                FormBody.Builder form = new FormBody.Builder().add("tgl_pelaksanaan", DateUtils.getNewDateForServer(tanggal))
                        .add("jenis_media", jenisMedia)
                        .add("meta_media", namaMedia)
                        .add("lokasi", lokasi)
                        .add("jenis_kegiatan", jenisKegiatan)
                        .add("jumlah_yang_melihat", jumlah)
                        .add("idpelaksana", prefUtils.getInstansi())
                        .add("materi", materi);

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
                        .url(Configuration.baseUrl+"/api/disemonline")
                        .headers(headers)
                        .post(requestBody)
                        .build();
            } else if (modeTask.equals("ubah")) {
                FormBody.Builder form = new FormBody.Builder().add("tgl_pelaksanaan", DateUtils.getNewDateForServer(tanggal))
                        .add("jenis_media", jenisMedia)
                        .add("meta_media", namaMedia)
                        .add("lokasi", lokasi)
                        .add("jenis_kegiatan", jenisKegiatan)
                        .add("jumlah_yang_melihat", jumlah)
                        .add("idpelaksana", prefUtils.getInstansi())
                        .add("materi", materi);

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
                        .url(Configuration.baseUrl+"/api/disemonline/"+mediaonlineIDIntent)
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

                        showToast("Data berhasil di ubah.");
                        Intent listKasus = new Intent(TambahSosialisasiViaMediaOnlineActivity.this, SosialisasiViaMediaOnlineActivity.class);
                        startActivity(listKasus);
                        finish();
                    } else if (modeTask.equals("tambah")) {
                        progressDialog.dismiss();

                        showToast("Data berhasil disimpan.");
                        Intent listPemusnahan = new Intent(TambahSosialisasiViaMediaOnlineActivity.this, SosialisasiViaMediaOnlineActivity.class);
                        startActivity(listPemusnahan);
                        finish();

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

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private String getValueJenisMedia(RadioGroup radioGroup) {
        String jenis = "";

        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radioMedsos:
                jenis = "MEDIA_SOSIAL";
                break;
            case R.id.radioWeb:
                jenis = "MEDIA_WEB";
                break;
            case R.id.radioRadStream:
                jenis = "MEDIA_RADIO_STREAMING";
                break;
        }

        return jenis;
    }

    private void setValueJenisMedia(String jenis) {
        switch (jenis) {
            case "MEDIA_SOIAL":
                radioMedsos.setChecked(true);
                break;
            case "MEDIA_WEB":
                radioWeb.setChecked(true);
                break;
            case "MEDIA_RADIO_STREAMING":
                radioRadStream.setChecked(true);
                break;
            default:
                Log.d("Do nothing", "Do Nothing");
                break;
        }
    }

    private String getValueNamaMedia(ArrayList<CheckBox> checkBoxArrayList) {
        String nama = "[";

        for (CheckBox item : checkBoxArrayList) {
            if (item.isChecked()) {
                if (item.getId() == checkBoxFacebook.getId()) {
                    nama += "MEDIA_SOSIAL_FACEBOOK,";
                }

                if (item.getId() == checkBoxTwitter.getId()) {
                    nama += "MEDIA_SOSIAL_TWITTER,";
                }

                if (item.getId() == checkBoxInstagram.getId()) {
                    nama += "MEDIA_SOSIAL_INSTAGRAM,";
                }

                if (item.getId() == checkBoxPath.getId()) {
                    nama += "MEDIA_SOSIAL_PATH,";
                }

                if (item.getId() == checkBoxSms.getId()) {
                    nama += "MEDIA_SOSIAL_SMS,";
                }

                if (item.getId() == checkBoxBbm.getId()) {
                    nama += "MEDIA_SOSIAL_BBM,";
                }

                if (item.getId() == checkBoxWhatsapp.getId()) {
                    nama += "MEDIA_SOSIAL_WHATSAPP,";
                }

                if (item.getId() == checkBoxYoutube.getId()) {
                    nama += "MEDIA_SOSIAL_YOUTUBE,";
                }
            }
        }

        if (nama.length() > 0) {
            if (nama.substring(nama.length() - 1).contains(",")) {
                nama = nama.substring(0, (nama.length() - 1));
            }
        }
        nama += "]";
        return nama;
    }

    private void setValueNamaMedia(String nama) {
        if (nama.contains(",")) {
            String[] listNama = nama.split(",");

            for (String namaMedia : listNama) {
                if (namaMedia.equalsIgnoreCase("MEDIA_SOSIAL_FACEBOOK")) {
                    checkBoxFacebook.setChecked(true);
                }

                if (namaMedia.equalsIgnoreCase("MEDIA_SOSIAL_TWITTER")) {
                    checkBoxTwitter.setChecked(true);
                }

                if (namaMedia.equalsIgnoreCase("MEDIA_SOSIAL_INSTAGRAM")) {
                    checkBoxInstagram.setChecked(true);
                }

                if (namaMedia.equalsIgnoreCase("MEDIA_SOSIAL_PATH")) {
                    checkBoxPath.setChecked(true);
                }

                if (namaMedia.equalsIgnoreCase("MEDIA_SOSIAL_SMS")) {
                    checkBoxSms.setChecked(true);
                }

                if (namaMedia.equalsIgnoreCase("MEDIA_SOSIAL_BBM")) {
                    checkBoxBbm.setChecked(true);
                }

                if (namaMedia.equalsIgnoreCase("MEDIA_SOSIAL_WHATSAPP")) {
                    checkBoxWhatsapp.setChecked(true);
                }

                if (namaMedia.equalsIgnoreCase("MEDIA_SOSIAL_YOUTUBE")) {
                    checkBoxYoutube.setChecked(true);
                }
            }
        }
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
        startActivity(new Intent(TambahSosialisasiViaMediaOnlineActivity.this, SosialisasiViaMediaOnlineActivity.class));
        finish();
    }
}

