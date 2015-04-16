package com.imozerov.catalogapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by imozerov on 24.03.2015.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String TABLE_CATEGORIES = "categories";
    public static final String CATEGORIES_COLUMN_ID = "_id";
    public static final String CATEGORIES_COLUMN_IMAGE = "image";
    public static final String CATEGORIES_COLUMN_NAME = "name";
    public static final String CATEGORIES_COLUMN_IS_USER_DEFINED = "is_user_defined";
    public static final String CREATE_TABLE_CATEGORIES = "create table "
            + TABLE_CATEGORIES + "(" + CATEGORIES_COLUMN_ID
            + " integer primary key autoincrement, " + CATEGORIES_COLUMN_IMAGE
            + " blob, " + CATEGORIES_COLUMN_NAME + " text, "
            + CATEGORIES_COLUMN_IS_USER_DEFINED + " integer" +
            ");";
    public static final String TABLE_ITEMS = "items";
    public static final String ITEMS_COLUMN_ID = "_id";
    public static final String ITEMS_COLUMN_IMAGE_X = "image";
    public static final String ITEMS_COLUMN_IMAGE_1 = "image1";
    public static final String ITEMS_COLUMN_IMAGE_2 = "image2";
    public static final String ITEMS_COLUMN_IMAGE_3 = "image3";
    public static final String ITEMS_COLUMN_IMAGE_4 = "image4";
    public static final String ITEMS_COLUMN_NAME = "name";
    public static final String ITEMS_COLUMN_DESCRIPTION = "description";
    public static final String ITEMS_COLUMN_CATEGORY_ID = "category";
    public static final String ITEMS_COLUMN_IS_USER_DEFINED = "is_user_defined";

    public static final String CREATE_TABLE_ITMES = "create table "
            + TABLE_ITEMS + "("
            + ITEMS_COLUMN_ID + " integer primary key autoincrement, "
            + ITEMS_COLUMN_IMAGE_1 + " text, "
            + ITEMS_COLUMN_IMAGE_2 + " text, "
            + ITEMS_COLUMN_IMAGE_3 + " text, "
            + ITEMS_COLUMN_IMAGE_4 + " text, "
            + ITEMS_COLUMN_NAME + " text, "
            + ITEMS_COLUMN_DESCRIPTION + " text, "
            + ITEMS_COLUMN_CATEGORY_ID + " integer, "
            + ITEMS_COLUMN_IS_USER_DEFINED + " integer" +
            ");";
    private static final String TAG = MySQLiteOpenHelper.class.getName();
    private static final String DATABASE_NAME = "catalog.db";
    private static final int DATABASE_VERSION = 2;

    public MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_ITMES);
        addCategoriesAndItems(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG,
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    private void addCategoriesAndItems(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        //CATEGORY 1
        values.put(MySQLiteOpenHelper.CATEGORIES_COLUMN_NAME, "Category 1");
        // IS_USER_DEFINED
        values.put(MySQLiteOpenHelper.CATEGORIES_COLUMN_IS_USER_DEFINED, 0);
        // IMAGE IF NEEDED. NEED TO CREATE BITMAP ALSO
        // values.put(MySQLiteOpenHelper.CATEGORIES_COLUMN_IMAGE, new Bitmap());
        db.insert(MySQLiteOpenHelper.TABLE_CATEGORIES, null, values);

        // ITEM 1
        values = new ContentValues();
        // BELONG TO CATEGORY 1
        values.put(MySQLiteOpenHelper.ITEMS_COLUMN_CATEGORY_ID, 1);
        // HAS DESCRIPTION "Test test"
        values.put(MySQLiteOpenHelper.ITEMS_COLUMN_DESCRIPTION, "Test test");
        // IS_USER_DEFIENED
        values.put(MySQLiteOpenHelper.ITEMS_COLUMN_IS_USER_DEFINED, 0);
        // ITEM NAME
        values.put(MySQLiteOpenHelper.ITEMS_COLUMN_NAME, "Item 1");
        // Item image if needed. image path should be string representing path to image file on device.
        // values.put(MySQLiteOpenHelper.ITEMS_COLUMN_IMAGE_1, item_image_path);
        db.insert(MySQLiteOpenHelper.TABLE_ITEMS, null,
                values);

        //CATEGORY 2
        values = new ContentValues();
        // NAME
        values.put(MySQLiteOpenHelper.CATEGORIES_COLUMN_NAME, "Category 2");
        // IS_USER_DEFINED
        values.put(MySQLiteOpenHelper.CATEGORIES_COLUMN_IS_USER_DEFINED, 0);
        // IMAGE IF NEEDED. NEED TO CREATE BITMAP ALSO
        // values.put(MySQLiteOpenHelper.CATEGORIES_COLUMN_IMAGE, new Bitmap());
        db.insert(MySQLiteOpenHelper.TABLE_CATEGORIES, null, values);

        // ITEM 2
        values = new ContentValues();
        // BELONG TO CATEGORY 2
        values.put(MySQLiteOpenHelper.ITEMS_COLUMN_CATEGORY_ID, 2);
        // HAS DESCRIPTION "Test test"
        values.put(MySQLiteOpenHelper.ITEMS_COLUMN_DESCRIPTION, "Test test");
        // IS_USER_DEFIENED
        values.put(MySQLiteOpenHelper.ITEMS_COLUMN_IS_USER_DEFINED, 0);
        // ITEM NAME
        values.put(MySQLiteOpenHelper.ITEMS_COLUMN_NAME, "Item 2");
        db.insert(MySQLiteOpenHelper.TABLE_ITEMS, null,
                values);
    }
}
