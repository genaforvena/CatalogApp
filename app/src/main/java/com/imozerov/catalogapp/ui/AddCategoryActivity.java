package com.imozerov.catalogapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.services.DatabaseUpdateService;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.utils.Constants;
import com.imozerov.catalogapp.utils.ImageUtils;
import com.imozerov.catalogapp.utils.LoadImageBitmapAsyncTask;

public class AddCategoryActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = AddCategoryActivity.class.getName();

    public static final String CATEGORY_KEY = TAG + ".category";
    private static final int LOAD_IMAGE = 123;
    public static final String CATEGORY_IMAGE_PATH = TAG + ".category_image_path";

    private EditText mNameField;
    private ImageView mImageField;
    private Button mDoneButton;

    private String mImagePath;
    private Category mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        mNameField = (EditText) findViewById(R.id.activity_add_category_name);
        mImageField = (ImageView) findViewById(R.id.activity_add_category_image);
        mDoneButton = (Button) findViewById(R.id.activity_add_category_done_button);

        mDoneButton.setOnClickListener(this);

        mImageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE);
            }
        });

        Category editCategory = getIntent().getParcelableExtra(CATEGORY_KEY);

        if (editCategory != null) {
            mCategory = editCategory;
            mNameField.setText(editCategory.getName());
            if (mCategory.getImage() != null) {
                mImageField.setImageBitmap(editCategory.getImage());
            }
        }

        if (mImagePath != null) {
            new LoadImageBitmapAsyncTask(mImageField).execute(mImagePath);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            String picturePath = ImageUtils.getImagePath(this, data);
            mImagePath = picturePath;
            Log.i(TAG, "New image path is " + mImagePath);
            new LoadImageBitmapAsyncTask(mImageField).execute(mImagePath);
        }
    }

    private boolean isUserInputValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(mNameField.getText().toString())) {
            mNameField.setError(getString(R.string.error_required_field));
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onClick(View v) {
        if (!isUserInputValid()) {
            return;
        }
        if (mCategory == null) {
            mCategory = new Category();
        }

        mCategory.setName(mNameField.getText().toString());
        mCategory.setUserDefined(true);

        Intent intent = new Intent(this, DatabaseUpdateService.class);
        intent.setAction(Constants.ACTION_ADD_CATEGORY);
        intent.putExtra(CATEGORY_KEY, mCategory);
        if (!TextUtils.isEmpty(mImagePath)) {
            intent.putExtra(CATEGORY_IMAGE_PATH, mImagePath);
        }
        startService(intent);

        startActivity(new Intent(this, CatalogActivity.class));
        finish();
        Log.i(TAG, "Created new category " + mCategory);
    }
}
