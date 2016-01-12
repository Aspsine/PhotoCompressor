package com.aspsine.photocompressor;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by aspsine on 16/1/12.
 */
public class CompressHandler extends Handler {

    public static final int STATUS_PROGRESS = 100;

    public static final int STATUS_COMPLETE = 200;

    public static final int STATUS_FAILURE = -1;

    private WeakReference<CompressCallback> mCallbackReference;

    public CompressHandler() {

    }

    public CompressHandler(CompressCallback callback) {
        this.mCallbackReference = new WeakReference<CompressCallback>(callback);
    }

    public void setCompressCallback(CompressCallback callback) {
        clear();
        this.mCallbackReference = new WeakReference<CompressCallback>(callback);
    }

    @Override
    public void handleMessage(Message msg) {
        CompressCallback callback = mCallbackReference.get();
        switch (msg.what) {
            case STATUS_PROGRESS:
                if (callback != null) {
                    callback.onProgress(msg.arg1, msg.arg2);
                }
                break;
            case STATUS_COMPLETE:
                List<String> paths = (List<String>) msg.obj;
                if (callback != null) {
                    callback.onComplete(paths);
                }
                break;
            case STATUS_FAILURE:
                Exception e = (Exception) msg.obj;
                if (callback != null) {
                    callback.onFailure(e);
                }
                break;
        }
    }

    public void progress(int total, int finished) {
        Message msg = obtainMessage();
        msg.what = STATUS_PROGRESS;
        msg.arg1 = total;
        msg.arg2 = finished;
        msg.sendToTarget();
    }

    public void complete(List<String> paths) {
        Message msg = obtainMessage();
        msg.what = STATUS_COMPLETE;
        msg.obj = paths;
        msg.sendToTarget();
    }

    public void failure(Exception e) {
        Message msg = obtainMessage();
        msg.what = STATUS_FAILURE;
        msg.obj = e;
        msg.sendToTarget();
    }

    public void clear() {
        removeCallbacksAndMessages(null);
        if (mCallbackReference != null) {
            mCallbackReference.clear();
            mCallbackReference = null;
        }
    }
}
