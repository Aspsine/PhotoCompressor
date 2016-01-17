package com.aspsine.photocompressor.demo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.*;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aspsine.photocompressor.CompressOptions;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConfigActivity extends AppCompatActivity {

    private static final String TAG = ConfigActivity.class.getSimpleName();

    @Bind(R.id.sbMaxPixel)
    SeekBar sbMaxPixel;

    @Bind(R.id.tvMaxPixel)
    TextView tvMaxPixel;

    @Bind(R.id.sbQuality)
    SeekBar sbQuality;

    @Bind(R.id.tvQuality)
    TextView tvQuality;

    @Bind(R.id.rgConfig)
    RadioGroup rgConfig;

    @Bind(R.id.rgFormat)
    RadioGroup rgFormat;

    private int mScreenWidth;

    private int mMaxPixel;

    private int mQuality;

    private Bitmap.Config mConfig;

    private Bitmap.CompressFormat mFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);

        calculateScreenWidth();

        initMaxPixel();
        initQuality();
        initConfig();
        initFormat();

        initDefaultValue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("SAVE");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("SAVE")) {
            configComplete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void calculateScreenWidth() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
    }

    private void initMaxPixel() {
        sbMaxPixel.setMax(mScreenWidth);
        sbMaxPixel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMaxPixel.setText(String.valueOf(progress));
                mMaxPixel = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initQuality() {
        sbQuality.setMax(100);
        sbQuality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvQuality.setText(String.valueOf(progress));
                mQuality = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initConfig() {
        rgConfig.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbALPHA_8:
                        mConfig = Bitmap.Config.ALPHA_8;
                        break;
                    case R.id.rbRGB_565:
                        mConfig = Bitmap.Config.RGB_565;
                        break;
                    case R.id.rbARGB_8888:
                        mConfig = Bitmap.Config.ARGB_8888;
                        break;
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initFormat() {
        rgFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbJPG:
                        mFormat = Bitmap.CompressFormat.JPEG;
                        break;
                    case R.id.rbPNG:
                        mFormat = Bitmap.CompressFormat.PNG;
                        break;
                    case R.id.rbWEBP:
                        mFormat = Bitmap.CompressFormat.WEBP;
                        break;
                }
            }
        });
    }

    private void initDefaultValue() {
        sbMaxPixel.setProgress(mScreenWidth);
        sbQuality.setProgress(100);
        rgConfig.check(R.id.rbARGB_8888);
        rgFormat.check(R.id.rbJPG);
    }

    private void configComplete() {
        Log.i(TAG, "CompressOptions{" +
                "maxPixel=" + mMaxPixel +
                ", quality=" + mQuality +
                ", config=" + mConfig +
                ", format=" + mFormat +
                "}");
        CompressOptions options = new CompressOptions.Builder()
                .setMaxPixel(mMaxPixel)
                .setQuality(mQuality)
                .setConfig(mConfig)
                .setFormat(mFormat)
                .setProcessPriority(Process.THREAD_PRIORITY_BACKGROUND)
                .setDir(new File(getExternalCacheDir(), MainActivity.COMPRESS_OUTPUT_DIR))
                .build();
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_COMPRESS_OPTIONS, options);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        configComplete();
    }
}
