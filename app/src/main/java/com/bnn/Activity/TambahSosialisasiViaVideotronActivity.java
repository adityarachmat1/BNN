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

public class TambahSosialisasiViaVideotronActivity extends ActivityBase implements View.OnClickListener, LocationProvider.LocationCallback, MapsActivity.LocationListener {
    //DiPerbaharui pada 14 Nov 2017
    ImageView imgToogle, imageback, imgBukaMap, imgTambahPenyidik, imgTambahPelaku, imgTambahBarangBukti, imgUpload1, imgUpload2, imgUpload3;
    EditText txttanggalpelaksaan, txtlokasipenempatan, txtjumlahbarangbukti, txtpenyidik, txtpelaku, edNoId, edNoLkn, edPelaksana;
    AutoCompleteTextView acpelaksana;
    RelativeLayout fotobarangbukti, fotopelaku, fotolokasi, layoutUbahTambahBarbuk;
    Button btnsimpan, btnbatal;
    LinearLayout listLayoutPenyidik, listLayoutPelaku;
    ScrollView layoutUbahTambahData;

    TextView txtJudul, txtInfoUbah;
    LinearLayout  layoutJudulNoId, layoutJudulNoLkn;
    RelativeLayout  layoutContainerNoId, layoutContainerNoLkn;

    //Layout variable untuk tambah ubah barbuk
    ImageView imgTambahBarbuk;
    LinearLayout listLayoutTambahBarbuk;
    Button btnSimpanBarbuk, btnBatalBarbuk;

    //Ditambahkan pada 04 Nov 2017
    PreferenceUtils prefUtils;
    Button btnYa, btnTidak;
    ImageView imgClosePopup;

    //Variable untuk menentukan jenis form ubah atau tambah
    String mode = "", eventIDIntent = "", pelaksanaIntent = "";

    //Ditambahkan pada 06 Nov 2017
    private String[] from = {"Ambil Foto", "Dari Galeri"};
    private Uri fileUri;
    private String uploadFotoKe = "";

    private LocationProvider mLocationProvider;

    double longitude = 0.0;
    double latitude = 0.0;

