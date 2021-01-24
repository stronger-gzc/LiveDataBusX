package com.gzc.livedatabus;

import androidx.lifecycle.LifecycleOwner;

/**
 * User: Administrator
 * Date: 2021-01-24 18:09
 * Describe:
 */
public class Subscription {
    final LifecycleOwner subscriber;
    final SubscriberMethod subscriberMethod;
    final String key;
    volatile boolean active;

    Subscription(LifecycleOwner subscriber, SubscriberMethod subscriberMethod) {
        this(subscriber, null, subscriberMethod);
    }

    Subscription(LifecycleOwner subscriber, String key, SubscriberMethod subscriberMethod) {
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
        this.key = key;
        active = true;
    }

    public LifecycleOwner getSubscriber() {
        return subscriber;
    }

    public String getKey() {
        return key;
    }
}
