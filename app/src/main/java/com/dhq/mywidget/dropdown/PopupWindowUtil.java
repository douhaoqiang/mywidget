package com.dhq.mywidget.dropdown;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by popfisher on 2016/8/19.
 */

public class PopupWindowUtil {
    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView  呼出window的view
     * @param contentView   window的内容布局
     * @return window显示的左上角的xOff,yOff坐标
     */
    public static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = getAnchorLoc(anchorView);
        final int anchorHeight = anchorView.getMeasuredHeight();
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        // 测量contentView
        measureWindowPos(contentView);

//        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }


    /**
     * 判断是否需要在view的上方显示
     * @param anchorView
     * @param contentView
     * @return
     */
    public static Map needShowUp(View anchorView, View contentView){

        Map map=new HashMap();
        int anchorLoc[] = getAnchorLoc(anchorView);
        int anchorHeight = anchorView.getMeasuredHeight();
        // 获取屏幕的高宽
        int screenHeight = getScreenHeight(anchorView.getContext());
        // 测量contentView
        measureWindowPos(contentView);

        // 计算contentView的高宽
        int windowHeight = contentView.getMeasuredHeight();

        // 判断需要向上弹出还是向下弹出显示
        boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        map.put("needShowUp",isNeedShowUp);
        map.put("yHeight",-(anchorHeight+windowHeight));

        return map;
    }

    /**
     * 获取锚点View在屏幕上的左上角坐标位置
     * @param anchorView
     * @return
     */
    private static int[] getAnchorLoc(final View anchorView){
        final int anchorLoc[] = new int[2];
        anchorView.getLocationOnScreen(anchorLoc);
        return anchorLoc;
    }

    /**
     * 测量弹框View的大小尺寸
     * @param contentView
     * @return
     */
    private static void measureWindowPos(View contentView){
        // 测量contentView
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int dp2px(Context context, float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()) + 0.5f);
    }

}
