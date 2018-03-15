package com.widgets;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.mycom.R;

/**
 * Created by Administrator on 2018/3/16.
 */

public class SwitchInterface implements CompoundButton.OnCheckedChangeListener{
    public SwitchInterface(){
    }

    Context mContext;
    View mView;
    Switch mSwitch;

    public View setView(Context context){
        mContext = context;
        mView = LayoutInflater.from(mContext).inflate(R.layout.widgets_switch,null);

        mSwitch = (Switch)mView.findViewById(R.id.id_switch);
        mSwitch.setOnCheckedChangeListener(this);

        return mView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.id_switch){
            if(isChecked){
                //按钮 打开
                Log.i("Jiong","按钮 打开");
            }else{
                //按钮 关闭
                Log.i("Jiong","按钮 关闭");
            }
        }
    }
}
