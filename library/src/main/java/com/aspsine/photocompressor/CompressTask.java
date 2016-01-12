package com.aspsine.photocompressor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aspsine on 16/1/12.
 */
public abstract class CompressTask implements Runnable {

    public static final String TAG = CompressTask.class.getSimpleName();

    private List<String> mPaths;

    private CompressOptions mOptions;

    private CompressHandler mHandler;

    public CompressTask() {
    }

    public void setParams(List<String> paths, CompressOptions options, CompressHandler handler) {
        this.mOptions = options;
        this.mPaths = paths;
        this.mHandler = handler;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(mOptions.processPriority);
        try {
            List<String> paths = new ArrayList<>();
            int total = mPaths.size();
            for (int i = 0; i < total; i++) {
                String path = mPaths.get(i);
                File file = new File(path);
                if (!file.exists()) {
                    throw new FileNotFoundException("file not found");
                }
                Bitmap bitmap = getBitmap(path, mOptions);
                Bitmap compressedBitmap = CompressBitmap(bitmap, mOptions);
                bitmap = null;
                String key = getKey(path, mOptions);
                String compressedPath = convertBitmapToFile(key, compressedBitmap, mOptions);
                compressedBitmap = null;
                if (!TextUtils.isEmpty(compressedPath)) {
                    paths.add(compressedPath);
                } else {
                    Log.e(TAG, "Compressed failed:" + path);
                }
                mHandler.progress(total, i + 1);
            }
            mHandler.complete(paths);
        } catch (Exception e) {
            mHandler.failure(e);
        }
    }

    protected String convertBitmapToFile(String key, Bitmap bitmap, CompressOptions options) throws IOException {
        File file = new File(mOptions.dir, key);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        BufferedOutputStream outputStream = new BufferedOutputStream(bufferedOutputStream);
        boolean success = bitmap.compress(options.format, options.quality, outputStream);
        if (success) {
            return file.getPath();
        }
        return null;
    }

    protected Bitmap CompressBitmap(Bitmap bitmap, CompressOptions options) {
        int maxSize = options.maxPixel;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (height <= maxSize && width <= maxSize) {
            // do nothing. just for better understanding.
        } else {
            if (height >= width) {
                float bei = height / (float) maxSize;
                width = (int) (width / bei);
                height = maxSize;
            } else {
                float bei = width / (float) maxSize;
                width = maxSize;
                height = (int) (height / bei);
            }
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    protected Bitmap getBitmap(String path, CompressOptions compressOptions) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = compressOptions.config;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    protected String getKey(String path, CompressOptions options) {
        return MD5.md5(path);
    }
}
