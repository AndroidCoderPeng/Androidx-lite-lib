package com.pengxh.androidx.lite.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pengxh.androidx.lite.R;

import java.util.List;

/**
 * 数量可编辑图片适配器
 */
public class EditableImageAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "EditableImageAdapter";
    private final Context context;
    private final List<String> images;
    private final int viewWidth;
    private final int imageCountLimit;
    private final int spanCount;
    private final LayoutInflater layoutInflater;

    /**
     * 数量可编辑图片适配器
     *
     * @param context         使用适配的上下文
     * @param viewWidth       RecyclerView实际宽度，一般情况下就是屏幕宽度，但是如果有其他控件和它在同一行，需要计算实际宽度，不然无法正确显示RecyclerView item的布局
     * @param imageCountLimit 最多显示的图片数目
     * @param spanCount       每行显示的图片数目
     */
    public EditableImageAdapter(Context context, List<String> images, int viewWidth, int imageCountLimit, int spanCount) {
        this.context = context;
        this.images = images;
        this.viewWidth = viewWidth;
        this.imageCountLimit = imageCountLimit;
        this.spanCount = spanCount;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_editable_rv_g, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageView imageView = holder.getView(R.id.imageView);
        int imageSize = viewWidth / spanCount;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
        imageView.setLayoutParams(params);

        if (position == getItemCount() - 1 && images.size() < imageCountLimit) {
            imageView.setImageResource(R.drawable.ic_add_pic);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加图片
                    if (itemClickListener == null) {
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
                        return true;
                    }
                    itemClickListener.onItemLongClick(v, holder.getBindingAdapterPosition());
                    return true;
                }
            });
        }
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