package me.iswear.springcanalclient.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CanalListener {

    String[] connectorNames() default {};

}
