package com.pengxh.androidx.lite.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.hub.FloatHub;
import com.pengxh.androidx.lite.hub.IntHub;
import com.pengxh.androidx.lite.utils.WeakReferenceHandler;

/**
 * 圆形进度条
 */
public class CircleProgressBar extends View implements Handler.Callback {

    private final Context context;
    private int ringRadius;
    private RectF rectF;

    //控件边长
    private int viewSideLength;
    private int ringStroke;
    private Rect viewRect;
    private Paint guidePaint;

    private int backgroundColor;
    private int foregroundColor;
    private String text = "";
    private float centerX = 0f;
    private float centerY = 0f;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private TextPaint textPaint;

    //当前值
    private String currentValue = "";

    //当前测量值转为弧度扫过的角度
    private float sweepAngle = 0f;
    private WeakReferenceHandler weakReferenceHandler;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        weakReferenceHandler = new WeakReferenceHandler(this);

        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        ringRadius = type.getDimensionPixelOffset(R.styleable.CircleProgressBar_cpb_ring_radius, IntHub.dp2px(context, 100));
        rectF = new RectF(-ringRadius, -ringRadius, ringRadius, ringRadius);
        ringStroke = type.getDimensionPixelOffset(R.styleable.CircleProgressBar_cpb_ring_stroke, IntHub.dp2px(context, 10));
        //需要给外围刻度留位置
        viewSideLength = ringRadius + IntHub.dp2px(context, 30);
        //辅助框
        viewRect = new Rect(-viewSideLength, -viewSideLength, viewSideLength, viewSideLength);

        backgroundColor = type.getColor(R.styleable.CircleProgressBar_cpb_backgroundColor, Color.LTGRAY);
        foregroundColor = type.getColor(R.styleable.CircleProgressBar_cpb_foregroundColor, Color.BLUE);
        text = type.getString(R.styleable.CircleProgressBar_cpb_text);

        type.recycle();
        //初始化画笔
        initPaint();
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return true;
    }

    private void initPaint() {
        guidePaint = new Paint();
        guidePaint.setColor(Color.LTGRAY);
        guidePaint.setStyle(Paint.Style.STROKE);
        guidePaint.setStrokeWidth(FloatHub.dp2px(context, 1f));
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
        textPaint.setColor(Color.LTGRAY);
        textPaint.setTextSize(FloatHub.dp2px(context, 14f));
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

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 画布移到中心位置
         * */
        canvas.translate(centerX, centerY);
        drawGuides(canvas);

        //绘制进度条背景
        canvas.drawCircle(0f, 0f, ringRadius, backgroundPaint);

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

        int i = 0;
        while (i <= value) {
            weakReferenceHandler.post(updateProgressRunnable.setProgress(i));
            i++;
        }
//        new Thread(() -> {
//            for (int i = 0; i < value; i++) {
//                weakReferenceHandler.post(updateProgressRunnable.setProgress(i));
//            }
//        }).start();
    }

    private interface UpdateProgressRunnable extends Runnable {
        UpdateProgressRunnable setProgress(int progress);
    }

    private final UpdateProgressRunnable updateProgressRunnable = new UpdateProgressRunnable() {

        private int progress = 0;

        @Override
        public UpdateProgressRunnable setProgress(int progress) {
            this.progress = progress;
            return this;
        }

        @Override
        public void run() {
            sweepAngle = (float) progress * 360 / 100;
            invalidate();
        }
    };
}