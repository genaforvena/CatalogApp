package com.imozerov.catalogapp.models;

import android.net.Uri;

import java.util.List;

/**
 * Created by imozerov on 22.03.2015.
 */
public class Category {
    public final String name;
    public final Uri imageUri;
    public final List<Item> items;
    public final boolean isUserDefined;

    public Category(String name, Uri imageUri, List<Item> items, boolean isUserDefined) {
        this.name = name;
        this.imageUri = imageUri;
        this.items = items;
        this.isUserDefined = isUserDefined;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (isUserDefined != category.isUserDefined) return false;
        if (imageUri != null ? !imageUri.equals(category.imageUri) : category.imageUri != null)
            return false;
        if (name != null ? !name.equals(category.name) : category.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (imageUri != null ? imageUri.hashCode() : 0);
        result = 31 * result + (isUserDefined ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return name.toUpperCase();
    }
}
