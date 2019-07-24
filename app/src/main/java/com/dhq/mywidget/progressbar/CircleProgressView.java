package com.dhq.mywidget.progressbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.dhq.mywidget.R;

/**
 * 作者： @author liuhaijian
 * 创建时间： 2017-05-17 18:50
 * 类描述：
 * 修改人：
 * 修改时间：
 */
public class CircleProgressView extends View {

    private static final String TAG = "CircleProgressBar";

    private int mMaxProgress = 100;

    private int mProgress = 0;

    private final int mCircleLineStrokeWidth = 8;

    private final int mTxtStrokeWidth = 2;

    // 画圆所在的距形区域
    private final RectF mRectF;

    private final Paint mPaint;

    private final Context mContext;


    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        mRectF = new RectF();
        mPaint = new Paint();
    }

    Bitmap bitmap;

    @Override
    protected void onDraw(Canvas canvas) {
        bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_progress_title);
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        // 设置画笔相关属性
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb(0xe9, 0xe9, 0xe9));
        canvas.drawColor(Color.TRANSPARENT);
        mPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        // 位置
        mRectF.left = mCircleLineStrokeWidth / 2 + bitmap.getWidth() / 2; // 左上角x
        mRectF.top = mCircleLineStrokeWidth / 2 + bitmap.getWidth() / 2; // 左上角y
        mRectF.right = width - mCircleLineStrokeWidth / 2 - bitmap.getWidth() / 2; // 左下角x
        mRectF.bottom = height - mCircleLineStrokeWidth / 2 - bitmap.getWidth() / 2; // 右下角y

        // 绘制圆圈，进度条背景
        canvas.drawArc(mRectF, 0, 360, false, mPaint);
        mPaint.setColor(Color.rgb(0xf8, 0x60, 0x30));
        canvas.drawArc(mRectF, 90, ((float) mProgress / mMaxProgress) * 360, false, mPaint);

        // 绘制进度文案显示
        mPaint.setStrokeWidth(mTxtStrokeWidth);
        String text = mProgress + "%";
        int textHeight = height / 4;
        mPaint.setTextSize(textHeight);
        int textWidth = (int) mPaint.measureText(text, 0, text.length());
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, width / 2 - textWidth / 2, height / 2 + textHeight / 2, mPaint);


        drawBitmap(canvas);
    }

    /**
     * 方法描述：绘制左上角的图片
     */
    private void drawBitmap(Canvas canvas) {
        int circleR = (int) (mRectF.width() / 2);
        int mBitWidth = bitmap.getWidth();
        int mBitHeight = bitmap.getHeight();
        //计算图片坐标
        int bitX = (int) (getWidth() / 2 - circleR * Math.sin(2 * Math.PI * ((float) mProgress / mMaxProgress))) - mBitWidth / 2;
        int bitY = (int) (getHeight() / 2 + circleR * Math.cos(2 * Math.PI * ((float) mProgress / mMaxProgress))) - mBitHeight / 2;
        canvas.drawBitmap(bitmap, bitX, bitY, mPaint);
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        this.invalidate();
    }


}
