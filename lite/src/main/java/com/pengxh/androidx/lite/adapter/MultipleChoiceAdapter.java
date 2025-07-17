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
    private final int xmlResource;
    private final List<T> dataRows;
    private final ConcurrentHashMap<Integer, Boolean> multipleSelected = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<T> selectedItems = new CopyOnWriteArrayList<>();

    public MultipleChoiceAdapter(@LayoutRes int xmlResource, List<T> dataRows) {
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
        if (position < 0 || position >= dataRows.size()) {
            Log.d(TAG, "Invalid position: " + position);
            return;
        }

        convertView(holder, position, dataRows.get(position));

        holder.itemView.setSelected(multipleSelected.containsKey(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multipleSelected.containsKey(position)) {
                    multipleSelected.remove(position);
                    selectedItems.removeIf(item -> item.equals(dataRows.get(position)));
                    holder.itemView.setSelected(false);
                } else {
                    multipleSelected.put(position, true);
                    selectedItems.add(dataRows.get(position));
                    holder.itemView.setSelected(true);
                }

                if (itemCheckedListener == null) {
                    Log.d(TAG, "No listener set for item checked events");
                    return;
                }
                itemCheckedListener.onItemChecked(selectedItems);
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
        int startPosition = dataRows.size();
        int newSize = newRows.size();
        dataRows.addAll(newRows);
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
