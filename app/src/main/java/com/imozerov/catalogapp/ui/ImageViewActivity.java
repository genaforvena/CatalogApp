package com.imozerov.catalogapp.ui;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.utils.LoadImageBitmapAsyncTask;


public class ImageViewActivity extends Activity implements View.OnClickListener {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_view);
        mImageView = (ImageView) findViewById(R.id.activity_image_view_image);
        mImageView.setOnClickListener(this);

        String selectedImage = getIntent().getStringExtra(ItemViewActivity.SELECTED_IMAGE);
        new LoadImageBitmapAsyncTask(mImageView).execute(selectedImage);
    }

    @Override
    public void onClick(View v) {
        this.onBackPressed();
    }
}
