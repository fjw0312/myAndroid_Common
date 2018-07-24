package com.mycom.Demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mycom.R;
import com.views.SlideLayout;

/**
 * Created by jiongfang on 2018/5/7.
 */
public class SlideLayoutActivity extends AppCompatActivity {

    SlideLayout slidelayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidelayout);

        slidelayout = (SlideLayout)findViewById(R.id.slidelayout);
        slidelayout.setOnSildingFinishListener(new SlideLayout.OnSildingFinishListener() {
            @Override
            public void onSildingFinish() {
                SlideLayoutActivity.this.finish();
            }
        });
        slidelayout.setTouchView(slidelayout);
    }
}
