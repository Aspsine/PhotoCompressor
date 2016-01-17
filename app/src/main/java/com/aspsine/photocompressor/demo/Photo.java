package com.aspsine.photocompressor.demo;

import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by aspsine on 16/1/17.
 */
public class Photo {
    private String path;
    private int width;
    private int height;
    private int size;

    public Photo(String path) {
        this.path = path;
        try {
            FileInputStream inputStream = new FileInputStream(path);
            size = inputStream.available();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            width = options.outWidth;
            height = options.outHeight;
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getShowSize() {
        return String.valueOf((int) (((float) size) / 1024)) + "KB";
    }

    public String getShowPixel() {
        return width + " * " + height;
    }
}
