package me.iswear.springexceltool;

import freemarker.template.TemplateException;
import lombok.Getter;
import me.iswear.springexceltool.annotation.ExcelEntity;
import me.iswear.springexceltool.annotation.ExcelField;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by iswear on 2017/10/28.
 */
public abstract class AbstractExcelWorkbook {

    @Getter
    private OutputStream os;

    public AbstractExcelWorkbook(OutputStream os) {
        this.os = os;
    }

    public abstract AbstractExcelWorkbook addExcelSheet(String title, AbstractExcelSheetDataProvider dataProvider) throws IOException, TemplateException, IllegalAccessException;

    public abstract AbstractExcelWorkbook flush(boolean closeOutputStream) throws IOException, TemplateException;

    public SheetInfo getSheetInfoFromExcelEntity(Class<?> entity) {
        ExcelEntity excelEntity = entity.getAnnotation(ExcelEntity.class);
        if (excelEntity != null) {
           return new SheetInfo(excelEntity);
        } else {
            throw new RuntimeException(
                    String.format("类：%s缺少:%s注解",
                            entity.getClass().getCanonicalName(),
                            ExcelEntity.class.getClass().getCanonicalName()));
        }
    }

    public List<ColumnInfo> getColumnInfosFromExcelEntity(Class<?> entity) {
        List<ColumnInfo> columnInfos = new ArrayList<>();
        Class<?> type = entity;
        while (type != null) {
            Field[] fields = type.getDeclaredFields();
            if (fields != null) {
                for (Field field : fields) {
                    ExcelField excelField = field.getAnnotation(ExcelField.class);
                    if (excelField != null) {
                        field.setAccessible(true);
                        columnInfos.add(new ColumnInfo(field, excelField));
                    }
                }
            }
            type = type.getSuperclass();
        }
        Collections.sort(
                columnInfos,
                new Comparator<ColumnInfo>() {
                    @Override
                    public int compare(ColumnInfo o1, ColumnInfo o2) {
                        return o1.excelField.column() - o2.excelField.column();
                    }
                }
        );
        return columnInfos;
    }

    public static class SheetInfo {

        @Getter
        private final ExcelEntity excelEntity;

        public SheetInfo(ExcelEntity excelEntity) {
            this.excelEntity = excelEntity;
        }

    }

    public static class ColumnInfo {

        @Getter
        private final Field field;

        @Getter
        private final ExcelField excelField;

        public ColumnInfo(Field field, ExcelField excelField) {
            this.field = field;
            this.excelField = excelField;
        }

    }

}
