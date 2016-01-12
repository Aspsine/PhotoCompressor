package com.aspsine.photocompressor;

import java.util.List;

/**
 * Created by aspsine on 16/1/12.
 */
public interface CompressCallback {

    void onProgress(int total, int finished);

    void onComplete(List<String> paths);

    void onFailure(Exception e);
}
