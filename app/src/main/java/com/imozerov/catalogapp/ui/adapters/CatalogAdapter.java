package com.imozerov.catalogapp.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.database.CatalogDataSource;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;

/**
 * Created by imozerov on 24.03.2015.
 */
public class CatalogAdapter extends CursorTreeAdapter {

    private final LayoutInflater mInflater;
    private final CatalogDataSource mCatalogDataSource;
    private final Activity mActivity;

    private FilterQueryProvider mFilterQueryProvider;

    public CatalogAdapter(Cursor cursor, Activity activity, CatalogDataSource catalogDataSource) {
        super(cursor, activity);
        mInflater = LayoutInflater.from(activity);
        mCatalogDataSource = catalogDataSource;
        mActivity = activity;
        mFilterQueryProvider = new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return mCatalogDataSource.getItemsCursor(constraint);
            }
        };
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        if (groupCursor == null) {
            return null;
        }
        Category category = CatalogDataSource.cursorToCategory(groupCursor);
        return mCatalogDataSource.getItemsCursor(category);
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.item_category, parent, false);
        return view;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        if (cursor == null || view == null) {
            return;
        }

        Category category = CatalogDataSource.cursorToCategory(cursor);

        TextView categoryNameView = (TextView) view.findViewById(R.id.item_category_name);
        ImageView categoryImageView = (ImageView) view.findViewById(R.id.item_category_image);

        if (categoryNameView != null) {
            categoryNameView.setText(category.getName());
        }

        if (categoryImageView != null && category.getImage() != null) {
            categoryImageView.setImageBitmap(category.getImage());
        }
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.item_item, parent, false);
        return view;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        if (cursor == null || view == null) {
            return;
        }

        Item item = CatalogDataSource.cursorToItem(cursor, null);

        TextView itemNameView = (TextView) view.findViewById(R.id.item_item_name);
        ImageView itemImageView = (ImageView) view.findViewById(R.id.item_item_image);

        if (itemImageView != null && item.getImage() != null) {
            itemImageView.setImageBitmap(item.getImage());
        }

        if (itemNameView != null) {
            itemNameView.setText(item.getName());
        }
    }

    public void filterData(String query) {
        final Cursor oldCursor = getChildrenCursor(getCursor());
        setFilterQueryProvider(mFilterQueryProvider);
        getFilter().filter(query, new Filter.FilterListener() {
            public void onFilterComplete(int count) {

            }
        });
    }
}
