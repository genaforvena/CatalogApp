package com.imozerov.catalogapp.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.database.RuntimeDatabase;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;

public class AddItemActivity extends ActionBarActivity {
    private static final String TAG = AddItemActivity.class.getName();

    private EditText mNameField;
    private EditText mDescriptionField;
    private Spinner mCategorySpinner;
    private Button mDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mNameField = (EditText) findViewById(R.id.activity_add_item_name);
        mDescriptionField = (EditText) findViewById(R.id.activity_add_item_description);
        mCategorySpinner = (Spinner) findViewById(R.id.activity_add_item_category);
        mDoneButton = (Button) findViewById(R.id.activity_add_item_done_button);

        ArrayAdapter<Category> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, RuntimeDatabase.getInstance().getCategories());
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(spinnerArrayAdapter);

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUserInputValid()) {
                    return;
                }
                Item newItem = new Item(mNameField.getText().toString(), true, mDescriptionField.getText().toString());
                RuntimeDatabase.getInstance().addItem((Category) mCategorySpinner.getSelectedItem(), newItem);

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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
