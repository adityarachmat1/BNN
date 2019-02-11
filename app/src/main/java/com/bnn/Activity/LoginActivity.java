package com.bnn.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bnn.Modal.UserMenu;
import com.bnn.R;
import com.bnn.Utils.Configuration;
import com.bnn.Utils.PreferenceUtils;
import com.crashlytics.android.Crashlytics;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;
import java.util.Set;

import io.fabric.sdk.android.Fabric;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //Ditambahkan pada 03 Nov 2017
    private static final String TAG = LoginActivity.class.getSimpleName();

    EditText txtUsername, txtPassword;
    TextView txtForgot;
    Button btnLogin;
    long backPressed;
    PreferenceUtils prefUtils;
    String email,password,deviceToken;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefUtils = PreferenceUtils.getInstance(this);
        Fabric.with(this, new Crashlytics());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memverifikasi akun..");

        if (prefUtils.isLogin()) {
            Intent x = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(x);
            finish();
        } else if (!prefUtils.isLogin()) {
            initViewLogin();
        }

        OneSignal.startInit(this).init();
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                deviceToken = userId;
            }
        });
    }

    public void forceCrash() {
        throw new RuntimeException("This is a crash");
    }

    private void initViewLogin() {
        setContentView(R.layout.activity_login);

        txtUsername = (EditText) findViewById(R.id.username);
        txtPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.login);
        txtForgot = (TextView) findViewById(R.id.forgotpassword);

        //jgn lupa hapus kalo build APK untuk user
//        txtUsername.setText("daniel@drife.co.id");
//        txtPassword.setText("asdasd");

        btnLogin.setOnClickListener(this);
        txtForgot.setOnClickListener(this);

        txtPassword.setTransformationMethod(new PasswordTransformationMethod());

        //Ditambahakan pada 03 Nov 2017
        txtForgot.setText(Html.fromHtml("<u>Lupa Kata Sandi ?</u>"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                //function login
                email = txtUsername.getText().toString();
                password = txtPassword.getText().toString();

                Log.d(TAG, "Email: "+email.equals(""));

//                Intent x = new Intent(LoginActivity.this, DashboardActivity.class);
//                x.putExtra("id", "1");
//                prefUtils.setTokenKey("abcde");
//                startActivity(x);
//                finish();

                if (!email.equals("") && !password.equals("")) {
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
                } else if (email.equals("")) {
                    Toast.makeText(getBaseContext(), "Email tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(getBaseContext(), "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.forgotpassword:
                //function forgotpassword
                Intent y = new Intent(LoginActivity.this, ForgotActivity.class);
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
                    .add("email", email)
                    .add("password", password)
                    .add("device_id", deviceToken != null ? deviceToken : "")
                    .build();

            Request httpRequest = new Request.Builder()
                    .url(Configuration.baseUrl+"/api/login")
                    .post(body)
                    .build();
            Response httpResponse = null;

            Log.d("param", bodyToString(httpRequest));

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

            Log.d("test", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("status").equalsIgnoreCase("sukses")) {
                    JSONObject data = jsonObject.getJSONObject("data");

                    String wilayah = data.getString("wilayah") != null && !data.getString("wilayah").equals("null") && !data.getString("wilayah").equals("") ? data.getString("wilayah"):"";
                    Log.d("test", wilayah);
                    Log.d("test1", "");

                    prefUtils.setURL(Configuration.baseUrl+"/api/");
                    prefUtils.setTokenKey(data.getString("access_token"));
                    Log.d("token", data.getString("access_token"));
                    prefUtils.setId(data.getString("id"));
                    prefUtils.setName(data.getString("name"));
                    prefUtils.setRank(data.getString("group"));
                    prefUtils.setMenu(data.getJSONArray("menu").toString());
                    prefUtils.setWilayah(data.getString("wilayah") != null && !data.getString("wilayah").equals("null") && !data.getString("wilayah").equals("") ? data.getString("wilayah"):"");
                    prefUtils.setInstansi(data.getString("id_instansi") != null && !data.getString("id_instansi").equals("null") && !data.getString("id_instansi").equals("") ? data.getString("id_instansi"):"");
                    prefUtils.setIsLogin(true);

                    if (data.optJSONArray("menu").length() > 0) {
                        StringBuilder stringMenuUser = new StringBuilder();

                        for (int i = 0; i < data.getJSONArray("menu").length(); i++) {
                            JSONObject jsonObject1 = data.getJSONArray("menu").getJSONObject(i);
//
                            String id = i == data.getJSONArray("menu").length()-1 ? String.valueOf(jsonObject1.getInt("menu_id")) : String.valueOf(jsonObject1.getInt("menu_id"))+",";

                            stringMenuUser.append(id);
                        }

                        Log.d("id", "id: "+stringMenuUser.toString());

                        prefUtils.setUserMenu(stringMenuUser.toString());
                    }

//                    Log.d("cobalogin","url : "+ Configuration.baseUrl+"/api/\ndata : " + data.toString() + "\ntoken : " + data.getString("access_token")
//                    + "\nid : " + data.getString("id") + "\nname : " + data.getString("name") + "\nwilayah : " + data.getString("wilayah"));

                    Intent x = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(x);
                    finish();
                } else {
                    prefUtils.setIsLogin(false);

                    if (jsonObject.getString("status").equalsIgnoreCase("Error")) {
                        Toast.makeText(LoginActivity.this, jsonObject.getString("comment")+".", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Maaf terjadi kesalahan, mohon coba beberapa saat lagi.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            this.finish();
        } else {
            Toast.makeText(getBaseContext(), "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
            backPressed = System.currentTimeMillis();
        }
    }

    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
