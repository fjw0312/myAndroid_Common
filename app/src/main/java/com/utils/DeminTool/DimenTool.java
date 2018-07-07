package com.utils.DeminTool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

/**
 * Created by jiongfang on 2018/6/27.
 * 快速生成适配工具类
 */
public class DimenTool {
    private final static float[] DIMENS = {2.00f, 1.50f, 1.80f, 2.25f};
    private final static String SW[] = new String[]{"sw320","sw360","sw500","sw720"};

    public static void gen() {
        //以此文件夹下的dimens.xml文件内容为初始值参照
        File file = new File("./app/src/main/res/values/dimens.xml");

        BufferedReader reader = null;
        StringBuilder[] swStringBuiler = new StringBuilder[SW.length];
        for(int i=0; i<swStringBuiler.length; i++){
            swStringBuiler[i] = new StringBuilder();
        }

        String[] swFile = new String[swStringBuiler.length];
        for(int i=0; i<swStringBuiler.length; i++){
            swFile[i] = "./app/src/main/res/values-"+SW[i]+"dp/dimens.xml";
        }

        try {
            System.out.println("生成不同分辨率：");
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            int line = 1;

            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                System.out.println("转换Line=："+tempString);
                if (tempString.contains("</dimen>")) {
                    //tempString = tempString.replaceAll(" ", "");
                    String start = tempString.substring(0, tempString.indexOf(">") + 1);
                    String end = tempString.substring(tempString.lastIndexOf("<") - 2);
                    //截取<dimen></dimen>标签内的内容，从>右括号开始，到左括号减2，取得配置的数字
                    Double num = Double.parseDouble
                            (tempString.substring(tempString.indexOf(">") + 1,
                                    tempString.indexOf("</dimen>") - 2));

                    //根据不同的尺寸，计算新的值，拼接新的字符串，并且结尾处换行。
                    for(int i=0; i<swStringBuiler.length; i++){
                        DecimalFormat df = new DecimalFormat(".00");  //小数点处理  保留2位
                        String strNum = df.format(num * DIMENS[i]);
                       // strNum = String.format("%.2f",num * DIMENS[i]);
                        swStringBuiler[i].append(start).append( strNum ).append(end).append("\r\n");
                    }

                } else {
                    if( swStringBuiler != null ){
                        for(int i=0; i<swStringBuiler.length; i++){
                            if(swStringBuiler[i] == null){
                                System.out.println(" swStringBuiler[i]=null："+swStringBuiler[i]);
                            }
                            swStringBuiler[i].append(tempString).append("").append("\r\n");;
                        }
                    }
                }
                line++;

            }

            reader.close();
            for(int i=0; i<swStringBuiler.length; i++){
                System.out.println("<!--  "+SW[i]+" -->");
                System.out.println(swStringBuiler[i]);
            }


            //将新的内容，写入到指定的文件中去
            for(int i=0; i<swStringBuiler.length; i++){
                writeFile(swFile[i], swStringBuiler[i].toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();

                }
            }
        }
    }


    /**
     * 写入方法
     *
     */
    public static void writeFile(String file, String text) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.println(text);
            out.close();
        } catch (IOException e) {
            if(out != null){
                out.close();
            }
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        gen();
    }
}

/**
 * 使用方式
 * 1.先在数组中 写入 文件名称 及比例dimens
 * 2.项目中创建values-sw**dp 文件夹
 * 3.项目的所有配置结束后，在DimenTool.java类中，右键Run ->DimenTool.main执行这段代码
 * */
