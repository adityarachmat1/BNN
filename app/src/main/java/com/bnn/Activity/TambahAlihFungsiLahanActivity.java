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

import com.bnn.R;
import com.bnn.Task.LocationProvider;
import com.bnn.Task.ReverseAddressTask;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.Configuration;
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

public class TambahAlihFungsiLahanActivity extends ActivityBase implements View.OnClickListener, LocationProvider.LocationCallback, MapsActivity.LocationListener{
    //DiPerbaharui pada 14 Nov 2017
    ImageView imgToogle, imageback, imgBukaMap, imgTambahPenyidik, imgTambahPelaku, imgTambahBarangBukti, imgUpload1, imgUpload2, imgUpload3;
    ImageView imgViewLahan;
    EditText txttanggalpeninjauan, txtlokasipeninjauan, txtluasarea, txtstatuskepemilikantanah, txtketeranganlainnya, txtjumlahbarangbukti, txtpenyidik, txtpelaku, edNoId, edNoLkn, edPelaksana;
    AutoCompleteTextView acpelaksana;
    RelativeLayout fotobarangbukti, fotopelaku, fotolokasi, layoutUbahTambahBarbuk;
    Button btnsimpan, btnbatal;
    LinearLayout listLayoutPenyidik, listLayoutPelaku;
    ScrollView layoutUbahTambahData;

    TextView txtJudul, txtInfoUbah;
    LinearLayout  layoutJudulNoId;
    RelativeLayout  layoutContainerNoId;

    //Ditambahkan pada 04 Nov 2017
    PreferenceUtils prefUtils;
    Button btnYa, btnTidak;
    ImageView imgClosePopup;
    ImageUtils imageUtils;

    JSONArray jarr_meta_tanah, meta_kode_penyelenggara, meta_kode_komoditi;
    //Variable untuk menentukan jenis form ubah atau tambah
    String mode = "", lahanIDIntent = "", pelaksanaIntent = "";

    //Ditambahkan pada 06 Nov 2017
    private String[] from = {"Ambil Foto", "Dari Galeri"};
    private Uri fileUri;
    private String uploadFotoKe = "";

    private LocationProvider mLocationProvider;

    double longitude = 0.0;
    double latitude = 0.0;

    //Variable for input tesnarkoba
    private String tanggal,lokasi,meta_lokasi_lahan,keteranganlainnya, meta_kode_penyelenggara_, meta_kode_komoditi_;

    ProgressDialog progressDialog;

    String foto1 = "", foto2 = "", foto3 = "", fotoUbah1 = "", fotoUbah2 = "", fotoUbah3 = "";

    //added by anta40
//    LinearLayout headerLahan;
//    LinearLayout listLayoutTambahLahan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_tambah_alihfungsilahan, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);
        progressDialog = new ProgressDialog(this);
        MapsActivity.locationListener = this;
        imageUtils = ImageUtils.getInstance(this);

        lahanIDIntent = getIntent().getStringExtra("id") != null ? getIntent().getStringExtra("id") : "";
        pelaksanaIntent = getIntent().getStringExtra("pelaksana") != null ? getIntent().getStringExtra("pelaksana"): "";
        mode = getIntent().getStringExtra("mode_form") != null ? getIntent().getStringExtra("mode_form") : "tambah";

        initLayoutTambahUbahData();
        detectLocation();

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent y = new Intent(TambahAlihFungsiLahanActivity.this, AlihFungsiLahanActivity.class);
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

