package com.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycom.R;

/**
 * Created by Administrator on 2018/3/18.
 */

public class DialogInterface {
    public DialogInterface(){

    }
    //进度 对话框
    public void showProgressDialog(Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        //progressDialog.setTitle("This is ProgressDialog");
        progressDialog.setMessage("Loading...");
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); //进度条模式
        progressDialog.setCancelable(true); //是否可以通过back键取消掉对话框
        progressDialog.show();
    }
    //普通 提示 对话框
    public void showAlertDialog(Context context,String title,String msg){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .create().show();
    }

    //普通 询问确认对话框
    public interface InquiryDialgInterfacce{
        public void onYesClick();
        public void onNodClick();
    }
    InquiryDialgInterfacce inquiryDialgInterfacce;
    public void setInquiryDialgInterfacce(InquiryDialgInterfacce inquiryDialgInterfacce){
        this.inquiryDialgInterfacce = inquiryDialgInterfacce;
    }
    public void showInquiryDialog(Context context,String title,String msg){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        if(inquiryDialgInterfacce != null) inquiryDialgInterfacce.onYesClick();
                    }
                })
                .setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        if(inquiryDialgInterfacce != null) inquiryDialgInterfacce.onNodClick();
                    }
                })
                .create().show();
    }

    //子项选择 对话框
    public interface ItemDialogInerface{
        public void onItemClick(int which);
    }
    ItemDialogInerface itemDialogInerface;
    public void setItemDialogInerface(ItemDialogInerface itemDialogInerface){
        this.itemDialogInerface = itemDialogInerface;
    }
    public void showItemDialog(Context context,String title,String[] items){
        if(items == null || items.length ==0) return;
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(items, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        itemDialogInerface.onItemClick(which);
                    }
                }).create().show();
    }

    //单项选择 Dialog
    public interface SingleChoiceDialogInerface{
        public void onSingleChoiceClick(int which);
    }
    SingleChoiceDialogInerface singleChoiceDialogInerface;
    public void setSingleChoiceDialogInerface(SingleChoiceDialogInerface singleChoiceDialogInerface){
        this.singleChoiceDialogInerface = singleChoiceDialogInerface;
    }
    public void showSingleChoiceDialog(Context context,String title,String[] items){
        if(items == null || items.length ==0) return;
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setSingleChoiceItems(items, 0, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        singleChoiceDialogInerface.onSingleChoiceClick(which);
                    }
                }).create().show();
    }

    //多项选择 Dialog
    public interface MultiChoiceDialogInerface{
        public void onMultiChoiceClick(int which, boolean isChecked);
    }
    MultiChoiceDialogInerface multiChoiceDialogInerface;
    public void setMultiChoiceDialogInerface(MultiChoiceDialogInerface multiChoiceDialogInerface){
        this.multiChoiceDialogInerface = multiChoiceDialogInerface;
    }
    public void showMultiChoiceDialog(Context context,String title,String[] items){
        if(items == null || items.length ==0) return;
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMultiChoiceItems(items, null, new android.content.DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which, boolean isChecked) {
                        multiChoiceDialogInerface.onMultiChoiceClick(which,isChecked);
                    }
                }).create().show();
    }

    //输入 对话框
    public interface InputDialogInterface{
        public void onInputDialog();
    }
    InputDialogInterface inputDialogInterface;
    public void setInputDialogInterface(InputDialogInterface inputDialogInterface){
        this.inputDialogInterface = inputDialogInterface;
    }
    public void showInputDialog(Context context,String title){
        View view = View.inflate(context, R.layout.widgets_dialog_input,null);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        if(inputDialogInterface != null) inputDialogInterface.onInputDialog();
                    }
                })
                .setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    //多功能 组合  Dialog
    AlertDialog alert;
    EditText editText;
    public final static int NULL_BUTTON = 0;  //无按钮
    public final static int SINGLE_BUTTON = 1; //单按钮
    public final static int DOUBLE_BUTTON = 2; //双按钮
    public final static int THREE_BUTTON = 3;  //三按钮
    ComDialogInterface comDialogInterface;
    public interface ComDialogInterface{
        public void OnPositiveButton();
        public void OnNegativeButton();
        public void OnNeutralButton();
        public void OnInputDialog(String str);
    }
    public DialogInterface setComDialogInterface(ComDialogInterface comDialogInterface){
        this.comDialogInterface = comDialogInterface;
        return this;
    }
    public void createComDialog(Context context,String title,String msg,int buttonMode){
        View view = View.inflate(context, R.layout.dialog_base,null);
        TextView titleText = (TextView)view.findViewById(R.id.title);
        TextView contentText = (TextView)view.findViewById(R.id.content);
        editText = (EditText)view.findViewById(R.id.edit_input);
        LinearLayout cancel_ly = (LinearLayout)view.findViewById(R.id.cancel_ly);
        LinearLayout mid_ly = (LinearLayout)view.findViewById(R.id.mid_ly);
        LinearLayout subimt_ly = (LinearLayout)view.findViewById(R.id.subimt_ly);
        TextView cancel = (TextView)view.findViewById(R.id.cancel);
        TextView reset = (TextView)view.findViewById(R.id.reset);
        TextView submit = (TextView)view.findViewById(R.id.submit);
        if(TextUtils.isEmpty(title)){
            titleText.setVisibility(View.GONE);
        }else{
            titleText.setText(title);
        }
        if(TextUtils.isEmpty(msg)){
            contentText.setVisibility(View.GONE);
        }else{
            contentText.setText(msg);
            editText.setVisibility(View.GONE);
        }
        if(buttonMode==NULL_BUTTON){
            subimt_ly.setVisibility(View.GONE);
            cancel_ly.setVisibility(View.GONE);
            mid_ly.setVisibility(View.GONE);
        }else if(buttonMode==SINGLE_BUTTON){
            cancel_ly.setVisibility(View.GONE);
            mid_ly.setVisibility(View.GONE);
        }else if(buttonMode==THREE_BUTTON){
            //按钮都显示
        }else{
            mid_ly.setVisibility(View.GONE);
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comDialogInterface!=null){
                    if(editText.getVisibility()==View.GONE){  //显示对话框
                        comDialogInterface.OnPositiveButton();
                    }else{                                     //输入对话框
                        String str = editText.getText().toString();
                        comDialogInterface.OnInputDialog(str);
                    }
                }
                alert.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comDialogInterface!=null){
                    comDialogInterface.OnNegativeButton();
                }
                alert.dismiss();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comDialogInterface!=null){
                    comDialogInterface.OnNeutralButton();
                }
                alert.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        alert = builder.create();
        alert.show();
    }


}
