package com.imozerov.catalogapp.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imozerov.catalogapp.BuildConfig;
import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.ui.ItemActivity;
import com.imozerov.catalogapp.utils.ImageUtils;
import com.imozerov.catalogapp.utils.LoadImageBitmapAsyncTask;

import java.util.List;

/**
 * Created by imozerov on 22.03.2015.
 */
public class CatalogExpandableListViewAdapter extends BaseExpandableListAdapter {
    private final static String TAG = CatalogExpandableListViewAdapter.class.getName();

    private List<Category> mGroups;
    private Context mContext;

    public CatalogExpandableListViewAdapter(Context context, List<Category> groups) {
        mContext = context;
        mGroups = groups;
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).items.size();
    }

    @Override
    public Category getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Item getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_category, null);
        }

        TextView categoryName = (TextView) convertView.findViewById(R.id.item_category_name);
        categoryName.setText(mGroups.get(groupPosition).name);

        if (mGroups.get(groupPosition).imageUri != null) {
            ImageView categoryPic = (ImageView) convertView.findViewById(R.id.item_category_image);
            new LoadImageBitmapAsyncTask(categoryPic).execute(mGroups.get(groupPosition).imageUri);
        }

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_item, null);
        }

        Item currentItem = mGroups.get(groupPosition).items.get(childPosition);

        TextView itemName = (TextView) convertView.findViewById(R.id.item_item_name);
        itemName.setText(currentItem.name);

        if (currentItem.imageUri != null) {
            ImageView itemImage = (ImageView) convertView.findViewById(R.id.item_item_image);
            new LoadImageBitmapAsyncTask(itemImage).execute(currentItem.imageUri);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
