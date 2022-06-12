package com.pengxh.androidx.lite.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

@SuppressLint("NotifyDataSetChanged")
public class EditableImageAdapter extends RecyclerView.Adapter<EditableImageAdapter.ItemViewHolder> {

    private final Context context;
    private final int imageCountLimit;
    private List<String> imageData = new ArrayList<>();

    public EditableImageAdapter(Context context, int imageCountLimit) {
        this.context = context;
        this.imageCountLimit = imageCountLimit;
    }

    public void setupImage(List<String> images) {
        this.imageData = images;
        notifyDataSetChanged();
    }

    public void deleteImage(int position) {
        if (imageData.size() != 0) {
            imageData.remove(position);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        /**
         * CarrView水平外边距5dp
         * RelativeLayout水平内边距10dp
         * RecyclerView左边距100dp
         * */
        int realWidth = DeviceSizeUtil.getScreenWidth(context) - DeviceSizeUtil.dp2px(context, 130);
        int margins = DeviceSizeUtil.dp2px(context, 3);
        int itemSize = (realWidth - 4 * margins) / 2;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemSize, itemSize);
        params.setMargins(margins, margins, margins, margins);
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
                    mOnItemClickListener.onAddImageClick();
                }
            });
        } else {
            Glide.with(context).load(imageData.get(position)).into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击操作，查看大图
                    mOnItemClickListener.onItemClick(position);
                }
            });
            // 长按监听
            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //长按删除
                    mOnItemClickListener.onItemLongClick(v, position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // 满imageCountLimit张图就不让其添加新图
        if (imageData != null && imageData.size() >= imageCountLimit) {
            return imageCountLimit;
        } else {
            return imageData == null ? 1 : imageData.size() + 1;
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
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