package me.iswear.springexceltool;

import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

//@Slf4j
public class CsvExcelWorkbook extends AbstractExcelWorkbook {

    private static final String SPLIT_CHAR = ",";

    private static final String NEWLINE_CHAR = "\n";

    public CsvExcelWorkbook(OutputStream os) {
        super(os);
        try {
            byte[] bom = {
                    (byte)0xEF, (byte)0xBB, (byte)0xBF
            };
            os.write(bom);
        } catch (IOException e) {

        }
    }

    @Override
    public AbstractExcelWorkbook addExcelSheet(String title, AbstractExcelSheetDataProvider dataProvider) throws IOException, TemplateException, IllegalAccessException {
        Type tempType = dataProvider.getClass().getGenericSuperclass();
        if (tempType instanceof ParameterizedType) {
            Class<?> dataType = (Class<?>) ((ParameterizedType)tempType).getActualTypeArguments()[0];
            SheetInfo sheetInfo = this.getSheetInfoFromExcelEntity(dataType);
            List<ColumnInfo> columnInfos = this.getColumnInfosFromExcelEntity(dataType);
            if (columnInfos != null && columnInfos.size() > 0) {
                int maxRow = columnInfos.get(columnInfos.size() - 1).getExcelField().column();
                Map<Integer, ColumnInfo> columnInfoMap = new HashMap<>();

                for (ColumnInfo columnInfo : columnInfos) {
                    columnInfoMap.put(columnInfo.getExcelField().column(), columnInfo);
                }

                // 标题
                for (int i = 0; i <= maxRow; ++i ) {
                    if (i > 0) {
                        this.getOs().write(SPLIT_CHAR.getBytes("utf-8"));
                    }
                    ColumnInfo columnInfo = columnInfoMap.get(i);
                    if (columnInfo != null) {
                        this.getOs().write(StringEscapeUtils.escapeCsv(columnInfo.getExcelField().title()).getBytes("utf-8"));
                    }
                }
                this.getOs().write(NEWLINE_CHAR.getBytes("utf-8"));
                // 数据
                while (true) {
                    ExcelSheetDataProviderStore store = dataProvider.providerData();
                    List datas = store.getDatas();
                    if (datas != null && !datas.isEmpty()) {
                        for (Object data : datas) {
                            for (int i = 0; i <= maxRow; ++i) {
                                if (i > 0) {
                                    this.getOs().write(SPLIT_CHAR.getBytes("utf-8"));
                                }
                                ColumnInfo columnInfo = columnInfoMap.get(i);
                                if (columnInfo != null && data != null) {
                                    Object fieldValue = columnInfo.getField().get(data);
                                    if (fieldValue != null) {
                                        if (Date.class.isAssignableFrom(columnInfo.getField().getType())) {
                                            this.getOs().write(StringEscapeUtils.escapeCsv(DateFormatUtils.format((Date) fieldValue, sheetInfo.getExcelEntity().dataFormatter())).getBytes("utf-8"));
                                        } else {
                                            this.getOs().write(StringEscapeUtils.escapeCsv(String.valueOf(fieldValue)).getBytes("utf-8"));
                                        }
                                    }
                                }
                            }
                            this.getOs().write(NEWLINE_CHAR.getBytes("utf-8"));
                        }
                    }
                    if (store.isFinished()) {
                        break;
                    }
                }
            }
        } else {
            throw new RuntimeException("无法获取ExcelSheetDataProvider类的泛型参数类型");
        }
        return this;
    }

    @Override
    public AbstractExcelWorkbook flush(boolean closeOutputStream) throws IOException {
        this.getOs().flush();
        if (closeOutputStream) {
            this.getOs().close();
        }
        return this;
    }

}
