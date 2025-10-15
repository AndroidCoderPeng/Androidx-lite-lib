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

    private final float leftMargin;
    private final float rightMargin;
    private final Paint dividerPaint = new Paint();

    public RecyclerViewItemDivider(float leftMargin, float rightMargin, int color) {
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.dividerPaint.setColor(color);
        this.dividerPaint.setStrokeWidth(1);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            c.drawLine(leftMargin, view.getBottom(), view.getWidth() - rightMargin, view.getBottom(), dividerPaint);
        }
    }
}
