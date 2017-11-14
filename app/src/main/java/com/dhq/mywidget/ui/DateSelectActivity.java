package com.dhq.mywidget.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dhq.mywidget.R;
import com.dhq.mywidget.selectview.DateDownView;

/**
 * DESC 时间选择界面
 * Author douhaoqiang
 * Create by 2017/8/16.
 */

public class DateSelectActivity extends AppCompatActivity {

    private TextView tvStartDate;
    private TextView tvEndDate;

    private String startDate;
    private String endDate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select_view);

        tvStartDate = (TextView) findViewById(R.id.tv_start_date);
        tvEndDate = (TextView) findViewById(R.id.tv_end_date);
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDownView dateDownView = new DateDownView(DateSelectActivity.this, new DateDownView.DateCallBack() {
                    @Override
                    public void callback(String date, String year, String month, String day) {
                        startDate = date;
                        tvStartDate.setText(date);
                    }
                });

                dateDownView.show(tvStartDate);
            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDownView dateDownView = new DateDownView(DateSelectActivity.this, startDate, new DateDownView.DateCallBack() {
                    @Override
                    public void callback(String date, String year, String month, String day) {
                        endDate = date;
                        tvEndDate.setText(date);
                    }
                });

                dateDownView.show(tvEndDate);
            }
        });

    }
}
