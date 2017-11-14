package com.dhq.mywidget.selectview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.OverScroller;

import com.dhq.mywidget.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 格尺滑动选择控件
 */
public class WheelView<T> extends View {

    private Paint mPaintLine;//中间刻度画笔
    private Paint mPaintText;//文字画笔

    private float mTextSize = 0;
    private float mPointY = 0f;
    private float mPointYoff = 0f;
    private int mPadding = dip2px(1);
    private OverScroller scroller;//控制滑动
    private VelocityTracker velocityTracker;

    private float mUnit = 50;
    private int mMaxValue = 200;
    private int mMinValue = 150;
    private int mDefaultIndex = (mMaxValue + mMinValue) / 2;//默认选中项下标
    private int lineColor = Color.rgb(228, 228, 228);//背景颜色
    private int textColor = Color.rgb(151, 151, 151);//文字的颜色
    private int textSelectColor = Color.rgb(151, 151, 151);//选中文字的颜色

    private int minvelocity;

    private int mDefaultHeight = 100;//控件的最小高度
    private int mDefaultWidth = 100;//控件的最小宽度

    private int mHeight = 100;//控件的高度
    private int mWidth = 300;//控件的宽度

    private T mSelectItem;


    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new OverScroller(context);
        minvelocity = ViewConfiguration.get(getContext())
                .getScaledMinimumFlingVelocity();
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.WheelView);
        if (typedArray != null) {
            mTextSize = typedArray.getDimension(R.styleable.WheelView_wheelTextSize, dip2px(30));
            lineColor = typedArray.getColor(R.styleable.WheelView_wheelLineColor, getResources().getColor(R.color.line_gray));
            textColor = typedArray.getColor(R.styleable.WheelView_wheelTextColor, getResources().getColor(R.color.text_gray));
            textSelectColor = typedArray.getColor(R.styleable.WheelView_wheelTextSelectColor, getResources().getColor(R.color.line_gray));
            typedArray.recycle();
        }
        mUnit = mTextSize + 50;
        initPaint();
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {

        mPaintLine = new Paint();
        mPaintLine.setAntiAlias(true);
        mPaintLine.setColor(lineColor);
        mPaintLine.setStrokeWidth(dip2px(1) * 3 / 2);
        mPaintLine.setStyle(Paint.Style.STROKE);

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(textColor);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setStyle(Paint.Style.FILL);

    }


    @Override
    protected void onDraw(final Canvas canvas) {

        super.onDraw(canvas);

        canvasCenterLine(canvas);
        canvasShowText(canvas);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);//获取宽的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);//获取高的模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);//获取宽的尺寸
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);//获取高的尺寸

        //根据宽的模式进行判断并复制
        if (widthMode == MeasureSpec.EXACTLY) {
            //EXACTLY如果match_parent或者具体的值，直接赋值
            mWidth = widthSize;
        } else {
            mWidth = getPaddingLeft() + mDefaultWidth + getPaddingRight();
        }
        //高度跟宽度的处理方式一样
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = getPaddingTop() + mDefaultHeight + getPaddingBottom();
        }
        //保存测量宽度和测量高度
        setMeasuredDimension(mWidth, mHeight);

    }


    /**
     * 绘制中间选中项上下两条线
     *
     * @param canvas
     */
    private void canvasCenterLine(Canvas canvas) {

        float centerY = mHeight / 2;

        canvas.drawLine(0, centerY - mUnit / 2, mWidth, centerY - mUnit / 2, mPaintLine);
        canvas.drawLine(0, centerY + mUnit / 2, mWidth, centerY + mUnit / 2, mPaintLine);

    }


    /**
     * 绘制滚轮显示文字
     *
     * @param canvas
     */
    private void canvasShowText(Canvas canvas) {

        for (int i = 1; i <= listValue.size(); i++) {

            //距离中间刻度的间隔数
            int space = mDefaultIndex - i;

            //计算刻度的纵坐标位置
            float itemCenterY = mHeight / 2 - space * mUnit + mPointY;

            //判断该点坐标是否在视图范围内
            if (isCanShow(itemCenterY)) {

                float centerX = mWidth / 2;
                int alpha = (int) (255 * ((mHeight / 2 - Math.abs(mHeight / 2 - itemCenterY)) / (mHeight / 2)));
                mPaintText.setAlpha(alpha);

                String text = "";
                if (mSelectListener != null) {
                    text = mSelectListener.setShowValue(listValue.get(i - 1));
                }

                float topY = itemCenterY - mUnit / 2;

                canvas.drawText(text,
                        centerX - getFontlength(mPaintText, text) / 2,
                        topY + getFontBaseLineHeight(mPaintText),
                        mPaintText);

            }
        }

    }


    /**
     * 判断该项是否应该显示到界面上
     *
     * @param itemCenterY item的中心线纵坐标
     * @return
     */
    private boolean isCanShow(float itemCenterY) {

        if (itemCenterY > mHeight / 2) {
            //当在中间以下是 看上边缘位置
            return itemCenterY - mUnit / 2 < mHeight - mPadding;
        } else if (itemCenterY > mHeight / 2) {
            //当在中间以上是 看下边缘位置
            return itemCenterY + mUnit / 2 > mPadding;
        }
        return true;
    }

    /**
     * 更新界面
     */
    public void update() {
        update(mDefaultIndex);
    }

    /**
     * 更新界面
     */
    public void update(int selectIndex) {
        mMaxValue = listValue.size();
        if (selectIndex < mMinValue) {
            mDefaultIndex = mMinValue;
        } else if (selectIndex > listValue.size()) {
            mDefaultIndex = listValue.size();
        } else {
            mDefaultIndex = selectIndex;
        }
        startAnim();
        postInvalidate();
    }

    private boolean isActionUp = false;
    private float mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        float yPosition = event.getY();

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mScrolleAnim != null) {
                    clearAnimation();
                }
                isActionUp = false;
                scroller.forceFinished(true);

                break;
            case MotionEvent.ACTION_MOVE:
                isActionUp = false;
                //计算滑动距离
                mPointYoff = yPosition - mLastY;
                mPointY = mPointY + mPointYoff;
                postInvalidate();

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isActionUp = true;
                countVelocityTracker(event);//控制快速滑动
                startAnim();
