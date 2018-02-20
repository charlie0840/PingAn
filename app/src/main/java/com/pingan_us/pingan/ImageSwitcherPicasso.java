package com.pingan_us.pingan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageSwitcher;

import com.squareup.picasso.Picasso;

/**
 * Created by yshui on 1/30/18.
 */

public class ImageSwitcherPicasso implements com.squareup.picasso.Target {

    private ImageSwitcher mSwitcher;
    private Context mContext;

    public ImageSwitcherPicasso(Context context, ImageSwitcher imageSwitcher) {
        mSwitcher = imageSwitcher;
        mContext = context;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
        mSwitcher.setImageDrawable(new BitmapDrawable(mContext.getResources()));
    }

    @Override
    public void onBitmapFailed(Drawable drawable) {

    }

    @Override
    public void onPrepareLoad(Drawable drawable) {

    }

}
