package com.mycom.Demo;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.MyApplication;
import com.mycom.R;
import com.viewbase.OverlaysBaseView;

/**
 * Created by jiongfang on 2018/3/28.
 * 固件更新提示
 * 该视图实例化  必须在MainThread
 */
public class OverlayUpdateInterface {

    private final static String TAG = "OverlayUpdateInterface";
    private OverlaysBaseView overlaysBaseView;


    public OverlayUpdateInterface() {
        initView();
    }

    LinearLayout layout;
    TextView cancel;
    TextView ok;
    //Handler UiHandler = new Hamdler(){}

    private void initView() {
        overlaysBaseView = new OverlaysBaseView(MyApplication.getContext());
        overlaysBaseView.setUpView(R.layout.overlay_update, new OverlaysBaseView.FindViewInterface() {
            @Override
            public void OnFindViewById(View view) {
                layout = (LinearLayout) view.findViewById(R.id.update_lay);
                cancel = (TextView) view.findViewById(R.id.update_cancel);
                ok = (TextView) view.findViewById(R.id.update_ok);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //取消更新下载
                        overlaysBaseView.removeOverlays();
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //进行更新下载
                        overlaysBaseView.removeOverlays();

                    }
                });
            }
        });
        overlaysBaseView.showOverlayView();
    }


}
