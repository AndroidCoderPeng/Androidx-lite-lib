package com.pengxh.androidx.lite.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

/**
 * RecyclerView单选适配器
 */
public abstract class SingleChoiceAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "SingleChoiceAdapter";
    private final int mXmlResource;
    private final List<T> mDataRows;
    //选择的位置
    private T mSelectedItem;

    public void setSelectedItem(T item) {
        mSelectedItem = item;
    }

    public SingleChoiceAdapter(@LayoutRes int xmlResource, List<T> dataRows) {
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

        // 根据 item 是否等于选中的 item 设置状态
        boolean isSelected = item == mSelectedItem;
        holder.itemView.setSelected(isSelected);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != mSelectedItem) {
                    T oldItem = mSelectedItem;
                    mSelectedItem = item;

                    int oldPosition = mDataRows.indexOf(oldItem);
                    if (oldPosition != -1) {
                        notifyItemChanged(oldPosition);
                    }

                    notifyItemChanged(oldPosition);

                    if (itemCheckedListener == null) {
                        return;
                    }
                    itemCheckedListener.onItemChecked(position, item);
                }
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
        void onItemChecked(int position, T item);
    }

    public void setOnItemCheckedListener(OnItemCheckedListener<T> listener) {
        itemCheckedListener = listener;
    }
}
