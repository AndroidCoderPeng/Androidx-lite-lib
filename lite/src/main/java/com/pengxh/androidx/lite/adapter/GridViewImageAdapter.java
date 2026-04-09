package com.pengxh.androidx.lite.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pengxh.androidx.lite.R;

import java.util.List;

/**
 * 不可编辑图片适配器
 * 仅支持 {@link android.widget.GridView}
 */
public class GridViewImageAdapter extends BaseAdapter {

    private final List<String> mImages;
    private final int mViewWidth;

    public GridViewImageAdapter(List<String> images, int viewWidth) {
        this.mImages = images;
        this.mViewWidth = viewWidth;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < 0 || position >= mImages.size()) {
            throw new IndexOutOfBoundsException();
        }
        return mImages.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_readonly_gv, null);
            holder = new ItemViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }
        Glide.with(parent.getContext())
                .load(mImages.get(position))
                .apply(new RequestOptions().error(R.mipmap.load_image_error))
                .into(holder.imageView);

        //动态设置图片高度，和图片宽度保持一致
        int mSpanCount = 3;
        int imageSize = mViewWidth / mSpanCount;
        if (holder.cachedLayoutParams == null) {
            holder.cachedLayoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        } else {
            holder.cachedLayoutParams.width = imageSize;
            holder.cachedLayoutParams.height = imageSize;
        }
        holder.imageView.setLayoutParams(holder.cachedLayoutParams);
        return convertView;
    }

    private static class ItemViewHolder {
        private ImageView imageView;
        private LinearLayout.LayoutParams cachedLayoutParams;
    }
}
