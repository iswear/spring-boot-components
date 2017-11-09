package me.iswear.springexceltool;

/**
 * Created by iswear on 2017/10/28.
 */
public abstract class AbstractExcelSheetDataProvider<T> {

    public abstract ExcelSheetDataProviderStore<T> providerData();

}
