package com.aspsine.photocompressor.demo;

import android.graphics.Bitmap;
import android.os.*;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;

import com.aspsine.photocompressor.CompressCallback;
import com.aspsine.photocompressor.CompressHandler;
import com.aspsine.photocompressor.CompressOptions;
import com.aspsine.photocompressor.CompressTask;
import com.aspsine.photocompressor.Compressor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Compressor<CompressTask> mCompressor;
    private CompressHandler mCompressHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CompressOptions options = new CompressOptions.Builder()
                .setConfig(Bitmap.Config.ARGB_8888)
                .setDir(new File(getExternalCacheDir(), "compress"))
                .setMaxPixel(1000)
                .setProcessPriority(Process.THREAD_PRIORITY_BACKGROUND)
                .setFormat(Bitmap.CompressFormat.JPEG)
                .setQuality(100)
                .build();

        mCompressor = new Compressor<CompressTask>(options);
        mCompressHandler = new CompressHandler();
    }

    private void Compress() {
        mCompressHandler.setCompressCallback(new CompressCallback() {
            @Override
            public void onProgress(int total, int finished) {

            }

            @Override
            public void onComplete(List<String> paths) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        mCompressor.compress(new ArrayList<String>(), mCompressHandler, CompressTask.class);
    }

    @Override
    protected void onStop() {
        mCompressHandler.clear();
        mCompressHandler = null;
        mCompressor = null;
        super.onStop();
    }
}