//                startAnim = true;
                return false;
            default:
                break;
        }

        mLastY = yPosition;
        return true;
    }


    @Override
    public void computeScroll() {

        if (scroller.computeScrollOffset()) {
            float mPointYoff = (scroller.getFinalY() - scroller.getCurrY());
            mPointY = mPointY + mPointYoff * functionSpeed();
            startAnim();

        }
        super.computeScroll();

    }

    /**
     * 控制滑动速度
     *
     * @return
     */
    private float functionSpeed() {
        return 0.5f;
    }

    private void countVelocityTracker(MotionEvent event) {
        velocityTracker.computeCurrentVelocity(800, 800);
        float yVelocity = velocityTracker.getYVelocity();
        if (Math.abs(yVelocity) > minvelocity) {
            scroller.fling(0, 0, 0, (int) yVelocity, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, 0, 0);
        }
    }

    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    /**
     * 计算文字的高度
     *
     * @param paint
     * @return
     */
    public float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    /**
     * 计算文字基础线的高度
     *
     * @param paint
     * @return
     */
    public float getFontBaseLineHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (mUnit - fm.descent - fm.ascent) / 2;
    }


    private ScrolleAnim mScrolleAnim;

    private class ScrolleAnim extends Animation {

        float fromY = 0f;
        float desY = 0f;

        public ScrolleAnim(float d, float f) {
            fromY = f;
            desY = d;
        }


        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);

            mPointY = fromY + (desY - fromY) * interpolatedTime;//计算动画每贞滑动的距离

            invalidate();


        }
    }


    private void startAnim() {
        float mMinPointY = (mDefaultIndex - mMinValue) * mUnit;
        float mMaxPointY = (mMaxValue - mDefaultIndex) * mUnit;

        if (mPointY > 0 && Math.abs(mPointY) > mMinPointY) {
            moveToY(mDefaultIndex - mMinValue, 300);
        } else if (mPointY < 0 && Math.abs(mPointY) > mMaxPointY) {
            moveToY(mDefaultIndex - mMaxValue, 300);
        } else {
            int space = (int) (Math.rint(mPointY / mUnit));//四舍五入计算出往上还是往下滑动
            moveToY(space, 200);
        }
    }


    private void moveToY(int distance, int time) {
        if (mScrolleAnim != null)
            clearAnimation();
        mScrolleAnim = new ScrolleAnim((distance * mUnit), mPointY);
        mScrolleAnim.setDuration(time);
        startAnimation(mScrolleAnim);
        if (mSelectListener != null) {
            int index = mDefaultIndex - distance - 1;
            if (index < 0 || index >= listValue.size()) {
                return;
            }
            if (mSelectItem != listValue.get(index)) {
                mSelectItem = listValue.get(index);
                mSelectListener.onSelectItem(listValue.get(index));
            }

        }

    }

    private List<T> listValue = new ArrayList<>();

    private SelectListener mSelectListener;


    /**
     * 设置滑动数据（默认选中第一项）
     *
     * @param list 要设置的滑动数据
     */
    public void setDatas(List<T> list) {
        setDatas(list, 1);
    }

    /**
     * 设置滑动数据（默认选中第一项）
     *
     * @param list  要设置的滑动数据
     * @param index 默认选中项坐标
     */
    public void setDatas(List<T> list, int index) {
        listValue = list;
        mMaxValue = listValue.size();
        mMinValue = 1;
        mDefaultIndex = index;

        invalidate();

    }


    public void setSelectListener(SelectListener selectListener) {
        this.mSelectListener = selectListener;
    }

    public interface SelectListener<T> {
        String setShowValue(T item);

        void onSelectItem(T item);
    }


}
