package com.dhq.mywidget.wave;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.dhq.mywidget.R;

import static android.animation.ValueAnimator.INFINITE;

/**
 * DESC 圆形波浪线进度条
 * Created by douhaoqiang on 2017/11/14.
 */

public class WaveView extends View {


    /**
     * 默认波长
     */
    private static final int DEFAULT_RADIUS = 100;

    /**
     * 默认波峰和波谷的高度
     */
    private static final int DEFAULT_WAVE_HEIGHT = 20;

    /**
     * 默认的最大的进度
     */
    private static final int DEFAULT_MAX_PROGRESS = 100;

    /**
     * 默认边框宽度
     */
    private static final int DEFAULT_BORDER_WIDTH = 2;

    /**
     * 默认的进度字体大小
     */
    private static final int DEFAULT_TEXT_SIZE = 16;

    //进度
    private int mProgress = 50;
    //半径
    private int mRadius = DEFAULT_RADIUS;

    //进度条尺寸
    private int mProgressSize = 100;
    //文字的大小
    private int mTextSize;
    //波高
    private int mWaveHeight;
    //文字颜色
    private int mTextColor;
    //波浪的颜色
    private int mWaveColor;
    //圆形边框的颜色
    private int mBorderColor;
    //圆形边框的宽度
    private int borderWidth;
    //是否隐藏进度文字
    private boolean isHideProgressText = false;

    //进度条的波浪贝塞尔曲线
    private Path mWavePath;
    //用于裁剪的Path
    private Path mCirclePath;

    // 画圆的画笔
    private Paint mCirclePaint;
    // 画文字的笔
    private Paint mTextPaint;
    // 画波浪的笔
    private Paint mWavePaint;
    // 文字的区域
    private Rect mTextRect;

    private ValueAnimator mAnimator;
    private int mMoveX = 0;
    private boolean isStartAnimation = false;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(attrs);
        initPaint();
    }

    /**
     * 获取自定义属性
     *
     * @param attrs
     */
    private void getAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.WaveProgressView);
        mTextColor = ta.getColor(R.styleable.WaveProgressView_wavetextColor, Color.BLACK);
        mWaveColor = ta.getColor(R.styleable.WaveProgressView_waveColor, Color.RED);
        mBorderColor = ta.getColor(R.styleable.WaveProgressView_waveborderColor, Color.RED);
        borderWidth = ta.getDimensionPixelOffset(R.styleable.WaveProgressView_waveborderWidth, dp2px(DEFAULT_BORDER_WIDTH));
        mTextSize = ta.getDimensionPixelSize(R.styleable.WaveProgressView_wavetextSize, sp2px(DEFAULT_TEXT_SIZE));
        mWaveHeight = ta.getDimensionPixelSize(R.styleable.WaveProgressView_waveHeight, dp2px(DEFAULT_WAVE_HEIGHT));
        mProgress = ta.getInteger(R.styleable.WaveProgressView_waveprogress, 0);
        isHideProgressText = ta.getBoolean(R.styleable.WaveProgressView_wavehideText, false);
        ta.recycle();
    }

    //初始化画笔工具
    private void initPaint() {

        mWavePath = new Path();
        mCirclePath = new Path();

        mWavePaint = new Paint();
        mWavePaint.setColor(mWaveColor);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.STROKE);// 空心画笔
        mCirclePaint.setColor(mBorderColor);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(borderWidth);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mTextRect = new Rect();

        mProgress = 50;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            //不是精确尺寸
            widthSize = mProgressSize;
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            //不是精确尺寸
            heightSize = mProgressSize;
        }

        //选择尺寸小的一边长度作为波浪进度的大小
        if (widthSize > heightSize) {
            mProgressSize = heightSize;
        } else {
            mProgressSize = widthSize;
        }

        mRadius = mProgressSize / 2;

        setMeasuredDimension(mProgressSize, mProgressSize);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCirclePath.reset();
        mWavePath.reset();

        mCirclePath.addCircle(mRadius, mRadius, mRadius, Path.Direction.CCW);
        mWavePath.moveTo(0 + mMoveX, getWaveBaseLine());

        //绘制两个波长的波纹
        for (int i = 0; i < 2; i++) {
            mWavePath.rQuadTo(mProgressSize / 4, -mWaveHeight, mProgressSize / 2, 0);
            mWavePath.rQuadTo(mProgressSize / 4, mWaveHeight, mProgressSize / 2, 0);
        }
        mWavePath.lineTo(mProgressSize, mProgressSize);
        mWavePath.lineTo(0, mProgressSize);
        mWavePath.close();

        //裁剪一个圆形区域
        canvas.clipPath(mCirclePath);

        canvas.drawPath(mWavePath, mWavePaint);

        canvas.drawPath(mCirclePath, mCirclePaint);


        //绘制进度文字（画文字可不是直接drawText这么简单，要找基线去画）
        String progress = mProgress + "%";
        if (isHideProgressText) {
            progress = "";
        }
        mTextPaint.getTextBounds(progress, 0, progress.length(), mTextRect);
        canvas.drawText(progress, mRadius - mTextRect.width() / 2,
                mRadius + mTextRect.height() / 2, mTextPaint);

        //开启属性动画使波浪浪起来(这里只需要启动一次)
//        if (!isStartAnimation) {
//            isStartAnimation = true;
//            startAnimation();
//        }

    }


    /**
     * 开始动画
     */
    private void startAnimation() {
        mAnimator = ValueAnimator.ofInt(0, mProgressSize);
        mAnimator.setDuration(2000);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMoveX=(int)animation.getAnimatedValue();
                invalidate();
            }
        });
        isStartAnimation=true;
        mAnimator.start();
    }


    /**
     * 获取波浪线基准线纵坐标
     *
     * @return
     */
    private float getWaveBaseLine() {
        return mProgressSize * (1 - mProgress * 1f / DEFAULT_MAX_PROGRESS);
    }


    //dp to px
    protected int dp2px(int dpval) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpval, getResources().getDisplayMetrics());
    }

    //sp to px
    protected int sp2px(int dpval) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dpval, getResources().getDisplayMetrics());
    }

}
