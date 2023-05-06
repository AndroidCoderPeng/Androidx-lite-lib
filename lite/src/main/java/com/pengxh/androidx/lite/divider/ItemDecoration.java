package com.pengxh.androidx.lite.divider;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 可以设置左右边距的RecyclerView分割线
 */
public class ItemDecoration extends RecyclerView.ItemDecoration {

    private final float leftMargin;
    private final float rightMargin;
    private final Paint bottomLinePaint = new Paint();

    public ItemDecoration(float leftMargin, float rightMargin) {
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;

        bottomLinePaint.setAntiAlias(true);
        bottomLinePaint.setColor(Color.LTGRAY);
    }

    //画分割线
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            c.drawRect(leftMargin, view.getBottom(), parent.getWidth() - rightMargin, (view.getBottom() + 1), bottomLinePaint);
        }
    }
}