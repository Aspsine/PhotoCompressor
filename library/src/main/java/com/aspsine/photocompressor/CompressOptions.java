package com.aspsine.photocompressor;

import android.graphics.Bitmap;

import java.io.File;
import java.io.Serializable;

/**
 * Created by aspsine on 16/1/12.
 */
public class CompressOptions implements Serializable{

    public int maxPixel;

    public int quality;

    public Bitmap.Config config;

    public Bitmap.CompressFormat format;

    public File dir;

    public int processPriority;

    public CompressOptions() {
    }

    public CompressOptions(int maxPixel, int quality, Bitmap.Config config, Bitmap.CompressFormat format, File dir, int processPriority) {
        this.maxPixel = maxPixel;
        this.quality = quality;
        this.config = config;
        this.format = format;
        this.dir = dir;
        this.processPriority = processPriority;
    }

    public static class Builder {
        private int maxPixel;

        private int quality;

        private Bitmap.Config config;

        private Bitmap.CompressFormat format;

        private File dir;

        private int processPriority;

        private CompressOptions options;

        public Builder() {
            options = new CompressOptions();
        }

        public Builder setMaxPixel(int maxPixel) {
            this.maxPixel = maxPixel;
            return this;
        }

        public Builder setQuality(int quality) {
            this.quality = quality;
            return this;
        }

        public Builder setConfig(Bitmap.Config config) {
            this.config = config;
            return this;
        }

        public Builder setFormat(Bitmap.CompressFormat format) {
            this.format = format;
            return this;
        }

        public Builder setDir(File dir) {
            this.dir = dir;
            return this;
        }

        public Builder setProcessPriority(int processPriority) {
            this.processPriority = processPriority;
            return this;
        }

        public CompressOptions build() {
            return new CompressOptions(maxPixel, quality, config, format, dir, processPriority);
        }
    }
}
