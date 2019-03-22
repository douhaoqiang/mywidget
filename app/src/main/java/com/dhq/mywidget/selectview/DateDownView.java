package com.dhq.mywidget.selectview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dhq.baselibrary.util.LogUtil;
import com.dhq.mywidget.R;
import com.dhq.mywidget.dialog.CustomPopWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * DESC
 * Created by DHQ on 2017/7/8 0008.
 */

public class DateDownView {

    private static int DEFAULT_YEARS = 10;
    private int mYearCount = DEFAULT_YEARS;

    private Context mContext;
    private View mView;
    private View rootView;
    private CustomPopWindow popWindow;
    private DateCallBack mCallBack;
    private ArrayList<String> yearList = new ArrayList<>();
    private ArrayList<String> monthList = new ArrayList<>();
    private ArrayList<String> dayList = new ArrayList<>();
    private SimpleDateFormat valueSf = new SimpleDateFormat("yyyy-MM-dd");
    private String mYear, mMonth, mDay;

    private WheelView mWheelYear;
    private WheelView mWheelMonth;
    private WheelView mWheelDay;

    private String startTime;//开始时间
    private String[] startDates;//开始时间
    private String[] currentDateArray;

    int monthIndex = 0;
    int dayIndex = 0;

    /**
     * 默认时间选择（往前显示10年数据）
     *
     * @param context
     * @param callBack
     */
    public DateDownView(Context context, DateCallBack callBack) {
        this.mContext = context;
        this.mCallBack = callBack;
        initDates();
    }

    /**
     * 时间选择（显示大于startTime时间数据）
     *
     * @param context
     * @param startTime 开始时间 格式为"yyyy-MM-dd"
     * @param callBack
     */
    public DateDownView(Context context, String startTime, DateCallBack callBack) {
        this.mContext = context;
        this.mCallBack = callBack;
        try {
            this.startTime = startTime;
            Date startDate = valueSf.parse(startTime);
            startDates = startTime.split("-");
            initDates();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("时间格式错误 必须为yyyy-MM-dd格式");
        }

    }


    /**
     * 时间选择（往前显示yearCount年数据）
     *
     * @param context
     * @param yearCount
     * @param callBack
     */
    public DateDownView(Context context, int yearCount, DateCallBack callBack) {
        this.mContext = context;
        this.mCallBack = callBack;
        mYearCount = yearCount;
        initDates();
    }


