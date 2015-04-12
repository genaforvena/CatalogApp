package com.imozerov.catalogapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.imozerov.catalogapp.utils.FixedSizeArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imozerov on 22.03.2015.
 */
public class Item implements Parcelable {
    public static final int MAX_IMAGES = 4;

    private long mId;
    private String mName;
    private boolean mIsUserDefined;
    private String mDescription;
    private FixedSizeArrayList mImages;
    private Category mCategory;

    public Item() {}

    protected Item(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        mIsUserDefined = in.readByte() != 0x00;
        mDescription = in.readString();
        mImages = new FixedSizeArrayList(4);
        Object images = in.readValue(FixedSizeArrayList.class.getClassLoader());
        if (images != null) {
            mImages.addAll(((ArrayList<String>) images));
        }
        mCategory = (Category) in.readValue(Category.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeByte((byte) (mIsUserDefined ? 0x01 : 0x00));
        dest.writeString(mDescription);
        dest.writeValue(mImages);
        dest.writeValue(mCategory);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (mId != item.mId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }



    @Override
    public String toString() {
        return "Item{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mIsUserDefined=" + mIsUserDefined +
                ", mDescription='" + mDescription + '\'' +
                ", mImages='" + mImages + '\'' +
                '}';
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isUserDefined() {
        return mIsUserDefined;
    }

    public void setUserDefined(boolean isUserDefined) {
        mIsUserDefined = isUserDefined;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public FixedSizeArrayList getImages() {
        return mImages;
    }

    public void setImages(FixedSizeArrayList images) {
        mImages = images;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        mCategory = category;
    }
}
