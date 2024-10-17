package com.pengxh.androidx.lite.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.pengxh.androidx.lite.R;
import com.pengxh.androidx.lite.kit.StringKit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlideBarView extends View implements View.OnTouchListener {

    private final List<String> letterArray = Arrays.asList(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    );
    private List<String> dataSet = new ArrayList<>();

    private final float viewWidth;
    private final float roundRadius;
    private final float textSize;
    private final int textColor;

    private float centerX;
    private float centerY;

    private int touchIndex;
    private int letterHeight;
    private boolean showBackground = false;

    private RectF viewBgRectF;
    private Paint backgroundPaint;
    private TextPaint textPaint;
    private Rect textRect;

    private RecyclerView recyclerView;
    private final PopupWindow popupWindow;
    private final TextView centerTextView;

    public SlideBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.SlideBarView);
        viewWidth = attr.getDimension(R.styleable.SlideBarView_slide_width, 30f);
        roundRadius = viewWidth / 2f;
        textSize = attr.getDimension(R.styleable.SlideBarView_slide_textSize, 18f);
        textColor = attr.getColor(R.styleable.SlideBarView_slide_textColor, Color.LTGRAY);
        attr.recycle();

        //初始化画笔
        initPaint();
        //触摸事件
        setOnTouchListener(this);

        //初始化Popup
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View contentView = layoutInflater.inflate(R.layout.popup_slide_bar, null);
        popupWindow = new PopupWindow(
                contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false
        );
        popupWindow.setContentView(contentView);
        centerTextView = contentView.findViewById(R.id.letterView);
    }

    private final Runnable showCenterTextRunnable = new Runnable() {
        @Override
        public void run() {
            popupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
        }
    };

    private final Runnable dismissCenterTextRunnable = new Runnable() {
        @Override
        public void run() {
            popupWindow.dismiss();
        }
    };

    private void initPaint() {
        Paint tickPaint = new Paint();
        tickPaint.setColor(Color.DKGRAY);
        tickPaint.setStyle(Paint.Style.STROKE);
        tickPaint.setStrokeWidth(1f);
        tickPaint.setAntiAlias(true);

        Paint anchorPaint = new Paint();
        anchorPaint.setColor(Color.RED);
        anchorPaint.setAntiAlias(true);

        //背景色画笔
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setAntiAlias(true);

        //文字画笔
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textRect = new Rect();
    }

    public void attachToRecyclerView(RecyclerView recyclerView, List<String> dataSet) {
        this.recyclerView = recyclerView;
        this.dataSet = dataSet;
    }

    public int getLetterPosition(String letter) {
        int index = -1;
        for (int i = 0; i < dataSet.size(); i++) {
            String firstLetter = StringKit.getHanYuPinyin(dataSet.get(i)).substring(0, 1);
            if (letter.equals(firstLetter)) {
                index = i;
                //当有相同的首字母之后就跳出循环
                break;
            }
        }
        return index;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w >> 2;
        centerY = h >> 2;

        // 设置背景圆角矩形外边框范围
        viewBgRectF = new RectF(-centerX, -centerY, centerX, centerY);

        // 每个字母的高度
        letterHeight = h / letterArray.size();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        // 获取宽
        int mWidth;
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mWidth = widthSpecSize;
        } else {
            // wrap_content，外边界宽
            mWidth = (int) viewWidth;
        }
        setMeasuredDimension(mWidth, heightSpecSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 画布移到中心位置，方便绘制一系列图形
         */
        canvas.translate(centerX, centerY);
        if (showBackground) {
            canvas.drawRoundRect(viewBgRectF, roundRadius, roundRadius, backgroundPaint);
        }

        for (int i = 0; i < letterArray.size(); i++) {
            //字母变色
            if (touchIndex == i) {
                //让当前字母变色
                textPaint.setColor(Color.parseColor("#00CB87"));
                textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                //其他字母不变色
                textPaint.setColor(textColor);
                textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }

            //绘制文字
            String letter = letterArray.get(i);

            //每个文字左下角坐标
            double textY = -centerY + (2 * i + 1) * 0.5 * letterHeight;

            //计算文字高度
            textPaint.getTextBounds(letter, 0, letter.length(), textRect);
            int textHeight = textRect.height();
            canvas.drawText(letter, 0f, (float) (textY + textHeight / 2), textPaint);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            float y = Math.abs(event.getY()); //取绝对值，不然y可能会取到负值
            int index = (int) (y / letterHeight); //字母的索引
            if (index != touchIndex) {
                touchIndex = Math.min(index, letterArray.size() - 1);
                //点击设置中间字母
                String letter = letterArray.get(touchIndex);
                centerTextView.setText(letter);
                onLetterIndexChangeListener.onLetterIndexChange(letter);
                //显示Popup
                post(showCenterTextRunnable);
                invalidate();
            }
            showBackground = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            showBackground = false;
            touchIndex = -1;
            //取消popup显示
            postDelayed(dismissCenterTextRunnable, 500);
            invalidate();
        }
        return true;
    }

    private OnLetterIndexChangeListener onLetterIndexChangeListener;

    public void setOnLetterIndexChangeListener(OnLetterIndexChangeListener listener) {
        onLetterIndexChangeListener = listener;
    }

    interface OnLetterIndexChangeListener {
        void onLetterIndexChange(String letter);
    }
}
