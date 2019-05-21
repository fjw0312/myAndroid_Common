package com.fragment.single;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycom.R;

/**
 * Created by jiongfang on 2018/6/1.
 * 基础碎片类
 */
public class RightFragment extends Fragment {

    public RightFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_right, container, false);
        return view;
    }


}
