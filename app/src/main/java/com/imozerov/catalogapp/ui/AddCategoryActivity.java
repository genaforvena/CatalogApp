package com.imozerov.catalogapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.database.CatalogDataSource;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.services.DatabaseUpdateService;
import com.imozerov.catalogapp.utils.Constants;
import com.imozerov.catalogapp.utils.ImageUtils;
import com.imozerov.catalogapp.utils.LoadImageBitmapAsyncTask;

import java.lang.ref.WeakReference;

public class AddCategoryActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = AddCategoryActivity.class.getName();

    public static final String CATEGORY_KEY = TAG + ".category";
    public static final String CATEGORY_IMAGE_PATH = TAG + ".category_image_path";
    private static final String IMAGE = TAG + ".image";
    private static final int LOAD_IMAGE = 123;
    private EditText mNameField;
    private ImageView mImageField;
    private Button mDoneButton;
    private CatalogDataSource mCatalogDataSource;

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

        if (savedInstanceState != null) {
            new LoadImageBitmapAsyncTask(mImageField).execute((String) savedInstanceState.getString(IMAGE));
        } else {
            Category editCategory = getIntent().getParcelableExtra(CATEGORY_KEY);
            if (editCategory != null) {
                mCatalogDataSource = new CatalogDataSource(this);
                mCategory = editCategory;
                mNameField.setText(editCategory.getName());
                new LoadCategoryFromDbTask(
                        new CategoryLoadObserver() {
                            @Override
                            public void onCategoryLoaded(Category category) {
                                if (category.getImage() != null) {
                                    mImageField.setImageBitmap(category.getImage());
                                }
                            }
                        }).execute(editCategory.getId());
            }

            if (mImageField.getTag() != null) {
                new LoadImageBitmapAsyncTask(mImageField).execute((String) mImageField.getTag());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(IMAGE, (String) mImageField.getTag());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            String picturePath = ImageUtils.getImagePath(this, data);
            new LoadImageBitmapAsyncTask(mImageField).execute(picturePath);
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
        if (!TextUtils.isEmpty((String) mImageField.getTag())) {
            intent.putExtra(CATEGORY_IMAGE_PATH, (String) mImageField.getTag());
        }
        startService(intent);

        startActivity(new Intent(this, CatalogActivity.class));
        finish();
        Log.i(TAG, "Created new category " + mCategory);
    }

    private interface CategoryLoadObserver {
        void onCategoryLoaded(Category category);
    }

    private class LoadCategoryFromDbTask extends AsyncTask<Long, Void, Category> {
        private final WeakReference<CategoryLoadObserver> mObserver;

        private LoadCategoryFromDbTask(CategoryLoadObserver observer) {
            mObserver = new WeakReference<>(observer);
        }

        @Override
        protected Category doInBackground(Long... categoryIds) {
            if (categoryIds == null) {
                return null;
            }
            mCatalogDataSource.open();
            Category category = mCatalogDataSource.getCategory(categoryIds[0]);
            mCatalogDataSource.close();
            return category;
        }

        @Override
        protected void onPostExecute(Category category) {
            if (category == null) {
                return;
            }
            CategoryLoadObserver observer = mObserver.get();
            if (observer != null) {
                observer.onCategoryLoaded(category);
            }
        }
    }
}
