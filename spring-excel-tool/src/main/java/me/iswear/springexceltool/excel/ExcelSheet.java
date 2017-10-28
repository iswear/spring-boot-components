package me.iswear.springexceltool.excel;

import lombok.Data;

@Data
public class ExcelSheet<T> {

    private Class<T> entityType;

    private RowsRequestHandler rowsRequestHandler;

    public ExcelSheet(Class<T> entityType, RowsRequestHandler rowsRequestHandler) {
        this.entityType = entityType;
        this.rowsRequestHandler = rowsRequestHandler;
    }

    interface RowsRequestHandler {
        boolean requestRows();
    }

}
