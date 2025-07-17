package com.pengxh.androidx.lite.adapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * RecyclerView普通列表适配器
 */
public abstract class NormalRecyclerAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "NormalRecyclerAdapter";
    private final int xmlResource;
    private final List<T> dataRows;

    public NormalRecyclerAdapter(@LayoutRes int xmlResource, List<T> dataRows) {
        this.xmlResource = xmlResource;
        this.dataRows = dataRows;
    }

    @Override
    public int getItemCount() {
        return dataRows.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(xmlResource, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        convertView(holder, position, dataRows.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickedListener == null) {
                    return;
                }
                itemClickedListener.onItemClicked(position, dataRows.get(position));
            }
        });
    }

    public void refresh(List<T> newRows, ItemComparator<T> itemComparator) {
        if (newRows.isEmpty()) {
            return;
        }
        if (itemComparator != null) {
            List<T> oldDataSnapshot = new ArrayList<>(dataRows); // 旧数据副本
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
                    return itemComparator.areItemsTheSame(
                            oldDataSnapshot.get(oldItemPosition),
                            newDataSnapshot.get(newItemPosition)
                    );
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return itemComparator.areContentsTheSame(
                            oldDataSnapshot.get(oldItemPosition),
                            newDataSnapshot.get(newItemPosition)
                    );
                }
            };

            // 在子线程计算 Diff
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        result.dispatchUpdatesTo(NormalRecyclerAdapter.this);
                        dataRows.clear();
                        dataRows.addAll(newDataSnapshot);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            dataRows.clear();
            dataRows.addAll(newRows);
            notifyItemRangeChanged(0, dataRows.size());
        }
    }

    /**
     * 加载更多
     */
    public void loadMore(List<T> newRows) {
        if (newRows.isEmpty()) {
            return;
        }
        int startPosition = this.dataRows.size();
        this.dataRows.addAll(newRows);
        notifyItemRangeInserted(startPosition, newRows.size());
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
