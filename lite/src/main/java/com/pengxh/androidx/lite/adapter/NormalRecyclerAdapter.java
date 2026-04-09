package com.pengxh.androidx.lite.adapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RecyclerView普通列表适配器
 */
public abstract class NormalRecyclerAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "NormalRecyclerAdapter";
    private final int mXmlResource;
    private final List<T> mDataRows;

    public NormalRecyclerAdapter(int xmlResource, List<T> dataRows) {
        this.mXmlResource = xmlResource;
        this.mDataRows = Collections.synchronizedList(dataRows);
    }

    @Override
    public int getItemCount() {
        return mDataRows.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(mXmlResource, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position < 0 || position >= mDataRows.size()) {
            Log.w(TAG, "onBindViewHolder: invalid position=" + position + ", size=" + mDataRows.size());
            return;
        }

        T item = mDataRows.get(position);
        convertView(holder, position, item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickedListener == null) {
                    return;
                }
                // 点击时重新获取当前 position 对应的 item
                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < mDataRows.size()) {
                    itemClickedListener.onItemClicked(currentPosition, mDataRows.get(currentPosition));
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refresh(List<T> newRows, ItemComparator<T> itemComparator) {
        if (newRows.isEmpty()) {
            Log.d(TAG, "refresh: newRows isEmpty");
            return;
        }

        int oldSize = mDataRows.size();

        if (itemComparator != null) {
            List<T> oldDataSnapshot = new ArrayList<>(mDataRows); // 旧数据副本
            List<T> newDataSnapshot = new ArrayList<>(newRows); // 新数据副本

            DiffUtil.Callback diffCallback = new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return oldDataSnapshot.size();
                }

                @Override
                public int getNewListSize() {
                    return newDataSnapshot.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return itemComparator.areItemsTheSame(oldDataSnapshot.get(oldItemPosition), newDataSnapshot.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return itemComparator.areContentsTheSame(oldDataSnapshot.get(oldItemPosition), newDataSnapshot.get(newItemPosition));
                }
            };

            // 在子线程计算 Diff
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback);
                    synchronized (mDataRows) {
                        mDataRows.clear();
                        mDataRows.addAll(newDataSnapshot);
                    }
                    new Handler(Looper.getMainLooper()).post(() -> result.dispatchUpdatesTo(NormalRecyclerAdapter.this));
                } catch (Exception e) {
                    // 回退到全量刷新
                    synchronized (mDataRows) {
                        mDataRows.clear();
                        mDataRows.addAll(newDataSnapshot);
                    }
                    notifyDataSetChanged();
                }
            });
        } else {
            int newSize = newRows.size();
            synchronized(mDataRows) {
                mDataRows.clear();
                mDataRows.addAll(newRows);
            }

            // 新数据比旧数据少，需要通知删除部分 item ，否则会越界
            if (newSize < oldSize) {
                notifyItemRangeRemoved(newSize, oldSize - newSize);
            }
            notifyItemRangeChanged(0, newSize);
        }
    }

    /**
     * 加载更多
     */
    public void loadMore(List<T> newRows) {
        if (newRows.isEmpty()) {
            Log.d(TAG, "loadMore: newRows isEmpty");
            return;
        }
        int startPosition = mDataRows.size();
        int newSize = newRows.size();
        synchronized(mDataRows) {
            mDataRows.addAll(newRows);
        }
        notifyItemRangeInserted(startPosition, newSize);
    }

    public abstract void convertView(ViewHolder viewHolder, int position, T item);

    private OnItemClickedListener<T> itemClickedListener;

    public interface OnItemClickedListener<T> {
        void onItemClicked(int position, T t);
    }

    public void setOnItemClickedListener(OnItemClickedListener<T> listener) {
        itemClickedListener = listener;
    }

    public interface ItemComparator<T> {
        boolean areItemsTheSame(T oldItem, T newItem);

        boolean areContentsTheSame(T oldItem, T newItem);
    }
}