    private String tanggal,lokasi, pelaksana;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_tambah_viavideotron, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);
        progressDialog = new ProgressDialog(this);
        MapsActivity.locationListener = this;

        eventIDIntent = getIntent().getStringExtra("id") != null ? getIntent().getStringExtra("id") : "";
        pelaksanaIntent = getIntent().getStringExtra("nm_instansi") != null ? getIntent().getStringExtra("nm_instansi"): "";
        mode = getIntent().getStringExtra("mode_form") != null ? getIntent().getStringExtra("mode_form") : "tambah";

        initLayoutTambahUbahData();
        detectLocation();

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent y = new Intent(TambahSosialisasiViaVideotronActivity.this, SosialisasiViaVideotronActivity.class);
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

    private void detectLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                new ReverseAddressTask(TambahSosialisasiViaVideotronActivity.this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
                    @Override
                    public void onAdrressListener(String address) {
                        if (mode.equals("tambah")) {
                            txttanggalpelaksaan.setText(address);
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
                        txttanggalpelaksaan.setText(address);
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
        imgBukaMap = (ImageView) findViewById(R.id.imageViewlokasipenempatan);

        //Ditambahkan pada 05 Nov 2017
        layoutJudulNoId = (LinearLayout) findViewById(R.id.layoutJudulNoId);
        layoutContainerNoId = (RelativeLayout) findViewById(R.id.layoutContainerNoId);
        edNoId = (EditText) findViewById(R.id.edNoId);
        edPelaksana = (EditText) findViewById(R.id.edPelaksana);

        txttanggalpelaksaan = (EditText) findViewById(R.id.tanggalpelaksanaan);
        txtlokasipenempatan = (EditText) findViewById(R.id.editTextlokasipenempatan);
        fotopelaku = (RelativeLayout) findViewById(R.id.fotopelaku);
        fotolokasi = (RelativeLayout) findViewById(R.id.fotolokasi);
        txtInfoUbah = (TextView) findViewById(R.id.txtInfoUbah);

        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        btnsimpan = (Button) findViewById(R.id.buttonsimpan);
        btnbatal = (Button) findViewById(R.id.buttonbatal);
        txtJudul = (TextView) findViewById(R.id.textViewjudul);

        layoutUbahTambahData = (ScrollView) findViewById(R.id.layoutUbahTambahData);

        btnsimpan.setOnClickListener(this);
        btnbatal.setOnClickListener(this);
        imgUpload1.setOnClickListener(this);
        imgUpload2.setOnClickListener(this);
        imgUpload3.setOnClickListener(this);

        //Ditambahkan pada 04 Nov 2017
        txttanggalpelaksaan.setOnClickListener(this);
        imgBukaMap.setOnClickListener(this);

        if (mode.equals("ubah")) {
            txtJudul.setText("UBAH DATA");

            layoutJudulNoId.setVisibility(View.VISIBLE);
            layoutContainerNoId.setVisibility(View.VISIBLE);

            new DataUploadTask().execute();
        } else {
            layoutJudulNoId.setVisibility(View.GONE);
            layoutContainerNoId.setVisibility(View.GONE);

            txttanggalpelaksaan.setText(getTodayDate());
            txtJudul.setText("TAMBAH DATA BARU");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tanggalpelaksanaan:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TambahSosialisasiViaVideotronActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        txttanggalpelaksaan.setText(getNewDate(sDate));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
                break;
            case R.id.imageViewlokasipenempatan:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogView = getLayoutInflater().inflate(R.layout.popup_bukamap, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialog = new AlertDialog.Builder(TambahSosialisasiViaVideotronActivity.this)
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

                        Intent bukaMap = new Intent(TambahSosialisasiViaVideotronActivity.this, MapsActivity.class);
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
                if (mode.equals("tambah")) {

                    if (isEmptyField(txttanggalpelaksaan)) {
                        Toast.makeText(TambahSosialisasiViaVideotronActivity.this,
                                "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (isEmptyField(txtlokasipenempatan)) {
                        Toast.makeText(TambahSosialisasiViaVideotronActivity.this,
                                "Lokasi tes tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (!isEmptyField(txttanggalpelaksaan) && !isEmptyField(txtlokasipenempatan)) {

                        tanggal = txttanggalpelaksaan.getText().toString();
                        lokasi = txtlokasipenempatan.getText().toString();
                        pelaksana = edPelaksana.getText().toString();

                        progressDialog.setMessage("Menyimpan data..");
                        progressDialog.show();
                        new DataSimpanUbahTask(mode).execute();
                    }

                } else if (mode.equals("ubah")) {

                    if (isEmptyField(txttanggalpelaksaan)) {
                        Toast.makeText(TambahSosialisasiViaVideotronActivity.this,
                                "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (isEmptyField(txtlokasipenempatan)) {
                        Toast.makeText(TambahSosialisasiViaVideotronActivity.this,
                                "lokasi kasus tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                    } else if (!isEmptyField(txttanggalpelaksaan) && !isEmptyField(txtlokasipenempatan)) {
                        tanggal = txttanggalpelaksaan.getText().toString();
                        lokasi = txtlokasipenempatan.getText().toString();
                        pelaksanaIntent = edPelaksana.getText().toString();

                        progressDialog.setMessage("Mengubah data..");
                        progressDialog.show();
                        new DataSimpanUbahTask(mode).execute();
                    }
                }
                break;
            case R.id.buttonbatal:
                //function batal
                Intent y = new Intent(TambahSosialisasiViaVideotronActivity.this, SosialisasiViaVideotronActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.imageViewbarangbukti:
                uploadFotoKe = "1";

                AlertDialog alertUpload1 = new AlertDialog.Builder(TambahSosialisasiViaVideotronActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahSosialisasiViaVideotronActivity.this);

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

                AlertDialog alertUpload2 = new AlertDialog.Builder(TambahSosialisasiViaVideotronActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahSosialisasiViaVideotronActivity.this);

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

                AlertDialog alertUpload3 = new AlertDialog.Builder(TambahSosialisasiViaVideotronActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahSosialisasiViaVideotronActivity.this);

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
                    txtlokasipenempatan.setText(address);
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
                    txtlokasipenempatan.setText(address);
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

//            progressBar.setVisibility(View.VISIBLE);
//            expandList.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            RequestBody requestBody = null;

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/disemvideotron/"+eventIDIntent)
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

                    edNoId.setText(!data.getString("id").equals("null") ? data.getString("id") : "-");
                    txttanggalpelaksaan.setText(!data.getString("tanggal_pelaksanaan").equals("null") ? getNewDate1(data.getString("tanggal_pelaksanaan")) : "-");
                    txtlokasipenempatan.setText(!data.getString("lokasi_penempatan").equals("null") ? data.getString("lokasi_penempatan") : "");
                    edPelaksana.setText(pelaksanaIntent);

//                    progressBar.setVisibility(View.GONE);
//                    expandList.setVisibility(View.VISIBLE);
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
//                    progressBar.setVisibility(View.GONE);

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahSosialisasiViaVideotronActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahSosialisasiViaVideotronActivity.this, LoginActivity.class);
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
                requestBody = new FormBody.Builder()
                        .add("tanggal_pelaksanaan", DateUtils.getNewDateForServer(tanggal))
                        .add("lokasi_penempatan", lokasi)
                        .add("nm_instansi", pelaksana)
                        .add("idpelaksana", prefUtils.getInstansi())
                        .build();

                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/disemvideotron")
                        .headers(headers)
                        .post(requestBody)
                        .build();
            } else if (modeTask.equals("ubah")) {
                requestBody = new FormBody.Builder()
                        .add("tanggal_pelaksanaan", DateUtils.getNewDateForServer(tanggal))
                        .add("lokasi_penempatan", lokasi)
                        .add("nm_instansi", pelaksana)
                        .add("idpelaksana", prefUtils.getInstansi())
                        .build();

                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl+"/api/disemvideotron/"+ eventIDIntent)
                        .headers(headers)
                        .put(requestBody)
                        .build();
            }
//            else if (modeTask.equals("hapus_barbuk")) {
//
//                httpRequest = new Request.Builder()
//                        .url(Configuration.baseUrl+"/api/tesnarkoba/"+pesertaIDIntent)
//                        .headers(headers)
//                        .delete()
//                        .build();
//            }
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

                        Toast.makeText(TambahSosialisasiViaVideotronActivity.this, "Data berhasil di ubah.", Toast.LENGTH_LONG).show();
                        Intent listKasus = new Intent(TambahSosialisasiViaVideotronActivity.this, SosialisasiViaVideotronActivity.class);
                        startActivity(listKasus);
                        finish();
                    } else if (modeTask.equals("tambah")) {

                        progressDialog.dismiss();

                        Toast.makeText(TambahSosialisasiViaVideotronActivity.this, "Data berhasil disimpan.", Toast.LENGTH_LONG).show();
                        Intent listKasus = new Intent(TambahSosialisasiViaVideotronActivity.this, SosialisasiViaVideotronActivity.class);
                        startActivity(listKasus);
                        finish();

                    }
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahSosialisasiViaVideotronActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahSosialisasiViaVideotronActivity.this, LoginActivity.class);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TambahSosialisasiViaVideotronActivity.this, SosialisasiViaVideotronActivity.class));
        finish();
    }
}

