package com.bnn.Activity;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bnn.R;
import com.bnn.Task.LocationProvider;
import com.bnn.Utils.ActivityBase;

public class TambahPemusnahanBarangBukti2Activity  extends ActivityBase implements View.OnClickListener,
        LocationProvider.LocationCallback, MapsActivity.LocationListener {

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_tambah_pemusnahan_barang_bukti2);
        id = getIntent().getStringExtra("id");
        Log.d("id", id);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void getLatLong(double lat, double lng) {

    }

    @Override
    public void handleNewLocation(Location location) {

    }

    @Override
    public void onBackPressed() {
        Intent iii = new Intent(TambahPemusnahanBarangBukti2Activity.this, PilihLKNActivity.class);
        startActivity(iii);
        finish();
    }
}
