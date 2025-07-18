package com.pengxh.androidx.lite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.kit.ContextKit;

import java.util.List;

/**
 * 不可编辑图片适配器
 * 仅支持 {@link android.widget.GridView}
 */
public class GridViewImageAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> images;
    private final int screenWidth;

    public GridViewImageAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
        this.screenWidth = ContextKit.getScreenWidth(context);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < 0 || position >= images.size()) {
            throw new IndexOutOfBoundsException();
        }
        return images.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_readonly_gv, null);
            holder = new ItemViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }
        Glide.with(context)
                .load(images.get(position))
                .apply(new RequestOptions().error(R.mipmap.load_image_error))
                .into(holder.imageView);

        //动态设置图片高度，和图片宽度保持一致
        int padding = convertView.getPaddingLeft() + convertView.getPaddingRight();
        int imageSize = (screenWidth - padding) / 3;
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
