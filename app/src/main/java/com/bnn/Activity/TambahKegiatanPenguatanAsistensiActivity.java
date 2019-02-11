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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

public class TambahKegiatanPenguatanAsistensiActivity extends ActivityBase implements View.OnClickListener, LocationProvider.LocationCallback, MapsActivity.LocationListener {

    ImageView imgToogle, imageback, imgTambah, imgLokasiKegiatan, imgJumlahPeserta, imgUpload1, imgUpload2,
            imgUpload3, iconTitle;
    EditText edNoId, edJenisKegiatan, edTanggalKegiatan, edLokasiKegiatan, edUraianSingkatMateri, edInstansi, 
            edSasaran, edJumlahPeserta, edSumberBiayaKegiatan, edLaporanAttach;
    Button btnsimpan, btnbatal;
    TextView txtJudul, txtTitle, txtMessage, txtInfoUbah;

    Spinner spinnerPelaksanaKegiatan;

    PreferenceUtils prefUtils;
    Button btnYa, btnTidak;
    ImageView imgClosePopup;

    String mode = "", penguatanIDIntent = "";

    //Ditambahkan pada 07 Nov 2017
    private String[] from = {"Ambil Foto", "Dari Galeri"};
    private Uri fileUri;
    private String uploadFotoKe = "";

    private LocationProvider mLocationProvider;

    ProgressDialog progressDialog;

    double longitude = 0.0;
    double latitude = 0.0;

    String tanggal, jenis, jumlahPeserta, sumberBiaya, lokasiKegiatan, materi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_tambah_kegiatanpenguatanasistensi, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);

        progressDialog = new ProgressDialog(this);
        MapsActivity.locationListener = this;

        penguatanIDIntent = getIntent().getStringExtra("penguatan_id") != null ? getIntent().getStringExtra("penguatan_id") : "";
        mode = getIntent().getStringExtra("mode_form") != null ? getIntent().getStringExtra("mode_form") : "tambah";

        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imageback = (ImageView) findViewById(R.id.imgBack);
        btnsimpan = (Button) findViewById(R.id.btnSimpan);
        btnbatal = (Button) findViewById(R.id.btnBatal);
        txtJudul = (TextView) findViewById(R.id.textViewjudul);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        spinnerPelaksanaKegiatan = (Spinner) findViewById(R.id.spinnerPelaksanaKegiatan);
        txtInfoUbah = (TextView) findViewById(R.id.txtInfoUbah);

        edNoId = (EditText) findViewById(R.id.edNoId);
        edJenisKegiatan = (EditText) findViewById(R.id.edJenisKegiatan);
        edTanggalKegiatan = (EditText) findViewById(R.id.edTanggalKegiatan);
        edLokasiKegiatan = (EditText) findViewById(R.id.edLokasiKegiatan);
        edInstansi = (EditText) findViewById(R.id.edInstansi);
        edSasaran = (EditText) findViewById(R.id.edSasaran);
        edJumlahPeserta = (EditText) findViewById(R.id.edJumlahPeserta);
//        edSumberBiayaKegiatan = (EditText) findViewById(R.id.edSumberBiayaKegiatan);
        edLaporanAttach = (EditText) findViewById(R.id.edNoId);
        edUraianSingkatMateri = (EditText) findViewById(R.id.edUraianSingkatMateri);

        imgJumlahPeserta = (ImageView) findViewById(R.id.imgJumlahPeserta);
