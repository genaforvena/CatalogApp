package com.imozerov.catalogapp.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.imozerov.catalogapp.database.RuntimeDatabase;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.ui.adapters.CatalogExpandableListViewAdapter;


public class CatalogExpandableListActivity extends ActionBarActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    public static final int REQUEST_CODE_ADD_ITEM = 112;
    private final static String TAG = CatalogExpandableListActivity.class.getName();
    private static final int REQUEST_CODE_ADD_CATEGORY = 114;

    private ExpandableListView mExpandableListView;
    private CatalogExpandableListViewAdapter mCatalogExpandableListViewAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_list);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) findViewById(R.id.activity_catalog_list_search);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);

        mExpandableListView = (ExpandableListView) findViewById(R.id.activity_catalog_list_listview);

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Item from list view was clicked.");

                    final Category currentCategory = mCatalogExpandableListViewAdapter.getGroup(groupPosition);
                    final Item currentItem = currentCategory.items.get(childPosition);

                    Intent intent = new Intent(CatalogExpandableListActivity.this, ItemActivity.class);
                    intent.putExtra(ItemActivity.ITEM_KEY, currentItem);
                    intent.putExtra(ItemActivity.CATEGORY_KEY, currentCategory);
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });

        mCatalogExpandableListViewAdapter = new CatalogExpandableListViewAdapter(this, RuntimeDatabase.getInstance().getCategories());
        mExpandableListView.setAdapter(mCatalogExpandableListViewAdapter);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        if (intent != null) {
            Item deletedItem = intent.getParcelableExtra(ItemActivity.DELETED_ITEM_KEY);
            Category deletedCategory = intent.getParcelableExtra(ItemActivity.DELETED_CATEGORY_KEY);
            if (deletedItem == null && deletedCategory == null) {
                return;
            }
            RuntimeDatabase.getInstance().deleteItem(deletedCategory, deletedItem);
            mCatalogExpandableListViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "Activity result is received. requestCode: " + requestCode + "; resultCode: " + resultCode + "; data: " + data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }

        if (requestCode == REQUEST_CODE_ADD_CATEGORY) {
            Category category = data.getParcelableExtra(AddCategoryActivity.CATEGORY_KEY);
            RuntimeDatabase.getInstance().addCategory(category);
        } else if (requestCode == REQUEST_CODE_ADD_ITEM) {
            Category itemsCategory = data.getParcelableExtra(AddItemActivity.CATEGORY_KEY);
            Item newItem = data.getParcelableExtra(AddItemActivity.ITEM_KEY);
            RuntimeDatabase.getInstance().addItem(itemsCategory, newItem);
        }

        mCatalogExpandableListViewAdapter.notifyDataSetChanged();
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
        int count = mCatalogExpandableListViewAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            mExpandableListView.expandGroup(i);
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mCatalogExpandableListViewAdapter.filterData(query);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mCatalogExpandableListViewAdapter.filterData(query);
        expandAll();
        return false;
    }

    @Override
    public boolean onClose() {
        mCatalogExpandableListViewAdapter.filterData("");
        expandAll();
        return false;
    }
}
