package com.bnn.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.Configuration;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by USER on 11/1/2017.
 */

public class TambahTATActivity extends ActivityBase implements View.OnClickListener {

    ImageView imgToogle, imageback, imgBukaMap, imgTambahPenyidik, imgTambahPelaku, imgTambahBarangBukti, imgUpload1, imgUpload2, imgUpload3;
    EditText txttanggalasesmen, txtnik, txttempat, txttanggallahir, txthasilasesmen, edNoId, txtasalpengiriman, edPelaksana;
    AutoCompleteTextView acpelaksana;
    RelativeLayout fotobarangbukti, fotopelaku, fotolokasi, layoutUbahTambahBarbuk;
    Button btnsimpan, btnbatal;
    LinearLayout listLayoutPenyidik, listLayoutPelaku;
    ScrollView layoutUbahTambahData;
    Spinner spjeniskelamin;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_tambah_tat, linearLayout);

        prefUtils = PreferenceUtils.getInstance(this);

//        eventIDIntent = getIntent().getStringExtra("event_id") != null ? getIntent().getStringExtra("event_id") : "";
//        pelaksanaIntent = getIntent().getStringExtra("pelaksana") != null ? getIntent().getStringExtra("pelaksana"): "";
        mode = getIntent().getStringExtra("mode_form") != null ? getIntent().getStringExtra("mode_form") : "tambah";

        initLayoutTambahUbahData();

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent y = new Intent(TambahTATActivity.this, PenangananKasusNarkobaActivity.class);
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

    private void initLayoutTambahUbahData() {

        //Init layout tambah ubah data baru
        imgToogle = (ImageView) findViewById(R.id.imgToogleMenu);
        imageback = (ImageView) findViewById(R.id.imageback);

        //Ditambahkan pada 05 Nov 2017
        layoutJudulNoId = (LinearLayout) findViewById(R.id.layoutJudulNoId);
        layoutContainerNoId = (RelativeLayout) findViewById(R.id.layoutContainerNoId);
        edNoId = (EditText) findViewById(R.id.edNoId);
        edPelaksana = (EditText) findViewById(R.id.edPelaksana);

        txttanggalasesmen = (EditText) findViewById(R.id.tanggalasesmen);
        txtnik = (EditText) findViewById(R.id.edNIK);
        txttempat = (EditText) findViewById(R.id.edTempat);
        txttanggallahir = (EditText) findViewById(R.id.edTanggallahir);
        txtasalpengiriman = (EditText) findViewById(R.id.edAsalPengiriman);
        txthasilasesmen = (EditText) findViewById(R.id.edHasilAsesmen);
        spjeniskelamin = (Spinner) findViewById(R.id.spKelamin);
        txtInfoUbah = (TextView) findViewById(R.id.txtInfoUbah);

        fotobarangbukti = (RelativeLayout) findViewById(R.id.fotobarangbukti);
        fotopelaku = (RelativeLayout) findViewById(R.id.fotopelaku);
        fotolokasi = (RelativeLayout) findViewById(R.id.fotolokasi);

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
        txttanggallahir.setOnClickListener(this);
        txttanggalasesmen.setOnClickListener(this);

        if (mode.equals("ubah")) {
            txtJudul.setText("UBAH DATA");

            layoutJudulNoId.setVisibility(View.VISIBLE);
            layoutContainerNoId.setVisibility(View.VISIBLE);

//            new DataUploadTask().execute();
        } else {
            layoutJudulNoId.setVisibility(View.GONE);
            layoutContainerNoId.setVisibility(View.GONE);

            txtJudul.setText("TAMBAH DATA BARU");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tanggalasesmen:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(TambahTATActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        txttanggalasesmen.setText(getNewDate(sDate));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
                break;
            case R.id.edTanggallahir:
                Calendar calendar1 = Calendar.getInstance();
                DatePickerDialog dpd1 = new DatePickerDialog(TambahTATActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String sDate = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        txttanggallahir.setText(getNewDate(sDate));
                    }
                }, calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
                dpd1.show();
                break;
            case R.id.imageViewgpstkpkasus:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogView = getLayoutInflater().inflate(R.layout.popup_bukamap, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialog = new AlertDialog.Builder(TambahTATActivity.this)
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
                        Intent logoutIntent = new Intent(TambahTATActivity.this, LoginActivity.class);
                        startActivity(logoutIntent);
                        finish();
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

                break;
            case R.id.buttonbatal:
                //function batal
                Intent y = new Intent(TambahTATActivity.this, PenangananKasusNarkobaActivity.class);
                startActivity(y);
                finish();
                break;
            case R.id.imgViewbarangbukti:
                layoutUbahTambahData.setVisibility(View.GONE);
                layoutUbahTambahBarbuk.setVisibility(View.VISIBLE);
                break;
            case R.id.imgTambahBarbuk:
                Log.d("Clicker", "Clicker");

                LayoutInflater inflaterBarbuk= getLayoutInflater();
                final RelativeLayout layoutBarbuk = (RelativeLayout) inflaterBarbuk.inflate(R.layout.item_list_barbuk, listLayoutTambahBarbuk, false);

                AutoCompleteTextView spinnerJenis = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerJenis);
                AutoCompleteTextView spinnerJumlah = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerJumlah);
                AutoCompleteTextView spinnerSatuan = (AutoCompleteTextView) layoutBarbuk.findViewById(R.id.spinnerSatuan);
                ImageView imgHapus = (ImageView) layoutBarbuk.findViewById(R.id.imgHapusBarbuk);

                String[] listJenis = {"", "SHABU", "KOKAIN", "ALKOHOL", "PUTAU"};
                String[] listJumlah = {"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                String[] listSatuan = {"", "gr", "kg", "ton", "ml", "ltr"};

                spinnerJenis.setAdapter(new ArrayAdapter<String>(TambahTATActivity.this,
                        R.layout.spinner_item_small, listJenis));
                spinnerJumlah.setAdapter(new ArrayAdapter<String>(TambahTATActivity.this,
                        R.layout.spinner_item_small, listJumlah));
                spinnerSatuan.setAdapter(new ArrayAdapter<String>(TambahTATActivity.this,
                        R.layout.spinner_item_small, listSatuan));

                imgHapus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listLayoutTambahBarbuk.removeView(layoutBarbuk);
                    }
                });

                listLayoutTambahBarbuk.addView(layoutBarbuk);
                break;
            case R.id.imageViewbarangbukti:
                uploadFotoKe = "1";

                AlertDialog alertUpload1 = new AlertDialog.Builder(TambahTATActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahTATActivity.this);

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

                AlertDialog alertUpload2 = new AlertDialog.Builder(TambahTATActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahTATActivity.this);

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

                AlertDialog alertUpload3 = new AlertDialog.Builder(TambahTATActivity.this)
                        .setTitle("Pilih Gambar")
                        .setItems(from, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                switch (pos) {
                                    case 0:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        fileUri = ImageUtils.getOutputMediaFileUri(TambahTATActivity.this);

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
                    .url(Configuration.baseUrl+"/api/kasus/"+eventIDIntent)
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

//                    edNoId.setText(!data.getString("kasus_id").equals("null") ? data.getString("kasus_id") : "-");
//                    edNoLkn.setText(!data.getString("kasus_no").equals("null") ? data.getString("kasus_no") : "-");
//                    txttanggalkejadian.setText(!data.getString("kasus_tanggal").equals("null") ? getNewDate1(data.getString("kasus_tanggal")) : "-");
//                    txttkpkasus.setText(!data.getString("kasus_tkp").equals("null") ? data.getString("kasus_tkp") : "");
//                    txtjumlahbarangbukti.setText("0       Barang");
//                    edPelaksana.setText(pelaksanaIntent);
//                    txtpenyidik.setText("-");
//                    txtpelaku.setText("-");

//                    progressBar.setVisibility(View.GONE);
//                    expandList.setVisibility(View.VISIBLE);
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
//                    progressBar.setVisibility(View.GONE);

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TambahTATActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(TambahTATActivity.this, LoginActivity.class);
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
        startActivity(new Intent(TambahTATActivity.this, PenangananKasusNarkobaActivity.class));
        finish();
    }
}

