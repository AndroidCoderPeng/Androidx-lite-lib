package com.pengxh.androidx.lite.divider;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 设置 {@link androidx.recyclerview.widget.RecyclerView} Item外边距，也可以代替设置上下左右分割线
 */
public class RecyclerViewItemOffsets extends RecyclerView.ItemDecoration {

    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    public RecyclerViewItemOffsets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = left;
        outRect.top = top;
        outRect.right = right;
        outRect.bottom = bottom;
    }
}
