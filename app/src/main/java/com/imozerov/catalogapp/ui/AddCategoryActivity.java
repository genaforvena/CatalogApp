package com.imozerov.catalogapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.database.RuntimeDatabase;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;

import java.util.ArrayList;

public class AddCategoryActivity extends ActionBarActivity {
    private static final String TAG = AddCategoryActivity.class.getName();

    public static final String CATEGORY_KEY = TAG + ".category";

    private EditText mNameField;
    private Button mDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        mNameField = (EditText) findViewById(R.id.activity_add_category_name);
        mDoneButton = (Button) findViewById(R.id.activity_add_category_done_button);

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUserInputValid()) {
                    return;
                }
                Category category = new Category(mNameField.getText().toString(), null, new ArrayList<Item>(), true);

                Intent intent = new Intent();
                intent.putExtra(CATEGORY_KEY, category);
                setResult(RESULT_OK, intent);
                finish();
                Log.i(TAG, "Created new category " + category);
            }
        });

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
