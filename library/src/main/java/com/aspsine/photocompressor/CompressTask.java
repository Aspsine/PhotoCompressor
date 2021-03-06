package com.aspsine.photocompressor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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
public class CompressTask implements Runnable {

    public static final String TAG = CompressTask.class.getSimpleName();

    private List<String> mPaths;

    private CompressOptions mOptions;

    private CompressHandler mHandler;
    private int sampleSize;

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
                mHandler.progress(total, i + 1);
                String path = mPaths.get(i);
                File file = new File(path);
                if (!file.exists()) {
                    throw new FileNotFoundException("file not found");
                }
                Bitmap compressedBitmap = getCompressedBitmap(path, mOptions);
                String key = getKey(path, mOptions);
                String compressedPath = convertBitmapToFile(key, compressedBitmap, mOptions);
                compressedBitmap = null;
                if (!TextUtils.isEmpty(compressedPath)) {
                    paths.add(compressedPath);
                } else {
                    Log.e(TAG, "Compressed failed:" + path);
                }
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

    protected Bitmap getCompressedBitmap(String path, CompressOptions compressOptions) throws IOException {
        BitmapFactory.Options options = createInitDecodeOptions();

        BitmapFactory.decodeFile(path, options);

        int outWidth = options.outWidth;
        int outHeight = options.outHeight;

        float bei = getBei(outWidth, outHeight, compressOptions.maxPixel);

        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) bei;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        bei = getBei(width, height, compressOptions.maxPixel);
        width = (int) (width / bei);
        height = (int) (height / bei);

        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }


    private BitmapFactory.Options createInitDecodeOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            options.inPurgeable = true;
            options.inInputShareable = true;
        }
        return options;
    }

    protected String getKey(String path, CompressOptions options) {
        return MD5.md5(path);
    }

    public float getBei(int outWidth, int outHeight, int preferSize) {
        int biggerOne = outWidth > outHeight ? outWidth : outHeight;
        float bei = 1;
        if (biggerOne > preferSize) {
            bei = biggerOne / (float) preferSize;
        }
        return bei;
    }
}
