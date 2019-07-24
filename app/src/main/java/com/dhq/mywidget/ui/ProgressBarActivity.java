package com.dhq.mywidget.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dhq.mywidget.R;
import com.dhq.mywidget.progressbar.CircleProgressView;

/**
 * DESC 圆形进度条
 * Created by douhaoqiang on 2017/7/24.
 */

public class ProgressBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_progress);

        CircleProgressView progressView = (CircleProgressView) findViewById(R.id.main_circleprogressView);
        progressView.setProgress(20);

    }
}
