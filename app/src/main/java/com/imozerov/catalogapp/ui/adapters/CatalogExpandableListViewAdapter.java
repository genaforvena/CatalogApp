package com.imozerov.catalogapp.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imozerov.catalogapp.R;
import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;
import com.imozerov.catalogapp.utils.LoadImageBitmapAsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imozerov on 22.03.2015.
 */
public class CatalogExpandableListViewAdapter extends BaseExpandableListAdapter {
    private final static String TAG = CatalogExpandableListViewAdapter.class.getName();

    private List<Category> mCategories;
    private List<Category> mOriginalCategories;
    private Context mContext;

    public CatalogExpandableListViewAdapter(Context context, List<Category> categories) {
        mContext = context;
        mCategories = categories;
        mOriginalCategories = new ArrayList<>(categories);
    }

    @Override
    public int getGroupCount() {
        return mCategories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCategories.get(groupPosition).items.size();
    }

    @Override
    public Category getGroup(int groupPosition) {
        return mCategories.get(groupPosition);
    }

    @Override
    public Item getChild(int groupPosition, int childPosition) {
        return mCategories.get(groupPosition).items.get(childPosition);
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
        categoryName.setText(mCategories.get(groupPosition).name);

        if (mCategories.get(groupPosition).imageUri != null) {
            ImageView categoryPic = (ImageView) convertView.findViewById(R.id.item_category_image);
            new LoadImageBitmapAsyncTask(categoryPic).execute(mCategories.get(groupPosition).imageUri);
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

        Item currentItem = mCategories.get(groupPosition).items.get(childPosition);

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

    public void filterData(String query){

        query = query.toLowerCase();
        Log.v(TAG, "Filtering data " + mCategories.size());
        mCategories.clear();

        if(query.isEmpty()){
            mCategories.addAll(mOriginalCategories);
        }
        else {

            for(Category category : mOriginalCategories){

                List<Item> itemsList = category.items;
                List<Item> newList = new ArrayList<>();
                for(Item item: itemsList){
                    if(item.name.toLowerCase().contains(query)){
                        newList.add(item);
                    }
                }
                if(newList.size() > 0){
                    Category nCategory = new Category(category.name, category.imageUri, newList, category.isUserDefined);
                    mCategories.add(nCategory);
                }
            }
        }

        notifyDataSetChanged();

    }
}
