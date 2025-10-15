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

/**
 * RecyclerView单选适配器
 */
public abstract class SingleChoiceAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "SingleChoiceAdapter";
    private final int mXmlResource;
    private final List<T> mDataRows;
    //选择的位置
    private int selectedPosition = -1;

    public void setSelectedPosition(int position) {
        if (position >= 0 && position < mDataRows.size()) {
            selectedPosition = position;
        } else {
            Log.d(TAG, "Invalid position: $position");
        }
    }

    public SingleChoiceAdapter(@LayoutRes int xmlResource, List<T> dataRows) {
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
        if (position >= 0 && position < mDataRows.size()) {
            convertView(holder, position, mDataRows.get(position));

            holder.itemView.setSelected(holder.getLayoutPosition() == selectedPosition);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.getLayoutPosition() != selectedPosition) {
                        int oldPosition = selectedPosition;
                        selectedPosition = holder.getLayoutPosition();
                        holder.itemView.setSelected(true);
                        notifyItemChanged(oldPosition);
                        if (itemCheckedListener == null) {
                            return;
                        }
                        itemCheckedListener.onItemChecked(position, mDataRows.get(position));
                    }
                }
            });
        } else {
            Log.d(TAG, "Invalid position: $position");
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
        mDataRows.addAll(newRows);
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
