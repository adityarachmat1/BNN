package com.bnn.Activity.Preview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bnn.R;

/**
 * Created by ferdinandprasetyo on 12/15/17.
 */

public class ImagePreviewAdapter extends PagerAdapter {
    private Context context;
    private Bitmap[] images;

    public ImagePreviewAdapter(Context context, Bitmap[] images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Bitmap image = images[position];
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.image_preview_item, container, false);
        ((ImageView)layout.findViewById(R.id.imgPreview)).setImageBitmap(image);
        container.addView(layout);
        return layout;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
