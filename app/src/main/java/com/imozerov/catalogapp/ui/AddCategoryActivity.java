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
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.utils.ImageUtils;

import java.util.ArrayList;

public class AddCategoryActivity extends ActionBarActivity {
    private static final String TAG = AddCategoryActivity.class.getName();

    public static final String CATEGORY_KEY = TAG + ".category";
    private static final int LOAD_IMAGE = 123;

    private EditText mNameField;
    private ImageView mImageField;
    private Button mDoneButton;

    private String mSelectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        mNameField = (EditText) findViewById(R.id.activity_add_category_name);
        mImageField = (ImageView) findViewById(R.id.activity_add_category_image);
        mDoneButton = (Button) findViewById(R.id.activity_add_category_done_button);

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUserInputValid()) {
                    return;
                }
                Category category = new Category(mNameField.getText().toString(), mSelectedImagePath, new ArrayList<Item>(), true);

                Intent intent = new Intent();
                intent.putExtra(CATEGORY_KEY, category);
                setResult(RESULT_OK, intent);
                finish();
                Log.i(TAG, "Created new category " + category);
            }
        });

        mImageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE);
            }
        });

        if (mSelectedImagePath != null) {
            mImageField.setImageBitmap(ImageUtils.createBigImageBitmap(mSelectedImagePath));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            String picturePath = ImageUtils.getImagePath(this, data);
            mSelectedImagePath = picturePath;
            Log.i(TAG, "New image path is " + mSelectedImagePath);
            mImageField.setImageBitmap(ImageUtils.createBigImageBitmap(mSelectedImagePath));
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
}
