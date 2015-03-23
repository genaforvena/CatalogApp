package com.imozerov.catalogapp.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imozerov on 22.03.2015.
 */
public class Category implements Parcelable {
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

    protected Category(Parcel in) {
        name = in.readString();
        imageUri = (Uri) in.readValue(Uri.class.getClassLoader());
        if (in.readByte() == 0x01) {
            items = new ArrayList<Item>();
            in.readList(items, Item.class.getClassLoader());
        } else {
            items = null;
        }
        isUserDefined = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeValue(imageUri);
        if (items == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(items);
        }
        dest.writeByte((byte) (isUserDefined ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
