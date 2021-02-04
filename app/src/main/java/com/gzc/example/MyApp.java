package com.gzc.example;

import android.app.Application;

import com.gzc.example.LiveDataObservers;
import com.gzc.livedatabusx.LiveDataBusX;

/**
 * author：gzc
 * date：2021/1/28
 * describe：
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LiveDataBusX.getInstance()
                .setObservers(new LiveDataObservers());
    }
}
