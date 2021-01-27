package com.gzc.livedatabus;

import android.text.TextUtils;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.gzc.livedatabus.test.TestBean1;
import com.gzc.livedatabus.test.TestBean2;
import com.gzc.livedatabus.test.TestBean3;
import com.gzc.livedatabus.test.TestBean4;

/**
 * User: Administrator
 * Date: 2021-01-24 19:01
 * Describe:
 */
public class MainActivityLiveDataObserver12 implements LiveDataObserver {
    @Override
    public void observe(LifecycleOwner owner, String key) {
        //@Observe(threadMode = ThreadMode.MAIN,sticky = false,append = false,key = "")
        Bus.getInstance()
                .with("MainActivity::testBean1", TestBean1.class, false)
                .observe(owner, new Observer<TestBean1>() {
                    @Override
                    public void onChanged(TestBean1 testBean1) {

                    }
                });
//        //@Observe(threadMode = ThreadMode.MAIN,sticky = false,append = false,key = "")
//        Bus.getInstance()
//                .with("MainActivity::testBean2", TestBean2.class, true)
//                .observe(owner, new Observer<TestBean2>() {
//                    @Override
//                    public void onChanged(TestBean2 testBean2) {
//
//                    }
//                });
//        //@Observe(threadMode = ThreadMode.MAIN,sticky = true,append = true,key = "")
//        Bus.getInstance()
//                .with("MainActivity::testBean3::" + key, TestBean3.class, true)
//                .observe(owner, new Observer<TestBean3>() {
//                    @Override
//                    public void onChanged(TestBean3 testBean3) {
//
//                    }
//                });
//        //@Observe(threadMode = ThreadMode.MAIN,sticky = true,key = "TestBean3")
//        Bus.getInstance()
//                .with("TestBean3", TestBean4.class, true)
//                .observe(owner, new Observer<TestBean4>() {
//                    @Override
//                    public void onChanged(TestBean4 testBean4) {
//
//                    }
//                });
    }
}
