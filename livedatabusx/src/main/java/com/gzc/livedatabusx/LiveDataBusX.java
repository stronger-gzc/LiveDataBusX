package com.gzc.livedatabusx;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

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
public class LiveDataBusX {
    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private static LiveDataBusX sInstance;

    private Map<String,LiveDataObserver> liveDataObserverMap = new HashMap<>();

    private final ExecutorService executorService;

    private final AsyncPoster asyncPoster;
    private final BackgroundPoster backgroundPoster;


    private LiveDataBusX(){
        executorService = DEFAULT_EXECUTOR_SERVICE;
        asyncPoster = new AsyncPoster(this);
        backgroundPoster = new BackgroundPoster(this);
    }

    public static LiveDataBusX getInstance(){
        if(sInstance==null){
            synchronized (LiveDataBusX.class){
                if(sInstance==null){
                    sInstance = new LiveDataBusX();
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
                    .with(key)
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
            String combinationKey = key+"::"+dynamicKey;
            Bus.getInstance()
                    .with(combinationKey)
                    .postValue(object);

        }
    }

    /**
     *
     * @param key
     * @param liveDataObserver
     */
    public void setObserver(String key,LiveDataObserver liveDataObserver){
        Log.e("guanzhenchuang","key:"+key);
        if(!liveDataObserverMap.containsKey(key)) {
            liveDataObserverMap.put(key, liveDataObserver);
        }
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
        Log.e("guanzhenchuang",owner.getClass().getName());
        liveDataObserverMap.get(owner.getClass().getName())
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