//        imgUploadLaporan = (ImageView) findViewById(R.id.imgUploadLaporan);
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

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent y = new Intent(TambahKegiatanPenguatanAsistensiActivity.this, KegiatanPenguatanAsistensiActivity.class);
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

            edTanggalKegiatan.setFocusable(true);
            edTanggalKegiatan.setCursorVisible(true);
            edTanggalKegiatan.setOnClickListener(this);

            new DataUploadTask().execute();
        } else {
            txtJudul.setText("TAMBAH DATA BARU");

            edTanggalKegiatan.setText(getTodayDate());

            imgTambah.setVisibility(View.GONE);
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

                new ReverseAddressTask(TambahKegiatanPenguatanAsistensiActivity.this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
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
                        Toast.makeText(TambahKegiatanPenguatanAsistensiActivity.this,
                                "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (isEmptyField(edLokasiKegiatan)) {
                        Toast.makeText(TambahKegiatanPenguatanAsistensiActivity.this,
                                "Lokasi kegiatan tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (!isEmptyField(edTanggalKegiatan) && !isEmptyField(edLokasiKegiatan)) {
                        tanggal = edTanggalKegiatan.getText().toString();
                        jenis = edJenisKegiatan.getText().toString();
                        lokasiKegiatan = edLokasiKegiatan.getText().toString();
                        jumlahPeserta = edJumlahPeserta.getText().toString();
//                        sumberBiaya = edSumberBiayaKegiatan.getText().toString();
                        materi = edUraianSingkatMateri.getText().toString();

                        progressDialog.setMessage("Menyimpan data..");
                        progressDialog.show();
                        new DataSimpanUbahTask(mode).execute();
                    }
                } else if (mode.equals("ubah")) {
                    if (isEmptyField(edTanggalKegiatan)) {
                        Toast.makeText(TambahKegiatanPenguatanAsistensiActivity.this,
                                "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (isEmptyField(edLokasiKegiatan)) {
                        Toast.makeText(TambahKegiatanPenguatanAsistensiActivity.this,
                                "Lokasi kegiatan tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (!isEmptyField(edTanggalKegiatan) && !isEmptyField(edLokasiKegiatan)) {
                        tanggal = edTanggalKegiatan.getText().toString();
                        jenis = edJenisKegiatan.getText().toString();
                        lokasiKegiatan = edLokasiKegiatan.getText().toString();
                        jumlahPeserta = edJumlahPeserta.getText().toString();
//                        sumberBiaya = edSumberBiayaKegiatan.getText().toString();
                        materi = edUraianSingkatMateri.getText().toString();

                        progressDialog.setMessage("Mengubah data..");
                        progressDialog.show();
                        new DataSimpanUbahTask(mode).execute();
                    }
                }

                break;
            case R.id.btnBatal:
                //function batal
                Intent y = new Intent(TambahKegiatanPenguatanAsistensiActivity.this, KegiatanPenguatanAsistensiActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.imgAdd:
                //function batal
                Intent tambah = new Intent(TambahKegiatanPenguatanAsistensiActivity.this, TambahKegiatanPenguatanAsistensiActivity.class);
                tambah.putExtra("mode_form", "tambah");
                startActivity(tambah);
                finish();
                break;
            case R.id.edTanggalKegiatan:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TambahKegiatanPenguatanAsistensiActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                final AlertDialog alertDialog = new AlertDialog.Builder(TambahKegiatanPenguatanAsistensiActivity.this)
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

                        Intent mapIntent = new Intent(TambahKegiatanPenguatanAsistensiActivity.this, MapsActivity.class);
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

                AlertDialog alertUpload1 = new AlertDialog.Builder(TambahKegiatanPenguatanAsistensiActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahKegiatanPenguatanAsistensiActivity.this);

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

                AlertDialog alertUpload2 = new AlertDialog.Builder(TambahKegiatanPenguatanAsistensiActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahKegiatanPenguatanAsistensiActivity.this);

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

                AlertDialog alertUpload3 = new AlertDialog.Builder(TambahKegiatanPenguatanAsistensiActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahKegiatanPenguatanAsistensiActivity.this);

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
//                final AlertDialog alertDialogDownload = new AlertDialog.Builder(TambahKegiatanPenguatanAsistensiActivity.this)
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
//                        Intent logoutIntent = new Intent(TambahKegiatanPenguatanAsistensiActivity.this, KegiatanPenguatanAsistensiActivity.class);
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

                Bitmap bmp = null;
                try {
                    bmp = ImageUtils.modifyOrientation(BitmapFactory.decodeFile(fileUri.getPath(), options), fileUri.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                if (bmp.getByteCount() < 750000) {
//                    profile_image_file = imgUtils.encodeBase64String(imgUtils.getResizedBitmap(bmp));
//                } else {
//                    profile_image_file = imgUtils.encodeBase64String(imgUtils.getResizedBitmap(bmp));
//                }

                final File img = new File(fileUri.getPath());
//                profile_image_name = img.getName();

                if (!uploadFotoKe.equals("") && uploadFotoKe.equals("1")) {
                    imgUpload1.setImageBitmap(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
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
                try {
                    bmp = ImageUtils.modifyOrientation(BitmapFactory.decodeFile(imgDecodableString), imgDecodableString);
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                if (bmp.getByteCount() < 750000) {
//                    profile_image_file = imgUtils.encodeBase64String(imgUtils.getResizedBitmap(bmp));
//                } else {
//                    profile_image_file = imgUtils.encodeBase64String(imgUtils.getResizedBitmap(bmp));
//                }

                File img = new File(imgDecodableString);
//                profile_image_name = img.getName();

                if (!uploadFotoKe.equals("") && uploadFotoKe.equals("1")) {
                    imgUpload1.setImageBitmap(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
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

//            progressBar.setVisibility(View.VISIBLE);
//            expandList.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            RequestBody requestBody = null;

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/advoasistensipenguatan/"+penguatanIDIntent)
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

                    edJenisKegiatan.setText(!data.getString("jenis_kegiatan").equals("null") ? data.getString("jenis_kegiatan") : "");
                    edTanggalKegiatan.setText(!data.getString("tgl_pelaksanaan").equals("null") ? getNewDate(data.getString("tgl_pelaksanaan")) : "");
                    edLokasiKegiatan.setText(!data.getString("lokasi_kegiatan").equals("null") ? data.getString("lokasi_kegiatan") : "");
                    edJumlahPeserta.setText(!data.getString("jumlah_peserta").equals("null") ? data.getString("jumlah_peserta") : "");
//                    edSumberBiayaKegiatan.setText(!data.getString("kodesumberanggaran").equals("null") ? data.getString("kodesumberanggaran") : "");
                    edUraianSingkatMateri.setText(!data.getString("materi").equals("null") ? data.getString("materi") : "-");
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahKegiatanPenguatanAsistensiActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahKegiatanPenguatanAsistensiActivity.this, LoginActivity.class);
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

            Log.d("Data", "Data: "+tanggal+" "+jenis+" "+jumlahPeserta+" "+lokasiKegiatan+" ");

            if (modeTask.equals("tambah")) {
                requestBody = new FormBody.Builder().add("tgl_pelaksanaan", DateUtils.getNewDateForServer(tanggal))
                        .add("jenis_kegiatan", jenis)
                        .add("jumlah_peserta", jumlahPeserta)
                        .add("lokasi_kegiatan", lokasiKegiatan)
                        .add("materi", materi)
                        .add("idpelaksana", prefUtils.getInstansi())
                        .build();

                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/advoasistensipenguatan")
                        .headers(headers)
                        .post(requestBody)
                        .build();
            } else if (modeTask.equals("ubah")) {
                requestBody = new FormBody.Builder().add("tgl_pelaksanaan", DateUtils.getNewDateForServer(tanggal))
                        .add("jenis_kegiatan", jenis)
                        .add("jumlah_peserta", jumlahPeserta)
                        .add("materi", materi)
                        .add("idpelaksana", prefUtils.getInstansi())
                        .build();

                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/advoasistensipenguatan/"+penguatanIDIntent)
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

                        Toast.makeText(TambahKegiatanPenguatanAsistensiActivity.this, "Data berhasil di ubah.", Toast.LENGTH_LONG).show();
                        Intent listKasus = new Intent(TambahKegiatanPenguatanAsistensiActivity.this, KegiatanPenguatanAsistensiActivity.class);
                        startActivity(listKasus);
                        finish();
                    } else if (modeTask.equals("tambah")) {
                        progressDialog.dismiss();

                        Toast.makeText(TambahKegiatanPenguatanAsistensiActivity.this, "Data berhasil disimpan.", Toast.LENGTH_LONG).show();
                        Intent listPemusnahan = new Intent(TambahKegiatanPenguatanAsistensiActivity.this, KegiatanPenguatanAsistensiActivity.class);
                        startActivity(listPemusnahan);
                        finish();

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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TambahKegiatanPenguatanAsistensiActivity.this, KegiatanPenguatanAsistensiActivity.class));
        finish();
    }
}

