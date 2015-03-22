package com.imozerov.catalogapp.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.imozerov.catalogapp.BuildConfig;
import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.ui.ItemActivity;

import java.util.List;

/**
 * Created by imozerov on 22.03.2015.
 */
public class CatalogExpandableListViewAdapter extends BaseExpandableListAdapter {
    private final static String TAG = CatalogExpandableListViewAdapter.class.getName();

    private List<Category> mGroups;
    private Context mContext;

    public CatalogExpandableListViewAdapter (Context context, List<Category> groups){
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
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
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

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_item, null);
        }

        final Item currentItem = mGroups.get(groupPosition).items.get(childPosition);

        TextView itemName = (TextView) convertView.findViewById(R.id.item_item_name);
        itemName.setText(currentItem.name);
        itemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Item from list view was clicked.");
                    Intent intent = new Intent(mContext, ItemActivity.class);
                    intent.putExtra(ItemActivity.ITEM_KEY, currentItem);
                    mContext.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
