package com.gzc.livedatabus;

import androidx.lifecycle.LifecycleOwner;

import java.lang.reflect.Method;

/**
 * User: Administrator
 * Date: 2021-01-24 18:09
 * Describe:
 */
public class Subscription {
    final LifecycleOwner subscriber;
    final Method method;

    Subscription(LifecycleOwner subscriber, Method method) {
        this.subscriber = subscriber;
        this.method = method;
    }
}
