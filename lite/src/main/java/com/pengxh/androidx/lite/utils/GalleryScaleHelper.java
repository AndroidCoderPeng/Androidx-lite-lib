package com.pengxh.androidx.lite.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryScaleHelper {

    private final String TAG = "GalleryScaleHelper";
    private final LinearSnapHelper snapHelper = new LinearSnapHelper();
    // 卡片的padding, 卡片间的距离等于2倍的pagePadding
    private final int pagePadding = 15;
    // 左边卡片显示大小
    private final int leftCardShowWidth = 15;
    // 两边视图缩放比例
    private final float scale = 0.9f;
    // 卡片宽度
    private int cardWidth = 0;
    private int currentItemOffset = 0;

    //当前卡片的index
    private int currentItemPos = 0;

    public void attachToRecyclerView(RecyclerView recyclerView) {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                cardWidth = recyclerView.getWidth() - 2 * (pagePadding + leftCardShowWidth);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx != 0) {
                    currentItemOffset += dx;

                    currentItemPos = currentItemOffset / cardWidth;

                    int offset = currentItemOffset - currentItemPos * cardWidth;
                    float percent = (float) Math.max(Math.abs(offset) * 1.0 / cardWidth, 0.0001);

                    View leftView = null;
                    View rightView = null;

                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        if (currentItemPos > 0) {
                            leftView = layoutManager.findViewByPosition(currentItemPos - 1);
                        }
                        View currentView = layoutManager.findViewByPosition(currentItemPos);

                        int childCount = recyclerView.getChildCount();
                        if (currentItemPos < childCount - 1) {
                            rightView = layoutManager.findViewByPosition(currentItemPos + 1);
                        }

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
        });
        snapHelper.attachToRecyclerView(recyclerView);
    }

    public int getCurrentIndex() throws IndexOutOfBoundsException {
        return currentItemPos;
    }
}
