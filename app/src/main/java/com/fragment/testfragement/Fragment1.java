package com.fragment.testfragement;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fragment.base.FragmentLazy;
import com.mycom.R;

/**
 * Created by jiongfang on 2018/6/1.
 */
public class Fragment1 extends FragmentLazy {

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        //获取相关数据  并 数据控件处理
        //   int src_id = (int)getArguments().get("key");
    }


    @Override
    protected void stopLoad() {
        super.stopLoad();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        Log.i("Class1Fragment", "intio onCreateView");

        return view;
    }

    private Activity mActivity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
