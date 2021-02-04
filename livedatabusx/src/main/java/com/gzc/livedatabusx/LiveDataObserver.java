package com.gzc.livedatabusx;

import androidx.lifecycle.LifecycleOwner;

/**
 * User: Administrator
 * Date: 2021-01-24 19:00
 * Describe:
 */
public interface LiveDataObserver {
    void observe(LifecycleOwner owner, String key);
}
