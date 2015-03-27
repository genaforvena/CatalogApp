package com.imozerov.catalogapp.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by imozerov on 23.03.2015.
 */
public class LoadImageBitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private final ImageView mImageView;

    public LoadImageBitmapAsyncTask(ImageView imageView) {
        mImageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return ImageUtils.createBigImageBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }
}
