package me.iswear.springexceltool.excel;

import lombok.Getter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by iswear on 2017/10/28.
 */
public abstract class AbstractExcelWorkbook {

    @Getter
    private OutputStream os;

    public AbstractExcelWorkbook(OutputStream os) {
        this.os = os;
    }

    public abstract AbstractExcelWorkbook addExcelSheet(String title, AbstractExcelSheetDataProvider dataProvider);

    public abstract AbstractExcelWorkbook flush(boolean closeOutputStream) throws IOException;

}
