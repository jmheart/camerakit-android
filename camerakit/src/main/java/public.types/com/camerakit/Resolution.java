package com.camerakit;


import android.hardware.Camera;
import android.util.Size;

public class Resolution implements Comparable<Resolution> {

    private final int mWidth;
    private final int mHeight;

    public Resolution(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public Resolution inverse() {
        return new Resolution(mHeight, mWidth);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof Resolution) {
            Resolution size = (Resolution) o;
            return mWidth == size.mWidth && mHeight == size.mHeight;
        }
        return false;
    }

    @Override
    public String toString() {
        return mWidth + "x" + mHeight;
    }

    @Override
    public int hashCode() {
        return mHeight ^ ((mWidth << (Integer.SIZE / 2)) | (mWidth >>> (Integer.SIZE / 2)));
    }

    @Override
    public int compareTo(Resolution another) {
        return mWidth * mHeight - another.mWidth * another.mHeight;
    }

    public static Resolution get(Camera.Size size) {
        return new Resolution(size.width, size.height);
    }

    public static Resolution get(Size size) {
        return new Resolution(size.getWidth(), size.getHeight());
    }

}
