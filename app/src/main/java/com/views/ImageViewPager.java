package com.views;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fjw0312 on 2018/9/10.
 * 自定义控制  继承现有控件ViewPager
 * 组件功能：  页面切换容器
 * notice:
 * author:  fjw0312@163.com
 */

public class ImageViewPager extends ViewPager {
    public ImageViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init_view(context);
    }

    public ImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init_view(context);
    }

    public ImageViewPager(Context context) {
        super(context);
        init_view(context);
    }

    //dp转换成px
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    // 使用变量定义
    Context mContext;
    AdapterImagePager adapter;


    //初始化 控件
    private void init_view(Context context) {
        mContext = context;
    }

    //参数1：activity中viewpager布局  和fragment中ViewPager布局 不一样
    //外部调用api  更新 控件页面数据  参数：FrameActivity.getSupportFragmentManager() / fragment.getChildFragmentManager()
    public void init_adapter(List<Integer> lstImgId) {
        if (adapter != null) {
            adapter.UpdateAdapter(lstImgId);
            adapter.notifyDataSetChanged();
        } else {
            adapter = new AdapterImagePager(lstImgId);
            this.setAdapter(adapter);
        }
    }

    //外部调用api  更新  控件某个item 数据（某个页面数据）
    public void Update(int index, int rsId) {
        if (adapter != null) {
            adapter.UdateAdapter(index, rsId);
            adapter.notifyDataSetChanged();
        }
    }


    //定义 碎片适配器
    public class AdapterImagePager extends PagerAdapter {
        //  private List<ImageView> listImages;
        private List<Integer> lstImgId;

        //     public AdapterImagePager(List<ImageView> listImages) {
        //         this.listImages = listImages;
        //     }
        public AdapterImagePager(List<Integer> lstImgId) {
            this.lstImgId = lstImgId;
        }


        //更新  适配器  链表某个字item
        public void UdateAdapter(int index, int rsId) {
            if (index >= getCount()) return;
            List<Integer> newListIds = new ArrayList<Integer>();
            for (int i = 0; i < lstImgId.size(); i++) {
                if (i == index) {
                    newListIds.add(rsId);
                } else {
                    newListIds.add(lstImgId.get(i));
                }
            }
            lstImgId.clear();
            lstImgId = newListIds;
            newListIds = null;
        }

        //更新 适配器 链表
        public void UpdateAdapter(List<Integer> lstImgId) {
            if (this.lstImgId != null) {
                this.lstImgId.clear();
            }
            this.lstImgId = lstImgId;
        }

        @Override
        public int getCount() {
            //  return listImages == null ? 0 : listImages.size();
            return lstImgId == null ? 0 : lstImgId.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(lstImgId.get(position));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // container.removeView(listImages.get(position));
            container.removeView((View) object);
        }


    }


    public class GallyPageTransformer implements ViewPager.PageTransformer {
        private static final float MAX_SCALE = 1.0f;
        private static final float MIN_SCALE = 0.85f;//0.85f

        @Override
        public void transformPage(View page, float position) {
            //setScaleY只支持api11以上
            if (position < -1) {
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            } else if (position <= 1) //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
            { // [-1,1]
//              Log.e("TAG", view + " , " + position + "");
                float scaleFactor = MIN_SCALE + (1 - Math.abs(position)) * (MAX_SCALE - MIN_SCALE);
                page.setScaleX(scaleFactor);
                //每次滑动后进行微小的移动目的是为了防止在三星的某些手机上出现两边的页面为显示的情况
                if (position > 0) {
                    page.setTranslationX(-scaleFactor * 2);
                } else if (position < 0) {
                    page.setTranslationX(scaleFactor * 2);
                }
                page.setScaleY(scaleFactor);

            } else { // (1,+Infinity]

                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);

            }
        }
    }
    /**
     * 实现Viewpager的翻页效果
     *  1.ViewPager 一定先参数设置可拓展 android:clipChildren="false" 以及 一定要在父ViewGroup 设置clipChildren="false"
     *  2.设置ViewPager的布局RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams();  //也可以在xml 配置
     *         ViewGroup.LayoutParams lp = mViewPager.getLayoutParams();
     *     eg: params.Width = (int) (getResources().getDisplayMetrics().widthPixels * 3.0f / 5.0f);
     *  3. viewpager设置setPageTransformer（true, new GallyPageTransformer()）   notice:  方法一定要在setAdapter（）方法之前 即可！
     *  4. 设置 缓存页面数量>=2  mViewPager.setOffscreenPageLimit(num);
     *  5. 设置页面间距  mViewPager.setPageMargin(100);
     */
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
