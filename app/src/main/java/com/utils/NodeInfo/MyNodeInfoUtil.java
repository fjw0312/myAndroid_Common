package com.utils.NodeInfo;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.MyApplication;
import com.utils.MyBroadcastReceiver;

import java.util.List;

/**
 * Created by jiongfang on 2018/3/1.
 * 获得 无障碍服务的视图 操作接口工具
 */
public class MyNodeInfoUtil {

    public MyNodeInfoUtil(AccessibilityService accessibilityService){
        mAccessibilityService = mAccessibilityService;

    }
    public AccessibilityService mAccessibilityService = null;

    //输入 复制黏贴
    public boolean setClipboard(AccessibilityNodeInfo NodeInfo, String Str) {
      //API>=21 时
        NodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,null);//清除进入内容
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, Str);
        NodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
       return true;
    }
    //输入 复制黏贴
    public boolean setClipboard(Context context, AccessibilityNodeInfo NodeInfo, String clearbuttonId, String Str){
        //android>18 时   需要先清除
        AccessibilityNodeInfo newInfo = getCurrentWindowNodeInfo(clearbuttonId); //获得ting 控件
        if(newInfo!=null){
            newInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", Str);
        clipboard.setPrimaryClip(clip);
        //焦点（NodeInfo是AccessibilityNodeInfo对象）
        NodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        NodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);//粘贴进入内容

        return false;
    }
    //启动 一个第三方app
    public void startTApp(String inPackage, String inClassName){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(inPackage, inClassName));
        MyApplication.getContext().startActivity(intent);
    }
    //判断 是否还在 目标App界面内
    public int isInTargetApp(String inPackage, String inClassName){
        if(mAccessibilityService == null)  return -1;
        AccessibilityNodeInfo NodeInfo = mAccessibilityService.getRootInActiveWindow(); //获取整个窗口视图对象
        if (NodeInfo != null) {
            if(inPackage.equals(NodeInfo.getPackageName())) {  //是否已启动 App
                NodeInfo.recycle();
                return 1;
            }else{
                Log.i("Jiong>>","不在 目标App 界面内");
                return 0;
            }
        }else{
            Log.i("Jiong>>","障碍服务 失效");
        }
        return -1;
    }

    //判别 启动目标App   //返回参数 1：在目标App 0:不在目标App -1:不能获取到界面信息
    public int startTargetApp(String inPackage, String inClassName){
        if(mAccessibilityService == null)  return -1;
        AccessibilityNodeInfo NodeInfo = mAccessibilityService.getRootInActiveWindow(); //获取整个窗口视图对象
        if (NodeInfo != null) {
            if(inPackage.equals(NodeInfo.getPackageName())){  //是否已启动 App
                NodeInfo.recycle();
                return 1;
            }else{  //没启动App
                startTApp(inPackage, inClassName);
                Log.i("Jiong>>","拉起App:"+inPackage);
                return 0;
            }

        }else{
            Log.i("Jiong>>","getRootInActiveWindow() == null  怀疑 无障碍服务 失效！");
            //可以 考虑重新 拉起service
            return -1;
        }
    }

    //回收List<AccessibilityNodeInfo>     //返回 该list 个数大小
    public int recycleNodeInfoList(List<AccessibilityNodeInfo> list){
        int size = 0;
        if(list != null){
            size = list.size();
            for(int i=0;i<list.size();i++){
                list.get(i).recycle();
            }
            list.clear();
        }
        return size;
    }
    //回收AccessibilityNodeInfo   //nodeInfo!=null 返回true
    public boolean recycleNodeInfo(AccessibilityNodeInfo nodeInfo){
        if(nodeInfo == null){
            return false;
        }else{
            nodeInfo.recycle();
            return true;
        }
    }
    //获得 当前视图
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public List<AccessibilityNodeInfo> getCurrentWindow_list(String nodeId){
        AccessibilityNodeInfo NodeInfo = mAccessibilityService.getRootInActiveWindow(); //获取整个窗口视图对象
        if (NodeInfo != null) {
            List<AccessibilityNodeInfo> listView = NodeInfo.findAccessibilityNodeInfosByViewId(nodeId);
            NodeInfo.recycle();
            if(listView==null|| listView.size()==0){
              //  MyBroadcastReceiver.sendBroad_Error_HAL("无法根据Id获得视图！");
            }else{
                return listView;
            }
        }else{
            Log.i("Jiong>>","getCurrentWindow() 无法获得视图!");
            MyBroadcastReceiver.sendBroad_Error_HAL("无法获得视图！");
        }
        return null;
    }
    //获得 当前视图
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo getCurrentWindowNodeInfo(String nodeId){
        AccessibilityNodeInfo Node = null;
        List<AccessibilityNodeInfo> listView;
        AccessibilityNodeInfo NodeInfo = mAccessibilityService.getRootInActiveWindow(); //获取整个窗口视图对象
        if (NodeInfo != null) {
            listView = NodeInfo.findAccessibilityNodeInfosByViewId(nodeId);
            NodeInfo.recycle();
            if(listView==null|| listView.size()==0){
               // Log.i("Jiong>>","getCurrentWindow() 无法根据Id获得视图!");
               // MyBroadcastReceiver.sendBroad_Error_HAL("无法根据Id获得视图！");
            }else{
                Node = listView.get(0);
            }
        }else{
            Log.i("Jiong>>","getCurrentWindow() 无法获得视图!");
            MyBroadcastReceiver.sendBroad_Error_HAL("无法获得视图！");
        }
        return Node;
    }
    //获得视图 列表
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public List<AccessibilityNodeInfo> getCurrentWindow_NodeInfoList_byText(String text){
        List<AccessibilityNodeInfo> listView = null;
        AccessibilityNodeInfo NodeInfo = mAccessibilityService.getRootInActiveWindow(); //获取整个窗口视图对象
        if(NodeInfo!=null){
            listView = NodeInfo.findAccessibilityNodeInfosByText(text);
            NodeInfo.recycle();
            if(listView==null|| listView.size()==0){
        //        Log.i("Jiong>>","getCurrentWindow() 无法根据Id获得视图!");
        //        MyBroadcastReceiver.sendBroad_Error_HAL("无法根据Id获得视图！");
            }
        }else{
            Log.i("Jiong>>","getCurrentWindow() 无法获得视图!");
            MyBroadcastReceiver.sendBroad_Error_HAL("无法获得视图！");
        }
        return listView;
    }
    //获得视图
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo getCurrentWindow_NodeInfo_byText(String text){
        AccessibilityNodeInfo Node = null;
        List<AccessibilityNodeInfo> listView = null;
        AccessibilityNodeInfo NodeInfo = mAccessibilityService.getRootInActiveWindow(); //获取整个窗口视图对象
        if(NodeInfo!=null){
            listView = NodeInfo.findAccessibilityNodeInfosByText(text);
            NodeInfo.recycle();
            if(listView==null|| listView.size()==0){
             //   Log.i("Jiong>>","getCurrentWindow() 无法根据Id获得视图!");
             //   MyBroadcastReceiver.sendBroad_Error_HAL("无法根据Id获得视图！");
            }else{
                    Node = listView.get(0);
            }
        }else{
            Log.i("Jiong>>","getCurrentWindow() 无法获得视图!");
            MyBroadcastReceiver.sendBroad_Error_HAL("无法获得视图！");
        }
        return Node;
    }
    public AccessibilityNodeInfo getAccessibilityNodeInfo(AccessibilityNodeInfo NodeInfo, String nodeId){
        if(NodeInfo==null) return null;
        AccessibilityNodeInfo Node = null;
        List<AccessibilityNodeInfo> listView = NodeInfo.findAccessibilityNodeInfosByViewId(nodeId);
        if(NodeInfo!=null){
            if(listView==null|| listView.size()==0){
                //   Log.i("Jiong>>","getCurrentWindow() 无法根据Id获得视图!");
                //   MyBroadcastReceiver.sendBroad_Error_HAL("无法根据Id获得视图！");
            }else{
                    Node = listView.get(0);
            }
        }
        return Node;
    }
    //获得子视图
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo getItemAccessibilityNodeInfo(String nodeId, int index){
        AccessibilityNodeInfo NodeInfo = getCurrentWindowNodeInfo(nodeId);
        if(NodeInfo!=null && NodeInfo.getChildCount()>index){
            return NodeInfo.getChild(index);
        }
        return null;
    }


    //判断 当前页面下  该控件是否存在
    public boolean IsHasCurrentWindowNodeInfo(String nodeId){
        AccessibilityNodeInfo NodeInfo = getCurrentWindowNodeInfo(nodeId);
        return recycleNodeInfo(NodeInfo); //返回该控件是否存在 并释放内存对象
    }
    //判断 当前页面下  该控件是否存在并 可见
    public boolean IsHasAndVisibleCurrentWindowNodeInfo(String nodeId){
        boolean canVisible = false;
        AccessibilityNodeInfo NodeInfo = getCurrentWindowNodeInfo(nodeId);
        if(NodeInfo!=null){
            canVisible = NodeInfo.isVisibleToUser();
            recycleNodeInfo(NodeInfo); //释放内存对象
        }
        return canVisible; //返回该控件是否存在并 可见
    }

    //触发点击 控件
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void clickNodeInfo(AccessibilityNodeInfo NodeInfo){
        if(NodeInfo==null) return;
        NodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }
    //触发点击 控件
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean clickNodeInfo(String nodeId){
        AccessibilityNodeInfo NodeInfo = getCurrentWindowNodeInfo(nodeId);
        clickNodeInfo(NodeInfo);
        return recycleNodeInfo(NodeInfo); //返回该控件是否存在 并释放内存对象
    }

    //触发点击 某个子view
    public boolean clickNodeInfoChild(String nodeId, int index){
        AccessibilityNodeInfo NodeInfo = getItemAccessibilityNodeInfo(nodeId,index);
        clickNodeInfo(NodeInfo);
        return recycleNodeInfo(NodeInfo); //返回该控件是否存在 并释放内存对象
    }

    //触发点击 父控件
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void clickNodeInfoParent(AccessibilityNodeInfo NodeInfo){
        if(NodeInfo==null) return;
        NodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }
    //触发点击 父控件
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean clickNodeInfoParent(String nodeId){
        AccessibilityNodeInfo NodeInfo = getCurrentWindowNodeInfo(nodeId);
        clickNodeInfoParent(NodeInfo);
        return recycleNodeInfo(NodeInfo); //返回该控件是否存在 并释放内存对象
    }

    //获得 视图内容Text
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getTextNodeInfo(String nodeId){
        String strContent = "";
        AccessibilityNodeInfo NodeInfo = getCurrentWindowNodeInfo(nodeId);
        if(NodeInfo!=null){
            CharSequence str = NodeInfo.getText();
            strContent = str.toString();
            recycleNodeInfo(NodeInfo);
        }
        return strContent;
    }

    //设置 视图EditView 内容
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public  boolean setEditTextNodeInfo(String nodeId, String text){
        AccessibilityNodeInfo NodeInfo = getCurrentWindowNodeInfo(nodeId);
        if(NodeInfo!=null){
            setClipboard(NodeInfo,text);
            return recycleNodeInfo(NodeInfo);
        }
        return false;
    }

    //点击 全局返回 按键
    public void goGlobalBack(){
        mAccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }
}
