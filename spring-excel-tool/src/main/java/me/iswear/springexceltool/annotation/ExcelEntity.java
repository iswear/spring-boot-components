package me.iswear.springexceltool.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExcelEntity {

    String title() default "sheet";

}
