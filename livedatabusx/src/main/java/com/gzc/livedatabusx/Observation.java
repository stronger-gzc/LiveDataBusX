package com.gzc.livedatabusx;

import androidx.lifecycle.LifecycleOwner;

import com.livedatabusx.annotation.ThreadMode;

/**
 * author：gzc
 * date：2021/1/28
 * describe：
 */
public class Observation {
    //ThreadMode threadMode, final LifecycleOwner owner, final Object event, final String methodName
    ThreadMode threadMode;
    LifecycleOwner owner;
    Object event;
    String methodName;
    boolean sticky;
    String key;

    public Observation(ThreadMode threadMode, LifecycleOwner owner, Object event, String methodName) {
        this.threadMode = threadMode;
        this.owner = owner;
        this.event = event;
        this.methodName = methodName;
    }

    public Observation(boolean sticky, String key) {
        this.sticky = sticky;
        this.key = key;
    }
}
