package me.iswear.springexceltool.annotation;

import java.lang.annotation.*;

/**
 * Created by iswear on 2017/10/28.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelEntity {

    String dataFormatter() default "yyyy-MM-dd HH:mm:ss";

}
