package me.iswear.springexceltool;

import java.util.List;

public class ExcelSheetDataProviderStore<T> {

    private final boolean finished;

    private final List<T> datas;

    private ExcelSheetDataProviderStore(boolean finished, List<T> datas) {
        this.finished = finished;
        this.datas = datas;
    }

    public boolean isFinished() {
        return finished;
    }

    public List<T> getDatas() {
        return datas;
    }

    public static <T> ExcelSheetDataProviderStore<T> createContinueStore(List<T> datas) {
        return new ExcelSheetDataProviderStore<>(false, datas);
    }

    public static <T> ExcelSheetDataProviderStore<T> createFinishedStore(List<T> datas) {
        return new ExcelSheetDataProviderStore<>(true, datas);
    }

}
