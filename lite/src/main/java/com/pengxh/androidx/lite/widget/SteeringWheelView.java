package com.pengxh.androidx.lite.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.kit.ContextKit;

@SuppressLint("ClickableViewAccessibility")
public class SteeringWheelView extends RelativeLayout {

    public SteeringWheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.SteeringWheelView);
        float diameter = type.getDimension(R.styleable.SteeringWheelView_ctrl_diameter, 250f);
        type.recycle();
        if (diameter <= 150f) {
            diameter = 150f;
        }

        if (diameter >= ContextKit.getScreenWidth(context)) {
            diameter = ContextKit.getScreenWidth(context);
        }

        LayoutParams layoutParams = new LayoutParams((int) diameter, (int) diameter);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_view_steering_wheel, this);
        RelativeLayout rootView = view.findViewById(R.id.rootView);
        rootView.setLayoutParams(layoutParams);

        ImageButton leftButton = view.findViewById(R.id.leftButton);
        leftButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    listener.onLeftTurn();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    listener.onActionTurnUp(Direction.LEFT);
                }
                postInvalidate();
                return false;
            }
        });

        ImageButton topButton = view.findViewById(R.id.topButton);
        topButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    listener.onTopTurn();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    listener.onActionTurnUp(Direction.TOP);
                }
                postInvalidate();
                return false;
            }
        });

        ImageButton rightButton = view.findViewById(R.id.rightButton);
        rightButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    listener.onRightTurn();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    listener.onActionTurnUp(Direction.RIGHT);
                }
                postInvalidate();
                return false;
            }
        });

        ImageButton bottomButton = view.findViewById(R.id.bottomButton);
        bottomButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    listener.onBottomTurn();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    listener.onActionTurnUp(Direction.BOTTOM);
                }
                postInvalidate();
                return false;
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
