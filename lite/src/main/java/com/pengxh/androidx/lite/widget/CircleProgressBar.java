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
import com.pengxh.androidx.lite.utils.DeviceSizeUtil;
import com.pengxh.androidx.lite.utils.WeakReferenceHandler;

/**
 * 圆形进度条
 */
public class CircleProgressBar extends View {

    private final Context context;
    private final int backgroundColor;
    private final int foregroundColor;
    private final String text;
    private float centerX;
    private float centerY;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private TextPaint textPaint;
    private int radius;
    private String currentValue;//当前污染物测量值
    private float sweepAngle;//当前测量值转为弧度扫过的角度
    private final WeakReferenceHandler weakReferenceHandler;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyleAttr, 0);
        backgroundColor = a.getColor(R.styleable.CircleProgressBar_cpb_backgroundColor, Color.parseColor("#D3D3D3"));
        foregroundColor = a.getColor(R.styleable.CircleProgressBar_cpb_foregroundColor, Color.parseColor("#0000FF"));
        text = a.getString(R.styleable.CircleProgressBar_cpb_text);
        a.recycle();
        //初始化画笔
        initPaint();

        weakReferenceHandler = new WeakReferenceHandler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == 2022010101) {
                    sweepAngle = (float) msg.arg1 * 360 / 100;
                }
                return true;
            }
        });
    }

    private void initPaint() {
        //背景色画笔
        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(DeviceSizeUtil.dp2px(context, 12));
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);//圆头
        backgroundPaint.setAntiAlias(true);

        //前景色画笔
        foregroundPaint = new Paint();
        foregroundPaint.setColor(foregroundColor);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(DeviceSizeUtil.dp2px(context, 12));
        foregroundPaint.setStrokeCap(Paint.Cap.ROUND);//圆头
        foregroundPaint.setAntiAlias(true);

        //文字画笔
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.parseColor("#333333"));
        textPaint.setTextSize(DeviceSizeUtil.sp2px(context, 14));
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
        int viewWidth;
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            viewWidth = widthSpecSize;
        } else {
            // wrap_content
            viewWidth = DeviceSizeUtil.dp2px(context, 150);
        }
        // 获取高
        int viewHeight;
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            viewHeight = heightSpecSize;
        } else {
            // wrap_content
            viewHeight = DeviceSizeUtil.dp2px(context, 150);
        }
        //园半径等于View宽或者高的一半
        this.radius = (viewWidth - DeviceSizeUtil.dp2px(context, 20)) >> 1;
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 画布移到中心位置
         * */
        canvas.translate(centerX, centerY);
        //绘制进度条背景
        canvas.drawCircle(0, 0, radius, backgroundPaint);

        //绘制上面百分比
        Rect valueRect = new Rect(0, 0, 0, 0);
        int valueY = (int) (valueRect.centerY() + (textPaint.getFontMetrics().top) * 0.3);//基线中间点的y轴计算公式
        if (TextUtils.isEmpty(currentValue)) {
            canvas.drawText("未定义!", valueRect.centerX(), valueY, textPaint);
        } else {
            canvas.drawText(currentValue, valueRect.centerX(), valueY, textPaint);
        }

        //绘制下面Tip文字
        Rect tipsRect = new Rect(0, 0, 0, 0);
        //计算文字左下角坐标
        int tipsY = (int) (tipsRect.centerY() - (textPaint.getFontMetrics().top) * 1.2);//基线中间点的y轴计算公式
        if (TextUtils.isEmpty(text)) {
            canvas.drawText("未定义！", tipsRect.centerX(), tipsY, textPaint);
        } else {
            canvas.drawText(text, tipsRect.centerX(), tipsY, textPaint);
        }

        //绘制前景进度
        drawForegroundArc(canvas);
    }

    private void drawForegroundArc(Canvas canvas) {
        RectF rectF = new RectF(-radius, -radius, radius, radius);
        canvas.drawArc(rectF, -90, sweepAngle, false, foregroundPaint);
        invalidate();
    }

    public void setCurrentValue(int value) {
        if (value < 0) {
            this.currentValue = "0";
        } else if (value > 100) {
            this.currentValue = "100%";
        } else {
            this.currentValue = value + "%";
        }

        new Thread(() -> {
            for (int i = 0; i < value; i++) {
                Message message = weakReferenceHandler.obtainMessage();
                message.arg1 = i;
                message.what = 2022010101;
                weakReferenceHandler.handleMessage(message);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}