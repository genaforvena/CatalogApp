package com.imozerov.catalogapp.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.imozerov.catalogapp.BuildConfig;
import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.database.CatalogDataSource;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.services.DatabaseUpdateService;
import com.imozerov.catalogapp.utils.Constants;
import com.imozerov.catalogapp.utils.FixedSizeArrayList;
import com.imozerov.catalogapp.utils.ImageUtils;
import com.imozerov.catalogapp.utils.LoadImageBitmapAsyncTask;

import java.util.List;

public class AddItemActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = AddItemActivity.class.getName();
    public static final String ITEM_KEY = TAG + ".item";
    public static final String CATEGORY_KEY = TAG + ".category";
    private static final int LOAD_IMAGE_1 = 123;
    private static final int LOAD_IMAGE_2 = 223;
    private static final int LOAD_IMAGE_3 = 323;
    private static final int LOAD_IMAGE_4 = 423;
    private static final String IMAGE1 = TAG + ".image1";
    private static final String IMAGE2 = TAG + ".image2";
    private static final String IMAGE3 = TAG + ".image3";
    private static final String IMAGE4 = TAG + ".image4";
    private static final String CATEGORY = TAG + ".category";
    private EditText mNameField;
    private EditText mDescriptionField;
    private Spinner mCategorySpinner;
    private Button mDoneButton;
    private ImageView mImageField1;
    private ImageView mImageField2;
    private ImageView mImageField3;
    private ImageView mImageField4;
    private CatalogDataSource mDatabase;

    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mNameField = (EditText) findViewById(R.id.activity_add_item_name);
        mDescriptionField = (EditText) findViewById(R.id.activity_add_item_description);
        mImageField1 = (ImageView) findViewById(R.id.activity_add_item_image1);
        mImageField2 = (ImageView) findViewById(R.id.activity_add_item_image2);
        mImageField3 = (ImageView) findViewById(R.id.activity_add_item_image3);
        mImageField4 = (ImageView) findViewById(R.id.activity_add_item_image4);
        mCategorySpinner = (Spinner) findViewById(R.id.activity_add_item_category);
        mDoneButton = (Button) findViewById(R.id.activity_add_item_done_button);

        mDatabase = new CatalogDataSource(this);

        mDatabase.open();
        List<Category> categoryList = mDatabase.getCategories();
        mDatabase.close();

        ArrayAdapter<Category> spinnerArrayAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, categoryList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(spinnerArrayAdapter);

        mImageField1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE_1);
            }
        });

        mImageField2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE_2);
            }
        });

        mImageField3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE_3);
            }
        });

        mImageField4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE_4);
            }
        });

        mDoneButton.setOnClickListener(this);

        if (savedInstanceState != null) {
            // Activity was recreated.
            new LoadImageBitmapAsyncTask(mImageField1).execute(savedInstanceState.getString(IMAGE1));
            new LoadImageBitmapAsyncTask(mImageField2).execute(savedInstanceState.getString(IMAGE2));
            new LoadImageBitmapAsyncTask(mImageField3).execute(savedInstanceState.getString(IMAGE3));
            new LoadImageBitmapAsyncTask(mImageField4).execute(savedInstanceState.getString(IMAGE4));
            mCategorySpinner.setSelection(savedInstanceState.getInt(CATEGORY));
        } else {
            // Activity is newly created
            Item editItem = getIntent().getParcelableExtra(ITEM_KEY);
            Category categoryToAdd = getIntent().getParcelableExtra(CATEGORY_KEY);
            if (editItem != null) {
                mItem = editItem;
                mNameField.setText(mItem.getName());
                mDescriptionField.setText(mItem.getDescription());

                if (mItem.getImages() != null && !mItem.getImages().isEmpty()) {
                    int imagesSize = mItem.getImages().size();
                    if (imagesSize == 1) {
                        new LoadImageBitmapAsyncTask(mImageField1).execute(mItem.getImages().get(0));
                    } else if (imagesSize == 2) {
                        new LoadImageBitmapAsyncTask(mImageField1).execute(mItem.getImages().get(0));
                        new LoadImageBitmapAsyncTask(mImageField2).execute(mItem.getImages().get(1));
                    } else if (imagesSize == 3) {
                        new LoadImageBitmapAsyncTask(mImageField1).execute(mItem.getImages().get(0));
                        new LoadImageBitmapAsyncTask(mImageField2).execute(mItem.getImages().get(1));
                        new LoadImageBitmapAsyncTask(mImageField3).execute(mItem.getImages().get(2));
                    } else if (imagesSize == 4) {
                        new LoadImageBitmapAsyncTask(mImageField1).execute(mItem.getImages().get(0));
                        new LoadImageBitmapAsyncTask(mImageField2).execute(mItem.getImages().get(1));
                        new LoadImageBitmapAsyncTask(mImageField3).execute(mItem.getImages().get(2));
                        new LoadImageBitmapAsyncTask(mImageField4).execute(mItem.getImages().get(3));
                    }
                }
            }
            if (categoryToAdd != null) {
                mCategorySpinner.setSelection(indexOf(categoryList, categoryToAdd));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(IMAGE1, (String) mImageField1.getTag());
        savedInstanceState.putString(IMAGE2, (String) mImageField2.getTag());
        savedInstanceState.putString(IMAGE3, (String) mImageField3.getTag());
        savedInstanceState.putString(IMAGE4, (String) mImageField4.getTag());
        savedInstanceState.putInt(CATEGORY, mCategorySpinner.getSelectedItemPosition());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String picturePath = ImageUtils.getImagePath(this, data);
            switch (requestCode) {
                case LOAD_IMAGE_1:
                    new LoadImageBitmapAsyncTask(mImageField1).execute(picturePath);
                    break;
                case LOAD_IMAGE_2:
                    new LoadImageBitmapAsyncTask(mImageField2).execute(picturePath);
                    break;
                case LOAD_IMAGE_3:
                    new LoadImageBitmapAsyncTask(mImageField3).execute(picturePath);
                    break;
                case LOAD_IMAGE_4:
                    new LoadImageBitmapAsyncTask(mImageField4).execute(picturePath);
                    break;
                default:
                    if (BuildConfig.DEBUG) {
                        throw new RuntimeException("unknown request code " + requestCode);
                    }
                    Log.e(TAG, "unknown request code " + requestCode);
            }
        }
    }

    private boolean isUserInputValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(mNameField.getText().toString())) {
            mNameField.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        if (TextUtils.isEmpty(mDescriptionField.getText().toString())) {
            mDescriptionField.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onClick(View v) {
        if (!isUserInputValid()) {
            return;
        }
        Category itemsCategory = (Category) mCategorySpinner.getSelectedItem();

        Item newItem;
        if (mItem == null) {
            newItem = new Item();
        } else {
            newItem = mItem;
            newItem.setId(mItem.getId());
        }
        newItem.setName(mNameField.getText().toString());
        newItem.setCategory(itemsCategory);
        newItem.setDescription(mDescriptionField.getText().toString());
        newItem.setUserDefined(true);

        FixedSizeArrayList itemsImages = new FixedSizeArrayList(4);
        itemsImages.add((String) mImageField1.getTag());
        itemsImages.add((String) mImageField2.getTag());
        itemsImages.add((String) mImageField3.getTag());
        itemsImages.add((String) mImageField4.getTag());
        newItem.setImages(itemsImages);

        Intent intent = new Intent(this, DatabaseUpdateService.class);
        intent.setAction(Constants.ACTION_ADD_ITEM);
        intent.putExtra(ITEM_KEY, newItem);
        startService(intent);

        startActivity(new Intent(this, CatalogActivity.class));
        finish();
        Log.i(TAG, "Created new item " + newItem);
    }

    private static <T> int indexOf(List<T> source, T target) {
        for (int i = 0; i < source.size(); i++) {
            if (source.get(i).equals(target)) {
                return i;
            }
        }
        return 0;
    }
}
