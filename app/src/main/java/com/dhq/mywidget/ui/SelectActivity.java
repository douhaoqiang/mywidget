package com.dhq.mywidget.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dhq.mywidget.R;
import com.dhq.mywidget.selectview.SelectView;
import com.dhq.mywidget.selectview.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * DESC
 * Created by douhaoqiang on 2017/7/24.
 */

public class SelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_view);

        SelectView selectView = (SelectView) findViewById(R.id.main_selectview);
        WheelView wheelView = (WheelView) findViewById(R.id.main_wheelview);


        selectView.setSelectListener(new SelectView.SelectListener<String>() {
            @Override
            public String setShowValue(String item) {
                return item;
            }

            @Override
            public void onSelectItem(String item) {

            }
        });
        List<String> list_year = new ArrayList<>();
        for (int i = 1988; i <= 2056; i++) {
            list_year.add(i + "");
        }
        selectView.setDatas(list_year);

        wheelView.setSelectListener(new WheelView.SelectListener<String>() {
            @Override
            public String setShowValue(String item) {
                return item;
            }

            @Override
            public void onSelectItem(String item) {
                Log.d("wheelView", item);
            }
        });
        List<String> list_year2 = new ArrayList<>();
        for (int i = 1988; i <= 2056; i++) {
            list_year2.add(i + "");
        }
        wheelView.setDatas(list_year2);
    }
}