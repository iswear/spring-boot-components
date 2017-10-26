package me.iswear.springexceltool.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelField {

    String title() default "";

    int column() default 0;

    int startRow() default 0;

    String headerForeColor() default "";

    String headerBackgroudColor() default "";

    String textForeColor() default "";

    String textBackgroundColor() default "";

}
