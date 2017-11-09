package me.iswear.springexceltool;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

//@Slf4j
public class XmlExcelWorkbook extends AbstractExcelWorkbook {

    private static Configuration configuration;

    private OutputStreamWriter writer;

    static {
        configuration = new Configuration(Configuration.VERSION_2_3_0);
        configuration.setDefaultEncoding("utf-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setTemplateLoader(new ClassTemplateLoader(XmlExcelWorkbook.class.getClassLoader(), "templates/ExcelTpl"));
    }


    public XmlExcelWorkbook(OutputStream os) {
        super(os);
        try {
            this.writer = new OutputStreamWriter(this.getOs());
            Template template = configuration.getTemplate("ExcelHeader.ftl");
            template.process(null, this.writer);
        } catch (IOException e) {
            throw new RuntimeException("IO异常", e);
        } catch (TemplateException e) {
            throw new RuntimeException("模板异常", e);
        }
    }

    @Override
    public AbstractExcelWorkbook addExcelSheet(String title, AbstractExcelSheetDataProvider dataProvider) throws IOException, TemplateException, IllegalAccessException {
        Type tempType = dataProvider.getClass().getGenericSuperclass();
        if (tempType instanceof ParameterizedType) {
            Class<?> dataType = (Class<?>) ((ParameterizedType) tempType).getActualTypeArguments()[0];
            SheetInfo sheetInfo = this.getSheetInfoFromExcelEntity(dataType);
            List<ColumnInfo> columnInfos = this.getColumnInfosFromExcelEntity(dataType);
            if (columnInfos != null && columnInfos.size() > 0) {
                int maxRow = columnInfos.get(columnInfos.size() - 1).getExcelField().column();
                Map<Integer, ColumnInfo> columnInfoMap = new HashMap<>();

                for (ColumnInfo columnInfo : columnInfos) {
                    columnInfoMap.put(columnInfo.getExcelField().column(), columnInfo);
                }

                Template sheetTemplate = configuration.getTemplate("ExcelSheetHeader.ftl");
                Map<String, Object> params = new HashMap<>();
                params.put("title", title);
                sheetTemplate.process(params, this.writer);

                Template rowTemplate = configuration.getTemplate("ExcelRowHeader.ftl");
                List<String> rowTitles = new LinkedList<>();
                for (int i = 0; i <= maxRow; ++i) {
                    ColumnInfo columnInfo = columnInfoMap.get(i);
                    if (columnInfo != null) {
                        rowTitles.add(StringEscapeUtils.escapeHtml4(columnInfo.getExcelField().title()));
                    } else {
                        rowTitles.add("");
                    }
                }

                Map<String, Object> rowHeaderModel = new HashMap<>();
                rowHeaderModel.put("titles", rowTitles);
                rowTemplate.process(rowHeaderModel, this.writer);

                Template rowDataTemplate = configuration.getTemplate("ExcelRowData.ftl");
                while (true) {
                    ExcelSheetDataProviderStore store = dataProvider.providerData();
                    List datas = store.getDatas();
                    if (datas != null && !datas.isEmpty()) {
                        List<Map<String, Object>> rows = new LinkedList<>();
                        for (Object data : datas) {
                            List<String> cells = new LinkedList<>();
                            for (int i = 0; i <= maxRow; ++i) {
                                ColumnInfo columnInfo = columnInfoMap.get(i);
                                if (columnInfo != null && data != null) {
                                    Object fieldValue = columnInfo.getField().get(data);
                                    if (fieldValue != null) {
                                        if (Date.class.isAssignableFrom(columnInfo.getField().getType())) {
                                            cells.add(StringEscapeUtils.escapeHtml4(DateFormatUtils.format((Date)fieldValue, sheetInfo.getExcelEntity().dataFormatter())));
                                        } else {
                                            cells.add(StringEscapeUtils.escapeHtml4(String.valueOf(fieldValue)));
                                        }
                                    } else {
                                        cells.add("");
                                    }
                                } else {
                                    cells.add("");
                                }
                            }
                            Map<String, Object> row = new HashMap<>();
                            row.put("cells", cells);
                            rows.add(row);
                        }

                        Map<String, Object> rowDataModel = new HashMap<>();
                        rowDataModel.put("rows", rows);
                        rowDataTemplate.process(rowDataModel, this.writer);
                    }
                    if (store.isFinished()) {
                        break;
                    }
                }
            }
            Template sheetFooterTemplate = configuration.getTemplate("ExcelSheetFooter.ftl");
            sheetFooterTemplate.process(null, this.writer);
        } else {
            throw new RuntimeException("无法获取ExcelSheetDataProvider类的泛型参数类型");
        }
        return this;
    }

    @Override
    public AbstractExcelWorkbook flush(boolean closeOutputStream) throws IOException, TemplateException {
        try {
            Template excelFooter = configuration.getTemplate("ExcelFooter.ftl");
            excelFooter.process(null, this.writer);
            this.writer.flush();
        } finally {
            if (closeOutputStream) {
                this.writer.close();
            }
        }
        return this;
    }

}
