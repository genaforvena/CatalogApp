package com.imozerov.catalogapp.ui;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.imozerov.catalogapp.BuildConfig;
import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.database.CatalogDataSource;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.ui.adapters.CatalogAdapter;
import com.imozerov.catalogapp.utils.Constants;


public class CatalogActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, ExpandableListView.OnChildClickListener {
    private final static String TAG = CatalogActivity.class.getName();

    private ExpandableListView mCatalogView;
    private CatalogAdapter mCatalogAdapter;
    private SearchView mSearchView;
    private CatalogDataSource mCatalogDataSource;
    private DatabaseUpdatedBroadcastReceiver mDatabaseUpdatedBroadcastReceiver;
    private IntentFilter mStatusIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCatalogDataSource = new CatalogDataSource(this);
        mCatalogDataSource.open();

        mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION_DATABASE_UPDATED);
        mDatabaseUpdatedBroadcastReceiver = new DatabaseUpdatedBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDatabaseUpdatedBroadcastReceiver,
                mStatusIntentFilter);

        setContentView(R.layout.activity_catalog_list);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) findViewById(R.id.activity_catalog_list_search);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);

        mCatalogView = (ExpandableListView) findViewById(R.id.activity_catalog_list_listview);

        mCatalogView.setOnChildClickListener(this);

        mCatalogAdapter = new CatalogAdapter(mCatalogDataSource.getCategoriesCursor(), this, mCatalogDataSource);
        mCatalogView.setAdapter(mCatalogAdapter);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onDestroy() {
        mCatalogAdapter.changeCursor(null);
        mCatalogAdapter = null;
        mCatalogDataSource.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_item) {
            Log.i(TAG, "Adding item");
            Intent intent = new Intent(this, AddItemActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_add_category) {
            Log.i(TAG, "Adding category");
            Intent intent = new Intent(this, AddCategoryActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_share_app) {
            Log.i(TAG, "Sharing app is not available as app is not available on Play Store.");
            Toast.makeText(this, "Sharing app", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            Toast.makeText(this, "Showing about", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void expandAll() {
        int count = mCatalogAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            mCatalogView.expandGroup(i);
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mCatalogAdapter.filterList(query);
        expandAll();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mCatalogAdapter.filterList(query);

        expandAll();
        return true;
    }

    @Override
    public boolean onClose() {
        mCatalogAdapter.filterList("");
        expandAll();
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Item from list view was clicked. Id = " + id);
        }

        Item item = mCatalogDataSource.getItem(id);
        Intent intent = new Intent(CatalogActivity.this, ItemViewActivity.class);
        intent.putExtra(ItemViewActivity.ITEM_KEY, item);
        startActivity(intent);
        return true;
    }

    private class DatabaseUpdatedBroadcastReceiver extends BroadcastReceiver {
        private DatabaseUpdatedBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive(" + intent + ")");
            if (mCatalogDataSource != null && mCatalogDataSource.isOpen()) {
                mCatalogAdapter.setGroupCursor(mCatalogDataSource.getCategoriesCursor());
                mCatalogView.setAdapter(mCatalogAdapter);
            }
        }
    }
}
