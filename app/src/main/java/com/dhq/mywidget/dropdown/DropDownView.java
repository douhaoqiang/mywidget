package com.dhq.mywidget.dropdown;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dhq.mywidget.R;

/**
 * DESC
 * Created by douhaoqiang on 2017/7/4.
 */

public class DropDownView<T> extends LinearLayout {


    private String[] mDatas = {"数据1", "数据2", "数据3", "数据4", "数据5", "数据6", "数据7", "数据8", "数据9"};

    private TextView tvSelect;
    private PopupWindow mPopupWindow;
    private BaseAdapter mAdapter;
    private DropDownCallBack mListener;
    private View mTabView;
    private View mDropView;

    public DropDownView(Context context) {
        super(context);
        init();
    }

    public DropDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DropDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTabView = LayoutInflater.from(getContext()).inflate(R.layout.drop_down_tab, this);
        tvSelect = (TextView) mTabView.findViewById(R.id.tv_select_view);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
            }
        });
    }

    private void initPopWindow() {
        mDropView = LayoutInflater.from(getContext()).inflate(R.layout.drop_down_view, null);
        ListView listView = (ListView) mDropView.findViewById(R.id.drop_down_list);
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mDatas.length;
            }

            @Override
            public String getItem(int position) {
                return mDatas[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_drop_down, null);
                    viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_show_value);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                if (mListener != null) {
                    viewHolder.tvContent.setText(mListener.showContent(getItem(position)));
                } else {
                    viewHolder.tvContent.setText("");
                }

                convertView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            tvSelect.setText(mListener.showContent(getItem(position)));
                            mListener.clickCallBack(getItem(position));
                        }

                        mPopupWindow.dismiss();
                    }
                });

                return convertView;
            }
        };
        listView.setAdapter(mAdapter);
        mPopupWindow = new PopupWindow(getMeasuredWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setContentView(mDropView);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
    }

    private void showPopWindow() {
        if (mPopupWindow == null) {
            initPopWindow();
        }

//        Map showMap = PopupWindowUtil.needShowUp(mTabView, mDropView);
//        boolean needShowUp = (Boolean) showMap.get("needShowUp");
//        int yHeight = (int) showMap.get("yHeight");
//        if(needShowUp){
//            mPopupWindow.showAsDropDown(this, 0, yHeight);
//        }else{
//            mPopupWindow.showAsDropDown(this, 0, 0);
//        }
//        mPopupWindow.showAtLocation(this, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);



        mPopupWindow.showAsDropDown(this, 0, 0);
    }

    private class ViewHolder {
        TextView tvContent;
    }

    private void setDatas(String[] datas) {
        this.mDatas = datas;
        mAdapter.notifyDataSetChanged();
    }

    public void setListener(DropDownCallBack listener) {
        this.mListener = listener;
    }


    public interface DropDownCallBack<T> {
        String showContent(T item);

        void clickCallBack(T item);
    }

}
