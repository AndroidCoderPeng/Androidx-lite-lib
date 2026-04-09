package com.pengxh.androidx.lite.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * RecyclerView多选适配器
 * <p>
 * 注意：此方案要求 T 正确实现 equals() 和 hashCode()
 */
public abstract class MultipleChoiceAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "MultipleChoiceAdapter";
    private final int mXmlResource;
    private final List<T> mDataRows;
    private final Set<T> mSelectedItems = Collections.synchronizedSet(new HashSet<>());

    public MultipleChoiceAdapter(int xmlResource, List<T> dataRows) {
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
        T item = mDataRows.get(position);
        convertView(holder, position, item);

        holder.itemView.setSelected(mSelectedItems.contains(item));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (mSelectedItems) {
                    if (mSelectedItems.contains(item)) {
                        mSelectedItems.remove(item);
                        holder.itemView.setSelected(false);
                    } else {
                        mSelectedItems.add(item);
                        holder.itemView.setSelected(true);
                    }
                }

                if (itemCheckedListener == null) {
                    Log.d(TAG, "No listener set for item checked events");
                    return;
                }
                itemCheckedListener.onItemChecked(new ArrayList<>(mSelectedItems));
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
        synchronized(mDataRows) {
            mDataRows.addAll(newRows);
        }
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
