package com.bnn.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bnn.Activity.Preview.ImagePreviewActivity;
import com.bnn.Adapter.ListPesertaAdapter;
import com.bnn.Modal.Peserta;
import com.bnn.R;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.Configuration;
import com.bnn.Utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ramdan tri kusumawijaya on 11/27/17.
 */

public class DetilKegiatanSupervisiActivity extends ActivityBase implements View.OnClickListener {

    ImageView imgBack, imgTambah, toggleMenu, imgBukaLokasi, imgLihatPeserta, imgClosePopup, iconTitle, imgUpload1, imgUpload2, imgUpload3, imgDownload;
    Button btnHapus, btnUbah, btnYa, btnTidak, btnKembali;
    EditText edNoid, edJenisKegiatan, edTglKegiatan, edLokasiKegiatan, edPelaksana, edJmlPeserta, edAsalPeserta,edUraianSingkatMateri;ScrollView layoutDetilPenyuluhan;
    RelativeLayout layoutDetilPeserta;
    TextView txtTitle, txtMessage;

    //Create list barang bukti
    ListPesertaAdapter listPeserta;
    ArrayList<Peserta> pesertaArrayList = new ArrayList<>();
    ListView listViewPeserta;

    PreferenceUtils prefUtils;
    String penyuluhabIDIntent;
    ProgressDialog progressDialog;
    String foto1, foto2, foto3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_detil_kegiatansupervisi, linearLayout);

        penyuluhabIDIntent = getIntent().getStringExtra("id");

        prefUtils = PreferenceUtils.getInstance(this);
        progressDialog = new ProgressDialog(this);

        toggleMenu = (ImageView) findViewById(R.id.imgToogleMenu);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
        imgBukaLokasi = (ImageView) findViewById(R.id.imgLokasiKegiatan);
        imgLihatPeserta = (ImageView) findViewById(R.id.imgJumlahPeserta);
        imgDownload = (ImageView) findViewById(R.id.imgDownloadLaporan);

//        //Ditambahkan pada 05 No 2017
        edNoid = (EditText) findViewById(R.id.edNoId);
        edJenisKegiatan = (EditText) findViewById(R.id.edJenisKegiatan);
        edTglKegiatan = (EditText) findViewById(R.id.edTanggalKegiatan);
        edLokasiKegiatan = (EditText) findViewById(R.id.edLokasiKegiatan);
        edPelaksana = (EditText) findViewById(R.id.edPelaksanaKegiatan);
        edAsalPeserta = (EditText) findViewById(R.id.edAsalPesertaKegiatan);
        edJmlPeserta = (EditText) findViewById(R.id.edJumlahPeserta);
        edUraianSingkatMateri = (EditText) findViewById(R.id.edUraianSingkatMateri);

