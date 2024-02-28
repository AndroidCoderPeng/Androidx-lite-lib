package com.pengxh.androidx.lite.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

/**
 * 设置 {@link androidx.recyclerview.widget.RecyclerView} 吸顶分割线
 */
public class RecyclerStickDecoration extends RecyclerView.ItemDecoration {

    private final Paint topGapPaint = new Paint();
    private final Paint dividerPaint = new Paint();
    private final TextPaint textPaint = new TextPaint();
    private final Rect textRect = new Rect();

    private int topGap = 0;
    private ViewGroupListener listener;

    public RecyclerStickDecoration setTopGap(int topGap) {
        this.topGap = topGap;
        return this;
    }

    public RecyclerStickDecoration setViewGroupListener(ViewGroupListener listener) {
        this.listener = listener;
        return this;
    }

    public RecyclerStickDecoration build() {
        topGapPaint.setAntiAlias(true);
        topGapPaint.setColor(Color.parseColor("#F1F1F1"));

        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(1f);
        dividerPaint.setColor(Color.LTGRAY);

        textPaint.setAntiAlias(true);
        //字体占用topGap的75%
        textPaint.setTextSize(topGap * 0.75f);
        textPaint.setColor(Color.BLACK);
        return this;
    }

    /**
     * 调整item顶部间距作为吸顶区域
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
        long groupTag = listener.groupTag(pos);
        if (groupTag < 0) {
            return;
        }
        //同组的第一个才添加padding
        if (pos == 0 || isSameGroup(pos)) {
            outRect.top = topGap;
        } else {
            outRect.top = 0;
        }
    }

    /**
     * 判断是否为同组数据
     */
    private boolean isSameGroup(int pos) {
        if (pos == 0) {
            return true;
        } else {
            long prevGroupId = listener.groupTag(pos - 1);
            long groupId = listener.groupTag(pos);
            return prevGroupId != groupId;
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            c.drawLine(0f, view.getBottom(), view.getWidth(), view.getBottom(), dividerPaint);
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        long lastGroupId;
        long groupId = -1L;
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            lastGroupId = groupId;
            groupId = listener.groupTag(position);
            if (groupId < 0 || groupId == lastGroupId) continue;
            String firstLetter = listener.groupFirstLetter(position).toUpperCase(Locale.getDefault());
            if (firstLetter.isEmpty()) continue;
            int viewBottom = Math.max(topGap, view.getTop());
            //下一个和当前不一样移动当前
            if (position + 1 < state.getItemCount()) {
                long nextGroupId = listener.groupTag(position + 1);
                //组内最后一个view进入了header
                if (nextGroupId != groupId && view.getBottom() < viewBottom) {
                    viewBottom = view.getBottom();
                }
            }
            //绘制吸顶底部背景
            c.drawRect(0f, viewBottom - topGap, view.getWidth(), viewBottom, topGapPaint);

            //绘制吸顶文字
            textPaint.getTextBounds(firstLetter, 0, firstLetter.length(), textRect);
            int textWidth = textRect.width();
            c.drawText(firstLetter, view.getLeft() + textWidth, viewBottom - topGap / 4f, textPaint);
        }
    }

    /**
     * 点击某个字母将RecyclerView滑动到item顶部
     */
    static class SmoothGroupTopScroller extends LinearSmoothScroller {
        public SmoothGroupTopScroller(Context context) {
            super(context);
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }
    }

    public interface ViewGroupListener {
        long groupTag(int position);

        String groupFirstLetter(int position);
    }
}
