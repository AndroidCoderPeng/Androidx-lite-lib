package com.pengxh.androidx.lite.divider;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 可以设置上下间距的RecyclerView分割线
 */
public class VerticalMarginItemDecoration extends RecyclerView.ItemDecoration {

    private final int topMargin;
    private final int bottomMargin;

    public VerticalMarginItemDecoration(int topMargin, int bottomMargin) {
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
    }

    //设置Item间的间隔

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, topMargin, 0, bottomMargin);
    }
}