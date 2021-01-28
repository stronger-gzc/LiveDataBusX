package com.gzc.livedatabus;

import android.app.Application;

/**
 * author：gzc
 * date：2021/1/28
 * describe：
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LiveDataBus.getInstance()
                .setObservers(new LiveDataObservers());
    }
}
