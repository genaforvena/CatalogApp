package com.imozerov.catalogapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.utils.LoadImageBitmapAsyncTask;

public class ItemViewActivity extends ActionBarActivity {
    private static final String TAG = ItemViewActivity.class.getName();

    public static final String ITEM_KEY = TAG + ".item";
    public static final String CATEGORY_KEY = TAG + ".category";
    public static final String DELETED_ITEM_KEY = TAG + ".deletedItem";

    private TextView mItemName;
    private ImageView mItemImage;
    private TextView mItemDescription;
    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        mItem = getIntent().getParcelableExtra(ITEM_KEY);

        mItemName = (TextView) findViewById(R.id.activity_item_name);
        mItemImage = (ImageView) findViewById(R.id.activity_item_image);
        mItemDescription = (TextView) findViewById(R.id.activity_item_description);

        mItemName.setText(mItem.getName());
        mItemDescription.setText(mItem.getDescription());
        if (mItem.getImage() != null) {
            mItemImage.setImageBitmap(mItem.getImage());
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            Intent intent = new Intent(this, CatalogActivity.class);
            intent.putExtra(DELETED_ITEM_KEY, mItem);
            finish();
            startActivity(intent);
            return true;
        } else if (id == R.id.action_report) {
            Toast.makeText(this, "Reporting item!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
