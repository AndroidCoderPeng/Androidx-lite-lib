package com.pengxh.androidx.lite.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.pengxh.androidx.lite.R;

@SuppressLint("ClickableViewAccessibility")
public class SteeringWheelView extends RelativeLayout {

    //画布中心x
    private int canvasCenterX = 0;

    //画布中心y
    private int canvasCenterY = 0;

    //控件直径
    private final float diameter;

    //Paint
    private final Paint backgroundPaint;
    private final Paint borderPaint;
    private final Paint directionPaint;

    // 各控件使用状态
    private boolean leftTurn = false;
    private boolean topTurn = false;
    private boolean rightTurn = false;
    private boolean bottomTurn = false;

    //外圆区域
    private RectF outerCircleRectF;

    public SteeringWheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.SteeringWheelView);
        diameter = type.getDimension(R.styleable.SteeringWheelView_ctrl_diameter, 200f);
        int borderColor = type.getColor(R.styleable.SteeringWheelView_ctrl_borderColor, Color.CYAN);
        int backgroundColor = type.getColor(R.styleable.SteeringWheelView_ctrl_backgroundColor, Color.WHITE);
        float borderStroke = type.getDimension(R.styleable.SteeringWheelView_ctrl_borderStroke, 5f);
        type.recycle();

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setDither(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderStroke);
        borderPaint.setColor(borderColor);

        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setDither(true);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);

        directionPaint = new Paint();
        directionPaint.setAntiAlias(true);
        directionPaint.setDither(true);
        directionPaint.setStyle(Paint.Style.FILL);
        directionPaint.setColor(Color.parseColor("#EEEEEE"));

        LayoutParams layoutParams = new LayoutParams((int) diameter, (int) diameter);

        View view = LayoutInflater.from(context).inflate(R.layout.widget_view_steering_wheel, this);

        RelativeLayout rootView = view.findViewById(R.id.rootView);
        rootView.setLayoutParams(layoutParams);

        ImageButton leftButton = view.findViewById(R.id.leftButton);
        leftButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    leftTurn = true;
                    listener.onLeftTurn();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    leftTurn = false;
                    listener.onActionTurnUp(Direction.LEFT);
                }
                postInvalidate();
                return true;
            }
        });

        ImageButton topButton = view.findViewById(R.id.topButton);
        topButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    topTurn = true;
                    listener.onTopTurn();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    topTurn = false;
                    listener.onActionTurnUp(Direction.TOP);
                }
                postInvalidate();
                return true;
            }
        });

        ImageButton rightButton = view.findViewById(R.id.rightButton);
        rightButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    rightTurn = true;
                    listener.onRightTurn();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    rightTurn = false;
                    listener.onActionTurnUp(Direction.RIGHT);
                }
                postInvalidate();
                return true;
            }
        });

        ImageButton bottomButton = view.findViewById(R.id.bottomButton);
        bottomButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    bottomTurn = true;
                    listener.onBottomTurn();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    bottomTurn = false;
                    listener.onActionTurnUp(Direction.BOTTOM);
                }
                postInvalidate();
                return true;
            }
        });

        ImageButton centerButton = view.findViewById(R.id.centerButton);
        centerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCenterClicked();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasCenterX = (w >> 1);
        canvasCenterY = (h >> 1);

        float outerCircleRadius = (diameter - 20) / 2; //半径

        // 大外圈区域
        outerCircleRectF = new RectF(
                (canvasCenterX - outerCircleRadius),
                (canvasCenterY - outerCircleRadius),
                (canvasCenterX + outerCircleRadius),
                (canvasCenterY + outerCircleRadius)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //半径
        float outerCircleRadius = diameter / 2;

        //背景
        canvas.drawCircle(canvasCenterX, canvasCenterY, outerCircleRadius, backgroundPaint);

        //外圆圆圈
        canvas.drawCircle(canvasCenterX, canvasCenterY, outerCircleRadius, borderPaint);

        if (leftTurn) {
            canvas.drawArc(outerCircleRectF, (90 * 2 - 45), 90f, true, directionPaint);
        }

        if (topTurn) {
            canvas.drawArc(outerCircleRectF, (90 * 3 - 45), 90f, true, directionPaint);
        }

        if (rightTurn) {
            canvas.drawArc(outerCircleRectF, -45f, 90f, true, directionPaint);
        }

        if (bottomTurn) {
            canvas.drawArc(outerCircleRectF, 45f, 90f, true, directionPaint);
        }
    }

    private OnWheelTouchListener listener;

    interface OnWheelTouchListener {
        /**
         * 左
         */
        void onLeftTurn();

        /**
         * 上
         */
        void onTopTurn();

        /**
         * 右
         */
        void onRightTurn();

        /**
         * 下
         */
        void onBottomTurn();

        /**
         * 中间
         */
        void onCenterClicked();

        /**
         * 松开
         */
        void onActionTurnUp(Direction dir);
    }

    public void setOnWheelTouchListener(OnWheelTouchListener listener) {
        this.listener = listener;
    }

    enum Direction {
        LEFT, TOP, RIGHT, BOTTOM
    }
}
