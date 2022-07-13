package com.pengxh.androidx.lite.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.utils.DeviceSizeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数量可编辑图片适配器
 */
@SuppressLint("NotifyDataSetChanged")
public class EditableImageAdapter extends RecyclerView.Adapter<EditableImageAdapter.ItemViewHolder> {

    private static final String TAG = "EditableImageAdapter";
    private final Context context;
    private final int imageCountLimit;
    private List<String> imageData = new ArrayList<>();
    private float leftMargin = 0f;
    private float rightMargin = 0f;
    private int padding = 0;

    public EditableImageAdapter(Context context, int imageCountLimit) {
        this.context = context;
        this.imageCountLimit = imageCountLimit;
    }

    public void setupImage(List<String> images) {
        this.imageData = images;
        notifyDataSetChanged();
    }

    public void deleteImage(int position) {
        if (!imageData.isEmpty()) {
            imageData.remove(position);
            notifyDataSetChanged();
        }
    }

    /**
     * @param leftMargin  RecyclerView左边距
     * @param rightMargin RecyclerView右边距
     * @param padding     RecyclerView内边距
     */
    public void setImageMargins(float leftMargin, float rightMargin, float padding) {
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.padding = DeviceSizeUtil.dp2px(context, padding);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        int realWidth = DeviceSizeUtil.obtainScreenWidth(context)
                - DeviceSizeUtil.dp2px(context, leftMargin)
                - DeviceSizeUtil.dp2px(context, rightMargin);
        int itemSize = (realWidth - 4 * padding) / 3;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemSize, itemSize);
        params.setMargins(padding, padding, padding, padding);
        params.gravity = Gravity.CENTER;
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(params);
        return new ItemViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position == getItemCount() - 1 && imageData.size() < imageCountLimit) {
            holder.imageView.setImageResource(R.drawable.ic_add_pic);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加图片
                    if (itemClickListener == null) {
                        Log.e(TAG, "onClick: itemClickListener not init");
                        return;
                    }
                    itemClickListener.onAddImageClick();
                }
            });
        } else {
            Glide.with(context).load(imageData.get(position)).into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击操作，查看大图
                    if (itemClickListener == null) {
                        Log.e(TAG, "onClick: itemClickListener not init");
                        return;
                    }
                    itemClickListener.onItemClick(position);
                }
            });
            // 长按监听
            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //长按删除
                    if (itemClickListener == null) {
                        Log.e(TAG, "onClick: itemClickListener not init");
                        return true;
                    }
                    itemClickListener.onItemLongClick(v, position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (imageData.size() >= imageCountLimit) {
            return imageCountLimit;
        } else {
            return imageData.size() + 1;
        }
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onAddImageClick();

        void onItemClick(int position);

        void onItemLongClick(View view, int position);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        private ItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}