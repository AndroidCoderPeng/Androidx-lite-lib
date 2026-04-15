package com.pengxh.androidx.lib.calendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.pengxh.androidx.lib.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarView extends View {

    // ==================== 接口定义 ====================
    public interface OnDateSelectedListener {
        void onDateSelected(Calendar date);
    }

    // ==================== 内部类 ====================
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int FLING_THRESHOLD = 600;

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            handleClick(e.getX(), e.getY());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            if (isMonthAnimating) return true;

            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                if (velocityX < -FLING_THRESHOLD) {
                    switchMonthWithAnim(1);
                    return true;
                } else if (velocityX > FLING_THRESHOLD) {
                    switchMonthWithAnim(-1);
                    return true;
                }
            }
            return false;
        }
    }

    // ==================== 常量 ====================
    private static final long MONTH_ANIM_DURATION = 260L;

    private final String[] weekLabels = {"日", "一", "二", "三", "四", "五", "六"};

    private final int colorBlue = Color.parseColor("#2F80ED");
    private final int colorNormal = Color.parseColor("#333333");
    private final int colorLight = Color.parseColor("#999999");
    private final int colorWhite = Color.parseColor("#FFFFFF");
    private final int colorLine = Color.parseColor("#EEEEEE");
    private final int colorWeekend = Color.parseColor("#E53935");

    // ==================== Paint 对象 ====================
    private final Paint monthTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dayTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint weekTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint selectedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint todayTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // ==================== 状态变量 ====================
    private final Calendar today = Calendar.getInstance();
    private final Calendar currentMonth = Calendar.getInstance();
    private Calendar selectedDate = null;

    private final List<DayCell> monthCells = new ArrayList<>();
    private final List<DayCell> animMonthCells = new ArrayList<>();

    private boolean isMonthAnimating = false;
    private float monthAnimOffset = 0f;
    private int monthAnimDirection = 0;

    // ==================== 布局参数 ====================
    private int headerHeight;
    private int weekBarHeight;
    private int rowHeight;
    private int cellWidth;

    // ==================== 其他组件 ====================
    private Drawable blueCircleDrawable;
    private GestureDetector gestureDetector;

    // ==================== 监听器 ====================
    private OnDateSelectedListener onDateSelectedListener;

    // ==================== 构造函数 ====================
    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    // ==================== 初始化方法 ====================
    private void init(Context context) {
        headerHeight = dp(52);
        weekBarHeight = dp(40);
        rowHeight = dp(48);

        initPaints();

        blueCircleDrawable = ContextCompat.getDrawable(context, R.drawable.bg_calendar_blue_circle);
        gestureDetector = new GestureDetector(context, new GestureListener());

        buildMonthCells();
    }

    private void initPaints() {
        monthTitlePaint.setTextSize(sp(20));
        monthTitlePaint.setColor(colorNormal);
        monthTitlePaint.setFakeBoldText(true);
        monthTitlePaint.setTextAlign(Paint.Align.CENTER);

        dayTextPaint.setTextSize(sp(16));
        dayTextPaint.setColor(colorNormal);
        dayTextPaint.setTextAlign(Paint.Align.CENTER);

        weekTextPaint.setTextSize(sp(14));
        weekTextPaint.setColor(colorLight);
        weekTextPaint.setTextAlign(Paint.Align.CENTER);

        selectedTextPaint.setTextSize(sp(16));
        selectedTextPaint.setColor(colorBlue);
        selectedTextPaint.setFakeBoldText(true);
        selectedTextPaint.setTextAlign(Paint.Align.CENTER);

        todayTextPaint.setTextSize(sp(16));
        todayTextPaint.setColor(colorWhite);
        todayTextPaint.setFakeBoldText(true);
        todayTextPaint.setTextAlign(Paint.Align.CENTER);

        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1f);
        linePaint.setColor(colorLine);
    }

    // ==================== 工具方法 ====================
    private String getMonthTitle(Calendar calendar) {
        return String.format(Locale.getDefault(), "%d年%d月",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1);
    }

    private int dp(float value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }

    private int sp(float value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                value,
                getResources().getDisplayMetrics()
        );
    }

    private Calendar cloneCalendar(Calendar src) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(src.getTimeInMillis());
        return c;
    }

    private boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
                && a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH);
    }

    // ==================== 数据构建 ====================
    private void buildMonthCells() {
        buildMonthCellsFor(currentMonth, monthCells);
    }

    private void buildMonthCellsFor(Calendar month, List<DayCell> outCells) {
        outCells.clear();

        Calendar temp = cloneCalendar(month);
        temp.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayWeek = temp.get(Calendar.DAY_OF_WEEK);
        int offset = firstDayWeek - Calendar.SUNDAY;

        temp.add(Calendar.DAY_OF_MONTH, -offset);

        for (int i = 0; i < 42; i++) {
            boolean isCurrent = temp.get(Calendar.MONTH) == month.get(Calendar.MONTH)
                    && temp.get(Calendar.YEAR) == month.get(Calendar.YEAR);
            outCells.add(new DayCell(temp, isCurrent));
            temp = cloneCalendar(temp);
            temp.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    // ==================== 测量和布局 ====================
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = headerHeight + weekBarHeight + rowHeight * 6;
        setMeasuredDimension(width, height);
    }

    // ==================== 绘制相关 ====================
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        cellWidth = getWidth() / 7;

        drawMonthTitle(canvas);
        drawWeekHeader(canvas);
        drawCalendarBody(canvas);
    }

    private void drawMonthTitle(Canvas canvas) {
        drawMonthTitleText(canvas, currentMonth, monthAnimOffset);

        if (isMonthAnimating) {
            Calendar targetMonth = cloneCalendar(currentMonth);
            targetMonth.add(Calendar.MONTH, monthAnimDirection);

            float incomingOffset = monthAnimDirection > 0
                    ? monthAnimOffset + getWidth()
                    : monthAnimOffset - getWidth();

            drawMonthTitleText(canvas, targetMonth, incomingOffset);
        }

        canvas.drawLine(0, headerHeight, getWidth(), headerHeight, linePaint);
    }

    private void drawMonthTitleText(Canvas canvas, Calendar month, float offsetX) {
        String title = getMonthTitle(month);

        int restoreAlpha = monthTitlePaint.getAlpha();
        if (isMonthAnimating) {
            float progress = Math.min(1f, Math.abs(offsetX) / Math.max(1, getWidth()));
            int alpha = (int) (255 * (1f - progress * 0.2f));
            monthTitlePaint.setAlpha(alpha);
        }

        Paint.FontMetrics fm = monthTitlePaint.getFontMetrics();
        float cx = getWidth() / 2f + offsetX;
        float cy = headerHeight / 2f;
        float baseline = cy + (Math.abs(fm.ascent) - fm.descent) / 2f;
        canvas.drawText(title, cx, baseline, monthTitlePaint);

        monthTitlePaint.setAlpha(restoreAlpha);
    }

    private void drawWeekHeader(Canvas canvas) {
        Paint.FontMetrics fm = weekTextPaint.getFontMetrics();
        float baseline = headerHeight + weekBarHeight / 2f + (Math.abs(fm.ascent) - fm.descent) / 2f;

        for (int i = 0; i < 7; i++) {
            float x = cellWidth * i + cellWidth / 2f;

            Paint paint = new Paint(weekTextPaint);
            if (i == 0 || i == 6) {
                paint.setColor(colorWeekend);
            } else {
                paint.setColor(colorLight);
            }

            canvas.drawText(weekLabels[i], x, baseline, paint);
        }

        canvas.drawLine(0, headerHeight + weekBarHeight, getWidth(), headerHeight + weekBarHeight, linePaint);
    }

    private void drawCalendarBody(Canvas canvas) {
        drawMonthCells(canvas, monthCells, monthAnimOffset, true);

        if (isMonthAnimating) {
            float incomingOffset = monthAnimDirection > 0
                    ? monthAnimOffset + getWidth()
                    : monthAnimOffset - getWidth();
            drawMonthCells(canvas, animMonthCells, incomingOffset, false);
        }
    }

    private void drawMonthCells(Canvas canvas, List<DayCell> cells, float offsetX, boolean updateRect) {
        float top = headerHeight + weekBarHeight;

        int alpha = 255;
        if (isMonthAnimating) {
            float progress = Math.min(1f, Math.abs(offsetX) / Math.max(1, getWidth()));
            alpha = (int) (255 * (1f - progress * 0.15f));
        }

        for (int i = 0; i < cells.size(); i++) {
            int row = i / 7;
            int col = i % 7;

            float left = col * cellWidth + offsetX;
            float cellTop = top + row * rowHeight;
            float right = left + cellWidth;
            float bottom = cellTop + rowHeight;

            DayCell cell = cells.get(i);

            if (updateRect) {
                cell.rect.set((int) left, (int) cellTop, (int) right, (int) bottom);
            }

            if (right < 0 || left > getWidth()) {
                continue;
            }

            int restoreDayAlpha = dayTextPaint.getAlpha();
            int restoreWeekAlpha = weekTextPaint.getAlpha();
            int restoreSelAlpha = selectedTextPaint.getAlpha();
            int restoreTodayAlpha = todayTextPaint.getAlpha();

            dayTextPaint.setAlpha(alpha);
            weekTextPaint.setAlpha(alpha);
            selectedTextPaint.setAlpha(alpha);
            todayTextPaint.setAlpha(alpha);

            drawDayCell(canvas, cell, left, cellTop, right, bottom);

            dayTextPaint.setAlpha(restoreDayAlpha);
            weekTextPaint.setAlpha(restoreWeekAlpha);
            selectedTextPaint.setAlpha(restoreSelAlpha);
            todayTextPaint.setAlpha(restoreTodayAlpha);
        }
    }

    private void drawDayCell(Canvas canvas, DayCell cell, float left, float top, float right, float bottom) {
        float cx = (left + right) / 2f;
        float cy = (top + bottom) / 2f;
        float radius = dp(18);

        boolean isToday = isSameDay(cell.date, today);
        boolean isSelected = selectedDate != null && isSameDay(cell.date, selectedDate);

        int day = cell.date.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = cell.date.get(Calendar.DAY_OF_WEEK);
        boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

        if (isToday) {
            drawBlueCircle(canvas, cx, cy, radius);
            drawCenteredText(canvas, String.valueOf(day), cx, cy, todayTextPaint);
        } else if (isSelected) {
            Paint selectedPaint = new Paint(selectedTextPaint);
            selectedPaint.setColor(colorBlue);
            selectedPaint.setFakeBoldText(true);
            drawCenteredText(canvas, String.valueOf(day), cx, cy, selectedPaint);
        } else {
            Paint paintToUse = new Paint(dayTextPaint);
            if (!cell.isCurrentMonth) {
                paintToUse.setColor(colorLight);
            } else if (isWeekend) {
                paintToUse.setColor(colorWeekend);
            } else {
                paintToUse.setColor(colorNormal);
            }
            drawCenteredText(canvas, String.valueOf(day), cx, cy, paintToUse);
        }
    }

    private void drawBlueCircle(Canvas canvas, float cx, float cy, float radius) {
        if (blueCircleDrawable != null) {
            int l = (int) (cx - radius);
            int t = (int) (cy - radius);
            int r = (int) (cx + radius);
            int b = (int) (cy + radius);
            blueCircleDrawable.setBounds(l, t, r, b);
            blueCircleDrawable.draw(canvas);
        }
    }

    private void drawCenteredText(Canvas canvas, String text, float cx, float cy, Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        float baseline = cy + (Math.abs(fm.ascent) - fm.descent) / 2f;
        canvas.drawText(text, cx, baseline, paint);
    }

    // ==================== 月份切换动画 ====================
    private void switchMonthWithAnim(final int direction) {
        if (isMonthAnimating) return;

        isMonthAnimating = true;
        monthAnimDirection = direction;
        monthAnimOffset = 0f;

        Calendar targetMonth = cloneCalendar(currentMonth);
        targetMonth.add(Calendar.MONTH, direction);
        buildMonthCellsFor(targetMonth, animMonthCells);

        float start = 0f;
        float end = direction > 0 ? -getWidth() : getWidth();

        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(MONTH_ANIM_DURATION);
        animator.setInterpolator(new DecelerateInterpolator());

        animator.addUpdateListener(animation -> {
            monthAnimOffset = (float) animation.getAnimatedValue();
            invalidate();
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentMonth.add(Calendar.MONTH, direction);
                buildMonthCells();

                monthAnimOffset = 0f;
                isMonthAnimating = false;
                animMonthCells.clear();

                invalidate();
            }
        });

        animator.start();
    }

    // ==================== 事件处理 ====================
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            performClick();
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void handleClick(float x, float y) {
        if (isMonthAnimating) return;

        for (DayCell cell : monthCells) {
            if (cell.rect.contains((int) x, (int) y)) {
                selectedDate = cloneCalendar(cell.date);

                if (!(cell.date.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
                        && cell.date.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR))) {
                    currentMonth.set(Calendar.YEAR, cell.date.get(Calendar.YEAR));
                    currentMonth.set(Calendar.MONTH, cell.date.get(Calendar.MONTH));
                }

                buildMonthCells();
                invalidate();

                if (onDateSelectedListener != null) {
                    onDateSelectedListener.onDateSelected(cloneCalendar(selectedDate));
                }
                break;
            }
        }
    }

    // ==================== 公共 API ====================
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.onDateSelectedListener = listener;
    }

    public Calendar getSelectedDate() {
        return selectedDate == null ? null : cloneCalendar(selectedDate);
    }
}