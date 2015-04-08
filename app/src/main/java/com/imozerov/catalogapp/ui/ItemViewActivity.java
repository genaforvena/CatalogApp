package com.imozerov.catalogapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.services.DatabaseUpdateService;
import com.imozerov.catalogapp.utils.Constants;
import com.imozerov.catalogapp.utils.LoadImageBitmapAsyncTask;
import com.imozerov.catalogapp.utils.OnSwipeTouchListener;

import java.util.ArrayList;
import java.util.List;

public class ItemViewActivity extends ActionBarActivity {
    private static final String TAG = ItemViewActivity.class.getName();

    public static final String ITEM_KEY = TAG + ".item";
    public static final String DELETED_ITEM_KEY = TAG + ".deletedItem";
    public static final String DELETED_CATEGORY_KEY = TAG + ".deletedCategory";

    private TextView mItemName;
    private ImageView mItemImage;
    private TextView mItemDescription;
    private Item mItem;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        mItem = getIntent().getParcelableExtra(ITEM_KEY);

        mItemName = (TextView) findViewById(R.id.activity_item_name);
        mItemImage = (ImageView) findViewById(R.id.activity_item_image);
        mItemDescription = (TextView) findViewById(R.id.activity_item_description);

        mItemName.setText(mItem.getName());
        mItemDescription.setText(mItem.getDescription());

        if (mItem.getImages() != null && !mItem.getImages().isEmpty()) {
            new LoadImageBitmapAsyncTask(mItemImage).execute(mItem.getImages().next());
            mItemImage.setOnTouchListener(new OnSwipeTouchListener(this) {
                @Override
                public void onSwipeRight() {
                    new LoadImageBitmapAsyncTask(mItemImage).execute(mItem.getImages().previous());
                }

                @Override
                public void onSwipeLeft() {
                    new LoadImageBitmapAsyncTask(mItemImage).execute(mItem.getImages().next());
                }
            });
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
                        images.add(Uri.parse(path));
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
}
