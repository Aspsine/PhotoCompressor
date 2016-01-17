package com.aspsine.photocompressor.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.aspsine.photocompressor.CompressCallback;
import com.aspsine.photocompressor.CompressHandler;
import com.aspsine.photocompressor.CompressOptions;
import com.aspsine.photocompressor.CompressTask;
import com.aspsine.photocompressor.Compressor;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CONFIG = 100;

    public static final int REQUEST_CODE_GALLERY = 200;

    public static final String EXTRA_COMPRESS_OPTIONS = "EXTRA_COMPRESS_OPTIONS";

    public static final String COMPRESS_OUTPUT_DIR = "compressed";

    private Compressor<CompressTask> mCompressor;

    private CompressHandler mCompressHandler;

    private ProgressDialog mProgressDialog;

    private CompressOptions mCompressOptions;

    private Adapter mAdapter;

    @Bind(R.id.listView)
    ListView listView;

    @Bind(R.id.btnAdd)
    Button btnAdd;

    @Bind(R.id.btnCompress)
    Button btnCompress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initCompressor();
        initView();

    }

    private void initView() {
        if (mAdapter == null) {
            mAdapter = new Adapter();
        }
        if (listView.getAdapter() == null) {
            listView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("CONFIG");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("CONFIG")) {
            Intent intent = new Intent(this, ConfigActivity.class);
            startActivityForResult(intent, 100);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        destroyCompressor();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CONFIG && resultCode == RESULT_OK) {
            dealWithConfigResult(data);
        } else if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            dealWithGalleryResult(data);
        }
    }

    private void dealWithConfigResult(Intent data) {
        CompressOptions options = (CompressOptions) data.getSerializableExtra(EXTRA_COMPRESS_OPTIONS);
        if (options != null) {
            mCompressOptions.maxPixel = options.maxPixel;
            mCompressOptions.quality = options.quality;
            mCompressOptions.config = options.config;
            mCompressOptions.format = options.format;
            mCompressOptions.processPriority = options.processPriority;
            mCompressOptions.dir = options.dir;
        }
    }

    private void dealWithGalleryResult(Intent data) {
        Uri uri = data.getData();
        String path = FileUtils.getImagePath(this, uri);
        addPhoto(path);
    }

    private void addPhoto(String path) {
        mAdapter.add(path);
    }

    private void addCompressedPhotos(List<String> paths) {
        mAdapter.addCompressedPhotos(paths);
    }

    private void initCompressor() {
        mCompressOptions = new CompressOptions.Builder()
                .setConfig(Bitmap.Config.ARGB_8888)
                .setDir(new File(getExternalCacheDir(), COMPRESS_OUTPUT_DIR))
                .setMaxPixel(1000)
                .setProcessPriority(Process.THREAD_PRIORITY_BACKGROUND)
                .setFormat(Bitmap.CompressFormat.JPEG)
                .setQuality(100)
                .build();
        mCompressor = new Compressor<CompressTask>(mCompressOptions);
        mCompressHandler = new CompressHandler();
    }

    private void destroyCompressor() {
        mCompressHandler.clear();
        mCompressHandler = null;
        mCompressor = null;
    }

    @OnClick(R.id.btnAdd)
    void addPhotoClick() {
        Intent intents = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intents.setType("image/*");
        startActivityForResult(intents, REQUEST_CODE_GALLERY);
    }

    @OnClick(R.id.btnCompress)
    void compressClick() {
        List<String> paths = getPhotoPath();
        if (check(paths)) {
            compress(paths);
        }
    }

    private List<String> getPhotoPath() {
        return mAdapter.getPhotos();
    }

    private boolean check(List<String> paths) {
        if (paths.isEmpty()) {
            Toast.makeText(this, "no photo to compress", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) {
                Toast.makeText(this, "path: " + path + "don't exist", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void compress(List<String> paths) {

        mCompressHandler.setCompressCallback(new CompressCallback() {
            @Override
            public void onProgress(int total, int finished) {
                show("Please Wait", String.format("Compress process is running (%d/%d)", finished, total));
            }

            @Override
            public void onComplete(List<String> paths) {
                show("Congratulations!", "Compress complete.");
                dismiss();
                addCompressedPhotos(paths);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, "Compress error!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        mCompressor.compress(paths, mCompressHandler, CompressTask.class);
    }


    private void show(String title, String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dismiss() {
        if (mProgressDialog != null) {
            mProgressDialog.setMessage("");
            mProgressDialog.setTitle("");
            mProgressDialog.dismiss();
        }
    }
}
