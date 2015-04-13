package com.imozerov.catalogapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.imozerov.catalogapp.BuildConfig;
import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.services.DatabaseUpdateService;
import com.imozerov.catalogapp.utils.Constants;
import com.imozerov.catalogapp.utils.LoadImageBitmapAsyncTask;

import java.io.File;
import java.util.ArrayList;

public class ItemViewActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = ItemViewActivity.class.getName();

    public static final String ITEM_KEY = TAG + ".item";
    public static final String DELETED_ITEM_KEY = TAG + ".deletedItem";
    public static final String DELETED_CATEGORY_KEY = TAG + ".deletedCategory";
    public static final String SELECTED_IMAGE = TAG + ".selected_image";

    private TextView mItemName;
    private ImageView mItemImage1;
    private ImageView mItemImage2;
    private ImageView mItemImage3;
    private ImageView mItemImage4;
    private TextView mItemDescription;
    private Item mItem;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        mItem = getIntent().getParcelableExtra(ITEM_KEY);

        mItemName = (TextView) findViewById(R.id.activity_item_name);
        mItemImage1 = (ImageView) findViewById(R.id.activity_item_image1);
        mItemImage1.setOnClickListener(this);
        mItemImage2 = (ImageView) findViewById(R.id.activity_item_image2);
        mItemImage2.setOnClickListener(this);
        mItemImage3 = (ImageView) findViewById(R.id.activity_item_image3);
        mItemImage3.setOnClickListener(this);
        mItemImage4 = (ImageView) findViewById(R.id.activity_item_image4);
        mItemImage4.setOnClickListener(this);
        mItemDescription = (TextView) findViewById(R.id.activity_item_description);

        mItemName.setText(mItem.getName());
        mItemDescription.setText(mItem.getDescription());

        if (mItem.getImages() != null && !mItem.getImages().isEmpty()) {
            if (mItem.getImages() != null && !mItem.getImages().isEmpty()) {
                int imagesSize = mItem.getImages().size();
                if (imagesSize == 1) {
                    new LoadImageBitmapAsyncTask(mItemImage1).execute(mItem.getImages().get(0));
                    mItemImage2.setVisibility(View.GONE);
                    mItemImage3.setVisibility(View.GONE);
                    mItemImage4.setVisibility(View.GONE);
                } else if (imagesSize == 2) {
                    new LoadImageBitmapAsyncTask(mItemImage1).execute(mItem.getImages().get(0));
                    new LoadImageBitmapAsyncTask(mItemImage2).execute(mItem.getImages().get(1));
                    mItemImage3.setVisibility(View.GONE);
                    mItemImage4.setVisibility(View.GONE);
                } else if (imagesSize == 3) {
                    new LoadImageBitmapAsyncTask(mItemImage1).execute(mItem.getImages().get(0));
                    new LoadImageBitmapAsyncTask(mItemImage2).execute(mItem.getImages().get(1));
                    new LoadImageBitmapAsyncTask(mItemImage3).execute(mItem.getImages().get(2));
                    mItemImage4.setVisibility(View.GONE);
                } else if (imagesSize == 4) {
                    new LoadImageBitmapAsyncTask(mItemImage1).execute(mItem.getImages().get(0));
                    new LoadImageBitmapAsyncTask(mItemImage2).execute(mItem.getImages().get(1));
                    new LoadImageBitmapAsyncTask(mItemImage3).execute(mItem.getImages().get(2));
                    new LoadImageBitmapAsyncTask(mItemImage4).execute(mItem.getImages().get(3));
                }
            }
        } else {
            mItemImage1.setVisibility(View.GONE);
            mItemImage2.setVisibility(View.GONE);
            mItemImage3.setVisibility(View.GONE);
            mItemImage4.setVisibility(View.GONE);
        }
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mItem.isUserDefined()) {
            getMenuInflater().inflate(R.menu.menu_item_user_item, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_item_default_item, menu);
        }

        MenuItem item = menu.findItem(R.id.menu_item_share);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
                sharingIntent.setType("*/*");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mItem.getName());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mItem.getDescription());
                ArrayList<Uri> images = new ArrayList<>();
                if (mItem.getImages() != null) {
                    for (String path : mItem.getImages()) {
                        Uri imageUri = Uri.fromFile(new File(path));
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Image uri: " + imageUri);
                        }
                        images.add(imageUri);
                    }
                }
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, images);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            }
        });

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            Intent intent = new Intent(this, DatabaseUpdateService.class);
            intent.setAction(Constants.ACTION_DELETE_ITEM);
            intent.putExtra(DELETED_ITEM_KEY, mItem);
            startService(intent);

            startActivity(new Intent(this, CatalogActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_report) {
            Toast.makeText(this, "Reporting item!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ImageViewActivity.class);
        intent.putExtra(SELECTED_IMAGE, (String) v.getTag());
        startActivity(intent);
    }
}
