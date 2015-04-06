package com.imozerov.catalogapp.ui;

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

import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.database.CatalogDataSource;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.services.DatabaseUpdateService;
import com.imozerov.catalogapp.utils.Constants;
import com.imozerov.catalogapp.utils.ImageUtils;
import com.imozerov.catalogapp.utils.LoadImageBitmapAsyncTask;

import java.util.List;

public class AddItemActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = AddItemActivity.class.getName();

    public static final String ITEM_KEY = TAG + ".item";
    public static final String CATEGORY_KEY = TAG + ".category";
    public static final String ITEM_IMAGE_PATH = TAG + ".was_image_added";
    private static final int LOAD_IMAGE = 123;
    private EditText mNameField;
    private EditText mDescriptionField;
    private Spinner mCategorySpinner;
    private Button mDoneButton;
    private ImageView mImageField;
    private CatalogDataSource mDatabase;

    private String mImagePath;
    private Item mItem;

    private static <T> int indexOf(List<T> source, T target) {
        for (int i = 0; i < source.size(); i++) {
            if (source.get(i).equals(target)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mNameField = (EditText) findViewById(R.id.activity_add_item_name);
        mDescriptionField = (EditText) findViewById(R.id.activity_add_item_description);
        mImageField = (ImageView) findViewById(R.id.activity_add_item_image);
        mCategorySpinner = (Spinner) findViewById(R.id.activity_add_item_category);
        mDoneButton = (Button) findViewById(R.id.activity_add_item_done_button);

        mDatabase = new CatalogDataSource(this);

        mDatabase.open();
        List<Category> categoryList = mDatabase.getCategories();
        ArrayAdapter<Category> spinnerArrayAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, categoryList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(spinnerArrayAdapter);

        mDatabase.close();
        if (mImagePath != null) {
            new LoadImageBitmapAsyncTask(mImageField).execute(mImagePath);
        }

        mImageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE);
            }
        });

        mDoneButton.setOnClickListener(this);

        Item editItem = getIntent().getParcelableExtra(ITEM_KEY);
        Category categoryToAdd = getIntent().getParcelableExtra(CATEGORY_KEY);
        if (editItem != null) {
            mItem = editItem;
            mNameField.setText(mItem.getName());
            mDescriptionField.setText(mItem.getDescription());
            mCategorySpinner.setSelection(indexOf(categoryList, mItem.getCategory()));
            if (mItem.getImage() != null) {
                mImageField.setImageBitmap(mItem.getImage());
            }
        } else if (categoryToAdd != null) {
            mCategorySpinner.setSelection(indexOf(categoryList, categoryToAdd));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            String picturePath = ImageUtils.getImagePath(this, data);
            Log.i(TAG, "New image path is " + picturePath);
            mImagePath = picturePath;
            new LoadImageBitmapAsyncTask(mImageField).execute(mImagePath);
            ;
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

        Intent intent = new Intent(this, DatabaseUpdateService.class);
        intent.setAction(Constants.ACTION_ADD_ITEM);
        intent.putExtra(ITEM_KEY, newItem);
        if (!TextUtils.isEmpty(mImagePath)) {
            intent.putExtra(ITEM_IMAGE_PATH, mImagePath);
        }
        startService(intent);

        startActivity(new Intent(this, CatalogActivity.class));
        finish();
        Log.i(TAG, "Created new item " + newItem);
    }
}