    /**
     * 初始化数据
     */
    private void initDates() {

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();//获取现在的时间
        String dateStr = valueSf.format(currentDate);//获取现在的时间(yyyy-MM-dd)

        currentDateArray = dateStr.split("-");

        initYear();


//        //添加12个月数据
//        for (int i = 1; i <= 12; i++) {
//            String monthStr = "";
//            if (i < 10) {
//                monthStr = "0" + i;
//            } else {
//                monthStr = String.valueOf(i);
//            }
//            if (currentDateArray[1].equals(monthStr)) {
//                monthIndex = i;
//                mMonth = monthStr;
//            }
//            monthList.add(monthStr);
//        }
        initMonth(mYear);
        initDay(mYear, mMonth);

//        //默认天数
//        for (int i = 1; i <= 31; i++) {
//            String dayStr = "";
//            if (i < 10) {
//                dayStr = "0" + i;
//            } else {
//                dayStr = String.valueOf(i);
//            }
//            if (currentDateArray[2].equals(dayStr)) {
//                dayIndex = i;
//                mDay = dayStr;
//            }
//            dayList.add(dayStr);
//        }


        rootView = LayoutInflater.from(mContext).inflate(R.layout.date_select_view, null);
        TextView tvCommit = (TextView) rootView.findViewById(R.id.tv_commit);
        TextView tvCancle = (TextView) rootView.findViewById(R.id.tv_cancle);
        mWheelYear = (WheelView) rootView.findViewById(R.id.wheelView_year);
        mWheelMonth = (WheelView) rootView.findViewById(R.id.wheelView_month);
        mWheelDay = (WheelView) rootView.findViewById(R.id.wheelView_day);

        mWheelYear.setDatas(yearList);
        mWheelMonth.setDatas(monthList, monthIndex);
        mWheelDay.setDatas(dayList, dayIndex);

        mWheelYear.setSelectListener(new WheelView.WheelListener<String>() {
            @Override
            public String setShowValue(String item) {
                return item;
            }

            @Override
            public void onSelectItem(String item) {
                mYear = item;
                Log.d("date", item);
                initMonth(mYear);
                updateMonthWheel();
//                updateDayWheel();
            }
        });

        mWheelMonth.setSelectListener(new WheelView.WheelListener<String>() {
            @Override
            public String setShowValue(String item) {
                return item;
            }

            @Override
            public void onSelectItem(String item) {
                mMonth = item;
                initDay(mYear,mMonth);
                updateDayWheel();
            }
        });

        mWheelDay.setSelectListener(new WheelView.WheelListener<String>() {
            @Override
            public String setShowValue(String item) {
                return item;
            }

            @Override
            public void onSelectItem(String item) {
                mDay = item;
            }
        });

        //确定选择时间
        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dissmiss();
                if (mCallBack != null) {
                    mCallBack.callback(mYear + "-" + mMonth + "-" + mDay, mYear, mMonth, mDay);
                }
            }
        });

        //取消
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dissmiss();
            }
        });
    }

    /**
     * 初始化年
     */
    private void initYear() {
        //获取最近10年列表
        for (int i = 0; i < mYearCount; i++) {
            int year = Integer.valueOf(currentDateArray[0]) - i;
            if (startDates != null) {
                if (year < Integer.valueOf(startDates[0])) {
                    break;
                } else if (year == Integer.valueOf(startDates[0])) {
                    if (isLastDayOfYear()) {
                        break;
                    }
                }
            }
            yearList.add(String.valueOf(year));
        }
        mYear = yearList.get(0);
    }


    /**
     * 初始化月数据
     *
     * @param year
     */
    private void initMonth(String year) {

        monthList.clear();

        //添加12个月数据
        for (int i = 1; i <= 12; i++) {
            String monthStr = "";
            if (i < 10) {
                monthStr = "0" + i;
            } else {
                monthStr = String.valueOf(i);
            }

            if (startDates != null && year.equals(startDates[0])) {
                //于开始时间的年相同 判断应该显示几月
                if (Integer.valueOf(monthStr) > Integer.valueOf(startDates[1])) {
                    monthList.add(monthStr);
                } else if (Integer.valueOf(monthStr) == Integer.valueOf(startDates[1])) {
                    if (!isLastDayOfMonth(year, monthStr)) {
                        monthList.add(monthStr);
                    }
                }
            } else {
                if (monthIndex == 0 && currentDateArray[1].equals(monthStr)) {
                    monthIndex = i;
                    mMonth = monthStr;
                }
                monthList.add(monthStr);
            }

        }

        if (monthIndex == 0) {
            monthIndex = 1;
            mMonth = monthList.get(0);
        }
    }

    /**
     * 初始化天数据
     */
    private void initDay(String year, String month) {
        dayList.clear();
        int daysOfMonth = getDaysOfMonth(year, month);
        //默认天数
        for (int i = 1; i <= daysOfMonth; i++) {
            String dayStr = "";
            if (i < 10) {
                dayStr = "0" + i;
            } else {
                dayStr = String.valueOf(i);
            }

            if (startDates != null && year.equals(startDates[0]) && month.equals(startDates[1])) {
                //于开始时间的日期相同 判断应该显示多少天
                if (Integer.valueOf(dayStr) > Integer.valueOf(startDates[2])) {
                    dayList.add(dayStr);
                }
            } else {
                if (dayIndex == 0 && currentDateArray[2].equals(dayStr)) {
                    dayIndex = i;
                    mDay = dayStr;
                }
                dayList.add(dayStr);
            }

        }
        if (dayIndex == 0) {
            dayIndex = 1;
            mDay = dayList.get(0);
        }

    }

    public void show(View view) {
//        int viewWidth = view.getMeasuredWidth();
//        rootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        int popWidth = rootView.getMeasuredWidth();
//        int xPoint = -(popWidth - viewWidth) / 2;
        show(view, 0);
    }

    public void show(View view, int xOff) {
        if (rootView == null) {
            return;
        }
        this.mView = view;
        popWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                .setView(rootView)//显示的布局，还可以通过设置一个View
                //     .size(600,400) //设置显示的大小，不设置就默认包裹内容
                .setFocusable(true)//是否获取焦点，默认为ture
                .setOutsideTouchable(true)//是否PopupWindow 以外触摸dissmiss
                .create()//创建PopupWindow
                .showAsDropDown(mView, xOff, 0);//显示PopupWindow
    }


    /**
     * 更新天
     */
    private void updateMonthWheel() {

        mWheelMonth.update();

    }

    /**
     * 更新天
     */
    private void updateDayWheel() {
//        int daysOfMonth = getDaysOfMonth(mYear, mMonth);
//        if (dayList.size() > daysOfMonth) {
//            for (int i = dayList.size() - 1; i >= daysOfMonth; i--) {
//                dayList.remove(i);
//            }
//        } else if (dayList.size() < daysOfMonth) {
//            for (int i = dayList.size() + 1; i <= daysOfMonth; i++) {
//                dayList.add(String.valueOf(i));
//            }
//        }
        mWheelDay.update();

    }

    /**
     * 获取当月的天数
     *
     * @param mYear
     * @param mMonth
     * @return
     */
    private int getDaysOfMonth(String mYear, String mMonth) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, Integer.valueOf(mYear));
        a.set(Calendar.MONTH, Integer.valueOf(mMonth) - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }


    /**
     * 判断日期是否是今年的最后一天
     *
     * @return
     */
    private boolean isLastDayOfYear() {

        Calendar cal = Calendar.getInstance();

//        cal.set(Calendar.DAY_OF_YEAR, 1);
//        cal.getTime();
//        //一年的第一天
//        String firstDay = valueSf.format(cal.getTime());
        cal.set(Calendar.YEAR, Integer.valueOf(startDates[0]));
        cal.set(Calendar.DAY_OF_YEAR,
                cal.getActualMaximum(Calendar.DAY_OF_YEAR));
        //一年的最后一天
        String lastDay = valueSf.format(cal.getTime());
        if (lastDay.equals(startTime)) {
            return true;
        }
        return false;
    }

    /**
     * 判断日期是否是这个月的最后一天
     *
     * @return
     */
    private boolean isLastDayOfMonth(String year, String month) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.valueOf(year));
        cal.set(Calendar.MONTH, Integer.valueOf(month) - 1);
//        cal.set(Calendar.DAY_OF_MONTH, 1);
//        cal.getTime();
//        start_time.setText(dateFormater.format(cal.getTime()) + "");

        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        //一个月的最后一天
        String lastDay = valueSf.format(cal.getTime());
        if (lastDay.equals(startTime)) {
            return true;
        }
        return false;
    }


    public interface DateCallBack {
        void callback(String date, String year, String month, String day);
    }

}
