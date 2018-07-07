package com.widgets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.MyApplication;
import com.mycom.R;
import com.utils.LogcatFileHelper;
import com.utils.dialog.DialogInterface;

/**
 * Created by Administrator on 2018/3/15.
 */

public class TestActivity extends AppCompatActivity {

    Button bn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widgets);

        LogcatFileHelper.i("Jiong>>文件储存路劲："," "+ MyApplication.SAVE_FILE_PATH);
        SwitchDemo switchInterface = new SwitchDemo();
        View switchView = switchInterface.setView(this);
        addContentView(switchView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        SeekBarDemo seekBarInterface = new SeekBarDemo();
        View seekbarView = seekBarInterface.setView(this);
        addContentView(seekbarView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        bn = (Button)findViewById(R.id.Bn_id);
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogInterface().setComDialogInterface(new DialogInterface.ComDialogInterface() {
                    @Override
                    public void OnPositiveButton() {
                        Log.i("Jiong>>","OnPositiveButton");
                    }

                    @Override
                    public void OnNegativeButton() {
                        Log.i("Jiong>>","OnNegativeButton");
                    }

                    @Override
                    public void OnNeutralButton() {
                        Log.i("Jiong>>","OnNeutralButton");
                    }

                    @Override
                    public void OnInputDialog(String str) {
                        Log.i("Jiong>>","OnInputDialog  "+str);
                    }
                }).createComDialog(TestActivity.this,"提示","", DialogInterface.SINGLE_BUTTON);
            }
        });
    }
}
