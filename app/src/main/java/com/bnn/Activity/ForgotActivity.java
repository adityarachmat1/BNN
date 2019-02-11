package com.bnn.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bnn.R;
import com.bnn.Utils.Configuration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener  {
    private static final String TAG = ForgotActivity.class.getSimpleName();

    EditText txtforgotpassword;
    Button btnforgot;
    //Ditambahkan 03 Nov 2017
    TextView txtLogin;
    long backPressed;
    String forgot;

    //Ditambahkan pada 09 November 2017
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        //Menampilkan dialog untuk memberitahu bahwa permintaan sedang diproses
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses permintaan..");

        txtforgotpassword = (EditText) findViewById(R.id.textforgotpassword);
        btnforgot = (Button) findViewById(R.id.button_forgotpassword);
        //Ditambahkan 03 Nov 2017
        txtLogin = (TextView) findViewById(R.id.txtGoToLogin);

        //Ditambahkan 03 Nov 2017
        txtLogin.setText(Html.fromHtml("<u>Masuk</u>"));

        btnforgot.setOnClickListener(this);

        //Ditambahkan 03 Nov 2017
        txtLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //Salah di bagian id untuk klik button
            case R.id.button_forgotpassword:
                //function forgot
                forgot = txtforgotpassword.getText().toString();

                //Ditambakan 03 Nov 2017
                if (forgot.equals("")) {
                    Toast.makeText(ForgotActivity.this, "Masukkan email anda.", Toast.LENGTH_LONG).show();
                } else if (!forgot.equals("")) {
                    if (Configuration.isDeviceOnline(this)) {
                        progressDialog.show();

                        DataUploadTask dataUploadTask = new DataUploadTask();
                        dataUploadTask.execute();
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(this)
                                .setCancelable(false)
                                .setMessage("Tidak dapat melakukan permintaan. Silahkan cek koneksi anda dan coba lagi.")
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alert.create().show();
                    }
                }

                break;
            //Ditambahkan 03 Nov 2017
            case R.id.txtGoToLogin:
                Intent y = new Intent(ForgotActivity.this, LoginActivity.class);
                startActivity(y);
                finish();
                break;
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

        }

        @Override
        protected String doInBackground(Bitmap... b) {
            OkHttpClient httpclient = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("email", forgot)
                    .build();

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/password/email")
                    .addHeader("Accept", "application/json")
                    .post(body)
                    .build();
            Response httpResponse = null;

            try {
                httpResponse = httpclient.newCall(httpRequest).execute();
            }  catch (IOException e) {
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

            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d(TAG, "Response forgot: "+jsonObject.toString()+" "+forgot);

                //Dirubah pada 03 Nov 2017 menambahkan fungsi else dan show message gagal request
                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    txtforgotpassword.setText("");
                    forgot = "";

                    Toast.makeText(ForgotActivity.this, "Berhasil meminta, silahkan cek email anda untuk merubah password.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotActivity.this, "Tidak dapat melakukan proses.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotActivity.this, LoginActivity.class));
        finish();
    }
}
