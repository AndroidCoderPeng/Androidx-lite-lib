package com.pengxh.androidx.lite.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.hub.ContextHub;
import com.pengxh.androidx.lite.hub.FloatHub;

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
    private final int screenWidth;
    private final float spacing;
    private final LayoutInflater layoutInflater;
    private List<String> imageData = new ArrayList<>();

    /**
     * @param imageCountLimit 最多显示几张图片
     * @param spacing         RecyclerView边距，左右外边距+ImageView内边距,单位dp
     */
    public EditableImageAdapter(Context context, int imageCountLimit, float spacing) {
        this.context = context;
        this.imageCountLimit = imageCountLimit;
        this.spacing = spacing;
        this.screenWidth = ContextHub.getScreenWidth(context);
        this.layoutInflater = LayoutInflater.from(context);
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

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(layoutInflater.inflate(R.layout.item_gridview_editable, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        configImageParams(holder.imageView, position);
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

    private void configImageParams(ImageView imageView, int position) {
        int totalPadding = FloatHub.dp2px(context, spacing) * 4;
        int imageSize = (screenWidth - totalPadding) / 3;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
        if (position >= 3 && position <= 5) {
            params.setMargins(0, FloatHub.dp2px(context, spacing), 0, FloatHub.dp2px(context, spacing));
        } else {
            params.setMargins(0, 0, 0, 0);
        }
        imageView.setLayoutParams(params);
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
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}