package com.pengxh.androidx.lite.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * RecyclerView多选适配器
 */
public abstract class MultipleChoiceAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "MultipleChoiceAdapter";
    private final int xmlResource;
    private final List<T> dataRows;
    private final Set<Integer> multipleSelected = new HashSet<>();
    private final List<T> selectedItems = new ArrayList<>();

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
        convertView(holder, position, dataRows.get(position));

        holder.itemView.setSelected(multipleSelected.contains(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCheckedListener == null) {
                    Log.e(TAG, "onClick: itemClickListener not init");
                    return;
                }

                if (multipleSelected.contains(position)) {
                    multipleSelected.remove(position);
                    selectedItems.remove(dataRows.get(position));
                    holder.itemView.setSelected(false);
                } else {
                    multipleSelected.add(position);
                    selectedItems.add(dataRows.get(position));
                    holder.itemView.setSelected(true);
                }
                itemCheckedListener.onItemChecked(selectedItems);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRefreshData(List<T> dataRows) {
        this.dataRows.clear();
        this.dataRows.addAll(dataRows);
        notifyDataSetChanged();
    }

    public void setLoadMoreData(List<T> dataRows) {
        this.dataRows.addAll(dataRows);
        notifyItemRangeInserted(this.dataRows.size(), dataRows.size());
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
