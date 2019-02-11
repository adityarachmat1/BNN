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
import android.widget.CheckBox;
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

import com.bnn.Modal.BarangBukti;
import com.bnn.Modal.JenisBarangBukti;
import com.bnn.Modal.SatuanBarangBukti;
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

public class TambahSosialisasiViaMediaPenyiaranActivity extends ActivityBase implements View.OnClickListener, LocationProvider.LocationCallback, MapsActivity.LocationListener {

    ImageView imgToogle, imageback, imgTambah, imgBukaMap, imgUpload1, imgUpload2,
            imgUpload3, iconTitle, imgAddNamaMediaPenyiaran;
    EditText edNoId, edTanggalMediaPenyiaran, edLokasiMediaPenyiaran, edListNamaMediaPenyiaran,
            edUraianSingkatMateri, edJenisKegiatan, edJumlah;
    Button btnSimpan, btnBatal;
    TextView txtJudul, txtTitle, txtMessage, txtInfoUbah;
    private RadioGroup radioJenisMediaPenyiaran;
    RadioButton radioTelevisi, radioRadio;

    LinearLayout layoutListMediaPenyiaran;

    Spinner spinnerPelaksanaMediaPenyiaran;

    PreferenceUtils prefUtils;
    ImageUtils imageUtils;
    Button btnYa, btnTidak;
    ImageView imgClosePopup;

    String mode = "", penyiaranIDIntent = "";

    //Ditambahkan pada 07 Nov 2017
    private String[] from = {"Ambil Foto", "Dari Galeri"};
    private Uri fileUri;
    private String uploadFotoKe = "";

    private LocationProvider mLocationProvider;

    ProgressDialog progressDialog;

    double longitude = 0.0;
    double latitude = 0.0;

    String tanggal, jenisMedia, namaMedia = "", uraianSingkat, lokasi, jenisKegiatan, jumlah;

    String foto1 = "", foto2 = "", foto3 = "", fotoUbah1 = "", fotoUbah2 = "", fotoUbah3 = "";

    //added by anta40
    ImageView imgMulaiPublish, imgSelesaiPublish;
    EditText edMulaiPublish, edSelesaiPublish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_tambah_viamediapenyiaran, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);
        imageUtils = ImageUtils.getInstance(this);

        progressDialog = new ProgressDialog(this);
        MapsActivity.locationListener = this;

        penyiaranIDIntent = getIntent().getStringExtra("mediapenyiaran_id") != null ? getIntent().getStringExtra("mediapenyiaran_id") : "";
        mode = getIntent().getStringExtra("mode_form") != null ? getIntent().getStringExtra("mode_form") : "tambah";

        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imageback = (ImageView) findViewById(R.id.imgBack);
        btnSimpan = (Button) findViewById(R.id.btnSimpan);
        btnBatal = (Button) findViewById(R.id.btnBatal);
        txtJudul = (TextView) findViewById(R.id.textViewjudul);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        imgAddNamaMediaPenyiaran = (ImageView) findViewById(R.id.imgTambahNamaMediaPenyiaran);
        imgBukaMap = (ImageView) findViewById(R.id.imgLokasiKegiatan);
        radioJenisMediaPenyiaran = (RadioGroup) findViewById(R.id.radioJenisMediaPenyiaran);
        radioTelevisi = (RadioButton) findViewById(R.id.radioTelevisi);
        radioRadio = (RadioButton) findViewById(R.id.radioRadio);
        txtInfoUbah = (TextView) findViewById(R.id.txtInfoUbah);

        edNoId = (EditText) findViewById(R.id.edNoId);
        edUraianSingkatMateri = (EditText) findViewById(R.id.edUraianSingkatMateri);

        edTanggalMediaPenyiaran = (EditText) findViewById(R.id.edTanggalMediaPenyiaran);
        edListNamaMediaPenyiaran = (EditText) findViewById(R.id.edListNamaMediaPenyiaran);
        edLokasiMediaPenyiaran = (EditText) findViewById(R.id.edLokasiMediaPenyiaran);
        edJenisKegiatan = (EditText) findViewById(R.id.edJenisKegiatanMediaPenyiaran) ;
        edJumlah = (EditText) findViewById(R.id.edJumlahMediaPenyiaran) ;

