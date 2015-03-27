package com.imozerov.catalogapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.database.CatalogDataSource;
import com.imozerov.catalogapp.services.DatabaseUpdateService;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.utils.Constants;

public class ItemViewActivity extends ActionBarActivity {
    private static final String TAG = ItemViewActivity.class.getName();

    public static final String ITEM_KEY = TAG + ".item";
    public static final String DELETED_ITEM_KEY = TAG + ".deletedItem";

    private TextView mItemName;
    private ImageView mItemImage;
    private TextView mItemDescription;
    private Item mItem;

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
        new AsyncTask<Void, Void, Item>() {
            @Override
            protected Item doInBackground(Void... params) {
                CatalogDataSource catalogDataSource = new CatalogDataSource(ItemViewActivity.this);
                catalogDataSource.open();
                Item item = catalogDataSource.getItem(mItem.getId());
                catalogDataSource.close();
                return item;
            }

            @Override
            protected void onPostExecute(Item item) {
                if (item == null) {
                    Log.w(TAG, "Item is null.");
                    return;
                }

                if (item.getImage() == null) {
                    Log.w(TAG, "Item has no image.");
                    return;
                }

                mItemImage.setImageBitmap(item.getImage());
            }
        }.execute();

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
