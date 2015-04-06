package com.imozerov.catalogapp.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.content.Loader;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.database.CatalogDataSource;
import com.imozerov.catalogapp.database.MySQLiteOpenHelper;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.ui.CatalogActivity;

import java.lang.ref.WeakReference;

/**
 * Created by imozerov on 24.03.2015.
 */
public class CatalogAdapter extends CursorTreeAdapter {
    private final static String TAG = CatalogAdapter.class.getName();

    private final LayoutInflater mInflater;
    private final WeakReference<CatalogActivity> mActivity;
    private final LongSparseArray<Integer> mGroupMap;
    private final WeakReference<ExpandableListView> mCatalogView;
    private int mLastExpandedGroupPosition = -1;
    private boolean mIsSearch;

    public CatalogAdapter(Cursor cursor, CatalogActivity activity, ExpandableListView catalogView) {
        super(cursor, activity);
        mInflater = LayoutInflater.from(activity);
        mActivity = new WeakReference<CatalogActivity>(activity);
        mGroupMap = new LongSparseArray<>();
        mCatalogView = new WeakReference<ExpandableListView>(catalogView) ;
    }

    @Override
    protected Cursor getChildrenCursor(final Cursor groupCursor) {
        int groupPos = groupCursor.getPosition();
        int groupId = groupCursor.getInt(groupCursor.getColumnIndex(MySQLiteOpenHelper.CATEGORIES_COLUMN_ID));
        Log.d(TAG, "getChildrenCursor() for groupId " + groupId);

        mGroupMap.put(groupId, groupPos);
        Loader<Cursor> loader = mActivity.get().getSupportLoaderManager().getLoader(groupId);
        if (loader != null && !loader.isReset()) {
            mActivity.get().getSupportLoaderManager().restartLoader(groupId, null, mActivity.get());
        } else {
            mActivity.get().getSupportLoaderManager().initLoader(groupId, null, mActivity.get());
        }

        return null;
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

        if (categoryImageView != null) {
            if (category.getImage() != null) {
                categoryImageView.setImageBitmap(category.getImage());
            } else {
                categoryImageView.setImageResource(R.drawable.abc_cab_background_internal_bg);
            }
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

        if (itemNameView != null) {
            itemNameView.setText(item.getName());
        }
    }

    @Override
    public void onGroupExpanded(int groupPosition){
        if (mIsSearch) {
            super.onGroupExpanded(groupPosition);
            return;
        }
        if(groupPosition != mLastExpandedGroupPosition){
            mCatalogView.get().collapseGroup(mLastExpandedGroupPosition);
        }

        super.onGroupExpanded(groupPosition);
        mLastExpandedGroupPosition = groupPosition;
    }

    public boolean isSearch() {
        return mIsSearch;
    }

    public void setSearch(boolean isSearch) {
        mIsSearch = isSearch;
    }

    public LongSparseArray<Integer> getGroupMap() {
        return mGroupMap;
    }
}
