package com.gzc.livedatabus;

import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.livedatabus.annotion.ThreadMode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.lifecycle.Lifecycle.State.STARTED;

/**
 * author：gzc
 * date：2021/1/27
 * describe：
 */
public class LiveDataBus {
    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private static LiveDataBus sInstance;

    private Map<String,LiveDataObserver>observerMap = new HashMap<>();

    private final ExecutorService executorService;

    private final AsyncPoster asyncPoster;
    private final BackgroundPoster backgroundPoster;


    private LiveDataBus(){
        executorService = DEFAULT_EXECUTOR_SERVICE;
        asyncPoster = new AsyncPoster(this);
        backgroundPoster = new BackgroundPoster(this);
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

    ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * 不带动态key的发送
     * @param key
     * @param object
     */
    public void post(String key,Object object){
        if(object!=null){
            Bus.getInstance()
                    .setKey(key)
                    .postValue(object);
        }
    }

    /**
     * 带动态key的发送
     * @param key
     * @param dynamicKey
     * @param object
     */
    public void post(String key,String dynamicKey,Object object){
        if(object!=null){
            Bus.getInstance()
                    .setKey(key+"::"+dynamicKey)
                    .postValue(object);

        }
    }

    /**
     * 设置observers，在app初始化的时候设置，建议在application的onCreate方法中
     * @param observers
     */
    public void setObservers(Observers observers){
        observerMap.putAll(observers.getObservers());
    }

    /**
     * 相当于register，不带动态key
     * @param owner
     */
    public void observe(LifecycleOwner owner){
        observe(owner,null);
    }

    /**
     * 相当于register
     * @param owner
     * @param dynamicKey 动态key
     */
    public void observe(LifecycleOwner owner,String dynamicKey){
        observerMap.get(owner.getClass().getName())
                .observe(owner,dynamicKey);
    }

    /**
     * 切换线程
     */
    public void postToThread(final Observation observation){
        switch (observation.threadMode){
            case MAIN:
                invokeSubscriber(observation);
                break;
            case BACKGROUND:
                backgroundPoster.enqueue(observation);
                break;
            case ASYNC:
                asyncPoster.enqueue(observation);
                break;
        }
    }

    /**
     * 反射方法
     */
    void invokeSubscriber(Observation observation) {
        //判断一下owner是否处于活跃状态
        if(!active(observation.owner)){
            return;
        }
        try {
            Method method = observation.owner.getClass().getDeclaredMethod(observation.methodName, observation.event.getClass());
            method.setAccessible(true);
            method.invoke(observation.owner, observation.event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void invokeSubscriber(PendingPost pendingPost) {
        Observation observation = pendingPost.observation;
        PendingPost.releasePendingPost(pendingPost);
        invokeSubscriber(observation);

    }

    /**
     * LifecycleOwner 是否处于活跃状态
     * @param owner
     * @return
     */
    private boolean active(LifecycleOwner owner){
        return owner.getLifecycle().getCurrentState().isAtLeast(STARTED);
    }
}
