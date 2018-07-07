package com.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycom.R;

import java.util.List;

/***
 *  自定义控制  继承现有控件GridView
 *  组件功能： 横向列表（带下划线）
 *  notice:  使用时  需要<HorizontalScrolView <LinearLayout/> 包裹 />  才能滚动
 *  author:  fjw0312@163.com
 *   date:   2017.10.10
 * */
public class MyHorizontalListView extends GridView{

		public MyHorizontalListView(Context context, AttributeSet attrs,
				int defStyle) {
			super(context, attrs, defStyle);
			// TODO Auto-generated constructor stub
			init_view(context);
		}

		public MyHorizontalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub 
			init_view(context);
		}

		public MyHorizontalListView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			init_view(context);
		}
		
	    // 控件 默认 参数
		public int itemWith = 200;   //子item 宽度
	    public int hasSelect_id = 0;  //选择item 0
	    public int hasSelect_color = Color.RED;  //选择item 颜色
	    public int default_TxColor = Color.BLACK; //正常字体颜色
	    public int default_LyColor = Color.TRANSPARENT; //字体下划底板颜色

		
		// 使用变量
		MyHorizontalListViewAdapter adapter; //适配器变量
		TextView textView;  // 选中的item

		//初始化 控件
		private void init_view(Context context){
			adapter = new MyHorizontalListViewAdapter();
			this.setAdapter(adapter);

		}

		//更新  控件数据   外部调用api
		public void Update(List<String> lst,int ItemWith){	
			itemWith = ItemWith;
			Update(lst);
		}
	   //更新  控件数据   外部调用api
		public void Update(List<String> lst){
			ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
			layoutParams.width = lst.size() * itemWith;
			this.setLayoutParams(layoutParams);
			this.setNumColumns(lst.size());
			this.setVerticalScrollBarEnabled(false);
			adapter = new MyHorizontalListViewAdapter(lst);
			this.setAdapter(adapter);
		}
	    //选择 item   外部调用api
		public void Select(int itemId){
			hasSelect_id = itemId;
			adapter.notifyDataSetChanged();
			//TextPaint paint = textView.getPaint();  //设置字体变粗
			//paint.setFakeBoldText(true);
		}

	//定义 适配器
	public class MyHorizontalListViewAdapter extends BaseAdapter{
		
		public MyHorizontalListViewAdapter() {
			super();
			// TODO Auto-generated constructor stub
		}
		public MyHorizontalListViewAdapter(List<String> lst) {
			super();
			// TODO Auto-generated constructor stub
			Str_lst = lst;
		}
		
		List<String> Str_lst = null;
		class ViewHolder{
			TextView textView;
			LinearLayout ly;
		};


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(Str_lst==null) return 0;
			return Str_lst.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			if(Str_lst==null) return null;
			return Str_lst.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			String text = (String)getItem(arg0);
			View view;
			ViewHolder viewHolder = new ViewHolder();
			TextView textView;
			
			if(arg1==null){
				view = View.inflate(getContext(), R.layout.item_horlstview, null);
				viewHolder.textView = (TextView)view.findViewById(R.id.Tx_barItem);
				viewHolder.ly = (LinearLayout) view.findViewById(R.id.ly_id);
				view.setTag(viewHolder);

			}else{ 
				view = arg1;
				viewHolder = (ViewHolder)view.getTag();
			}
			
			if(viewHolder != null){
				viewHolder.textView.setText(text);
				//处理初始化 第一个元素 选中颜色
				if(arg0 == hasSelect_id){
					viewHolder.textView.setTextColor(hasSelect_color);
					viewHolder.ly.setBackgroundColor(hasSelect_color);
				}else{
					viewHolder.textView.setTextColor(default_TxColor);
					viewHolder.ly.setBackgroundColor(default_LyColor);
				}
			}
			
			
			return view;
		}
		
	}


	/**
	 * 使用demo:
	 * //1.初始化
	 *  myHorizontalListView = (MyHorizontalListView) view.findViewById(R.id.MyHorizontalListView); //获取控件
	    myHorizontalListView.Update(Arrays.asList(strTitle_lst),(int)(MainActivity.wPx/strTitle_lst.length));//更新控件数据 item.lenth
	 		  //myHorizontalListView.Update(Arrays.asList(strTitle_lst),250);//更新控件数据 item.lenth
	   //2.设置监听
	   myHorizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //控件设置item点击监听
		 @Override
		 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				 //  Adapter adapter = parent.getAdapter(); //获得控件 适配器
				 myHorizontalListView.Select(position);  //更新选中item

				 //       translateMyHorizontalListView_itemToCenter(position);  //选择item 自动居中移动
				 }
		 });

	 	//3.设置Tag 中间显示
		 //定义 控制横向列表 点击选择的Item 居中自移动
		 private void translateMyHorizontalListView_itemToCenter(int position){
				 int horLstView_Width = myHorizontalListView.getWidth(); //横向列表长度
				 int horSrcView_Width = horizontalScrollView.getWidth();  //横向列表可见长度
				 int tranlate = position * myHorizontalListView.itemWith + myHorizontalListView.itemWith / 2 - horSrcView_Width / 2;
				 if (tranlate < 0) {
				 tranlate = 0;
				 } else if (tranlate > horLstView_Width - horSrcView_Width) {
				 tranlate = horLstView_Width - horSrcView_Width;
				 }

				 horizontalScrollView.smoothScrollTo(tranlate, 0); // horizontalScrollView.scrollTo(tranlate,0);
		 }
	    // 其他 页面滑动  带动Tag 滑动 myHorizontalListView.Select(position);  //更新选中item
	 *
	 */
}
