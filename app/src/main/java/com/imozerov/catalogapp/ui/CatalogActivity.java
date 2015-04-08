package com.imozerov.catalogapp.ui;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.imozerov.catalogapp.BuildConfig;
import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.database.CatalogDataSource;
import com.imozerov.catalogapp.database.helpers.SimpleCursorLoader;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.services.DatabaseUpdateService;
import com.imozerov.catalogapp.ui.adapters.CatalogAdapter;
import com.imozerov.catalogapp.utils.Constants;


public class CatalogActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener, SearchView.OnCloseListener, ExpandableListView.OnChildClickListener, AdapterView.OnItemLongClickListener {
    private final static String TAG = CatalogActivity.class.getName();

    private ExpandableListView mCatalogView;
    private CatalogAdapter mCatalogAdapter;
    private SearchView mSearchView;
    private CatalogDataSource mCatalogDataSource;
    private DatabaseUpdatedBroadcastReceiver mDatabaseUpdatedBroadcastReceiver;
    private IntentFilter mStatusIntentFilter;
    private String mSearchQuery;

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

        Loader loader = getSupportLoaderManager().getLoader(-1);
        if (loader != null && !loader.isReset()) {
            getSupportLoaderManager().restartLoader(-1, null, this);
        } else {
            getSupportLoaderManager().initLoader(-1, null, this).forceLoad();
        }

