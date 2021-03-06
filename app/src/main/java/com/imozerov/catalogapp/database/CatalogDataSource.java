package com.imozerov.catalogapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.imozerov.catalogapp.BuildConfig;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.utils.FixedSizeArrayList;
import com.imozerov.catalogapp.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imozerov on 24.03.2015.
 */
public class CatalogDataSource {
    private static final String TAG = CatalogDataSource.class.getName();
    private SQLiteDatabase mDatabase;
    private MySQLiteOpenHelper mOpenHelper;
    private String[] allCategoriesColumns = {
            MySQLiteOpenHelper.CATEGORIES_COLUMN_ID,
            MySQLiteOpenHelper.CATEGORIES_COLUMN_NAME,
            MySQLiteOpenHelper.CATEGORIES_COLUMN_IMAGE,
            MySQLiteOpenHelper.CATEGORIES_COLUMN_IS_USER_DEFINED
    };

    private String[] allItemsColumns = {
            MySQLiteOpenHelper.ITEMS_COLUMN_ID,
            MySQLiteOpenHelper.ITEMS_COLUMN_CATEGORY_ID,
            MySQLiteOpenHelper.ITEMS_COLUMN_DESCRIPTION,
            MySQLiteOpenHelper.ITEMS_COLUMN_IMAGE_1,
            MySQLiteOpenHelper.ITEMS_COLUMN_NAME,
            MySQLiteOpenHelper.ITEMS_COLUMN_IS_USER_DEFINED,

    };

    public CatalogDataSource(Context context) {
        mOpenHelper = new MySQLiteOpenHelper(context);
    }

