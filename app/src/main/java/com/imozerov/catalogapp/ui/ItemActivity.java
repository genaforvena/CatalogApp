package com.imozerov.catalogapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.models.Item;

public class ItemActivity extends ActionBarActivity {
    private static final String TAG = ItemActivity.class.getName();

    public static final String ITEM_KEY = TAG + ".item";

    private TextView mItemName;
    private ImageView mItemImage;
    private TextView mItemDescription;
    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        mItem = (Item) getIntent().getParcelableExtra(ITEM_KEY);

        mItemName = (TextView) findViewById(R.id.activity_item_name);
        mItemImage = (ImageView) findViewById(R.id.activity_item_image);
        mItemDescription = (TextView) findViewById(R.id.activity_item_description);

        mItemName.setText(mItem.name);
        mItemDescription.setText(mItem.description);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mItem.isUserDefined) {
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
            Toast.makeText(this, "Deleting item!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_report) {
            Toast.makeText(this, "Reporting item!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
