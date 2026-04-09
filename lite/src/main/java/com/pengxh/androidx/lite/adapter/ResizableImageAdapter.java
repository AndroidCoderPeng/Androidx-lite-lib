package com.pengxh.androidx.lite.adapter;

import android.annotation.SuppressLint;
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
public class ResizableImageAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "ResizableImageAdapter";
    private final List<String> mImages;
    private final int mViewWidth;
    private final int mLimit = 9;
    private boolean mShowAddButton;

    /**
     * 数量可编辑图片适配器
     *
     * @param viewWidth RecyclerView实际宽度，一般情况下就是屏幕宽度，但是如果有其他控件和它在同一行，需要计算实际宽度，不然无法正确显示RecyclerView item的布局
     */
    public ResizableImageAdapter(List<String> mImages, int viewWidth) {
        this.mImages = mImages;
        this.mViewWidth = viewWidth;
        this.mShowAddButton = mImages.size() < mLimit;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_editable_rv_g, parent, false);
        int mSpanCount = 3;
        int imageSize = mViewWidth / mSpanCount;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
        view.findViewById(R.id.imageView).setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageView imageView = holder.getView(R.id.imageView);
        if (position == getItemCount() - 1 && mImages.size() < mLimit) {
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
            Glide.with(holder.itemView.getContext()).load(mImages.get(position)).into(imageView);
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
        if (mShowAddButton) {
            return mImages.size() + 1;
        } else {
            return mImages.size();
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

    public void addItem(String imagePath) {
        if (mImages.size() >= mLimit) return;
        mImages.add(imagePath);
        int insertedPosition = mImages.size() - 1;  // 新图片的位置
        if (mImages.size() == mLimit) {
            // 加到第9张：加号按钮消失，先通知 removed，再通知图片 inserted
            mShowAddButton = false;
            notifyItemRemoved(insertedPosition);   // 加号按钮消失
            notifyItemInserted(insertedPosition); // 第9张图片出现（同一位置，RecyclerView 会正确处理）
        } else {
            // 普通插入：加号按钮往后移动一格
            notifyItemInserted(insertedPosition);
        }
    }

    public void removeItem(int position) {
        if (position < 0 || position >= mImages.size()) return;
        boolean wasAtLimit = mImages.size() == mLimit;
        mImages.remove(position);
        if (wasAtLimit) {
            // 从9张删到8张：加号按钮重新出现
            mShowAddButton = true;
            notifyItemRemoved(position);
            notifyItemInserted(mImages.size()); // 加号按钮在末尾出现
        } else {
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        int oldCount = getItemCount();
        mImages.clear();
        mShowAddButton = true;  // 清空后加号按钮重新显示
        notifyItemRangeRemoved(0, oldCount);
    }


    public List<String> getImages() {
        return mImages;
    }
}