    public static Item cursorToItem(Cursor cursor, Category category) {
        if (cursor == null) {
            return null;
        }

        Item item = new Item();
        item.setUserDefined(cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.ITEMS_COLUMN_IS_USER_DEFINED)) == 1 ? true : false);
        item.setId(cursor.getLong(cursor.getColumnIndex(MySQLiteOpenHelper.ITEMS_COLUMN_ID)));
        item.setName(cursor.getString(cursor.getColumnIndex(MySQLiteOpenHelper.ITEMS_COLUMN_NAME)));
        item.setDescription(cursor.getString(cursor.getColumnIndex(MySQLiteOpenHelper.ITEMS_COLUMN_DESCRIPTION)));
        item.setCategory(category);

        FixedSizeArrayList itemsImages = new FixedSizeArrayList(4);

        itemsImages.add(cursor.getString(cursor.getColumnIndex(MySQLiteOpenHelper.ITEMS_COLUMN_IMAGE_1)));
        itemsImages.add(cursor.getString(cursor.getColumnIndex(MySQLiteOpenHelper.ITEMS_COLUMN_IMAGE_2)));
        itemsImages.add(cursor.getString(cursor.getColumnIndex(MySQLiteOpenHelper.ITEMS_COLUMN_IMAGE_3)));
        itemsImages.add(cursor.getString(cursor.getColumnIndex(MySQLiteOpenHelper.ITEMS_COLUMN_IMAGE_4)));

        if (!itemsImages.isEmpty()) {
            item.setImages(itemsImages);
        }

        return item;
    }

    public static Category cursorToCategory(Cursor cursor) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "cursor is " + cursor);
        }
        if (cursor == null) {
            return null;
        }
        Category category = new Category();
        category.setId(cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.CATEGORIES_COLUMN_ID)));
        category.setImage(ImageUtils.getImage(cursor.getBlob(cursor.getColumnIndex(MySQLiteOpenHelper.CATEGORIES_COLUMN_IMAGE))));
        category.setName(cursor.getString(cursor.getColumnIndex(MySQLiteOpenHelper.CATEGORIES_COLUMN_NAME)));
        category.setUserDefined(cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.CATEGORIES_COLUMN_IS_USER_DEFINED)) == 1 ? true : false);
        return category;
    }

    public void open() throws SQLException {
        mDatabase = mOpenHelper.getWritableDatabase();
    }

    public boolean isOpen() {
        return mDatabase.isOpen();
    }

    public void close() {
        mOpenHelper.close();
    }

    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();

        Cursor cursor = mDatabase.query(MySQLiteOpenHelper.TABLE_CATEGORIES,
                allCategoriesColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category category = cursorToCategory(cursor);
            categories.add(category);
            cursor.moveToNext();
        }
        cursor.close();
        return categories;
    }

    public Item getItem(long itemId) {
        String query = "select * from " + MySQLiteOpenHelper.TABLE_ITEMS + " where " + MySQLiteOpenHelper.ITEMS_COLUMN_ID + " = " + itemId;
        Cursor cursor = mDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        Item item = cursorToItem(cursor, null);
        cursor.close();
        return item;
    }

    public Category getCategory(long categoryId) {
        String query = "select * from " + MySQLiteOpenHelper.TABLE_CATEGORIES + " where " + MySQLiteOpenHelper.CATEGORIES_COLUMN_ID + " = " + categoryId;
        Cursor cursor = mDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        Category category = cursorToCategory(cursor);
        cursor.close();
        return category;
    }

    public Cursor getItemsCursor(int categoryId) {
        String query = "select * from " + MySQLiteOpenHelper.TABLE_ITEMS
                + " where " + MySQLiteOpenHelper.ITEMS_COLUMN_CATEGORY_ID + " = '" + categoryId + "'";
        return mDatabase.rawQuery(query, null);
    }

    public Cursor getItemsCursor(int categoryId, CharSequence constraint) {
        String query = "select * from " + MySQLiteOpenHelper.TABLE_ITEMS
                + " where " + MySQLiteOpenHelper.ITEMS_COLUMN_NAME
                + " LIKE '%" + constraint
                + "%' AND " + MySQLiteOpenHelper.ITEMS_COLUMN_CATEGORY_ID + " = '" + categoryId + "'";
        return mDatabase.rawQuery(query, null);
    }

    public Cursor getCategoriesCursor() {
        String query = "select * from " + MySQLiteOpenHelper.TABLE_CATEGORIES;
        return mDatabase.rawQuery(query, null);
    }

    public void addItem(Item item) {
        if (item == null) {
            Log.e(TAG, "Null item passed");
            return;
        }
        ContentValues values = new ContentValues();
        if (item.getId() != 0) {
            values.put(MySQLiteOpenHelper.CATEGORIES_COLUMN_ID, item.getId());
        }
        values.put(MySQLiteOpenHelper.ITEMS_COLUMN_CATEGORY_ID, item.getCategory().getId());
        values.put(MySQLiteOpenHelper.ITEMS_COLUMN_DESCRIPTION, item.getDescription());
        if (item.getImages() != null) {
            int i = 1;
            for (String imagePath : item.getImages()) {
                values.put(MySQLiteOpenHelper.ITEMS_COLUMN_IMAGE_X + i, imagePath);
                i++;
            }
        }

        values.put(MySQLiteOpenHelper.ITEMS_COLUMN_IS_USER_DEFINED, item.isUserDefined() ? 1 : 0);
        values.put(MySQLiteOpenHelper.ITEMS_COLUMN_NAME, item.getName());
        mDatabase.insertWithOnConflict(MySQLiteOpenHelper.TABLE_ITEMS, null,
                values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void addCategory(Category category) {
        if (category == null) {
            Log.e(TAG, "Null category passed");
            return;
        }
        ContentValues values = new ContentValues();
        if (category.getId() != 0) {
            values.put(MySQLiteOpenHelper.CATEGORIES_COLUMN_ID, category.getId());
        }
        values.put(MySQLiteOpenHelper.CATEGORIES_COLUMN_IMAGE, ImageUtils.getBytes(category.getImage()));
        values.put(MySQLiteOpenHelper.CATEGORIES_COLUMN_NAME, category.getName());
        values.put(MySQLiteOpenHelper.CATEGORIES_COLUMN_IS_USER_DEFINED, category.isUserDefined() ? 1 : 0);

        mDatabase.insertWithOnConflict(MySQLiteOpenHelper.TABLE_CATEGORIES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deleteItem(Item item) {
        if (item == null) {
            Log.e(TAG, "Null item passed");
            return;
        }
        long id = item.getId();
        Log.i(TAG, "Comment deleted with id: " + id);
        mDatabase.delete(MySQLiteOpenHelper.TABLE_ITEMS, MySQLiteOpenHelper.ITEMS_COLUMN_ID
                + " = " + id, null);
    }

    public void deleteCategory(Category category) {
        if (category == null) {
            Log.e(TAG, "Null category passed");
            return;
        }
        long id = category.getId();
        Log.i(TAG, "Comment deleted with id: " + id);
        mDatabase.delete(MySQLiteOpenHelper.TABLE_CATEGORIES, MySQLiteOpenHelper.CATEGORIES_COLUMN_ID
                + " = " + id, null);
    }
}
