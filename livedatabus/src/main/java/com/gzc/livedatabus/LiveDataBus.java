package com.gzc.livedatabus;

import androidx.lifecycle.LifecycleOwner;

/**
 * author：gzc
 * date：2021/1/27
 * describe：
 */
public class LiveDataBus {
    private static LiveDataBus sInstance;


    private LiveDataBus(){

    }

    public static LiveDataBus getInstance(){
        if(sInstance==null){
            synchronized (LiveDataBus.class){
                if(sInstance==null){
                    sInstance = new LiveDataBus();
                }
            }
        }
        return sInstance;
    }

    public void post(String key,Object object){
        if(object!=null){
            Bus.getInstance()
                    .setKey(key)
                    .postValue(object);

        }
    }

    public void observe(LifecycleOwner owner,String dynamicKey){

    }
}
