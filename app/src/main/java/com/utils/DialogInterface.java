package com.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

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
    interface InquiryDialgInterfacce{
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
    interface ItemDialogInerface{
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
    interface SingleChoiceDialogInerface{
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
    interface MultiChoiceDialogInerface{
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
    interface InputDialogInterface{
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

}