//        imgUploadLaporan = (ImageView) findViewById(R.id.imgUploadLaporan);
        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        layoutListMediaPenyiaran = (LinearLayout) findViewById(R.id.layoutListNamaMediaPenyiaran);

        imgBukaMap.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);
        btnBatal.setOnClickListener(this);
        imgTambah.setOnClickListener(this);
        imgAddNamaMediaPenyiaran.setOnClickListener(this);

        imgUpload1.setOnClickListener(this);
        imgUpload2.setOnClickListener(this);
        imgUpload3.setOnClickListener(this);
//        imgUploadLaporan.setOnClickListener(this);

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
                Intent y = new Intent(TambahSosialisasiViaMediaPenyiaranActivity.this, SosialisasiViaMediaPenyiaranActivity.class);
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

            edTanggalMediaPenyiaran.setOnClickListener(this);

            loadData();
        } else {
            txtJudul.setText("TAMBAH DATA BARU");

            edTanggalMediaPenyiaran.setText(getTodayDate());

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

                new ReverseAddressTask(TambahSosialisasiViaMediaPenyiaranActivity.this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
                    @Override
                    public void onAdrressListener(String address) {
                        if (mode.equals("tambah")) {
                            edLokasiMediaPenyiaran.setText(address);
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
                        edLokasiMediaPenyiaran.setText(address);
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
                Intent addIntent = new Intent(TambahSosialisasiViaMediaPenyiaranActivity.this, TambahSosialisasiViaMediaPenyiaranActivity.class);
                startActivity(addIntent);
                finish();
                break;
            case R.id.imgLokasiKegiatan:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogView = getLayoutInflater().inflate(R.layout.popup_custom, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialog = new AlertDialog.Builder(TambahSosialisasiViaMediaPenyiaranActivity.this)
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

                        Intent mapIntent = new Intent(TambahSosialisasiViaMediaPenyiaranActivity.this, MapsActivity.class);
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
            case R.id.imgTambahNamaMediaPenyiaran:
                edListNamaMediaPenyiaran.setVisibility(View.GONE);

                LayoutInflater inflaterPanyiaran = getLayoutInflater();
                final RelativeLayout layoutPenyiaran = (RelativeLayout) inflaterPanyiaran.inflate(R.layout.item_list_mediapenyiaran, layoutListMediaPenyiaran, false);

                EditText edNamaMedia = (EditText) layoutPenyiaran.findViewById(R.id.edNamaMedia);
                ImageView imgHapus = (ImageView) layoutPenyiaran.findViewById(R.id.imgHapusMedia);

                imgHapus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        layoutListMediaPenyiaran.removeView(layoutPenyiaran);

                        if (layoutListMediaPenyiaran.getChildCount() == 0) {
                            edListNamaMediaPenyiaran.setVisibility(View.VISIBLE);
                        }
                    }
                });

                layoutListMediaPenyiaran.addView(layoutPenyiaran);
                break;
            case R.id.edTanggalMediaPenyiaran:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TambahSosialisasiViaMediaPenyiaranActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        edTanggalMediaPenyiaran.setText(getNewDate(sDate));
                    }
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
                dpd.show();
                break;
            case R.id.imageViewbarangbukti:
                uploadFotoKe = "1";

                AlertDialog alertUpload1 = new AlertDialog.Builder(TambahSosialisasiViaMediaPenyiaranActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahSosialisasiViaMediaPenyiaranActivity.this);

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

                AlertDialog alertUpload2 = new AlertDialog.Builder(TambahSosialisasiViaMediaPenyiaranActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahSosialisasiViaMediaPenyiaranActivity.this);

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

                AlertDialog alertUpload3 = new AlertDialog.Builder(TambahSosialisasiViaMediaPenyiaranActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahSosialisasiViaMediaPenyiaranActivity.this);

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
//            case R.id.imgUploadLaporan:
//                //Inflate custom layout untuk menampilkan form out-office
//                View dialogDwonload = getLayoutInflater().inflate(R.layout.popup_custom, null);
//                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
//                final AlertDialog alertDialogDownload = new AlertDialog.Builder(TambahSosialisasiViaMediaPenyiaranActivity.this)
//                        .setCancelable(false)
//                        .setView(dialogDwonload).create();
//                initDialogView(dialogDwonload);
//
//                alertDialogDownload.show();
//                //Membuat alert dialog layout menjadi responsive ketika keyboard muncul
//                alertDialogDownload.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                //Membuat background menjadi transparent
//                alertDialogDownload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//                txtTitle.setText("PILIH BERKAS");
//                iconTitle.setImageResource(R.drawable.attach_copy);
//                txtMessage.setText("Unggah berkas yang ada di ponsel anda?");
//
//                btnTidak.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialogDownload.dismiss();
//                    }
//                });
//
//                btnYa.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent logoutIntent = new Intent(TambahSosialisasiViaMediaPenyiaranActivity.this, SosialisasiViaMediaPenyiaranActivity.class);
//                        startActivity(logoutIntent);
//                        finish();
//                    }
//                });
//
//                imgClosePopup.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialogDownload.dismiss();
//                    }
//                });
//                break;
            case R.id.btnSimpan:
                if (mode.equals("tambah")) {
                    if (isEmptyField(edTanggalMediaPenyiaran)) {
                        showToast("Tanggal tidak boleh kosong.");
                    } else if (isEmptyField(edLokasiMediaPenyiaran)) {
                        showToast("Lokasi kegiatan tidak boleh kosong.");
                    } else if (!isEmptyField(edTanggalMediaPenyiaran) && !isEmptyField(edLokasiMediaPenyiaran)) {
                        tanggal = edTanggalMediaPenyiaran.getText().toString();
                        jenisMedia = getValueJenisMedia(radioJenisMediaPenyiaran);
                        uraianSingkat = edUraianSingkatMateri.getText().toString();
                        lokasi = edLokasiMediaPenyiaran.getText().toString();
                        jenisKegiatan = edJenisKegiatan.getText().toString();
                        jumlah = edJumlah.getText().toString();

                        if (layoutListMediaPenyiaran != null && layoutListMediaPenyiaran.getChildCount() > 0) {
                            int count = 0;
                            String nama = "";
                            JSONArray jsonArray = new JSONArray();

                            for (int i = 0; i < layoutListMediaPenyiaran.getChildCount(); i++) {
                                if (layoutListMediaPenyiaran.getChildAt(i) instanceof RelativeLayout) {
                                    RelativeLayout relative = (RelativeLayout) layoutListMediaPenyiaran.getChildAt(i);

                                    JSONObject jsonObject = new JSONObject();

                                    if (relative.getChildAt(0).getId() == R.id.edNamaMedia) {
                                        nama = ((EditText) relative.getChildAt(0).findViewById(R.id.edNamaMedia)).getText().toString();
                                    }

                                    try {
                                        jsonObject.put("list_media_nama", nama);
                                        jsonArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                            namaMedia = jsonArray.toString();
                        }

                        Log.d("Jenis", "Jenis: "+jenisMedia);
                        Log.d("Nama", "Nama: "+namaMedia);

                        progressDialog.setMessage("Menyimpan data..");
                        progressDialog.show();

                        new DataSimpanUbahTask(mode).execute();
                    }
                } else if (mode.equals("ubah")) {
                    if (isEmptyField(edTanggalMediaPenyiaran)) {
                        showToast("Tanggal tidak boleh kosong.");
                    } else if (isEmptyField(edLokasiMediaPenyiaran)) {
                        showToast("Lokasi kegiatan tidak boleh kosong.");
                    } else if (!isEmptyField(edTanggalMediaPenyiaran) && !isEmptyField(edLokasiMediaPenyiaran)) {
                        tanggal = edTanggalMediaPenyiaran.getText().toString();
                        jenisMedia = getValueJenisMedia(radioJenisMediaPenyiaran);
                        uraianSingkat = edUraianSingkatMateri.getText().toString();
                        lokasi = edLokasiMediaPenyiaran.getText().toString();
                        jenisKegiatan = edJenisKegiatan.getText().toString();
                        jumlah = edJumlah.getText().toString();

                        if (layoutListMediaPenyiaran != null && layoutListMediaPenyiaran.getChildCount() > 0) {
                            int count = 0;
                            String nama = "";
                            JSONArray jsonArray = new JSONArray();

                            for (int i = 0; i < layoutListMediaPenyiaran.getChildCount(); i++) {
                                if (layoutListMediaPenyiaran.getChildAt(i) instanceof RelativeLayout) {
                                    RelativeLayout relative = (RelativeLayout) layoutListMediaPenyiaran.getChildAt(i);

                                    JSONObject jsonObject = new JSONObject();

                                    if (relative.getChildAt(0).getId() == R.id.edNamaMedia) {
                                        nama = ((EditText) relative.getChildAt(0).findViewById(R.id.edNamaMedia)).getText().toString();
                                    }

                                    try {
                                        jsonObject.put("list_media_nama", nama);
                                        jsonArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            namaMedia = jsonArray.toString();
                        }

                        progressDialog.setMessage("Mengubah data..");
                        progressDialog.show();

                        new DataSimpanUbahTask(mode).execute();
                    }
                }
                break;
            case R.id.btnBatal:
                //function batal
                Intent y = new Intent(TambahSosialisasiViaMediaPenyiaranActivity.this, SosialisasiViaMediaPenyiaranActivity.class);
                startActivity(y);
                finish();
                break;

            case R.id.imgTglMulaiPublish:
                Calendar cal = Calendar.getInstance();
                dpd = new DatePickerDialog(TambahSosialisasiViaMediaPenyiaranActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                dpd = new DatePickerDialog(TambahSosialisasiViaMediaPenyiaranActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                    foto1 = ImageUtils.getInstance(TambahSosialisasiViaMediaPenyiaranActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahSosialisasiViaMediaPenyiaranActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahSosialisasiViaMediaPenyiaranActivity.this).encodeBase64String(bmp);
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
                    foto1 = ImageUtils.getInstance(TambahSosialisasiViaMediaPenyiaranActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahSosialisasiViaMediaPenyiaranActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahSosialisasiViaMediaPenyiaranActivity.this).encodeBase64String(bmp);
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
                    edLokasiMediaPenyiaran.setText(address);
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
                    edLokasiMediaPenyiaran.setText(address);
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
                    .url(Configuration.baseUrl+"/api/disempenyiaran/"+penyiaranIDIntent)
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

                    edTanggalMediaPenyiaran.setText(!data.getString("tgl_pelaksanaan").equals("null") ? getNewDate(data.getString("tgl_pelaksanaan")) : "");
                    edUraianSingkatMateri.setText(!data.getString("materi").equals("null") ? data.getString("materi") : "");
                    setValueJenisMedia(!data.getString("jenis_media").equals("null") ? data.getString("jenis_media") : "");
                    String metaMedia = !data.getString("meta_media").equals("null") ? data.getString("meta_media") : "";
                    edLokasiMediaPenyiaran.setText(!data.getString("lokasi").equals("null") ? data.getString("lokasi") : "");
                    edJenisKegiatan.setText(!data.getString("jenis_kegiatan").equals("null") ? data.getString("jenis_kegiatan") : "");
                    edJumlah.setText(!data.getString("jumlah").equals("null") ? data.getString("jumlah") : "");

                    if (!metaMedia.equals("")) {
                        try {
                            JSONArray barbukObj = new JSONArray(metaMedia);

                            if (barbukObj.length() > 0) {
                                edListNamaMediaPenyiaran.setVisibility(View.GONE);
                            } else {
                                edListNamaMediaPenyiaran.setVisibility(View.VISIBLE);
                            }

                            for (int i = 0;i < barbukObj.length();i++) {
                                LayoutInflater inflaterBarbuk= getLayoutInflater();
                                final RelativeLayout layoutMedia = (RelativeLayout) inflaterBarbuk.inflate(R.layout.item_list_mediapenyiaran, layoutListMediaPenyiaran, false);

                                final EditText edNamaMedia = (EditText) layoutMedia.findViewById(R.id.edNamaMedia);
                                ImageView imgHapus = (ImageView) layoutMedia.findViewById(R.id.imgHapusMedia);

                                edNamaMedia.setText(((JSONObject)barbukObj.get(i)).getString("list_media_nama"));

                                imgHapus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        layoutListMediaPenyiaran.removeView(layoutMedia);

                                        if (layoutListMediaPenyiaran.getChildCount() == 0) {
                                            edListNamaMediaPenyiaran.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });

                                layoutListMediaPenyiaran.addView(layoutMedia);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

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

//                    String meta = !data.getString("meta_media").equals("null") ? data.getString("meta_media") : "-";
//                    if (!meta.equals("-")) {
//                        JSONArray jsonArray = new JSONArray(meta);
//                        StringBuilder stringBuilder = new StringBuilder();
//                        String nama = "-";
//
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            stringBuilder.append(i == jsonArray.length()-1 ? ((JSONObject)jsonArray.get(i)).getString("list_media_nama") : ((JSONObject)jsonArray.get(i)).getString("list_media_nama")+"\n");
//                        }
//
//                        edListNamaMediaPenyiaran.setText(stringBuilder.toString());
//                    }

                    progressDialog.dismiss();
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    progressDialog.dismiss();

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahSosialisasiViaMediaPenyiaranActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahSosialisasiViaMediaPenyiaranActivity.this, LoginActivity.class);
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
                        .add("materi", uraianSingkat)
                        .add("jenis_media", jenisMedia)
                        .add("lokasi", lokasi)
                        .add("jenis_kegiatan", jenisKegiatan)
                        .add("jumlah", jumlah)
                        .add("idpelaksana", prefUtils.getInstansi())
                        .add("meta_media", namaMedia);

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
                        .url(Configuration.baseUrl+"/api/disempenyiaran")
                        .headers(headers)
                        .post(requestBody)
                        .build();
            } else if (modeTask.equals("ubah")) {
                FormBody.Builder form = new FormBody.Builder().add("tgl_pelaksanaan", DateUtils.getNewDateForServer(tanggal))
                        .add("materi", uraianSingkat)
                        .add("jenis_media", jenisMedia)
                        .add("lokasi", lokasi)
                        .add("jenis_kegiatan", jenisKegiatan)
                        .add("jumlah", jumlah)
                        .add("idpelaksana", prefUtils.getInstansi())
                        .add("meta_media", namaMedia);

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
                        .url(Configuration.baseUrl+"/api/disempenyiaran/"+penyiaranIDIntent)
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
                        Intent listKasus = new Intent(TambahSosialisasiViaMediaPenyiaranActivity.this, SosialisasiViaMediaPenyiaranActivity.class);
                        startActivity(listKasus);
                        finish();
                    } else if (modeTask.equals("tambah")) {
                        progressDialog.dismiss();

                        showToast("Data berhasil disimpan.");
                        Intent listPemusnahan = new Intent(TambahSosialisasiViaMediaPenyiaranActivity.this, SosialisasiViaMediaPenyiaranActivity.class);
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
            case R.id.radioTelevisi:
                jenis = "MEDIA_PENYIARAN_TV";
                break;
            case R.id.radioRadio:
                jenis = "MEDIA_PENYIARAN_RADIO";
                break;
        }

        return jenis;
    }

    private String setValueJenisMedia(String jenis) {

        switch (jenis) {
            case "MEDIA_PENYIARAN_TV":
                radioTelevisi.setChecked(true);
                break;
            case "MEDIA_PENYIARAN_RADIO":
                radioRadio.setChecked(true);
                break;
            default:
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
        startActivity(new Intent(TambahSosialisasiViaMediaPenyiaranActivity.this, SosialisasiViaMediaPenyiaranActivity.class));
        finish();
    }
}