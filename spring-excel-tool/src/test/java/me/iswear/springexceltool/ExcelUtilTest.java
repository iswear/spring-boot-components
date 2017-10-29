package me.iswear.springexceltool;

import me.iswear.springexceltool.annotation.ExcelEntity;
import me.iswear.springexceltool.annotation.ExcelField;
import me.iswear.springexceltool.excel.AbstractExcelSheetDataProvider;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

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
        File file = new File("/Users/iswear/Desktop/logs/aaa.xls");
        ExcelUtil.createXMLExcelWorkbook(new FileOutputStream(file))
                .addExcelSheet("sheet1", this.createTestDataProvider())
                .flush(true);
    }

    @Test
    public void createPOIExcelWorkbook() throws Exception {
        File file = new File("/Users/iswear/Desktop/logs/bbb.xls");
        ExcelUtil.createPOIExcelWorkbook(new FileOutputStream(file))
                .addExcelSheet("sheet1", this.createTestDataProvider())
                .flush(true);
    }

    private AbstractExcelSheetDataProvider<TestBean> createTestDataProvider() {
        return new AbstractExcelSheetDataProvider<TestBean>(TestBean.class) {
            private int i = 0;

            @Override
            public List providerData() {
                if (i < 10000) {
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
                    return testBeans;
                } else {
                    this.setFinished(true);
                    ++i;
                    return null;
                }
            }
        };
    }



}