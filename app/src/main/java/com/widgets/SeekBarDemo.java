package com.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.mycom.R;

/**
 * Created by Administrator on 2018/3/16.
 */

public class SeekBarDemo implements SeekBar.OnSeekBarChangeListener {
    public SeekBarDemo() {
    }

    Context mContext;
    View mView;
    SeekBar seekBar;

    public View setView(Context context) {
        mContext = context;
        mView = LayoutInflater.from(mContext).inflate(R.layout.widgets_seekbar, null);

        seekBar = (SeekBar) mView.findViewById(R.id.id_seekbar);
        seekBar.setOnSeekBarChangeListener(this);

        return mView;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
