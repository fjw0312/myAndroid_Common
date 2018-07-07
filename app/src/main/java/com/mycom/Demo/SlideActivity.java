package com.mycom.Demo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.activitybase.SlideBaseActivity;
import com.mycom.R;

/**
 * Created by jiongfang on 2018/5/7.
 */
public class SlideActivity extends SlideBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
