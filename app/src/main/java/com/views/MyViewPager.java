package com.views;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fjw0312 on 2017/10/10.
 * 自定义控制  继承现有控件ViewPager
 * 组件功能：  页面切换容器
 * notice:   Context 必须传入FragmentActivity
 * author:  fjw0312@163.com
 */

public class MyViewPager extends ViewPager {
    public MyViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init_view(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init_view(context);
    }

    public MyViewPager(Context context) {
        super(context);
        init_view(context);
    }

    // 使用变量定义
    Context mContext;
    AdapterPagerFragment adapter;


    //初始化 控件
    private void init_view(Context context) {
        mContext = context;
    }

    //参数1：activity中viewpager布局  和fragment中ViewPager布局 不一样
    //外部调用api  更新 控件页面数据  参数：FrameActivity.getSupportFragmentManager() / fragment.getChildFragmentManager()
    public void init_adapter(FragmentManager fragmentManager, List<Fragment> mFragments) {
        if (adapter != null) {
            adapter.UpdateAdapter(mFragments);
            adapter.notifyDataSetChanged();
        } else {
            adapter = new AdapterPagerFragment(fragmentManager, mFragments);
            this.setAdapter(adapter);
        }
    }

    //外部调用api  更新  控件某个item 数据（某个页面数据）
    public void Update(int id, Fragment fragment) {
        if (adapter != null) {
            adapter.UdateAdapter(id, fragment);
            adapter.notifyDataSetChanged();
        }
    }


    //定义 碎片适配器
    private class AdapterPagerFragment extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public AdapterPagerFragment(FragmentManager fm) {
            super(fm);
        }

        public AdapterPagerFragment(FragmentManager fm, List<Fragment> mFragments) {
            super(fm);
            this.mFragments = mFragments;
        }

        //更新 适配器 链表
        public void UpdateAdapter(List<Fragment> mFragments) {
            if (this.mFragments != null) {
                this.mFragments.clear();
            }
            this.mFragments = mFragments;
        }

        //更新  适配器  链表某个字item
        public void UdateAdapter(int id, Fragment fragment) {
            if (id >= getCount()) return;
            List<Fragment> newFragments = new ArrayList<Fragment>();
            for (int i = 0; i < mFragments.size(); i++) {
                if (i == id) {
                    newFragments.add(fragment);
                } else {
                    newFragments.add(mFragments.get(i));
                }
            }
            mFragments.clear();
            mFragments = newFragments;
            newFragments = null;
        }


        @Override
        public Fragment getItem(int position) {
            if (mFragments == null) return null;
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            if (mFragments == null) return 0;
            return mFragments.size();
        }
    }

    /**
     * 实现Viewpager的翻页效果
     * 1.ViewPager 一定先参数设置可拓展 android:clipChildren="false"
     * 2.设置ViewPager的布局RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams();
     * ViewGroup.LayoutParams lp = mViewPager.getLayoutParams();
     * eg: params.Width = (int) (getResources().getDisplayMetrics().widthPixels * 3.0f / 5.0f);
     * 3. viewpager设置setPageTransformer（true, new GallyPageTransformer()）   notice:  方法一定要在setAdapter（）方法之前 即可！
     * 4. 设置 缓存页面数量>=2  mViewPager.setOffscreenPageLimit(num);
     * 5. 设置页面间距  mViewPager.setPageMargin(100);
     */
    public class GallyPageTransformer implements PageTransformer {
        private static final float MIN_SCALE = 0.85f;

        @Override
        public void transformPage(View page, float position) {
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float rotate = 10 * Math.abs(position);
            //position小于等于1的时候，代表page已经位于中心item的最左边，
            //此时设置为最小的缩放率以及最大的旋转度数
            if (position <= -1) {
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
                page.setRotationY(rotate);
            } else if (position < 0) {//position从0变化到-1，page逐渐向左滑动
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setRotationY(rotate);
            } else if (position >= 0 && position < 1) {//position从0变化到1，page逐渐向右滑动
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setRotationY(-rotate);
            } else if (position >= 1) {//position大于等于1的时候，代表page已经位于中心item的最右边
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setRotationY(-rotate);
            }
        }
    }

    /*********
     * 使用 demo:
     *
     *   myViewPager = (MyViewPager) findViewById(R.id.MyViewPager);       //获取控件
     myViewPager.init_adapter(getSupportFragmentManager(),fragments);  //初始化适配器           初始化数据
     myViewPager.setOffscreenPageLimit(fragments.size()-1);            //设置 缓冲个数       保证跳转流畅性  fragments.size()-1:表示所有页面都缓冲
     myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {  //设置页面滑动监听
     @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

     }

     @Override //页面 切换结束  position页面id
     public void onPageSelected(int position) {
     Log.w("TabActivity>>","myViewPager>onPageSelected");
     navigationBar.onChange(position,false);

     }

     @Override //state 0->1->2->0  1正在滑动 2滑动完毕  0不动了
     public void onPageScrollStateChanged(int state) {

     }
     });
     //其他代码切换 控制  myViewPager.setCurrentItem(position);     //切换ViewPager页面
     */


}
