package com.mycom.Demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mycom.R;

import java.util.List;

/**
 * 下拉 后折叠效果 框架 CoordinatorLayout  使用案例
 * Created by jiongfang on 2018/9/6.
 *
 * 学习资料;http://www.jcodecraeer.com/plus/view.php?aid=10428
 *
 * notice: 注意依赖包 会有冲突要一致：
 *  compile 'com.android.support:appcompat-v7:25.3.1'
 *  compile 'com.android.support:design:25.3.1'
 */
public class CollapseToolbarActivity extends Activity {

    private List<Fragment> mFragments;
    private String[] mTabTitles = {"消息", "好友", "动态"};

    String[] str_s = {"微信","QQ","陌陌","来往","探探",
            "爱奇艺","优酷","腾讯视频","乐视","bilibili",
            "凤凰","头条","网易","虎扑","天行","美团","携程","滴滴","京东","百度","腾讯","阿里" };

    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collapsetoolbar_activity);
       // FullScreenUI.FullScreenUI(this);

     //    listView = (ListView)findViewById(R.id.lv);
     //    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,str_s);
     //    listView.setAdapter(adapter);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(getApplicationContext(), R.layout.item_horlstview, null);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.mTextView.setText(str_s[position]);
        }

        @Override
        public int getItemCount() {
            return str_s.length;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.Tx_barItem);
        }
    }


}
