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
                    Log.e(TAG, "onClick: itemClickListener not init");
                    return;
                }
                itemClickedListener.onItemClicked(position, dataRows.get(position));
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

    private OnItemClickedListener<T> itemClickedListener;

    public interface OnItemClickedListener<T> {
        void onItemClicked(int position, T t);
    }

    public void setOnItemClickedListener(OnItemClickedListener<T> listener) {
        itemClickedListener = listener;
    }
}
