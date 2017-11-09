package me.iswear.springexceltool;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class PoiExcelWorkbook extends AbstractExcelWorkbook {

    private Workbook workbook;

    public PoiExcelWorkbook(OutputStream os) {
        super(os);
        this.workbook = new HSSFWorkbook();
    }

    @Override
    public AbstractExcelWorkbook addExcelSheet(String title, AbstractExcelSheetDataProvider dataProvider) throws IllegalAccessException {
        Type tempType = dataProvider.getClass().getGenericSuperclass();
        if (tempType instanceof ParameterizedType) {
            Class<?> dataType = (Class<?>) ((ParameterizedType) tempType).getActualTypeArguments()[0];
            SheetInfo sheetInfo = this.getSheetInfoFromExcelEntity(dataType);
            List<ColumnInfo> columnInfos = this.getColumnInfosFromExcelEntity(dataType);

            // sheet开始
            Sheet sheet = this.workbook.createSheet(title);
            // sheet标题
            Row headerRow = sheet.createRow(0);
            for (ColumnInfo columnInfo : columnInfos) {
                Cell cell = headerRow.createCell(columnInfo.getExcelField().column(), CellType.STRING);
                cell.setCellValue(columnInfo.getExcelField().title());
            }
            // sheet数据
            int row = 0;
            while (true) {
                ExcelSheetDataProviderStore store = dataProvider.providerData();

                List datas = store.getDatas();
                if (datas != null && !datas.isEmpty()) {
                    for (Object data : datas) {
                        Row dataRow = sheet.createRow(++row);
                        for (ColumnInfo columnInfo : columnInfos) {
                            Cell dataCell = dataRow.createCell(columnInfo.getExcelField().column(), CellType.STRING);
                            if (data == null) {
                                dataCell.setCellValue("");
                            } else {
                                Object fieldValue = columnInfo.getField().get(data);
                                if (fieldValue != null) {
                                    if (Date.class.isAssignableFrom(columnInfo.getField().getType())) {
                                        dataCell.setCellValue(StringEscapeUtils.escapeHtml4(DateFormatUtils.format((Date)fieldValue, sheetInfo.getExcelEntity().dataFormatter())));
                                    } else {
                                        dataCell.setCellValue(StringEscapeUtils.escapeHtml4(String.valueOf(fieldValue)));
                                    }
                                } else {
                                    dataCell.setCellValue("");
                                }
                            }
                        }
                    }
                }

                if (store.isFinished()) {
                    break;
                }
            }

        } else {
            throw new RuntimeException("无法获取ExcelSheetDataProvider类的泛型参数类型");
        }
        return this;
    }

    @Override
    public AbstractExcelWorkbook flush(boolean closeOutputStream) throws IOException {
        try {
            this.workbook.write(this.getOs());
        } finally {
            if (closeOutputStream) {
                this.getOs().close();
            }
        }
        return this;
    }

}
