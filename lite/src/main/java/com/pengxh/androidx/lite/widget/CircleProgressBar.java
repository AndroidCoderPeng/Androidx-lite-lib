package com.pengxh.androidx.lite.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.kit.FloatKit;
import com.pengxh.androidx.lite.kit.IntKit;

/**
 * 圆形进度条
 */
public class CircleProgressBar extends View {
    private final int ringRadius;
    private final RectF rectF;

    //控件边长
    private final int viewSideLength;
    private final int ringStroke;
    private final Rect viewRect;
    private Paint guidePaint;

    private final int backgroundColor;
    private final int foregroundColor;
    private final int textColor;
    private final String text;
    private final float textSize;
    private final boolean hideText;

    private float centerX = 0f;
    private float centerY = 0f;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private TextPaint textPaint;

    //当前值
    private String currentValue = "";

    //当前测量值转为弧度扫过的角度
    private float sweepAngle = 0f;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        ringRadius = type.getDimensionPixelSize(R.styleable.CircleProgressBar_cpb_ring_radius, IntKit.dp2px(context, 100));
        rectF = new RectF(-ringRadius, -ringRadius, ringRadius, ringRadius);
        ringStroke = type.getDimensionPixelSize(R.styleable.CircleProgressBar_cpb_ring_stroke, IntKit.dp2px(context, 10));
        viewSideLength = ringRadius + IntKit.dp2px(context, 10);
        //辅助框
        viewRect = new Rect(-viewSideLength, -viewSideLength, viewSideLength, viewSideLength);

        backgroundColor = type.getColor(R.styleable.CircleProgressBar_cpb_background, Color.LTGRAY);
        foregroundColor = type.getColor(R.styleable.CircleProgressBar_cpb_foreground, Color.BLUE);
        text = type.getString(R.styleable.CircleProgressBar_cpb_text);
        textColor = type.getColor(R.styleable.CircleProgressBar_cpb_text_color, Color.DKGRAY);
        textSize = type.getDimension(R.styleable.CircleProgressBar_cpb_text_size, 18f);
        hideText = type.getBoolean(R.styleable.CircleProgressBar_cpb_hide_text, false);
        type.recycle();
        //初始化画笔
        initPaint();
    }

    private void initPaint() {
        guidePaint = new Paint();
        guidePaint.setColor(Color.LTGRAY);
        guidePaint.setStyle(Paint.Style.STROKE);
        guidePaint.setStrokeWidth(FloatKit.dp2px(getContext(), 1f));
        guidePaint.setAntiAlias(true);

        //背景色画笔
        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(ringStroke);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);  //圆头
        backgroundPaint.setAntiAlias(true);

        //前景色画笔
        foregroundPaint = new Paint();
        foregroundPaint.setColor(foregroundColor);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(ringStroke);
        foregroundPaint.setStrokeCap(Paint.Cap.ROUND);  //圆头
        foregroundPaint.setAntiAlias(true);

        //文字画笔
        textPaint = new TextPaint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
    }

    //计算出中心位置，便于定位
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w >> 1;
        centerY = h >> 1;
    }

    //计算控件实际大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        // 获取宽
        int mWidth;
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mWidth = widthSpecSize;
        } else {
            // wrap_content，外边界宽
            mWidth = (viewSideLength * 2);
        }
        // 获取高
        int mHeight;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 画布移到中心位置
         * */
        canvas.translate(centerX, centerY);
//        drawGuides(canvas);

        //绘制进度条背景
        canvas.drawCircle(0f, 0f, ringRadius, backgroundPaint);

        if (!hideText) {
            //绘制上面百分比
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float top = fontMetrics.top;
            float bottom = fontMetrics.bottom;
            if (TextUtils.isEmpty(currentValue)) {
                canvas.drawText("###", 0f, (top + bottom) / 2, textPaint);
            } else {
                canvas.drawText(currentValue, 0f, (top + bottom) / 2, textPaint);
            }

            //绘制下面Tip文字
            if (TextUtils.isEmpty(text)) {
                canvas.drawText("###", 0f, -(top + bottom) * 1.5f, textPaint);
            } else {
                canvas.drawText(text, 0f, -(top + bottom) * 1.5f, textPaint);
            }
        }

        //绘制前景进度
        canvas.drawArc(rectF, -90f, sweepAngle, false, foregroundPaint);
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
    }

    public void setCurrentValue(int value) {
        if (value < 0) {
            this.currentValue = "0";
        } else if (value > 100) {
            this.currentValue = "100%";
        } else {
            this.currentValue = value + "%";
        }

        final int[] i = {0};
        post(new Runnable() {
            @Override
            public void run() {
                i[0]++;
                sweepAngle = (float) i[0] * 360 / 100;
                invalidate();
                if (i[0] <= value) {
                    postDelayed(this, 10);
                } else {
                    removeCallbacks(this);
                }
            }
        });
    }
}