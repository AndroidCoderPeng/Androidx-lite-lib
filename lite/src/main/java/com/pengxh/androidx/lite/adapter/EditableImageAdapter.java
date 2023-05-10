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
public class EditableImageAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "EditableImageAdapter";
    private final Context context;
    private final int imageCountLimit;
    private final int screenWidth;
    private final float spacing;
    private final LayoutInflater layoutInflater;
    private List<String> images = new ArrayList<>();

    /**
     * 数量可编辑图片适配器
     *
     * @param imageCountLimit 最多显示几张图片，每行3张图片
     * @param spacing         上下左右外边距，无需在 {@link androidx.recyclerview.widget.RecyclerView} 设置边距
     */
    public EditableImageAdapter(Context context, int imageCountLimit, float spacing) {
        this.context = context;
        this.imageCountLimit = imageCountLimit;
        this.spacing = spacing;
        this.screenWidth = ContextHub.getScreenWidth(context);
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setupImage(List<String> images) {
        this.images = images;
        notifyItemRangeChanged(0, images.size());
    }

    public void deleteImage(int position) {
        if (!images.isEmpty()) {
            images.remove(position);
            /**
             * 发生变化的item数目
             * */
            notifyItemRangeRemoved(position, 1);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_editable_rv_g, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageView imageView = holder.getView(R.id.imageView);
        configImageParams(imageView, position);
        if (position == getItemCount() - 1 && images.size() < imageCountLimit) {
            imageView.setImageResource(R.drawable.ic_add_pic);
            imageView.setOnClickListener(new View.OnClickListener() {
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
            Glide.with(context).load(images.get(position)).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击操作，查看大图
                    if (itemClickListener == null) {
                        Log.e(TAG, "onClick: itemClickListener not init");
                        return;
                    }
                    itemClickListener.onItemClick(holder.getBindingAdapterPosition());
                }
            });
            // 长按监听
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //长按删除
                    if (itemClickListener == null) {
                        Log.e(TAG, "onClick: itemClickListener not init");
                        return true;
                    }
                    itemClickListener.onItemLongClick(v, holder.getBindingAdapterPosition());
                    return true;
                }
            });
        }
    }

    private void configImageParams(ImageView imageView, int position) {
        int temp = FloatHub.dp2px(context, spacing);
        int imageSize = (screenWidth - temp * 3) / 3;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
        switch (position) {
            case 0:
                params.setMargins(temp, temp, temp >> 1, temp >> 1);
                break;
            case 1:
                params.setMargins(temp >> 1, temp, temp >> 1, temp >> 1);
                break;
            case 2:
                params.setMargins(temp >> 1, temp, temp, temp >> 1);
                break;
            case 3:
                params.setMargins(temp, temp >> 1, temp >> 1, temp >> 1);
                break;
            case 4:
                params.setMargins(temp >> 1, temp >> 1, temp >> 1, temp >> 1);
                break;
            case 5:
                params.setMargins(temp >> 1, temp >> 1, temp, temp >> 1);
                break;
            case 6:
                params.setMargins(temp, temp >> 1, temp >> 1, temp);
                break;
            case 7:
                params.setMargins(temp >> 1, temp >> 1, temp >> 1, temp);
                break;
            case 8:
                params.setMargins(temp >> 1, temp >> 1, temp, temp);
                break;
        }
        imageView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        if (images.size() >= imageCountLimit) {
            return imageCountLimit;
        } else {
            return images.size() + 1;
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
}