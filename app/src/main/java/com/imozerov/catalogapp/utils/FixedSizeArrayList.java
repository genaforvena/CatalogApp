package com.imozerov.catalogapp.utils;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by imozerov on 07.04.2015.
 */
public class FixedSizeArrayList extends ArrayList<String> {
    private final int mMaxSize;
    private int mCurrentIndex = 0;
    public FixedSizeArrayList(int maxSize) {
        super();
        this.mMaxSize = maxSize;
    }

    public boolean add(String t) {
        if (TextUtils.isEmpty(t)) {
            return false;
        }
        if (size() >= mMaxSize) {
            remove(0);
        }
        return super.add(t);
    }

    // This should be an iterator implementation.
    // But I think as scope of this method is really limited
    // We can implement it in ugly way.
    public String next() {
        mCurrentIndex++;
        if (mCurrentIndex > mMaxSize) {
            mCurrentIndex = 0;
        }
        if (mCurrentIndex >= size()) {
            mCurrentIndex--;
        }
        return get(mCurrentIndex);
    }

    public String previous() {
        mCurrentIndex--;
        if (mCurrentIndex < 0) {
            mCurrentIndex++;
        }
        return get(mCurrentIndex);
    }
}