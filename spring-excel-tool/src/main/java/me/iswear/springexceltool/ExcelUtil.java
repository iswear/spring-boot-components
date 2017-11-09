package me.iswear.springexceltool;

import java.io.OutputStream;

/**
 * Created by iswear on 2017/10/29.
 */
public class ExcelUtil {

    public static PoiExcelWorkbook createPOIExcelWorkbook(OutputStream os) {
        return new PoiExcelWorkbook(os);
    }

    public static XmlExcelWorkbook createXMLExcelWorkbook(OutputStream os) {
        return new XmlExcelWorkbook(os);
    }

    public static CsvExcelWorkbook createCSVExcelWorkbook(OutputStream os) {
        return new CsvExcelWorkbook(os);
    }

}
