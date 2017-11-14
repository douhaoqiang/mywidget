package com.dhq.mywidget.ratingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.dhq.mywidget.R;

/**
 * RatingBar 评分条
 */
public class MyRatingBar extends View {

    private int numberOfStars;      //星星的数量
    private int spaceStar;          //星星间距
    private float stepSize;         //每步的大小
    private float rating;           //现在的进度
    private boolean isIndicator;    //是否仅是指示

    private int starNormalImgId;    //星星的默认图片
    private int starSelectImgId;    //星星的选择图片

    private Paint paintStar;
    private OnRatingBarChangeListener listener;


    private Bitmap star_default;//底部默认五角星
    private Bitmap star_selected;//选中状态五角星

    private int starRealHeight;//星星真实高度
    private int starRealWidth;//星星真实宽度

    private int starShowHeight;//星星显示高度
    private int starShowWidth;//星星显示宽度

    private int totleWith;


    public MyRatingBar(Context context) {
        super(context);
        initView();
    }

    public MyRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(attrs);
        initView();
    }

    public MyRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(attrs);
        initView();
    }

    /**
     * 初始化资源
     */
    private void initView() {

        paintStar = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paintStar.setStyle(Paint.Style.FILL_AND_STROKE);
        paintStar.setAntiAlias(true);
        paintStar.setColor(Color.BLACK);

        star_default = BitmapFactory.decodeResource(getResources(), starNormalImgId == -1 ? R.mipmap.star_default : starNormalImgId);
        star_selected = BitmapFactory.decodeResource(getResources(), starSelectImgId == -1 ? R.mipmap.star_selected : starSelectImgId);

        starRealHeight = star_default.getHeight();

        starRealWidth = star_default.getWidth();

        if (starShowHeight == 0) {
            starShowWidth = starRealWidth;
            starShowHeight = starRealHeight;
        } else {
            //根据显示宽度，计算星星显示高度
            starShowWidth = starRealWidth * starShowHeight / starRealHeight;
        }

        //计算占据的宽度大小
        totleWith = starShowWidth * numberOfStars + spaceStar * (numberOfStars - 1);
    }

    /**
     * Parses attributes defined in XML.
     */
    private void parseAttrs(AttributeSet attrs) {
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.MyRatingBar);

        //星星的数量
        numberOfStars = arr.getInteger(R.styleable.MyRatingBar_starsNumber, 5);
        //星星的尺寸
        starShowHeight = arr.getDimensionPixelSize(R.styleable.MyRatingBar_starSize, 0);
        //星星间隔
        spaceStar = arr.getDimensionPixelSize(R.styleable.MyRatingBar_starsSeparation, 20);
        //每步的大小
        stepSize = arr.getFloat(R.styleable.MyRatingBar_starStepSize, Float.MAX_VALUE);
        //现在的进度
        rating = normalizeRating(arr.getFloat(R.styleable.MyRatingBar_starRating, 0f));
        //是否仅是指示
        isIndicator = arr.getBoolean(R.styleable.MyRatingBar_starIsIndicator, false);

        starNormalImgId = arr.getResourceId(R.styleable.MyRatingBar_starNormalImg, -1);

        starSelectImgId = arr.getResourceId(R.styleable.MyRatingBar_starSelectImg, -1);

        arr.recycle();

        validateAttrs();
    }

    /**
     * 参数验证
     */
    private void validateAttrs() {
        if (numberOfStars <= 0) {
            throw new IllegalArgumentException(String.format("MyRatingBar initialized with invalid value for numberOfStars. Found %d, but should be greater than 0", numberOfStars));
        }
        if (stepSize <= 0) {
            throw new IllegalArgumentException(String.format("MyRatingBar initialized with invalid value for stepSize. Found %f, but should be greater than 0", stepSize));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else {
            width = totleWith;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else {
            height = starShowHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @Override
    public void onDraw(Canvas canvas) {

        Rect srcRect = new Rect(0, 0, starRealWidth, starRealHeight);// 截取bmp1中的矩形区域

        //绘制五个底部默认五角星
        for (int i = 0; i < numberOfStars; i++) {

            int left = spaceStar * i + starShowWidth * i;
            int top = 0;
            int right = left + starShowWidth;
            int bottom = starShowHeight;
            Rect dstRect = new Rect(left, top, right, bottom);
            canvas.drawBitmap(star_default, srcRect, dstRect, paintStar);

        }

        //绘制选择的五角星
        for (int i = 1; i <= numberOfStars; i++) {

            int selectLeft = (spaceStar + starShowWidth) * (i - 1);
            int selectTop = 0;
            int selectBottom = starShowHeight;
            int selectRight;
            if (rating / i >= 1) {
                //绘制整个星星
                selectRight = selectLeft + starShowWidth;
                Rect selectDstRect = new Rect(selectLeft, selectTop, selectRight, selectBottom);// bmp1在目标画布中的位置
                canvas.drawBitmap(star_selected, srcRect, selectDstRect, paintStar);
            } else {
                //计算需要绘制的部分星星的比例
                float pressgra = rating - i + 1;
                if (pressgra > 0) {
                    float starSelectSize = starRealWidth * pressgra;//计算星星选择部分的大小
                    float starShowSize = starShowWidth * pressgra;//计算星星选择部分的大小
                    selectRight = (int) (selectLeft + starShowSize);
                    Rect srcRect2 = new Rect(0, 0, (int) starSelectSize, starRealHeight);// 截取bmp1中的矩形区域
                    Rect selectDstRect = new Rect(selectLeft, selectTop, selectRight, selectBottom);// bmp1在目标画布中的位置
                    canvas.drawBitmap(star_selected, srcRect2, selectDstRect, paintStar);
                }
                break;
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isIndicator) {
            return false;
        }

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setNewRatingFromTouch(event.getX(), event.getY());
            case MotionEvent.ACTION_UP:
                setNewRatingFromTouch(event.getX(), event.getY());
            case MotionEvent.ACTION_CANCEL:
                if (listener != null) {
                    listener.onRatingChanged(this, rating);
                }
                break;
        }

        invalidate();
        return true;
    }

    /**
     * 根据触摸位置计算进度
     *
     * @param x
     * @param y
     */
    private void setNewRatingFromTouch(float x, float y) {


        if (x > totleWith) {
            rating = numberOfStars;
            return;
        }

        rating = (float) numberOfStars / totleWith * x;

        if (stepSize != Float.MAX_VALUE) {
            float mod = rating % stepSize;
            if (mod < stepSize / 2) {
                rating = rating - mod;
                rating = Math.max(0, rating);
            } else {
                rating = rating - mod + stepSize;
                rating = Math.min(numberOfStars, rating);
            }
        }

    }

  /* ----------- GETTERS AND SETTERS ----------- */

    public float getRating() {
        return rating;
    }

    /**
     * 设置进度 （ 0 ~ ）
     *
     * @param rating
     */
    public void setRating(float rating) {
        this.rating = normalizeRating(rating);
        if (stepSize != Float.MAX_VALUE) {
            rating -= rating % stepSize;
        }
        // request redraw of the view
        invalidate();
        if (listener != null) {
            listener.onRatingChanged(this, rating);
        }
    }

    /**
     * 获取是否仅是指示标志
     */
    public boolean isIndicator() {
        return this.isIndicator;
    }

    /**
     * 设置是否仅是指示标志
     * 如果设置true 触摸将不起作用，false用户可以触摸进度
     *
     * @param indicator
     */
    public void setIndicator(boolean indicator) {
        this.isIndicator = indicator;
    }


    /**
     * 获取步辐
     *
     * @return
     */
    public float getStepSize() {
        return stepSize;
    }

    /**
     * 设置步辐
     *
     * @param stepSize
     */
    public void setStepSize(float stepSize) {
        this.stepSize = stepSize;
        if (stepSize <= 0) {
            throw new IllegalArgumentException(String.format("MyRatingBar initialized with invalid value for stepSize. Found %f, but should be greater than 0", stepSize));
        }
        // request redraw of the view
        invalidate();
    }

    /**
     * Normalizes rating passed by argument between 0 and numberOfStars.
     *
     * @param rating
     * @return
     */
    private float normalizeRating(float rating) {
        if (rating < 0) {
            Log.w("MyRatingBar", String.format("Assigned rating is less than 0 (%f < 0), I will set it to exactly 0", rating));
            return 0;
        } else if (rating > numberOfStars) {
            Log.w("MyRatingBar", String.format("Assigned rating is greater than numberOfStars (%f > %d), I will set it to exactly numberOfStars", rating, numberOfStars));
            return numberOfStars;
        } else {
            return rating;
        }
    }

    /**
     * Sets OnRatingBarChangeListener.
     *
     * @param listener
     */
    public void setOnRatingBarChangeListener(OnRatingBarChangeListener listener) {
        this.listener = listener;
    }

    public interface OnRatingBarChangeListener {

        /**
         * 评星控件变化监听
         *
         * @param simpleRatingBar .
         * @param rating          进度.
         */
        void onRatingChanged(MyRatingBar simpleRatingBar, float rating);

    }

}
