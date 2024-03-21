package com.pengxh.androidx.lite.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.hub.FloatHub;
import com.pengxh.androidx.lite.hub.IntHub;


/**
 * 空气污染指数表盘，仿HUAWEI天气
 */
public class AirDashBoardView extends View {
    //View中心X坐标
    private float centerX = 0f;

    //View中心Y坐标
    private float centerY = 0f;

    //控件边长
    private final int viewSideLength;
    private final Rect viewRect;
    private Paint guidePaint;

    //表盘圆弧色
    private final int background;
    private final int foreground;
    private final int ringStroke;
    private final RectF ringRectF;
    private Paint ringPaint;

    //阈值
    private TextPaint thresholdPaint;

    private final int centerTextColor;
    private final int centerTextSize;

    //当前污染物测量值
    private int currentValue = 0;

    //污染物最大值
    private final int maxValue = 500;

    //当前测量值转为弧度扫过的角度
    private float sweepAngle = 0f;

    //表盘中心文字
    private String centerText = "###";
    private TextPaint centerPaint;
    private Paint forePaint;

    public AirDashBoardView(Context context) {
        this(context, null, 0);
    }

    public AirDashBoardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AirDashBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.AirDashBoardView);
        /**
         * getDimension()返回的是float
         * getDimensionPixelSize()返回的是实际数值的四舍五入
         * getDimensionPixelOffset返回的是实际数值去掉后面的小数点
         */
        int ringRadius = type.getDimensionPixelSize(R.styleable.AirDashBoardView_air_ring_radius, IntHub.dp2px(context, 100));
        viewSideLength = ringRadius + IntHub.dp2px(context, 15);
        //辅助框
        viewRect = new Rect(-viewSideLength, -viewSideLength, viewSideLength, viewSideLength);
        ringRectF = new RectF(-ringRadius, -ringRadius, ringRadius, ringRadius);
        background = type.getColor(R.styleable.AirDashBoardView_air_ring_background, Color.LTGRAY);
        foreground = type.getColor(R.styleable.AirDashBoardView_air_ring_foreground, Color.BLUE);
        ringStroke = type.getDimensionPixelSize(R.styleable.AirDashBoardView_air_ring_stroke, IntHub.dp2px(context, 5));
        centerTextSize = type.getDimensionPixelSize(R.styleable.AirDashBoardView_air_center_text_size, IntHub.sp2px(context, 20));
        centerTextColor = type.getColor(R.styleable.AirDashBoardView_air_center_text_color, Color.BLUE);
        type.recycle();

        //初始化画笔
        initPaint();
    }

    private void initPaint() {
        guidePaint = new Paint();
        guidePaint.setColor(Color.LTGRAY);
        guidePaint.setStyle(Paint.Style.STROKE);
        guidePaint.setStrokeWidth(FloatHub.sp2px(getContext(), 1f));
        guidePaint.setAntiAlias(true);

        ringPaint = new Paint();
        ringPaint.setColor(background);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(ringStroke);
        ringPaint.setAntiAlias(true);

        thresholdPaint = new TextPaint();
        thresholdPaint.setColor(Color.DKGRAY);
        thresholdPaint.setAntiAlias(true);
        thresholdPaint.setTextAlign(Paint.Align.CENTER);
        thresholdPaint.setTextSize(FloatHub.dp2px(getContext(), 16f));

        centerPaint = new TextPaint();
        centerPaint.setColor(centerTextColor);
        centerPaint.setAntiAlias(true);
        centerPaint.setTextAlign(Paint.Align.CENTER);
        centerPaint.setTextSize(centerTextSize);

        forePaint = new Paint();
        forePaint.setColor(foreground);
        forePaint.setStrokeCap(Paint.Cap.ROUND);
        forePaint.setStyle(Paint.Style.STROKE);
        forePaint.setStrokeWidth(ringStroke);
        forePaint.setAntiAlias(true);
        //设置背景光晕
        forePaint.setMaskFilter(new BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //圆心位置
        centerX = w >> 1;
        centerY = h >> 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth, mHeight;
        // 获取宽
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mWidth = widthSpecSize;
        } else {
            // wrap_content，外边界宽
            mWidth = (viewSideLength * 2);
        }
        // 获取高
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mHeight = heightSpecSize;
        } else {
            // wrap_content，外边界高
            mHeight = (viewSideLength * 2);
        }
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 画布移到中心位置，方便绘制一系列图形
         */
        canvas.translate(centerX, centerY);
//        drawGuides(canvas)

        /**
         * 从左往右画，顺时针，左边是180度
         */
        canvas.drawArc(ringRectF, 135f, 270f, false, ringPaint);

        /**
         * 绘制左边最小值
         */
        drawMinValue(canvas);
        /**
         * 绘制右边最大值
         */
        drawMaxValue(canvas);
        /**
         * 绘制中间实际值
         */
        drawCurrentValue(canvas);
        /**
         * 绘制中间文字
         */
        drawCenterText(canvas);
        /**
         *
         * 绘制前景进度
         */
        drawForegroundArc(canvas);
    }

    /**
     * 辅助线
     */
    private void drawGuides(Canvas canvas) {
        //最外层方框，即自定义View的边界
        canvas.drawRect(viewRect, guidePaint);

        //中心横线
        canvas.drawLine(-viewSideLength, 0f, viewSideLength, 0f, guidePaint);

        //中心竖线
        canvas.drawLine(0f, -viewSideLength, 0f, viewSideLength, guidePaint);

        //对角线
        canvas.drawLine(-viewSideLength, -viewSideLength, viewSideLength, viewSideLength, guidePaint);

        //对角线
        canvas.drawLine(viewSideLength, -viewSideLength, -viewSideLength, viewSideLength, guidePaint);

        //最小值基准线
        canvas.drawLine(-viewSideLength / 2f, -viewSideLength, -viewSideLength / 2f, viewSideLength, guidePaint);

        //最大值基准线
        canvas.drawLine(viewSideLength / 2f, -viewSideLength, viewSideLength / 2f, viewSideLength, guidePaint);
    }

    private void drawForegroundArc(Canvas canvas) {
        canvas.drawArc(ringRectF, 135f, sweepAngle, false, forePaint);
        invalidate();
    }

    private void drawMinValue(Canvas canvas) {
        //污染物最小值
        canvas.drawText("0", -viewSideLength / 2f, viewSideLength / 2f + viewSideLength / 3f, thresholdPaint);
    }

    private void drawMaxValue(Canvas canvas) {
        canvas.drawText(String.valueOf(maxValue), viewSideLength / 2f, viewSideLength / 2f + viewSideLength / 3f, thresholdPaint);
    }

    private void drawCurrentValue(Canvas canvas) {
        canvas.drawText(String.valueOf(currentValue), 0f, viewSideLength / 2f - viewSideLength / 3f, centerPaint);
    }

    private void drawCenterText(Canvas canvas) {
        canvas.drawText(centerText, 0f, -viewSideLength / 12f, centerPaint);
    }

    public AirDashBoardView setCenterText(String centerText) {
        this.centerText = centerText;
        invalidate();
        return this;
    }

    public void setCurrentValue(int value) {
        if (value < 0) {
            currentValue = 0;
        } else if (value > maxValue) {
            currentValue = maxValue;
        } else {
            currentValue = value;
        }

        int[] i = {0};
        post(new Runnable() {
            @Override
            public void run() {
                i[0]++;
                sweepAngle = (float) i[0] * 270 / maxValue;
                invalidate();
                if (i[0] <= value) {
                    postDelayed(this, 5);
                } else {
                    removeCallbacks(this);
                }
            }
        });
    }
}
