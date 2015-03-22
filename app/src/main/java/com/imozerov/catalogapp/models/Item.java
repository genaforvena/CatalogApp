package com.imozerov.catalogapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by imozerov on 22.03.2015.
 */
public class Item implements Parcelable {
    public final String name;
    public final boolean isUserDefined;
    public final String description;

    public Item(String name, boolean isUserDefined, String description) {
        this.name = name;
        this.isUserDefined = isUserDefined;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (isUserDefined != item.isUserDefined) return false;
        if (name != null ? !name.equals(item.name) : item.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (isUserDefined ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", isUserDefined=" + isUserDefined +
                '}';
    }

    protected Item(Parcel in) {
        name = in.readString();
        isUserDefined = in.readByte() != 0x00;
        description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeByte((byte) (isUserDefined ? 0x01 : 0x00));
        dest.writeString(description);
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
}
