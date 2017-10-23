package me.iswear.springcanalclient.annotation;

import me.iswear.springcanalclient.CanalConsumerScanProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(CanalConsumerScanProcessor.class)
public @interface EnableCanalListener {

}
