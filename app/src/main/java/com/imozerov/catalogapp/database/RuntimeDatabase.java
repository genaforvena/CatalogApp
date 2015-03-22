package com.imozerov.catalogapp.database;

import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imozerov on 22.03.2015.
 */
public class RuntimeDatabase implements IDatabase {
    private static final String TAG = RuntimeDatabase.class.getName();
    private static RuntimeDatabase sInstance;
    private List<Category> mCategories;

    public static RuntimeDatabase getInstance() {
        if (sInstance == null) {
            sInstance = new RuntimeDatabase();
        }
        return sInstance;
    }

    private RuntimeDatabase() {
        mCategories = new ArrayList<>();

        Item item1 = new Item("Item1", false, "Some meaningful description");
        Item item2 = new Item("Item2", false, "Some meaningful description");
        Item item3 = new Item("Item3", false, "Some meaningful description");
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        Category category1 = new Category("One", null, items, false);

        mCategories.add(category1);
    }

    @Override
    public List<Category> getCategories() {
        return mCategories;
    }

    @Override
    public List<Item> getItems(Category category) {
        for (Category category1 : mCategories) {
            if (category.equals(category1)) {
                return category1.items;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void addItem(Category category, Item item) {
        List<Item> itemsNow = getItems(category);
        itemsNow.add(item);
    }

    @Override
    public void deleteItem(Category category, Item item) {
        List<Item> itemsNow = getItems(category);
        itemsNow.remove(item);
    }

    @Override
    public void addCategory(Category category) {
        mCategories.add(category);
    }

    @Override
    public void deleteCategory(Category category) {
        mCategories.remove(category);
    }
}