        setContentView(R.layout.activity_catalog_list);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) findViewById(R.id.activity_catalog_list_search);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);

        mCatalogView = (ExpandableListView) findViewById(R.id.activity_catalog_list_listview);

        mCatalogView.setOnChildClickListener(this);
        mCatalogView.setOnItemLongClickListener(this);

        mCatalogAdapter = new CatalogAdapter(mCatalogDataSource.getCategoriesCursor(), this, mCatalogView);
        mCatalogView.setAdapter(mCatalogAdapter);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    protected void onDestroy() {
        if (mCatalogDataSource != null || mCatalogDataSource.isOpen()) {
            mCatalogDataSource.close();
        }
        mCatalogAdapter.changeCursor(null);
        mCatalogAdapter = null;
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
            Toast.makeText(this, "Sharing app is not available as app is not available on Play Store.", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ExpandableListView listView = (ExpandableListView) parent;
        long pos = listView.getExpandableListPosition(position);

        final int itemType = ExpandableListView.getPackedPositionType(pos);
        final int groupPos = ExpandableListView.getPackedPositionGroup(pos);
        final int childPos = ExpandableListView.getPackedPositionChild(pos);

        boolean isItemClicked = itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD;
        boolean isCategoryClicked = itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP;
        if (isItemClicked) {
            Cursor cursor = mCatalogAdapter.getChild(groupPos, childPos);
            final Item selectedItem = CatalogDataSource.cursorToItem(cursor, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(R.array.item_options_array, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent;
                    switch (which) {
                        case 0:
                            if (!selectedItem.isUserDefined()) {
                                Toast.makeText(CatalogActivity.this, "Cannot edit predefined item!", Toast.LENGTH_SHORT).show();
                                break;
                            }

                            Cursor cursor = mCatalogAdapter.getGroup(groupPos);
                            final Category selectedCategory = CatalogDataSource.cursorToCategory(cursor);

                            intent = new Intent(CatalogActivity.this, AddItemActivity.class);
                            intent.putExtra(AddItemActivity.ITEM_KEY, selectedItem);
                            intent.putExtra(AddItemActivity.CATEGORY_KEY, selectedCategory);
                            startActivity(intent);
                            break;
                        case 1:
                            if (!selectedItem.isUserDefined()) {
                                Toast.makeText(CatalogActivity.this, "Cannot delete predefined item!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            new AlertDialog.Builder(CatalogActivity.this)
                                    .setMessage("Are you sure you want to delete item?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(CatalogActivity.this, DatabaseUpdateService.class);
                                            intent.setAction(Constants.ACTION_DELETE_ITEM);
                                            intent.putExtra(ItemViewActivity.DELETED_ITEM_KEY, selectedItem);
                                            startService(intent);
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                            break;
                        default:
                            Log.e(TAG, "Weird item was picked! Which is " + which);
                            if (BuildConfig.DEBUG) {
                                throw new RuntimeException("Weird item was picked! Which is " + which);
                            }
                            break;
                    }
                }
            });
            builder.show();

            return true;
        } else if (isCategoryClicked) {
            Cursor cursor = mCatalogAdapter.getGroup(groupPos);
            final Category selectedCategory = CatalogDataSource.cursorToCategory(cursor);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(R.array.category_options_array, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent;
                    switch (which) {
                        case 0:
                            if (!selectedCategory.isUserDefined()) {
                                Toast.makeText(CatalogActivity.this, "Cannot edit predefined category!", Toast.LENGTH_SHORT).show();
                                break;
                            }

                            intent = new Intent(CatalogActivity.this, AddCategoryActivity.class);
                            intent.putExtra(AddCategoryActivity.CATEGORY_KEY, selectedCategory);
                            startActivity(intent);
                            break;
                        case 1:
                            if (!selectedCategory.isUserDefined()) {
                                Toast.makeText(CatalogActivity.this, "Cannot delete predefined category!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            new AlertDialog.Builder(CatalogActivity.this)
                                    .setMessage("Are you sure you want to delete category?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(CatalogActivity.this, DatabaseUpdateService.class);
                                            intent.setAction(Constants.ACTION_DELETE_CATEGORY);
                                            intent.putExtra(ItemViewActivity.DELETED_CATEGORY_KEY, selectedCategory);
                                            startService(intent);
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                            break;
                        case 2:
                            intent = new Intent(CatalogActivity.this, AddItemActivity.class);
                            intent.putExtra(AddItemActivity.CATEGORY_KEY, selectedCategory);
                            startActivity(intent);
                            break;
                        case 3:
                            intent = new Intent(CatalogActivity.this, AddCategoryActivity.class);
                            startActivity(intent);
                            break;
                        default:
                            Log.e(TAG, "Weird item was picked! Which is " + which);
                            if (BuildConfig.DEBUG) {
                                throw new RuntimeException("Weird item was picked! Which is " + which);
                            }
                            break;
                    }
                }
            });
            builder.show();
            return true;
        }
        return false;
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onQueryTextChange(String query) {
        return doFilter(query);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return doFilter(query);
    }

    @Override
    public boolean onClose() {
        return doFilter("");
    }

    private boolean doFilter(String query) {
        if (TextUtils.isEmpty(query)) {
            mCatalogAdapter.setSearch(false);
        } else {
            mCatalogAdapter.setSearch(true);
        }
        filterList(query);
        if (TextUtils.isEmpty(query)) {
            collapseAll();
        } else {
            expandAll();
        }
        return true;
    }

    private void filterList(String query) {
        mSearchQuery = query;
        mCatalogAdapter.notifyDataSetChanged();
    }

    private void expandAll() {
        int count = mCatalogAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            mCatalogView.expandGroup(i);
        }
    }

    private void collapseAll() {
        int count = mCatalogAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            mCatalogView.collapseGroup(i);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, Bundle args) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreateLoader for loader_id " + id);
        }
        if (id != -1) {
            return new SimpleCursorLoader(this) {
                @Override
                public Cursor loadInBackground() {
                    if (TextUtils.isEmpty(mSearchQuery)) {
                        return mCatalogDataSource.getItemsCursor(id);
                    } else {
                        return mCatalogDataSource.getItemsCursor(id, mSearchQuery);
                    }
                }
            };
        } else {
            return new SimpleCursorLoader(this) {
                @Override
                public Cursor loadInBackground() {
                    return mCatalogDataSource.getCategoriesCursor();
                }
            };
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mCatalogAdapter == null) {
            return;
        }
        int id = loader.getId();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onLoadFinished() for loader_id " + id);
        }
        LongSparseArray<Integer> groupMap = mCatalogAdapter.getGroupMap();
        if (id != -1) {
            if (!data.isClosed()) {
                try {
                    int groupPos = groupMap.get(id);
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "data.getCount() " + data.getCount());
                    }
                    mCatalogAdapter.setChildrenCursor(groupPos, data);
                } catch (Exception e) {
                    Log.w(TAG, e);
                }
            }
        } else {
            mCatalogAdapter.setGroupCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onLoaderReset() for loader_id " + id);
        }
        if (mCatalogAdapter == null) {
            return;
        }
        if (id != -1) {
            try {
                mCatalogAdapter.setChildrenCursor(id, null);
            } catch (Exception e) {
                Log.w(TAG, e);
            }
        } else {
            if (!mCatalogDataSource.isOpen()) {
                mCatalogDataSource.open();
            }
            mCatalogAdapter.setGroupCursor(mCatalogDataSource.getCategoriesCursor());
        }
    }

    private class DatabaseUpdatedBroadcastReceiver extends BroadcastReceiver {
        private DatabaseUpdatedBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive(" + intent + ")");
            if (mCatalogAdapter == null) {
                return;
            }

            if (mCatalogDataSource == null || !mCatalogDataSource.isOpen()) {
                return;
            }

            mCatalogAdapter.setGroupCursor(mCatalogDataSource.getCategoriesCursor());
            mCatalogAdapter.notifyDataSetChanged();
        }
    }
}
