package com.bnn.Activity.Preview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnn.R;

import org.w3c.dom.Text;

/**
 * Created by ferdinandprasetyo on 12/15/17.
 */

public class ImagePreviewActivity extends Activity {
    private ViewPager viewPager;
    private ImagePreviewAdapter imagePreviewAdapter;
    private ImageView imageView, imageBack;
    private TextView txtTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.image_preview_activity);

        String foto = getIntent().getStringExtra("foto");
        String title = getIntent().getStringExtra("title");

        imageView = (ImageView) findViewById(R.id.imgView);
        imageBack = (ImageView) findViewById(R.id.imgBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);

        imageView.setImageBitmap(createBitmap(foto));
        txtTitle.setText(title);

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePreviewActivity.this.finish();
            }
        });
//        viewPager = (ViewPager) findViewById(R.id.viewPager);
//        imagePreviewAdapter = new ImagePreviewAdapter(this, bitmaps);
//
//        viewPager.setAdapter(imagePreviewAdapter);
    }

    private Bitmap createBitmap(String base64) {
        if (!base64.equalsIgnoreCase("")) {
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        }

        return null;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
