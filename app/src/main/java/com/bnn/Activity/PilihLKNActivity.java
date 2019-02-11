package com.bnn.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.bnn.Modal.LaporanKasus;
import com.bnn.R;
import com.bnn.Utils.ActivityBase;
import com.bnn.Utils.Configuration;
import com.bnn.Utils.DateUtils;
import com.bnn.Utils.LayoutUtil;
import com.bnn.Utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PilihLKNActivity extends ActivityBase implements  View.OnClickListener {

    //Spinner spinnerLKN;
    AutoCompleteTextView tvLKN;
    //ArrayList<String> listLKN;
    ArrayList<LaporanKasus> listLKN;
    Hashtable<String, String> hashLKN;
    PreferenceUtils prefUtils;
    Button btnPilihNoLKN;
    String id;
    int selected_idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_pilih_lkn, linearLayout);
      //  setContentView(R.layout.activity_pilih_lkn);
        prefUtils = PreferenceUtils.getInstance(this);

        tvLKN = (AutoCompleteTextView) findViewById(R.id.auto_tv_LKN);
        tvLKN.setThreshold(1);
        listLKN = new ArrayList<LaporanKasus>();
        hashLKN = new Hashtable<String, String>();

        btnPilihNoLKN = (Button) findViewById(R.id.btnSimpan);
        btnPilihNoLKN.setOnClickListener(this);
        id = "";
        new FetchLKNList("").execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSimpan:
                String laporan = tvLKN.getText().toString();
                for(Map.Entry entry: hashLKN.entrySet()){
                    if(laporan.equals(entry.getValue())){
                        id = entry.getKey().toString();
                        break;
                    }
                }

                Intent tambahBukti = new Intent(PilihLKNActivity.this, TambahPemusnahanBarangBukti2Activity.class);
                tambahBukti.putExtra("id", id);
                Log.d("id", id);
                startActivity(tambahBukti);
                finish();
                break;
        }
    }

    class FetchLKNList extends AsyncTask<String, String, String>{
        private String responseServer;
        private String modeTask;
        private String idBarbuk;

        public FetchLKNList(String modeTask) {
            this.modeTask = modeTask;
        }

        public FetchLKNList(String modeTask, String idBarbuk) {
            this.modeTask = modeTask;
            this.idBarbuk = idBarbuk;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            responseServer = "";
        }


//        String url = Configuration.baseUrl + "/api/listpemusnahan";
//        JSONArray dataJsonArr = null;
//        OkHttpClient client = new OkHttpClient();
//        Response httpResponse = null;
//        String responseServer = "" ;
//
//        Request request = new Request.Builder()
//                .url(url)
//                .addHeader("Authorization", "Bearer "+ prefUtils.getTokenKey())
//                .get()
//                .build();

        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient httpclient = new OkHttpClient();

            RequestBody requestBody = null;
            Request httpRequest = null;

            Headers headers = new Headers.Builder().add("Authorization", "Bearer " + prefUtils.getTokenKey())
                    .add("Accept", "application/json").build();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(5, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS);

                FormBody.Builder form = new FormBody.Builder();

                requestBody = form.build();

                httpRequest = new Request.Builder()
                        .url(Configuration.baseUrl + "/api/listpemusnahan")
                        .headers(headers)
                        .post(requestBody)
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("result", s);
            listLKN.clear();
            hashLKN.clear();

            try {
                JSONObject rawResult = new JSONObject(s);
                JSONArray jarray = rawResult.getJSONArray("data");
                int len = jarray.length();
                LaporanKasus laporan = null;

                for (int x = 0; x < len; x++){
                    String id = jarray.getJSONObject(x).getString("id");
                    String no = jarray.getJSONObject(x).getString("nomor_lkn");
                    laporan = new LaporanKasus(id, no);
                    hashLKN.put(id, no);
                    listLKN.add(laporan);
                }

                int sz = listLKN.size();
                String[] items = new String[sz];
                for (int x = 0; x < sz; x++ ){
                    LaporanKasus lk = listLKN.get(x);
                    items[x] = lk.getNo();
                }
                tvLKN.setAdapter(new ArrayAdapter<String>(PilihLKNActivity.this,
                        R.layout.spinner_item_small, items));

//                tvLKN.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    public void onItemClick(AdapterView<?> parent, View view,int position, long id)
//                    {
//                        Toast.makeText(getApplicationContext(), "position: "+listLKN.get(position).getId(), Toast.LENGTH_LONG).show();
//                    }});
                /*
//                tvLKN.setOnClickListener(new View.OnClickListener(){
//
//                    @Override
//                    public void onClick(View view) {
//                        tvLKN.showDropDown();
//                    }
//                });
                */

                //Toast.makeText(getApplicationContext(), "Count: "+len, Toast.LENGTH_LONG).show();

            }
            catch (JSONException je){
                je.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PilihLKNActivity.this, PemberantasanActivity.class));
        finish();
    }
}
