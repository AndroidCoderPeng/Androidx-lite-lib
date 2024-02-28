package com.pengxh.androidx.lite.divider;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 设置 {@link androidx.recyclerview.widget.RecyclerView} Item的分割线
 */
public class RecyclerViewItemDivider extends RecyclerView.ItemDecoration {

    private final Paint dividerPaint = new Paint();

    public RecyclerViewItemDivider(int strokeWidth, int color) {
        dividerPaint.setColor(color);
        dividerPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            c.drawLine(0f, view.getBottom(), view.getWidth(), view.getBottom(), dividerPaint);
        }
    }
}