                new ReverseAddressTask(TambahAlihFungsiLahanActivity.this, latitude, longitude, new ReverseAddressTask.ReverseAddress() {
                    @Override
                    public void onAdrressListener(String address) {
                        if (mode.equals("tambah")) {
                            txtlokasipeninjauan.setText(address);
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
                        txtlokasipeninjauan.setText(address);
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
        imgBukaMap = (ImageView) findViewById(R.id.imageViewgpslokaspeninjauan);

        //Ditambahkan pada 05 Nov 2017
        layoutJudulNoId = (LinearLayout) findViewById(R.id.layoutJudulNoId);
        layoutContainerNoId = (RelativeLayout) findViewById(R.id.layoutContainerNoId);
        edNoId = (EditText) findViewById(R.id.edNoId);

        txttanggalpeninjauan = (EditText) findViewById(R.id.tanggalpeninjauan);
        txtlokasipeninjauan = (EditText) findViewById(R.id.editTextlokasipeninjauan);
        edPelaksana = (EditText) findViewById(R.id.edPelaksana);

//        if (mode.equals("ubah")){
//            edPelaksana.setEnabled(false);
//        }else {
//
//        }


        txtluasarea = (EditText) findViewById(R.id.etTextluasarea);
        txtketeranganlainnya = (EditText) findViewById(R.id.edKeteranganLainnya);
        fotobarangbukti = (RelativeLayout) findViewById(R.id.fotobarangbukti);
        fotopelaku = (RelativeLayout) findViewById(R.id.fotopelaku);
        fotolokasi = (RelativeLayout) findViewById(R.id.fotolokasi);

        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        btnsimpan = (Button) findViewById(R.id.buttonsimpan);
        btnbatal = (Button) findViewById(R.id.buttonbatal);
        txtJudul = (TextView) findViewById(R.id.textViewjudul);
        txtInfoUbah = (TextView)findViewById(R.id.txtInfoUbah);

        btnsimpan.setOnClickListener(this);
        btnbatal.setOnClickListener(this);
        imgUpload1.setOnClickListener(this);
        imgUpload2.setOnClickListener(this);
        imgUpload3.setOnClickListener(this);

        //added by anta40
//        headerLahan = (LinearLayout) findViewById(R.id.headerLahan);
//        imgViewLahan = (ImageView) findViewById(R.id.imgViewLahan);
//        imgViewLahan.setOnClickListener(this);
//        listLayoutTambahLahan = (LinearLayout) findViewById(R.id.listLayoutTambahLahan);

        //Ditambahkan pada 04 Nov 2017
        txttanggalpeninjauan.setOnClickListener(this);
        imgBukaMap.setOnClickListener(this);

        if (mode.equals("ubah")) {
            txtJudul.setText("UBAH DATA");

            layoutJudulNoId.setVisibility(View.VISIBLE);
            layoutContainerNoId.setVisibility(View.VISIBLE);

            loadData();
        } else {
            layoutJudulNoId.setVisibility(View.GONE);
            layoutContainerNoId.setVisibility(View.GONE);

            txttanggalpeninjauan.setText(getTodayDate());
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
            case R.id.tanggalpeninjauan:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TambahAlihFungsiLahanActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        txttanggalpeninjauan.setText(getNewDate(sDate));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
                break;
            case R.id.imageViewgpslokaspeninjauan:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogView = getLayoutInflater().inflate(R.layout.popup_bukamap, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialog = new AlertDialog.Builder(TambahAlihFungsiLahanActivity.this)
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

                        Intent mapIntent = new Intent(TambahAlihFungsiLahanActivity.this, MapsActivity.class);
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
            case R.id.buttonsimpan:
                if (Configuration.isDeviceOnline(TambahAlihFungsiLahanActivity.this)) {
                    if (mode.equals("tambah")) {

                        if (isEmptyField(txttanggalpeninjauan)) {
                            Toast.makeText(TambahAlihFungsiLahanActivity.this,
                                    "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (isEmptyField(txtlokasipeninjauan)) {
                            Toast.makeText(TambahAlihFungsiLahanActivity.this,
                                    "Lokasi tes tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (!isEmptyField(txttanggalpeninjauan) && !isEmptyField(txtlokasipeninjauan)) {

                            tanggal = txttanggalpeninjauan.getText().toString();
                            lokasi = txtlokasipeninjauan.getText().toString();
//                            luaslahan = txtluasarea.getText().toString();

                            meta();

                            keteranganlainnya = txtketeranganlainnya.getText().toString();

                            progressDialog.setMessage("Menyimpan data..");
                            progressDialog.show();
                            new DataSimpanUbahTask(mode).execute();
                        }

                    } else if (mode.equals("ubah")) {

                        if (isEmptyField(txttanggalpeninjauan)) {
                            Toast.makeText(TambahAlihFungsiLahanActivity.this,
                                    "Tanggal tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (isEmptyField(txtlokasipeninjauan)) {
                            Toast.makeText(TambahAlihFungsiLahanActivity.this,
                                    "lokasi kasus tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                        } else if (!isEmptyField(txttanggalpeninjauan) && !isEmptyField(txtlokasipeninjauan)) {
                            tanggal = txttanggalpeninjauan.getText().toString();
                            lokasi = txtlokasipeninjauan.getText().toString();
//                            luaslahan = txtluasarea.getText().toString();

                            meta();

                            keteranganlainnya = txtketeranganlainnya.getText().toString();

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
                Intent y = new Intent(TambahAlihFungsiLahanActivity.this, AlihFungsiLahanActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.imageViewbarangbukti:
                uploadFotoKe = "1";

                AlertDialog alertUpload1 = new AlertDialog.Builder(TambahAlihFungsiLahanActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahAlihFungsiLahanActivity.this);

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

                AlertDialog alertUpload2 = new AlertDialog.Builder(TambahAlihFungsiLahanActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahAlihFungsiLahanActivity.this);

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

                AlertDialog alertUpload3 = new AlertDialog.Builder(TambahAlihFungsiLahanActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahAlihFungsiLahanActivity.this);

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

//            case R.id.imgViewLahan:
//                headerLahan.setVisibility(View.VISIBLE);
//                LayoutInflater inflaterLahan = getLayoutInflater();
//
//                final RelativeLayout layoutLahan = (RelativeLayout) inflaterLahan.inflate(R.layout.item_list_lahan, listLayoutTambahLahan, false);
//
//                final AutoCompleteTextView spinnerKabupaten = (AutoCompleteTextView) layoutLahan.findViewById(R.id.spinnerKabupaten);
//                ImageView imgHapus = (ImageView) layoutLahan.findViewById(R.id.imgHapusBarbuk);
//
//                imgHapus.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (mode.equals("ubah")) {
//                            int ind = ((LinearLayout)((RelativeLayout)((LinearLayout)spinnerKabupaten.getParent()).getParent()).getParent()).indexOfChild(((RelativeLayout)((LinearLayout)spinnerKabupaten.getParent()).getParent()));
//                        }
//
//                        listLayoutTambahLahan.removeView(layoutLahan);
//
//                        if (listLayoutTambahLahan.getChildCount() == 0) {
//                            txtjumlahbarangbukti.setVisibility(View.VISIBLE);
//                            headerLahan.setVisibility(View.GONE);
//                        }
//                    }
//                });
//
//                listLayoutTambahLahan.addView(layoutLahan);
//                break;
        }
    }

    private void meta() {
        jarr_meta_tanah = new JSONArray();

        JSONObject jso = new JSONObject();
        try {
            jso.put("lokasilahan_idkabkota", "");
            jso.put("luas_lahan", txtluasarea.getText().toString());
            jso.put("kodestatustanah", "");
            jarr_meta_tanah.put(jso);
        }
        catch (JSONException je){

        }

        meta_lokasi_lahan = jarr_meta_tanah.toString();
        Log.d("meta_lokasi_lahan", jarr_meta_tanah.toString());

//        ==========================================

        meta_kode_penyelenggara = new JSONArray();
        meta_kode_penyelenggara.put("PENYELENGGARA_BNNP");
        meta_kode_penyelenggara.put("PENYELENGGARA_BNN");
        meta_kode_penyelenggara.put("PENYELENGGARA_BNNK");
        meta_kode_penyelenggara.put("PENYELENGGARA_PEMDA");
        meta_kode_penyelenggara.put("PENYELENGGARA_POLRI");
        meta_kode_penyelenggara.put("PENYELENGGARA_BUMN");
        meta_kode_penyelenggara.put("PENYELENGGARA_SWASTA");
        meta_kode_penyelenggara.put("PENYELENGGARA_LSM");
        meta_kode_penyelenggara.put("PENYELENGGARA_KEMENTRIAN_LEMBAGA");

        meta_kode_penyelenggara_ = meta_kode_penyelenggara.toString();
        Log.d("meta_kode_penyelenggara", meta_kode_penyelenggara.toString());

        //=============================================

        meta_kode_komoditi = new JSONArray();
        meta_kode_komoditi.put("Cabe");
        meta_kode_komoditi.put("Kunyit");
        meta_kode_komoditi.put("Jabon");
        meta_kode_komoditi.put("Coklat");
        meta_kode_komoditi.put("Jagung");
        meta_kode_komoditi.put("Kedelai");

        meta_kode_komoditi_ = meta_kode_komoditi.toString();
        Log.d("meta_kode_penyelenggara", meta_kode_komoditi.toString());
    }

    //Inflate setiap components ui yang akan kita implement di alert dialog
    private void initDialogView(View dialogView) {
        btnTidak = (Button) dialogView.findViewById(R.id.btnTidak);
        btnYa = (Button) dialogView.findViewById(R.id.btnYa);
        imgClosePopup = (ImageView) dialogView.findViewById(R.id.imgClosePopup);
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
                    foto1 = ImageUtils.getInstance(TambahAlihFungsiLahanActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahAlihFungsiLahanActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahAlihFungsiLahanActivity.this).encodeBase64String(bmp);
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
                    foto1 = ImageUtils.getInstance(TambahAlihFungsiLahanActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("2")) {
                    imgUpload2.setImageBitmap(bmp);
                    foto2 = ImageUtils.getInstance(TambahAlihFungsiLahanActivity.this).encodeBase64String(bmp);
                } else if (!uploadFotoKe.equals("") && uploadFotoKe.equals("3")) {
                    imgUpload3.setImageBitmap(bmp);
                    foto3 = ImageUtils.getInstance(TambahAlihFungsiLahanActivity.this).encodeBase64String(bmp);
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
                    txtlokasipeninjauan.setText(address);
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
                    txtlokasipeninjauan.setText(address);
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
                    .url(Configuration.baseUrl+"/api/altdevlahan/"+lahanIDIntent)
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

                    String meta_lokasi_lahan = data.getString("meta_lokasi_lahan");
                        Log.d("meta_lokasi_lahan", meta_lokasi_lahan);

                        try {
                            JSONArray jarr = new JSONArray(meta_lokasi_lahan);
                            for (int i = 0; i < jarr.length(); i++){
                                JSONObject obj = jarr.getJSONObject(i);
                                String luas_lahan = obj.getString("luas_lahan");
                                Log.d("My App", luas_lahan);
                                txtluasarea.setText(!obj.getString("luas_lahan").equals("null") ? obj.getString("luas_lahan") : "");
                            }
                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + meta_lokasi_lahan + "\"");
                        }

                    edNoId.setText(!data.getString("id").equals("null") ? data.getString("id") : "");
                    txttanggalpeninjauan.setText(!data.getString("tgl_kegiatan").equals("null") ? getNewDate(data.getString("tgl_kegiatan")) : "");
                    txtlokasipeninjauan.setText(!data.getString("lokasi").equals("null") ? data.getString("lokasi") : "");
                    edPelaksana.setText(!data.getString("idpelaksana").equals("null") ? data.getString("idpelaksana") : "");
                    txtketeranganlainnya.setText(!data.getString("keterangan_lainnya").equals("null") ? data.getString("keterangan_lainnya") : "");

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

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahAlihFungsiLahanActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahAlihFungsiLahanActivity.this, LoginActivity.class);
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
                            .add("tgl_kegiatan", getNewDateForServer(tanggal))
                            .add("meta_lokasi_lahan", meta_lokasi_lahan)
//                            .add("meta_kode_penyelenggara", meta_kode_penyelenggara_)
//                            .add("meta_kode_komoditi", meta_kode_komoditi_)
                            .add("lokasi", lokasi)
                            .add("keterangan_lainnya", keteranganlainnya)
                            .add("idpelaksana", edPelaksana.getText().toString())
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
                            .url(Configuration.baseUrl+"/api/altdevlahan")
                            .headers(headers)
                            .post(requestBody)
                            .build();

            } else if (modeTask.equals("ubah")) {
                    FormBody.Builder form = new FormBody.Builder()
                            .add("tgl_kegiatan", getNewDateForServer(tanggal))
                            .add("meta_lokasi_lahan", meta_lokasi_lahan)
//                            .add("meta_kode_penyelenggara", meta_kode_penyelenggara_)
//                            .add("meta_kode_komoditi", meta_kode_komoditi_)
                            .add("lokasi", lokasi)
                            .add("keterangan_lainnya", keteranganlainnya)
                            .add("idpelaksana", edPelaksana.getText().toString())
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
                        .url(Configuration.baseUrl+"/api/altdevlahan/"+lahanIDIntent)
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

                        Toast.makeText(TambahAlihFungsiLahanActivity.this, "Data berhasil di ubah.", Toast.LENGTH_LONG).show();
                        Intent listKasus = new Intent(TambahAlihFungsiLahanActivity.this, AlihFungsiLahanActivity.class);
                        startActivity(listKasus);
                        finish();
                    } else if (modeTask.equals("tambah")) {

                        progressDialog.dismiss();

                        Toast.makeText(TambahAlihFungsiLahanActivity.this, "Data berhasil disimpan.", Toast.LENGTH_LONG).show();
                        Intent listKasus = new Intent(TambahAlihFungsiLahanActivity.this, AlihFungsiLahanActivity.class);
                        startActivity(listKasus);
                        finish();

                    }
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    progressDialog.dismiss();

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahAlihFungsiLahanActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahAlihFungsiLahanActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(TambahAlihFungsiLahanActivity.this, "Terjadi kesalahan, silahkan coba lagi.", Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(TambahAlihFungsiLahanActivity.this, AlihFungsiLahanActivity.class));
        finish();
    }
}