//        Ditambahkan pada 07 No 2017
        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        btnUbah = (Button) findViewById(R.id.btnUbah);
        btnHapus = (Button) findViewById(R.id.btnHapus);
        layoutDetilPenyuluhan = (ScrollView) findViewById(R.id.layoutDetilPenyuluhan);

        //Init view untuk layout list barang bukti
        listViewPeserta = (ListView) findViewById(R.id.listviewPeserta);
        layoutDetilPeserta = (RelativeLayout) findViewById(R.id.layoutDetilPeserta);
        btnKembali = (Button) findViewById(R.id.btnKembaliPeserta);

        toggleMenu.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgTambah.setOnClickListener(this);
        btnHapus.setOnClickListener(this);
        btnUbah.setOnClickListener(this);

        imgUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previewActivity = new Intent(DetilKegiatanSupervisiActivity.this, ImagePreviewActivity.class);

                if (foto1 != null) {
                    if (!foto1.equals("")) {
                        previewActivity.putExtra("foto", foto1);
                        previewActivity.putExtra("title", "Foto 1");

                        startActivity(previewActivity);
                    }
                }
            }
        });

        imgUpload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previewActivity = new Intent(DetilKegiatanSupervisiActivity.this, ImagePreviewActivity.class);

                if (foto2 != null) {
                    if (!foto2.equals("")) {
                        previewActivity.putExtra("foto", foto2);
                        previewActivity.putExtra("title", "Foto 2");

                        startActivity(previewActivity);
                    }
                }
            }
        });

        imgUpload3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previewActivity = new Intent(DetilKegiatanSupervisiActivity.this, ImagePreviewActivity.class);

                if (foto3 != null) {
                    if (!foto3.equals("")) {
                        previewActivity.putExtra("foto", foto3);
                        previewActivity.putExtra("title", "Foto 3");

                        startActivity(previewActivity);
                    }
                }
            }
        });

        loadData();
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
            case R.id.imgToogleMenu:
                menuDrawerLayout.openDrawer(linearLayout2);
                break;
            case R.id.imgBack:
                Intent penangananKasus = new Intent(DetilKegiatanSupervisiActivity.this, KegiatanSupervisiActivity.class);
                startActivity(penangananKasus);
                finish();
                break;
            case R.id.imgAdd:
                Intent tambahPenangananKasus = new Intent(DetilKegiatanSupervisiActivity.this, TambahKegiatanSupervisiActivity.class);
                startActivity(tambahPenangananKasus);
                finish();
                break;
            case R.id.imgLokasiKegiatan:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogView = getLayoutInflater().inflate(R.layout.popup_custom, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialog = new AlertDialog.Builder(DetilKegiatanSupervisiActivity.this)
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

                        Intent mapIntent = new Intent(DetilKegiatanSupervisiActivity.this, MapsActivity.class);
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
            case R.id.btnHapus:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogViewJml = getLayoutInflater().inflate(R.layout.popup_custom, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialogJml = new AlertDialog.Builder(DetilKegiatanSupervisiActivity.this)
                        .setCancelable(false)
                        .setView(dialogViewJml).create();
                initDialogView(dialogViewJml);

                alertDialogJml.show();
                //Membuat alert dialog layout menjadi responsive ketika keyboard muncul
                alertDialogJml.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                //Membuat background menjadi transparent
                alertDialogJml.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                txtTitle.setText("HAPUS DATA");
                iconTitle.setImageResource(R.drawable.trash_copy);

                btnTidak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogJml.dismiss();
                    }
                });

                btnYa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DataDeleteTask().execute();
                    }
                });

                imgClosePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogJml.dismiss();
                    }
                });
                break;
            case R.id.btnUbah:
                Intent ubahIntent = new Intent(DetilKegiatanSupervisiActivity.this, TambahKegiatanSupervisiActivity.class);
                ubahIntent.putExtra("id", penyuluhabIDIntent);
                ubahIntent.putExtra("mode_form", "ubah");
                startActivity(ubahIntent);
                finish();
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
                    .url(Configuration.baseUrl+"/api/advosupervisi/"+penyuluhabIDIntent)
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

                    edNoid.setText(!data.getString("id").equals("null") ? data.getString("id") : "-");
                    edJenisKegiatan.setText(!data.getString("jenis_kegiatan").equals("null") ? data.getString("jenis_kegiatan") : "-");
                    edTglKegiatan.setText(!data.getString("tgl_pelaksanaan").equals("null") ? getNewDate(data.getString("tgl_pelaksanaan")) : "-");
                    edLokasiKegiatan.setText(!data.getString("lokasi_kegiatan").equals("null") ? data.getString("lokasi_kegiatan") : "-");
                    edPelaksana.setText(!data.getString("created_by_username").equals("null") ? data.getString("created_by_username") : "-");
                    edAsalPeserta.setText(!data.getString("kodesasaran").equals("null") ? data.getString("kodesasaran") : "-");
                    edJmlPeserta.setText(!data.getString("jumlah_peserta").equals("null") ? data.getString("jumlah_peserta") : "-");
                    edUraianSingkatMateri.setText(!data.getString("uraian_singkat").equals("null") ? data.getString("uraian_singkat") : "-");

                    foto1 = !data.getString("foto1").equalsIgnoreCase("null") ? data.getString("foto1") : "";
                    foto2 = !data.getString("foto2").equalsIgnoreCase("null") ? data.getString("foto2") : "";
                    foto3 = !data.getString("foto3").equalsIgnoreCase("null") ? data.getString("foto3") : "";

                    loadFoto(foto1, imgUpload1);
                    loadFoto(foto2, imgUpload2);
                    loadFoto(foto3, imgUpload3);

                    progressDialog.dismiss();
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    progressDialog.dismiss();

                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetilKegiatanSupervisiActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(DetilKegiatanSupervisiActivity.this, LoginActivity.class);
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

    class DataDeleteTask extends AsyncTask<Bitmap, Integer, String> {
        private String responseServer;
        private String keyKataKunci;

        public DataDeleteTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseServer = "";

            progressDialog.setMessage("Menghapus data..");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            Headers headers = new Headers.Builder().add("Authorization", "Bearer " + prefUtils.getTokenKey())
                    .add("Accept", "application/json").build();

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/advosupervisi/"+penyuluhabIDIntent)
                    .headers(headers)
                    .delete()
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

            Log.d("Result", result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
                    Toast.makeText(DetilKegiatanSupervisiActivity.this, "Berhasil menghapus data.", Toast.LENGTH_LONG).show();

                    Intent logoutIntent = new Intent(DetilKegiatanSupervisiActivity.this, KegiatanSupervisiActivity.class);
                    startActivity(logoutIntent);
                    finish();
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetilKegiatanSupervisiActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(DetilKegiatanSupervisiActivity.this, LoginActivity.class);
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

    private void loadFoto(String base64, ImageView fotoFrame) {
        if (!base64.equalsIgnoreCase("")) {
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            fotoFrame.setImageBitmap(decodedByte);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, KegiatanSupervisiActivity.class));
        finish();
    }
}
