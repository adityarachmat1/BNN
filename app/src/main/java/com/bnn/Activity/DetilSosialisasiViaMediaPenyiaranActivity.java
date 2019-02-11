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
 * Created by ferdinandprasetyo on 11/4/17.
 */

public class DetilSosialisasiViaMediaPenyiaranActivity extends ActivityBase implements View.OnClickListener {

    ImageView imgBack, imgTambah, toggleMenu, imgClosePopup, imgBukaMap, iconTitle, imgUpload1, imgUpload2, imgUpload3, imgDownload;
    Button btnHapus, btnUbah, btnYa, btnTidak;
    EditText edNoid, edJenisMediaPenyiaran, edTglMediaPenyiaran, edPelaksana, edListNamaMediaPenyiaran, edLaporan,
            edUraianSingkatMateri, edNarasumberPenyiaran, edLokasiMediaPenyiaran, edJenisKegiatan, edJumlah;
    ScrollView layoutDetilPenyuluhan;
    RelativeLayout layoutDetilPeserta;
    TextView txtTitle, txtMessage;

    //Create list barang bukti
    ListPesertaAdapter listPeserta;
    ArrayList<Peserta> pesertaArrayList = new ArrayList<>();
    ListView listViewPeserta;

    PreferenceUtils prefUtils;
    String mediapenyiaranIDIntent;

    ProgressDialog progressDialog;

    String foto1, foto2, foto3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_detil_sosialisasimediapenyiaran, linearLayout);

        mediapenyiaranIDIntent = getIntent().getStringExtra("mediapenyiaran_id");

        prefUtils = PreferenceUtils.getInstance(this);

        progressDialog = new ProgressDialog(this);

        toggleMenu = (ImageView) findViewById(R.id.imgToogleMenu);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgTambah = (ImageView) findViewById(R.id.imgAdd);
//        imgDownload = (ImageView) findViewById(R.id.imgDownloadLaporan);
//        imgBukaMap = (ImageView) findViewById(R.id.imgLokasiKegiatan);

//        //Ditambahkan pada 05 No 2017
        edNoid = (EditText) findViewById(R.id.edNoId);
        edJenisMediaPenyiaran = (EditText) findViewById(R.id.edJenisMediaPenyiaran);
        edTglMediaPenyiaran = (EditText) findViewById(R.id.edTanggalMediaPenyiaran);
        edLokasiMediaPenyiaran = (EditText) findViewById(R.id.edLokasiMediaPenyiaran);
        edUraianSingkatMateri = (EditText) findViewById(R.id.edUraianSingkatMateri);
        edPelaksana = (EditText) findViewById(R.id.edPelaksanaMediaPenyiaran);
        edListNamaMediaPenyiaran = (EditText) findViewById(R.id.edListNamaMediaPenyiaran);
//        edNarasumberPenyiaran = (EditText) findViewById(R.id.edNarasumberPenyiaran);
//        edLaporan = (EditText) findViewById(R.id.edLaporanAttach);
        edJenisKegiatan = (EditText) findViewById(R.id.edJenisKegiatanMediaPenyiaran);
        edJumlah = (EditText) findViewById(R.id.edJumlahMediaPenyiaran);

//        Ditambahkan pada 07 No 2017
        imgUpload1 = (ImageView) findViewById(R.id.imageViewbarangbukti);
        imgUpload2 = (ImageView) findViewById(R.id.imageViewpelaku);
        imgUpload3 = (ImageView) findViewById(R.id.imageViewlokasi);

        btnUbah = (Button) findViewById(R.id.btnUbah);
        btnHapus = (Button) findViewById(R.id.btnHapus);
        layoutDetilPenyuluhan = (ScrollView) findViewById(R.id.layoutDetilPenyuluhan);

        toggleMenu.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgTambah.setOnClickListener(this);
//        imgDownload.setOnClickListener(this);
        btnHapus.setOnClickListener(this);
        btnUbah.setOnClickListener(this);
//        imgBukaMap.setOnClickListener(this);

        imgUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent previewActivity = new Intent(DetilSosialisasiViaMediaPenyiaranActivity.this, ImagePreviewActivity.class);

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
                Intent previewActivity = new Intent(DetilSosialisasiViaMediaPenyiaranActivity.this, ImagePreviewActivity.class);

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
                Intent previewActivity = new Intent(DetilSosialisasiViaMediaPenyiaranActivity.this, ImagePreviewActivity.class);

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
                Intent penangananKasus = new Intent(DetilSosialisasiViaMediaPenyiaranActivity.this, SosialisasiViaMediaPenyiaranActivity.class);
                startActivity(penangananKasus);
                finish();
                break;
            case R.id.imgAdd:
                Intent tambahPenangananKasus = new Intent(DetilSosialisasiViaMediaPenyiaranActivity.this, TambahSosialisasiViaMediaPenyiaranActivity.class);
                startActivity(tambahPenangananKasus);
                finish();
                break;
            case R.id.btnHapus:
                //Inflate custom layout untuk menampilkan form out-office
                View dialogViewJml = getLayoutInflater().inflate(R.layout.popup_custom, null);
                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
                final AlertDialog alertDialogJml = new AlertDialog.Builder(DetilSosialisasiViaMediaPenyiaranActivity.this)
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
                        alertDialogJml.dismiss();
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
                Intent ubahIntent = new Intent(DetilSosialisasiViaMediaPenyiaranActivity.this, TambahSosialisasiViaMediaPenyiaranActivity.class);
                ubahIntent.putExtra("mediapenyiaran_id", mediapenyiaranIDIntent);
                ubahIntent.putExtra("mode_form", "ubah");
                startActivity(ubahIntent);
                finish();
                break;
