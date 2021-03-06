package me.iswear.springexceltool;

import me.iswear.springexceltool.annotation.ExcelEntity;
import me.iswear.springexceltool.annotation.ExcelField;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by iswear on 2017/10/29.
 */
public class ExcelUtilTest {

    @ExcelEntity
    public static class TestBean {

        @ExcelField(title = "用户名", column = 0)
        private String username;

        @ExcelField(title = "密码", column = 1)
        private String password;

        @ExcelField(title = "注册时间", column = 2)
        private Date regtime;

        public TestBean(String username, String password, Date regtime) {
            this.username = username;
            this.password = password;
            this.regtime = regtime;
        }
    }


    @Test
    public void createXMLExcelWorkbook() throws Exception {
        File file = new File("/Users/hujianbing/Desktop/yaya/11.xls");
        ExcelUtil.createXMLExcelWorkbook(new FileOutputStream(file))
                .addExcelSheet("sheet1", this.createTestDataProvider())
                .flush(true);
    }

    @Test
    public void createPOIExcelWorkbook() throws Exception {
        File file = new File("/Users/hujianbing/Desktop/yaya/22.xls");
        ExcelUtil.createPOIExcelWorkbook(new FileOutputStream(file))
                .addExcelSheet("sheet1", this.createTestDataProvider())
                .flush(true);
    }

    @Test
    public void createCSVExcelWorkbook() throws Exception {
        File file = new File("/Users/hujianbing/Desktop/yaya/33.csv");
        ExcelUtil.createCSVExcelWorkbook(new FileOutputStream(file))
                .addExcelSheet("sheet1", this.createTestDataProvider())
                .flush(true);
    }

    private AbstractExcelSheetDataProvider<TestBean> createTestDataProvider() {
        return new AbstractExcelSheetDataProvider<TestBean>() {

            private int i = 0;

            @Override
            public ExcelSheetDataProviderStore<TestBean> providerData() {
                if (i < 1000) {
                    List<TestBean> testBeans = new LinkedList<>();
                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
                    ++i;
                    return ExcelSheetDataProviderStore.createContinueStore(testBeans);
                } else {
                    return ExcelSheetDataProviderStore.createFinishedStore(null);
                }
            }
//            private int i = 0;
//
//            @Override
//            public List providerData() {
//                if (i < 10000) {
//                    List<TestBean> testBeans = new LinkedList<>();
//                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
//                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
//                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
//                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
//                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
//                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
//                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
//                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
//                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
//                    testBeans.add(new TestBean("用户1", "密码1", new Date()));
//                    ++i;
//                    return testBeans;
//                } else {
//                    this.setFinished(true);
//                    ++i;
//                    return null;
//                }
//            }
        };
    }



}