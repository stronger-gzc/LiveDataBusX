package com.gzc.livedatabus;

import java.util.Map;

/**
 * author：gzc
 * date：2021/1/27
 * describe：
 */
public interface Observers {
    Map<String,LiveDataObserver> getObserver(String key);
}
