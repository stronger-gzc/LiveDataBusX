package com.gzc.livedatabus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: Administrator
 * Date: 2021-01-24 19:33
 * Describe:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Observe {
    ThreadMode threadMode() default ThreadMode.MAIN;

    boolean sticky() default false;

    boolean append() default false;

    String key() default "";
}