//            case R.id.imgLokasiKegiatan:
//                //Inflate custom layout untuk menampilkan form out-office
//                View dialogView = getLayoutInflater().inflate(R.layout.popup_custom, null);
//                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
//                final AlertDialog alertDialog = new AlertDialog.Builder(DetilSosialisasiViaMediaPenyiaranActivity.this)
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
//                txtTitle.setText("BUKA PETA LOKASI");
//                iconTitle.setImageResource(R.drawable.pin_copy);
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
//                        Intent mapIntent = new Intent(DetilSosialisasiViaMediaPenyiaranActivity.this, MapsActivity.class);
//                        startActivity(mapIntent);
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
//            case R.id.imgDownloadLaporan:
//                //Inflate custom layout untuk menampilkan form out-office
//                View dialogDwonload = getLayoutInflater().inflate(R.layout.popup_custom, null);
//                //Init alert dialog yang akan digunakan sebagai media untuk menampilkan jendela form
//                final AlertDialog alertDialogDownload = new AlertDialog.Builder(DetilSosialisasiViaMediaPenyiaranActivity.this)
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
//                txtTitle.setText("UNDUH LAMPIRAN");
//                iconTitle.setImageResource(R.drawable.download);
//                txtMessage.setText("Apakah anda ingin mengunduh file lampiran?");
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
//                        Intent logoutIntent = new Intent(DetilSosialisasiViaMediaPenyiaranActivity.this, SosialisasiViaMediaPenyiaranActivity.class);
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
                    .url(Configuration.baseUrl+"/api/disempenyiaran/"+mediapenyiaranIDIntent)
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
                    edLokasiMediaPenyiaran.setText(!data.getString("lokasi").equals("null") ? data.getString("lokasi") : "-");
                    edTglMediaPenyiaran.setText(!data.getString("tgl_pelaksanaan").equals("null") ? getNewDate(data.getString("tgl_pelaksanaan")) : "-");
                    edUraianSingkatMateri.setText(!data.getString("materi").equals("null") ? data.getString("materi") : "-");
                    edJenisMediaPenyiaran.setText(!data.getString("jenis_media").equals("null") ? data.getString("jenis_media") : "-");
                    edListNamaMediaPenyiaran.setText(!data.getString("meta_media").equals("null") ? data.getString("meta_media") : "-");
                    edJenisKegiatan.setText(!data.getString("jenis_kegiatan").equals("null") ? data.getString("jenis_kegiatan") : "-");
                    edJumlah.setText(!data.getString("jumlah").equals("null") ? data.getString("jumlah") : "0");

//                    edLaporan.setText(!data.getString("file_upload").equals("null") ? data.getString("file_upload") : "-");
//                    edNarasumberPenyiaran.setText(!data.getString("narasumber").equals("null") ? data.getString("narasumber") : "-");

                    String meta = !data.getString("meta_media").equals("null") ? data.getString("meta_media") : "-";
                    if (!meta.equals("-")) {
                        JSONArray jsonArray = new JSONArray(meta);
                        StringBuilder stringBuilder = new StringBuilder();
                        String nama = "-";

                        for (int i = 0; i < jsonArray.length(); i++) {
                            stringBuilder.append(i == jsonArray.length()-1 ? ((JSONObject)jsonArray.get(i)).getString("list_media_nama") : ((JSONObject)jsonArray.get(i)).getString("list_media_nama")+"\n");
                        }

                        edListNamaMediaPenyiaran.setText(stringBuilder.toString());
                    }

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

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetilSosialisasiViaMediaPenyiaranActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(DetilSosialisasiViaMediaPenyiaranActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    }
                }
            } catch(JSONException e){
                loadData();
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
                    .url(Configuration.baseUrl+"/api/disempenyiaran/"+mediapenyiaranIDIntent)
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
                    showToast("Berhasil menghapus data.");

                    Intent logoutIntent = new Intent(DetilSosialisasiViaMediaPenyiaranActivity.this, SosialisasiViaMediaPenyiaranActivity.class);
                    startActivity(logoutIntent);
                    finish();
                } else if (jsonObject.getString("status").equalsIgnoreCase("error")) {
                    if (jsonObject.getString("comment").equalsIgnoreCase("token expired")) {
                        prefUtils.clearPref();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetilSosialisasiViaMediaPenyiaranActivity.this)
                                .setMessage("Token anda telah expired, silahkan login ulang.")
                                .setCancelable(false)
                                .setNeutralButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent logout = new Intent(DetilSosialisasiViaMediaPenyiaranActivity.this, LoginActivity.class);
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

    private void loadFoto(String base64, ImageView fotoFrame) {
        if (!base64.equalsIgnoreCase("")) {
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            fotoFrame.setImageBitmap(decodedByte);
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

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetilSosialisasiViaMediaPenyiaranActivity.this, SosialisasiViaMediaPenyiaranActivity.class));
        finish();
    }
}
