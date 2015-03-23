package com.imozerov.catalogapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.imozerov.catalogapp.database.RuntimeDatabase;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.utils.ImageUtils;

public class AddItemActivity extends ActionBarActivity {
    private static final String TAG = AddItemActivity.class.getName();

    public static final String CATEGORY_KEY = TAG + ".category";
    public static final String ITEM_KEY = TAG + ".item";
    private static final int LOAD_IMAGE = 123;

    private EditText mNameField;
    private EditText mDescriptionField;
    private Spinner mCategorySpinner;
    private Button mDoneButton;
    private ImageView mImageField;

    private String mSelectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mNameField = (EditText) findViewById(R.id.activity_add_item_name);
        mDescriptionField = (EditText) findViewById(R.id.activity_add_item_description);
        mImageField = (ImageView) findViewById(R.id.activity_add_item_image);
        mCategorySpinner = (Spinner) findViewById(R.id.activity_add_item_category);
        mDoneButton = (Button) findViewById(R.id.activity_add_item_done_button);

        ArrayAdapter<Category> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, RuntimeDatabase.getInstance().getCategories());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(spinnerArrayAdapter);

        if (mSelectedImageUri != null) {
            mImageField.setImageBitmap(BitmapFactory.decodeFile(mSelectedImageUri));
        }

        mImageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE);
            }
        });

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUserInputValid()) {
                    return;
                }
                Item newItem = new Item(mNameField.getText().toString(), true, mDescriptionField.getText().toString(), mSelectedImageUri);
                Category itemsCategory = (Category) mCategorySpinner.getSelectedItem();

                Intent intent = new Intent();
                intent.putExtra(CATEGORY_KEY, itemsCategory);
                intent.putExtra(ITEM_KEY, newItem);
                setResult(RESULT_OK, intent);
                finish();
                Log.i(TAG, "Created new item " + newItem);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            String picturePath = ImageUtils.getImagePath(this, data);
            mSelectedImageUri = picturePath;
            Log.i(TAG, "New image path is " + mSelectedImageUri);
            mImageField.setImageBitmap(ImageUtils.createBigImageBitmap(mSelectedImageUri));
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
}
