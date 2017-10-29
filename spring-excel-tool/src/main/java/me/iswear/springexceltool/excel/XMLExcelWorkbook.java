package me.iswear.springexceltool.excel;

import freemarker.template.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

public class XMLExcelWorkbook extends AbstractExcelWorkbook {

    private static Configuration configuration;

    private OutputStreamWriter writer;

    static {
        try {
            configuration = new Configuration(Configuration.VERSION_2_3_0);
            configuration.setDefaultEncoding("utf-8");
            configuration.setDirectoryForTemplateLoading(new File(XMLExcelWorkbook.class.getClassLoader().getResource("ExcelTpl").getPath()));
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        } catch (IOException e) {
            throw new RuntimeException("IO异常", e);
        }
    }


    public XMLExcelWorkbook(OutputStream os) {
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
    public AbstractExcelWorkbook addExcelSheet(String title, AbstractExcelSheetDataProvider dataProvider) {
        try {
            AbstractExcelSheetDataProvider.SheetInfo sheetInfo = dataProvider.getSheetInfo();
            List<AbstractExcelSheetDataProvider.ColumnInfo> columnInfos = dataProvider.getColumnInfos();
            if (CollectionUtils.isNotEmpty(columnInfos)) {
                int maxRow = columnInfos.get(columnInfos.size() - 1).getExcelField().column();
                Map<Integer, AbstractExcelSheetDataProvider.ColumnInfo> columnInfoMap = new HashMap<>();
                for (AbstractExcelSheetDataProvider.ColumnInfo columnInfo : columnInfos) {
                    columnInfoMap.put(columnInfo.getExcelField().column(), columnInfo);
                }

                Template sheetHeader = configuration.getTemplate("ExcelSheetHeader.ftl");
                Map<String, Object> params = new HashMap<>();
                params.put("title", title);
                sheetHeader.process(params, this.writer);

                Template rowHeader = configuration.getTemplate("ExcelRowHeader.ftl");
                List<String> rowHeaderTitles = new LinkedList<>();
                for (int i = 0; i <= maxRow; ++i) {
                    AbstractExcelSheetDataProvider.ColumnInfo columnInfo = columnInfoMap.get(i);
                    if (columnInfo != null) {
                        rowHeaderTitles.add(columnInfo.getExcelField().title());
                    } else {
                        rowHeaderTitles.add("");
                    }
                }
                Map<String, Object> rowHeaderModel = new HashMap<>();
                rowHeaderModel.put("titles", rowHeaderTitles);
                rowHeader.process(rowHeaderModel, this.writer);

                Template rowDataTpl = configuration.getTemplate("ExcelRowData.ftl");
                do {
                    List<Object> datas = dataProvider.providerData();
                    if (CollectionUtils.isNotEmpty(datas)) {
                        List<Map<String, Object>> rows = new LinkedList<>();
                        for (Object data : datas) {
                            List<String> cells = new LinkedList<>();
                            for (int i = 0; i <= maxRow; ++i) {
                                AbstractExcelSheetDataProvider.ColumnInfo columnInfo = columnInfoMap.get(i);
                                if (columnInfo != null && data != null) {
                                    Object fieldValue = columnInfo.getField().get(data);
                                    if (fieldValue != null) {
                                        if (Date.class.isAssignableFrom(columnInfo.getField().getType())) {
                                            cells.add(DateFormatUtils.format((Date)fieldValue, sheetInfo.getExcelEntity().dataFormatter()));
                                        } else {
                                            cells.add(String.valueOf(fieldValue));
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
                        rowDataTpl.process(rowDataModel, this.writer);
                    }
                } while (!dataProvider.isFinished());

                Template sheetFooter = configuration.getTemplate("ExcelSheetFooter.ftl");
                sheetFooter.process(null, this.writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("IO异常", e);
        } catch (TemplateException e) {
            throw new RuntimeException("模板异常", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("反射获取field失败", e);
        }
        return this;
    }

    @Override
    public AbstractExcelWorkbook flush(boolean closeOutputStream) {
        try {
            Template excelFooter = configuration.getTemplate("ExcelFooter.ftl");
            excelFooter.process(null, this.writer);
            this.writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("IO异常", e);
        } catch (TemplateException e) {
            throw new RuntimeException("模板异常", e);
        }
        return this;
    }

}
