package com.gzc.livedatabus;

import android.text.TextUtils;

import androidx.lifecycle.LifecycleOwner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Administrator
 * Date: 2021-01-24 17:28
 * Describe:
 */
public class MyLiveDataBus {
    private static MyLiveDataBus liveDataBus = new MyLiveDataBus();

    private Map<Class<?>, LiveDataObserver> liveDataObservers = new HashMap<>();

    private MyLiveDataBus() {
    }

    public static MyLiveDataBus getInstance() {
        return liveDataBus;
    }

    public void observe(LifecycleOwner owner) {
        observe(owner, null);
    }

    /**
     * @param owner
     * @param key
     */
    public void observe(LifecycleOwner owner, String key) {
        LiveDataObserver liveDataObserver = liveDataObservers.get(owner);
        liveDataObserver.observe(owner, key);
    }

    /**
     * APT来调用的
     *
     * @param clazz
     * @param observer
     */
    public void setLiveDataObservers(Class<?> clazz, LiveDataObserver observer) {
        liveDataObservers.put(clazz, observer);
    }
}
