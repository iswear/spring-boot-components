package me.iswear.springexceltool.excel;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

public class POIExcelWorkbook extends AbstractExcelWorkbook {

    private Workbook workbook;


    public POIExcelWorkbook(OutputStream os) {
        super(os);
        this.workbook = new HSSFWorkbook();
    }

    @Override
    public AbstractExcelWorkbook addExcelSheet(String title, AbstractExcelSheetDataProvider dataProvider) {
        Sheet sheet = this.workbook.createSheet(title);
        AbstractExcelSheetDataProvider.SheetInfo sheetInfo = dataProvider.getSheetInfo();
        List<AbstractExcelSheetDataProvider.ColumnInfo> columnInfos = dataProvider.getColumnInfos();

        // 装配头部
        Row headerRow = sheet.createRow(0);
        for (AbstractExcelSheetDataProvider.ColumnInfo columnInfo : columnInfos) {
            Cell cell = headerRow.createCell(columnInfo.getExcelField().column(), CellType.STRING);
            cell.setCellValue(columnInfo.getExcelField().title());
        }

        // 装配数据
        int row = 1;
        do {
            List<Object> datas = dataProvider.providerData();
            if (CollectionUtils.isNotEmpty(datas)) {
                for (Object data : datas) {
                    Row dataRow = sheet.createRow(row++);
                    for (AbstractExcelSheetDataProvider.ColumnInfo columnInfo : columnInfos) {
                        Cell dataCell = dataRow.createCell(columnInfo.getExcelField().column(), CellType.STRING);
                        try {
                            if (data == null) {
                                dataCell.setCellValue("");
                            } else {
                                Object fieldValue = columnInfo.getField().get(data);
                                if (fieldValue != null) {
                                    if (Date.class.isAssignableFrom(columnInfo.getField().getType())) {
                                        dataCell.setCellValue(DateFormatUtils.format((Date)fieldValue, sheetInfo.getExcelEntity().dataFormatter()));
                                    } else {
                                        dataCell.setCellValue(String.valueOf(fieldValue));
                                    }
                                } else {
                                    dataCell.setCellValue("");
                                }
                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } while (!dataProvider.isFinished());
        return this;
    }

    @Override
    public AbstractExcelWorkbook flush(boolean closeOutputStream) throws IOException {
        this.workbook.write(this.getOs());
        if (closeOutputStream) {
            this.getOs().close();
        }
        return this;
    }

}
