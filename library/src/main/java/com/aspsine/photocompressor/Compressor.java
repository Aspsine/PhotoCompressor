package com.aspsine.photocompressor;

import android.util.Log;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by aspsine on 16/1/12.
 */
public class Compressor<T extends CompressTask> {

    private static final String TAG = Compressor.class.getSimpleName();

    private Executor mExecutor;

    private CompressOptions mOptions;

    public Compressor(CompressOptions options) {
        mOptions = options;
        mExecutor = Executors.newFixedThreadPool(1);
    }

    public void compress(List<String> paths, CompressHandler handler, Class<T> clazz) {
        CompressTask task = null;
        try {
            task = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (task != null) {
            task.setParams(paths, mOptions, handler);
            mExecutor.execute(task);
        } else {
            Log.e(TAG, clazz.getSimpleName() + " must have an default(zero-argument) constructor and it's must be public");
        }
    }
}
