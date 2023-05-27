package pers.qh.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShopCache {
    //定义一个属性value
    String value() default "cache";
    //是否开启布隆过滤器
    boolean enableBloom() default true;
}
