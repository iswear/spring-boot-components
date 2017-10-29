package me.iswear.springexceltool.excel;

import lombok.Getter;
import lombok.Setter;
import me.iswear.springexceltool.annotation.ExcelEntity;
import me.iswear.springexceltool.annotation.ExcelField;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by iswear on 2017/10/28.
 */
public abstract class AbstractExcelSheetDataProvider<T> {

    @Setter
    @Getter
    private boolean finished;

    @Getter
    private Class<T> dataType;

    @Getter
    private SheetInfo sheetInfo;

    @Getter
    private List<ColumnInfo> columnInfos;

    public AbstractExcelSheetDataProvider(Class<T> dataType) {
        this.dataType = dataType;
        this.columnInfos = new LinkedList<>();
        Class type = dataType;
        while (type != null) {
            try {
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
            } finally {
                type = type.getSuperclass();
            }
        }
        columnInfos.sort(new Comparator<ColumnInfo>() {
            @Override
            public int compare(ColumnInfo o1, ColumnInfo o2) {
                return o1.excelField.column() - o2.excelField.column();
            }
        });
        ExcelEntity excelEntity = dataType.getAnnotation(ExcelEntity.class);
        if (excelEntity == null) {
            throw new RuntimeException(String.format("类：%s缺少:%s注解", dataType.getClass().getCanonicalName(), ExcelEntity.class.getClass().getCanonicalName()));
        } else {
            this.sheetInfo = new SheetInfo(excelEntity);
        }
    }

    public abstract List<T> providerData();


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
