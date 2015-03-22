package com.imozerov.catalogapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.imozerov.catalogapp.BuildConfig;
import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.database.RuntimeDatabase;
import com.imozerov.catalogapp.ui.adapters.CatalogExpandableListViewAdapter;


public class CatalogExpandableListActivity extends ActionBarActivity {
    private final static String TAG = CatalogExpandableListActivity.class.getName();
    public static final int REQUEST_CODE_ADD_ITEM = 112;

    private ExpandableListView mExpandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_list);
        mExpandableListView = (ExpandableListView) findViewById(R.id.activity_catalog_list_listview);

        CatalogExpandableListViewAdapter catalogExpandableListViewAdapter = new CatalogExpandableListViewAdapter(this, RuntimeDatabase.getInstance().getCategories());
        mExpandableListView.setAdapter(catalogExpandableListViewAdapter);
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
            Toast.makeText(this, "Adding category", Toast.LENGTH_SHORT).show();
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
}
