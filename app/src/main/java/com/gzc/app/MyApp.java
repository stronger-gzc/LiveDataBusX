package com.gzc.app;

import android.app.Application;

import com.gzc.livedatabusx.LiveDataBusX;

/**
 * User: Administrator
 * Date: 2021-02-13 19:18
 * Describe:
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        LiveDataBusX.getInstance()
//                .setObservers(new MyObservers());
    }
}
