package me.iswear.springexceltool;

import me.iswear.springexceltool.excel.POIExcelWorkbook;
import me.iswear.springexceltool.excel.XMLExcelWorkbook;

import java.io.OutputStream;

/**
 * Created by iswear on 2017/10/29.
 */
public class ExcelUtil {

    public static POIExcelWorkbook createPOIExcelWorkbook(OutputStream os) {
        return new POIExcelWorkbook(os);
    }

    public static XMLExcelWorkbook createXMLExcelWorkbook(OutputStream os) {
        return new XMLExcelWorkbook(os);
    }

}
