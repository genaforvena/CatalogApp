package com.imozerov.catalogapp.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
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
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.ui.adapters.CatalogAdapter;
import com.imozerov.catalogapp.utils.ImageUtils;


public class CatalogActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, ExpandableListView.OnChildClickListener {
    public static final int REQUEST_CODE_ADD_ITEM = 112;
    private final static String TAG = CatalogActivity.class.getName();
    private static final int REQUEST_CODE_ADD_CATEGORY = 114;

    private ExpandableListView mCatalogView;
    private CatalogAdapter mCatalogAdapter;
    private SearchView mSearchView;
    private CatalogDataSource mCatalogDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCatalogDataSource = new CatalogDataSource(this);
        mCatalogDataSource.open();
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

        final Intent intent = getIntent();
        if (intent != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    Item deletedItem = intent.getParcelableExtra(ItemViewActivity.DELETED_ITEM_KEY);
                    if (deletedItem == null) {
                        return null;
                    }
                    mCatalogDataSource.deleteItem(deletedItem);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    mCatalogAdapter.notifyDataSetChanged();
                }
            }.execute();
        }
    }

    @Override
    protected void onDestroy() {
        mCatalogDataSource.close();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.i(TAG, "Activity result is received. requestCode: " + requestCode + "; resultCode: " + resultCode + "; data: " + data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }

        if (requestCode == REQUEST_CODE_ADD_CATEGORY) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    Category category = data.getParcelableExtra(AddCategoryActivity.CATEGORY_KEY);
                    String imagePath = data.getStringExtra(AddCategoryActivity.CATEGORY_IMAGE_PATH);
                    if (!TextUtils.isEmpty(imagePath)) {
                        category.setImage(ImageUtils.createBigImageBitmap(imagePath));
                    }
                    mCatalogDataSource.addCategory(category);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    mCatalogAdapter.setGroupCursor(mCatalogDataSource.getCategoriesCursor());
                    mCatalogAdapter.notifyDataSetChanged();
                }
            }.execute();

        } else if (requestCode == REQUEST_CODE_ADD_ITEM) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    Item newItem = data.getParcelableExtra(AddItemActivity.ITEM_KEY);
                    String imagePath = data.getStringExtra(AddItemActivity.ITEM_IMAGE_PATH);
                    if (!TextUtils.isEmpty(imagePath)) {
                        newItem.setImage(ImageUtils.createBigImageBitmap(imagePath));
                    }
                    mCatalogDataSource.addItem(newItem);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    mCatalogAdapter.notifyDataSetChanged();
                }
            }.execute();
        }
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
            startActivityForResult(intent, REQUEST_CODE_ADD_ITEM);
            return true;
        } else if (id == R.id.action_add_category) {
            Log.i(TAG, "Adding category");
            Intent intent = new Intent(this, AddCategoryActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_CATEGORY);
            return true;
        } else if (id == R.id.action_share_app) {
            Log.i(TAG, "Sharing app");
            Toast.makeText(this, "Sharing app", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_about) {
            Log.i(TAG, "Showing about");
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
}
