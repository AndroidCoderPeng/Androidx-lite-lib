package com.pengxh.androidx.lite.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
        if (itemComparator == null) {
            DiffUtil.Callback diffCallback = new DiffUtil.Callback() {

                @Override
                public int getOldListSize() {
                    return dataRows.size();
                }

                @Override
                public int getNewListSize() {
                    return newRows.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return itemComparator.areItemsTheSame(dataRows.get(oldItemPosition), newRows.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return itemComparator.areContentsTheSame(dataRows.get(oldItemPosition), newRows.get(newItemPosition));
                }
            };
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback, true);
            dataRows.clear();
            dataRows.addAll(newRows);
            diffResult.dispatchUpdatesTo(this);
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
