package com.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;
import me.zhouzhuo.zzexcelcreator.ZzExcelCreator;
import me.zhouzhuo.zzexcelcreator.ZzFormatCreator;

/**
 * Created by jiongfang on 2018/10/9.
 * 1.需要添加开源依赖包：    compile 'me.zhouzhuo.zzexcelcreator:zz-excel-creator:1.0.0'
 */
public class ExcelUtils {


    public String mPath = "";  //文件目录
    public String mFileName = "";  //文件名称
    public String firstSheetName = "";  //首个sheet名称
    public int fontSize = 14;  //字体大小
    public Colour fontColor = Colour.BLACK;
    public Colour backgroudColor = Colour.DEFAULT_BACKGROUND1;
    public int cellColumnWidth = 25;
    public int cellRowHeight = 400;


    //创建excels 文件
    public void createExcel(String path, String fileName, String sheetName) {
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(sheetName)) return;
        mPath = path;
        mFileName = fileName;
        firstSheetName = sheetName;
        try {
            ZzExcelCreator
                    .getInstance()
                    .createExcel(path, fileName)
                    .createSheet(sheetName)
                    .close();
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增Sheet
     */
    public void addSheet(String sheetName) {
        addSheet(mPath, mFileName, sheetName);
    }

    public void addSheet(String path, String fileName, String sheetName) {
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(sheetName)) return;
        try {
            ZzExcelCreator
                    .getInstance()
                    .openExcel(new File(path + fileName + ".xls"))  //如果不想覆盖文件，注意是openExcel
                    .createSheet(sheetName)
                    .close();

        } catch (IOException | WriteException e) {
            e.printStackTrace();

        } catch (BiffException e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置 行格式
     */
    public WritableCellFormat setCellFormat() {
        WritableCellFormat format = null;
        try {
            //设置单元格内容格式
            format = ZzFormatCreator
                    .getInstance()
                    .createCellFont(WritableFont.ARIAL)  //设置字体
                    .setAlignment(Alignment.CENTRE, VerticalAlignment.CENTRE)  //设置对齐方式(水平和垂直)
                    .setFontSize(fontSize)                    //设置字体大小
                    .setFontColor(fontColor)          //设置字体颜色
                    .setBackgroundColor(backgroudColor)
                    .getCellFormat();
        } catch (WriteException e) {
            e.printStackTrace();

        }
        return format;
    }

    /**
     * 写入cell
     */
    public void setCellContent(int col, int row, String content) {
        setCellContent(mPath, mFileName, col, row, content, setCellFormat());
    }

    public void setCellContent(String path, String fileName, int col, int row, String content, WritableCellFormat format) {
        try {
            ZzExcelCreator
                    .getInstance()
                    .openExcel(new File(path + fileName + ".xls"))
                    .openSheet(0)   //打开第1个sheet
                    .setColumnWidth(col, cellColumnWidth)
                    .setRowHeight(row, cellRowHeight)
                    .fillContent(col, row, content, format)
                    .close();
        } catch (IOException | WriteException | BiffException e) {
            e.printStackTrace();
        }
    }

    /**
     * 合并表格
     * co1l 起始行号
     * row1 起始列号
     * co12 结束行号
     * row2 结束列号
     */
    public void setMergeCell(int co1l, int row1, int co12, int row2) {
        setMergeCell(mPath, mFileName, co1l, row1, co12, row2);
    }

    public void setMergeCell(String path, String fileName, int co1l, int row1, int co12, int row2) {
        try {
            ZzExcelCreator
                    .getInstance()
                    .openExcel(new File(path + fileName + ".xls"))
                    .openSheet(0) //打开第1个sheet
                    .merge(co1l, row1, co12, row2) //合并
                    .close();
        } catch (IOException | WriteException | BiffException e) {
            e.printStackTrace();

        }
    }

    /**
     * 填充写入首行标题
     */
    public void setFirstCellTitle(String[] titles, Colour titleBackgroudColor, Colour titleDefaultColor) {
        if (titles == null) return;
        backgroudColor = titleBackgroudColor;
        for (int i = 0; i < titles.length; i++) {
            setCellContent(i, 0, titles[i]);
        }

        backgroudColor = titleDefaultColor;
    }

    /**
     * 填充写入每一行 内容
     */
    public void setLineContent(String[] content, int col) {
        if (content == null) return;
        for (int i = 0; i < content.length; i++) {
            setCellContent(i, col, content[i]);
        }
    }


    /**
     * 使用方式：
     * 1. //创建对象  new ExcelUtils();
     ExcelUtils excelUtils = new ExcelUtils();
     2.  创建xls文件并 sheet表  createExcel
     excelUtils.createExcel(Environment.getExternalStorageDirectory().getAbsolutePath() + "/fjw/", "XXOO", "NetSearch");
     3.  设置表头 首行setFirstCellTitle
     excelUtils.setFirstCellTitle(titles, Colour.BLUE_GREY, Colour.DEFAULT_BACKGROUND1);
     4.  写入表格每一行内容 setLineContent
     excelUtils.setLineContent(content, i + 1);
     * */

}
