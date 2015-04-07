package com.imozerov.catalogapp.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by imozerov on 23.03.2015.
 */
public class LoadImageBitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> mImageView;
    private String mImagePath;

    public LoadImageBitmapAsyncTask(ImageView imageView) {
        mImageView = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        mImagePath = params[0];
        return ImageUtils.createBigImageBitmap(mImagePath);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mImageView.get() == null) {
            return;
        }

        mImageView.get().setImageBitmap(bitmap);
        mImageView.get().setTag(mImagePath);
    }
}
