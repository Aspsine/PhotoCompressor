package com.aspsine.photocompressor.demo;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by aspsine on 16/1/17.
 */
public class Adapter extends BaseAdapter {

    private List<Photo> photos;

    private List<Photo> compressedPhotos;

    private DisplayImageOptions mOptions;

    public Adapter() {
        photos = new ArrayList<>();
        compressedPhotos = new ArrayList<>();
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public List<String> getPhotos() {
        List<String> paths = new ArrayList<>();
        for (Photo photo : photos) {
            paths.add(photo.getPath());
        }
        return paths;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Photo getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Photo photo = getItem(position);
        Photo compressedPhoto = null;
        if (position < compressedPhotos.size()) {
            compressedPhoto = compressedPhotos.get(position);
        }

        if (photo != null) {
            ImageLoader.getInstance().displayImage("file://" + photo.getPath(), holder.ivPhoto, mOptions);
            holder.tvPixel.setText(photo.getShowPixel());
            holder.tvSize.setText(photo.getShowSize());
            holder.tvPath.setText(photo.getPath());
        } else {
            holder.ivPhoto.setImageBitmap(null);
            holder.tvPixel.setText("");
            holder.tvSize.setText("");
            holder.tvPath.setText("");
        }

        if (compressedPhoto != null) {
            ImageLoader.getInstance().displayImage("file://" + compressedPhoto.getPath(), holder.ivCompressedPhoto, mOptions);
            holder.tvCompressedPixel.setText(compressedPhoto.getShowPixel());
            holder.tvCompressedSize.setText(compressedPhoto.getShowSize());
            holder.tvCompressedPath.setText(compressedPhoto.getPath());
        } else {
            holder.ivCompressedPhoto.setImageBitmap(null);
            holder.tvCompressedPixel.setText("");
            holder.tvCompressedSize.setText("");
            holder.tvCompressedPath.setText("");
        }
        return convertView;
    }

    public void add(String path) {
        Photo photo = new Photo(path);
        photos.add(photo);
        notifyDataSetChanged();
    }

    void addCompressedPhotos(List<String> paths) {
        compressedPhotos.clear();
        for (String path : paths) {
            compressedPhotos.add(new Photo(path));
        }
        notifyDataSetChanged();
    }

    class ViewHolder {
        @Bind(R.id.ivPhoto)
        ImageView ivPhoto;

        @Bind(R.id.ivCompressedPhoto)
        ImageView ivCompressedPhoto;

        @Bind(R.id.tvPixel)
        TextView tvPixel;

        @Bind(R.id.tvCompressedPixel)
        TextView tvCompressedPixel;

        @Bind(R.id.tvSize)
        TextView tvSize;

        @Bind(R.id.tvCompressedSize)
        TextView tvCompressedSize;

        @Bind(R.id.tvPath)
        TextView tvPath;

        @Bind(R.id.tvCompressedPath)
        TextView tvCompressedPath;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            DisplayMetrics metrics = view.getResources().getDisplayMetrics();
            int px8 = (int) (8 * metrics.density + 0.5f);
            int px = metrics.widthPixels / 2 - px8 * 2;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(px, px);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            layoutParams.setMargins(0, px8, 0, 0);
            ivPhoto.setLayoutParams(layoutParams);
            ivCompressedPhoto.setLayoutParams(layoutParams);
        }
    }

}
