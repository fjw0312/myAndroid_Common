package com.fragment.single;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.fragment.testfragement.Fragment1;
import com.fragment.testfragement.Fragment2;
import com.fragment.testfragement.Fragment3;
import com.mycom.R;

/**
 * Created by jiongfang on 2018/6/1.
 *
 * 碎片的最简易用法，特点 碎片页面 更换 但 不保存碎片，一更换马上销毁
 */
public class TestSignalFragmentActivity extends AppCompatActivity {

    FrameLayout ly;
    Button bn;


    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testsignalfragment);

        init_fragment(savedInstanceState);

        ly = (FrameLayout)findViewById(R.id.ly);
        bn = (Button)findViewById(R.id.bn);
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //碎片 最简易 使用方式1：
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.ly, new LeftFragment());  //碎片 销毁 更换  相当于 add remove 的组合效果
                transaction.addToBackStack(null);  //将碎片放入栈中
                transaction.commit();

                //碎片 使用方式2：
               // show_fragement(fragment2);
            }
        });
    }

    //初始化 碎片页面
    private void init_fragment(Bundle savedInstanceState){  //必须在onCreate调用
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if(savedInstanceState != null){  //内存重启  不用添加碎片页面了
            fragment1 = (Fragment1)fragmentManager.findFragmentByTag(fragment1.getClass().getName());
            fragment2 = (Fragment2)fragmentManager.findFragmentByTag(fragment2.getClass().getName());
            fragment3 = (Fragment3)fragmentManager.findFragmentByTag(fragment3.getClass().getName());

        }else{
            fragment1 = new Fragment1();
            fragment2 = new Fragment2();
            fragment3 = new Fragment3();
            fragmentTransaction.add(R.id.ly,fragment1,fragment1.getClass().getName());
            fragmentTransaction.add(R.id.ly,fragment2,fragment2.getClass().getName());
            fragmentTransaction.add(R.id.ly,fragment3,fragment3.getClass().getName());
        }
        fragmentTransaction.hide(fragment1);
        fragmentTransaction.hide(fragment2);
        fragmentTransaction.hide(fragment3);
        fragmentTransaction.commit();   //提交事务
    }

    private void show_fragement(Fragment fragment){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment1);
        fragmentTransaction.hide(fragment2);
        fragmentTransaction.hide(fragment3);
        fragmentTransaction.show(fragment);
        // fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);  //设置 淡出动画  -- 效果不好
        //  fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);  //设置 淡出动画
        //  fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);  //设置 淡出动画
        // fragmentTransaction.setCustomAnimations(R.anim.my_anima,R.anim.my_anima2);  //设置  自定义动画
        fragmentTransaction.commit();   //提交事务
    }


}
