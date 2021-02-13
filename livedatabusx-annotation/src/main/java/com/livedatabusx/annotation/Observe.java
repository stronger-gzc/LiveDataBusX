package com.livedatabusx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author：gzc
 * date：2021/1/26
 * describe：
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Observe {
    ThreadMode threadMode() default ThreadMode.MAIN;

    boolean sticky() default false;

    boolean append() default false;

    String key();
}
