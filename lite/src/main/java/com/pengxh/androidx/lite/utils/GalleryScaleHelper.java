package com.pengxh.androidx.lite.utils;

import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryScaleHelper extends RecyclerView.OnScrollListener {

    private final LinearSnapHelper snapHelper = new LinearSnapHelper();
    // 卡片的padding, 卡片间的距离等于2倍的pagePadding
    private final int pagePadding = 15;
    // 左边卡片显示大小
    private final int leftCardShowWidth = 15;
    // 卡片宽度
    private int cardWidth = 0;
    private int currentItemOffset = 0;
    //当前卡片的index
    private int currentItemPos = 0;

    public void attachToRecyclerView(RecyclerView recyclerView) {
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                cardWidth = recyclerView.getWidth() - 2 * (pagePadding + leftCardShowWidth);
            }
        });
        recyclerView.addOnScrollListener(this);
        snapHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dx != 0) {
            currentItemOffset += dx;
            currentItemPos = currentItemOffset / cardWidth;

            // 边界条件处理
            int minus = recyclerView.getAdapter() != null ? recyclerView.getAdapter().getItemCount() - 1 : 0;
            currentItemPos = Math.max(0, Math.min(currentItemPos, minus));

            int offset = currentItemOffset - currentItemPos * cardWidth;
            float percent = Math.max(Math.abs(offset) / (float) cardWidth, 0.0001f);

            View leftView = null;
            View rightView = null;

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager != null) {
                if (currentItemPos > 0) {
                    leftView = layoutManager.findViewByPosition(currentItemPos - 1);
                }
                View currentView = layoutManager.findViewByPosition(currentItemPos);
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                if (adapter != null) {
                    if (currentItemPos < adapter.getItemCount() - 1) {
                        rightView = layoutManager.findViewByPosition(currentItemPos + 1);
                    }
                }

                // 两边视图缩放比例
                float scale = 0.9f;
                if (leftView != null) {
                    leftView.setScaleY((1 - scale) * percent + scale);
                }
                if (currentView != null) {
                    currentView.setScaleY((scale - 1) * percent + 1);
                }
                if (rightView != null) {
                    rightView.setScaleY((1 - scale) * percent + scale);
                }
            }
        }
    }

    public void removeOnScrollListener(RecyclerView recyclerView) {
        recyclerView.removeOnScrollListener(this);
    }

    public int getCurrentIndex() {
        return currentItemPos;
    }
}
