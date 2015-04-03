package com.imozerov.catalogapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.imozerov.catalogapp.database.CatalogDataSource;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.ui.AddCategoryActivity;
import com.imozerov.catalogapp.ui.AddItemActivity;
import com.imozerov.catalogapp.ui.ItemViewActivity;
import com.imozerov.catalogapp.utils.Constants;
import com.imozerov.catalogapp.utils.ImageUtils;

/**
 * Created by imozerov on 27.03.2015.
 */
public class DatabaseUpdateService extends IntentService {
    private final static String TAG = DatabaseUpdateService.class.getName();

    private CatalogDataSource mCatalogDataSource;

    public DatabaseUpdateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent(" + intent + ")");
        if (intent == null || intent.getAction() == null) {
            return;
        }

        if (mCatalogDataSource == null) {
            mCatalogDataSource = new CatalogDataSource(this);
        }

        mCatalogDataSource.open();

        String action = intent.getAction();
        if (action.equals(Constants.ACTION_ADD_ITEM)) {
            Item newItem = intent.getParcelableExtra(AddItemActivity.ITEM_KEY);
            String imagePath = intent.getStringExtra(AddItemActivity.ITEM_IMAGE_PATH);
            if (!TextUtils.isEmpty(imagePath)) {
                newItem.setImage(ImageUtils.createBigImageBitmap(imagePath));
            }
            mCatalogDataSource.addItem(newItem);
            Log.i(TAG, "Item " + newItem + " saved into db.");
        } else if (action.equals(Constants.ACTION_ADD_CATEGORY)) {
            Category category = intent.getParcelableExtra(AddCategoryActivity.CATEGORY_KEY);
            String imagePath = intent.getStringExtra(AddCategoryActivity.CATEGORY_IMAGE_PATH);
            if (!TextUtils.isEmpty(imagePath)) {
                category.setImage(ImageUtils.createSmallImageBitmap(imagePath));
            }
            mCatalogDataSource.addCategory(category);
            Log.i(TAG, "Category " + category + " saved into db.");
        } else if (action.equals(Constants.ACTION_DELETE_ITEM)) {
            Item deletedItem = intent.getParcelableExtra(ItemViewActivity.DELETED_ITEM_KEY);
            mCatalogDataSource.deleteItem(deletedItem);
            Log.i(TAG, "Item " + deletedItem + " deleted from db.");
        } else if (action.equals(Constants.ACTION_DELETE_CATEGORY)) {
            Category deletedCategory = intent.getParcelableExtra(ItemViewActivity.DELETED_CATEGORY_KEY);
            mCatalogDataSource.deleteCategory(deletedCategory);
            Log.i(TAG, "Item " + deletedCategory + " deleted from db.");
        }
        mCatalogDataSource.close();

        Intent localIntent = new Intent(Constants.BROADCAST_ACTION_DATABASE_UPDATED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
