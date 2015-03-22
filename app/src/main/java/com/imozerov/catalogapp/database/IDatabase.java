package com.imozerov.catalogapp.database;

import com.imozerov.catalogapp.models.Category;
import com.imozerov.catalogapp.models.Item;

import java.util.List;

/**
 * Created by imozerov on 22.03.2015.
 */
public interface IDatabase {
    List<Category> getCategories();
    List<Item> getItems(Category category);
    void addItem(Category category, Item item);
    void deleteItem(Category category, Item item);
    void addCategory(Category category);
    void deleteCategory(Category category);
}
