package com.pengxh.androidx.lite.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * RecyclerView多选适配器
 */
public abstract class MultipleChoiceAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "MultipleChoiceAdapter";
    private final int mXmlResource;
    private final List<T> mDataRows;
    private final ConcurrentHashMap<Integer, Boolean> mMultipleSelected = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<T> mSelectedItems = new CopyOnWriteArrayList<>();

    public MultipleChoiceAdapter(@LayoutRes int xmlResource, List<T> dataRows) {
        this.mXmlResource = xmlResource;
        this.mDataRows = dataRows;
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
            Log.d(TAG, "Invalid position: " + position);
            return;
        }

        convertView(holder, position, mDataRows.get(position));

        holder.itemView.setSelected(mMultipleSelected.containsKey(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMultipleSelected.containsKey(position)) {
                    mMultipleSelected.remove(position);
                    mSelectedItems.removeIf(item -> item.equals(mDataRows.get(position)));
                    holder.itemView.setSelected(false);
                } else {
                    mMultipleSelected.put(position, true);
                    mSelectedItems.add(mDataRows.get(position));
                    holder.itemView.setSelected(true);
                }

                if (itemCheckedListener == null) {
                    Log.d(TAG, "No listener set for item checked events");
                    return;
                }
                itemCheckedListener.onItemChecked(mSelectedItems);
            }
        });
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
        mDataRows.addAll(newRows);
        notifyItemRangeInserted(startPosition, newSize);
    }

    public abstract void convertView(ViewHolder viewHolder, int position, T item);

    private OnItemCheckedListener<T> itemCheckedListener;

    public interface OnItemCheckedListener<T> {
        void onItemChecked(List<T> items);
    }

    public void setOnItemCheckedListener(OnItemCheckedListener<T> listener) {
        itemCheckedListener = listener;
    }
}
